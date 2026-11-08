<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>TEA POS - Cổng Đặt Nước Trực Tuyến & Trà Sữa Cao Cấp</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.2/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/global.css?v=2026.11.06" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/portal.css?v=2026.11.06" rel="stylesheet">
    <style>
        .hero-section {
            background: radial-gradient(circle at 10% 20%, rgba(236, 253, 245, 0.8) 0%, rgba(255, 255, 255, 0) 70%),
                        radial-gradient(circle at 90% 80%, rgba(209, 250, 229, 0.6) 0%, rgba(255, 255, 255, 0) 70%),
                        #ffffff;
            border-radius: 28px;
            padding: 50px 40px;
            margin-top: 24px;
            margin-bottom: 40px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 10px 30px -5px rgba(15, 23, 42, 0.05);
            position: relative;
            overflow: hidden;
        }
        .hero-title {
            font-size: 42px;
            font-weight: 850;
            line-height: 1.2;
            letter-spacing: -0.03em;
            color: #0f172a;
        }
        .text-gradient-matcha {
            background: linear-gradient(135deg, #059669 0%, #047857 50%, #064e3b 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .spotlight-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(12px);
            border-radius: 24px;
            padding: 24px;
            border: 1.5px solid #d1fae5;
            box-shadow: 0 20px 35px -10px rgba(5, 150, 105, 0.18);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        .spotlight-card:hover {
            transform: translateY(-6px);
            box-shadow: 0 25px 45px -10px rgba(5, 150, 105, 0.25);
        }
        .voucher-ticket {
            background: #ffffff;
            border-radius: 16px;
            border: 1.5px dashed #10b981;
            padding: 18px 20px;
            transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
            position: relative;
            cursor: pointer;
        }
        .voucher-ticket:hover {
            transform: translateY(-4px);
            box-shadow: 0 12px 24px -4px rgba(16, 185, 129, 0.2);
            border-color: #059669;
        }
        .voucher-ticket::before, .voucher-ticket::after {
            content: '';
            position: absolute;
            top: 50%;
            width: 14px;
            height: 14px;
            background: var(--bg-main);
            border-radius: 50%;
            transform: translateY(-50%);
        }
        .voucher-ticket::before { left: -8px; }
        .voucher-ticket::after { right: -8px; }
        .value-card {
            background: #ffffff;
            border-radius: 20px;
            padding: 26px 20px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(15, 23, 42, 0.04);
            transition: all 0.25s ease;
            text-align: left;
            height: 100%;
        }
        .value-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 12px 24px rgba(15, 23, 42, 0.08);
            border-color: #cbd5e1;
        }
        @media (max-width: 768px) {
            .hero-section {
                padding: 30px 20px;
            }
            .hero-title {
                font-size: 28px;
            }
        }
    </style>
</head>
<body class="bg-light">
<!-- NAV HEADER -->
<jsp:include page="/views/layout/header_portal.jsp" />

<div class="container">
    <!-- =========================================================================
         1. MODERN SPLIT-SCREEN HERO SECTION
         ========================================================================= -->
    <div class="hero-section">
        <div class="row align-items-center g-4 g-lg-5">
            <!-- Cột trái: Headline & Call To Action -->
            <div class="col-12 col-lg-7 text-start">
                <div class="d-inline-flex align-items-center gap-2 px-3 py-1.5 rounded-pill bg-success bg-opacity-10 text-success fw-bold small mb-3 border border-success border-opacity-20 shadow-xs">
                    <i class="bi bi-patch-check-fill text-success"></i>
                    <span>100% TRÀ TƯƠI MỘC CHÂU & BẢO LỘC CAO CẤP</span>
                </div>
                <h1 class="hero-title mb-3">
                    Hương Vị Trà Nguyên Bản<br>
                    <span class="text-gradient-matcha">Đậm Đà Từng Giọt Tinh Túy</span>
                </h1>
                <p class="text-muted fs-6 mb-4" style="max-width: 540px; line-height: 1.6;">
                    Trải nghiệm hương vị trà sữa pha phin chuẩn vị, kết hợp sữa tươi thanh trùng và trân châu nấu mới mỗi 4 giờ. Đặt món trước qua phương thức <strong>Click & Collect</strong> – nhận nước ngay tại quầy không cần xếp hàng!
                </p>

                <!-- Dual Action Buttons -->
                <div class="d-flex flex-wrap align-items-center gap-3 mb-4">
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary-teapos px-4 py-3 fw-bold rounded-pill shadow-md d-flex align-items-center gap-2" style="font-size: 15px;">
                        <i class="bi bi-cup-hot-fill fs-5"></i>
                        <span>KHÁM PHÁ MENU NGAY</span>
                    </a>
                    <a href="#voucherSection" class="btn btn-outline-success px-4 py-3 fw-bold rounded-pill d-flex align-items-center gap-2" style="font-size: 15px; border-width: 2px;">
                        <i class="bi bi-ticket-perforated-fill text-warning fs-5"></i>
                        <span>SĂN VOUCHER VIP 🔥</span>
                    </a>
                </div>

                <!-- Trust Metrics / Highlights -->
                <div class="d-flex flex-wrap align-items-center gap-4 pt-3 border-top text-muted small">
                    <div class="d-flex align-items-center gap-2">
                        <i class="bi bi-star-fill text-warning fs-5"></i>
                        <span><strong>4.9/5</strong> (10.000+ Khách hài lòng)</span>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <i class="bi bi-lightning-charge-fill text-success fs-5"></i>
                        <span><strong>5 Phút</strong> có nước tại quầy</span>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <i class="bi bi-gift-fill text-danger fs-5"></i>
                        <span><strong>Tích điểm</strong> thăng hạng đổi quà</span>
                    </div>
                </div>
            </div>

            <!-- Cột phải: Spotlight Signature Drink Card -->
            <div class="col-12 col-lg-5">
                <div class="spotlight-card text-center position-relative">
                    <span class="position-absolute top-0 end-0 badge bg-danger text-white fw-bold m-3 px-3 py-1.5 rounded-pill shadow-sm" style="font-size: 11px;">
                        👑 SIGNATURE #1 BESTSELLER
                    </span>
                    <img src="https://images.unsplash.com/photo-1558857563-b37cfb496f8c?w=600" 
                         alt="Trà Sữa Trân Châu Đường Đen" 
                         class="img-fluid rounded-4 mb-3 shadow-sm" 
                         style="height: 240px; width: 100%; object-fit: cover;">
                    <h5 class="fw-bold text-dark mb-1">Trà Sữa Trân Châu Đường Đen Hoàng Gia</h5>
                    <p class="text-muted small mb-2">Trà đen đậm đà quyện đường đen Okinawa và trân châu dẻo dai</p>
                    <div class="d-flex justify-content-center align-items-center gap-3 mb-3">
                        <span class="text-muted text-decoration-line-through small">45.000 đ</span>
                        <strong class="text-success fs-4 fw-black">35.000 đ</strong>
                        <span class="badge bg-warning bg-opacity-15 text-warning border border-warning px-2 py-1 rounded-pill small fw-bold">-22%</span>
                    </div>
                    <a href="${pageContext.request.contextPath}/product/detail?id=SP-00001" class="btn btn-primary-teapos w-100 py-2.5 rounded-pill fw-bold">
                        <i class="bi bi-cart-plus-fill me-1"></i> TÙY BIẾN & ĐẶT LY NÀY
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- =========================================================================
         2. KHO VOUCHER TỨC THÌ (FLASH VOUCHERS SHOWCASE)
         ========================================================================= -->
    <div class="mb-5 text-start" id="voucherSection">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
                <h4 class="fw-bold text-dark mb-0 d-flex align-items-center gap-2">
                    <i class="bi bi-ticket-perforated-fill text-warning"></i> VÍ VOUCHER ƯU ĐÃI ĐỘC QUYỀN
                </h4>
                <small class="text-muted">Chạm vào voucher để tự động sao chép mã giảm giá</small>
            </div>
            <a href="${pageContext.request.contextPath}/profile/vouchers" class="text-success fw-bold small text-decoration-none">
                Xem tất cả ví <i class="bi bi-chevron-right"></i>
            </a>
        </div>

        <div class="row g-3">
            <!-- Voucher 1: TEA_GIAM20K -->
            <div class="col-12 col-md-4">
                <div class="voucher-ticket shadow-sm d-flex justify-content-between align-items-center" onclick="copyVoucherCode('TEA_GIAM20K')">
                    <div>
                        <div class="d-flex align-items-center gap-2 mb-1">
                            <span class="badge bg-dark font-monospace fw-bold">TEA_GIAM20K</span>
                            <span class="badge bg-success bg-opacity-10 text-success fw-bold">Giảm 20K</span>
                        </div>
                        <h6 class="fw-bold text-dark mb-1">Ưu đãi Khai trương</h6>
                        <small class="text-muted d-block" style="font-size: 11px;">Đơn tối thiểu từ 50.000 đ</small>
                    </div>
                    <button type="button" class="btn btn-sm btn-outline-success fw-bold px-3 rounded-pill text-nowrap">
                        <i class="bi bi-copy me-1"></i>LƯU MÃ
                    </button>
                </div>
            </div>

            <!-- Voucher 2: TEA_FREESHIP100 -->
            <div class="col-12 col-md-4">
                <div class="voucher-ticket shadow-sm d-flex justify-content-between align-items-center" onclick="copyVoucherCode('TEA_FREESHIP100')">
                    <div>
                        <div class="d-flex align-items-center gap-2 mb-1">
                            <span class="badge bg-danger text-white font-monospace fw-bold">TEA_FREESHIP100</span>
                            <span class="badge bg-danger bg-opacity-10 text-danger fw-bold">Tối đa 100K</span>
                        </div>
                        <h6 class="fw-bold text-dark mb-1">Voucher 0đ Miễn phí 100%</h6>
                        <small class="text-muted d-block" style="font-size: 11px;">Áp dụng cho mọi đơn hàng</small>
                    </div>
                    <button type="button" class="btn btn-sm btn-danger fw-bold px-3 rounded-pill text-nowrap">
                        <i class="bi bi-fire me-1"></i>LƯU MÃ
                    </button>
                </div>
            </div>

            <!-- Voucher 3: TEA_VIP30 -->
            <div class="col-12 col-md-4">
                <div class="voucher-ticket shadow-sm d-flex justify-content-between align-items-center" onclick="copyVoucherCode('TEA_VIP30')">
                    <div>
                        <div class="d-flex align-items-center gap-2 mb-1">
                            <span class="badge bg-warning text-dark font-monospace fw-bold">TEA_VIP30</span>
                            <span class="badge bg-warning bg-opacity-10 text-dark fw-bold">Giảm 30%</span>
                        </div>
                        <h6 class="fw-bold text-dark mb-1">Đặc quyền Thành viên CRM</h6>
                        <small class="text-muted d-block" style="font-size: 11px;">Đơn tối thiểu từ 80.000 đ</small>
                    </div>
                    <button type="button" class="btn btn-sm btn-outline-warning text-dark fw-bold px-3 rounded-pill text-nowrap">
                        <i class="bi bi-star-fill me-1 text-warning"></i>LƯU MÃ
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- =========================================================================
         3. DANH MỤC ĐỒ UỐNG CHỌN LỌC
         ========================================================================= -->
    <div class="mb-5 text-center">
        <h4 class="fw-bold mb-4 d-flex align-items-center justify-content-center gap-2 text-dark">
            <i class="bi bi-grid-fill text-success"></i> DANH MỤC THỰC ĐƠN ĐẶC SẮC
        </h4>
        <div class="row row-cols-2 row-cols-md-4 g-3 g-md-4 justify-content-center">
            <c:forEach var="cat" items="${categories}">
                <div class="col">
                    <div class="category-circle-card hover-lift p-3 border rounded-4 bg-white shadow-sm" onclick="window.location.href='${pageContext.request.contextPath}/products?category=${cat.maDm}'">
                        <c:choose>
                            <c:when test="${not empty cat.hinhAnh}">
                                <img src="${cat.hinhAnh}" class="mx-auto rounded-circle mb-3 border border-2 border-success shadow-sm" style="width: 76px; height: 76px; object-fit: cover;">
                            </c:when>
                            <c:otherwise>
                                <div class="mx-auto bg-success bg-opacity-10 text-success rounded-circle d-flex align-items-center justify-content-center mb-3 shadow-sm" style="width: 76px; height: 76px;">
                                    <i class="bi bi-cup-straw fs-2"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <span class="fw-bold text-dark text-uppercase small d-block" style="letter-spacing: 0.3px;"><c:out value="${cat.tenDm}"/></span>
                        <span class="text-muted" style="font-size: 11px;">Xem các món <i class="bi bi-arrow-right"></i></span>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <!-- =========================================================================
         4. SẢN PHẨM BÁN CHẠY NHẤT (BESTSELLERS)
         ========================================================================= -->
    <div class="mb-5 text-start">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h4 class="fw-bold mb-0 d-flex align-items-center gap-2 text-dark">
                    <i class="bi bi-fire text-danger animate-pulse"></i> ĐỒ UỐNG BÁN CHẠY NHẤT (HOT)
                </h4>
                <small class="text-muted">Những ly nước được khách hàng yêu thích và đặt nhiều nhất mỗi ngày</small>
            </div>
            <a href="${pageContext.request.contextPath}/products?filter=hot" class="text-success fw-bold small text-decoration-none d-flex align-items-center gap-1">
                Xem tất cả <i class="bi bi-chevron-right"></i>
            </a>
        </div>
        <div class="row g-3 g-md-4 row-cols-1 row-cols-md-2 row-cols-lg-3">
            <c:forEach var="sp" items="${bestsellers}">
                <div class="col">
                    <div class="product-card hover-lift p-3 shadow-sm position-relative">
                        <!-- Nút yêu thích wishlist -->
                        <button type="button" class="btn btn-sm btn-light border-0 rounded-circle position-absolute top-0 start-0 m-2.5 shadow-sm btn-wishlist-toggle d-flex align-items-center justify-content-center" style="width: 34px; height: 34px; z-index: 10;" data-masp="${sp.maSp}" data-tensp="${sp.tenSp}" data-giaban="${not empty sp.sizesList ? sp.sizesList[0].giaBan : 0}" data-hinhanh="${sp.hinhAnh}" onclick="toggleWishlistBtn(this, event)" title="Yêu thích">
                            <i class="bi bi-heart text-muted"></i>
                        </button>
                        <!-- Badge Bestseller -->
                        <span class="position-absolute top-0 end-0 badge bg-danger text-white fw-bold m-2.5 px-3 py-1.5 shadow-sm" style="border-radius: 50px; font-size: 10.5px; z-index: 10;">
                            BESTSELLER 🔥
                        </span>
                        <img src="${not empty sp.hinhAnh ? sp.hinhAnh : 'https://cdn-icons-png.flaticon.com/512/3177/3177440.png'}" class="w-100 rounded-3 mb-3 product-card-img" style="height: 195px; object-fit: cover;" alt="Product">
                        <div class="product-card-body p-0">
                            <h6 class="fw-bold text-dark mb-1 text-truncate product-card-title"><c:out value="${sp.tenSp}"/></h6>
                            <p class="text-muted small text-truncate mb-3 d-none d-md-block product-card-desc"><c:out value="${sp.moTa}"/></p>

                            <!-- Size Price Row -->
                            <div class="d-flex flex-column gap-2 mt-auto border-top pt-3 product-card-footer">
                                <div class="product-card-price-row">
                                    <span class="product-card-price-lbl">Giá khởi điểm:</span>
                                    <strong class="product-card-price-val">
                                        <c:forEach var="size" items="${sp.sizesList}" end="0">
                                            <fmt:formatNumber value="${size.giaBan}" type="currency" currencySymbol="" maxFractionDigits="0"/>đ
                                        </c:forEach>
                                    </strong>
                                </div>
                                <div class="d-flex gap-2">
                                    <button type="button" class="btn btn-outline-success fw-bold py-2 px-3 rounded-3 flex-shrink-0" onclick="quickAddToCart('${sp.maSp}', '${sp.tenSp}', '${not empty sp.sizesList ? sp.sizesList[0].maSize : 1}')" title="Thêm nhanh vào giỏ hàng">
                                        <i class="bi bi-cart-plus-fill"></i>
                                    </button>
                                    <a href="${pageContext.request.contextPath}/product/detail?id=${sp.maSp}" class="btn btn-primary-teapos flex-grow-1 py-2 fw-bold text-center d-flex align-items-center justify-content-center gap-1 btn-customize-mobile" style="font-size: 12.5px; border-radius: 12px !important;">
                                        <i class="bi bi-cup-straw"></i> TÙY BIẾN PHA CHẾ
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <!-- =========================================================================
         5. SẢN PHẨM MỚI LÊN KỆ (NEW ARRIVALS)
         ========================================================================= -->
    <div class="mb-5 text-start">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h4 class="fw-bold mb-0 d-flex align-items-center gap-2 text-dark">
                    <i class="bi bi-stars text-warning animate-pulse"></i> MÓN MỚI LÊN KỆ HÔM NAY
                </h4>
                <small class="text-muted">Các sáng tạo hương vị mới nhất vừa được bổ sung vào thực đơn</small>
            </div>
            <a href="${pageContext.request.contextPath}/products?filter=new" class="text-success fw-bold small text-decoration-none d-flex align-items-center gap-1">
                Xem tất cả <i class="bi bi-chevron-right"></i>
            </a>
        </div>
        <div class="row g-3 g-md-4 row-cols-1 row-cols-md-2 row-cols-lg-3">
            <c:forEach var="sp" items="${newArrivals}">
                <div class="col">
                    <div class="product-card hover-lift p-3 shadow-sm position-relative">
                        <!-- Nút yêu thích wishlist -->
                        <button type="button" class="btn btn-sm btn-light border-0 rounded-circle position-absolute top-0 start-0 m-2.5 shadow-sm btn-wishlist-toggle d-flex align-items-center justify-content-center" style="width: 34px; height: 34px; z-index: 10;" data-masp="${sp.maSp}" data-tensp="${sp.tenSp}" data-giaban="${not empty sp.sizesList ? sp.sizesList[0].giaBan : 0}" data-hinhanh="${sp.hinhAnh}" onclick="toggleWishlistBtn(this, event)" title="Yêu thích">
                            <i class="bi bi-heart text-muted"></i>
                        </button>
                        <!-- Badge New -->
                        <span class="position-absolute top-0 end-0 badge bg-warning text-dark fw-bold m-2.5 px-3 py-1.5 shadow-sm" style="border-radius: 50px; font-size: 10.5px; z-index: 10;">
                            NEW ✨
                        </span>
                        <img src="${not empty sp.hinhAnh ? sp.hinhAnh : 'https://cdn-icons-png.flaticon.com/512/3177/3177440.png'}" class="w-100 rounded-3 mb-3 product-card-img" style="height: 195px; object-fit: cover;" alt="Product">
                        <div class="product-card-body p-0">
                            <h6 class="fw-bold text-dark mb-1 text-truncate product-card-title"><c:out value="${sp.tenSp}"/></h6>
                            <p class="text-muted small text-truncate mb-3 d-none d-md-block product-card-desc"><c:out value="${sp.moTa}"/></p>

                            <!-- Size Price Row -->
                            <div class="d-flex flex-column gap-2 mt-auto border-top pt-3 product-card-footer">
                                <div class="product-card-price-row">
                                    <span class="product-card-price-lbl">Giá khởi điểm:</span>
                                    <strong class="product-card-price-val">
                                        <c:forEach var="size" items="${sp.sizesList}" end="0">
                                            <fmt:formatNumber value="${size.giaBan}" type="currency" currencySymbol="" maxFractionDigits="0"/>đ
                                        </c:forEach>
                                    </strong>
                                </div>
                                <div class="d-flex gap-2">
                                    <button type="button" class="btn btn-outline-success fw-bold py-2 px-3 rounded-3 flex-shrink-0" onclick="quickAddToCart('${sp.maSp}', '${sp.tenSp}', '${not empty sp.sizesList ? sp.sizesList[0].maSize : 1}')" title="Thêm nhanh vào giỏ hàng">
                                        <i class="bi bi-cart-plus-fill"></i>
                                    </button>
                                    <a href="${pageContext.request.contextPath}/product/detail?id=${sp.maSp}" class="btn btn-primary-teapos flex-grow-1 py-2 fw-bold text-center d-flex align-items-center justify-content-center gap-1 btn-customize-mobile" style="font-size: 12.5px; border-radius: 12px !important;">
                                        <i class="bi bi-cup-straw"></i> TÙY BIẾN PHA CHẾ
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <!-- =========================================================================
         6. CAM KẾT GIÁ TRỊ THƯƠNG HIỆU (BRAND PILLARS)
         ========================================================================= -->
    <div class="mb-5 pt-3">
        <div class="text-center mb-4">
            <h4 class="fw-bold text-dark mb-1">VÌ SAO NÊN CHỌN TEA POS?</h4>
            <p class="text-muted small">Cam kết mang đến trải nghiệm đồ uống chuẩn vị, tiện lợi và an tâm nhất</p>
        </div>
        <div class="row g-3 g-md-4">
            <div class="col-12 col-sm-6 col-lg-3">
                <div class="value-card">
                    <div class="rounded-circle bg-success bg-opacity-10 text-success d-flex align-items-center justify-content-center mb-3" style="width: 52px; height: 52px; font-size: 24px;">
                        🍃
                    </div>
                    <h6 class="fw-bold text-dark mb-1">100% Trà Hái Tay</h6>
                    <p class="text-muted small mb-0">Chắt lọc từ nông trường Mộc Châu & Bảo Lộc, không hương liệu độc hại.</p>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-lg-3">
                <div class="value-card">
                    <div class="rounded-circle bg-primary bg-opacity-10 text-primary d-flex align-items-center justify-content-center mb-3" style="width: 52px; height: 52px; font-size: 24px;">
                        ⚡
                    </div>
                    <h6 class="fw-bold text-dark mb-1">Click & Collect 5 Phút</h6>
                    <p class="text-muted small mb-0">Đặt online trước qua website, ghé quầy nhận nước ngay không cần chờ.</p>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-lg-3">
                <div class="value-card">
                    <div class="rounded-circle bg-warning bg-opacity-10 text-warning d-flex align-items-center justify-content-center mb-3" style="width: 52px; height: 52px; font-size: 24px;">
                        🎁
                    </div>
                    <h6 class="fw-bold text-dark mb-1">Tích Điểm CRM Đỉnh Cao</h6>
                    <p class="text-muted small mb-0">Thưởng 1 điểm mỗi 10k chi tiêu, đổi voucher giảm giá và nâng hạng VIP.</p>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-lg-3">
                <div class="value-card">
                    <div class="rounded-circle bg-danger bg-opacity-10 text-danger d-flex align-items-center justify-content-center mb-3" style="width: 52px; height: 52px; font-size: 24px;">
                        💳
                    </div>
                    <h6 class="fw-bold text-dark mb-1">Thanh Toán Linh Hoạt</h6>
                    <p class="text-muted small mb-0">VietQR động tự động xác thực hoặc tiền mặt tại quầy, xử lý đơn 0đ an toàn.</p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- FOOTER -->
<jsp:include page="/views/layout/footer_portal.jsp" />
</body>
</html>
