package com.poly.sof309.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * =========================================================================
 * [Y4 - P1] PAGE OBJECT MODEL (POM) CHO TRANG ĐĂNG NHẬP TEA POS
 * Khớp DOM thực tế của login_admin.jsp (/login) và login_customer.jsp (/customer/login).
 * =========================================================================
 */
public class LoginPage extends BasePage {
    // 1. Định nghĩa các Locator theo DOM thực tế của JSP
    private final By txtUsername = By.name("username");
    private final By txtPassword = By.name("password");
    private final By btnLogin = By.xpath("//form//button[@type='submit']");
    private final By brandBannerTitle = By.cssSelector(".brand-banner h4");
    private final By linkRegister = By.cssSelector("a[href*='/register']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // 2. Điều hướng trang
    public LoginPage openStaffLogin(String baseUrl) {
        driver.get(baseUrl + "/login");
        waitForVisibility(txtUsername);
        return this;
    }

    public LoginPage openCustomerLogin(String baseUrl) {
        driver.get(baseUrl + "/customer/login");
        waitForVisibility(txtUsername);
        return this;
    }

    // 3. Các hành động nghiệp vụ
    public LoginPage enterUsername(String username) {
        type(txtUsername, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(txtPassword, password);
        return this;
    }

    public void clickLogin() {
        click(btnLogin);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public void loginAsAdminAndWait(String username, String password) {
        login(username, password);
        waitForUrlContains("/admin/dashboard");
    }

    public void loginAsCashierAndWait(String username, String password) {
        login(username, password);
        waitForUrlContains("/pos");
    }

    public void loginAsCustomerAndWait(String username, String password) {
        login(username, password);
        waitForUrlContains("/home");
    }

    public String getErrorMessage() {
        return waitForSwalMessage();
    }

    public void dismissErrorAlert() {
        confirmSwalDialog();
    }

    public String getUsernameValidationMessage() {
        return getValidationMessage(txtUsername);
    }

    public String getPasswordValidationMessage() {
        return getValidationMessage(txtPassword);
    }

    public String getBrandBannerTitle() {
        return getText(brandBannerTitle);
    }

    public void clickGoToRegister() {
        click(linkRegister);
        waitForUrlContains("/register");
    }

    public void logout(String baseUrl) {
        driver.get(baseUrl + "/logout");
        waitForVisibility(txtUsername);
    }
}
