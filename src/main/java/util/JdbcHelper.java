package util;

import config.DBConnect;
import repository.RowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * =========================================================================
 * TEA POS ENTERPRISE - JDBC HELPER (TRANSACTION AWARE)
 * Supports both standalone connection pool queries and managed transaction connections.
 * =========================================================================
 */
public class JdbcHelper {
    private static final Logger LOGGER = Logger.getLogger(JdbcHelper.class.getName());

    private JdbcHelper() {}

    private static void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                Object p = params[i];
                if (p == null) {
                    ps.setNull(i + 1, Types.NULL);
                } else {
                    ps.setObject(i + 1, p);
                }
            }
        }
    }

    public static <T> List<T> query(String sql, RowMapper<T> rm, Object... params) {
        List<T> list = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rm.mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn SQL: " + sql + " | Params: " + java.util.Arrays.toString(params), e);
        }
        return list;
    }

    public static <T> List<T> query(Connection conn, String sql, RowMapper<T> rm, Object... params) throws SQLException {
        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rm.mapRow(rs));
                }
            }
        }
        return list;
    }

    public static <T> T queryForObject(String sql, RowMapper<T> rm, Object... params) {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rm.mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn đơn giá trị SQL: " + sql, e);
        }
        return null;
    }

    public static <T> T queryForObject(Connection conn, String sql, RowMapper<T> rm, Object... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rm.mapRow(rs);
                }
            }
        }
        return null;
    }

    public static int update(String sql, Object... params) {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi thực thi cập nhật SQL: " + sql, e);
            return 0;
        }
    }

    public static int update(Connection conn, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            return ps.executeUpdate();
        }
    }

    public static long insertAndGetGeneratedKey(Connection conn, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(ps, params);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }
}
