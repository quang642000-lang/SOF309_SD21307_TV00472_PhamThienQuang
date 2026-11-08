# KẾ HOẠCH KIỂM THỬ (TEST PLAN)
### Dự án: TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE
> **Môn học:** SOF309 - Kiểm thử nâng cao cùng AI (FPT Polytechnic)  
> **Sinh viên:** Phạm Thiện Quang | **MSSV:** TV00472 | **Lớp:** SD21307  
> **Giảng viên hướng dẫn:** ThS. Trần Quang Bình (binhtq7 - Thầy Mười Giáo)  
> **Tham chiếu lý thuyết:** Slide 1 (Kiểm thử đơn vị) & Lab 1 (Tổng quan kiểm thử phần mềm)  

---

## 1. MỤC TIÊU KIỂM THỬ (TEST OBJECTIVES - BÀI 2 LAB 1)

Dựa trên 5 mục tiêu kiểm thử cốt lõi trong ngữ cảnh hệ thống bán lẻ và cổng đặt nước Tea POS:

| STT | Mục tiêu kiểm thử | Tầm quan trọng trong thực tế | Hoạt động kiểm thử cụ thể | Phân loại |
| :---: | :--- | :--- | :--- | :---: |
| **1** | **Xác thực an toàn và tính toàn vẹn của Phân hệ Xác thực (Auth)** | Ngăn chặn các cuộc tấn công Brute-force, chiếm quyền trái phép vào màn hình POS và rò rỉ dữ liệu khách hàng. | Viết Unit Test kiểm tra thuật toán băm Salted SHA-256, độ mạnh mật khẩu và bắt lỗi trùng lặp số điện thoại. | **Verification** |
| **2** | **Đảm bảo tính chính xác tuyệt đối trong Xử lý Hóa đơn & Thanh toán** | Tránh thất thoát tài chính, sai lệch sổ sách kế toán, sai tiền thừa và lỗi xử lý đơn hàng giá trị cao. | Viết Unit Test & Integration Test kiểm thử hạn mức giao dịch > 1.000.000 VNĐ, xử lý đơn 0 VNĐ và xác thực OTP an toàn (Lab 1). | **Verification** |
| **3** | **Đảm bảo tính toàn vẹn và ràng buộc dữ liệu CRUD Sản phẩm / Menu** | Ngăn chặn việc nhân viên nhập giá bán âm, trùng tên món nước, hoặc xóa nhầm danh mục đang kinh doanh. | Viết Unit Test kiểm thử các phương thức Thêm, Sửa, Xóa món nước, Topping, áp dụng ngoại lệ `ValidationException`. | **Verification** |
| **4** | **Kiểm thử trải nghiệm người dùng tự động trên giao diện Web (UI Test)** | Đảm bảo khách hàng đặt nước online và thu ngân tại quầy thao tác trơn tru, không gặp lỗi giật lag hay vỡ layout. | Xây dựng bộ kiểm thử Selenium WebDriver theo mô hình Page Object Model (POM) tự động tương tác trình duyệt Chrome. | **Validation** |
| **5** | **Tự động hóa hồi quy và Tích hợp liên tục (CI/CD Regression Testing)** | Đảm bảo mỗi khi lập trình viên cập nhật mã nguồn mới lên GitHub, toàn bộ hệ thống test cũ không bị gãy (Break). | Thiết lập GitHub Actions Pipeline (`.github/workflows/test.yml`) chạy tự động `mvn test` trên môi trường Ubuntu Headless. | **Verification** |

---

## 2. PHÂN BIỆT KIỂM THỬ VÀ GỠ LỖI (TESTING VS DEBUGGING - BÀI 1 LAB 1)

* **Kiểm thử (Testing):** Là hoạt động thiết kế và thực thi có chủ đích các kịch bản kiểm thử (Test Scripts trên JUnit 5 & Selenium) nhằm phát hiện ra các sai sót (Defects) và ghi nhận lỗi hệ thống (Failures) khi hành vi thực tế khác biệt so với đặc tả nghiệp vụ (`SPECIFICATIONS.md`).
* **Gỡ lỗi (Debugging):** Là hoạt động chuyên biệt của lập trình viên sau khi nhận được báo cáo thất bại từ Tester/CI: Phân tích Stack Trace $\rightarrow$ xác định nguyên nhân gốc rễ (Root Cause) trong mã nguồn (Service/Controller) $\rightarrow$ sửa lỗi và tối ưu code $\rightarrow$ thực thi lại bộ Test để xác nhận lỗi đã được khắc phục hoàn toàn.

---

## 3. PHẠM VI KIỂM THỬ (TEST SCOPE)

### 3.1. Trong phạm vi kiểm thử (In-Scope)
* **Phân hệ Xác thực (Authentication):**
  * Đăng nhập Admin / Thu ngân với thông tin hợp lệ / không hợp lệ.
  * Đăng ký tài khoản Khách hàng: Kiểm tra định dạng SĐT (10 số), mật khẩu phức tạp, kiểm tra trùng lặp.
* **Phân hệ POS & Xử lý Giao dịch (POS & Payment):**
  * Tùy biến ly nước: Size (S/M/L), Đường (0-100%), Đá (0-100%), Topping.
  * Giảm giá Voucher & Tiêu điểm thành viên CRM.
  * Thanh toán tiền mặt (tính tiền thừa), thanh toán VietQR động, xử lý ngoại lệ hạn mức đơn hàng > 1.000.000 VNĐ.
* **Phân hệ Quản trị CRUD (Admin Products & Toppings):**
  * Thêm, sửa, xóa sản phẩm món nước, bắt ngoại lệ giá bán <= 0, tên món trùng.
* **Kiểm thử tự động giao diện (Selenium UI Automation):**
  * Kịch bản đăng nhập, tìm kiếm món nước và thêm món vào giỏ hàng trên giao diện Web.

### 3.2. Ngoài phạm vi kiểm thử (Out-of-Scope)
* Kiểm thử chịu tải cao (Load/Stress Testing hàng triệu request cùng lúc).
* Kiểm thử thâm nhập an ninh mạng chuyên sâu (Penetration Testing tầng mạng hạ tầng).

---

## 4. CHIẾN LƯỢC VÀ KỸ THUẬT KIỂM THỬ (TEST STRATEGY)

1. **Kỹ thuật Hộp đen (Black-box Testing):**
   * **Phân vùng tương đương (Equivalence Partitioning - EP):** Chia trường dữ liệu (SĐT, Mật khẩu, Giá bán, % Giảm giá) thành các vùng dữ liệu hợp lệ và không hợp lệ.
   * **Phân tích giá trị biên (Boundary Value Analysis - BVA):** Kiểm tra kỹ các giá trị tại ngưỡng biên (ví dụ: mật khẩu 7 ký tự vs 8 ký tự; số tiền 0đ vs 1đ; hạn mức 1.000.000đ vs 1.000.001đ).
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
