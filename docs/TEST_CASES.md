# BỘ KỊCH BẢN KIỂM THỬ CHI TIẾT (TEST CASES)
### Dự án: TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE
> **Môn học:** SOF309 - Kiểm thử nâng cao cùng AI (FPT Polytechnic)  
> **Sinh viên:** Phạm Thiện Quang | **MSSV:** TV00472 | **Lớp:** SD21307  
> **Giảng viên hướng dẫn:** ThS. Trần Quang Bình  

---

## 1. PHÂN HỆ XÁC THỰC, BẢO MẬT & PHÂN QUYỀN (AUTHENTICATION & SECURITY)

| Test Case ID | Tên ca kiểm thử | Loại test | Tiền điều kiện | Các bước thực hiện | Dữ liệu đầu vào (Test Data) | Kết quả mong đợi | Trạng thái |
| :---: | :--- | :---: | :--- | :--- | :--- | :--- | :---: |
| **TC_AUTH_01** | Đăng nhập Nhân viên Quản trị (Admin) thành công | Positive | Tài khoản Admin tồn tại trong DB, `trang_thai = 1`, `ma_vt = 1` | 1. Truy cập `/login`<br>2. Nhập username Admin<br>3. Nhập password đúng<br>4. Bấm Đăng nhập | `username = "admin"`<br>`password = "Admin@123"` | Đăng nhập thành công, lưu session `user`, ghi log kiểm toán IP, chuyển hướng về `/admin/dashboard` | **PASS** |
| **TC_AUTH_02** | Đăng nhập Nhân viên Thu ngân (POS Cashier) thành công | Positive | Tài khoản Thu ngân tồn tại, `trang_thai = 1`, `ma_vt = 2` | 1. Truy cập `/login`<br>2. Nhập username Thu ngân<br>3. Nhập password đúng<br>4. Bấm Đăng nhập | `username = "cashier01"`<br>`password = "Cashier@123"` | Đăng nhập thành công, chuyển hướng về màn hình quầy bán lẻ `/pos` | **PASS** |
| **TC_AUTH_03** | Báo lỗi khi để trống Tên đăng nhập hoặc Mật khẩu | Negative | Đang ở màn hình đăng nhập nội bộ `/login` | 1. Để trống username hoặc password<br>2. Bấm Đăng nhập | `username = ""` hoặc `password = ""` | Hệ thống chặn lại, hiển thị thông báo: "Tên đăng nhập và mật khẩu không được trống." | **PASS** |
| **TC_AUTH_04** | Đăng nhập thất bại khi nhập sai Mật khẩu | Negative | Tài khoản `admin` tồn tại trong hệ thống | 1. Nhập username đúng<br>2. Nhập password sai<br>3. Bấm Đăng nhập | `username = "admin"`<br>`password = "SaiMatKhau999"` | Đăng nhập thất bại, tăng bộ đếm đăng nhập sai, hiển thị: "Tên đăng nhập hoặc mật khẩu không chính xác." | **PASS** |
| **TC_AUTH_05** | Kích hoạt khóa tài khoản 5 phút khi sai mật khẩu 5 lần liên tiếp (Brute-Force) | Negative / Security | Tài khoản `cashier01` đang hoạt động bình thường | 1. Nhập sai mật khẩu liên tiếp 5 lần<br>2. Thử đăng nhập lần thứ 6 | `username = "cashier01"`<br>`password = "Sai123"` x 5 lần | Hệ thống khóa tài khoản 300 giây, hiển thị thông báo: "Tài khoản đã bị khóa tạm thời 5 phút do nhập sai mật khẩu liên tiếp 5 lần." | **PASS** |
| **TC_AUTH_06** | Đăng ký tài khoản Khách hàng Portal thành công | Positive | SĐT và Email chưa từng có trong bảng `KHACH_HANG` | 1. Truy cập `/register`<br>2. Điền đầy đủ form<br>3. Tick chọn đồng ý điều khoản<br>4. Bấm Đăng ký | `tenKh = "Phạm Thiện Quang"`<br>`soDienThoai = "0987654321"`<br>`email = "quang@example.com"`<br>`password = "Quang@123"`<br>`confirmPassword = "Quang@123"`<br>`dongYDieuKhoan = "on"` | Tạo tài khoản ở trạng thái `trang_thai = 0` (chờ kích hoạt), lưu session gửi mã OTP qua email, chuyển sang `/verify-otp?type=activation` | **PASS** |
| **TC_AUTH_07** | Báo lỗi đăng ký khi Mật khẩu xác nhận không trùng khớp | Negative | Đang ở màn hình đăng ký `/register` | 1. Điền form đăng ký<br>2. Nhập `confirmPassword` khác `password`<br>3. Bấm Đăng ký | `password = "Quang@123"`<br>`confirmPassword = "Quang@KhacNhau"` | Hệ thống chặn lại, giữ nguyên giá trị đã nhập, báo lỗi: "Mật khẩu nhập lại không trùng khớp với mật khẩu ban đầu." | **PASS** |
| **TC_AUTH_08** | Báo lỗi đăng ký khi chưa tick chọn Điều khoản dịch vụ | Negative | Đang ở màn hình đăng ký `/register` | 1. Điền đầy đủ thông tin hợp lệ<br>2. Bỏ tick ô "Đồng ý điều khoản"<br>3. Bấm Đăng ký | `dongYDieuKhoan = null` | Báo lỗi: "Bạn bắt buộc phải tick chọn đồng ý với điều khoản sử dụng hệ thống." | **PASS** |
| **TC_AUTH_09** | Báo lỗi đăng ký khi trùng lặp Số điện thoại hoặc Email | Negative | SĐT `0987654321` đã tồn tại trong CSDL | 1. Điền thông tin với SĐT đã đăng ký trước đó<br>2. Bấm Đăng ký | `soDienThoai = "0987654321"` | Báo lỗi: "Đăng ký không thành công. Số điện thoại hoặc Email này đã tồn tại trong hệ thống." | **PASS** |

---

## 2. PHÂN HỆ QUẦY THU NGÂN POS & XỬ LÝ THANH TOÁN (POS CASHIER & PAYMENT)

| Test Case ID | Tên ca kiểm thử | Loại test | Tiền điều kiện | Các bước thực hiện | Dữ liệu đầu vào (Test Data) | Kết quả mong đợi | Trạng thái |
| :---: | :--- | :---: | :--- | :--- | :--- | :--- | :---: |
| **TC_PAY_01** | Thanh toán tiền mặt tại quầy và tính tiền thừa chính xác | Positive | Đơn hàng 2 ly trà sữa, tổng phải trả 85.000 VNĐ | 1. Chọn phương thức Tiền mặt (`maPt = 1`)<br>2. Thu ngân nhập tiền khách đưa 100.000 VNĐ<br>3. Bấm Chốt đơn | `tongPhaiTra = 85000`<br>`tienKhachDua = 100000`<br>`maPt = 1` | `tienThua = 15000 VNĐ`, chuyển `trangThaiThanhToan = 1`, `trangThaiDon = 4` (Hoàn thành), in hóa đơn | **PASS** |
| **TC_PAY_02** | Xử lý tự động Đơn hàng 0 VNĐ (Áp voucher 100% hoặc đổi trọn điểm) | Positive | Khách áp mã giảm giá hoặc đổi điểm bù toàn bộ giá trị đơn | 1. Áp voucher 100% làm `tongPhaiTra <= 0`<br>2. Bấm Thanh toán | `tongTienHang = 60000`<br>`tienGiamGia = 60000`<br>`tongPhaiTra = 0` | Tự động gán `maPt = 1` (Miễn phí), chuyển `trangThaiThanhToan = 1`, `trangThaiDon = 4`, tuyệt đối không sinh mã VietQR 0đ | **PASS** |
| **TC_PAY_03** | Tính chuẩn xác Thuế Giá trị Gia tăng (VAT 8%) trên số tiền sau giảm trừ | Positive | Đơn hàng áp voucher giảm giá một phần | 1. Chọn món 100.000 VNĐ<br>2. Áp voucher giảm 20.000 VNĐ<br>3. Kiểm tra tiền thuế VAT và tổng phải trả | `tongTienHang = 100000`<br>`tienGiamGia = 20000`<br>`billBeforeTax = 80000` | `vatPrice = Math.round(80000 * 0.08) = 6400 VNĐ`. Tổng phải trả: `80000 + 6400 = 86400 VNĐ` | **PASS** |
| **TC_PAY_04** | Thanh toán VietQR động tạo mã QR Napas chính xác | Positive | Đơn hàng chọn hình thức chuyển khoản (`maPt = 2`) | 1. Chọn thanh toán chuyển khoản VietQR<br>2. Bấm Thanh toán | `maPt = 2`<br>`tongPhaiTra = 120000` | Tạo hóa đơn ở trạng thái chờ (`trang_thai_thanh_toan = 0`), hiển thị mã QR động chứa đúng mã đơn và số tiền 120.000 VNĐ | **PASS** |
| **TC_PAY_05** | Tự động tích lũy điểm CRM sau khi hoàn tất hóa đơn (10.000đ = 1 điểm) | Positive | Đơn hàng có gắn khách hàng thành viên `KH001` | 1. Thanh toán hoàn tất hóa đơn giá trị sau giảm 150.000 VNĐ | `maKh = "KH001"`<br>`billBeforeTax = 150000` | Khách hàng được cộng tự động `diemCong = 150000 / 10000 = 15 điểm` vào CSDL | **PASS** |

---

## 3. PHÂN HỆ QUẢN TRỊ THỰC ĐƠN SẢN PHẨM & KÍCH CỠ (PRODUCT & SIZE CRUD)

| Test Case ID | Tên ca kiểm thử | Loại test | Tiền điều kiện | Các bước thực hiện | Dữ liệu đầu vào (Test Data) | Kết quả mong đợi | Trạng thái |
| :---: | :--- | :---: | :--- | :--- | :--- | :--- | :---: |
| **TC_PROD_01** | Thêm mới món nước thành công với cấu hình kích cỡ và giá bán hợp lệ | Positive | Đang ở form `/admin/sanpham?action=create` | 1. Nhập tên sản phẩm, chọn danh mục<br>2. Tick chọn Size M kèm giá bán 35.000đ<br>3. Bấm Lưu sản phẩm | `tenSp = "Trà Oolong Sữa Dừa"`<br>`maDm = "DM01"`<br>`size_active_1 = "on"`<br>`size_price_1 = 35000` | Thêm mới thành công vào `SAN_PHAM` và `SAN_PHAM_KICH_CO`, chuyển hướng về danh sách món với thông báo thành công | **PASS** |
| **TC_PROD_02** | Báo lỗi khi thêm món nước mới mà không cấu hình kích cỡ và giá bán | Negative / BVA | Đang ở form `/admin/sanpham?action=create` | 1. Nhập tên sản phẩm và chọn danh mục<br>2. Không tick chọn bất kỳ kích cỡ nào (`selectedSizes` rỗng)<br>3. Bấm Lưu sản phẩm | `tenSp = "Trà Xanh Thái"`<br>`maDm = "DM01"`<br>Không chọn size nào | Hệ thống chặn lại, báo lỗi: "Sản phẩm mới bắt buộc phải cấu hình tối thiểu một kích cỡ và giá bán đi kèm!" | **PASS** |
| **TC_PROD_03** | Báo lỗi khi để trống Tên đồ uống hoặc Danh mục khi tạo sản phẩm | Negative | Đang ở form `/admin/sanpham?action=create` | 1. Để trống `tenSp` hoặc `maDm`<br>2. Bấm Lưu sản phẩm | `tenSp = ""` hoặc `maDm = ""` | Hệ thống báo lỗi: "Vui lòng nhập đầy đủ Tên đồ uống và Nhóm danh mục!" | **PASS** |
| **TC_PROD_04** | Soft Delete sản phẩm đã từng phát sinh hóa đơn bán lẻ | Positive | Sản phẩm `SP001` đã có trong bảng `CHI_TIET_DON_HANG` | 1. Truy cập `/admin/sanpham?action=delete&id=SP001`<br>2. Xác nhận xóa | `id = "SP001"` | Hệ thống thực hiện Soft Delete (cập nhật `trang_thai = 0` trong `SAN_PHAM`), ghi log kiểm toán, bảo toàn toàn vẹn dữ liệu lịch sử kế toán | **PASS** |
| **TC_PROD_05** | Hard Delete sản phẩm chưa từng phát sinh hóa đơn | Positive | Sản phẩm `SP999` vừa tạo thử nghiệm, chưa có đơn hàng nào | 1. Truy cập `/admin/sanpham?action=delete&id=SP999`<br>2. Xác nhận xóa | `id = "SP999"` | Hệ thống xóa hoàn toàn (Hard Delete) khỏi cả hai bảng `SAN_PHAM_KICH_CO` và `SAN_PHAM`, ghi log kiểm toán thành công | **PASS** |

---

## 4. PHÂN HỆ KHUYẾN MÃI & TIÊU ĐIỂM CRM (VOUCHER & CRM LOYALTY)

| Test Case ID | Tên ca kiểm thử | Loại test | Tiền điều kiện | Các bước thực hiện | Dữ liệu đầu vào (Test Data) | Kết quả mong đợi | Trạng thái |
| :---: | :--- | :---: | :--- | :--- | :--- | :--- | :---: |
| **TC_VOUCHER_01** | Áp dụng Voucher giảm theo % bị chặn bởi mức giảm tối đa (Trần giảm) | Positive / BVA | Đơn hàng 500.000 VNĐ, có mã `SALE20` (giảm 20%, tối đa 50.000 VNĐ) | 1. Gọi API `/pos/apply-voucher`<br>2. Truyền mã `SALE20` và tổng tiền hàng | `code = "SALE20"`<br>`tongTienHang = 500000`<br>(20% x 500k = 100k > max 50k) | Trả về JSON hợp lệ, số tiền giảm chốt chính xác bằng trần `discount = 50000 VNĐ` | **PASS** |
| **TC_VOUCHER_02** | Báo lỗi khi tổng tiền hàng chưa đạt giá trị đơn tối thiểu của Voucher | Negative / BVA | Voucher `GIAM30K` yêu cầu đơn tối thiểu 100.000 VNĐ | 1. Áp mã `GIAM30K` cho đơn hàng 70.000 VNĐ | `code = "GIAM30K"`<br>`tongTienHang = 70000`<br>`donToiThieu = 100000` | Trả về JSON lỗi: "Đơn hàng chưa đạt giá trị tối thiểu 100000 đ!" | **PASS** |
| **TC_VOUCHER_03** | Đổi điểm tích lũy CRM sang tiền mặt trừ đơn (1 điểm = 1.000 VNĐ) | Positive | Khách hàng có 50 điểm trong tài khoản | 1. Nhập số điểm muốn dùng: 30 điểm<br>2. Áp dụng vào hóa đơn 60.000 VNĐ | `diemSuDung = 30`<br>`tongTienHang = 60000` | Khấu trừ đúng `tienTruDiem = 30000 VNĐ`, số tiền còn lại sau trừ điểm là `30000 VNĐ`, điểm dư còn 20 | **PASS** |

---

## 5. PHÂN HỆ ĐẶT HÀNG TRỰC TUYẾN CLICK & COLLECT (PORTAL CHECKOUT)

| Test Case ID | Tên ca kiểm thử | Loại test | Tiền điều kiện | Các bước thực hiện | Dữ liệu đầu vào (Test Data) | Kết quả mong đợi | Trạng thái |
| :---: | :--- | :---: | :--- | :--- | :--- | :--- | :---: |
| **TC_CART_01** | Đặt hàng Click & Collect với giờ hẹn lấy nước hợp lệ (>= 15 phút) | Positive / BVA | Khách hàng đang ở màn hình `/checkout` có giỏ hàng hợp lệ | 1. Chọn thời gian hẹn đến lấy nước cách hiện tại 25 phút<br>2. Bấm Xác nhận đặt hàng | `thoiGianHenLay = now + 25 phút` | Đơn hàng tạo thành công (`loai_don_hang = 3`), trạng thái `0` (Chờ xác nhận), đồng bộ sang màn hình POS thu ngân `/pos/nhandon` | **PASS** |
| **TC_CART_02** | Báo lỗi khi giờ hẹn lấy nước quá gấp (< 15 phút) | Negative / BVA | Khách hàng đang ở màn hình `/checkout` | 1. Chọn thời gian hẹn lấy cách hiện tại chỉ 5 phút<br>2. Bấm Đặt hàng | `thoiGianHenLay = now + 5 phút` | Hệ thống chặn lại, hiển thị cảnh báo: "Loi: Thoi gian hen lay phai cach thoi diem hien tai toi thieu 15 phut!" | **PASS** |
| **TC_CART_03** | Ngăn chặn hành vi spam đặt trùng đơn hàng (Double Checkout Protection) | Negative / Security | Đơn hàng vừa nhấn Đặt hàng thành công | 1. Nhấn nút Back trên trình duyệt và bấm Đặt hàng lại | `checkoutToken` đã bị invalidate khỏi Session | Phát hiện token không hợp lệ, redirect về `/cart?msg=double_checkout_detected`, không sinh đơn rác | **PASS** |
| **TC_CART_04** | Luồng Mua ngay (Buy Now) xử lý độc lập và dọn sạch Session | Positive | Khách bấm "Mua ngay" trên trang chi tiết sản phẩm | 1. Mua ngay 1 ly trà sữa<br>2. Đặt hàng thành công | `buyNowItem` trong session | Tạo đơn hàng thành công, dọn dẹp `session.removeAttribute("buyNowItem")`, bảo toàn nguyên vẹn giỏ hàng cũ trong CSDL | **PASS** |
