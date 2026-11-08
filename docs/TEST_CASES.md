# BỘ KỊCH BẢN KIỂM THỬ CHI TIẾT (TEST CASES)
### Dự án: TEA POS SYSTEM - O2O & CLICK & COLLECT ENTERPRISE
> **Môn học:** SOF309 - Kiểm thử nâng cao cùng AI (FPT Polytechnic)  
> **Sinh viên:** Phạm Thiện Quang | **MSSV:** TV00472 | **Lớp:** SD21307  
> **Giảng viên hướng dẫn:** ThS. Trần Quang Bình (binhtq7 - Thầy Mười Giáo)  

---

## 1. MODULE XÁC THỰC & PHÂN QUYỀN (AUTHENTICATION)

| Test Case ID | Tên ca kiểm thử | Loại test | Tiền điều kiện | Các bước thực hiện | Dữ liệu đầu vào (Test Data) | Kết quả mong đợi | Trạng thái |
| :---: | :--- | :---: | :--- | :--- | :--- | :--- | :---: |
| **TC_AUTH_01** | Đăng nhập Admin thành công với tài khoản hợp lệ | Positive | Tài khoản đã tồn tại trong DB | 1. Nhập username Admin<br>2. Nhập password đúng<br>3. Bấm Đăng nhập | `user = "admin"`<br>`pass = "Admin@123"` | Xác thực thành công, trả về quyền `ADMIN`, chuyển hướng trang Dashboard | **PASS** |
| **TC_AUTH_02** | Đăng nhập thất bại khi nhập sai mật khẩu | Negative | Tài khoản `admin` tồn tại | 1. Nhập username đúng<br>2. Nhập password sai<br>3. Bấm Đăng nhập | `user = "admin"`<br>`pass = "WrongPass999"` | Đăng nhập thất bại, trả về `false`, hiển thị thông báo "Tài khoản hoặc mật khẩu không đúng" | **PASS** |
| **TC_AUTH_03** | Báo lỗi khi để trống Tên đăng nhập | Negative | Đang ở form đăng nhập | 1. Để trống username<br>2. Nhập mật khẩu<br>3. Bấm Đăng nhập | `user = ""` (chuỗi rỗng)<br>`pass = "Admin@123"` | Hệ thống ném ngoại lệ `ValidationException` với thông báo "Tên đăng nhập không được để trống" | **PASS** |
| **TC_AUTH_04** | Báo lỗi khi để trống Mật khẩu | Negative | Đang ở form đăng nhập | 1. Nhập username<br>2. Để trống password<br>3. Bấm Đăng nhập | `user = "admin"`<br>`pass = ""` | Hệ thống ném ngoại lệ `ValidationException` với thông báo "Mật khẩu không được để trống" | **PASS** |
| **TC_AUTH_05** | Đăng ký thành viên mới với thông tin hợp lệ | Positive | SĐT và email chưa tồn tại | 1. Điền form đăng ký đầy đủ<br>2. Bấm Xác nhận đăng ký | `hoTen = "Phạm Thiện Quang"`<br>`sdt = "0987654321"`<br>`pass = "P@ssword123"` | Đăng ký thành công, mật khẩu được băm Salted SHA-256 lưu an toàn vào DB | **PASS** |
| **TC_AUTH_06** | Đăng ký thất bại khi trùng lặp Số điện thoại | Negative | SĐT `0987654321` đã đăng ký | 1. Điền form với SĐT đã có<br>2. Bấm Xác nhận đăng ký | `sdt = "0987654321"` | Ném ngoại lệ `ValidationException`: "Số điện thoại đã được đăng ký trong hệ thống" | **PASS** |

---

## 2. MODULE THANH TOÁN & HẠN MỨC GIAO DỊCH (PAYMENT - BÁM SÁT LAB 1)

| Test Case ID | Tên ca kiểm thử | Loại test | Tiền điều kiện | Các bước thực hiện | Dữ liệu đầu vào (Test Data) | Kết quả mong đợi | Trạng thái |
| :---: | :--- | :---: | :--- | :--- | :--- | :--- | :---: |
| **TC_PAY_01** | Thanh toán đơn hàng bình thường (<= 1.000.000 VNĐ) | Positive | Đơn hàng trạng thái `PENDING` | 1. Tạo hóa đơn 3 ly trà sữa 165.000 VNĐ<br>2. Chọn thanh toán tiền mặt/QR | `amount = 165,000 VNĐ`<br>`otp = null` | Thanh toán thành công, trạng thái hóa đơn chuyển sang `PAID` | **PASS** |
| **TC_PAY_02** | Thanh toán đơn > 1.000.000 VNĐ không OTP sẽ văng lỗi (Bài toán Lab 1) | Negative | Đơn hàng trạng thái `PENDING` | 1. Đặt tiệc trà sữa giá trị 1.500.000 VNĐ<br>2. Gọi hàm thanh toán không cung cấp OTP | `amount = 1,500,000 VNĐ`<br>`otp = null` | Hệ thống chặn giao dịch, ném `PaymentFailedException`, chuyển hóa đơn sang `FAILED` | **PASS** |
| **TC_PAY_03** | Thanh toán đơn > 1.000.000 VNĐ có xác thực OTP hợp lệ (Sau Debug) | Positive | Đơn hàng trạng thái `PENDING` | 1. Tạo đơn hàng 1.500.000 VNĐ<br>2. Nhập mã OTP quản lý `123456` | `amount = 1,500,000 VNĐ`<br>`otp = "123456"` | Xác thực thành công, nạp thanh toán thành công `PAID`, in phiếu thu | **PASS** |
| **TC_PAY_04** | Xử lý đơn hàng 0 VNĐ (Áp voucher 100% hoặc đổi trọn điểm) | Positive | Hóa đơn có mã giảm giá lớn | 1. Áp dụng voucher hoặc đổi điểm đủ giá trị đơn<br>2. Bấm Thanh toán | `finalAmount = 0 VNĐ` | Hệ thống tự động hoàn tất đơn (`PAID`), không yêu cầu sinh mã VietQR | **PASS** |
| **TC_PAY_05** | Bắt lỗi thanh toán với số tiền âm | Negative | Khởi tạo đơn thanh toán | 1. Nhập số tiền thanh toán âm<br>2. Bấm xác nhận | `amount = -50,000 VNĐ` | Ném ngoại lệ `ValidationException`: "Số tiền thanh toán phải lớn hơn 0" | **PASS** |
| **TC_PAY_06** | Tính tiền thừa cho khách tại quầy POS chính xác | Positive | Đơn hàng giá trị 85.000 VNĐ | 1. Thu ngân nhập số tiền khách đưa 100.000 VNĐ<br>2. Hệ thống tính tiền thối | `tongTien = 85,000`<br>`tienKhachDua = 100,000` | Tiền thừa tính ra chính xác `15,000 VNĐ`, cho phép in bill 80mm | **PASS** |

---

## 3. MODULE QUẢN LÝ SẢN PHẨM / MENU MÓN NƯỚC (PRODUCT CRUD)

| Test Case ID | Tên ca kiểm thử | Loại test | Tiền điều kiện | Các bước thực hiện | Dữ liệu đầu vào (Test Data) | Kết quả mong đợi | Trạng thái |
| :---: | :--- | :---: | :--- | :--- | :--- | :--- | :---: |
| **TC_PROD_01** | Thêm mới món nước hợp lệ vào Menu | Positive | Mã món chưa có trong hệ thống | 1. Nhập tên món, chọn danh mục<br>2. Nhập giá bán hợp lệ<br>3. Bấm Thêm | `ten = "Trà Sữa Oolong Nướng"`<br>`gia = 35,000 VNĐ`<br>`idDanhMuc = 1` | Thêm mới thành công, món nước xuất hiện trên bảng danh sách | **PASS** |
| **TC_PROD_02** | Báo lỗi khi giá bán sản phẩm <= 0 VNĐ (Biên giá trị) | Negative | Form thêm món mới | 1. Nhập tên món hợp lệ<br>2. Nhập giá bán âm hoặc bằng 0<br>3. Bấm Thêm | `ten = "Trà Xanh Thái Xanh"`<br>`gia = -10,000 VNĐ` | Ném ngoại lệ `ValidationException`: "Giá bán sản phẩm phải lớn hơn 0 VNĐ" | **PASS** |
| **TC_PROD_03** | Báo lỗi khi tên món nước dưới 3 ký tự (Biên độ dài) | Negative | Form thêm món mới | 1. Nhập tên món quá ngắn (1-2 ký tự)<br>2. Bấm Thêm | `ten = "Tr"`<br>`gia = 30,000 VNĐ` | Ném ngoại lệ `ValidationException`: "Tên sản phẩm phải từ 3 ký tự trở lên" | **PASS** |
| **TC_PROD_04** | Tìm kiếm món nước theo từ khóa Tiếng Việt không dấu | Positive | Đã có món "Trà Sữa Trân Châu" | 1. Nhập từ khóa không dấu "tra sua"<br>2. Bấm Tìm kiếm | `keyword = "tra sua"` | Trả về chính xác các món trà sữa nhờ cơ chế `Vietnamese_CI_AI` | **PASS** |
| **TC_PROD_05** | Cập nhật thông tin món nước thành công | Positive | Sản phẩm `SP01` tồn tại | 1. Thay đổi giá bán món `SP01`<br>2. Bấm Cập nhật | `id = "SP01"`<br>`giaMoi = 40,000 VNĐ` | Dữ liệu giá mới được lưu vào CSDL, hiển thị cập nhật ngay lập tức | **PASS** |
| **TC_PROD_06** | Ngừng kinh doanh (Soft Delete) món nước | Positive | Món `SP02` đang kinh doanh | 1. Chọn món `SP02`<br>2. Bấm Ngừng kinh doanh | `id = "SP02"` | Trạng thái chuyển sang `false` (Ẩn khỏi menu quầy POS và Portal) | **PASS** |

---

## 4. MODULE KHUYẾN MÃI & VOUCHER GIẢM GIÁ (VOUCHER & LOYALTY)

| Test Case ID | Tên ca kiểm thử | Loại test | Tiền điều kiện | Các bước thực hiện | Dữ liệu đầu vào (Test Data) | Kết quả mong đợi | Trạng thái |
| :---: | :--- | :---: | :--- | :--- | :--- | :--- | :---: |
| **TC_VOUCHER_01** | Áp dụng Voucher giảm tiền mặt thành công | Positive | Đơn hàng 100.000 VNĐ | 1. Áp mã voucher giảm 20.000 VNĐ<br>2. Kiểm tra tổng thanh toán | `ma = "TEAPOS20K"`<br>`donToiThieu = 80,000` | Tổng tiền giảm còn `80,000 VNĐ`, áp dụng thành công | **PASS** |
| **TC_VOUCHER_02** | Báo lỗi khi đơn hàng chưa đạt giá trị tối thiểu của Voucher | Negative | Đơn hàng 50.000 VNĐ | 1. Áp mã voucher yêu cầu đơn từ 80.000 VNĐ | `ma = "TEAPOS20K"`<br>`tongTien = 50,000` | Ném lỗi: "Đơn hàng chưa đạt giá trị tối thiểu 80.000 VNĐ để dùng voucher" | **PASS** |
| **TC_VOUCHER_03** | Áp dụng Voucher giảm theo % nhưng bị chặn mức giảm tối đa | Positive | Đơn hàng 500.000 VNĐ | 1. Áp mã giảm 20% tối đa 50.000 VNĐ | `ma = "SALE20"` (20% của 500k = 100k > max 50k) | Hệ thống chỉ giảm chính xác `50,000 VNĐ`, tổng tiền là `450,000 VNĐ` | **PASS** |
| **TC_VOUCHER_04** | Đổi điểm tích lũy thành viên CRM thành tiền mặt trừ đơn | Positive | Khách hàng có 30 điểm | 1. Nhập số điểm muốn đổi là 20 điểm | `diemDoi = 20`<br>`tongTien = 60,000` | Trừ thẳng `20,000 VNĐ`, số tiền cần trả còn `40,000 VNĐ`, số dư điểm còn 10 | **PASS** |

---

## 5. MODULE GIỎ HÀNG & HẸN GIỜ LẤY NƯỚC (CLICK & COLLECT PORTAL)

| Test Case ID | Tên ca kiểm thử | Loại test | Tiền điều kiện | Các bước thực hiện | Dữ liệu đầu vào (Test Data) | Kết quả mong đợi | Trạng thái |
| :---: | :--- | :---: | :--- | :--- | :--- | :--- | :---: |
| **TC_CART_01** | Thêm món nước kèm Topping vào Giỏ hàng | Positive | Khách đang xem menu | 1. Chọn Trà Sữa Size L (+12k)<br>2. Thêm Topping Cheese foam (+10k)<br>3. Bấm Thêm vào giỏ | Giá gốc: 30k + 12k + 10k = `52,000 VNĐ` | Món nước được thêm vào giỏ hàng với đúng giá `52,000 VNĐ` và chi tiết topping | **PASS** |
| **TC_CART_02** | Chọn giờ hẹn lấy nước hợp lệ (Sau 20 phút) | Positive | Đang ở màn hình checkout | 1. Chọn thời gian hẹn lấy cách hiện tại 20 phút | `thoiGianHen = now + 20 phút` | Hệ thống xác nhận giờ hẹn hợp lệ, chuyển sang bước thanh toán | **PASS** |
| **TC_CART_03** | Báo lỗi khi giờ hẹn lấy nước quá gấp (< 15 phút) | Negative | Đang ở màn hình checkout | 1. Chọn giờ hẹn lấy cách hiện tại 5 phút | `thoiGianHen = now + 5 phút` | Hệ thống cảnh báo: "Vui lòng hẹn giờ lấy trước tối thiểu 15 phút để quầy pha chế" | **PASS** |
| **TC_CART_04** | Đồng bộ đơn hàng online sang màn hình Thu ngân POS | Positive | Khách đã thanh toán đơn online | 1. Hoàn tất đơn hàng trên Portal | Mã đơn `ORD_ONLINE_01` | Màn hình POS thu ngân tự động cập nhật đơn mới ở trạng thái `PREPARING` | **PASS** |
