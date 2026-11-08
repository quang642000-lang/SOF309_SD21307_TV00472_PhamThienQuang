-- =========================================================================
-- HỆ THỐNG QUẢN LÝ VÀ BÁN HÀNG TRÀ SỮA TEA POS ENTERPRISE SYSTEM
-- BẢN THIẾT KẾ CƠ SỞ DỮ LIỆU ĐẦY ĐỦ VÀ HOÀN CHỈNH NHẤT - CHUẨN ĐỒ ÁN TỐT NGHIỆP
-- Đội ngũ CodeDevSquad 2026 - Tối ưu hóa hiệu năng, bảo mật và tính toàn vẹn
-- Khớp 100% với kiến trúc Java Enterprise (Servlets, HikariCP, REST API, SePay)
-- =========================================================================
USE master;
GO

-- 1. Khởi tạo Database tươi mới an toàn
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'QuanLyQuanCafehetcuu1')
BEGIN
    CREATE DATABASE QuanLyQuanCafehetcuu1;
END;
GO

USE QuanLyQuanCafehetcuu1;
GO

-- =========================================================================
-- I. DỌN DẸP BẢNG CŨ THEO THỨ TỰ TRÁNH LỖI RÀNG BUỘC KHÓA NGOẠI (DROP TABLES)
-- =========================================================================
IF OBJECT_ID('dbo.THONG_KE_NGAY', 'U') IS NOT NULL DROP TABLE dbo.THONG_KE_NGAY;
IF OBJECT_ID('dbo.NHAT_KY_HOAT_DONG', 'U') IS NOT NULL DROP TABLE dbo.NHAT_KY_HOAT_DONG;
IF OBJECT_ID('dbo.CHI_TIET_TOPPING', 'U') IS NOT NULL DROP TABLE dbo.CHI_TIET_TOPPING;
IF OBJECT_ID('dbo.CHI_TIET_DON_HANG', 'U') IS NOT NULL DROP TABLE dbo.CHI_TIET_DON_HANG;
IF OBJECT_ID('dbo.DON_HANG', 'U') IS NOT NULL DROP TABLE dbo.DON_HANG;
IF OBJECT_ID('dbo.CHI_TIET_TOPPING_GIO_HANG', 'U') IS NOT NULL DROP TABLE dbo.CHI_TIET_TOPPING_GIO_HANG;
IF OBJECT_ID('dbo.CHI_TIET_GIO_HANG', 'U') IS NOT NULL DROP TABLE dbo.CHI_TIET_GIO_HANG;
IF OBJECT_ID('dbo.GIO_HANG', 'U') IS NOT NULL DROP TABLE dbo.GIO_HANG;
IF OBJECT_ID('dbo.KHACH_HANG', 'U') IS NOT NULL DROP TABLE dbo.KHACH_HANG;
IF OBJECT_ID('dbo.NHAN_VIEN', 'U') IS NOT NULL DROP TABLE dbo.NHAN_VIEN;
IF OBJECT_ID('dbo.TOPPING', 'U') IS NOT NULL DROP TABLE dbo.TOPPING;
IF OBJECT_ID('dbo.SAN_PHAM_KICH_CO', 'U') IS NOT NULL DROP TABLE dbo.SAN_PHAM_KICH_CO;
IF OBJECT_ID('dbo.SAN_PHAM', 'U') IS NOT NULL DROP TABLE dbo.SAN_PHAM;
IF OBJECT_ID('dbo.DANH_MUC', 'U') IS NOT NULL DROP TABLE dbo.DANH_MUC;
IF OBJECT_ID('dbo.PHUONG_THUC_THANH_TOAN', 'U') IS NOT NULL DROP TABLE dbo.PHUONG_THUC_THANH_TOAN;
IF OBJECT_ID('dbo.KICH_CO', 'U') IS NOT NULL DROP TABLE dbo.KICH_CO;
IF OBJECT_ID('dbo.HANG_THANH_VIEN', 'U') IS NOT NULL DROP TABLE dbo.HANG_THANH_VIEN;
IF OBJECT_ID('dbo.VAI_TRO', 'U') IS NOT NULL DROP TABLE dbo.VAI_TRO;
IF OBJECT_ID('dbo.CHUONG_TRINH_KHUYEN_MAI', 'U') IS NOT NULL DROP TABLE dbo.CHUONG_TRINH_KHUYEN_MAI;
GO

-- =========================================================================
-- II. KHỞI TẠO CÁC CHUỖI TỰ TĂNG (SEQUENCES) VỚI CẤU HÌNH NO CACHE TRÁNH NHẢY VỌT ID
-- =========================================================================
IF OBJECT_ID('dbo.seq_NhanVien', 'SO') IS NOT NULL DROP SEQUENCE dbo.seq_NhanVien;
IF OBJECT_ID('dbo.seq_KhachHang', 'SO') IS NOT NULL DROP SEQUENCE dbo.seq_KhachHang;
IF OBJECT_ID('dbo.seq_DonHang', 'SO') IS NOT NULL DROP SEQUENCE dbo.seq_DonHang;
IF OBJECT_ID('dbo.seq_SanPham', 'SO') IS NOT NULL DROP SEQUENCE dbo.seq_SanPham;
IF OBJECT_ID('dbo.seq_DanhMuc', 'SO') IS NOT NULL DROP SEQUENCE dbo.seq_DanhMuc;
IF OBJECT_ID('dbo.seq_Voucher', 'SO') IS NOT NULL DROP SEQUENCE dbo.seq_Voucher;
IF OBJECT_ID('dbo.seq_Topping', 'SO') IS NOT NULL DROP SEQUENCE dbo.seq_Topping;
GO

CREATE SEQUENCE seq_NhanVien START WITH 1 INCREMENT BY 1 NO CACHE;
CREATE SEQUENCE seq_KhachHang START WITH 1 INCREMENT BY 1 NO CACHE;
CREATE SEQUENCE seq_DonHang START WITH 1 INCREMENT BY 1 NO CACHE;
CREATE SEQUENCE seq_SanPham START WITH 1 INCREMENT BY 1 NO CACHE;
CREATE SEQUENCE seq_DanhMuc START WITH 1 INCREMENT BY 1 NO CACHE;
CREATE SEQUENCE seq_Voucher START WITH 1 INCREMENT BY 1 NO CACHE;
CREATE SEQUENCE seq_Topping START WITH 1 INCREMENT BY 1 NO CACHE;
GO

-- =========================================================================
-- III. THIẾT KẾ CẤU TRÚC 19 BẢNG VẬT LÝ CHUẨN DOANH NGHIỆP
-- =========================================================================

-- 1. Bảng Vai Trò Nhân Sự (Role ACL)
CREATE TABLE VAI_TRO (
    ma_vt INT PRIMARY KEY,
    ten_vt NVARCHAR(50) NOT NULL,
    mo_ta NVARCHAR(255) NULL
);

-- 2. Bảng Phân Hạng Thành Viên CRM (Loyalty Tiers)
CREATE TABLE HANG_THANH_VIEN (
    ma_hang INT PRIMARY KEY,
    ten_hang NVARCHAR(50) NOT NULL,
    diem_toi_thieu INT NOT NULL DEFAULT 0,
    phan_tram_giam INT DEFAULT 0,
    mo_ta NVARCHAR(255) NULL
);

-- 3. Bảng Master Kích Cỡ Đồ Uống
CREATE TABLE KICH_CO (
    ma_size INT PRIMARY KEY,
    ten_size NVARCHAR(50) NOT NULL UNIQUE,
    dung_tich_ml INT NULL,
    thu_tu_hien_thi INT NOT NULL DEFAULT 1,
    he_so_gia FLOAT DEFAULT 1.0
);

-- 4. Bảng Phương Thức Thanh Toán (1: Tiền mặt, 2: Chuyển khoản VietQR)
CREATE TABLE PHUONG_THUC_THANH_TOAN (
    ma_pt INT PRIMARY KEY,
    ten_pt NVARCHAR(100) NOT NULL,
    mo_ta NVARCHAR(255) NULL,
    trang_thai BIT NOT NULL DEFAULT 1
);

-- 5. Bảng Nhóm Danh Mục Đồ Uống (Category)
CREATE TABLE DANH_MUC (
    ma_dm VARCHAR(20) PRIMARY KEY,
    ten_dm NVARCHAR(100) NOT NULL UNIQUE,
    hinh_anh VARCHAR(500) NULL,
    thu_tu_hien_thi INT NOT NULL DEFAULT 0,
    trang_thai BIT NOT NULL DEFAULT 1,
    thoi_gian_tao DATETIME NOT NULL DEFAULT GETDATE(),
    thoi_gian_cap_nhat DATETIME NULL
);

-- 6. Bảng Thực Đơn Sản Phẩm Mẹ (Product)
CREATE TABLE SAN_PHAM (
    ma_sp VARCHAR(20) PRIMARY KEY,
    ma_dm VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES DANH_MUC(ma_dm),
    ten_sp NVARCHAR(200) NOT NULL UNIQUE,
    mo_ta NVARCHAR(MAX) NULL,
    hinh_anh VARCHAR(500) NULL,
    cho_phep_doi_da BIT NOT NULL DEFAULT 1,
    cho_phep_doi_duong BIT NOT NULL DEFAULT 1,
    is_new BIT NOT NULL DEFAULT 0,
    is_bestseller BIT NOT NULL DEFAULT 0,
    trang_thai BIT NOT NULL DEFAULT 1,
    thoi_gian_tao DATETIME NOT NULL DEFAULT GETDATE(),
    thoi_gian_cap_nhat DATETIME NULL,
    cho_phep_topping BIT NOT NULL DEFAULT 1,
    thu_tu_hien_thi INT NOT NULL DEFAULT 0
);

-- 7. Bảng Biến Thể Kích Cỡ & Giá Bán Sản Phẩm (Product Sizes Matrix)
CREATE TABLE SAN_PHAM_KICH_CO (
    ma_sp VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES SAN_PHAM(ma_sp) ON DELETE CASCADE,
    ma_size INT NOT NULL FOREIGN KEY REFERENCES KICH_CO(ma_size),
    gia_ban INT NOT NULL CHECK (gia_ban >= 0),
    dinh_luong NVARCHAR(100) NULL,
    trang_thai BIT NOT NULL DEFAULT 1,
    PRIMARY KEY (ma_sp, ma_size)
);

-- 8. Bảng Topping Ăn Kèm
CREATE TABLE TOPPING (
    ma_tp VARCHAR(20) PRIMARY KEY,
    ten_tp NVARCHAR(150) NOT NULL UNIQUE,
    dinh_luong NVARCHAR(100) NULL,
    gia_ban INT NOT NULL CHECK (gia_ban >= 0),
    thu_tu_hien_thi INT NOT NULL DEFAULT 0,
    trang_thai BIT NOT NULL DEFAULT 1,
    hinh_anh VARCHAR(500) NULL
);

-- 9. Bảng Hồ Sơ Nhân Viên Hệ Thống
CREATE TABLE NHAN_VIEN (
    ma_nv VARCHAR(20) PRIMARY KEY,
    ma_vt INT NOT NULL FOREIGN KEY REFERENCES VAI_TRO(ma_vt),
    ho_ten NVARCHAR(100) NOT NULL,
    so_dien_thoai VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    ten_dang_nhap VARCHAR(50) NOT NULL UNIQUE,
    mat_khau VARCHAR(256) NOT NULL,
    trang_thai BIT NOT NULL DEFAULT 1,
    thoi_gian_tao DATETIME NOT NULL DEFAULT GETDATE(),
    thoi_gian_cap_nhat DATETIME NULL
);

-- 10. Bảng Hồ Sơ Khách Hàng CRM & Tích Điểm Loyalty
CREATE TABLE KHACH_HANG (
    ma_kh VARCHAR(20) PRIMARY KEY,
    ma_hang INT NOT NULL FOREIGN KEY REFERENCES HANG_THANH_VIEN(ma_hang) DEFAULT 1,
    so_dien_thoai VARCHAR(20) NOT NULL UNIQUE,
    ten_kh NVARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mat_khau VARCHAR(256) NULL,
    ngay_sinh DATE NULL,
    gioi_tinh NVARCHAR(20) NULL,
    dia_chi_lien_he NVARCHAR(500) NULL,
    hinh_anh_url VARCHAR(500) NULL,
    diem_tich_luy INT NOT NULL DEFAULT 0 CHECK (diem_tich_luy >= 0),
    trang_thai BIT NOT NULL DEFAULT 1,
    thoi_gian_tao DATETIME NOT NULL DEFAULT GETDATE(),
    thoi_gian_cap_nhat DATETIME NULL
);

-- 11. Bảng Giỏ Hàng Tổng Quát (O2O Click & Collect Sessions)
CREATE TABLE GIO_HANG (
    ma_gh INT IDENTITY(1,1) PRIMARY KEY,
    ma_kh VARCHAR(20) NOT NULL UNIQUE FOREIGN KEY REFERENCES KHACH_HANG(ma_kh),
    thoi_gian_tao DATETIME NOT NULL DEFAULT GETDATE(),
    thoi_gian_cap_nhat DATETIME NULL
);

-- 12. Bảng Chi Tiết Sản Phẩm Trong Giỏ Hàng
CREATE TABLE CHI_TIET_GIO_HANG (
    ma_ctgh BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_gh INT NOT NULL FOREIGN KEY REFERENCES GIO_HANG(ma_gh),
    ma_sp VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES SAN_PHAM(ma_sp),
    ma_size INT NOT NULL FOREIGN KEY REFERENCES KICH_CO(ma_size),
    so_luong INT NOT NULL CHECK (so_luong > 0),
    muc_da VARCHAR(20) NOT NULL DEFAULT '100% Đá',
    muc_duong VARCHAR(20) NOT NULL DEFAULT '100% Đường',
    ghi_chu_mon NVARCHAR(255) NULL,
    is_chon_mua BIT NOT NULL DEFAULT 1,
    thoi_gian_them DATETIME NOT NULL DEFAULT GETDATE()
);

-- 13. Bảng Chi Tiết Topping Trong Giỏ Hàng
CREATE TABLE CHI_TIET_TOPPING_GIO_HANG (
    ma_ctgh BIGINT NOT NULL FOREIGN KEY REFERENCES CHI_TIET_GIO_HANG(ma_ctgh) ON DELETE CASCADE,
    ma_tp VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES TOPPING(ma_tp),
    so_luong_tp INT NOT NULL CHECK (so_luong_tp > 0) DEFAULT 1,
    PRIMARY KEY (ma_ctgh, ma_tp)
);

-- 14. Bảng Chương Trình Khuyến Mãi / Voucher
CREATE TABLE CHUONG_TRINH_KHUYEN_MAI (
    ma_km VARCHAR(20) PRIMARY KEY,
    ten_km NVARCHAR(200) NOT NULL,
    ma_code VARCHAR(50) NOT NULL UNIQUE,
    mo_ta_dieu_kien NVARCHAR(MAX) NULL,
    hinh_anh_url VARCHAR(500) NULL,
    loai_giam INT NOT NULL CHECK (loai_giam IN (1, 2)), -- 1: Giảm cố định theo VNĐ, 2: Giảm theo tỷ lệ %
    gia_tri_giam INT NOT NULL CHECK (gia_tri_giam > 0),
    giam_toi_da INT NOT NULL DEFAULT 0,
    don_toi_thieu INT NOT NULL DEFAULT 0,
    is_public BIT NOT NULL DEFAULT 1,
    so_luong INT NOT NULL DEFAULT 0,
    ngay_bat_dau DATETIME NOT NULL,
    ngay_ket_thuc DATETIME NOT NULL,
    trang_thai BIT NOT NULL DEFAULT 1,
    so_luot_dung_ca_nhan INT NOT NULL DEFAULT 0,
    hang_ap_dung INT NOT NULL DEFAULT 1 FOREIGN KEY REFERENCES HANG_THANH_VIEN(ma_hang),
    loai_voucher INT NOT NULL DEFAULT 1 -- 1: Điện tử, 2: Voucher giấy
);

-- 15. Bảng Đơn Hàng Giao Dịch
CREATE TABLE DON_HANG (
    ma_dh VARCHAR(50) PRIMARY KEY,
    ma_kh VARCHAR(20) NULL FOREIGN KEY REFERENCES KHACH_HANG(ma_kh),
    ma_nv VARCHAR(20) NULL FOREIGN KEY REFERENCES NHAN_VIEN(ma_nv),
    ma_pt INT NOT NULL FOREIGN KEY REFERENCES PHUONG_THUC_THANH_TOAN(ma_pt),
    ma_km VARCHAR(20) NULL FOREIGN KEY REFERENCES CHUONG_TRINH_KHUYEN_MAI(ma_km),
    loai_don_hang INT NOT NULL DEFAULT 1, -- 1: Tại quầy, 2: Mang đi, 3: Đặt trước Online Click & Collect
    thoi_gian_hen_lay DATETIME NULL,
    tong_tien_hang INT NOT NULL CHECK (tong_tien_hang >= 0),
    tien_giam_gia INT NOT NULL DEFAULT 0 CHECK (tien_giam_gia >= 0),
    diem_su_dung INT NOT NULL DEFAULT 0 CHECK (diem_su_dung >= 0),
    tien_tru_diem INT NOT NULL DEFAULT 0 CHECK (tien_tru_diem >= 0),
    tong_phai_tra INT NOT NULL CHECK (tong_phai_tra >= 0),
    ghi_chu_don NVARCHAR(500) NULL,
    ly_do_huy NVARCHAR(500) NULL,
    trang_thai_thanh_toan INT NOT NULL DEFAULT 0, -- 0: Chưa thanh toán, 1: Đã thanh toán, 2: Đã hoàn tiền
    trang_thai_don INT NOT NULL DEFAULT 0, -- 0: Chờ xác nhận, 1: Đã xác nhận, 2: Đang làm, 3: Chờ lấy, 4: Hoàn thành, 5: Đã hủy
    thoi_gian_tao DATETIME NOT NULL DEFAULT GETDATE(),
    thoi_gian_hoan_thanh DATETIME NULL
);

-- 16. Bảng Chi Tiết Đơn Hàng
CREATE TABLE CHI_TIET_DON_HANG (
    ma_ctdh BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_dh VARCHAR(50) NOT NULL FOREIGN KEY REFERENCES DON_HANG(ma_dh) ON DELETE CASCADE,
    ma_sp VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES SAN_PHAM(ma_sp),
    ma_size INT NOT NULL FOREIGN KEY REFERENCES KICH_CO(ma_size),
    so_luong INT NOT NULL CHECK (so_luong > 0),
    gia_chot INT NOT NULL CHECK (gia_chot >= 0),
    muc_da VARCHAR(20) NOT NULL DEFAULT '100% Đá',
    muc_duong VARCHAR(20) NOT NULL DEFAULT '100% Đường',
    ghi_chu_mon NVARCHAR(255) NULL
);

-- 17. Bảng Chi Tiết Topping Trong Đơn Hàng
CREATE TABLE CHI_TIET_TOPPING (
    ma_ctdh BIGINT NOT NULL FOREIGN KEY REFERENCES CHI_TIET_DON_HANG(ma_ctdh) ON DELETE CASCADE,
    ma_tp VARCHAR(20) NOT NULL FOREIGN KEY REFERENCES TOPPING(ma_tp),
    so_luong INT NOT NULL CHECK (so_luong > 0) DEFAULT 1,
    gia_chot_tp INT NOT NULL CHECK (gia_chot_tp >= 0),
    PRIMARY KEY (ma_ctdh, ma_tp)
);

-- 18. Bảng Nhật Ký Hoạt Động Hệ Thống (Audit Logs)
CREATE TABLE NHAT_KY_HOAT_DONG (
    ma_log BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_nv VARCHAR(20) NULL FOREIGN KEY REFERENCES NHAN_VIEN(ma_nv),
    hanh_dong NVARCHAR(100) NOT NULL,
    bang_tac_dong NVARCHAR(100) NOT NULL,
    du_lieu_cu NVARCHAR(MAX) NULL,
    du_lieu_moi NVARCHAR(MAX) NULL,
    ip_address VARCHAR(50) NULL,
    thoi_gian DATETIME NOT NULL DEFAULT GETDATE()
);

-- 19. Bảng Thống Kê Tổng Hợp Doanh Thu Theo Ngày
CREATE TABLE THONG_KE_NGAY (
    id INT IDENTITY(1,1) PRIMARY KEY,
    ngay DATE NOT NULL UNIQUE,
    so_luong_don INT NOT NULL DEFAULT 0,
    tong_doanh_thu INT NOT NULL DEFAULT 0,
    thoi_gian_tao DATETIME NOT NULL DEFAULT GETDATE()
);
GO

-- =========================================================================
-- IV. STORED PROCEDURES (TẠO MỚI DỮ LIỆU ĐỒNG BỘ VỚI SEQUENCE)
-- =========================================================================

CREATE OR ALTER PROCEDURE sp_ThemDanhMuc
    @ten_dm NVARCHAR(100),
    @hinh_anh VARCHAR(500),
    @thu_tu_hien_thi INT,
    @trang_thai BIT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @ma_dm VARCHAR(20);
    SET @ma_dm = 'DM' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_DanhMuc AS VARCHAR(10)), 5);
    INSERT INTO DANH_MUC (ma_dm, ten_dm, hinh_anh, thu_tu_hien_thi, trang_thai, thoi_gian_tao)
    VALUES (@ma_dm, @ten_dm, @hinh_anh, @thu_tu_hien_thi, @trang_thai, GETDATE());
    SELECT @ma_dm AS ma_dm;
END;
GO

CREATE OR ALTER PROCEDURE sp_ThemSanPham
    @ma_dm VARCHAR(20),
    @ten_sp NVARCHAR(200),
    @mo_ta NVARCHAR(MAX),
    @hinh_anh VARCHAR(500)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @ma_sp VARCHAR(20);
    SET @ma_sp = 'SP' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_SanPham AS VARCHAR(10)), 5);
    INSERT INTO SAN_PHAM (
        ma_sp, ma_dm, ten_sp, mo_ta, hinh_anh,
        cho_phep_doi_da, cho_phep_doi_duong, is_new, is_bestseller,
        trang_thai, thoi_gian_tao, cho_phep_topping, thu_tu_hien_thi
    )
    VALUES (
        @ma_sp, @ma_dm, @ten_sp, @mo_ta, @hinh_anh,
        1, 1, 0, 0, 1, GETDATE(), 1, 0
    );
    SELECT @ma_sp AS ma_sp;
END;
GO

CREATE OR ALTER PROCEDURE sp_ThemVoucher
    @ten_km NVARCHAR(200),
    @ma_code VARCHAR(50),
    @mo_ta NVARCHAR(MAX),
    @hinh_anh VARCHAR(500),
    @loai_giam INT,
    @gia_tri_giam INT,
    @giam_toi_da INT,
    @don_toi_thieu INT,
    @is_public BIT,
    @so_luong INT,
    @ngay_bd DATETIME,
    @ngay_kt DATETIME,
    @trang_status BIT,
    @so_luot_dung_ca_nhan INT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @ma_km VARCHAR(20);
    SET @ma_km = 'KM' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_Voucher AS VARCHAR(10)), 5);
    INSERT INTO CHUONG_TRINH_KHUYEN_MAI (
        ma_km, ten_km, ma_code, mo_ta_dieu_kien, hinh_anh_url,
        loai_giam, gia_tri_giam, giam_toi_da, don_toi_thieu,
        is_public, so_luong, ngay_bat_dau, ngay_ket_thuc,
        trang_thai, so_luot_dung_ca_nhan, hang_ap_dung, loai_voucher
    )
    VALUES (
        @ma_km, @ten_km, @ma_code, @mo_ta, @hinh_anh,
        @loai_giam, @gia_tri_giam, @giam_toi_da, @don_toi_thieu,
        @is_public, @so_luong, @ngay_bd, @ngay_kt,
        @trang_status, @so_luot_dung_ca_nhan, 1, 1
    );
    SELECT @ma_km AS ma_km;
END;
GO

CREATE OR ALTER PROCEDURE sp_ThemNhanVien
    @ma_vt INT,
    @ho_ten NVARCHAR(100),
    @sdt VARCHAR(20),
    @email VARCHAR(150),
    @username VARCHAR(50),
    @password VARCHAR(256)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @ma_nv VARCHAR(20);
    SET @ma_nv = 'NV' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_NhanVien AS VARCHAR(10)), 5);
    INSERT INTO NHAN_VIEN (ma_nv, ma_vt, ho_ten, so_dien_thoai, email, ten_dang_nhap, mat_khau, trang_thai, thoi_gian_tao)
    VALUES (@ma_nv, @ma_vt, @ho_ten, @sdt, @email, @username, @password, 1, GETDATE());
    SELECT @ma_nv AS ma_nv;
END;
GO

CREATE OR ALTER PROCEDURE sp_ThemKhachHang
    @ten_kh NVARCHAR(100),
    @sdt VARCHAR(20),
    @email VARCHAR(150),
    @password VARCHAR(256)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @ma_kh VARCHAR(20);
    SET @ma_kh = 'KH' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_KhachHang AS VARCHAR(10)), 5);
    INSERT INTO KHACH_HANG (ma_kh, ma_hang, so_dien_thoai, ten_kh, email, mat_khau, diem_tich_luy, trang_thai, thoi_gian_tao)
    VALUES (@ma_kh, 1, @sdt, @ten_kh, @email, @password, 0, 1, GETDATE());
    SELECT @ma_kh AS ma_kh;
END;
GO

CREATE OR ALTER PROCEDURE sp_ThemTopping
    @ten_tp NVARCHAR(150),
    @dinh_luong NVARCHAR(100),
    @gia_ban INT,
    @thu_tu INT,
    @trang_thai BIT,
    @hinh_anh VARCHAR(500)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @ma_tp VARCHAR(20);
    SET @ma_tp = 'TP' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_Topping AS VARCHAR(10)), 5);
    INSERT INTO TOPPING (ma_tp, ten_tp, dinh_luong, gia_ban, thu_tu_hien_thi, trang_thai, hinh_anh)
    VALUES (@ma_tp, @ten_tp, @dinh_luong, @gia_ban, @thu_tu, @trang_thai, @hinh_anh);
    SELECT @ma_tp AS ma_tp;
END;
GO

-- =========================================================================
-- V. REPORTING VIEWS (BÁO CÁO THỐNG KÊ DASHBOARD QUẢN TRỊ)
-- =========================================================================

CREATE OR ALTER VIEW vw_DoanhThuNgay AS
SELECT
    CAST(thoi_gian_tao AS DATE) AS Ngay,
    COUNT(ma_dh) AS SoLuongDon,
    SUM(tong_phai_tra) AS TongDoanhThu
FROM DON_HANG
WHERE trang_thai_don = 4
GROUP BY CAST(thoi_gian_tao AS DATE);
GO

CREATE OR ALTER VIEW vw_DoanhThuThang AS
SELECT
    YEAR(thoi_gian_tao) AS Nam,
    MONTH(thoi_gian_tao) AS Thang,
    COUNT(ma_dh) AS SoLuongDon,
    SUM(tong_phai_tra) AS TongDoanhThu
FROM DON_HANG
WHERE trang_thai_don = 4
GROUP BY YEAR(thoi_gian_tao), MONTH(thoi_gian_tao);
GO

CREATE OR ALTER VIEW vw_TopSanPham AS
SELECT
    ct.ma_sp,
    sp.ten_sp,
    dm.ten_dm,
    SUM(ct.so_luong) AS TongSoLuongBan,
    SUM(ct.so_luong * ct.gia_chot) AS DoanhThuMangLai
FROM CHI_TIET_DON_HANG ct
JOIN SAN_PHAM sp ON ct.ma_sp = sp.ma_sp
JOIN DANH_MUC dm ON sp.ma_dm = dm.ma_dm
JOIN DON_HANG dh ON ct.ma_dh = dh.ma_dh
WHERE dh.trang_thai_don = 4
GROUP BY ct.ma_sp, sp.ten_sp, dm.ten_dm;
GO

CREATE OR ALTER VIEW vw_TopNhanVien AS
SELECT
    dh.ma_nv,
    nv.ho_ten,
    COUNT(dh.ma_dh) AS SoDonHoanThanh,
    SUM(dh.tong_phai_tra) AS DoanhThuTaoRa
FROM DON_HANG dh
JOIN NHAN_VIEN nv ON dh.ma_nv = nv.ma_nv
WHERE dh.trang_thai_don = 4
GROUP BY dh.ma_nv, nv.ho_ten;
GO

CREATE OR ALTER VIEW vw_DoanhThuTheoDanhMuc AS
SELECT
    dm.ma_dm,
    dm.ten_dm,
    SUM(ct.so_luong) AS TongSanPhamBan,
    SUM(ct.so_luong * ct.gia_chot) AS TongDoanhThu
FROM CHI_TIET_DON_HANG ct
JOIN SAN_PHAM sp ON ct.ma_sp = sp.ma_sp
JOIN DANH_MUC dm ON sp.ma_dm = dm.ma_dm
JOIN DON_HANG dh ON ct.ma_dh = dh.ma_dh
WHERE dh.trang_thai_don = 4
GROUP BY dm.ma_dm, dm.ten_dm;
GO

-- =========================================================================
-- VI. DỮ LIỆU MẪU BAN ĐẦU (SEED DATA CHUẨN HOÀN CHỈNH)
-- =========================================================================

-- 1. Master Vai Trò
INSERT INTO VAI_TRO (ma_vt, ten_vt, mo_ta) VALUES
(1, N'Quản Trị Viên (Admin)', N'Toàn quyền quản trị hệ thống, nhân sự, menu và báo cáo tài chính'),
(2, N'Nhân Viên Thu Ngân (Staff)', N'Vận hành quầy POS, tiếp nhận đơn online và in hóa đơn thanh toán');

-- 2. Master Hạng Thành Viên
INSERT INTO HANG_THANH_VIEN (ma_hang, ten_hang, diem_toi_thieu, phan_tram_giam, mo_ta) VALUES
(1, N'Đồng', 0, 0, N'Hội viên cơ bản mới đăng ký'),
(2, N'Bạc', 500, 5, N'Tích lũy từ 500.000đ - Giảm 5% hóa đơn'),
(3, N'Vàng', 2000, 10, N'Tích lũy từ 2.000.000đ - Giảm 10% hóa đơn'),
(4, N'Kim Cương', 5000, 15, N'Hội viên VIP thân thiết từ 5.000.000đ - Giảm 15% hóa đơn');

-- 3. Master Kích Cỡ
INSERT INTO KICH_CO (ma_size, ten_size, dung_tich_ml, thu_tu_hien_thi, he_so_gia) VALUES
(1, N'Size S', 350, 1, 1.0),
(2, N'Size M', 500, 2, 1.2),
(3, N'Size L', 700, 3, 1.4);

-- 4. Master Phương Thức Thanh Toán
INSERT INTO PHUONG_THUC_THANH_TOAN (ma_pt, ten_pt, mo_ta, trang_thai) VALUES
(1, N'Tiền mặt', N'Thanh toán trực tiếp bằng tiền mặt tại quầy', 1),
(2, N'Chuyển khoản VietQR', N'Quét mã VietQR SePay khớp tiền tự động thời gian thực', 1);

-- 5. Danh Mục Đồ Uống
DECLARE @DM1 VARCHAR(20) = 'DM' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_DanhMuc AS VARCHAR(10)), 5);
DECLARE @DM2 VARCHAR(20) = 'DM' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_DanhMuc AS VARCHAR(10)), 5);
DECLARE @DM3 VARCHAR(20) = 'DM' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_DanhMuc AS VARCHAR(10)), 5);
DECLARE @DM4 VARCHAR(20) = 'DM' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_DanhMuc AS VARCHAR(10)), 5);

INSERT INTO DANH_MUC (ma_dm, ten_dm, hinh_anh, thu_tu_hien_thi, trang_thai) VALUES
(@DM1, N'Trà Sữa Truyền Thống', 'https://images.unsplash.com/photo-1558857563-b37cfb496f8c?w=400', 1, 1),
(@DM2, N'Trà Trái Cây Tươi', 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=400', 2, 1),
(@DM3, N'Cà Phê & Macchiato', 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400', 3, 1),
(@DM4, N'Đá Xay & Sinh Tố', 'https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=400', 4, 1);

-- 6. Thực Đơn Sản Phẩm Mẹ
DECLARE @SP1 VARCHAR(20) = 'SP' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_SanPham AS VARCHAR(10)), 5);
DECLARE @SP2 VARCHAR(20) = 'SP' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_SanPham AS VARCHAR(10)), 5);
DECLARE @SP3 VARCHAR(20) = 'SP' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_SanPham AS VARCHAR(10)), 5);
DECLARE @SP4 VARCHAR(20) = 'SP' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_SanPham AS VARCHAR(10)), 5);
DECLARE @SP5 VARCHAR(20) = 'SP' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_SanPham AS VARCHAR(10)), 5);
DECLARE @SP6 VARCHAR(20) = 'SP' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_SanPham AS VARCHAR(10)), 5);

INSERT INTO SAN_PHAM (ma_sp, ma_dm, ten_sp, mo_ta, hinh_anh, cho_phep_doi_da, cho_phep_doi_duong, is_new, is_bestseller, trang_thai, cho_phep_topping, thu_tu_hien_thi) VALUES
(@SP1, @DM1, N'Trà Sữa Trân Châu Hoàng Gia', N'Hương vị trà đen Assam thượng hạng hòa quyện sữa tươi New Zealand và trân châu đường đen dẻo bùi.', 'https://images.unsplash.com/photo-1558857563-b37cfb496f8c?w=500', 1, 1, 0, 1, 1, 1, 1),
(@SP2, @DM2, N'Trà Đào Cam Sả Tươi Mát', N'Trà lài thanh nhẹ kết hợp miếng đào giòn ngọt mọng nước cùng tinh chất cam sả giải nhiệt ngày hè.', 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=500', 1, 1, 0, 1, 1, 1, 2),
(@SP3, @DM3, N'Trà Ô Long Sen Vàng Macchiato', N'Trà ô long cao sơn đậm vị, hạt sen bùi ngậy phủ lớp kem mặn cheese macchiato sánh mịn thơm lừng.', 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=500', 1, 1, 1, 0, 1, 1, 3),
(@SP4, @DM1, N'Trà Sữa Nướng Matcha Nhật Bản', N'Bột trà xanh Uji Kyoto tuyển chọn cùng đường đen caramel nướng thơm ngậy đặc biệt.', 'https://images.unsplash.com/photo-1536256263959-770b48d82b0a?w=500', 1, 1, 1, 0, 1, 1, 4),
(@SP5, @DM3, N'Cà Phê Muối Cổ Điển', N'Cà phê Robusta Đắk Lắk đậm đà kết hợp lớp bọt kem muối biển mặn mà, lôi cuốn khó cưỡng.', 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500', 1, 1, 0, 1, 1, 1, 5),
(@SP6, @DM4, N'Matcha Đá Xay Hạnh Nhân', N'Matcha Nhật xay nhuyễn cùng sữa chua Hy Lạp và vụn hạnh nhân giòn tan, thanh mát bổ dưỡng.', 'https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=500', 0, 1, 1, 0, 1, 1, 6);

-- 7. Biến Thể Kích Cỡ & Giá Bán (Matrix Sizes)
INSERT INTO SAN_PHAM_KICH_CO (ma_sp, ma_size, gia_ban, dinh_luong, trang_thai) VALUES
(@SP1, 1, 35000, N'350ml', 1),
(@SP1, 2, 45000, N'500ml', 1),
(@SP1, 3, 55000, N'700ml', 1),

(@SP2, 1, 40000, N'350ml', 1),
(@SP2, 2, 50000, N'500ml', 1),
(@SP2, 3, 60000, N'700ml', 1),

(@SP3, 1, 45000, N'350ml', 1),
(@SP3, 2, 55000, N'500ml', 1),
(@SP3, 3, 65000, N'700ml', 1),

(@SP4, 1, 38000, N'350ml', 1),
(@SP4, 2, 48000, N'500ml', 1),
(@SP4, 3, 58000, N'700ml', 1),

(@SP5, 1, 29000, N'350ml', 1),
(@SP5, 2, 39000, N'500ml', 1),
(@SP5, 3, 49000, N'700ml', 1),

(@SP6, 1, 49000, N'350ml', 1),
(@SP6, 2, 59000, N'500ml', 1),
(@SP6, 3, 69000, N'700ml', 1);

-- 8. Topping Ăn Kèm
DECLARE @TP1 VARCHAR(20) = 'TP' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_Topping AS VARCHAR(10)), 5);
DECLARE @TP2 VARCHAR(20) = 'TP' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_Topping AS VARCHAR(10)), 5);
DECLARE @TP3 VARCHAR(20) = 'TP' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_Topping AS VARCHAR(10)), 5);
DECLARE @TP4 VARCHAR(20) = 'TP' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_Topping AS VARCHAR(10)), 5);
DECLARE @TP5 VARCHAR(20) = 'TP' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_Topping AS VARCHAR(10)), 5);

INSERT INTO TOPPING (ma_tp, ten_tp, dinh_luong, gia_ban, thu_tu_hien_thi, trang_thai, hinh_anh) VALUES
(@TP1, N'Trân Châu Đen Dẻo', N'50g', 10000, 1, 1, 'https://images.unsplash.com/photo-1595981267035-7b04ca84a82d?w=300'),
(@TP2, N'Trân Châu Hoàng Kim', N'50g', 12000, 2, 1, 'https://images.unsplash.com/photo-1595981267035-7b04ca84a82d?w=300'),
(@TP3, N'Kem Cheese Macchiato', N'40ml', 15000, 3, 1, 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=300'),
(@TP4, N'Pudding Trứng Vàng', N'60g', 12000, 4, 1, 'https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=300'),
(@TP5, N'Thạch Củ Năng Giòn Tan', N'50g', 12000, 5, 1, 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=300');

-- 9. Tài Khoản Nhân Sự (Mật khẩu mặc định: 12345678 - Hỗ trợ cả Salted SHA-256 và Raw SHA-256)
DECLARE @NV1 VARCHAR(20) = 'NV' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_NhanVien AS VARCHAR(10)), 5);
DECLARE @NV2 VARCHAR(20) = 'NV' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_NhanVien AS VARCHAR(10)), 5);
DECLARE @NV3 VARCHAR(20) = 'NV' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_NhanVien AS VARCHAR(10)), 5);

INSERT INTO NHAN_VIEN (ma_nv, ma_vt, ho_ten, so_dien_thoai, email, ten_dang_nhap, mat_khau, trang_thai) VALUES
(@NV1, 1, N'Lâm Minh Quang', '0346406405', 'quang642000@gmail.com', 'admin', '3db8fe209bece1d01983058cb34f2725e864980fc0e4b882bcdaf32fbd9f85d6', 1),
(@NV2, 2, N'Nguyễn Thu Ngân', '0912345678', 'staff@teapos.vn', 'staff', '14afe2e96d51ca55a8f0a51149ea8909b46c47690aafb01492843b5337ee70d9', 1),
(@NV3, 1, N'Trần Tuấn Kiệt', '0988776655', 'kiet.tran@teapos.vn', 'admin1', 'e71bdd0570a76456695a4dfaa64d3ca3eddfa1d7d88fc2bb673636db08f8cd59', 1);

-- 10. Hồ Sơ Khách Hàng CRM (Mật khẩu: 12345678)
DECLARE @KH1 VARCHAR(20) = 'KH' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_KhachHang AS VARCHAR(10)), 5);
DECLARE @KH2 VARCHAR(20) = 'KH' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_KhachHang AS VARCHAR(10)), 5);
DECLARE @KH3 VARCHAR(20) = 'KH' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_KhachHang AS VARCHAR(10)), 5);

INSERT INTO KHACH_HANG (ma_kh, ma_hang, so_dien_thoai, ten_kh, email, mat_khau, diem_tich_luy, trang_thai) VALUES
(@KH1, 3, '0909999999', N'Trần Thị VIP', 'khach_vip@gmail.com', 'bf4e4a166a42f6ede1bd3185919eb80f03735968c1c8ff6b693a1f23d0b6cad0', 350, 1),
(@KH2, 2, '0346406405', N'Lâm Hoàng Quang', 'quang_customer@gmail.com', '6602b71c20e27a70fb8d2233da820468ef6208c6fc8d60d0f1e4141a8f6c4a0e', 120, 1),
(@KH3, 1, '0977112233', N'Khách Hàng Test QA', 'test.crm.teapos@gmail.com', '7f6558f07a70205215420aa431d80f23d65355ab06b9d57a3aaa9aa81f061782', 50, 1);

-- Khởi tạo Giỏ hàng cho khách hàng
INSERT INTO GIO_HANG (ma_kh, thoi_gian_tao) VALUES (@KH1, GETDATE());
INSERT INTO GIO_HANG (ma_kh, thoi_gian_tao) VALUES (@KH2, GETDATE());
INSERT INTO GIO_HANG (ma_kh, thoi_gian_tao) VALUES (@KH3, GETDATE());

-- 11. Chương Trình Khuyến Mãi / Voucher
DECLARE @KM1 VARCHAR(20) = 'KM' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_Voucher AS VARCHAR(10)), 5);
DECLARE @KM2 VARCHAR(20) = 'KM' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_Voucher AS VARCHAR(10)), 5);
DECLARE @KM3 VARCHAR(20) = 'KM' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_Voucher AS VARCHAR(10)), 5);
DECLARE @KM4 VARCHAR(20) = 'KM' + RIGHT('00000' + CAST(NEXT VALUE FOR seq_Voucher AS VARCHAR(10)), 5);

INSERT INTO CHUONG_TRINH_KHUYEN_MAI (
    ma_km, ten_km, ma_code, mo_ta_dieu_kien, hinh_anh_url,
    loai_giam, gia_tri_giam, giam_toi_da, don_toi_thieu,
    is_public, so_luong, ngay_bat_dau, ngay_ket_thuc,
    trang_thai, so_luot_dung_ca_nhan, hang_ap_dung, loai_voucher
) VALUES
(@KM1, N'Chào Bạn Mới - Giảm 10K', 'TEA_WELCOME10', N'Giảm ngay 10.000đ cho đơn hàng đạt tối thiểu 40.000đ. Áp dụng cho mọi hội viên.', 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=200', 1, 10000, 0, 40000, 1, 1000, GETDATE(), DATEADD(month, 3, GETDATE()), 1, 1, 1, 1),
(@KM2, N'Ưu Đãi Đặc Quyền Hội Viên VIP', 'TEA_VIP20K', N'Giảm ngay 20.000đ cho đơn hàng đạt mức tối thiểu 30.000đ. Đặc quyền tri ân hội viên hạng Vàng và Kim Cương.', 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=200', 1, 20000, 0, 30000, 0, 500, GETDATE(), DATEADD(month, 3, GETDATE()), 1, 2, 3, 1),
(@KM3, N'Khuyến Mãi Giờ Vàng - Giảm 20%', 'HAPPYHOUR20', N'Giảm 20% tối đa 25.000đ cho đơn hàng từ 50.000đ trong tuần lễ vàng.', 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=200', 2, 20, 25000, 50000, 1, 300, GETDATE(), DATEADD(month, 2, GETDATE()), 1, 1, 1, 1),
(@KM4, N'Siêu Voucher Tri Ân 100K (Đơn 0đ)', 'TEA_FREESHIP100', N'Giảm ngay 100.000đ cho đơn từ 70.000đ. Đơn hàng khấu trừ 100% tự động hoàn tất 0đ!', 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=200', 1, 100000, 0, 70000, 1, 200, GETDATE(), DATEADD(month, 3, GETDATE()), 1, 1, 1, 1);

-- 12. Đơn Hàng Mẫu Ban Đầu (Seed Transactions)
DECLARE @DH1 VARCHAR(50) = 'TEA-' + CONVERT(VARCHAR(8), GETDATE(), 112) + '-00001';
DECLARE @DH2 VARCHAR(50) = 'TEA-' + CONVERT(VARCHAR(8), GETDATE(), 112) + '-00002';

INSERT INTO DON_HANG (ma_dh, ma_kh, ma_nv, ma_pt, ma_km, loai_don_hang, tong_tien_hang, tien_giam_gia, diem_su_dung, tien_tru_diem, tong_phai_tra, trang_thai_thanh_toan, trang_thai_don, thoi_gian_tao) VALUES
(@DH1, @KH1, @NV1, 1, @KM1, 1, 100000, 10000, 0, 0, 97200, 1, 4, DATEADD(hour, -2, GETDATE())),
(@DH2, @KH2, @NV2, 2, NULL, 3, 50000, 0, 10, 10000, 43200, 1, 4, DATEADD(hour, -1, GETDATE()));

-- Chi tiết đơn hàng mẫu
INSERT INTO CHI_TIET_DON_HANG (ma_dh, ma_sp, ma_size, so_luong, gia_chot, muc_da, muc_duong, ghi_chu_mon) VALUES
(@DH1, @SP1, 2, 2, 45000, '70% Đá', '50% Đường', N'Ít ngọt'),
(@DH1, @SP5, 1, 1, 29000, '100% Đá', '100% Đường', N'Bình thường'),
(@DH2, @SP2, 2, 1, 50000, '100% Đá', '70% Đường', N'Giao ngay');

-- Thống kê ngày
INSERT INTO THONG_KE_NGAY (ngay, so_luong_don, tong_doanh_thu, thoi_gian_tao) VALUES
(CAST(GETDATE() AS DATE), 2, 140400, GETDATE());

GO
PRINT N'=========================================================================';
PRINT N'>>> HOÀN TẤT BIÊN DỊCH VÀ KHỞI TẠO CƠ SỞ DỮ LIỆU TEA POS ENTERPRISE! <<<';
PRINT N'=========================================================================';
GO
