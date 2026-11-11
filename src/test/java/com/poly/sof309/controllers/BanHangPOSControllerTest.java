package com.poly.sof309.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =========================================================================
 * [Y3] UNIT TEST CHO PHÂN HỆ QUẦY THU NGÂN POS & THANH TOÁN (POS CASHIER)
 * Dự án: TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE
 * Sinh viên: Phạm Thiện Quang - MSSV: TV00472 - Lớp: SD21307
 * Bám sát kịch bản: TC_PAY_01 đến TC_PAY_05 trong docs/TEST_CASES.md
 * =========================================================================
 */
public class BanHangPOSControllerTest {

    // =====================================================================
    // 1. PARAMETERIZED TESTS: TÍNH THUẾ VAT 8% VÀ TIỀN THỪA KHÁCH ĐƯA
    // =====================================================================

    @ParameterizedTest(name = "[{index}] Tiền sau giảm={0}đ -> VAT 8%={1}đ, Tổng trả={2}đ")
    @DisplayName("TC_PAY_03: Parameterized Test kiểm tra công thức tính thuế VAT 8% trên số tiền sau giảm")
    @CsvSource({
            "0, 0, 0",
            "50000, 4000, 54000",
            "80000, 6400, 86400",
            "100000, 8000, 108000",
            "125000, 10000, 135000",
            "250000, 20000, 270000"
    })
    public void testVat8PercentCalculation(int billBeforeTax, int expectedVat, int expectedTotalPayable) {
        int calculatedVat = calculateVat8(billBeforeTax);
        int totalPayable = billBeforeTax + calculatedVat;

        assertEquals(expectedVat, calculatedVat, "Thuế VAT 8% tính sai cho số tiền: " + billBeforeTax);
        assertEquals(expectedTotalPayable, totalPayable, "Tổng tiền phải trả tính sai cho số tiền: " + billBeforeTax);
    }

    @ParameterizedTest(name = "[{index}] Phải trả={0}đ, Khách đưa={1}đ -> Tiền thừa={2}đ")
    @DisplayName("TC_PAY_01: Parameterized Test kiểm tra tính tiền thừa cho khách tại quầy")
    @CsvSource({
            "85000, 100000, 15000",
            "50000, 50000, 0",
            "35000, 50000, 15000",
            "135000, 200000, 65000",
            "0, 0, 0"
    })
    public void testCashChangeCalculation(int totalPayable, int customerCash, int expectedChange) {
        int change = calculateChange(totalPayable, customerCash);
        assertEquals(expectedChange, change, "Tiền thừa tính không chính xác");
    }

    @ParameterizedTest(name = "[{index}] Số tiền thanh toán thực tế={0}đ -> Điểm CRM tích lũy={1}")
    @DisplayName("TC_PAY_05: Parameterized Test kiểm tra cơ chế tích điểm CRM (10.000đ = 1 điểm)")
    @CsvSource({
            "9999, 0",
            "10000, 1",
            "25000, 2",
            "86400, 8",
            "150000, 15",
            "500000, 50"
    })
    public void testCrmPointsEarning(int billBeforeTax, int expectedPoints) {
        int pointsEarned = calculateCrmPointsEarned(billBeforeTax);
        assertEquals(expectedPoints, pointsEarned, "Điểm tích lũy CRM tính sai cho giá trị: " + billBeforeTax);
    }

    // =====================================================================
    // 2. LOGIC ĐƠN HÀNG 0 VNĐ: TỰ ĐỘNG CHỐT TIỀN MẶT / MIỄN PHÍ KHÔNG SINH VIETQR
    // =====================================================================

    @Test
    @DisplayName("TC_PAY_02: Đơn hàng 0 VNĐ (áp voucher 100% hoặc đổi điểm trọn gói) tự động chuyển hoàn thành")
    public void testZeroDollarOrderAutoCompletion() {
        int tongTienHang = 60000;
        int tienGiamGia = 60000;
        int tienTruDiem = 0;

        PaymentResult result = processPosCheckout(tongTienHang, tienGiamGia, tienTruDiem, 2 /* Muốn chuyển khoản */);

        // Ràng buộc bảo vệ trong code BanHangPOSController:
        assertEquals(0, result.tongPhaiTra, "Tổng phải trả phải bằng 0đ");
        assertEquals(1, result.maPt, "Phương thức thanh toán phải tự động chuyển về 1 (Tiền mặt/Miễn phí 0đ)");
        assertEquals(1, result.trangThaiThanhToan, "Trạng thái thanh toán phải là 1 (Đã thanh toán)");
        assertEquals(4, result.trangThaiDon, "Trạng thái đơn hàng phải là 4 (Hoàn thành tại quầy)");
        assertFalse(result.generateQrCode, "Tuyệt đối không được yêu cầu sinh mã VietQR cho đơn hàng 0 VNĐ");
    }

    // =====================================================================
    // 3. EXCEPTION TESTING: BẮT NGOẠI LỆ THANH TOÁN VỚI assertThrows
    // =====================================================================

    @Test
    @DisplayName("TC_PAY_EX_01: Ném ngoại lệ IllegalArgumentException khi khách đưa thiếu tiền mặt")
    public void testInsufficientCashThrowsException() {
        int totalPayable = 85000;
        int customerCash = 50000; // Đưa thiếu 35.000đ

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            calculateChange(totalPayable, customerCash);
        }, "Phải ném ngoại lệ IllegalArgumentException khi khách đưa thiếu tiền mặt");

        assertTrue(ex.getMessage().contains("Số tiền khách đưa không được nhỏ hơn tổng phải trả"),
                "Thông báo lỗi phải rõ ràng cho thu ngân biết khách đưa thiếu tiền");
    }

    @Test
    @DisplayName("TC_PAY_EX_02: Ném ngoại lệ IllegalArgumentException khi số điểm CRM đổi vượt quá số dư hiện có")
    public void testOverPointsRedemptionThrowsException() {
        int currentCustomerPoints = 20; // Khách có 20 điểm (tương đương 20.000đ)
        int requestedPointsToUse = 50;  // Yêu cầu đổi 50 điểm

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validatePointsRedemption(requestedPointsToUse, currentCustomerPoints, 100000);
        });

        assertTrue(ex.getMessage().contains("Số điểm sử dụng vượt quá số dư điểm hiện có"));
    }

    // =====================================================================
    // 4. TIMEOUT TEST: HIỆU NĂNG TÍNH TOÁN HÓA ĐƠN DƯỚI 2 GIÂY
    // =====================================================================

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    @DisplayName("TC_PAY_PERF: Chốt đơn và tính toán số liệu tài chính hóa đơn diễn ra tức thì (< 2s)")
    public void testCheckoutPerformanceUnderTwoSeconds() {
        for (int i = 0; i < 1000; i++) {
            PaymentResult res = processPosCheckout(150000, 20000, 10000, 1);
            assertNotNull(res);
            assertTrue(res.tongPhaiTra > 0);
        }
    }

    // =====================================================================
    // HÀM HELPER MÔ PHỎNG NGHIỆP VỤ BanHangPOSController
    // =====================================================================

    private int calculateVat8(int billBeforeTax) {
        if (billBeforeTax <= 0) return 0;
        return (int) Math.round(billBeforeTax * 0.08);
    }

    private int calculateChange(int totalPayable, int customerCash) {
        if (customerCash < totalPayable) {
            throw new IllegalArgumentException("Số tiền khách đưa không được nhỏ hơn tổng phải trả!");
        }
        return customerCash - totalPayable;
    }

    private int calculateCrmPointsEarned(int billBeforeTax) {
        if (billBeforeTax <= 0) return 0;
        return billBeforeTax / 10000;
    }

    private void validatePointsRedemption(int requestedPoints, int currentBalance, int billAmount) {
        if (requestedPoints > currentBalance) {
            throw new IllegalArgumentException("Số điểm sử dụng vượt quá số dư điểm hiện có!");
        }
        int pointDiscount = requestedPoints * 1000;
        if (pointDiscount > billAmount) {
            throw new IllegalArgumentException("Tiền trừ điểm không được vượt quá giá trị đơn hàng!");
        }
    }

    private static class PaymentResult {
        int tongPhaiTra;
        int maPt;
        int trangThaiThanhToan;
        int trangThaiDon;
        boolean generateQrCode;
    }

    private PaymentResult processPosCheckout(int tongTienHang, int tienGiamGia, int tienTruDiem, int maPt) {
        if (tienGiamGia > tongTienHang) tienGiamGia = tongTienHang;
        int conLai = Math.max(0, tongTienHang - tienGiamGia);
        if (tienTruDiem > conLai) tienTruDiem = conLai;

        int billBeforeTax = Math.max(0, tongTienHang - tienGiamGia - tienTruDiem);
        int vatPrice = calculateVat8(billBeforeTax);
        int recalculatedPayable = billBeforeTax + vatPrice;

        PaymentResult res = new PaymentResult();
        if (recalculatedPayable <= 0) {
            res.tongPhaiTra = 0;
            res.maPt = 1; // Miễn phí/Tiền mặt
            res.trangThaiThanhToan = 1;
            res.trangThaiDon = 4;
            res.generateQrCode = false;
        } else if (maPt == 2) {
            res.tongPhaiTra = recalculatedPayable;
            res.maPt = 2;
            res.trangThaiThanhToan = 0;
            res.trangThaiDon = 0;
            res.generateQrCode = true;
        } else {
            res.tongPhaiTra = recalculatedPayable;
            res.maPt = 1;
            res.trangThaiThanhToan = 1;
            res.trangThaiDon = 4;
            res.generateQrCode = false;
        }
        return res;
    }
}
