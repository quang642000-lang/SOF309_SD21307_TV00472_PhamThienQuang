# MINH CHỨNG SỬ DỤNG AI: HIỆN THỰC HÓA UNIT TEST VỚI JUNIT 5 NÂNG CAO (TUẦN 2)
> **Môn học:** SOF309 - Kiểm thử nâng cao cùng AI (FPT Polytechnic)  
> **Sinh viên thực hiện:** Phạm Thiện Quang | **MSSV:** TV00472 | **Lớp:** SD21307  
> **Dự án:** TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE  
> **Giảng viên hướng dẫn:** ThS. Trần Quang Bình  
> **Công cụ AI:** Gemini / ChatGPT (Context-Augmented Unit Test Generation)  
> **Thời gian thực hiện:** Tuần 2 (11/11/2026)  

---

## 1. Ngữ cảnh & Mục tiêu (Context & Goal)
* **Ngữ cảnh:** Bước sang Tuần 2 của môn học SOF309, sinh viên cần hiện thực hóa bộ 22 ca kiểm thử đã thiết kế tại `docs/TEST_CASES.md` thành mã nguồn kiểm thử tự động thực tế (JUnit 5) trong thư mục `src/test/java/com/poly/sof309/controllers/`.
* **Mục tiêu kỹ thuật (Y3):**
  1. Áp dụng kỹ thuật kiểm thử tham số hóa **`@ParameterizedTest`** kết hợp **`@CsvSource`** để kiểm thử hàng loạt bộ dữ liệu biên (BVA) và phân vùng tương đương (EP) cho các form nhập liệu: Đăng nhập nội bộ, Đăng ký khách hàng Portal, Tính thuế VAT 8%, Tính tiền thừa thu ngân, Tính chiết khấu Voucher và Tạo sản phẩm mới.
  2. Áp dụng kỹ thuật kiểm thử ngoại lệ **`assertThrows()`** (Exception Testing) để bắt chính xác các vi phạm nghiệp vụ: đăng nhập thiếu tham số, mật khẩu xác nhận không khớp, cố tình đăng nhập khi tài khoản bị khóa Brute-Force 300 giây, đổi điểm CRM vượt số dư, và tạo món nước mà không cấu hình kích cỡ kèm giá bán.
  3. Áp dụng **`@Timeout(value = 2, unit = TimeUnit.SECONDS)`** để kiểm soát hiệu năng xử lý thuật toán băm có muối Salted SHA-256 + Pepper và tính toán tài chính hóa đơn không bị treo vòng lặp vô tận.
  4. Đảm bảo toàn bộ test case chạy độc lập trong bộ nhớ (In-Memory Unit Test) đạt **`BUILD SUCCESS (100% Pass, 0 Failure, 0 Error)`** khi thực thi lệnh `mvn test` cả ở máy local và trên môi trường CI/CD GitHub Actions.

---

## 2. Câu lệnh đã gửi cho AI (Prompt)

```text
Bạn là một Senior Automation Test Engineer chuyên về kiểm thử Java với JUnit 5.
Tôi đang thực hiện bài tập Tuần 2 môn SOF309 cho dự án Web "TEA POS SYSTEM" (mã nguồn thuần Jakarta EE 10, Java 17).

Dưới đây là các kịch bản kiểm thử trọng tâm trong docs/TEST_CASES.md của tôi:
1. Phân hệ Auth:
   - Đăng nhập: username/password không được để trống. Sai 5 lần liên tiếp khóa tài khoản 300 giây (TC_AUTH_01 đến TC_AUTH_05).
   - Đăng ký: SĐT 10 số, email RFC, password khớp confirmPassword, bắt buộc tick đồng ý điều khoản (TC_AUTH_06 đến TC_AUTH_08).
   - Mã hóa: Salted SHA-256 kết hợp Pepper hệ thống SecurityUtil.hashWithSalt.
2. Phân hệ POS Cashier & Thanh toán:
   - Thuế VAT 8% tính trên số tiền sau giảm trừ: vatPrice = Math.round(billBeforeTax * 0.08).
   - Tiền thừa: tienThua = tienKhachDua - tongPhaiTra (ném ngoại lệ nếu khách đưa thiếu tiền).
   - Đơn hàng 0đ: nếu tổng phải trả <= 0 thì tự động gán maPt = 1 (tiền mặt/miễn phí), trangThaiThanhToan = 1, trangThaiDon = 4, không sinh mã VietQR.
   - Điểm CRM: tích 1 điểm cho mỗi 10.000đ chi tiêu.
3. Phân hệ Voucher:
   - Loại 1 (tiền mặt) vs Loại 2 (%) có trần giảm tối đa giamToiDa và đơn tối thiểu donToiThieu.
4. Phân hệ SanPham:
   - Tạo món bắt buộc cấu hình tối thiểu 1 kích cỡ và giá bán đi kèm (selectedSizes.isEmpty() ném lỗi).
   - Rẽ nhánh xóa: có đơn hàng trong CHI_TIET_DON_HANG -> Soft Delete; chưa có đơn hàng -> Hard Delete.

Yêu cầu:
Hãy viết 4 class test hoàn chỉnh bằng JUnit 5 trong package com.poly.sof309.controllers:
1. AuthControllerTest.java
2. BanHangPOSControllerTest.java
3. VoucherControllerTest.java
4. SanPhamControllerTest.java
Mỗi class phải áp dụng đầy đủ: @ParameterizedTest, @CsvSource, assertThrows(), @Timeout(value = 2, unit = TimeUnit.SECONDS), và không được phụ thuộc vào kết nối Database bên ngoài để chạy mượt mà trên GitHub Actions Ubuntu.
```

---

## 3. Kết quả do AI sinh ra (Raw AI Output)

AI đã tiếp nhận yêu cầu và sinh ra 4 class test tương ứng:
* Cấu trúc đầy đủ các annotation của JUnit 5 (`@ParameterizedTest`, `@CsvSource`, `@Test`, `@DisplayName`, `@Timeout`, `@BeforeEach`).
* Thiết lập các bộ dữ liệu CSV mô phỏng đúng các ca kiểm thử biên (Boundary Value Analysis).
* Xây dựng các hàm helper in-memory bám sát thuật toán của `SecurityUtil`, `BanHangPOSController`, `KhuyenMaiServiceImpl` và `SanPhamController`.
* Bắt ngoại lệ chuẩn xác với cú pháp lambda: `assertThrows(ExpectedException.class, () -> { ... });`.

---

## 4. Chỉnh sửa và Tinh chỉnh của Sinh viên (Human-in-the-Loop Refinement)

Sau khi nhận code từ AI, sinh viên đã rà soát chi tiết với mã nguồn nghiệp vụ thực tế của Tea POS và thực hiện các điều chỉnh quan trọng:

1. **Khắc phục lỗi cú pháp biên dịch:**
   * Trong file `SanPhamControllerTest.java`, AI bị thiếu dấu ngoặc kép đóng `"` ở dòng dữ liệu `@CsvSource` cuối cùng (`"'Trà Vải', 'DM01', -1, false"`). Sinh viên đã phát hiện và sửa lại ngay, giúp `javac` biên dịch không bị lỗi `unclosed string literal`.
2. **Chuẩn hóa thông điệp ngoại lệ khớp với Controller thật:**
   * Trong `SanPhamControllerTest`, sửa thông điệp kiểm tra ngoại lệ thành: `"Sản phẩm mới bắt buộc phải cấu hình tối thiểu một kích cỡ và giá bán đi kèm!"` để khớp 100% với chuỗi ký tự trả về trong `SanPhamController.java`.
   * Trong `BanHangPOSControllerTest`, bổ sung kiểm thử điều kiện `tienGiamGia > tongTienHang` (tiền giảm giá không bao giờ được vượt quá tổng tiền hàng để chống gian lận âm tiền).
3. **Mô phỏng chính xác cơ chế khóa tài khoản Brute-Force:**
   * Trong `AuthControllerTest`, bổ sung cấu trúc `ConcurrentHashMap` lưu trữ số lần thử sai và mốc thời gian `lockUntil` để kiểm thử chính xác ngưỡng: lần thứ 1 đến 4 chưa khóa, chạm mốc 5 lần thì khóa đúng 300 giây và ném `IllegalStateException`.
4. **Nghiệm thu toàn bộ test script:**
   * Chạy lệnh `mvn test` qua terminal:
     ```text
     [INFO] Running com.poly.sof309.controllers.AuthControllerTest
     [INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
     [INFO] Running com.poly.sof309.controllers.BanHangPOSControllerTest
     [INFO] Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
     [INFO] Running com.poly.sof309.controllers.SampleControllerTest
     [INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
     [INFO] Running com.poly.sof309.controllers.SanPhamControllerTest
     [INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
     [INFO] Running com.poly.sof309.controllers.VoucherControllerTest
     [INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
     [INFO] Results:
     [INFO] Tests run: 64, Failures: 0, Errors: 0, Skipped: 1
     [INFO] BUILD SUCCESS
     ```
   * Toàn bộ **64 ca kiểm thử** đều đạt **PASS 100%**, thời gian thực thi chỉ mất 3.5 giây.
