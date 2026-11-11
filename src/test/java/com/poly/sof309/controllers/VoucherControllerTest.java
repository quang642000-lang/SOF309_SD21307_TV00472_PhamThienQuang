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
 * [Y3] UNIT TEST CHO PHÂN HỆ KHUYẾN MÃI & VOUCHER GIẢM GIÁ (VOUCHER MODULE)
 * Dự án: TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE
 * Sinh viên: Phạm Thiện Quang - MSSV: TV00472 - Lớp: SD21307
 * Bám sát kịch bản: TC_VOUCHER_01 đến TC_VOUCHER_03 trong docs/TEST_CASES.md
 * =========================================================================
 */
public class VoucherControllerTest {

    // =====================================================================
    // 1. PARAMETERIZED TESTS: TÍNH TOÁN CHIẾT KHẤU THEO % VÀ TIỀN MẶT CỐ ĐỊNH
    // =====================================================================

    @ParameterizedTest(name = "[{index}] Đơn={0}đ, Loại={1}, Trị={2}, Max={3}, Min={4} -> Giảm thực tế={5}đ")
    @DisplayName("TC_VOUCHER_01 - 03: Parameterized Test kiểm tra thuật toán chiết khấu Voucher")
    @CsvSource({
            // tongDon, loaiGiam (1:tien, 2:%), giaTriGiam, giamToiDa, donToiThieu, expectedDiscount
            "100000, 1, 20000, 0, 80000, 20000",        // Giảm 20k tiền mặt, đơn 100k > min 80k -> giảm 20k
            "50000, 1, 20000, 0, 80000, 0",             // Giảm 20k nhưng đơn 50k < min 80k -> không giảm (0đ)
            "500000, 2, 20, 50000, 100000, 50000",      // Giảm 20% (100k) nhưng bị trần max 50k -> chốt 50k
            "150000, 2, 20, 50000, 100000, 30000",      // Giảm 20% của 150k = 30k < max 50k -> giảm 30k
            "200000, 2, 10, 0, 50000, 20000",           // Giảm 10% của 200k = 20k (không giới hạn trần) -> giảm 20k
            "30000, 1, 50000, 0, 0, 30000"              // Tiền giảm 50k > tổng đơn 30k -> chỉ giảm tối đa 30k (không âm)
    })
    public void testVoucherDiscountCalculation(int tongDonHang, int loaiGiam, int giaTriGiam,
                                              int giamToiDa, int donToiThieu, int expectedDiscount) {
        int actualDiscount = calculateVoucherDiscount(tongDonHang, loaiGiam, giaTriGiam, giamToiDa, donToiThieu);
        assertEquals(expectedDiscount, actualDiscount,
                "Số tiền giảm giá tính toán không khớp với quy tắc nghiệp vụ!");
    }

    // =====================================================================
    // 2. EXCEPTION TESTING: BẮT NGOẠI LỆ DỮ LIỆU VOUCHER VỚI assertThrows
    // =====================================================================

    @Test
    @DisplayName("TC_VOUCHER_EX_01: Ném ngoại lệ IllegalArgumentException khi tổng tiền đơn hàng bị âm")
    public void testNegativeOrderAmountThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            calculateVoucherDiscount(-50000, 1, 10000, 0, 0);
        });

        assertTrue(ex.getMessage().contains("Tổng tiền đơn hàng không được nhỏ hơn 0"),
                "Ngoại lệ phải cảnh báo số tiền đơn hàng âm");
    }

    @Test
    @DisplayName("TC_VOUCHER_EX_02: Ném ngoại lệ IllegalArgumentException khi phần trăm giảm giá vượt quá 100%")
    public void testInvalidPercentageDiscountThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            calculateVoucherDiscount(100000, 2, 120 /* 120% */, 0, 0);
        });

        assertEquals("Phần trăm giảm giá phải nằm trong khoảng từ 1% đến 100%!", ex.getMessage());
    }

    @Test
    @DisplayName("TC_VOUCHER_EX_03: Ném ngoại lệ IllegalArgumentException khi mã code voucher bị rỗng hoặc null")
    public void testEmptyVoucherCodeThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            validateVoucherCodeFormat("   ");
        });

        assertEquals("Mã voucher không được để trống!", ex.getMessage());
    }

    // =====================================================================
    // 3. TIMEOUT TEST: HIỆU NĂNG TÍNH TOÁN KHUYẾN MÃI DƯỚI 2 GIÂY
    // =====================================================================

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    @DisplayName("TC_VOUCHER_PERF: Xử lý 10.000 phép tính chiết khấu voucher siêu tốc dưới 2 giây")
    public void testVoucherCalculationPerformance() {
        for (int i = 0; i < 10000; i++) {
            int discount = calculateVoucherDiscount(250000, 2, 15, 30000, 100000);
            assertEquals(30000, discount);
        }
    }

    // =====================================================================
    // HÀM HELPER MÔ PHỎNG NGHIỆP VỤ IKhuyenMaiService.calculateDiscount()
    // =====================================================================

    private int calculateVoucherDiscount(int tongDonHang, int loaiGiam, int giaTriGiam, int giamToiDa, int donToiThieu) {
        if (tongDonHang < 0) {
            throw new IllegalArgumentException("Tổng tiền đơn hàng không được nhỏ hơn 0!");
        }
        if (loaiGiam == 2 && (giaTriGiam < 1 || giaTriGiam > 100)) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải nằm trong khoảng từ 1% đến 100%!");
        }
        if (tongDonHang < donToiThieu) {
            return 0; // Chưa đạt giá trị đơn tối thiểu
        }
        int discount = 0;
        if (loaiGiam == 1) {
            discount = giaTriGiam;
        } else if (loaiGiam == 2) {
            discount = (int) (tongDonHang * (giaTriGiam / 100.0));
            if (giamToiDa > 0 && discount > giamToiDa) {
                discount = giamToiDa;
            }
        }
        // Chốt chặn an toàn: Tiền giảm không bao giờ vượt quá tổng tiền hàng
        if (discount > tongDonHang) {
            discount = tongDonHang;
        }
        return discount;
    }

    private void validateVoucherCodeFormat(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã voucher không được để trống!");
        }
    }
}
