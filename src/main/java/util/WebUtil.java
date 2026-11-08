package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.entity.KhachHang;
import model.entity.NhanVien;

public class WebUtil {

    private WebUtil() {}

    public static int parseIntSafe(String val, int defaultVal) {
        if (val == null || val.trim().isEmpty()) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static long parseLongSafe(String val, long defaultVal) {
        if (val == null || val.trim().isEmpty()) {
            return defaultVal;
        }
        try {
            return Long.parseLong(val.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static double parseDoubleSafe(String val, double defaultVal) {
        if (val == null || val.trim().isEmpty()) {
            return defaultVal;
        }
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static int getIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        String val = request.getParameter(paramName);
        return parseIntSafe(val, defaultValue);
    }

    public static String getCurrentActor(HttpServletRequest request) {
        if (request == null) return "SYSTEM";
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userObj = session.getAttribute("user");
            if (userObj instanceof NhanVien) {
                return ((NhanVien) userObj).getMaNv();
            }
        }
        return "SYSTEM";
    }

    public static KhachHang getCurrentCustomer(HttpServletRequest request) {
        if (request == null) return null;
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object customerObj = session.getAttribute("customer");
            if (customerObj instanceof KhachHang) {
                return (KhachHang) customerObj;
            }
        }
        return null;
    }

    public static String getRemoteIP(HttpServletRequest request) {
        if (request == null) return "127.0.0.1";
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "127.0.0.1";
    }

    public static boolean isAjax(HttpServletRequest request) {
        if (request == null) return false;
        String requestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader("Accept");
        return "XMLHttpRequest".equalsIgnoreCase(requestedWith) ||
                (accept != null && accept.contains("application/json"));
    }
}
