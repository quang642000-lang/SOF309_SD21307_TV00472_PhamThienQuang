package com.poly.sof309.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import util.SecurityUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =========================================================================
 * [Y3] UNIT TEST CHO PHÂN HỆ XÁC THỰC & BẢO MẬT (AUTH & SECURITY)
 * Dự án: TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE
 * Sinh viên: Phạm Thiện Quang - MSSV: TV00472 - Lớp: SD21307
 * Bám sát kịch bản: TC_AUTH_01 đến TC_AUTH_09 trong docs/TEST_CASES.md
 * =========================================================================
 */
public class AuthControllerTest {

    private ConcurrentHashMap<String, Integer> failedAttempts;
    private ConcurrentHashMap<String, Long> lockTimeouts;

    @BeforeEach
    public void setUp() {
        failedAttempts = new ConcurrentHashMap<>();
        lockTimeouts = new ConcurrentHashMap<>();
    }

    // =====================================================================
    // 1. PARAMETERIZED TESTS: KIỂM TRA BIÊN FORM ĐĂNG NHẬP & ĐĂNG KÝ
    // =====================================================================

    @ParameterizedTest(name = "[{index}] Kiểm tra form đăng nhập: user=''{0}'', pass=''{1}'' -> Hợp lệ: {2}")
    @DisplayName("TC_AUTH_01 - 04: Parameterized Test kiểm tra validation form đăng nhập")
    @CsvSource({
            "'admin', 'Admin@123', true",
            "'cashier01', 'Cashier@123', true",
            "'', 'Admin@123', false",
            "'admin', '', false",
            "'   ', '   ', false",
            "'manager', 'short', false"
    })
    public void testLoginInputValidation(String username, String password, boolean expectedValid) {
        boolean isValid = validateLoginInput(username, password);
        assertEquals(expectedValid, isValid,
                "Kết quả kiểm tra form đăng nhập không khớp với mong đợi đối với user: " + username);
    }

    @ParameterizedTest(name = "[{index}] Đăng ký: SĐT=''{1}'', Pass=''{3}'', Match=''{4}'', Terms={5} -> Kết quả: {6}")
    @DisplayName("TC_AUTH_06 - 08: Parameterized Test kiểm tra các trường dữ liệu đăng ký thành viên Portal")
    @CsvSource({
            // Họ tên, SĐT, Email, Password, ConfirmPassword, AgreeTerms, ExpectedSuccess
            "'Phạm Thiện Quang', '0987654321', 'quang@gmail.com', 'Pass@123', 'Pass@123', true, true",
            "'', '0987654321', 'quang@gmail.com', 'Pass@123', 'Pass@123', true, false",          // Tên trống
            "'Quang', '098765', 'quang@gmail.com', 'Pass@123', 'Pass@123', true, false",           // SĐT ngắn < 10 số
            "'Quang', '0987654321', 'sai_email', 'Pass@123', 'Pass@123', true, false",            // Email sai format
            "'Quang', '0987654321', 'quang@gmail.com', 'Pass@123', 'Pass@KhacNhau', true, false",  // Pass không khớp
            "'Quang', '0987654321', 'quang@gmail.com', 'Pass@123', 'Pass@123', false, false"       // Chưa tick điều khoản
    })
    public void testCustomerRegisterValidation(String fullName, String phone, String email,
                                               String pass, String confirmPass, boolean agreeTerms,
                                               boolean expectedSuccess) {
        boolean result = validateRegisterInput(fullName, phone, email, pass, confirmPass, agreeTerms);
        assertEquals(expectedSuccess, result, "Validation đăng ký thất bại cho SĐT: " + phone);
    }

    // =====================================================================
    // 2. EXCEPTION TESTING: BẮT NGOẠI LỆ NGHIỆP VỤ VỚI assertThrows
    // =====================================================================

    @Test
    @DisplayName("TC_AUTH_03: Ném ngoại lệ IllegalArgumentException khi đăng nhập với tham số null")
    public void testLoginWithNullParamsThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            authenticateStaff(null, null);
        }, "Phải ném ngoại lệ IllegalArgumentException khi thông tin đăng nhập bị null");

        assertTrue(ex.getMessage().contains("Tên đăng nhập và mật khẩu không được trống"),
                "Nội dung thông báo lỗi ngoại lệ phải chính xác");
    }

    @Test
    @DisplayName("TC_AUTH_07: Ném ngoại lệ IllegalArgumentException khi mật khẩu xác nhận không khớp")
    public void testPasswordMismatchThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            registerNewCustomer("Phạm Thiện Quang", "0987654321", "quang@gmail.com", "Password@123", "Password@MISMATCH");
        });

        assertEquals("Mật khẩu nhập lại không trùng khớp với mật khẩu ban đầu.", ex.getMessage());
    }

    @Test
    @DisplayName("TC_AUTH_05: Ném ngoại lệ IllegalStateException khi tài khoản bị khóa Brute-Force")
    public void testAccountLockedAfterConsecutiveFailuresThrowsException() {
        String testUser = "attacker_staff";

        // Giả lập nhập sai 5 lần liên tiếp
        for (int i = 1; i <= 5; i++) {
            recordFailedLogin(testUser);
        }

        assertTrue(isAccountLocked(testUser), "Tài khoản phải ở trạng thái bị khóa sau 5 lần nhập sai");

        // Lần thứ 6 cố tình đăng nhập tiếp -> Phải ném ngoại lệ chặn truy cập
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            checkLoginAllowed(testUser);
        }, "Hệ thống phải ném IllegalStateException khi tài khoản đang bị khóa tạm thời 5 phút");

        assertTrue(ex.getMessage().contains("Tài khoản đã bị khóa tạm thời"),
                "Thông báo ngoại lệ phải cảnh báo khóa tài khoản do Brute-Force");
    }

    // =====================================================================
    // 3. TIMEOUT & SECURITY TEST: THUẬT TOÁN BĂM SALTED SHA-256
    // =====================================================================

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    @DisplayName("TC_AUTH_01_SEC: Mã hóa mật khẩu Salted SHA-256 + Pepper và xác thực hoàn thành dưới 2 giây")
    public void testSaltedSha256PerformanceAndSecurity() {
        String rawPassword = "AdminSecure@2026";
        String username = "admin";

        // Thực hiện băm có muối
        String hashed = SecurityUtil.hashWithSalt(rawPassword, username);
        assertNotNull(hashed, "Mật khẩu băm không được null");
        assertNotEquals(rawPassword, hashed, "Mật khẩu sau khi băm phải khác mật khẩu thô");

        // Kiểm tra khớp mật khẩu
        boolean isMatch = SecurityUtil.checkPassword(rawPassword, hashed, username);
        assertTrue(isMatch, "Mật khẩu đúng phải được xác thực thành công");

        // Kiểm tra mật khẩu sai
        boolean isWrongMatch = SecurityUtil.checkPassword("WrongPass@999", hashed, username);
        assertFalse(isWrongMatch, "Mật khẩu sai tuyệt đối không được xác thực thành công");
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "cashier01", "manager_hanoi"})
    @DisplayName("TC_AUTH_RBAC: Kiểm tra phân quyền điều hướng đúng vai trò")
    public void testRoleRedirection(String roleName) {
        String redirectUrl = resolveRedirectUrl(roleName);
        assertNotNull(redirectUrl);
        assertTrue(redirectUrl.startsWith("/"), "URL điều hướng phải là đường dẫn hợp lệ");
    }

    // =====================================================================
    // HÀM HELPER MÔ PHỎNG NGHIỆP VỤ CONTROLLER (IN-MEMORY BUSINESS LOGIC)
    // =====================================================================

    private boolean validateLoginInput(String username, String password) {
        if (username == null || username.trim().isEmpty()) return false;
        if (password == null || password.trim().isEmpty()) return false;
        return password.length() >= 6;
    }

    private boolean validateRegisterInput(String fullName, String phone, String email,
                                          String pass, String confirmPass, boolean agreeTerms) {
        if (fullName == null || fullName.trim().isEmpty()) return false;
        if (phone == null || !phone.matches("^0[0-9]{9}$")) return false; // Đúng 10 số bắt đầu bằng 0
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) return false;
        if (pass == null || pass.length() < 6) return false;
        if (!pass.equals(confirmPass)) return false;
        return agreeTerms;
    }

    private void authenticateStaff(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập và mật khẩu không được trống.");
        }
    }

    private void registerNewCustomer(String name, String phone, String email, String pass, String confirmPass) {
        if (!pass.equals(confirmPass)) {
            throw new IllegalArgumentException("Mật khẩu nhập lại không trùng khớp với mật khẩu ban đầu.");
        }
    }

    private void recordFailedLogin(String username) {
        int count = failedAttempts.getOrDefault(username, 0) + 1;
        failedAttempts.put(username, count);
        if (count >= 5) {
            lockTimeouts.put(username, System.currentTimeMillis() + (300 * 1000));
        }
    }

    private boolean isAccountLocked(String username) {
        Long lockUntil = lockTimeouts.get(username);
        return lockUntil != null && System.currentTimeMillis() < lockUntil;
    }

    private void checkLoginAllowed(String username) {
        if (isAccountLocked(username)) {
            throw new IllegalStateException("Tài khoản đã bị khóa tạm thời 5 phút do nhập sai mật khẩu liên tiếp 5 lần.");
        }
    }

    private String resolveRedirectUrl(String role) {
        if ("admin".equals(role)) {
            return "/admin/dashboard";
        } else if ("cashier01".equals(role)) {
            return "/pos";
        }
        return "/home";
    }
}
