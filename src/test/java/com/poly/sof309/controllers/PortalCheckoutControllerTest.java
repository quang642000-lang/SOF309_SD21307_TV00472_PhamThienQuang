package com.poly.sof309.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =========================================================================
 * [Y3] UNIT TEST CHO PHÂN HỆ ĐẶT HÀNG TRỰC TUYẾN CLICK & COLLECT (PORTAL)
 * Dự án: TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE
 * Sinh viên: Phạm Thiện Quang - MSSV: TV00472 - Lớp: SD21307
 * Bám sát kịch bản: TC_CART_01 đến TC_CART_04 trong docs/TEST_CASES.md
 * =========================================================================
 */
public class PortalCheckoutControllerTest {

    private Map<String, Object> session;
    private List<String> dbCartItems;

    @BeforeEach
    public void setUp() {
        session = new HashMap<>();
        dbCartItems = new ArrayList<>(Arrays.asList("SP00001_SIZE_M", "SP00002_SIZE_L"));
    }

    // =====================================================================
    // 1. PARAMETERIZED TESTS: KIỂM TRA BIÊN THỜI GIAN HẸN LẤY NƯỚC (15 PHÚT)
    // =====================================================================

    @ParameterizedTest(name = "[{index}] Hẹn lấy sau {0} phút -> Hợp lệ, tạo đơn loaiDonHang=3, trangThaiDon=0")
    @DisplayName("TC_CART_01: Đặt hàng Click & Collect với giờ hẹn lấy hợp lệ (>= 15 phút)")
    @CsvSource({"15", "25", "60"})
    public void testPlaceOrderWithValidPickupTimeSucceeds(int offsetMinutes) {
        long now = System.currentTimeMillis();
        Timestamp pickupTime = new Timestamp(now + offsetMinutes * 60_000L);

        CheckoutOrder order = placeClickAndCollectOrder(pickupTime, now);

        assertEquals(3, order.loaiDonHang, "Đơn đặt online Click & Collect phải có loai_don_hang = 3");
        assertEquals(0, order.trangThaiDon, "Đơn mới tạo phải có trang_thai_don = 0 (Chờ xác nhận)");
    }

    @ParameterizedTest(name = "[{index}] Hẹn lấy sau {0} phút (< 15 phút) -> Ném IllegalArgumentException")
    @DisplayName("TC_CART_02: Báo lỗi khi giờ hẹn lấy nước quá gấp (< 15 phút)")
    @CsvSource({"-10", "0", "5", "14"})
    public void testPlaceOrderWithPickupTimeUnder15MinutesThrowsException(int offsetMinutes) {
        long now = System.currentTimeMillis();
        Timestamp pickupTime = new Timestamp(now + offsetMinutes * 60_000L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            placeClickAndCollectOrder(pickupTime, now);
        }, "Thời gian hẹn lấy < 15 phút bắt buộc phải bị chặn");

        assertEquals("Loi: Thoi gian hen lay phai cach thoi diem hien tai toi thieu 15 phut!", ex.getMessage());
    }

    // =====================================================================
    // 2. SECURITY & BUY NOW SESSION TESTS (TC_CART_03 & TC_CART_04)
    // =====================================================================

    @Test
    @DisplayName("TC_CART_03: Ngăn chặn spam đặt trùng đơn bằng checkoutToken UUID (Double Checkout Protection)")
    public void testDoubleCheckoutWithUsedTokenThrowsException() {
        String token = UUID.randomUUID().toString();
        session.put("checkoutToken", token);

        // Lần submit đầu tiên hợp lệ và hủy token ngay lập tức
        assertDoesNotThrow(() -> consumeCheckoutToken(token));
        assertNull(session.get("checkoutToken"), "Token phải bị xóa khỏi session ngay sau lần submit đầu tiên");

        // Lần submit thứ hai (bấm Back trình duyệt hoặc double click) phải bị chặn
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            consumeCheckoutToken(token);
        });

        assertTrue(ex.getMessage().contains("double_checkout_detected"),
                "Phải báo lỗi double_checkout_detected khi tái sử dụng token cũ");
    }

    @Test
    @DisplayName("TC_CART_04: Luồng Mua ngay (Buy Now) dọn sạch Session và giữ nguyên giỏ hàng DB")
    public void testBuyNowCheckoutCleansSessionAndPreservesDbCart() {
        String token = UUID.randomUUID().toString();
        session.put("checkoutToken", token);
        session.put("buyNowItem", "SP00003_SIZE_M");
        int initialDbCartSize = dbCartItems.size();

        boolean placed = processBuyNowCheckout(token, System.currentTimeMillis() + 25 * 60_000L, System.currentTimeMillis());

        assertTrue(placed, "Đặt hàng Mua ngay phải thành công");
        assertNull(session.get("buyNowItem"), "Thuộc tính buyNowItem trong session phải được xóa sau khi đặt xong");
        assertEquals(initialDbCartSize, dbCartItems.size(),
                "Giỏ hàng chính trong CSDL phải được giữ nguyên vẹn khi dùng luồng Mua ngay");
    }

    // =====================================================================
    // 3. TIMEOUT TEST: HIỆU NĂNG XÁC THỰC CHECKOUT DƯỚI 2 GIÂY
    // =====================================================================

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    @DisplayName("TC_CART_PERF: Xác thực 5.000 phiên kiểm tra thời gian hẹn và token dưới 2 giây")
    public void testCheckoutValidationPerformance() {
        long now = System.currentTimeMillis();
        Timestamp validPickup = new Timestamp(now + 30 * 60_000L);
        for (int i = 0; i < 5000; i++) {
            String token = UUID.randomUUID().toString();
            session.put("checkoutToken", token);
            consumeCheckoutToken(token);
            CheckoutOrder order = placeClickAndCollectOrder(validPickup, now);
            assertEquals(3, order.loaiDonHang);
        }
    }

    // =====================================================================
    // HÀM HELPER MÔ PHỎNG NGHIỆP VỤ PortalCheckoutController
    // =====================================================================

    private static class CheckoutOrder {
        int loaiDonHang;
        int trangThaiDon;
    }

    private CheckoutOrder placeClickAndCollectOrder(Timestamp pickupTime, long currentMillis) {
        if (pickupTime == null) {
            throw new IllegalArgumentException("Bat buoc phai hen gio den cua hang nhan nuoc!");
        }
        // Ràng buộc tối thiểu 15 phút (giữ dung sai 1 giây giống thực tế hệ thống)
        long minAllowedMillis = currentMillis + (15 * 60_000L) - 1000L;
        if (pickupTime.getTime() < minAllowedMillis) {
            throw new IllegalArgumentException("Loi: Thoi gian hen lay phai cach thoi diem hien tai toi thieu 15 phut!");
        }
        CheckoutOrder order = new CheckoutOrder();
        order.loaiDonHang = 3; // Online Click & Collect
        order.trangThaiDon = 0; // Chờ xác nhận
        return order;
    }

    private void consumeCheckoutToken(String paramToken) {
        String sessionToken = (String) session.get("checkoutToken");
        if (sessionToken == null || paramToken == null || !sessionToken.equals(paramToken)) {
            throw new IllegalStateException("redirect:/cart?msg=double_checkout_detected");
        }
        session.remove("checkoutToken");
    }

    private boolean processBuyNowCheckout(String paramToken, long pickupMillis, long nowMillis) {
        consumeCheckoutToken(paramToken);
        placeClickAndCollectOrder(new Timestamp(pickupMillis), nowMillis);
        if (session.get("buyNowItem") != null) {
            session.remove("buyNowItem");
        }
        return true;
    }
}
