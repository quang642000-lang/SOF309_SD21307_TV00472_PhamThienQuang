# MINH CHỨNG SỬ DỤNG AI: XÂY DỰNG KHUNG SELENIUM PAGE OBJECT MODEL (TUẦN 3)
> **Môn học:** SOF309 - Kiểm thử nâng cao cùng AI (FPT Polytechnic)  
> **Sinh viên thực hiện:** Phạm Thiện Quang | **MSSV:** TV00472 | **Lớp:** SD21307  
> **Dự án:** TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE  
> **Giảng viên hướng dẫn:** ThS. Trần Quang Bình  
> **Công cụ AI:** Gemini / Antigravity AI (DOM-Grounded POM Generation)  
> **Thời gian thực hiện:** Tuần 3 (20/11/2026)  

---

## 1. Ngữ cảnh & Mục tiêu (Context & Goal)
* **Ngữ cảnh:** Bước sang Tuần 3 (Y4 - Phần 1), từ bộ file mẫu ban đầu (`LoginPage.java`, `LoginTest.java` chỉ dùng locator giả định và gắn `@Disabled`), dự án cần nâng cấp thành kiến trúc **Selenium WebDriver 4 Page Object Model (POM)** thực thụ, khớp 100% với cấu trúc HTML/JSP thực tế của hệ thống **TEA POS**.
* **Mục tiêu kỹ thuật (Y4 - Phần 1):**
  1. Tách biệt hoàn toàn Locator & hành động DOM vào thư mục `src/test/java/com/poly/sof309/ui/pages/` (`BasePage`, `LoginPage`, `RegisterPage`, `ProductPage`, `PortalMenuPage`) và kịch bản kiểm thử vào `src/test/java/com/poly/sof309/ui/tests/` (`BaseUiTest`, `LoginTest`, `RegisterTest`, `CrudTest`).
  2. Áp dụng 100% **Explicit Wait (`WebDriverWait` + `ExpectedConditions`)**, loại bỏ hoàn toàn `Thread.sleep()` và `implicitlyWait`.
  3. Xử lý đúng cơ chế UI thực tế của Tea POS: **SweetAlert2** modal/toast, HTML5 native validation (`validationMessage`), bảng sản phẩm desktop đáp ứng Bootstrap responsive, và form cuộn bên trong `.admin-content`.
  4. Tự động phát hiện URL Tomcat local (`8080`/`8081`) để chạy Chrome trực tiếp trên máy cá nhân, đồng thời dùng JUnit 5 `Assumptions` bỏ qua an toàn khi server chưa khởi động để pipeline CI/CD GitHub Actions (`mvn clean test`) luôn giữ trạng thái xanh.

---

## 2. Câu lệnh đã gửi cho AI (Prompt)

```text
Bạn là Senior QA Automation Engineer chuyên Selenium WebDriver 4 & Page Object Model (POM) với Java 17 và JUnit 5.
Dưới đây là cấu trúc thực tế các trang JSP và Controller trong dự án TEA POS của tôi:
1. /login (login_admin.jsp) & /customer/login (login_customer.jsp):
   - Input: name="username", name="password", cả hai đều có thuộc tính HTML5 required.
   - Nút submit: //form//button[@type='submit'].
   - Thông báo lỗi server hiển thị qua SweetAlert2 (Swal.fire({ icon: 'error', text: ... })), không có #error-message tĩnh.
   - Cơ chế Brute-Force: sai 5 lần liên tiếp bị khóa tài khoản 5 phút (NhanVienServiceImpl).
2. /register (register.jsp):
   - Input theo id: tenKh, soDienThoai, email, password, confirmPassword, checkbox dongYDieuKhoan (required).
   - JS validate trước submit bằng SweetAlert2 (SĐT 10-11 số, password trùng confirmPassword, password >= 8 ký tự), lỗi trùng lặp từ RegisterController cũng render bằng SweetAlert2.
3. /admin/sanpham (san_pham.jsp) & /admin/sanpham?action=create|edit (sanpham-form.jsp):
   - Danh sách desktop nằm trong .d-none.d-lg-block (#productTable, #productTableBody tr.product-row), mỗi trang 10 dòng, lọc tức thì qua #productSearchInput (onkeyup).
   - Form tạo/sửa sản phẩm (#productForm) có #tenSp, #maDm, #thuTuHienThi, #moTa và bảng kích cỡ #sizesConfigTableBody (.size-check, #size_price_<id>, #size_volume_<id>).
   - Thiếu kích cỡ khi submit hiện SweetAlert2 warning; xóa sản phẩm hiện SweetAlert2 confirm ("Đồng ý") và chuyển về ?msg=harddeletesuccess nếu chưa từng có hóa đơn.
4. /products (danh_sach_san_pham.jsp):
   - Sidebar desktop #portalFilterSidebar có input[name='search'] và danh sách .product-card.

Yêu cầu:
- Thiết kế BasePage và 4 Page Classes (LoginPage, RegisterPage, ProductPage, PortalMenuPage) trong com.poly.sof309.ui.pages.
- Thiết kế BaseUiTest và 3 Test Classes (LoginTest, RegisterTest, CrudTest) trong com.poly.sof309.ui.tests.
- Bắt buộc dùng WebDriverWait, tuyệt đối không dùng Thread.sleep(), không đặt locator trong Test Class, và dọn sạch dữ liệu test tạo ra trong DB.
```

---

## 3. Kết quả do AI sinh ra (Raw AI Output)

AI đã sinh ra bộ khung kiến trúc POM hoàn chỉnh gồm 9 class chia 2 tầng rõ ràng:
* **Tầng Page Objects (`ui/pages/`):**
  * `BasePage.java`: Khởi tạo `WebDriverWait`, đóng gói `waitForVisibility`, `waitForClickable`, `scrollIntoView`, `click`, `type`, `getValidationMessage`, và bộ thao tác SweetAlert2 (`waitForSwalTitle`, `waitForSwalMessage`, `confirmSwalDialog`).
  * `LoginPage.java`, `RegisterPage.java`, `ProductPage.java`, `PortalMenuPage.java`: Quản lý tập trung toàn bộ `By` locator và trả về chuỗi hành động nghiệp vụ (Fluent Page Object).
* **Tầng Test Scripts (`ui/tests/`):**
  * `BaseUiTest.java`: Cấu hình `ChromeOptions`, hỗ trợ headless trên CI, tự dò Tomcat đang chạy và gọi `Assumptions.assumeTrue` khi không có server.
  * `LoginTest.java` (5 test), `RegisterTest.java` (5 test), `CrudTest.java` (4 test) bao phủ 14 kịch bản giao diện từ xác thực (`TC_AUTH_01`–`04`, `10`), đăng ký (`TC_AUTH_07`–`09`, `11`–`12`), CRUD sản phẩm (`TC_PROD_01`–`03`, `05`) đến tìm kiếm thực đơn Portal (`TC_PROD_06`).

---

## 4. Chỉnh sửa và Tinh chỉnh của Sinh viên (Human-in-the-Loop Refinement)

Sau khi đối chiếu code AI sinh ra với mã nguồn JSP và chạy kiểm thử thực tế trên Tomcat 11 + SQL Server + Chrome, sinh viên đã rà soát và tinh chỉnh các điểm quan trọng sau:

1. **Sửa Locator sai lệch từ file mẫu cũ sang DOM thật của Tea POS:**
   * File mẫu ban đầu dùng `By.id("username")`, `By.id("password")`, `By.id("error-message")`. Trong `login_admin.jsp` và `login_customer.jsp`, các ô nhập chỉ có thuộc tính `name="username"` / `name="password"` (không có `id`), còn thông báo lỗi do **SweetAlert2** render động tại `.swal2-popup .swal2-html-container`. Đã cập nhật toàn bộ sang locator thật và đọc `validationMessage` cho trường hợp bỏ trống HTML5 `required`.
2. **Xử lý viewport Responsive & vùng cuộn nội bộ `.admin-content`:**
   * Bảng `#productTable` (`san_pham.jsp`) và thanh tìm kiếm `#portalFilterSidebar` (`danh_sach_san_pham.jsp`) gắn lớp Bootstrap `d-none d-lg-block` (ẩn dưới 992px). Đã ép thêm cờ `--window-size=1920,1080` trong `BaseUiTest` để chạy Headless vẫn hiển thị đầy đủ giao diện Desktop.
   * Trong `sanpham-form.jsp`, form cuộn bên trong `.admin-content` (`overflow-y: auto`), nên ở `BasePage` bổ sung `scrollIntoView({block: 'center'})` trước khi click các checkbox kích cỡ và nút Lưu.
3. **Loại bỏ `implicitlyWait` & xử lý phân trang / Brute-Force / dọn rác DB:**
   * Loại bỏ `implicitlyWait` khỏi `BaseUiTest` để tránh cộng dồn độ trễ bất thường khi trộn với `WebDriverWait`.
   * Dùng ô lọc `#productSearchInput` trong `ProductPage` để đưa sản phẩm vừa tạo về trang 1 trước khi bấm Sửa/Xóa (tránh lỗi không tìm thấy khi danh sách có > 10 sản phẩm).
   * Dùng username `"invalid_qa_user"` trong `testStaffLoginInvalidCredentialsShowsSwalError` thay vì `"admin"` để không làm khóa tài khoản quản trị 5 phút bởi cơ chế Brute-Force của `NhanVienServiceImpl`.
   * Trong `CrudTest`, cuối kịch bản thêm/sửa sản phẩm gọi `deleteProductAndConfirm()` kích hoạt Hard Delete để không để lại dữ liệu rác trong SQL Server.
4. **Nghiệm thu thực thi thực tế:**
   * **Chế độ CI / Offline (`mvn test "-Dapp.baseUrl=http://127.0.0.1:59999"`, mô phỏng môi trường chưa bật Tomcat):**
     ```text
     [INFO] Tests run: 90, Failures: 0, Errors: 0, Skipped: 14
     [INFO] BUILD SUCCESS (Total time: 2.313 s)
     ```
     (76 Unit Test Controller đạt Pass 100%, 14 UI Test được skip an toàn qua `Assumptions.assumeTrue` để giữ xanh GitHub Actions).
   * **Chạy toàn bộ Suite thực tế trên Chrome 157 + Tomcat 11 + SQL Server (`mvn test`):**
     ```text
     [INFO] Running com.poly.sof309.controllers.AuthControllerTest (21/21 Pass)
     [INFO] Running com.poly.sof309.controllers.BanHangPOSControllerTest (22/22 Pass)
     [INFO] Running com.poly.sof309.controllers.PortalCheckoutControllerTest (10/10 Pass)
     [INFO] Running com.poly.sof309.controllers.SanPhamControllerTest (11/11 Pass)
     [INFO] Running com.poly.sof309.controllers.VoucherControllerTest (12/12 Pass)
     [INFO] Running com.poly.sof309.ui.tests.CrudTest
     [INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 30.37 s
     [INFO] Running com.poly.sof309.ui.tests.LoginTest
     [INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 9.227 s
     [INFO] Running com.poly.sof309.ui.tests.RegisterTest
     [INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 10.12 s
     [INFO] Results:
     [INFO] Tests run: 90, Failures: 0, Errors: 0, Skipped: 0
     [INFO] BUILD SUCCESS (Total time: 54.840 s)
     ```
