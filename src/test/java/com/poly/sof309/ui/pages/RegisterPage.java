package com.poly.sof309.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * =========================================================================
 * [Y4 - P1] PAGE OBJECT MODEL (POM) CHO TRANG ĐĂNG KÝ KHÁCH HÀNG PORTAL
 * Khớp DOM thực tế của register.jsp (/register).
 * =========================================================================
 */
public class RegisterPage extends BasePage {
    // 1. Locators khớp chính xác id trong register.jsp
    private final By txtFullName = By.id("tenKh");
    private final By txtPhone = By.id("soDienThoai");
    private final By txtEmail = By.id("email");
    private final By txtPassword = By.id("password");
    private final By txtConfirmPassword = By.id("confirmPassword");
    private final By chkAgreeTerms = By.id("dongYDieuKhoan");
    private final By btnRegister = By.xpath("//form[@id='registerForm']//button[@type='submit']");
    private final By pageHeading = By.cssSelector(".register-card h3");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    // 2. Mở trang đăng ký
    public RegisterPage open(String baseUrl) {
        driver.get(baseUrl + "/register");
        waitForVisibility(txtFullName);
        return this;
    }

    // 3. Các hành động nhập liệu
    public RegisterPage enterFullName(String fullName) {
        type(txtFullName, fullName);
        return this;
    }

    public RegisterPage enterPhone(String phone) {
        type(txtPhone, phone);
        return this;
    }

    public RegisterPage enterEmail(String email) {
        type(txtEmail, email);
        return this;
    }

    public RegisterPage enterPassword(String password) {
        type(txtPassword, password);
        return this;
    }

    public RegisterPage enterConfirmPassword(String confirmPassword) {
        type(txtConfirmPassword, confirmPassword);
        return this;
    }

    public RegisterPage setAgreeTerms(boolean agree) {
        WebElement checkbox = waitForClickable(chkAgreeTerms);
        scrollIntoView(checkbox);
        if (checkbox.isSelected() != agree) {
            checkbox.click();
        }
        return this;
    }

    public void clickRegister() {
        click(btnRegister);
    }

    public void register(String fullName, String phone, String email,
                         String password, String confirmPassword, boolean agreeTerms) {
        enterFullName(fullName);
        enterPhone(phone);
        enterEmail(email);
        enterPassword(password);
        enterConfirmPassword(confirmPassword);
        setAgreeTerms(agreeTerms);
        clickRegister();
    }

    // 4. Đọc thông báo lỗi / tiêu đề
    public String getAlertTitle() {
        return waitForSwalTitle();
    }

    public String getAlertMessage() {
        return waitForSwalMessage();
    }

    public void dismissAlert() {
        confirmSwalDialog();
    }

    public String getAgreeTermsValidationMessage() {
        return getValidationMessage(chkAgreeTerms);
    }

    public String getHeadingTitle() {
        return getText(pageHeading);
    }
}
