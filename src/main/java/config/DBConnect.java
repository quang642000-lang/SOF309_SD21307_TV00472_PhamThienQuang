package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * =========================================================================
 * TEA POS ENTERPRISE - HIGH-PERFORMANCE DATABASE CONNECTION POOL (HikariCP)
 * Auto-detects standard port 1433 and named instances (MSSQLSERVER01)
 * Supports environment variables resolution with fallback defaults.
 * =========================================================================
 */
public class DBConnect {
    private static final Logger LOGGER = Logger.getLogger(DBConnect.class.getName());
    private static HikariDataSource dataSource;

    static {
        initDataSource();
    }

    private static synchronized void initDataSource() {
        try {
            LOGGER.info("[TEA POS] Đang khởi tạo Connection Pool HikariCP...");
            Properties properties = new Properties();

            try (InputStream input = DBConnect.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input != null) {
                    properties.load(input);
                    LOGGER.info("[TEA POS] Đã nạp thành công application.properties.");
                } else {
                    LOGGER.warning("[TEA POS] Không tìm thấy application.properties! Dùng cấu hình mặc định.");
                }
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "[TEA POS] Lỗi nạp application.properties: " + ex.getMessage(), ex);
            }

            String primaryUrl = resolvePlaceholder(properties.getProperty("db.url", 
                    "jdbc:sqlserver://localhost:1433;databaseName=QuanLyQuanCafehetcuu1;encrypt=true;trustServerCertificate=true"));
            String instanceUrl = resolvePlaceholder(properties.getProperty("db.instanceUrl", 
                    "jdbc:sqlserver://localhost\\MSSQLSERVER01;databaseName=QuanLyQuanCafehetcuu1;encrypt=true;trustServerCertificate=true"));
            String dbUser = resolvePlaceholder(properties.getProperty("db.user", "sa"));
            String dbPass = resolvePlaceholder(properties.getProperty("db.password", ""));

            // Cố gắng kết nối bằng primaryUrl trước, nếu thất bại thử instanceUrl
            boolean configured = tryConfigurePool(primaryUrl, dbUser, dbPass);
            if (!configured && instanceUrl != null && !instanceUrl.equals(primaryUrl)) {
                LOGGER.warning("[TEA POS] Cổng 1433 chưa kết nối được, chuyển sang thử URL Named Instance: " + instanceUrl);
                configured = tryConfigurePool(instanceUrl, dbUser, dbPass);
            }

            if (!configured) {
                LOGGER.severe("[TEA POS CRITICAL] Không thể kết nối cơ sở dữ liệu với cả 2 cấu hình URL!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[TEA POS ERROR] Lỗi khởi tạo Connection Pool: " + e.getMessage(), e);
        }
    }

    private static boolean tryConfigurePool(String jdbcUrl, String user, String pass) {
        HikariDataSource testDs = null;
        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(pass);

            // Tối ưu hóa hiệu năng doanh nghiệp
            config.setMaximumPoolSize(25);
            config.setMinimumIdle(5);
            config.setIdleTimeout(300000);        // 5 phút
            config.setConnectionTimeout(15000);   // 15 giây Fail-fast
            config.setMaxLifetime(1800000);       // 30 phút
            config.setConnectionTestQuery("SELECT 1");
            config.setPoolName("TeaPosHikariPool");

            // Tối ưu hóa SQL Server PreparedStatement cache
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");

            testDs = new HikariDataSource(config);

            // Kiểm tra kết nối thử
            try (Connection conn = testDs.getConnection()) {
                if (conn.isValid(3)) {
                    dataSource = testDs;
                    LOGGER.info("[TEA POS SUCCESS] Khởi tạo Connection Pool HikariCP thành công với URL: " + jdbcUrl);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.warning("[TEA POS] Thử nghiệm kết nối thất bại với URL: " + jdbcUrl + " -> " + e.getMessage());
            if (testDs != null && !testDs.isClosed()) {
                testDs.close();
            }
        }
        return false;
    }

    private DBConnect() {}

    public static String resolvePlaceholder(String value) {
        if (value == null) return null;
        value = value.trim();
        if (value.startsWith("${") && value.endsWith("}")) {
            String content = value.substring(2, value.length() - 1);
            int colonIdx = content.indexOf(":");
            String envName = content;
            String defaultValue = "";
            if (colonIdx != -1) {
                envName = content.substring(0, colonIdx);
                defaultValue = content.substring(colonIdx + 1);
            }
            String envValue = System.getenv(envName);
            if (envValue != null && !envValue.trim().isEmpty()) {
                return envValue.trim();
            }
            return defaultValue.trim();
        }
        return value;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("DataSource chưa được khởi tạo hoặc không thể kết nối SQL Server.");
        }
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            LOGGER.info("[TEA POS] Thu hồi kết nối và đóng Connection Pool...");
            dataSource.close();
            LOGGER.info("[TEA POS] Đã giải phóng Connection Pool an toàn.");
        }
    }
}
