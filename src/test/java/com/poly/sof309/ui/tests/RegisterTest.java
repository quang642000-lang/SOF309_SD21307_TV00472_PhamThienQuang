package com.poly.sof309.ui.tests;

import com.poly.sof309.ui.listeners.TestListener;
import com.poly.sof309.ui.pages.RegisterPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * =========================================================================
 * [Y4 - P2] KỊCH BẢN KIỂM THỬ GIAO DIỆN ĐĂNG KÝ KHÁCH HÀNG VỚI TESTNG
 * Áp dụng: @DataProvider (Data-Driven), groups = "regression", @Listeners.
 * Bám sát TC_AUTH_07 - TC_AUTH_09 và TC_AUTH_11 - TC_AUTH_12 trong docs/TEST_CASES.md.
 * =========================================================================
 */
@Listeners(TestListener.class)
public class RegisterTest extends BaseUiTest {
    private RegisterPage registerPage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        registerPage = new RegisterPage(driver);
    }

    @DataProvider(name = "registerInvalidAlertData")
    public Object[][] getRegisterInvalidAlertData() {
        return new Object[][]{
                {"Phạm Thiện Quang", "098765", "quang_test_phone@gmail.com", "Quang@1234", "Quang@1234",
                        "Số điện thoại phải từ 10 đến 11 chữ số"},
                {"Phạm Thiện Quang", "0987654321", "quang_test_match@gmail.com", "Quang@1234", "Quang@Khac",
                        "Mật khẩu nhập lại không khớp"},
                {"Phạm Thiện Quang", "0987654321", "quang_test_short@gmail.com", "12345", "12345",
                        "tối thiểu 8 ký tự"},
                {"Khách Hàng Trùng Dữ Liệu", "0909999999", "khach_vip@gmail.com", "Quang@1234", "Quang@1234",
                        "Số điện thoại hoặc Email này đã tồn tại"}
        };
    }

    @Test(dataProvider = "registerInvalidAlertData", groups = "regression",
            description = "TC_AUTH_07, 09, 11, 12: Data-Driven kiểm thử các cảnh báo lỗi đăng ký")
    public void testRegisterInvalidDataShowsSwalAlert(String fullName, String phone, String email,
                                                      String password, String confirmPassword,
                                                      String expectedAlertFragment) {
        registerPage.open(baseUrl);
        registerPage.register(fullName, phone, email, password, confirmPassword, true);

        String alertMsg = registerPage.getAlertMessage();
        Assert.assertTrue(alertMsg.contains(expectedAlertFragment),
                "Thông báo SweetAlert2 không khớp mong đợi, nhận được: " + alertMsg);
        Assert.assertTrue(registerPage.getCurrentUrl().contains("/register"),
                "Đăng ký không hợp lệ vẫn phải ở lại trang /register");
    }

    @Test(groups = "regression",
            description = "TC_AUTH_08: Chặn submit khi chưa tick đồng ý điều khoản")
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
        Assert.assertFalse(validationMsg.isEmpty(),
                "Checkbox đồng ý điều khoản phải kích hoạt HTML5 required validation");
        Assert.assertTrue(registerPage.getCurrentUrl().contains("/register"),
                "Trang phải giữ nguyên tại /register khi chưa tick điều khoản");
    }
}
