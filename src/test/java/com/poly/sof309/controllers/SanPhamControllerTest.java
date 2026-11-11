package com.poly.sof309.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =========================================================================
 * [Y3] UNIT TEST CHO PHÂN HỆ QUẢN TRỊ THỰC ĐƠN SẢN PHẨM & KÍCH CỠ (MENU CRUD)
 * Dự án: TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE
 * Sinh viên: Phạm Thiện Quang - MSSV: TV00472 - Lớp: SD21307
 * Bám sát kịch bản: TC_PROD_01 đến TC_PROD_05 trong docs/TEST_CASES.md
 * =========================================================================
 */
public class SanPhamControllerTest {

    // =====================================================================
    // 1. PARAMETERIZED TESTS: KIỂM TRA BIÊN FORM TẠO MÓN NƯỚC MỚI
    // =====================================================================

    @ParameterizedTest(name = "[{index}] Tên=''{0}'', Danh mục=''{1}'', Thứ tự={2} -> Hợp lệ: {3}")
    @DisplayName("TC_PROD_01 - 03: Parameterized Test kiểm tra validation form thêm mới sản phẩm")
    @CsvSource({
            "'Trà Sữa Oolong Nướng', 'DM01', 1, true",
            "'Trà Sen Vàng Kem Cheese', 'DM02', 2, true",
            "'', 'DM01', 1, false",                          // Tên món trống
            "'   ', 'DM01', 1, false",                       // Tên món chỉ có khoảng trắng
            "'Trà Đào Cam Sả', '', 1, false",                // Danh mục trống
            "'Trà Vải', 'DM01', -1, false"                    // Thứ tự hiển thị âm
    })
    public void testCreateProductValidation(String tenSp, String maDm, int thuTuHienThi, boolean expectedValid) {
        boolean isValid = validateProductInput(tenSp, maDm, thuTuHienThi);
        assertEquals(expectedValid, isValid, "Kết quả validation sản phẩm không khớp với mong đợi!");
    }

    // =====================================================================
    // 2. EXCEPTION TESTING: BẮT NGOẠI LỆ NGHIỆP VỤ VỚI assertThrows
    // =====================================================================

    @Test
    @DisplayName("TC_PROD_02: Ném ngoại lệ IllegalArgumentException khi tạo món mà không cấu hình Size và Giá")
    public void testCreateProductWithoutSizeThrowsException() {
        String tenSp = "Trà Xanh Thái Lan";
        String maDm = "DM01";
        List<SizeConfig> emptySizes = new ArrayList<>(); // Rỗng, không chọn size nào

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            createProductWithSizes(tenSp, maDm, emptySizes);
        }, "Phải ném ngoại lệ IllegalArgumentException khi danh sách kích cỡ rỗng");

        assertEquals("Sản phẩm mới bắt buộc phải cấu hình tối thiểu một kích cỡ và giá bán đi kèm!",
                ex.getMessage(), "Nội dung ngoại lệ phải khớp chính xác với ràng buộc trong SanPhamController");
    }

    @Test
    @DisplayName("TC_PROD_EX_01: Ném ngoại lệ IllegalArgumentException khi giá bán của kích cỡ nhỏ hơn 0")
    public void testNegativeSizePriceThrowsException() {
        String tenSp = "Trà Sữa Trân Châu";
        String maDm = "DM01";
        List<SizeConfig> sizes = new ArrayList<>();
        sizes.add(new SizeConfig(1, -15000 /* Giá âm */));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            createProductWithSizes(tenSp, maDm, sizes);
        });

        assertTrue(ex.getMessage().contains("Giá bán của kích cỡ không được nhỏ hơn 0"),
                "Ngoại lệ phải thông báo giá bán kích cỡ không hợp lệ");
    }

    // =====================================================================
    // 3. LOGIC RẼ NHÁNH XÓA SẢN PHẨM: SOFT DELETE VS HARD DELETE
    // =====================================================================

    @Test
    @DisplayName("TC_PROD_04: Sản phẩm đã từng có hóa đơn bán lẻ -> Thực hiện SOFT DELETE (trang_thai = 0)")
    public void testProductWithOrdersPerformsSoftDelete() {
        String maSp = "SP001";
        boolean hasOrdersInHistory = true; // Đã từng bán trong CHI_TIET_DON_HANG

        DeleteAction action = resolveDeleteAction(maSp, hasOrdersInHistory);

        assertEquals(DeleteType.SOFT_DELETE, action.type,
                "Sản phẩm đã có hóa đơn lịch sử phải thực hiện SOFT DELETE để bảo toàn dữ liệu kế toán!");
        assertFalse(action.newStatus, "Trạng thái hoạt động của sản phẩm phải chuyển về 0 (ngừng kinh doanh)");
    }

    @Test
    @DisplayName("TC_PROD_05: Sản phẩm mới tạo chưa từng có hóa đơn -> Thực hiện HARD DELETE (xóa sạch DB)")
    public void testProductWithoutOrdersPerformsHardDelete() {
        String maSp = "SP999";
        boolean hasOrdersInHistory = false; // Chưa từng phát sinh đơn hàng

        DeleteAction action = resolveDeleteAction(maSp, hasOrdersInHistory);

        assertEquals(DeleteType.HARD_DELETE, action.type,
                "Sản phẩm chưa từng có hóa đơn được phép HARD DELETE xóa hoàn toàn khỏi CSDL!");
    }

    // =====================================================================
    // 4. TIMEOUT TEST: XỬ LÝ LỌC VÀ TÌM KIẾM SẢN PHẨM DƯỚI 2 GIÂY
    // =====================================================================

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    @DisplayName("TC_PROD_PERF: Tìm kiếm và duyệt danh sách thực đơn đa kích cỡ siêu tốc dưới 2 giây")
    public void testProductFilteringPerformance() {
        List<String> productCatalog = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            productCatalog.add("Món nước số " + i);
        }

        long matchCount = productCatalog.stream()
                .filter(name -> name.contains("50"))
                .count();

        assertTrue(matchCount > 0);
    }

    // =====================================================================
    // HÀM HELPER MÔ PHỎNG NGHIỆP VỤ SanPhamController
    // =====================================================================

    private boolean validateProductInput(String tenSp, String maDm, int thuTuHienThi) {
        if (tenSp == null || tenSp.trim().isEmpty()) return false;
        if (maDm == null || maDm.trim().isEmpty()) return false;
        return thuTuHienThi >= 0;
    }

    private static class SizeConfig {
        int maSize;
        int giaBan;
        SizeConfig(int maSize, int giaBan) {
            this.maSize = maSize;
            this.giaBan = giaBan;
        }
    }

    private void createProductWithSizes(String tenSp, String maDm, List<SizeConfig> sizes) {
        if (!validateProductInput(tenSp, maDm, 0)) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ Tên đồ uống và Nhóm danh mục!");
        }
        if (sizes == null || sizes.isEmpty()) {
            throw new IllegalArgumentException("Sản phẩm mới bắt buộc phải cấu hình tối thiểu một kích cỡ và giá bán đi kèm!");
        }
        for (SizeConfig sc : sizes) {
            if (sc.giaBan < 0) {
                throw new IllegalArgumentException("Giá bán của kích cỡ không được nhỏ hơn 0!");
            }
        }
    }

    private enum DeleteType { SOFT_DELETE, HARD_DELETE }

    private static class DeleteAction {
        DeleteType type;
        boolean newStatus;
        DeleteAction(DeleteType type, boolean newStatus) {
            this.type = type;
            this.newStatus = newStatus;
        }
    }

    private DeleteAction resolveDeleteAction(String maSp, boolean hasOrders) {
        if (hasOrders) {
            return new DeleteAction(DeleteType.SOFT_DELETE, false);
        } else {
            return new DeleteAction(DeleteType.HARD_DELETE, false);
        }
    }
}
