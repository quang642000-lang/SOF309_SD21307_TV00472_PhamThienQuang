<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.2/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/global.css?v=2026.11.06" rel="stylesheet" type="text/css">
    <link href="${pageContext.request.contextPath}/assets/css/portal.css?v=2026.11.06" rel="stylesheet" type="text/css">
</head>
<body>
<!-- PROMOTION BAR -->
<div class="portal-promo-bar text-center py-2 px-3 fw-bold d-flex justify-content-center align-items-center gap-2">
    <span>🎉 THƯỞNG 1 ĐIỂM CRM CHO MỖI 10.000 VNĐ CHI TIÊU</span>
    <span class="d-none d-md-inline">•</span>
    <span class="d-none d-md-inline">MÃ <strong class="text-warning">TEA_GIAM20K</strong> GIẢM 20K CHO ĐƠN TỪ 50K!</span>
</div>

<!-- TOP STICKY NAVBAR - MODERN GLASSMORPHISM -->
<nav class="navbar navbar-expand-lg sticky-top py-2.5 shadow-sm" style="background: rgba(255, 255, 255, 0.94); backdrop-filter: blur(16px); -webkit-backdrop-filter: blur(16px); border-bottom: 1px solid rgba(226, 232, 240, 0.9);">
    <div class="container-fluid container-lg d-flex align-items-center justify-content-between">
        <!-- BRAND LOGO -->
        <a class="navbar-brand fw-extrabold text-success d-flex align-items-center gap-2 mb-0" href="${pageContext.request.contextPath}/home" style="color: #059669 !important;">
            <div class="rounded-circle bg-success text-white d-flex align-items-center justify-content-center shadow-sm" style="width: 38px; height: 38px; background: linear-gradient(135deg, #059669 0%, #047857 100%) !important;">
                <i class="bi bi-cup-hot-fill fs-5"></i>
            </div>
            <div class="d-flex flex-column text-start">
                <span class="fw-black text-dark" style="font-size: 19px; letter-spacing: -0.5px; line-height: 1.1;">TEA POS</span>
                <span class="text-success fw-bold" style="font-size: 9.5px; letter-spacing: 1px;">PREMIUM CAFÉ</span>
            </div>
        </a>

        <!-- NAVBAR NAVIGATION LINKS (DESKTOP) -->
        <div class="collapse navbar-collapse order-lg-2 ms-lg-4" id="navbarPortal">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0 text-start gap-1">
                <li class="nav-item">
                    <a class="nav-link px-3 py-2 rounded-pill fw-bold text-dark" href="${pageContext.request.contextPath}/home">
                        Trang chủ
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link px-3 py-2 rounded-pill fw-bold text-dark" href="${pageContext.request.contextPath}/products">
                        Thực đơn
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link px-3 py-2 rounded-pill fw-bold text-dark d-flex align-items-center gap-1" href="${pageContext.request.contextPath}/profile/vouchers">
                        <i class="bi bi-ticket-perforated-fill text-warning"></i> Ví Voucher
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link px-3 py-2 rounded-pill fw-bold text-dark d-flex align-items-center gap-1" href="${pageContext.request.contextPath}/profile/orders">
                        <i class="bi bi-clock-history text-muted"></i> Theo dõi đơn
                    </a>
                </li>
            </ul>
        </div>

        <!-- PORTAL LIVE SEARCH AUTOCOMPLETE INPUT -->
        <div class="position-relative mx-lg-3 my-2 my-lg-0 flex-grow-1 d-none d-xl-block order-lg-3" style="max-width: 340px;">
            <div class="input-group input-group-sm">
                <span class="input-group-text bg-light border-end-0 rounded-start-pill ps-3 text-muted"><i class="bi bi-search"></i></span>
                <input type="text" id="portalSearchInput" class="form-control bg-light border-start-0 rounded-end-pill py-2" placeholder="Tìm kiếm đồ uống, topping..." autocomplete="off" oninput="handlePortalLiveSearch(this.value)" onfocus="handlePortalLiveSearch(this.value)">
            </div>
            <!-- FLOATING AUTOCOMPLETE DROPDOWN -->
            <div id="portalLiveSearchDropdown" class="position-absolute start-0 w-100 bg-white shadow-xl rounded-4 border mt-1 p-2" style="display: none; max-height: 380px; overflow-y: auto; z-index: 1080;">
                <!-- Populated dynamically via handlePortalLiveSearch() -->
            </div>
        </div>

        <!-- RIGHT ACTION BUTTONS (WISHLIST, CART, AUTH) -->
        <div class="d-flex align-items-center gap-2 ms-auto order-lg-4">
            <!-- Wishlist Icon Button -->
            <button type="button" class="btn btn-outline-danger position-relative p-2 rounded-circle border-0 bg-light" style="width: 40px; height: 40px; display: flex; align-items: center; justify-content: center;" onclick="openWishlistModal()" title="Danh sách món yêu thích">
                <i class="bi bi-heart fs-5" id="headerWishlistHeartIcon"></i>
                <span id="portalWishlistBadge" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger d-flex align-items-center justify-content-center" style="font-size: 10px; width: 18px; height: 18px; padding: 0; border: 1.5px solid #ffffff; display: none !important;">
                    0
                </span>
            </button>

            <!-- Cart Icon Button -->
            <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-success position-relative p-2 rounded-circle border-0 bg-light text-success" style="width: 40px; height: 40px; display: flex; align-items: center; justify-content: center;" title="Giỏ hàng">
                <i class="bi bi-cart3 fs-5"></i>
                <c:if test="${not empty sessionScope.customerCartCount && sessionScope.customerCartCount > 0}">
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger d-flex align-items-center justify-content-center shadow-sm" style="font-size: 10.5px; width: 22px; height: 22px; padding: 0; border: 2px solid #ffffff;">
                        ${sessionScope.customerCartCount}
                    </span>
                </c:if>
            </a>

            <!-- Account Profile Dropdown -->
            <c:choose>
                <c:when test="${not empty sessionScope.customer}">
                    <div class="dropdown">
                        <a class="dropdown-toggle text-decoration-none d-flex align-items-center gap-1.5 p-1 rounded-pill bg-light border" href="#" role="button" id="customerDropdownMenu" data-bs-toggle="dropdown" aria-expanded="false" style="padding-right: 10px !important;">
                            <img src="${not empty sessionScope.customer.hinhAnhUrl && sessionScope.customer.hinhAnhUrl ne 'None' ? sessionScope.customer.hinhAnhUrl : 'https://cdn-icons-png.flaticon.com/512/3177/3177440.png'}"
                                 alt="Avatar" class="rounded-circle border border-2 border-success" style="width: 32px; height: 32px; object-fit: cover;">
                            <span class="fw-bold text-dark small d-none d-sm-inline text-truncate" style="max-width: 110px;"><c:out value="${sessionScope.customer.tenKh}"/></span>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end shadow-xl border-0 mt-2 py-2 rounded-4" aria-labelledby="customerDropdownMenu" style="min-width: 220px;">
                            <li class="px-3 py-2 border-bottom border-light mb-1 text-start">
                                <small class="text-muted d-block" style="font-size:10px; text-transform: uppercase; letter-spacing: 0.5px; font-weight: 700;">Hội viên CRM VIP</small>
                                <strong class="text-dark d-block text-truncate"><c:out value="${sessionScope.customer.tenKh}"/></strong>
                                <span class="badge bg-success bg-opacity-10 text-success mt-1" style="font-size: 11px;">
                                    <i class="bi bi-coin me-1"></i>${sessionScope.customer.diemTichLuy} điểm
                                </span>
                            </li>
                            <li>
                                <a class="dropdown-item py-2 d-flex align-items-center gap-2 small fw-semibold" href="${pageContext.request.contextPath}/profile">
                                    <i class="bi bi-person-vcard text-muted fs-6"></i>
                                    <span>Hồ sơ cá nhân</span>
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item py-2 d-flex align-items-center gap-2 small fw-semibold" href="${pageContext.request.contextPath}/profile/orders">
                                    <i class="bi bi-clock-history text-muted fs-6"></i>
                                    <span>Lịch sử đặt nước</span>
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item py-2 d-flex align-items-center gap-2 small fw-semibold text-success" href="${pageContext.request.contextPath}/profile/vouchers">
                                    <i class="bi bi-ticket-perforated-fill fs-6"></i>
                                    <span>Ví Voucher cá nhân</span>
                                </a>
                            </li>
                            <li><hr class="dropdown-divider my-1"></li>
                            <li>
                                <a class="dropdown-item py-2 text-danger fw-bold d-flex align-items-center gap-2 small" href="${pageContext.request.contextPath}/logout">
                                    <i class="bi bi-box-arrow-right fs-6"></i>
                                    <span>Đăng xuất</span>
                                </a>
                            </li>
                        </ul>
                    </div>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/customer/login" class="btn btn-primary-teapos fw-bold px-3 py-2 btn-sm rounded-pill" style="font-size: 12.5px;">
                        <i class="bi bi-person-fill"></i> ĐĂNG NHẬP
                    </a>
                </c:otherwise>
            </c:choose>

            <!-- Navbar Toggler for mobile menus -->
            <button class="navbar-toggler border-0 p-1 d-lg-none" type="button" data-bs-toggle="collapse" data-bs-target="#navbarPortal">
                <span class="navbar-toggler-icon" style="width: 22px; height: 22px;"></span>
            </button>
        </div>
    </div>
</nav>

<!-- WISHLIST MODAL -->
<div class="modal fade" id="portalWishlistModal" tabindex="-1" aria-hidden="true" style="z-index: 1070;">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content border-0 shadow-xl" style="border-radius: 18px; overflow: hidden;">
            <div class="modal-header bg-danger text-white py-3">
                <h5 class="modal-title fw-bold m-0 d-flex align-items-center gap-2">
                    <i class="bi bi-heart-fill"></i> MÓN ĐỒ UỐNG YÊU THÍCH
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-3 bg-light text-start" id="portalWishlistContent">
                <!-- Rendered dynamically via renderWishlistModal() -->
            </div>
            <div class="modal-footer bg-light p-3 border-top d-flex justify-content-between">
                <button type="button" class="btn btn-outline-secondary btn-sm px-3 rounded-pill fw-bold" onclick="clearWishlist()">Xóa tất cả</button>
                <button type="button" class="btn btn-secondary btn-sm px-4 fw-bold rounded-pill" data-bs-dismiss="modal">Đóng</button>
            </div>
        </div>
    </div>
</div>
