# KẾ HOẠCH KIỂM THỬ (TEST PLAN)
### Dự án: TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE
> **Môn học:** SOF309 - Kiểm thử nâng cao cùng AI (FPT Polytechnic)  
> **Sinh viên:** Phạm Thiện Quang | **MSSV:** TV00472 | **Lớp:** SD21307  
> **Giảng viên hướng dẫn:** ThS. Trần Quang Bình  
> **Tham chiếu lý thuyết:** Slide 1 (Kiểm thử đơn vị) & Lab 1 (Tổng quan kiểm thử phần mềm)  

---

## 1. MỤC TIÊU KIỂM THỬ (TEST OBJECTIVES - BÀI 2 LAB 1)

Dựa trên 5 mục tiêu kiểm thử cốt lõi trong ngữ cảnh hệ thống bán lẻ và cổng đặt nước Tea POS:

| STT | Mục tiêu kiểm thử | Tầm quan trọng trong thực tế | Hoạt động kiểm thử cụ thể | Phân loại |
| :---: | :--- | :--- | :--- | :---: |
| **1** | **Xác thực an toàn và phòng chống tấn công dò mật khẩu (Auth & Brute-Force)** | Ngăn chặn các cuộc tấn công Brute-force, chiếm quyền trái phép vào màn hình POS và rò rỉ dữ liệu khách hàng. | Viết Unit Test kiểm thử cơ chế băm mật khẩu Salted SHA-256 + Pepper, kiểm thử khóa tài khoản 5 phút (300 giây) khi nhập sai 5 lần liên tiếp, và bắt lỗi trùng SĐT/Email. | **Verification** |
| **2** | **Đảm bảo tính chính xác tuyệt đối trong Xử lý Hóa đơn & Thuế VAT (POS Cashier)** | Tránh thất thoát tài chính, sai lệch sổ sách kế toán, sai tiền thừa và lỗi xử lý đơn hàng miễn phí. | Viết Unit Test kiểm thử công thức tính tổng tiền hàng, khấu trừ Voucher, đổi điểm CRM, tính thuế VAT 8% và cơ chế tự động hoàn tất đơn 0 VNĐ không sinh mã VietQR. | **Verification** |
| **3** | **Đảm bảo tính toàn vẹn dữ liệu Sản phẩm đa kích cỡ & Lịch sử bán lẻ (Menu CRUD)** | Ngăn chặn việc nhân viên tạo món nước thiếu giá bán hoặc xóa mất dữ liệu lịch sử trên hóa đơn cũ. | Viết Unit Test kiểm thử ràng buộc tạo món phải có ít nhất 1 size cấu hình kèm giá, và kiểm tra cơ chế phân nhánh: Soft Delete nếu đã có hóa đơn, Hard Delete nếu chưa từng bán. | **Verification** |
| **4** | **Kiểm thử trải nghiệm người dùng tự động trên giao diện Web (UI Test)** | Đảm bảo khách hàng đặt nước online và thu ngân tại quầy thao tác trơn tru, không gặp lỗi giật lag hay vỡ layout. | Xây dựng bộ kiểm thử Selenium WebDriver theo mô hình Page Object Model (POM) tự động tương tác trình duyệt Chrome. | **Validation** |
| **5** | **Tự động hóa hồi quy và Tích hợp liên tục (CI/CD Regression Testing)** | Đảm bảo mỗi khi lập trình viên cập nhật mã nguồn mới lên GitHub, toàn bộ hệ thống test cũ không bị gãy (Break). | Thiết lập GitHub Actions Pipeline (`.github/workflows/test.yml`) chạy tự động `mvn test` trên môi trường Ubuntu Headless. | **Verification** |

---

## 2. PHÂN BIỆT KIỂM THỬ VÀ GỠ LỖI (TESTING VS DEBUGGING - BÀI 1 LAB 1)

* **Kiểm thử (Testing):** Là hoạt động thiết kế và thực thi có chủ đích các kịch bản kiểm thử (Test Scripts trên JUnit 5 & Selenium) nhằm phát hiện ra các sai sót (Defects) và ghi nhận lỗi hệ thống (Failures) khi hành vi thực tế khác biệt so với đặc tả nghiệp vụ (`SPECIFICATIONS.md`).
* **Gỡ lỗi (Debugging):** Là hoạt động chuyên biệt của lập trình viên sau khi nhận được báo cáo thất bại từ Tester/CI: Phân tích Stack Trace $\rightarrow$ xác định nguyên nhân gốc rễ (Root Cause) trong mã nguồn (Service/Controller) $\rightarrow$ sửa lỗi và tối ưu code $\rightarrow$ thực thi lại bộ Test để xác nhận lỗi đã được khắc phục hoàn toàn.

---

## 3. PHẠM VI KIỂM THỬ (TEST SCOPE)

### 3.1. Trong phạm vi kiểm thử (In-Scope)
* **Phân hệ Xác thực & Phân quyền (Authentication & Authorization):**
  * Đăng nhập Admin / Thu ngân với thông tin hợp lệ / không hợp lệ.
  * Cơ chế khóa tài khoản 300 giây khi đăng nhập sai 5 lần liên tiếp.
  * Đăng ký tài khoản Khách hàng: Kiểm tra định dạng SĐT (10 số), mật khẩu xác nhận khớp, bắt buộc đồng ý điều khoản, kiểm tra trùng lặp SĐT/Email.
* **Phân hệ POS & Xử lý Giao dịch (POS & Payment):**
  * Tùy biến ly nước: Size (S/M/L), Đường (0-100%), Đá (0-100%), Topping.
  * Giảm giá Voucher (theo % có trần giảm tối đa và theo tiền mặt) & Tiêu điểm CRM (1 điểm = 1.000 VNĐ).
  * Công thức tính thuế VAT 8% trên số tiền sau giảm trừ.
  * Xử lý tự động đơn hàng 0 VNĐ (chuyển Tiền mặt/Miễn phí, không sinh mã VietQR).
  * Thanh toán tiền mặt (tính tiền thừa), thanh toán VietQR động.
* **Phân hệ Quản trị CRUD (Admin Products & Toppings):**
  * Thêm món nước: Ràng buộc bắt buộc tên, danh mục và tối thiểu 1 kích cỡ có giá bán đi kèm.
  * Xóa món nước: Rẽ nhánh Soft Delete (nếu đã có đơn hàng) vs Hard Delete (nếu chưa có đơn hàng).
* **Phân hệ Đặt hàng Online (Portal Click & Collect):**
  * Bắt buộc hẹn giờ lấy nước sau thời điểm hiện tại tối thiểu 15 phút.
  * Chống Double Checkout với Token UUID.
  * Xử lý luồng Mua ngay (Buy Now Session).
* **Kiểm thử tự động giao diện (Selenium UI Automation):**
  * Kịch bản đăng nhập, tìm kiếm món nước và thêm món vào giỏ hàng trên giao diện Web.

### 3.2. Ngoài phạm vi kiểm thử (Out-of-Scope)
* Kiểm thử chịu tải cao (Load/Stress Testing hàng triệu request cùng lúc).
* Kiểm thử thâm nhập an ninh mạng chuyên sâu (Penetration Testing tầng mạng hạ tầng).

---

## 4. CHIẾN LƯỢC VÀ KỸ THUẬT KIỂM THỬ (TEST STRATEGY)

1. **Kỹ thuật Hộp đen (Black-box Testing):**
   * **Phân vùng tương đương (Equivalence Partitioning - EP):** Chia trường dữ liệu (SĐT, Mật khẩu, Giá bán kích cỡ, % Giảm giá, Thời gian hẹn lấy) thành các vùng dữ liệu hợp lệ và không hợp lệ.
   * **Phân tích giá trị biên (Boundary Value Analysis - BVA):** Kiểm tra kỹ các giá trị tại ngưỡng biên:
     * Số lần đăng nhập sai: 4 lần (chưa khóa) vs 5 lần (khóa 300s).
     * Mức giảm tối đa Voucher: Giảm 20% trên đơn 500.000đ trần 50.000đ $\rightarrow$ chốt giảm đúng 50.000đ.
     * Số tiền thanh toán: Đơn hàng 0 VNĐ (miễn phí) vs 1.000 VNĐ.
     * Thời gian hẹn lấy nước: Cách thời điểm hiện tại 14 phút (Báo lỗi) vs 15 phút (Hợp lệ).
2. **Chiến lược Kiểm thử Đơn vị (Unit Test với JUnit 5 - Slide 1):**
   * Độc lập (Isolation): Mỗi ca test là một phương thức riêng biệt, độc lập nhờ `@BeforeEach`.
   * Sử dụng `@DisplayName` tiếng Việt rõ ràng, bám sát mã Test Case (ví dụ `TC_AUTH_01`, `TC_PAY_01`).
   * Sử dụng đầy đủ Assertions chuẩn: `assertEquals`, `assertTrue`, `assertFalse`, `assertNotNull`, `assertThrows` để kiểm thử ngoại lệ bắt lỗi nghiệp vụ.
3. **Chiến lược Kiểm thử Giao diện (Selenium WebDriver POM - Tuần 3 & 4):**
   * Áp dụng Page Object Model (POM): Tách biệt Class quản lý phần tử giao diện (`LoginPage.java`) và Class kịch bản test (`LoginTest.java`).
   * Sử dụng `WebDriverWait` (Explicit Wait) xử lý tải DOM bất đồng bộ, loại bỏ hoàn toàn `Thread.sleep()`.
4. **Chiến lược Tự động hóa CI/CD (GitHub Actions - Tuần 5):**
   * Tự động trigger pipeline kiểm thử mỗi khi có sự kiện `push` hoặc `pull_request` lên nhánh `main`.
   * Chạy trên máy ảo `ubuntu-latest`, Chrome Headless mode (`--headless=new`).

---

## 5. TIÊU CHÍ BẮT ĐẦU VÀ KẾT THÚC (ENTRY & EXIT CRITERIA)

* **Tiêu chí bắt đầu (Entry Criteria):**
  * Mã nguồn dự án Web Tea POS đã được tích hợp vào `src/main/` và biên dịch thành công.
  * Tài liệu đặc tả `SPECIFICATIONS.md` đã được hoàn thiện.
* **Tiêu chí kết thúc (Exit Criteria):**
  * 100% các ca Unit Test thực thi và đạt kết quả **PASS** (Không có Error hay Failure).
  * Quy trình GitHub Actions báo trạng thái **SUCCESS (Xanh lá cây)**.
  * Hồ sơ minh chứng tương tác AI (`docs/ai-logs/`) được ghi chép đầy đủ.

---

## 6. MÔI TRƯỜNG & CÔNG CỤ KIỂM THỬ

* **Hệ điều hành:** Windows 11 / Ubuntu (GitHub Actions Runner).
* **Môi trường phát triển:** IntelliJ IDEA 2026 / VS Code, Java 21 LTS / Java 17.
* **Trình duyệt kiểm thử:** Google Chrome (Phiên bản mới nhất) & Headless Chrome.
* **Quản lý mã nguồn & CI/CD:** Git, GitHub Repository, GitHub Actions.
