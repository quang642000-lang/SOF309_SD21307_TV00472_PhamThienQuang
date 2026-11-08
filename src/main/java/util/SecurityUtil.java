package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * =========================================================================
 * TEA POS ENTERPRISE - CRYPTOGRAPHIC & SECURITY UTILITY
 * Implements salted password hashing with constant-time equality check
 * to prevent timing side-channel attacks and rainbow table matching.
 * =========================================================================
 */
public class SecurityUtil {
    private static final Logger LOGGER = Logger.getLogger(SecurityUtil.class.getName());
    private static final String SYSTEM_PEPPER = "[$tea_pos_salt_key$]";

    private SecurityUtil() {}

    public static String hashSHA256(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Không tìm thấy thuật toán SHA-256", e);
            throw new RuntimeException("Lỗi bảo mật mã hóa mật khẩu", e);
        }
    }

    /**
     * Băm mật khẩu kết hợp Pepper hệ thống và Salt độc bản của từng tài khoản
     */
    public static String hashWithSalt(String password, String salt) {
        if (password == null) {
            return null;
        }
        String salted = password + SYSTEM_PEPPER + (salt != null ? salt.trim() : "");
        return hashSHA256(salted);
    }

    /**
     * So khớp mật khẩu đầu vào an toàn với thuật toán constant-time MessageDigest.isEqual
     */
    public static boolean checkPassword(String inputPassword, String dbHashedPassword, String salt) {
        if (inputPassword == null || dbHashedPassword == null) {
            return false;
        }
        String hashedInput = hashWithSalt(inputPassword, salt);
        if (hashedInput == null) {
            return false;
        }
        byte[] a = hashedInput.toLowerCase().getBytes(StandardCharsets.UTF_8);
        byte[] b = dbHashedPassword.trim().toLowerCase().getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }
}
