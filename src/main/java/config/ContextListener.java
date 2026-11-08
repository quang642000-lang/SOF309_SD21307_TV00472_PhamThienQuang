package config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import service.impl.ThongKeServiceImpl;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * =========================================================================
 * TEA POS ENTERPRISE - APPLICATION LIFECYCLE LISTENER
 * Manages Connection Pool startup/shutdown, database sequences NO CACHE,
 * and nightly cron job at 23:59 for business reporting aggregation.
 * =========================================================================
 */
@WebListener
public class ContextListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(ContextListener.class.getName());
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("[TEA POS] Đang khởi động hệ thống TEA POS...");
        try (Connection conn = DBConnect.getConnection()) {
            LOGGER.info("[TEA POS] Kết nối CSDL SQL Server thành công!");

            // Tối ưu hóa Sequences hệ thống chống hiện tượng nhảy số
            String[] sequenceCommands = {
                    "ALTER SEQUENCE seq_NhanVien NO CACHE;",
                    "ALTER SEQUENCE seq_KhachHang NO CACHE;",
                    "ALTER SEQUENCE seq_DonHang NO CACHE;",
                    "ALTER SEQUENCE seq_SanPham NO CACHE;",
                    "ALTER SEQUENCE seq_DanhMuc NO CACHE;",
                    "ALTER SEQUENCE seq_Voucher NO CACHE;",
                    "ALTER SEQUENCE seq_Topping NO CACHE;"
            };
            try (Statement stmt = conn.createStatement()) {
                for (String cmd : sequenceCommands) {
                    stmt.addBatch(cmd);
                }
                stmt.executeBatch();
                LOGGER.info("[TEA POS] Đã tối ưu cấu hình NO CACHE cho 7 Sequences hệ thống.");
            } catch (Exception ex) {
                LOGGER.log(Level.FINE, "[TEA POS] Bỏ qua cấu hình NO CACHE Sequences: " + ex.getMessage());
            }

            // Lập lịch tác vụ chốt sổ doanh thu tự động hàng đêm (23:59)
            startNightlyAggregationJob();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[TEA POS CRITICAL] Khởi chạy ứng dụng gặp lỗi: " + e.getMessage(), e);
        }
    }

    private void startNightlyAggregationJob() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "TeaPos-NightlyCron");
            t.setDaemon(true);
            return t;
        });

        Calendar calendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);

        long initialDelay = calendar.getTimeInMillis() - now.getTimeInMillis();
        if (initialDelay < 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            initialDelay = calendar.getTimeInMillis() - now.getTimeInMillis();
        }

        scheduler.scheduleAtFixedRate(() -> {
            LOGGER.info("[TEA POS CRON] Đang thực hiện chốt sổ dữ liệu doanh thu cuối ngày...");
            try {
                boolean success = ThongKeServiceImpl.getInstance().runNightlyAggregationJob();
                if (success) {
                    LOGGER.info("[TEA POS CRON] Chốt sổ báo cáo doanh thu hôm nay thành công!");
                } else {
                    LOGGER.info("[TEA POS CRON] Dữ liệu chốt sổ hôm nay đã hoàn tất trước đó.");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "[TEA POS CRON] Lỗi trong tác vụ chốt sổ đêm: " + e.getMessage(), e);
            }
        }, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);

        LOGGER.info("[TEA POS] Đã lên lịch chốt sổ doanh thu tự động lúc 23:59 hàng ngày.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("[TEA POS] Đang dừng ứng dụng, tiến hành giải phóng tài nguyên...");
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        try {
            DBConnect.shutdown();
            LOGGER.info("[TEA POS] Đã giải phóng Connection Pool thành công.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[TEA POS ERROR] Lỗi giải phóng Connection Pool: " + e.getMessage(), e);
        }
    }
}
