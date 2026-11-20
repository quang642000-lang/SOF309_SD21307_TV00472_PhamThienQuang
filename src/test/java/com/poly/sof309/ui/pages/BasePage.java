package com.poly.sof309.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * =========================================================================
 * [Y4 - P1] BASE PAGE OBJECT CHO SELENIUM POM (TEA POS SYSTEM)
 * Cung cấp các thao tác tương tác DOM chuẩn dùng Explicit Wait (WebDriverWait)
 * và xử lý hộp thoại SweetAlert2 / HTML5 validation, không dùng Thread.sleep().
 * =========================================================================
 */
public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    // Locators dùng chung cho SweetAlert2 của hệ thống Tea POS
    protected final By swalContainer = By.cssSelector(".swal2-container");
    protected final By swalPopup = By.cssSelector(".swal2-popup");
    protected final By swalTitle = By.cssSelector(".swal2-popup .swal2-title");
    protected final By swalHtmlContent = By.cssSelector(".swal2-popup .swal2-html-container");
    protected final By swalConfirmButton = By.cssSelector(".swal2-popup .swal2-confirm");

    public BasePage(WebDriver driver) {
        this(driver, Duration.ofSeconds(10));
    }

    public BasePage(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
    }

    protected void click(By locator) {
        WebElement element = waitForVisibility(locator);
        scrollIntoView(element);
        waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        scrollIntoView(element);
        element.clear();
        if (text != null && !text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    protected String getText(By locator) {
        return waitForVisibility(locator).getText().trim();
    }

    protected String getValidationMessage(By locator) {
        WebElement element = waitForVisibility(locator);
        Object message = ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", element);
        return message != null ? message.toString() : "";
    }

    public boolean waitForUrlContains(String fraction) {
        return wait.until(ExpectedConditions.urlContains(fraction));
    }

    public String waitForSwalTitle() {
        waitForVisibility(swalPopup);
        return getText(swalTitle);
    }

    public String waitForSwalMessage() {
        waitForVisibility(swalPopup);
        return getText(swalHtmlContent);
    }

    public void confirmSwalDialog() {
        waitForVisibility(swalPopup);
        click(swalConfirmButton);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(swalContainer));
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
