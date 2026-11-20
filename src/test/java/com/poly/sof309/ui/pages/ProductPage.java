package com.poly.sof309.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * =========================================================================
 * [Y4 - P1] PAGE OBJECT MODEL (POM) CHO QUẢN LÝ SẢN PHẨM ADMIN
 * Khớp DOM thực tế của san_pham.jsp (/admin/sanpham) và sanpham-form.jsp.
 * =========================================================================
 */
public class ProductPage extends BasePage {
    // 1. Locators trang danh sách (san_pham.jsp)
    private final By btnAddNewProduct = By.cssSelector("a[href*='action=create']");
    private final By txtSearch = By.id("productSearchInput");
    private final By selFilterCategory = By.id("filterCategory");
    private final By selFilterStatus = By.id("filterStatus");
    private final By productTable = By.id("productTable");
    private final By desktopRows = By.cssSelector("#productTableBody tr.product-row");

    // 2. Locators trang biểu mẫu (sanpham-form.jsp)
    private final By formProduct = By.id("productForm");
    private final By txtProductName = By.id("tenSp");
    private final By selCategory = By.id("maDm");
    private final By txtDisplayOrder = By.id("thuTuHienThi");
    private final By txtDescription = By.id("moTa");
    private final By sizeCheckboxes = By.cssSelector("#sizesConfigTableBody input.size-check");
    private final By btnSaveProduct = By.xpath("//form[@id='productForm']//button[@type='submit']");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    // Dynamic locators đóng gói nội bộ theo tên sản phẩm
    private By rowByProductName(String productName) {
        return By.xpath("//tbody[@id='productTableBody']//tr[contains(@class,'product-row') and @data-tensp='"
                + productName + "']");
    }

    private By editBtnByProductName(String productName) {
        return By.xpath("//tbody[@id='productTableBody']//tr[contains(@class,'product-row') and @data-tensp='"
                + productName + "']//a[contains(@href,'action=edit')]");
    }

    private By deleteBtnByProductName(String productName) {
        return By.xpath("//tbody[@id='productTableBody']//tr[contains(@class,'product-row') and @data-tensp='"
                + productName + "']//button[contains(@class,'btn-action-delete')]");
    }

    // 3. Điều hướng
    public ProductPage openList(String baseUrl) {
        driver.get(baseUrl + "/admin/sanpham");
        waitForVisibility(productTable);
        return this;
    }

    public ProductPage openCreateForm(String baseUrl) {
        driver.get(baseUrl + "/admin/sanpham?action=create");
        waitForVisibility(formProduct);
        return this;
    }

    public ProductPage clickAddNewProduct() {
        click(btnAddNewProduct);
        waitForVisibility(formProduct);
        return this;
    }

    // 4. Tương tác biểu mẫu thêm / sửa sản phẩm
    public ProductPage enterProductName(String tenSp) {
        type(txtProductName, tenSp);
        return this;
    }

    public ProductPage selectCategoryByIndex(int index) {
        WebElement selectEl = waitForVisibility(selCategory);
        scrollIntoView(selectEl);
        new Select(selectEl).selectByIndex(index);
        return this;
    }

    public ProductPage enterDisplayOrder(int order) {
        type(txtDisplayOrder, String.valueOf(order));
        return this;
    }

    public ProductPage enterDescription(String moTa) {
        type(txtDescription, moTa);
        return this;
    }

    public ProductPage configureFirstAvailableSize(int price, String volume) {
        List<WebElement> checkboxes = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(sizeCheckboxes));
        WebElement firstCheck = checkboxes.get(0);
        String maSize = firstCheck.getAttribute("id").replace("size_active_", "");

        scrollIntoView(firstCheck);
        if (!firstCheck.isSelected()) {
            wait.until(ExpectedConditions.elementToBeClickable(firstCheck)).click();
        }

        By priceLocator = By.id("size_price_" + maSize);
        WebElement priceInput = waitForClickable(priceLocator);
        scrollIntoView(priceInput);
        priceInput.clear();
        priceInput.sendKeys(String.valueOf(price));

        if (volume != null && !volume.isEmpty()) {
            By volumeLocator = By.id("size_volume_" + maSize);
            WebElement volumeInput = waitForClickable(volumeLocator);
            scrollIntoView(volumeInput);
            volumeInput.clear();
            volumeInput.sendKeys(volume);
        }
        return this;
    }

    public ProductPage uncheckAllSizes() {
        List<WebElement> checkboxes = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(sizeCheckboxes));
        for (WebElement checkbox : checkboxes) {
            if (checkbox.isSelected()) {
                scrollIntoView(checkbox);
                wait.until(ExpectedConditions.elementToBeClickable(checkbox)).click();
            }
        }
        return this;
    }

    public void clickSaveProduct() {
        click(btnSaveProduct);
    }

    public void createProduct(String tenSp, int categoryIndex, int price, String volume, String moTa) {
        enterProductName(tenSp);
        selectCategoryByIndex(categoryIndex);
        enterDescription(moTa);
        configureFirstAvailableSize(price, volume);
        clickSaveProduct();
    }

    // 5. Tương tác bảng danh sách (tìm kiếm, lọc, sửa, xóa)
    public ProductPage searchProduct(String keyword) {
        type(txtSearch, keyword);
        return this;
    }

    public ProductPage filterByCategory(String maDm) {
        new Select(waitForVisibility(selFilterCategory)).selectByValue(maDm);
        return this;
    }

    public ProductPage filterByStatus(String status) {
        new Select(waitForVisibility(selFilterStatus)).selectByValue(status);
        return this;
    }

    public int getVisibleRowCount() {
        return (int) driver.findElements(desktopRows).stream().filter(WebElement::isDisplayed).count();
    }

    public boolean isProductDisplayed(String productName) {
        searchProduct(productName);
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(rowByProductName(productName)));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public ProductPage clickEditProduct(String productName) {
        searchProduct(productName);
        waitForVisibility(rowByProductName(productName));
        click(editBtnByProductName(productName));
        waitForVisibility(formProduct);
        return this;
    }

    public void deleteProductAndConfirm(String productName) {
        searchProduct(productName);
        waitForVisibility(rowByProductName(productName));
        click(deleteBtnByProductName(productName));
        confirmSwalDialog();
        waitForUrlContains("deletesuccess");
    }

    public boolean waitForResultMsg(String msg) {
        return waitForUrlContains("msg=" + msg);
    }

    public String getValidationAlertMessage() {
        return waitForSwalMessage();
    }

    public String getProductNameValidationMessage() {
        return getValidationMessage(txtProductName);
    }
}
