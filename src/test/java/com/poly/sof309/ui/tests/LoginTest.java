package com.poly.sof309.ui.tests;

import com.poly.sof309.ui.pages.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =========================================================================
 * [Y4 - P1] KỊCH BẢN KIỂM THỬ GIAO DIỆN ĐĂNG NHẬP BẰNG SELENIUM POM
 * Bám sát TC_AUTH_01 - TC_AUTH_04 và TC_AUTH_10 trong docs/TEST_CASES.md.
 * =========================================================================
 */
public class LoginTest extends BaseUiTest {
    private LoginPage loginPage;

    @BeforeEach
    public void initPage() {
        loginPage = new LoginPage(driver);
    }

    @Test
    @DisplayName("TC_AUTH_01: Đăng nhập Admin thành công và điều hướng vào Dashboard")
    public void testAdminLoginSuccessUI() {
        loginPage.openStaffLogin(baseUrl);
        loginPage.loginAsAdminAndWait(ADMIN_USER, ADMIN_PASS);

        assertTrue(loginPage.getCurrentUrl().contains("/admin/dashboard"),
                "Sau khi đăng nhập Admin thành công phải chuyển hướng về /admin/dashboard");
    }

    @Test
    @DisplayName("TC_AUTH_02: Đăng nhập Thu ngân (Staff) thành công và điều hướng vào quầy POS")
    public void testCashierLoginSuccessUI() {
        loginPage.openStaffLogin(baseUrl);
        loginPage.loginAsCashierAndWait(STAFF_USER, STAFF_PASS);

        assertTrue(loginPage.getCurrentUrl().contains("/pos"),
                "Sau khi đăng nhập Thu ngân thành công phải chuyển hướng về /pos");
    }

    @Test
    @DisplayName("TC_AUTH_03: Bị chặn bởi HTML5 validation khi bỏ trống tài khoản đăng nhập")
    public void testStaffLoginEmptyFieldsBlockedByValidation() {
        loginPage.openStaffLogin(baseUrl);
        loginPage.clickLogin();

        String validationMsg = loginPage.getUsernameValidationMessage();
        assertFalse(validationMsg.isEmpty(),
                "Trình duyệt phải hiển thị thông báo bắt buộc nhập cho trường username");
        assertTrue(loginPage.getCurrentUrl().contains("/login"),
                "Trang vẫn phải giữ nguyên ở màn hình /login khi chưa nhập dữ liệu");
    }

    @Test
    @DisplayName("TC_AUTH_04: Hiển thị cảnh báo SweetAlert2 khi nhập sai thông tin đăng nhập")
    public void testStaffLoginInvalidCredentialsShowsSwalError() {
        loginPage.openStaffLogin(baseUrl);
        // Dùng tài khoản không tồn tại để tránh kích hoạt khóa Brute-Force 5 phút lên tài khoản admin
        loginPage.login("invalid_qa_user", "SaiMatKhau999");

        String errorMsg = loginPage.getErrorMessage();
        assertTrue(errorMsg.contains("Tên đăng nhập hoặc mật khẩu không chính xác"),
                "Thông báo SweetAlert2 phải báo sai tên đăng nhập hoặc mật khẩu, nhận được: " + errorMsg);
        assertTrue(loginPage.getCurrentUrl().contains("/login"),
                "Đăng nhập thất bại không được chuyển khỏi trang /login");
    }

    @Test
    @DisplayName("TC_AUTH_10: Khách hàng đăng nhập Portal thành công và chuyển về trang chủ /home")
    public void testCustomerPortalLoginSuccessUI() {
        loginPage.openCustomerLogin(baseUrl);
        loginPage.loginAsCustomerAndWait(CUSTOMER_USER, CUSTOMER_PASS);

        assertTrue(loginPage.getCurrentUrl().contains("/home"),
                "Khách hàng đăng nhập thành công phải chuyển hướng về /home");
    }
}
