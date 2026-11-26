package com.poly.sof309.ui.tests;

import com.poly.sof309.ui.listeners.TestListener;
import com.poly.sof309.ui.pages.LoginPage;
import com.poly.sof309.ui.pages.PortalMenuPage;
import com.poly.sof309.ui.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

/**
 * =========================================================================
 * [Y4 - P2] KỊCH BẢN KIỂM THỬ CRUD SẢN PHẨM & MENU VỚI TESTNG FRAMEWORK
 * Áp dụng: groups (smoke/regression), dependsOnMethods (Thêm -> Sửa -> Xóa), @Listeners.
 * Bám sát TC_PROD_01 - TC_PROD_03, TC_PROD_05 và TC_PROD_06 trong docs/TEST_CASES.md.
 * =========================================================================
 */
@Listeners(TestListener.class)
public class CrudTest extends BaseUiTest {
    private String createdProductName;
    private String updatedProductName;

    @Test(groups = {"smoke", "regression"},
            description = "Smoke: Đăng nhập Admin và hiển thị bảng danh sách sản phẩm")
    public void testAdminProductListDisplayed() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.openStaffLogin(baseUrl);
        loginPage.loginAsAdminAndWait(ADMIN_USER, ADMIN_PASS);

        ProductPage productPage = new ProductPage(driver);
        productPage.openList(baseUrl);

        Assert.assertTrue(productPage.getVisibleRowCount() > 0,
                "Bảng quản lý sản phẩm Admin phải hiển thị ít nhất 1 dòng dữ liệu");
    }

    @Test(groups = {"smoke", "regression"},
            description = "TC_PROD_06: Tìm kiếm món nước trên thực đơn Portal /products")
    public void testPortalMenuSearchWorksEndToEnd() {
        PortalMenuPage portalMenuPage = new PortalMenuPage(driver);
        portalMenuPage.open(baseUrl);

        Assert.assertTrue(portalMenuPage.getProductCardCount() > 0,
                "Trang thực đơn /products phải hiển thị ít nhất 1 sản phẩm đang mở bán");

        portalMenuPage.searchDrink("Trà Sữa");
        List<String> matchedNames = portalMenuPage.getDisplayedProductNames();
        Assert.assertFalse(matchedNames.isEmpty(),
                "Tìm kiếm từ khóa 'Trà Sữa' phải trả về ít nhất 1 món phù hợp");
        Assert.assertTrue(matchedNames.stream().anyMatch(name -> name.toLowerCase().contains("trà sữa")),
                "Danh sách kết quả phải chứa sản phẩm có tên 'Trà Sữa'");

        portalMenuPage.searchDrink("MonNuocKhongTonTaiXYZ999");
        Assert.assertTrue(portalMenuPage.isEmptyStateDisplayed(),
                "Khi tìm kiếm từ khóa không tồn tại phải hiển thị thông báo không tìm thấy món phù hợp");
    }

    @Test(groups = "regression",
            description = "TC_PROD_02: Cảnh báo SweetAlert2 khi thêm sản phẩm thiếu kích cỡ")
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
        Assert.assertTrue(alertMsg.contains("tối thiểu một kích cỡ và giá bán"),
                "Phải hiển thị cảnh báo bắt buộc cấu hình kích cỡ và giá bán, nhận được: " + alertMsg);
    }

    @Test(groups = "regression",
            description = "TC_PROD_03: Chặn submit khi bỏ trống tên đồ uống")
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
        Assert.assertFalse(validationMsg.isEmpty(),
                "Ô nhập Tên đồ uống phải kích hoạt HTML5 required validation khi bị bỏ trống");
        Assert.assertTrue(productPage.getCurrentUrl().contains("action=create"),
                "Trang phải giữ nguyên ở biểu mẫu tạo mới sản phẩm khi thiếu tên đồ uống");
    }

    @Test(groups = "regression", priority = 1,
            description = "TC_PROD_01: Thêm mới sản phẩm thành công")
    public void testCreateProductSuccess() {
        createdProductName = "Tra Sua TestNG " + System.currentTimeMillis();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.openStaffLogin(baseUrl);
        loginPage.loginAsAdminAndWait(ADMIN_USER, ADMIN_PASS);

        ProductPage productPage = new ProductPage(driver);
        productPage.openList(baseUrl)
                .clickAddNewProduct()
                .createProduct(createdProductName, 1, 35000, "350ml",
                        "Sản phẩm kiểm thử TestNG CRUD phụ thuộc");

        Assert.assertTrue(productPage.waitForResultMsg("createsuccess"),
                "Sau khi thêm mới thành công URL phải chứa msg=createsuccess");
        Assert.assertTrue(productPage.isProductDisplayed(createdProductName),
                "Sản phẩm vừa tạo phải hiển thị trong bảng danh sách khi tìm kiếm");
    }

    @Test(groups = "regression", priority = 2, dependsOnMethods = {"testCreateProductSuccess"},
            description = "TC_PROD_01: Cập nhật tên sản phẩm vừa tạo")
    public void testEditProductSuccess() {
        updatedProductName = createdProductName + " Edited";

        LoginPage loginPage = new LoginPage(driver);
        loginPage.openStaffLogin(baseUrl);
        loginPage.loginAsAdminAndWait(ADMIN_USER, ADMIN_PASS);

        ProductPage productPage = new ProductPage(driver);
        productPage.openList(baseUrl)
                .clickEditProduct(createdProductName)
                .enterProductName(updatedProductName)
                .clickSaveProduct();

        Assert.assertTrue(productPage.waitForResultMsg("updatesuccess"),
                "Sau khi cập nhật thành công URL phải chứa msg=updatesuccess");
        Assert.assertTrue(productPage.isProductDisplayed(updatedProductName),
                "Sản phẩm sau cập nhật tên phải hiển thị trong bảng danh sách");
    }

    @Test(groups = "regression", priority = 3, dependsOnMethods = {"testEditProductSuccess"},
            description = "TC_PROD_05: Hard Delete sản phẩm thử nghiệm sau khi sửa")
    public void testDeleteProductHardDeleteSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.openStaffLogin(baseUrl);
        loginPage.loginAsAdminAndWait(ADMIN_USER, ADMIN_PASS);

        ProductPage productPage = new ProductPage(driver);
        productPage.openList(baseUrl)
                .deleteProductAndConfirm(updatedProductName);

        Assert.assertTrue(productPage.waitForResultMsg("harddeletesuccess"),
                "Sản phẩm chưa từng có hóa đơn phải được Hard Delete và trả về msg=harddeletesuccess");
        Assert.assertFalse(productPage.isProductDisplayed(updatedProductName),
                "Sản phẩm sau khi Hard Delete không được xuất hiện lại trong bảng");
    }
}
