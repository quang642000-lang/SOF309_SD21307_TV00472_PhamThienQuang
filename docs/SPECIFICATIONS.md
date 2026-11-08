# TÀI LIỆU ĐẶC TẢ NGHIỆP VỤ HỆ THỐNG TEA POS (SPECIFICATIONS)
> **Dự án kiểm thử:** TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE  
> **Môn học:** SOF309 - Kiểm thử nâng cao cùng AI (FPT Polytechnic)  
> **Sinh viên thực hiện:** Phạm Thiện Quang | **MSSV:** TV00472 | **Lớp:** SD21307  
> **Giảng viên hướng dẫn:** ThS. Trần Quang Bình (binhtq7 - Thầy Mười Giáo)  

---

## 1. THÔNG TIN CHUNG DỰ ÁN
* **Tên ứng dụng:** TEA POS Enterprise & Portal O2O Click & Collect System
* **Mô hình kiến trúc:** 3-Tier Enterprise Architecture (MVC Pattern)
* **Công nghệ nền tảng:** Java 21 LTS / Java 17, Jakarta EE 10 (Servlet 6.0, JSP 3.1, JSTL), Microsoft SQL Server 2022 (19 bảng chuẩn hóa), Connection Pool HikariCP 5.1.0, Apache Tomcat 10/11.
* **Công nghệ kiểm thử:** JUnit 5 (5.10.2), Selenium WebDriver 4 (4.20.0), Maven Surefire, GitHub Actions CI/CD.
* **Cổng chạy local:** `http://localhost:8080/SOF309_SD21307_TV00472_PhamThienQuang` hoặc `http://localhost:8080`

---

## 2. QUY TẮC NGHIỆP VỤ CHI TIẾT (BUSINESS RULES)

### A. Phân hệ Xác thực & Phân quyền (Authentication & Authorization)
1. **Đăng nhập Quản trị & Quầy Thu ngân (`/views/auth/login_admin.jsp`):**
   * `username`: Bắt buộc, không được để trống, không chứa khoảng trắng.
   * `password`: Bắt buộc, không được để trống, độ dài tối thiểu 6 ký tự.
   * Cơ chế mã hóa: Băm mật khẩu chuẩn Salted SHA-256 kết hợp Pepper hệ thống nhằm chống tấn công Rainbow Table & Timing Side-channel.
   * Phân quyền (RBAC):
     * `ADMIN`: Toàn quyền hệ thống (Menu, Nhân sự, Doanh thu, Cài đặt).
     * `THUNGAN`: Quyền truy cập màn hình POS bán hàng, thanh toán, in hóa đơn.
     * `QUANLY`: Quản lý kho nguyên liệu, phê duyệt voucher, xem báo cáo ca.
   * Tài khoản kiểm thử mặc định: `admin` / `Admin@123` (Admin), `thungan1` / `Thungan@123` (Thu ngân).

2. **Đăng nhập & Đăng ký Khách hàng Portal (`/views/auth/login_customer.jsp`, `/views/auth/register.jsp`):**
   * `soDienThoai`: Bắt buộc đúng 10 chữ số, bắt đầu bằng các đầu số viễn thông Việt Nam (`03`, `05`, `07`, `08`, `09`). Không được trùng lặp trong cơ sở dữ liệu.
   * `email`: Định dạng RFC 5322 hợp lệ (phải chứa ký tự `@` và tên miền hợp lệ).
   * `password`: Tối thiểu 8 ký tự, bắt buộc chứa ít nhất 1 chữ hoa, 1 chữ thường, 1 chữ số và 1 ký tự đặc biệt.
   * `confirmPassword`: Phải hoàn toàn trùng khớp với `password`.
   * Kích hoạt & OTP: Mã OTP gồm 6 chữ số gửi qua SMTP Gmail (Jakarta Mail), thời gian hết hạn là 300 giây (5 phút). Nhập sai quá 5 lần sẽ khóa yêu cầu 15 phút.

---

### B. Phân hệ Quầy Thu Ngân Bán Lẻ (POS Cashier Pro) & Xử lý Giao dịch
1. **Tạo đơn hàng & Tùy biến ly nước:**
   * Chọn món từ danh mục (Trà sữa, Trà trái cây, Cà phê, Sinh tố...).
   * Ràng buộc Size:
     * Size S: Giá gốc (+0 VNĐ).
     * Size M: Phụ thu chuẩn (+6.000 VNĐ).
     * Size L: Phụ thu chuẩn (+12.000 VNĐ).
   * Cấp độ Đường / Đá: Nhận các giá trị hợp lệ cố định (`0%`, `30%`, `50%`, `70%`, `100%`).
   * Chọn Topping: Cho phép chọn nhiều topping (Trân châu đen +5.000đ, Trân châu trắng +8.000đ, Cheese foam +10.000đ...), giá topping không được âm.
   * Số lượng mỗi ly nước: Số nguyên dương >= 1 và <= 99 ly cho một món trong cùng đơn hàng.

2. **Hội viên CRM & Khuyến mãi (Loyalty & Voucher):**
   * Tra cứu khách hàng theo SĐT bằng AJAX: Trả về thông tin khách, hạng thành viên (Đồng, Bạc, Vàng, Kim Cương) và số điểm tích lũy hiện có.
   * Quy tắc tiêu điểm: 1 điểm tích lũy = 1.000 VNĐ giảm trừ trực tiếp. Số điểm tiêu không được vượt quá tổng giá trị đơn hàng hoặc số dư khả dụng.
   * Quy tắc Voucher:
     * Kiểm tra ngày hiệu lực (`ngayBatDau <= ngayHienTai <= ngayKetThuc`).
     * Kiểm tra giá trị đơn tối thiểu (`tongTien >= donToiThieu`).
     * Kiểm tra số lượt sử dụng còn lại (`soLuotConLai > 0`).
     * Giảm theo `%`: Giới hạn mức giảm tối đa (`giamToiDa`).
     * Giảm tiền mặt: Trừ thẳng số tiền quy định.

3. **Xử lý Thanh toán & Kiểm thử Hạn mức (Bám sát Lab 1 - Testing vs Debugging):**
   * Tổng tiền hóa đơn sau giảm giá (`finalAmount`): Phải >= 0 VNĐ.
   * **Đơn hàng 0 VNĐ:** Tự động hoàn tất (PAID), bỏ qua bước sinh mã VietQR.
   * **Thanh toán tiền mặt:** Thu ngân nhập `tienKhachDua >= finalAmount`, hệ thống tự tính `tienThua = tienKhachDua - finalAmount`. Ném lỗi nếu tiền khách đưa nhỏ hơn tiền phải trả.
   * **Thanh toán VietQR động:** Hệ thống tạo mã QR động chứa số tài khoản, mã đơn và số tiền chính xác. Khi đơn hàng có giá trị cao (> 1.000.000 VNĐ), hệ thống yêu cầu xác nhận 2 lớp an toàn (OTP quản lý hoặc đối soát tự động qua Webhook SePay).

---

### C. Phân hệ Cổng Đặt Nước Trực Tuyến O2O (Portal Click & Collect)
1. **Menu & Tìm kiếm sản phẩm:**
   * Tìm kiếm Tiếng Việt không dấu (`Vietnamese_CI_AI`): Nhập `"tra sua"` hay `"trà sữa"` đều trả về đúng kết quả tức thì.
   * Lọc theo Danh mục và sắp xếp theo Giá (Tăng dần / Giảm dần).
2. **Giỏ hàng & Hẹn giờ nhận nước (Click & Collect):**
   * Khách hàng tự do thêm, bớt, xóa món khỏi giỏ hàng.
   * **Quy tắc thời gian hẹn lấy nước:** Thời gian khách hẹn lấy phải sau thời điểm đặt tối thiểu 15 phút (để quầy pha chế chuẩn bị) và không vượt quá 24 giờ.
   * Khi hoàn tất thanh toán online, đơn hàng lập tức đồng bộ về màn hình POS của thu ngân ở trạng thái `PREPARING` (Đang pha chế).

---

### D. Phân hệ Quản trị CRUD (Admin Enterprise Module)
1. **Quản lý Sản phẩm / Món nước (`/admin/san-pham`):**
   * `maSanPham`: Tự sinh hoặc định danh duy nhất (không trùng lặp).
   * `tenSanPham`: Bắt buộc, độ dài từ 3 đến 100 ký tự, không được trùng lặp tên với sản phẩm đang hoạt động.
   * `giaBan`: Số thực dương bắt buộc > 0 VNĐ (Không chấp nhận giá 0đ hoặc giá âm).
   * `idDanhMuc`: Bắt buộc chọn từ danh mục có sẵn.
   * `trangThai`: `true` (Đang kinh doanh), `false` (Ngừng kinh doanh).
2. **Quản lý Topping (`/admin/topping`):**
   * `tenTopping`: Bắt buộc, không để trống.
   * `giaTopping`: Bắt buộc >= 0 VNĐ.
3. **Quản lý Voucher (`/admin/voucher`):**
   * `maVoucher`: Ký tự chữ và số in hoa (5 - 15 ký tự), duy nhất.
   * `phanTramGiam`: Từ 1% đến 100% (nếu loại là phần trăm).
   * `soTienGiam`: > 0 VNĐ (nếu loại là tiền cố định).
   * `ngayKetThuc`: Phải lớn hơn hoặc bằng `ngayBatDau`.
