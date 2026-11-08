# MINH CHỨNG SỬ DỤNG AI: THIẾT KẾ TEST PLAN & TEST CASE BẰNG AI RAG (Y2 & Y6)
> **Môn học:** SOF309 - Kiểm thử nâng cao cùng AI (FPT Polytechnic)  
> **Sinh viên thực hiện:** Phạm Thiện Quang | **MSSV:** TV00472 | **Lớp:** SD21307  
> **Dự án:** TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE  
> **Công cụ AI:** Gemini 1.5 Pro / ChatGPT-4o (RAG Context-Augmented Prompting)  
> **Thời gian thực hiện:** Tuần 1 (08/11/2026)  

---

## 1. Ngữ cảnh & Mục tiêu (RAG Context & Goal)
* **Mục tiêu:** Áp dụng phương pháp Retrieval-Augmented Generation (RAG) bằng cách nạp toàn bộ đặc tả nghiệp vụ dự án riêng **TEA POS** (`docs/SPECIFICATIONS.md`) vào mô hình ngôn ngữ lớn (LLM).
* **Nhiệm vụ của AI:** 
  1. Phân tích chuỗi nguyên nhân - kết quả **Error -> Defect -> Failure** trong kịch bản xử lý giao dịch thanh toán quầy POS & cổng online (bám sát lý thuyết Bài 1 Lab 1).
  2. Thiết kế Kế hoạch kiểm thử (**Test Plan**) hoàn chỉnh và Bộ kịch bản kiểm thử (**Test Cases**) chi tiết cho các phân hệ: Authentication, POS Cashier & Payment, Menu Sản phẩm CRUD, Khuyến mãi & Voucher, và Click & Collect.
  3. Phân định rõ ràng mục tiêu **Verification** (Xác minh code đáp ứng đặc tả) và **Validation** (Xác nhận sản phẩm đáp ứng trải nghiệm người dùng thực tế).

---

## 2. Câu lệnh đã gửi cho AI (Prompt)

```text
Bạn là một QA Lead chuyên nghiệp với hơn 10 năm kinh nghiệm trong lĩnh vực Kiểm thử Phần mềm và Tự động hóa (Test Automation).
Dưới đây là tài liệu đặc tả nghiệp vụ chi tiết của dự án Web "TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE" của tôi:

========================= [BẮT ĐẦU ĐẶC TẢ SPECIFICATIONS.MD] =========================
- Hệ thống: TEA POS Enterprise & Portal O2O Click & Collect System (Java 21, Jakarta EE 10, MS SQL Server 2022).
- Phân hệ Auth: Đăng nhập Admin/Thu ngân (băm Salted SHA-256), Đăng ký thành viên qua SĐT (10 số, không trùng), OTP 6 số.
- Phân hệ POS & Payment: Tùy biến ly nước (Size S/M/L, Đường 0-100%, Đá 0-100%, Topping), Hạn mức giao dịch tiêu chuẩn 1.000.000 VNĐ. Giao dịch > 1.000.000 VNĐ bắt buộc có OTP xác thực của quản lý. Đơn hàng 0 VNĐ tự động PAID không sinh mã VietQR. Tính tiền thừa chính xác.
- Phân hệ CRUD Menu: Thêm món mới giá > 0đ, tên món >= 3 ký tự không trùng; Topping giá >= 0đ; Ngừng kinh doanh (Soft Delete).
- Phân hệ Voucher: Giảm theo % (có trần giảm tối đa), giảm tiền mặt, ràng buộc đơn tối thiểu.
- Phân hệ Portal Click & Collect: Hẹn giờ lấy nước trước tối thiểu 15 phút, đồng bộ trạng thái đơn sang quầy POS.
========================= [KẾT THÚC ĐẶC TẢ SPECIFICATIONS.MD] =========================

Dựa vào ngữ cảnh trên, hãy thực hiện:
1. Phân tích bài toán Lab 1: Mối quan hệ giữa Error -> Defect -> Failure trong trường hợp nhân viên POS thanh toán đơn hàng tiệc trà sữa có giá trị 1.500.000 VNĐ (> 1.000.000 VNĐ).
2. Xây dựng Bảng Kế hoạch kiểm thử (Test Plan) gồm 5 mục tiêu trọng tâm (đối chiếu Verification vs Validation).
3. Thiết kế bảng Test Cases chuẩn hóa (gồm Positive và Negative, áp dụng Phân vùng tương đương và Phân tích giá trị biên BVA).
```

---

## 3. Kết quả do AI sinh ra (Raw AI Output)

AI đã tiếp nhận tài liệu đặc tả và phản hồi xuất sắc:

1. **Phân tích bản chất lỗi theo lý thuyết Lab 1:**
   * **Error (Sai sót con người / Human mistake):** Lập trình viên backend khi hiện thực hóa hàm xử lý thanh toán `processStandardPayment()` đã quên bổ sung điều kiện rẽ nhánh kiểm tra hạn mức an toàn đối với các hóa đơn giá trị lớn vượt ngưỡng 1.000.000 VNĐ.
   * **Defect (Khuyết tật mã nguồn / Bug):** Mã nguồn thiếu đoạn code kiểm tra `if (order.getTotalAmount() > 1_000_000 && order.getOtp() == null)`, dẫn đến việc hệ thống không yêu cầu nhập OTP hoặc ném ngoại lệ không kiểm soát (`NullPointerException` / crash giao dịch).
   * **Failure (Sự cố vận hành thực tế):** Tại quầy thu ngân, khi khách hàng đặt đơn tiệc 1.500.000 VNĐ, hệ thống POS bị treo hoặc dừng đột ngột, giao dịch không được ghi nhận, gây ách tắc hàng đợi của khách.

2. **Thiết kế Test Plan & Test Cases:**
   * AI đã sinh ra khung Test Plan phân định rạch ròi 5 mục tiêu theo tiêu chuẩn ISTQB: Bảo vệ đăng nhập (Verification), An toàn dòng tiền (Verification), Toàn vẹn CRUD (Verification), Trải nghiệm người dùng UI (Validation), Tích hợp CI/CD tự động (Verification).
   * AI sinh ra 20 ca kiểm thử bao quát các module trọng yếu.

---

## 4. Chỉnh sửa và Tinh chỉnh của Sinh viên (Human-in-the-Loop Refinement)

Mặc dù AI sinh kết quả rất tốt, sinh viên đã rà soát và bổ sung các điểm nghiệp vụ thực tế còn thiếu:

* **Điểm AI còn thiếu ban đầu:**
  1. Ban đầu AI chỉ tạo Test Case thanh toán giá trị dương, chưa bao quát ca kiểm thử giá bán sản phẩm âm hoặc bằng 0đ (`TC_PROD_02`).
  2. Ở phân hệ Voucher, AI chưa xử lý trường hợp giá trị giảm theo `%` vượt quá trần giảm tối đa (`TC_VOUCHER_03`).
  3. Ở phân hệ Click & Collect, AI chưa đưa vào kịch bản kiểm tra biên thời gian hẹn lấy nước (phải cách thời điểm đặt tối thiểu 15 phút - `TC_CART_03`).
* **Hoàn thiện của Sinh viên:**
  * Bổ sung đầy đủ các ca kiểm thử biên (Boundary Value Analysis): `TC_PAY_04` (Đơn 0đ), `TC_PAY_05` (Tiền âm), `TC_PROD_03` (Tên món < 3 ký tự), `TC_CART_03` (Hẹn lấy < 15 phút).
  * Chuẩn hóa bảng mã Test Case ID theo format môn học: `TC_AUTH_xx`, `TC_PAY_xx`, `TC_PROD_xx`, `TC_VOUCHER_xx`, `TC_CART_xx`.
  * Lưu kết quả hoàn thiện vào `docs/TEST_PLAN.md` và `docs/TEST_CASES.md`.
