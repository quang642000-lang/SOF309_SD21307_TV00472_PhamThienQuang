<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>TEA POS - Menu Đồ Uống Trực Tuyến 2026</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.2/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/global.css?v=2026.11.06" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/portal.css?v=2026.11.06" rel="stylesheet">
    <style>
        .product-card {
            border-radius: 16px;
            background: #ffffff;
            border: 1.5px solid var(--border-color);
            transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
            overflow: hidden;
        }
        .product-card:hover {
            transform: translateY(-4px);
            border-color: var(--primary);
            box-shadow: 0 12px 24px -4px rgba(5, 150, 105, 0.15) !important;
        }
        .product-img-wrapper {
            position: relative;
            overflow: hidden;
            border-radius: 12px;
            background: #f8fafc;
        }
        .product-card-img {
            transition: transform 0.4s ease;
        }
        .product-card:hover .product-card-img {
            transform: scale(1.05);
        }
        .category-pill {
            transition: all 0.2s ease;
            font-size: 12.5px;
            border: 1.5px solid var(--border-color);
        }
        .category-pill:hover {
            border-color: var(--primary);
            color: var(--primary);
        }
        .category-pill.active {
            background-color: var(--primary) !important;
            border-color: var(--primary) !important;
            color: #ffffff !important;
            box-shadow: 0 4px 12px rgba(5, 150, 105, 0.25);
        }
    </style>
</head>
<body class="bg-light">
<!-- NAV HEADER -->
<jsp:include page="/views/layout/header_portal.jsp" />

<div class="container py-4">
    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb" class="mb-3">
        <ol class="breadcrumb small">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none text-muted">Trang chủ</a></li>
            <li class="breadcrumb-item active text-success fw-bold" aria-current="page">Thực đơn đồ uống</li>
        </ol>
    </nav>

    <div class="row g-4">
        <!-- BỘ LỌC BÊN TRÁI (SIDEBAR DESKTOP) -->
        <div class="col-12 col-lg-3 text-start d-none d-lg-block" id="portalFilterSidebar">
            <div class="card border-0 p-4 shadow-sm" style="border-radius: 16px; background-color: #ffffff; border: 1.5px solid var(--border-color) !important;">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="fw-bold text-dark small text-uppercase mb-0" style="letter-spacing: 0.5px;">
                        <i class="bi bi-search text-success me-1"></i> Tìm kiếm
                    </h5>
                </div>
                <form action="${pageContext.request.contextPath}/products" method="GET" class="mb-4">
                    <div class="input-group input-group-sm">
                        <input type="text" name="search" class="form-control form-control-teapos" placeholder="Tìm tên món nước..." value="<c:out value="${searchKeyword}"/>">
                        <button type="submit" class="btn btn-success fw-bold px-3" style="background-color: var(--primary); border: none;"><i class="bi bi-search"></i></button>
                    </div>
                </form>

                <!-- LỌC DANH MỤC SẢN PHẨM -->
                <h5 class="fw-bold mb-3 text-dark small text-uppercase" style="letter-spacing: 0.5px;">
                    <i class="bi bi-grid-fill text-success me-1"></i> Nhóm Đồ Uống
                </h5>
                <div class="list-group list-group-flush gap-1.5">
                    <a href="${pageContext.request.contextPath}/products" class="list-group-item list-group-item-action border-0 rounded-2 py-2.5 px-3 fw-semibold ${empty selectedCategory ? 'active-category text-white' : 'text-dark bg-transparent'}" style="font-size: 13.5px;">
                        <i class="bi bi-cup-hot-fill me-2"></i> Tất cả thực đơn
                    </a>
                    <c:forEach var="cat" items="${categories}">
                        <a href="${pageContext.request.contextPath}/products?category=${cat.maDm}" class="list-group-item list-group-item-action border-0 rounded-2 py-2.5 px-3 fw-semibold ${selectedCategory eq cat.maDm ? 'active-category text-white' : 'text-dark bg-transparent'}" style="font-size: 13.5px;">
                            <i class="bi bi-tag-fill me-2 text-muted"></i> <c:out value="${cat.tenDm}"/>
                        </a>
                    </c:forEach>
                </div>

                <!-- CAM KẾT CHẤT LƯỢNG (TRUST BADGE) -->
                <div class="mt-4 pt-3 border-top">
                    <div class="d-flex align-items-center gap-2 mb-2">
                        <i class="bi bi-shield-check text-success fs-5"></i>
                        <small class="fw-bold text-dark">Cam kết chất lượng</small>
                    </div>
                    <small class="text-muted d-block" style="font-size: 11px; line-height: 1.5;">Đồ uống pha mới 100% trong vòng 5 phút sau khi đặt.</small>
                </div>
            </div>
        </div>

        <!-- LƯỚI SẢN PHẨM & LỌC SẮP XẾP BÊN PHẢI -->
        <div class="col-12 col-lg-9">
            <!-- TIÊU ĐỀ MENU VÀ LỌC GIÁ -->
            <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                <div class="text-start">
                    <h4 class="fw-bold mb-1 text-dark" style="letter-spacing: -0.5px;">THỰC ĐƠN TRÀ SỮA & CAFE 2026</h4>
                    <small class="text-muted">Tuyển chọn <strong class="text-success">${totalProducts}</strong> món thức uống tuyệt hảo chuẩn vị</small>
                </div>

                <!-- Bộ lọc sắp xếp thông minh -->
                <div class="btn-group border bg-white shadow-sm" style="border-radius: 20px; padding: 2px;">
                    <button class="btn btn-sm btn-light border-0 dropdown-toggle fw-bold text-dark px-3 py-1.5" type="button" data-bs-toggle="dropdown" style="font-size: 12.5px; border-radius: 18px;">
                        <c:choose>
                            <c:when test="${currentSort eq 'price_asc'}"><i class="bi bi-sort-numeric-down text-success me-1"></i> Giá: Thấp đến Cao</c:when>
                            <c:when test="${currentSort eq 'price_desc'}"><i class="bi bi-sort-numeric-up-alt text-success me-1"></i> Giá: Cao đến Thấp</c:when>
                            <c:otherwise><i class="bi bi-funnel-fill text-muted me-1"></i> Sắp xếp theo giá</c:otherwise>
                        </c:choose>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end shadow border-0 py-2 mt-1" style="border-radius: 8px;">
                        <li>
                            <a class="dropdown-item small fw-semibold py-2" href="${pageContext.request.contextPath}/products?sort=price_asc${not empty selectedCategory ? '&category='.concat(selectedCategory) : ''}${not empty searchKeyword ? '&search='.concat(searchKeyword) : ''}">
                                <i class="bi bi-sort-numeric-down text-success me-1"></i> Giá bán: Thấp đến Cao
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item small fw-semibold py-2" href="${pageContext.request.contextPath}/products?sort=price_desc${not empty selectedCategory ? '&category='.concat(selectedCategory) : ''}${not empty searchKeyword ? '&search='.concat(searchKeyword) : ''}">
                                <i class="bi bi-sort-numeric-up-alt text-success me-1"></i> Giá bán: Cao đến Thấp
                            </a>
                        </li>
                    </ul>
                </div>
            </div>

            <!-- HORIZONTAL CATEGORY SCROLLER (CHUẨN HEYTEA & KATINAT) -->
            <div class="d-flex overflow-x-auto gap-2 mb-4 pb-2 scrollbar-none" style="scrollbar-width: none; -webkit-overflow-scrolling: touch;">
                <a href="${pageContext.request.contextPath}/products" class="btn btn-sm rounded-pill px-3 py-2 fw-bold text-nowrap d-flex align-items-center gap-1.5 category-pill ${empty selectedCategory ? 'active' : 'text-muted bg-white'}">
                    <i class="bi bi-grid-fill"></i> Tất cả món
                </a>
                <c:forEach var="cat" items="${categories}">
                    <a href="${pageContext.request.contextPath}/products?category=${cat.maDm}" class="btn btn-sm rounded-pill px-3 py-2 fw-bold text-nowrap d-flex align-items-center gap-1.5 category-pill ${selectedCategory eq cat.maDm ? 'active' : 'text-muted bg-white'}">
                        <c:out value="${cat.tenDm}"/>
                    </a>
                </c:forEach>
            </div>

            <!-- LƯỚI SẢN PHẨM - TỐI ƯU SONG HÀNH 2 CỘT TRÊN DI ĐỘNG (row-cols-2) -->
            <div class="row row-cols-2 row-cols-md-2 row-cols-lg-3 g-3 g-md-4">
                <c:choose>
                    <c:when test="${not empty products}">
                        <c:forEach var="item" items="${products}">
                            <div class="col">
                                <div class="product-card p-3 shadow-sm position-relative d-flex flex-column h-100">
                                    <!-- Nút Wishlist yêu thích -->
                                    <button type="button" class="btn btn-sm btn-light border-0 rounded-circle position-absolute top-0 end-0 m-2 shadow-sm btn-wishlist-toggle d-flex align-items-center justify-content-center" style="width: 32px; height: 32px; z-index: 10;" data-masp="${item.maSp}" data-tensp="${item.tenSp}" data-giaban="${not empty item.sizesList ? item.sizesList[0].giaBan : 0}" data-hinhanh="${item.hinhAnh}" onclick="toggleWishlistBtn(this, event)" title="Yêu thích">
                                        <i class="bi bi-heart text-muted"></i>
                                    </button>
                                    
                                    <!-- NHÃN HOT / NEW -->
                                    <c:if test="${item.isNew}">
                                        <span class="position-absolute top-0 start-0 badge bg-warning text-dark fw-bold m-2 px-2.5 py-1.5 shadow-sm rounded-pill" style="font-size: 10px; z-index: 5;">NEW ✨</span>
                                    </c:if>
                                    <c:if test="${item.isBestseller}">
                                        <span class="position-absolute top-0 start-0 badge bg-danger text-white fw-bold m-2 px-2.5 py-1.5 shadow-sm rounded-pill" style="font-size: 10px; z-index: 5;">HOT 🔥</span>
                                    </c:if>

                                    <!-- Ảnh minh họa sản phẩm -->
                                    <a href="${pageContext.request.contextPath}/product/detail?id=${item.maSp}" class="text-decoration-none product-img-wrapper mb-2.5 d-block">
                                        <img src="${not empty item.hinhAnh ? item.hinhAnh : 'https://cdn-icons-png.flaticon.com/512/3177/3177440.png'}" class="product-card-img rounded w-100" alt="Pic" style="height: 180px; object-fit: cover;">
                                    </a>

                                    <!-- Thông tin cốc nước -->
                                    <div class="product-card-body p-0 flex-grow-1 text-start">
                                        <div class="d-flex align-items-center gap-1 mb-1">
                                            <span class="text-warning small" style="font-size: 11px;">⭐⭐⭐⭐⭐</span>
                                            <small class="text-muted" style="font-size: 11px;">(4.9)</small>
                                        </div>
                                        <h6 class="product-card-title mb-1 fw-bold">
                                            <a href="${pageContext.request.contextPath}/product/detail?id=${item.maSp}" class="text-dark text-decoration-none">
                                                <c:out value="${item.tenSp}"/>
                                            </a>
                                        </h6>
                                        <p class="text-muted small text-truncate mb-2.5 d-none d-md-block" style="font-size: 12px;"><c:out value="${item.moTa}"/></p>
                                    </div>

                                    <!-- Dải chốt giá bán và hành động kép -->
                                    <div class="d-flex flex-column gap-2 mt-auto border-top pt-2.5 product-card-footer">
                                        <div class="product-card-price-row d-flex justify-content-between align-items-baseline">
                                            <span class="text-muted small" style="font-size: 11.5px;">Giá từ:</span>
                                            <strong class="text-success fs-6 fw-bold">
                                                <c:forEach var="size" items="${item.sizesList}" end="0">
                                                    <fmt:formatNumber value="${size.giaBan}" type="currency" currencySymbol="" maxFractionDigits="0"/>đ
                                                </c:forEach>
                                            </strong>
                                        </div>
                                        
                                        <!-- BỘ NÚT ĐẶT HÀNG KÉP THÔNG MINH -->
                                        <div class="d-flex gap-1.5 w-100">
                                            <!-- Thêm nhanh vào giỏ hàng ngay lập tức -->
                                            <button type="button" class="btn btn-sm btn-outline-success rounded-pill fw-bold px-2 py-1.5 flex-grow-1 d-flex align-items-center justify-content-center gap-1" style="font-size: 12px;" onclick="quickAddToCart('${item.maSp}', '${item.tenSp}', ${not empty item.sizesList ? item.sizesList[0].maSize : 1})" title="Thêm nhanh vào giỏ với cấu hình tiêu chuẩn">
                                                <i class="bi bi-cart-plus-fill"></i> + Thêm
                                            </button>
                                            <!-- Tùy biến chi tiết (Đá, Đường, Topping) -->
                                            <a href="${pageContext.request.contextPath}/product/detail?id=${item.maSp}" class="btn btn-sm btn-success rounded-pill fw-bold px-2.5 py-1.5 d-flex align-items-center justify-content-center gap-1 btn-primary-teapos" style="font-size: 12px;" title="Tùy biến đá, đường, kích cỡ và topping">
                                                <i class="bi bi-sliders"></i> Tùy biến
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="col-12 text-center py-5">
                            <i class="bi bi-search fs-1 text-muted d-block mb-3 opacity-40"></i>
                            <h5 class="fw-bold text-muted">Không tìm thấy món nước nào phù hợp!</h5>
                            <a href="${pageContext.request.contextPath}/products" class="btn btn-sm btn-outline-success mt-2 rounded-pill px-3">Xem tất cả thực đơn</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- PHÂN TRANG CHUẨN BOOTSTRAP 5 -->
            <c:if test="${totalPages > 1}">
                <nav aria-label="Page navigation" class="mt-5">
                    <ul class="pagination pagination-sm justify-content-center gap-1">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link rounded-pill py-2 px-3 fw-semibold text-success" href="${pageContext.request.contextPath}/products?page=${currentPage - 1}${not empty selectedCategory ? '&category='.concat(selectedCategory) : ''}${not empty searchKeyword ? '&search='.concat(searchKeyword) : ''}${not empty currentSort ? '&sort='.concat(currentSort) : ''}" aria-label="Previous">
                                &laquo; Trước
                            </a>
                        </li>
                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link rounded-circle py-2 px-3 fw-bold ${currentPage == i ? 'bg-success border-success text-white' : 'text-dark'}" href="${pageContext.request.contextPath}/products?page=${i}${not empty selectedCategory ? '&category='.concat(selectedCategory) : ''}${not empty searchKeyword ? '&search='.concat(searchKeyword) : ''}${not empty currentSort ? '&sort='.concat(currentSort) : ''}">${i}</a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link rounded-pill py-2 px-3 fw-semibold text-success" href="${pageContext.request.contextPath}/products?page=${currentPage + 1}${not empty selectedCategory ? '&category='.concat(selectedCategory) : ''}${not empty searchKeyword ? '&search='.concat(searchKeyword) : ''}${not empty currentSort ? '&sort='.concat(currentSort) : ''}" aria-label="Next">
                                Sau &raquo;
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </div>
    </div>
</div>

<!-- FOOTER -->
<jsp:include page="/views/layout/footer_portal.jsp" />
</body>
</html>
