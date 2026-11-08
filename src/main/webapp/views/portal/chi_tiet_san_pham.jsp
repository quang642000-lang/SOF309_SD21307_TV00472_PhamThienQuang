<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>TEA POS - Cấu Hình Pha Chế Đồ Uống</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.2/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/global.css?v=2026.11.06" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/portal.css?v=2026.11.06" rel="stylesheet">
    <style>
        .selection-pill {
            border-radius: 12px !important;
            transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
            font-size: 13px;
            padding: 10px 8px;
            border: 1.5px solid var(--border-color) !important;
            background-color: #ffffff;
            color: var(--text-main) !important;
        }
        .btn-check:checked + .selection-pill {
            background-color: var(--primary-light) !important;
            border-color: var(--primary) !important;
            color: var(--primary-dark) !important;
            font-weight: 700 !important;
            box-shadow: 0 4px 12px rgba(5, 150, 105, 0.15) !important;
        }
        .topping-card {
            border-radius: 12px;
            transition: all 0.2s ease;
            background: #ffffff;
            border: 1.5px solid var(--border-color);
        }
        .topping-card:hover {
            border-color: var(--primary);
            box-shadow: 0 4px 12px rgba(15, 23, 42, 0.05);
        }
        .sticky-order-bar {
            position: fixed;
            bottom: 0;
            left: 0;
            right: 0;
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(16px);
            -webkit-backdrop-filter: blur(16px);
            border-top: 1.5px solid var(--border-color);
            box-shadow: 0 -8px 24px rgba(15, 23, 42, 0.08);
            z-index: 1020;
            padding: 12px 0;
        }
        @media (min-width: 768px) {
            .sticky-order-bar { display: none; }
        }
    </style>
</head>
<body class="bg-light pb-5 mb-md-0">
<!-- NAV HEADER -->
<jsp:include page="/views/layout/header_portal.jsp" />

<div class="container py-4">
    <!-- Breadcrumb điều hướng -->
    <nav aria-label="breadcrumb" class="mb-3">
        <ol class="breadcrumb small">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none text-muted">Trang chủ</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/products" class="text-decoration-none text-muted">Thực đơn</a></li>
            <li class="breadcrumb-item active text-success fw-bold" aria-current="page"><c:out value="${product.tenSp}"/></li>
        </ol>
    </nav>

    <div class="row g-4 g-lg-5">
        <!-- ẢNH ĐỒ UỐNG BÊN TRÁI -->
        <div class="col-12 col-md-5 text-start">
            <div class="sticky-top" style="top: 85px;">
                <div class="position-relative overflow-hidden rounded-4 shadow-sm border bg-white p-2">
                    <img src="${not empty product.hinhAnh ? product.hinhAnh : 'https://cdn-icons-png.flaticon.com/512/3177/3177440.png'}" class="w-100 rounded-3 detail-product-img" style="height: 380px; object-fit: cover;" alt="Tea">
                    <c:if test="${product.isBestseller}">
                        <span class="position-absolute top-0 start-0 badge bg-danger text-white fw-bold m-3 px-3 py-2 shadow-sm rounded-pill" style="font-size: 11px;">HOT BESTSELLER 🔥</span>
                    </c:if>
                </div>
                <!-- Cam kết chất lượng chuẩn F&B -->
                <div class="card border-0 bg-white shadow-sm p-3 mt-3 rounded-3" style="border: 1px solid var(--border-color) !important;">
                    <div class="d-flex align-items-center gap-3">
                        <i class="bi bi-shield-check text-success fs-3"></i>
                        <div>
                            <h6 class="fw-bold mb-0 text-dark small">Pha Chế Thủ Công Tươi Mới</h6>
                            <small class="text-muted" style="font-size: 11px;">100% lá trà hữu cơ nguyên bản, sữa tươi thanh trùng chuẩn vị.</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- BẢNG TÙY BIẾN PHA CHẾ BÊN PHẢI -->
        <div class="col-12 col-md-7">
            <div class="card p-4 border-0 shadow-sm bg-white" style="border-radius: 16px; border: 1.5px solid var(--border-color) !important;">
                <div class="text-start">
                    <span class="badge bg-success bg-opacity-10 text-success border border-success mb-2 px-3 py-1.5 fw-bold text-uppercase d-inline-block" style="max-width: fit-content; font-size: 11px;">Mã món: ${product.maSp}</span>
                    <h3 class="fw-bold mb-2 text-dark" style="letter-spacing: -0.5px;"><c:out value="${product.tenSp}"/></h3>
                    <p class="text-muted mb-4 small" style="line-height: 1.6;"><c:out value="${product.moTa}"/></p>
                </div>

                <form id="addToCartForm" action="${pageContext.request.contextPath}/cart/add" method="POST">
                    <input type="hidden" name="maSp" value="${product.maSp}">
                    <c:if test="${not empty editItem}">
                        <input type="hidden" name="maCtgh" value="${editItem.maCtgh}">
                    </c:if>

                    <!-- 1. CHỌN SIZE (HEYTEA SEGMENTED PILLS) -->
                    <div class="mb-4 text-start option-group-box">
                        <label class="option-title d-block mb-2.5 fw-bold text-dark small text-uppercase" style="letter-spacing: 0.5px;">
                            <i class="bi bi-cup-hot-fill text-success me-1"></i> 1. Kích Cỡ Cốc Nước (Size) <span class="text-danger">*</span>
                        </label>
                        <div class="row g-2">
                            <c:forEach var="size" items="${sizes}" varStatus="loop">
                                <c:set var="isSizeChecked" value="false"/>
                                <c:choose>
                                    <c:when test="${not empty editItem}">
                                        <c:if test="${editItem.maSize == size.maSize}">
                                            <c:set var="isSizeChecked" value="true"/>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${loop.first}">
                                            <c:set var="isSizeChecked" value="true"/>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>
                                <div class="col-4">
                                    <input type="radio" class="btn-check selection-radio-input" name="maSize" id="size_${size.maSize}" value="${size.maSize}" data-price="${size.giaBan}" ${isSizeChecked == 'true' ? 'checked' : ''} onchange="calculateRealtimeTotal()">
                                    <label class="btn btn-outline-success py-2.5 w-100 text-center fw-bold selection-pill" for="size_${size.maSize}">
                                        <span class="fs-6">Size ${size.tenSize == '1' ? "S" : (size.tenSize == '2' ? "M" : (size.tenSize == '3' ? "L" : size.tenSize))}</span> <br>
                                        <small class="text-muted fw-normal" style="font-size: 11px;">+<fmt:formatNumber value="${size.giaBan}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ</small>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>

                    <!-- 2. ĐÁ & ĐƯỜNG (HEYTEA VISUAL PILLS SELECTOR - THAY THẾ DROPDOWN) -->
                    <c:if test="${product.choPhepDoiDa}">
                        <div class="mb-4 text-start option-group-box">
                            <label class="option-title d-block mb-2.5 fw-bold text-dark small text-uppercase" style="letter-spacing: 0.5px;">
                                <i class="bi bi-snow text-info me-1"></i> 2. Mức Độ Đá
                            </label>
                            <div class="row g-2">
                                <div class="col-3">
                                    <input type="radio" class="btn-check" name="mucDa" id="da_100" value="100%" ${editItem.mucDa eq '100%' || empty editItem ? 'checked' : ''} onchange="calculateRealtimeTotal()">
                                    <label class="btn btn-outline-success py-2 w-100 text-center fw-bold selection-pill" for="da_100">
                                        100% Đá<br><small class="text-muted fw-normal" style="font-size: 10.5px;">Chuẩn vị</small>
                                    </label>
                                </div>
                                <div class="col-3">
                                    <input type="radio" class="btn-check" name="mucDa" id="da_70" value="70%" ${editItem.mucDa eq '70%' ? 'checked' : ''} onchange="calculateRealtimeTotal()">
                                    <label class="btn btn-outline-success py-2 w-100 text-center fw-bold selection-pill" for="da_70">
                                        70% Đá<br><small class="text-muted fw-normal" style="font-size: 10.5px;">Ít đá</small>
                                    </label>
                                </div>
                                <div class="col-3">
                                    <input type="radio" class="btn-check" name="mucDa" id="da_50" value="50%" ${editItem.mucDa eq '50%' ? 'checked' : ''} onchange="calculateRealtimeTotal()">
                                    <label class="btn btn-outline-success py-2 w-100 text-center fw-bold selection-pill" for="da_50">
                                        50% Đá<br><small class="text-muted fw-normal" style="font-size: 10.5px;">Vừa phải</small>
                                    </label>
                                </div>
                                <div class="col-3">
                                    <input type="radio" class="btn-check" name="mucDa" id="da_0" value="0%" ${editItem.mucDa eq '0%' ? 'checked' : ''} onchange="calculateRealtimeTotal()">
                                    <label class="btn btn-outline-success py-2 w-100 text-center fw-bold selection-pill" for="da_0">
                                        0% Đá<br><small class="text-muted fw-normal" style="font-size: 10.5px;">Không đá</small>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <c:if test="${product.choPhepDoiDuong}">
                        <div class="mb-4 text-start option-group-box">
                            <label class="option-title d-block mb-2.5 fw-bold text-dark small text-uppercase" style="letter-spacing: 0.5px;">
                                <i class="bi bi-droplet-half text-warning me-1"></i> 3. Mức Độ Ngọt (Đường)
                            </label>
                            <div class="row g-2">
                                <div class="col-3">
                                    <input type="radio" class="btn-check" name="mucDuong" id="duong_100" value="100%" ${editItem.mucDuong eq '100%' || empty editItem ? 'checked' : ''} onchange="calculateRealtimeTotal()">
                                    <label class="btn btn-outline-success py-2 w-100 text-center fw-bold selection-pill" for="duong_100">
                                        100% Đường<br><small class="text-muted fw-normal" style="font-size: 10.5px;">Chuẩn vị</small>
                                    </label>
                                </div>
                                <div class="col-3">
                                    <input type="radio" class="btn-check" name="mucDuong" id="duong_70" value="70%" ${editItem.mucDuong eq '70%' ? 'checked' : ''} onchange="calculateRealtimeTotal()">
                                    <label class="btn btn-outline-success py-2 w-100 text-center fw-bold selection-pill" for="duong_70">
                                        70% Đường<br><small class="text-muted fw-normal" style="font-size: 10.5px;">Ít ngọt</small>
                                    </label>
                                </div>
                                <div class="col-3">
                                    <input type="radio" class="btn-check" name="mucDuong" id="duong_50" value="50%" ${editItem.mucDuong eq '50%' ? 'checked' : ''} onchange="calculateRealtimeTotal()">
                                    <label class="btn btn-outline-success py-2 w-100 text-center fw-bold selection-pill" for="duong_50">
                                        50% Đường<br><small class="text-muted fw-normal" style="font-size: 10.5px;">Vừa phải</small>
                                    </label>
                                </div>
                                <div class="col-3">
                                    <input type="radio" class="btn-check" name="mucDuong" id="duong_0" value="0%" ${editItem.mucDuong eq '0%' ? 'checked' : ''} onchange="calculateRealtimeTotal()">
                                    <label class="btn btn-outline-success py-2 w-100 text-center fw-bold selection-pill" for="duong_0">
                                        0% Đường<br><small class="text-muted fw-normal" style="font-size: 10.5px;">Không đường</small>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <!-- 3. TOPPING (HEYTEA CARDS) -->
                    <div class="mb-4 text-start option-group-box">
                        <c:choose>
                            <c:when test="${product.choPhepTopping}">
                                <label class="option-title d-block mb-2.5 fw-bold text-dark small text-uppercase" style="letter-spacing: 0.5px;">
                                    <i class="bi bi-stars text-warning me-1"></i> 4. Thêm Topping Thơm Ngon (Chọn nhiều)
                                </label>
                                <div class="row g-2">
                                    <c:forEach var="tp" items="${toppings}">
                                        <c:set var="isTpChecked" value="false"/>
                                        <c:set var="tpQty" value="1"/>
                                        <c:if test="${not empty editItem}">
                                            <c:forEach var="et" items="${editItem.toppingGioHangList}">
                                                <c:if test="${et.maTp == tp.maTp}">
                                                    <c:set var="isTpChecked" value="true"/>
                                                    <c:set var="tpQty" value="${et.soLuongTp}"/>
                                                </c:if>
                                            </c:forEach>
                                        </c:if>
                                        <div class="col-12 col-md-6">
                                            <div class="topping-card p-2.5 d-flex justify-content-between align-items-center">
                                                <div class="form-check mb-0 d-flex align-items-center flex-grow-1">
                                                    <input class="form-check-input topping-check me-2" type="checkbox" name="toppings[]" id="tp_${tp.maTp}" value="${tp.maTp}" data-price="${tp.giaBan}" onchange="toggleWebToppingQty('${tp.maTp}')" ${isTpChecked == 'true' ? 'checked' : ''}>
                                                    <label class="form-check-label fw-semibold text-dark small" for="tp_${tp.maTp}">
                                                        <c:out value="${tp.tenTp}"/> <br>
                                                        <span class="text-success fw-bold font-monospace" style="font-size: 11.5px;">+<fmt:formatNumber value="${tp.giaBan}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ</span>
                                                    </label>
                                                </div>
                                                <div class="input-group input-group-sm" id="web_tp_qty_container_${tp.maTp}" style="${isTpChecked == 'true' ? 'display: flex !important;' : 'display: none !important;'}">
                                                    <button type="button" class="btn btn-outline-secondary px-2 py-0 border-opacity-50" onclick="adjustWebToppingQty('${tp.maTp}', -1)">-</button>
                                                    <input type="text" id="web_tp_qty_${tp.maTp}" name="topping_qty_${tp.maTp}" class="form-control text-center p-0 fw-bold" value="${tpQty}" readonly style="font-size: 11px; height: 24px; background-color: #ffffff; width:30px;">
                                                    <button type="button" class="btn btn-outline-secondary px-2 py-0 text-success border-opacity-50" onclick="adjustWebToppingQty('${tp.maTp}', 1)">+</button>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="p-3 bg-light rounded border border-dashed text-center">
                                    <span class="text-muted small fw-semibold"><i class="bi bi-info-circle text-warning"></i> Món ăn / bánh ngọt này không áp dụng ăn kèm Topping!</span>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- 4. GHI CHÚ -->
                    <div class="mb-4 text-start option-group-box">
                        <label for="ghiChuMon" class="option-title d-block mb-2 fw-bold text-dark small text-uppercase" style="letter-spacing: 0.5px;">
                            <i class="bi bi-pencil-square text-success me-1"></i> 5. Dặn Dò Thợ Pha Chế
                        </label>
                        <textarea class="form-control form-control-teapos" id="ghiChuMon" name="ghiChuMon" rows="2" placeholder="Ví dụ: Ly đá để riêng, đóng túi xách mang đi xa..."><c:out value="${not empty editItem ? editItem.ghiChuMon : ''}"/></textarea>
                    </div>

                    <!-- 5. TỔNG TIỀN VÀ SỐ LƯỢNG (DESKTOP) -->
                    <div class="d-flex align-items-center justify-content-between border-top pt-4 mb-4">
                        <div class="text-start">
                            <span class="text-muted d-block small fw-medium">Tổng giá trị cốc nước:</span>
                            <span class="fw-bold text-success fs-3" id="displayTotal">0 đ</span>
                        </div>
                        <div class="d-flex align-items-center gap-2">
                            <button type="button" class="btn btn-outline-secondary px-3 py-2 rounded-circle d-flex align-items-center justify-content-center" style="width: 38px; height: 38px;" onclick="adjustQty(-1)"><i class="bi bi-dash-lg"></i></button>
                            <input type="number" id="qtyInput" name="soLuong" class="form-control text-center fw-bold fs-5 px-0" value="${not empty editItem ? editItem.soLuong : 1}" min="1" readonly style="width: 50px; border: 0; background: transparent;">
                            <button type="button" class="btn btn-outline-secondary px-3 py-2 rounded-circle d-flex align-items-center justify-content-center" style="width: 38px; height: 38px;" onclick="adjustQty(1)"><i class="bi bi-plus-lg"></i></button>
                        </div>
                    </div>

                    <!-- BỘ ĐÔI NÚT ACTION DESKTOP -->
                    <div class="row g-3 d-none d-md-flex">
                        <c:choose>
                            <c:when test="${not empty editItem}">
                                <div class="col-12">
                                    <button type="button" class="btn btn-success w-100 py-3 fw-bold fs-6 rounded-pill d-flex align-items-center justify-content-center gap-2 btn-primary-teapos" onclick="handleCartAction('edit')">
                                        <i class="bi bi-check-circle-fill"></i> CẬP NHẬT GIỎ HÀNG
                                    </button>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="col-6">
                                    <button type="button" class="btn btn-outline-success w-100 py-3 fw-bold fs-6 rounded-pill d-flex align-items-center justify-content-center gap-2" style="color:var(--primary); border-color:var(--primary);" onclick="handleCartAction('add')">
                                        <i class="bi bi-bag-plus-fill"></i> THÊM VÀO GIỎ
                                    </button>
                                </div>
                                <div class="col-6">
                                    <button type="button" class="btn btn-success w-100 py-3 fw-bold fs-6 rounded-pill d-flex align-items-center justify-content-center gap-2 btn-primary-teapos" onclick="handleCartAction('buy')">
                                        <i class="bi bi-lightning-charge-fill"></i> MUA NGAY ⚡
                                    </button>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- STICKY ORDER BOTTOM BAR TRÊN MOBILE (CHUẨN HEYTEA & GONG CHA) -->
<div class="sticky-order-bar d-md-none">
    <div class="container d-flex align-items-center justify-content-between px-3">
        <div class="text-start">
            <small class="text-muted d-block" style="font-size: 11px;">Tổng cộng:</small>
            <strong class="text-success fs-5" id="displayTotalMobile">0 đ</strong>
        </div>
        <div class="d-flex gap-2">
            <button type="button" class="btn btn-sm btn-outline-success rounded-pill fw-bold px-3 py-2" onclick="handleCartAction('add')">
                <i class="bi bi-bag-plus-fill"></i> Thêm giỏ
            </button>
            <button type="button" class="btn btn-sm btn-success rounded-pill fw-bold px-3 py-2 btn-primary-teapos" onclick="handleCartAction('buy')">
                Mua ngay ⚡
            </button>
        </div>
    </div>
</div>

<!-- FOOTER -->
<jsp:include page="/views/layout/footer_portal.jsp" />

<script>
    function toggleWebToppingQty(maTp) {
        const chk = document.getElementById('tp_' + maTp);
        const container = document.getElementById('web_tp_qty_container_' + maTp);
        const qtyInput = document.getElementById('web_tp_qty_' + maTp);
        if (chk && container && qtyInput) {
            if (chk.checked) {
                container.style.setProperty('display', 'flex', 'important');
                qtyInput.value = 1;
            } else {
                container.style.setProperty('display', 'none', 'important');
                qtyInput.value = 1;
            }
        }
        calculateRealtimeTotal();
    }

    function adjustWebToppingQty(maTp, delta) {
        const qtyInput = document.getElementById('web_tp_qty_' + maTp);
        if (qtyInput) {
            let val = parseInt(qtyInput.value) || 1;
            val += delta;
            if (val < 1) val = 1;
            qtyInput.value = val;
        }
        calculateRealtimeTotal();
    }

    function calculateRealtimeTotal() {
        let total = 0;
        const checkedSize = document.querySelector('input[name="maSize"]:checked');
        if (checkedSize) {
            total += parseInt(checkedSize.dataset.price);
        }
        const activeToppings = document.querySelectorAll('.topping-check:checked');
        activeToppings.forEach(tp => {
            const tpId = tp.value;
            const qtyInput = document.getElementById('web_tp_qty_' + tpId);
            const qty = qtyInput ? parseInt(qtyInput.value) : 1;
            total += parseInt(tp.dataset.price) * qty;
        });
        const qty = parseInt(document.getElementById('qtyInput').value);
        const finalPrice = total * qty;

        const priceLabel = finalPrice.toLocaleString('vi-VN') + ' đ';
        const displayTotalEl = document.getElementById('displayTotal');
        const displayTotalMobileEl = document.getElementById('displayTotalMobile');
        if (displayTotalEl) displayTotalEl.innerText = priceLabel;
        if (displayTotalMobileEl) displayTotalMobileEl.innerText = priceLabel;
    }

    function adjustQty(amount) {
        const input = document.getElementById('qtyInput');
        let currentVal = parseInt(input.value);
        currentVal += amount;
        if (currentVal < 1) currentVal = 1;
        input.value = currentVal;
        calculateRealtimeTotal();
    }

    function handleCartAction(action) {
        const form = document.getElementById("addToCartForm");
        const formData = new FormData(form);
        formData.append('action', action);

        Swal.fire({
            title: 'Đang kết nối hệ thống...',
            allowOutsideClick: false,
            didOpen: () => { Swal.showLoading(); }
        });

        fetch(form.action, {
            method: 'POST',
            headers: { 'X-Requested-With': 'XMLHttpRequest' },
            body: new URLSearchParams(formData)
        })
            .then(res => {
                if (res.status === 401) {
                    window.location.href = '${pageContext.request.contextPath}/customer/login';
                    throw new Error('SESSION_EXPIRED');
                }
                return res.text();
            })
            .then(cleanData => {
                Swal.close();
                if (cleanData.trim() === 'SESSION_EXPIRED' || cleanData.trim() === 'NOT_LOGGED_IN') {
                    window.location.href = '${pageContext.request.contextPath}/customer/login';
                    return;
                }
                if (cleanData.startsWith('SUCCESS')) {
                    const parts = cleanData.split('|');
                    const cartCount = parts.length > 1 ? parts[1] : '0';
                    const badge = document.querySelector('.navbar .badge');
                    if (badge) {
                        badge.innerText = cartCount;
                        badge.style.display = 'flex';
                    }
                    if (action === 'buy') {
                        window.location.href = '${pageContext.request.contextPath}/checkout';
                    } else if (action === 'edit') {
                        Swal.fire({
                            icon: 'success',
                            title: 'Đã lưu thay đổi!',
                            text: 'Cấu hình ly nước này đã được cập nhật thành công trong giỏ hàng.',
                            confirmButtonColor: '#059669',
                            confirmButtonText: 'Quay lại giỏ hàng'
                        }).then(() => {
                            window.location.href = '${pageContext.request.contextPath}/cart';
                        });
                    } else {
                        Swal.fire({
                            icon: 'success',
                            title: 'Đã xếp vào giỏ hàng!',
                            text: 'Món uống của quý khách đã được thêm thành công với cấu hình pha chế đã chọn.',
                            showCancelButton: true,
                            confirmButtonColor: '#059669',
                            cancelButtonColor: '#64748b',
                            confirmButtonText: 'Đến giỏ hàng xem ngay',
                            cancelButtonText: 'Tiếp tục xem đồ uống khác'
                        }).then((result) => {
                            if (result.isConfirmed) {
                                window.location.href = '${pageContext.request.contextPath}/cart';
                            }
                        });
                    }
                } else {
                    showToast('error', 'Thao tác giỏ hàng thất bại! Vui lòng thử lại sau.');
                }
            })
            .catch(err => {
                Swal.close();
                console.error('Lỗi hệ thống kết nối AJAX:', err);
            });
    }

    document.addEventListener("DOMContentLoaded", function() {
        calculateRealtimeTotal();
    });
</script>
</body>
</html>
