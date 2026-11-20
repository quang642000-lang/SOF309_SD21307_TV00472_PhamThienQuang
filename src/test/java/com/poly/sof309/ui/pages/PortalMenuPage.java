package com.poly.sof309.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * =========================================================================
 * [Y4 - P1] PAGE OBJECT MODEL (POM) CHO TRANG THỰC ĐƠN PORTAL KHÁCH HÀNG
 * Khớp DOM thực tế của danh_sach_san_pham.jsp (/products).
 * =========================================================================
 */
public class PortalMenuPage extends BasePage {
    // Locators thực đơn Portal (/products)
    private final By txtSearch = By.cssSelector("#portalFilterSidebar input[name='search']");
    private final By btnSearch = By.cssSelector("#portalFilterSidebar button[type='submit']");
    private final By productCards = By.cssSelector(".product-card");
    private final By productTitleLinks = By.cssSelector(".product-card .product-card-title a");
    private final By emptyStateMessage = By.xpath("//h5[contains(text(),'Không tìm thấy món nước nào phù hợp')]");

    public PortalMenuPage(WebDriver driver) {
        super(driver);
    }

    public PortalMenuPage open(String baseUrl) {
        driver.get(baseUrl + "/products");
        waitForVisibility(txtSearch);
        return this;
    }

    public PortalMenuPage searchDrink(String keyword) {
        type(txtSearch, keyword);
        click(btnSearch);
        waitForUrlContains("search=");
        return this;
    }

    public int getProductCardCount() {
        return driver.findElements(productCards).size();
    }

    public List<String> getDisplayedProductNames() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productTitleLinks));
        return driver.findElements(productTitleLinks)
                .stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public boolean isEmptyStateDisplayed() {
        try {
            waitForVisibility(emptyStateMessage);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
