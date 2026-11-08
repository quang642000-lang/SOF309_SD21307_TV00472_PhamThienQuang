# TÀI LIỆU ĐẶC TẢ NGHIỆP VỤ HỆ THỐNG TEA POS (SPECIFICATIONS)
> **Dự án:** TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE  
> **Môn học:** SOF309 - Kiểm thử nâng cao cùng AI (FPT Polytechnic)  
> **Sinh viên thực hiện:** Phạm Thiện Quang | **MSSV:** TV00472 | **Lớp:** SD21307  
> **Giảng viên hướng dẫn:** ThS. Trần Quang Bình  

---

## 1. THÔNG TIN CHUNG DỰ ÁN
* **Tên ứng dụng:** TEA POS Enterprise & Portal O2O Click & Collect System
* **Kiến trúc hệ thống:** 3-Tier Enterprise Architecture (MVC Pattern)
* **Công nghệ nền tảng:** Java 21 LTS / Java 17, Jakarta EE 10 (Servlet 6.0, JSP 3.1, JSTL), Microsoft SQL Server 2022 (19 bảng chuẩn hóa, Sequences tự tăng NO CACHE), Connection Pool HikariCP 5.1.0, Apache Tomcat 10/11.
* **Công nghệ kiểm thử:** JUnit 5 (5.10.2), Selenium WebDriver 4 (4.20.0), Maven Surefire Plugin, GitHub Actions CI/CD.
* **Cổng chạy local:** `http://localhost:8080/SOF309_SD21307_TV00472_PhamThienQuang` hoặc `http://localhost:8080`

---

## 2. QUY TẮC NGHIỆP VỤ CHI TIẾT THEO MÃ NGUỒN THỰC TẾ (BUSINESS RULES)

### A. Phân hệ Xác thực, Phân quyền & Bảo mật (Authentication & Security)
1. **Đăng nhập Nội bộ Nhân viên (`/login` $\rightarrow$ `LoginController`):**
   * Tham số đầu vào: `username`, `password`. Bắt buộc không được để trống.
   * Cơ chế mã hóa: Thuật toán băm Salted SHA-256 kết hợp Pepper hệ thống (`[$tea_pos_salt_key$]`) trong `SecurityUtil` chống tấn công Rainbow Table.
   * Cơ chế chống Brute-Force: Theo dõi số lần đăng nhập thất bại liên tiếp trong `NhanVienServiceImpl`. Nhập sai 5 lần liên tiếp sẽ tạm khóa tài khoản 5 phút (300 giây) kèm đồng hồ đếm ngược.
   * Phân quyền điều hướng (RBAC):
     * Vai trò `ma_vt = 1` (Admin): Chuyển hướng về `/admin/dashboard`.
     * Vai trò `ma_vt = 2` (Thu ngân): Chuyển hướng về `/pos`.
   * Ghi vết kiểm toán: Tự động lưu địa chỉ IP máy trạm, thời gian và mã nhân viên vào bảng `NHAT_KY_HOAT_DONG`.

2. **Đăng nhập & Đăng ký Khách hàng Portal (`/customer/login`, `/register`):**
   * Đăng nhập khách hàng: Sử dụng `username` (Email hoặc Số điện thoại) và `password`. Bắt buộc tài khoản đã được kích hoạt OTP.
   * Đăng ký khách hàng (`/register`):
     * Tham số: `tenKh`, `soDienThoai` (10 số), `email` (định dạng RFC), `password`, `confirmPassword`, `dongYDieuKhoan` (bắt buộc tick chọn).
     * Ràng buộc: `password` và `confirmPassword` phải khớp nhau. `soDienThoai` và `email` không được trùng lặp với bản ghi đã có trong CSDL.
     * Tài khoản mới khởi tạo ở trạng thái `trang_thai = 0` (Chờ kích hoạt), tự động cấp giỏ hàng rỗng và gán hạng thành viên Đồng (`ma_hang = 1`).
   * Xác thực OTP kích hoạt tài khoản: Mã OTP 6 chữ số gửi qua SMTP Jakarta Mail, thời hạn hiệu lực 5 phút (300 giây). Xác thực thành công mới chuyển `trang_thai = 1`.

---

### B. Phân hệ Quầy Thu Ngân Bán Lẻ (POS Cashier Pro - `/pos`)
1. **Tra cứu Menu & Tùy biến Ly nước:**
   * Lọc sản phẩm theo danh mục (`ma_dm`). Chỉ hiển thị sản phẩm và danh mục có `trang_thai = 1`.
   * Tìm kiếm món: Hỗ trợ tìm kiếm tiếng Việt không dấu và có dấu tức thì.
   * Tùy biến ly nước:
     * Kích cỡ (Size): Giá ly tính theo bảng giá cấu hình trong `SAN_PHAM_KICH_CO` của từng size.
     * Mức Đường / Mức Đá: Nhận các giá trị hợp lệ cố định (`0%`, `30%`, `50%`, `70%`, `100%`).
     * Topping: Tích chọn từ bảng `TOPPING` (chỉ hiển thị topping có `trang_thai = 1`), hỗ trợ tăng số lượng từng loại topping.
     * Ghi chú pha chế: Ghi chú riêng cho ly nước được lưu vào `ghi_chu_mon` và in lên tem dán ly / hóa đơn.

2. **Hội viên CRM & Đổi điểm thưởng tại quầy:**
   * Tra cứu khách hàng AJAX qua URL `/pos/search-customer?phone=...`: Hiển thị họ tên, số điện thoại, hạng thẻ và điểm tích lũy hiện có.
   * Tạo nhanh khách hàng tại quầy qua URL `/pos/create-customer`: Tạo nhanh họ tên và SĐT mà không làm mất giỏ hàng đang chọn.
   * Quy tắc tiêu điểm CRM: **1 điểm tích lũy = 1.000 VNĐ**. Số điểm sử dụng không được vượt quá số dư điểm hiện có và không được trừ vượt quá tổng tiền đơn hàng.

3. **Áp dụng Mã giảm giá (Voucher) tại quầy (`/pos/apply-voucher`):**
   * Kiểm tra điều kiện: Voucher còn hạn (`ngay_bat_dau <= now <= ngay_ket_thuc`), còn số lượng phát hành (`so_luong > so_luong_da_dung`), và tổng tiền hàng đạt giá trị tối thiểu (`tongTienHang >= don_toi_thieu`).
   * Cơ chế giảm giá:
     * `loai_giam = 1`: Giảm tiền mặt cố định (`gia_tri_giam`).
     * `loai_giam = 2`: Giảm theo % (`tongTienHang * gia_tri_giam / 100`). Nếu có `giam_toi_da > 0` và số tiền giảm vượt mức trần thì chốt giảm bằng `giam_toi_da`.
     * Tiền giảm giá không bao giờ vượt quá tổng tiền hàng (`tienGiamGia <= tongTienHang`).

4. **Xử lý Thanh toán Hóa đơn (`/pos/checkout`):**
   * Tính toán tài chính:
     * `billBeforeTax = tongTienHang - tienGiamGia - tienTruDiem` (tối thiểu = 0).
     * Thuế VAT: Tính chuẩn 8% trên số tiền sau giảm trừ (`vatPrice = (int) Math.round(billBeforeTax * 0.08)`).
     * `tongPhaiTra = billBeforeTax + vatPrice`.
   * **Nghiệp vụ Đơn hàng 0 VNĐ:** Nếu `tongPhaiTra <= 0` (do voucher hoặc điểm trừ hết), hệ thống tự động gán `maPt = 1` (Tiền mặt/Miễn phí), chuyển `trangThaiThanhToan = 1` (Đã thanh toán) và `trangThaiDon = 4` (Hoàn thành đơn), tuyệt đối không bắt sinh mã VietQR 0đ.
   * **Thanh toán tiền mặt (`maPt = 1`):** Thu ngân nhập tiền khách đưa, tính `tienThua = tienKhachDua - tongPhaiTra`. Tự động chốt hoàn thành và cộng điểm CRM.
   * **Thanh toán VietQR (`maPt = 2`):** Sinh mã QR ngân hàng chuẩn Napas gắn mã đơn và số tiền cần trả.
   * **Tích điểm tự động:** Đơn hoàn tất tự động cộng điểm cho khách vào CSDL: `diemCong = billBeforeTax / 10000` (1 điểm cho mỗi 10.000 VNĐ chi tiêu thực tế).

---

### C. Phân hệ Đặt Nước Trực Tuyến O2O (Click & Collect Portal)
1. **Menu, Giỏ hàng & Mua ngay (`/portal`, `/products`, `/cart`):**
   * Menu: Phân loại theo Bestsellers (`is_bestseller = 1`) và New Arrivals (`is_new = 1`).
   * Quản lý giỏ hàng (`/cart`): Lưu trữ giỏ hàng trong CSDL (bảng `GIO_HANG`, `CHI_TIET_GIO_HANG`, `CHI_TIET_TOPPING_GIO_HANG`). Hỗ trợ chọn mua một phần giỏ hàng thông qua cờ `is_chon_mua`.
   * Mua ngay (Buy Now): Lưu ly nước tạm thời trong Session (`buyNowItem`), chuyển thẳng tới trang checkout mà không làm mất giỏ hàng chính trong CSDL.

2. **Hẹn giờ đến lấy nước (Click & Collect - `/checkout`, `/checkout/place`):**
   * **Ràng buộc bắt buộc:** Khách hàng bắt buộc phải chọn khung giờ hẹn lấy nước (`thoiGianHenLay`).
   * Thời gian hẹn lấy phải sau thời điểm đặt hàng tối thiểu 15 phút để quầy pha chế kịp chuẩn bị.
   * Chống trùng lặp đơn hàng (Anti Double-Checkout): Sử dụng Token UUID `checkoutToken` kết hợp vô hiệu hóa cache trình duyệt (`Cache-Control: no-cache, no-store`).

3. **Cổng thanh toán tự động VietQR & Webhook SePay:**
   * Tạo mã VietQR động chứa số tài khoản, số tiền và nội dung chuyển khoản là mã đơn hàng.
   * Đồng hồ đếm ngược giao dịch 10 phút.
   * Client polling định kỳ mỗi 2 giây gọi API kiểm tra trạng thái thanh toán từ bộ đệm RAM `PaymentStore` (tránh nghẽn CSDL).
   * Webhook tiếp nhận thông báo từ SePay, tự động chuyển đơn sang `trang_thai_thanh_toan = 1`.

4. **Điều phối đơn Online tại quầy thu ngân (`/pos/nhandon`):**
   * Hiển thị danh sách các đơn hàng online (`loai_don_hang = 3`).
   * Luân chuyển trạng thái đơn hàng:
     * `0`: Chờ xác nhận $\rightarrow$ `1`: Đã xác nhận $\rightarrow$ `2`: Đang làm $\rightarrow$ `3`: Chờ lấy $\rightarrow$ `4`: Hoàn thành $\rightarrow$ `5`: Đã hủy (bắt buộc nhập `ly_do_huy`, hoàn lại voucher và điểm thưởng).

---

### D. Phân hệ Quản trị Admin (Admin Enterprise Module)
1. **Quản trị Thực đơn Sản phẩm (`/admin/sanpham`):**
   * Các hành động (`action`): `list`, `create`, `edit`, `toggle`, `delete`.
   * Bảng `SAN_PHAM` lưu thông tin cha; giá bán chi tiết được lưu trong bảng biến thể `SAN_PHAM_KICH_CO`.
   * Ràng buộc tạo mới sản phẩm: Bắt buộc có tên sản phẩm (`tenSp`), danh mục (`maDm`), và bắt buộc cấu hình tối thiểu 1 kích cỡ kèm giá bán (`gia_ban >= 0`).
   * Cơ chế xóa sản phẩm (`performDelete`):
     * Nếu sản phẩm đã phát sinh hóa đơn trong `CHI_TIET_DON_HANG`: Thực hiện **Soft Delete** (cập nhật `trang_thai = 0` để bảo toàn dữ liệu kế toán lịch sử).
     * Nếu sản phẩm chưa từng có hóa đơn: Thực hiện **Hard Delete** (xóa sạch khỏi bảng `SAN_PHAM_KICH_CO` và `SAN_PHAM`).

2. **Quản trị Danh mục (`/admin/danhmuc`):**
   * CRUD danh mục đồ uống. Khi tắt trạng thái một danh mục, toàn bộ sản phẩm thuộc danh mục đó sẽ tự động ẩn trên POS và Portal.

3. **Quản trị Topping (`/admin/topping`):**
   * Quản lý danh sách topping kèm đơn giá (`gia_ban >= 0`) và trạng thái còn hàng/hết hàng.

4. **Quản trị Voucher Khuyến mãi (`/admin/voucher`):**
   * Thiết lập mã code in hoa duy nhất, loại giảm (tiền mặt / %), đơn tối thiểu, giảm tối đa, số lượng và thời hạn sử dụng.

5. **Nhật ký kiểm toán (`/admin/auditlog`):**
   * Lưu vết mọi thao tác thêm, sửa, xóa dữ liệu và đăng nhập/đăng xuất kèm địa chỉ IP vào bảng `NHAT_KY_HOAT_DONG`. Tự động che giấu mật khẩu.
