package com.poly.sof309.ui.tests;

import com.poly.sof309.ui.listeners.TestListener;
import com.poly.sof309.ui.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * =========================================================================
 * [Y4 - P2] KỊCH BẢN KIỂM THỬ GIAO DIỆN ĐĂNG NHẬP VỚI TESTNG FRAMEWORK
 * Áp dụng: @DataProvider (Data-Driven), groups (smoke/regression), @Listeners.
 * Bám sát TC_AUTH_01 - TC_AUTH_04 và TC_AUTH_10 trong docs/TEST_CASES.md.
 * =========================================================================
 */
@Listeners(TestListener.class)
public class LoginTest extends BaseUiTest {
    private LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        loginPage = new LoginPage(driver);
    }

    @DataProvider(name = "staffLoginData")
    public Object[][] getStaffLoginData() {
        return new Object[][]{
                {ADMIN_USER, ADMIN_PASS, true, "/admin/dashboard", ""},
                {STAFF_USER, STAFF_PASS, true, "/pos", ""},
                {"invalid_qa_user", "SaiMatKhau999", false, "/login", "Tên đăng nhập hoặc mật khẩu không chính xác"},
                {"unknown_staff_99", "WrongPass@123", false, "/login", "Tên đăng nhập hoặc mật khẩu không chính xác"}
        };
    }

    @Test(dataProvider = "staffLoginData", groups = {"smoke", "regression"},
            description = "TC_AUTH_01, 02, 04: Data-Driven kiểm thử đăng nhập nhân viên với nhiều bộ tài khoản")
    public void testStaffLoginDataDriven(String username, String password, boolean expectedSuccess,
                                         String expectedUrlFragment, String expectedErrorFragment) {
        loginPage.openStaffLogin(baseUrl);
        loginPage.login(username, password);

        if (expectedSuccess) {
            Assert.assertTrue(loginPage.waitForUrlContains(expectedUrlFragment),
                    "Đăng nhập thành công với user '" + username + "' phải điều hướng về " + expectedUrlFragment);
            Assert.assertTrue(loginPage.getCurrentUrl().contains(expectedUrlFragment),
                    "URL thực tế phải chứa " + expectedUrlFragment);
        } else {
            String errorMsg = loginPage.getErrorMessage();
            Assert.assertTrue(errorMsg.contains(expectedErrorFragment),
                    "Thông báo SweetAlert2 không khớp cho user '" + username + "', nhận được: " + errorMsg);
            Assert.assertTrue(loginPage.getCurrentUrl().contains(expectedUrlFragment),
                    "Đăng nhập thất bại không được rời khỏi " + expectedUrlFragment);
        }
    }

    @Test(groups = {"smoke", "regression"},
            description = "TC_AUTH_10: Khách hàng đăng nhập Portal thành công")
    public void testCustomerPortalLoginSuccessUI() {
        loginPage.openCustomerLogin(baseUrl);
        loginPage.loginAsCustomerAndWait(CUSTOMER_USER, CUSTOMER_PASS);

        Assert.assertTrue(loginPage.getCurrentUrl().contains("/home"),
                "Khách hàng đăng nhập thành công phải chuyển hướng về /home");
    }

    @Test(groups = "regression",
            description = "TC_AUTH_03: Chặn submit khi bỏ trống tài khoản đăng nhập")
    public void testStaffLoginEmptyFieldsBlockedByValidation() {
        loginPage.openStaffLogin(baseUrl);
        loginPage.clickLogin();

        String validationMsg = loginPage.getUsernameValidationMessage();
        Assert.assertFalse(validationMsg.isEmpty(),
                "Trình duyệt phải hiển thị thông báo bắt buộc nhập cho trường username");
        Assert.assertTrue(loginPage.getCurrentUrl().contains("/login"),
                "Trang vẫn phải giữ nguyên ở màn hình /login khi chưa nhập dữ liệu");
    }
}
