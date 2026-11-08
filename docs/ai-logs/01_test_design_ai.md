# MINH CHỨNG SỬ DỤNG AI: THIẾT KẾ TEST PLAN & TEST CASE BẰNG AI RAG (Y2 & Y6)
> **Môn học:** SOF309 - Kiểm thử nâng cao cùng AI (FPT Polytechnic)  
> **Sinh viên thực hiện:** Phạm Thiện Quang | **MSSV:** TV00472 | **Lớp:** SD21307  
> **Dự án:** TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE  
> **Giảng viên hướng dẫn:** ThS. Trần Quang Bình  
> **Công cụ AI:** Gemini / ChatGPT (RAG Context-Augmented Prompting)  
> **Thời gian thực hiện:** Tuần 1 (08/11/2026)  

---

## 1. Ngữ cảnh & Mục tiêu (RAG Context & Goal)
* **Mục tiêu:** Áp dụng phương pháp Retrieval-Augmented Generation (RAG) bằng cách nạp toàn bộ tài liệu đặc tả nghiệp vụ dự án thực tế **TEA POS** (`docs/SPECIFICATIONS.md`) vào mô hình ngôn ngữ lớn (LLM).
* **Nhiệm vụ của AI:** 
  1. Phân tích chuỗi quan hệ nguyên nhân - kết quả **Error $\rightarrow$ Defect $\rightarrow$ Failure** trong kịch bản xử lý nghiệp vụ bán lẻ và thanh toán quầy POS (bám sát lý thuyết Bài 1 Lab 1).
  2. Thiết kế Kế hoạch kiểm thử (**Test Plan**) hoàn chỉnh gồm 5 mục tiêu trọng tâm (đối chiếu tiêu chuẩn Verification vs Validation).
  3. Thiết kế Bộ kịch bản kiểm thử (**Test Cases**) chi tiết bao phủ các phân hệ trọng yếu: Xác thực & Chống Brute-Force, POS Cashier & Thanh toán VAT 8%, Quản trị Thực đơn đa kích cỡ (Product CRUD), Khuyến mãi Voucher & CRM, và Cổng đặt hàng Click & Collect.

---

## 2. Câu lệnh đã gửi cho AI (Prompt)

```text
Bạn là một QA Lead chuyên nghiệp với hơn 10 năm kinh nghiệm trong lĩnh vực Kiểm thử Phần mềm và Tự động hóa (Test Automation).
Dưới đây là tài liệu đặc tả nghiệp vụ chi tiết của dự án Web "TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE" của tôi:

========================= [BẮT ĐẦU ĐẶC TẢ SPECIFICATIONS.MD] =========================
- Hệ thống: TEA POS Enterprise & Portal O2O Click & Collect (Java 21, Jakarta EE 10, MS SQL Server 2022).
- Phân hệ Auth: Đăng nhập nội bộ Admin/Thu ngân (băm Salted SHA-256 + Pepper), cơ chế chống Brute-force nhập sai 5 lần liên tiếp sẽ tạm khóa tài khoản 5 phút (300 giây). Đăng ký khách hàng Portal kiểm tra SĐT 10 số, email hợp lệ, mật khẩu khớp và bắt buộc tick đồng ý điều khoản.
- Phân hệ POS & Payment:
  + Tùy biến ly nước: Size (S/M/L theo giá cấu hình trong SAN_PHAM_KICH_CO), Đường/Đá (0-100%), Topping.
  + Tiêu điểm CRM: 1 điểm = 1.000 VNĐ. Khấu trừ điểm không vượt quá tổng tiền sau voucher.
  + Thuế VAT: Tính chuẩn 8% trên số tiền sau giảm trừ (billBeforeTax = tongTienHang - tienGiamGia - tienTruDiem; vatPrice = Math.round(billBeforeTax * 0.08)).
  + Nghiệp vụ Đơn 0 VNĐ: Nếu tổng phải trả <= 0 (do voucher 100% hoặc đổi trọn điểm), hệ thống tự động gán maPt = 1 (Tiền mặt/Miễn phí), chuyển trangThaiThanhToan = 1, trangThaiDon = 4, tuyệt đối không sinh mã VietQR 0đ.
  + Tích điểm: Tự động cộng 1 điểm cho mỗi 10.000 VNĐ chi tiêu thực tế.
- Phân hệ CRUD Menu Sản phẩm:
  + Bảng SAN_PHAM không lưu giá bán đơn lẻ; giá bán cấu hình theo kích cỡ trong SAN_PHAM_KICH_CO. Tạo sản phẩm mới bắt buộc có tên, danh mục và tối thiểu 1 kích cỡ kèm giá bán.
  + Xóa sản phẩm: Nếu sản phẩm đã phát sinh trong CHI_TIET_DON_HANG thì Soft Delete (trang_thai = 0); nếu chưa từng có hóa đơn thì Hard Delete (xóa khỏi DB).
- Phân hệ Click & Collect Portal:
  + Khách bắt buộc hẹn giờ đến nhận nước; thời gian hẹn phải cách thời điểm hiện tại tối thiểu 15 phút.
  + Chống spam Double Checkout bằng cách Invalidate Token UUID trong session.
========================= [KẾT THÚC ĐẶC TẢ SPECIFICATIONS.MD] =========================

Dựa vào ngữ cảnh trên, hãy thực hiện:
1. Phân tích bài toán Lab 1: Mối quan hệ giữa Error -> Defect -> Failure trong trường hợp khách hàng áp dụng voucher giảm giá 100% dẫn đến tổng tiền đơn hàng phải trả bằng 0 VNĐ.
2. Xây dựng Bảng Kế hoạch kiểm thử (Test Plan) gồm 5 mục tiêu cốt lõi, phân loại Verification vs Validation.
3. Thiết kế bảng Test Cases chuẩn hóa (gồm Positive và Negative, áp dụng kỹ thuật Phân vùng tương đương EP và Phân tích giá trị biên BVA).
```

---

## 3. Kết quả do AI sinh ra (Raw AI Output)

AI đã tiếp nhận tài liệu đặc tả và phản hồi chi tiết:

1. **Phân tích bản chất lỗi theo lý thuyết Lab 1 (Error $\rightarrow$ Defect $\rightarrow$ Failure):**
   * **Error (Sai sót con người / Human mistake):** Lập trình viên backend khi xây dựng hàm xử lý thanh toán `performCheckout()` đã mặc định rằng mọi hóa đơn đều có giá trị lớn hơn 0 và luôn chuyển luồng thanh toán sang API sinh mã chuyển khoản VietQR nếu khách chọn chuyển khoản, mà quên kiểm tra trường hợp đặc biệt khi tổng tiền sau giảm giá về 0 VNĐ (`tongPhaiTra <= 0`).
   * **Defect (Khuyết tật mã nguồn / Bug trong code):** Thiếu khối lệnh rẽ nhánh bảo vệ:
     ```java
     if (recalculatedPayable <= 0 || tongPhaiTra <= 0) {
         tongPhaiTra = 0;
         maPt = 1; // Miễn phí / Tiền mặt
         dh.setTrangThaiThanhToan(1);
         dh.setTrangThaiDon(4);
     }
     ```
     Dẫn đến mã nguồn cố gắng gọi cổng thanh toán VietQR với số tiền `amount = 0`, gây lỗi HTTP 400 hoặc `IllegalArgumentException` từ API ngân hàng.
   * **Failure (Sự cố vận hành thực tế):** Tại quầy thu ngân POS, khi khách hàng VIP đổi voucher miễn phí ly trà sữa 0đ, màn hình thu ngân bị treo ở trạng thái "Đang kết nối cổng thanh toán...", không in được hóa đơn và không chốt được đơn hàng cho khách.

2. **Thiết kế Test Plan & Test Cases:**
   * AI đã sinh ra khung Kế hoạch kiểm thử với 5 mục tiêu theo chuẩn ISTQB: Bảo vệ đăng nhập & chống Brute-Force (Verification), Xử lý tài chính và VAT 8% (Verification), Toàn vẹn dữ liệu Menu đa kích cỡ (Verification), Trải nghiệm giao diện đặt hàng UI (Validation), Tự động hóa CI/CD hồi quy (Verification).
   * AI xây dựng bộ kịch bản kiểm thử phân loại Positive/Negative, bao phủ các trường hợp biên.

---

## 4. Chỉnh sửa và Tinh chỉnh của Sinh viên (Human-in-the-Loop Refinement)

Mặc dù AI phân tích logic rất nhanh, sinh viên đã rà soát lại trực tiếp với mã nguồn Java trong dự án và thực hiện các điều chỉnh quan trọng:

* **Những điểm AI còn thiếu hoặc chưa chuẩn ban đầu:**
  1. Ban đầu AI giả định bảng `SAN_PHAM` có cột `giaBan` đơn lẻ. Sinh viên đã đính chính: Hệ thống Tea POS quản lý giá theo biến thể kích cỡ trong bảng `SAN_PHAM_KICH_CO`, do đó điều kiện kiểm tra khi tạo món phải là `selectedSizes` không được rỗng (`TC_PROD_02`).
  2. Ở cơ chế xóa sản phẩm, AI chỉ đề xuất xóa thông thường. Sinh viên bổ sung kịch bản kiểm tra rẽ nhánh Soft Delete (sản phẩm đã có hóa đơn lịch sử) vs Hard Delete (sản phẩm chưa từng bán) để đảm bảo toàn vẹn dữ liệu kế toán (`TC_PROD_04`, `TC_PROD_05`).
  3. Ở phân hệ bảo mật, sinh viên bổ sung ca kiểm thử thực tế về cơ chế chống Brute-Force khóa tài khoản 300 giây khi sai mật khẩu liên tiếp 5 lần (`TC_AUTH_05`).
  4. Ở phân hệ Click & Collect, sinh viên chuẩn hóa kịch bản kiểm thử biên thời gian hẹn lấy nước: < 15 phút (Báo lỗi) vs $\ge$ 15 phút (Hợp lệ) và kiểm thử cơ chế chống trùng đơn bằng Token UUID (`TC_CART_01` $\rightarrow$ `TC_CART_04`).
* **Kết quả nghiệm thu:** Toàn bộ tài liệu chuẩn hóa đã được cập nhật đồng bộ vào `docs/TEST_PLAN.md` và `docs/TEST_CASES.md`.
