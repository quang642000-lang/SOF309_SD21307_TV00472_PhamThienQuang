package com.poly.sof309.ui.tests;

import com.poly.sof309.ui.pages.RegisterPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =========================================================================
 * [Y4 - P1] KỊCH BẢN KIỂM THỬ GIAO DIỆN ĐĂNG KÝ KHÁCH HÀNG (SELENIUM POM)
 * Bám sát TC_AUTH_06 - TC_AUTH_09, TC_AUTH_11 - TC_AUTH_12 trong docs/TEST_CASES.md.
 * =========================================================================
 */
public class RegisterTest extends BaseUiTest {
    private RegisterPage registerPage;

    @BeforeEach
    public void initPage() {
        registerPage = new RegisterPage(driver);
    }

    @Test
    @DisplayName("TC_AUTH_11: Cảnh báo SweetAlert2 khi số điện thoại ngắn hơn 10 chữ số")
    public void testRegisterInvalidPhoneShowsSwalWarning() {
        registerPage.open(baseUrl);
        registerPage.register(
                "Phạm Thiện Quang",
                "098765",
                "quang_test_phone@gmail.com",
                "Quang@1234",
                "Quang@1234",
                true
        );

        String alertMsg = registerPage.getAlertMessage();
        assertTrue(alertMsg.contains("Số điện thoại phải từ 10 đến 11 chữ số"),
                "Phải hiển thị cảnh báo SĐT không hợp lệ, nhận được: " + alertMsg);
    }

    @Test
    @DisplayName("TC_AUTH_07: Cảnh báo SweetAlert2 khi mật khẩu xác nhận không trùng khớp")
    public void testRegisterPasswordMismatchShowsSwalWarning() {
        registerPage.open(baseUrl);
        registerPage.register(
                "Phạm Thiện Quang",
                "0987654321",
                "quang_test_match@gmail.com",
                "Quang@1234",
                "Quang@Khac",
                true
        );

        String alertMsg = registerPage.getAlertMessage();
        assertTrue(alertMsg.contains("Mật khẩu nhập lại không khớp"),
                "Phải hiển thị cảnh báo mật khẩu xác nhận không khớp, nhận được: " + alertMsg);
    }

    @Test
    @DisplayName("TC_AUTH_12: Cảnh báo SweetAlert2 khi mật khẩu dưới 8 ký tự")
    public void testRegisterShortPasswordShowsSwalWarning() {
        registerPage.open(baseUrl);
        registerPage.register(
                "Phạm Thiện Quang",
                "0987654321",
                "quang_test_short@gmail.com",
                "12345",
                "12345",
                true
        );

        String alertMsg = registerPage.getAlertMessage();
        assertTrue(alertMsg.contains("tối thiểu 8 ký tự"),
                "Phải hiển thị cảnh báo mật khẩu tối thiểu 8 ký tự, nhận được: " + alertMsg);
    }

    @Test
    @DisplayName("TC_AUTH_08: Chặn submit bằng HTML5 validation khi chưa tick đồng ý điều khoản")
    public void testRegisterUncheckedTermsBlockedByHtml5Validation() {
        registerPage.open(baseUrl);
        registerPage.register(
                "Phạm Thiện Quang",
                "0987654321",
                "quang_test_terms@gmail.com",
                "Quang@1234",
                "Quang@1234",
                false
        );

        String validationMsg = registerPage.getAgreeTermsValidationMessage();
        assertFalse(validationMsg.isEmpty(),
                "Checkbox đồng ý điều khoản phải kích hoạt HTML5 required validation");
        assertTrue(registerPage.getCurrentUrl().contains("/register"),
                "Trang phải giữ nguyên tại /register khi chưa tick điều khoản");
    }

    @Test
    @DisplayName("TC_AUTH_09: Báo lỗi từ Server khi đăng ký trùng SĐT/Email đã tồn tại")
    public void testRegisterDuplicatePhoneShowsServerError() {
        registerPage.open(baseUrl);
        registerPage.register(
                "Khách Hàng Trùng Dữ Liệu",
                "0909999999",
                "khach_vip@gmail.com",
                "Quang@1234",
                "Quang@1234",
                true
        );

        String alertMsg = registerPage.getAlertMessage();
        assertTrue(alertMsg.contains("Số điện thoại hoặc Email này đã tồn tại"),
                "Server phải trả về lỗi trùng lặp SĐT/Email, nhận được: " + alertMsg);
        assertTrue(registerPage.getCurrentUrl().contains("/register"),
                "Đăng ký trùng lặp phải ở lại trang /register");
    }
}
