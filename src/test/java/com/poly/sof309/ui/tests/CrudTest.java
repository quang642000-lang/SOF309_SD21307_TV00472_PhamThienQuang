package com.poly.sof309.ui.tests;

import com.poly.sof309.ui.pages.LoginPage;
import com.poly.sof309.ui.pages.PortalMenuPage;
import com.poly.sof309.ui.pages.ProductPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =========================================================================
 * [Y4 - P1] KỊCH BẢN KIỂM THỬ CRUD SẢN PHẨM & TÌM KIẾM MENU (SELENIUM POM)
 * Bám sát TC_PROD_01 - TC_PROD_03, TC_PROD_05 và TC_PROD_06 trong docs/TEST_CASES.md.
 * =========================================================================
 */
public class CrudTest extends BaseUiTest {

    @Test
    @DisplayName("TC_PROD_02: Cảnh báo SweetAlert2 khi thêm sản phẩm mà không chọn kích cỡ nào")
    public void testCreateProductWithoutSizeShowsSwalWarning() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.openStaffLogin(baseUrl);
        loginPage.loginAsAdminAndWait(ADMIN_USER, ADMIN_PASS);

        ProductPage productPage = new ProductPage(driver);
        productPage.openCreateForm(baseUrl)
                .enterProductName("Trà Test Thiếu Size")
                .selectCategoryByIndex(1)
                .uncheckAllSizes()
                .clickSaveProduct();

        String alertMsg = productPage.getValidationAlertMessage();
        assertTrue(alertMsg.contains("tối thiểu một kích cỡ và giá bán"),
                "Phải hiển thị cảnh báo bắt buộc cấu hình kích cỡ và giá bán, nhận được: " + alertMsg);
    }

    @Test
    @DisplayName("TC_PROD_03: Chặn submit bằng HTML5 validation khi để trống Tên đồ uống")
    public void testCreateProductEmptyNameBlockedByHtml5Validation() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.openStaffLogin(baseUrl);
        loginPage.loginAsAdminAndWait(ADMIN_USER, ADMIN_PASS);

        ProductPage productPage = new ProductPage(driver);
        productPage.openCreateForm(baseUrl)
                .selectCategoryByIndex(1)
                .configureFirstAvailableSize(35000, "350ml")
                .clickSaveProduct();

        String validationMsg = productPage.getProductNameValidationMessage();
        assertFalse(validationMsg.isEmpty(),
                "Ô nhập Tên đồ uống phải kích hoạt HTML5 required validation khi bị bỏ trống");
        assertTrue(productPage.getCurrentUrl().contains("action=create"),
                "Trang phải giữ nguyên ở biểu mẫu tạo mới sản phẩm khi thiếu tên đồ uống");
    }

    @Test
    @DisplayName("TC_PROD_01 & TC_PROD_05: Luồng Thêm mới -> Tìm kiếm -> Cập nhật -> Xóa sạch (Hard Delete) sản phẩm")
    public void testAdminProductCreateEditAndHardDeleteFlow() {
        String createdName = "Tra Sua POM " + System.currentTimeMillis();
        String updatedName = createdName + " Edited";

        LoginPage loginPage = new LoginPage(driver);
        loginPage.openStaffLogin(baseUrl);
        loginPage.loginAsAdminAndWait(ADMIN_USER, ADMIN_PASS);

        // 1. Mở danh sách -> Bấm Thêm sản phẩm mới -> Nhập form & lưu
        ProductPage productPage = new ProductPage(driver);
        productPage.openList(baseUrl)
                .clickAddNewProduct()
                .createProduct(createdName, 1, 35000, "350ml", "Sản phẩm kiểm thử tự động Selenium POM");

        assertTrue(productPage.waitForResultMsg("createsuccess"),
                "Sau khi thêm mới thành công URL phải chứa msg=createsuccess");
        assertTrue(productPage.isProductDisplayed(createdName),
                "Sản phẩm vừa tạo phải hiển thị trong bảng danh sách khi tìm kiếm");

        // 2. Sửa tên sản phẩm -> Lưu cập nhật
        productPage.clickEditProduct(createdName)
                .enterProductName(updatedName)
                .clickSaveProduct();

        assertTrue(productPage.waitForResultMsg("updatesuccess"),
                "Sau khi cập nhật thành công URL phải chứa msg=updatesuccess");
        assertTrue(productPage.isProductDisplayed(updatedName),
                "Sản phẩm sau cập nhật tên phải hiển thị trong bảng danh sách");

        // 3. Hard Delete để dọn sạch bản ghi thử nghiệm khỏi SQL Server
        productPage.deleteProductAndConfirm(updatedName);
        assertTrue(productPage.waitForResultMsg("harddeletesuccess"),
                "Sản phẩm chưa từng có hóa đơn phải được Hard Delete và trả về msg=harddeletesuccess");
        assertFalse(productPage.isProductDisplayed(updatedName),
                "Sản phẩm sau khi Hard Delete không được xuất hiện lại trong bảng");
    }

    @Test
    @DisplayName("TC_PROD_06: Tìm kiếm món nước trên thực đơn Portal /products hoạt động chính xác")
    public void testPortalMenuSearchWorksEndToEnd() {
        PortalMenuPage portalMenuPage = new PortalMenuPage(driver);
        portalMenuPage.open(baseUrl);

        assertTrue(portalMenuPage.getProductCardCount() > 0,
                "Trang thực đơn /products phải hiển thị ít nhất 1 sản phẩm đang mở bán");

        // Tìm từ khóa có kết quả
        portalMenuPage.searchDrink("Trà Sữa");
        List<String> matchedNames = portalMenuPage.getDisplayedProductNames();
        assertFalse(matchedNames.isEmpty(),
                "Tìm kiếm từ khóa 'Trà Sữa' phải trả về ít nhất 1 món phù hợp");
        assertTrue(matchedNames.stream().anyMatch(name -> name.toLowerCase().contains("trà sữa")),
                "Danh sách kết quả phải chứa sản phẩm có tên 'Trà Sữa'");

        // Tìm từ khóa không tồn tại -> hiện trạng thái trống
        portalMenuPage.searchDrink("MonNuocKhongTonTaiXYZ999");
        assertTrue(portalMenuPage.isEmptyStateDisplayed(),
                "Khi tìm kiếm từ khóa không tồn tại phải hiển thị thông báo không tìm thấy món phù hợp");
    }
}
