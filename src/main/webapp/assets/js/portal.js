/**
 * =========================================================================
 * TEA PORTAL CUSTOMER RESPONSIVE INTERFACE SCRIPT (v3.0)
 * Handles offcanvas category sidebars, sticky purchase bars, and clipboard copying.
 * =========================================================================
 */

function getContextPath() {
    return window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));
}

// Global Toast notification utility
function showToast(icon, message) {
    if (typeof Swal !== 'undefined') {
        const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 2500,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', Swal.stopTimer);
                toast.addEventListener('mouseleave', Swal.resumeTimer);
            }
        });
        Toast.fire({
            icon: icon,
            title: message
        });
    } else {
        alert(message);
    }
}

document.addEventListener("DOMContentLoaded", function() {
    // 1. Sidebar toggler setup for mobile category selection
    const sidebarFilter = document.querySelector('.col-12.col-lg-3.text-start');
    if (sidebarFilter) {
        // Create offcanvas trigger button dynamically
        if (!document.getElementById("portalMobileFilterTriggerBtn") && window.innerWidth <= 768) {
            const filterBtn = document.createElement("button");
            filterBtn.id = "portalMobileFilterTriggerBtn";
            filterBtn.type = "button";
            filterBtn.className = "portal-mobile-filter-trigger";
            filterBtn.innerHTML = '<i class="bi bi-funnel-fill text-white"></i> <span>DANH MỤC & LỌC</span>';
            filterBtn.onclick = togglePortalFilter;
            document.body.appendChild(filterBtn);
        }

        // Create backdrop for filtering
        if (!document.getElementById("portalFilterBackdrop")) {
            const backdrop = document.createElement("div");
            backdrop.id = "portalFilterBackdrop";
            backdrop.className = "portal-filter-backdrop";
            backdrop.onclick = togglePortalFilter;
            document.body.appendChild(backdrop);
        }
    }

    // 2. Quantity Input Listeners for Client Cart page
    document.querySelectorAll('.qty-input-portal').forEach(input => {
        input.addEventListener('change', function() {
            const maCtgh = this.dataset.mactgh;
            const soLuong = this.value;
            updatePortalCartQuantity(maCtgh, soLuong);
        });
    });

    // 3. Dynamic Sticky Bottom Bars for Mobile Screens
    const path = window.location.pathname;

    // CASE A: Cart page sticky bottom checkout bar
    if (path.includes("/cart") && !document.getElementById("portalMobileCheckoutBar") && window.innerWidth <= 768) {
        injectMobileCheckoutBar();
    }

    // CASE B: Product Detail page sticky bottom buy bar
    if (path.includes("/product/detail") && !document.getElementById("portalMobileBuyBar") && window.innerWidth <= 768) {
        injectMobileBuyBar();
    }
});

function togglePortalFilter() {
    const sidebar = document.querySelector('.col-12.col-lg-3.text-start');
    const backdrop = document.getElementById('portalFilterBackdrop');
    if (sidebar && backdrop) {
        sidebar.classList.toggle('show');
        backdrop.classList.toggle('show');
        if (sidebar.classList.contains('show')) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = '';
        }
    }
}

function updatePortalCartQuantity(maCtgh, soLuong) {
    const formData = new FormData();
    formData.append('maCtgh', maCtgh);
    formData.append('soLuong', soLuong);

    fetch(getContextPath() + '/cart/update', {
        method: 'POST',
        headers: { 'X-Requested-With': 'XMLHttpRequest' },
        body: new URLSearchParams(formData)
    })
        .then(res => {
            if (res.status === 401) {
                window.location.href = getContextPath() + '/customer/login';
                throw new Error('SESSION_EXPIRED');
            }
            return res.text();
        })
        .then(data => {
            if (data.trim() === 'SUCCESS') {
                setTimeout(() => { location.reload(); }, 600);
            } else {
                showToast('error', 'Cập nhật số lượng thất bại!');
            }
        })
        .catch(err => console.error('Lỗi:', err));
}

function toggleSelectCartItem(maCtgh, checkboxElement) {
    const isChecked = checkboxElement.checked ? "1" : "0";
    const formData = new FormData();
    formData.append('maCtgh', maCtgh);
    formData.append('chon', isChecked);

    fetch(getContextPath() + '/cart/toggle-select', {
        method: 'POST',
        headers: { 'X-Requested-With': 'XMLHttpRequest' },
        body: new URLSearchParams(formData)
    })
        .then(res => {
            if (res.status === 401) {
                window.location.href = getContextPath() + '/customer/login';
                throw new Error('SESSION_EXPIRED');
            }
            return res.text();
        })
        .then(data => {
            if (data.trim() === 'SUCCESS') {
                setTimeout(() => { location.reload(); }, 600);
            } else {
                showToast('error', 'Xử lý lỗi hệ thống!');
            }
        })
        .catch(err => console.error('Lỗi:', err));
}

function quickAddToCart(maSp, tenSp, maSize) {
    const formData = new FormData();
    formData.append('maSp', maSp);
    formData.append('maSize', maSize ? maSize : '1');
    formData.append('soLuong', '1');
    formData.append('mucDa', '100%');
    formData.append('mucDuong', '100%');
    formData.append('ghiChuMon', 'Quick Add');

    fetch(getContextPath() + '/cart/add', {
        method: 'POST',
        headers: { 'X-Requested-With': 'XMLHttpRequest' },
        body: new URLSearchParams(formData)
    })
        .then(res => {
            if (res.status === 401) {
                window.location.href = getContextPath() + '/customer/login';
                throw new Error('SESSION_EXPIRED');
            }
            return res.json();
        })
        .then(data => {
            if (data.status === 'SUCCESS') {
                showToast('success', 'Đã thêm nhanh ' + tenSp + ' vào giỏ hàng!');
                const badge = document.querySelector('.navbar .badge');
                if (badge) {
                    badge.innerText = data.cartCount;
                    badge.style.display = 'flex';
                }
            } else {
                showToast('error', data.message || 'Thao tác thất bại!');
            }
        })
        .catch(err => console.error('Lỗi:', err));
}

function copyVoucherCode(code) {
    navigator.clipboard.writeText(code).then(() => {
        showToast('success', 'Đã sao chép mã giảm giá: ' + code);
    });
}

function injectMobileCheckoutBar() {
    const totalEl = document.getElementById("finalPayableCart");
    if (!totalEl) return;

    const bar = document.createElement("div");
    bar.id = "portalMobileCheckoutBar";
    bar.className = "portal-mobile-checkout-bar";

    const info = document.createElement("div");
    info.className = "portal-mobile-checkout-info";
    info.innerHTML = `<span>Tổng thanh toán:</span><strong id="mobileTotalDisplay">${totalEl.innerText}</strong>`;

    const checkoutBtn = document.getElementById("checkoutBtn");
    const isBtnDisabled = checkoutBtn ? checkoutBtn.classList.contains("disabled") : true;

    const actionBtn = document.createElement("button");
    actionBtn.type = "button";
    actionBtn.className = "btn " + (isBtnDisabled ? "btn-secondary disabled" : "btn-success") + " portal-mobile-checkout-btn";
    actionBtn.innerText = isBtnDisabled ? "Chưa chọn món" : "Đặt nước ngay";
    actionBtn.onclick = function() {
        if (!isBtnDisabled && checkoutBtn) {
            checkoutBtn.click();
        }
    };

    bar.appendChild(info);
    bar.appendChild(actionBtn);
    document.body.appendChild(bar);

    // Observer price changes
    const observer = new MutationObserver(function() {
        document.getElementById("mobileTotalDisplay").innerText = totalEl.innerText;
        const currentCheckoutBtn = document.getElementById("checkoutBtn");
        const isDisabledNow = currentCheckoutBtn ? currentCheckoutBtn.classList.contains("disabled") : true;
        actionBtn.className = "btn " + (isDisabledNow ? "btn-secondary disabled" : "btn-success") + " portal-mobile-checkout-btn";
        actionBtn.innerText = isDisabledNow ? "Chưa chọn món" : "Đặt nước ngay";
    });
    observer.observe(totalEl, { childList: true, characterData: true, subtree: true });
}

function injectMobileBuyBar() {
    const totalEl = document.getElementById("displayTotal");
    if (!totalEl) return;

    const bar = document.createElement("div");
    bar.id = "portalMobileBuyBar";
    bar.className = "portal-mobile-buy-bar";

    const info = document.createElement("div");
    info.className = "portal-mobile-buy-info";
    info.innerHTML = `<span>Tổng tạm tính:</span><strong id="mobileBuyTotalDisplay">${totalEl.innerText}</strong>`;

    const btnGroup = document.createElement("div");
    btnGroup.className = "portal-mobile-buy-btn-group";

    // Add To Cart Button
    const addBtn = document.createElement("button");
    addBtn.type = "button";
    addBtn.className = "btn btn-outline-success";
    addBtn.innerHTML = '<i class="bi bi-bag-plus-fill"></i> Thêm giỏ';
    addBtn.onclick = function() {
        if (typeof handleCartAction === "function") {
            handleCartAction('add');
        }
    };

    // Buy Now Button
    const buyBtn = document.createElement("button");
    buyBtn.type = "button";
    buyBtn.className = "btn btn-success";
    buyBtn.innerHTML = 'Mua ngay ⚡';
    buyBtn.onclick = function() {
        if (typeof handleCartAction === "function") {
            handleCartAction('buy');
        }
    };

    btnGroup.appendChild(addBtn);
    btnGroup.appendChild(buyBtn);
    bar.appendChild(info);
    bar.appendChild(btnGroup);
    document.body.appendChild(bar);

    // Observer total price change
    const observer = new MutationObserver(function() {
        document.getElementById("mobileBuyTotalDisplay").innerText = totalEl.innerText;
    });
    observer.observe(totalEl, { childList: true, characterData: true, subtree: true });
}


// =========================================================================
// 4. PORTAL LIVE SEARCH AUTOCOMPLETE
// =========================================================================
let portalSearchTimeout = null;

function handlePortalLiveSearch(query) {
    const dropdown = document.getElementById("portalLiveSearchDropdown");
    if (!dropdown) return;

    query = query ? query.trim() : "";
    if (query.length < 2) {
        dropdown.style.display = "none";
        dropdown.innerHTML = "";
        return;
    }

    clearTimeout(portalSearchTimeout);
    portalSearchTimeout = setTimeout(() => {
        dropdown.innerHTML = '<div class="text-center py-3 text-muted"><div class="spinner-border spinner-border-sm text-success me-2"></div>Đang tìm kiếm...</div>';
        dropdown.style.display = "block";

        fetch(getContextPath() + '/products?search=' + encodeURIComponent(query) + '&ajax=true')
            .then(res => res.json())
            .then(data => {
                if (!Array.isArray(data) || data.length === 0) {
                    dropdown.innerHTML = '<div class="p-3 text-center text-muted small"><i class="bi bi-search me-1"></i> Không tìm thấy đồ uống nào phù hợp</div>';
                    return;
                }
                let html = '<div class="small fw-bold text-muted px-2 py-1 mb-1 border-bottom">GỢI Ý ĐỒ UỐNG PHÙ HỢP (' + data.length + ')</div>';
                data.forEach(item => {
                    const priceFormatted = new Intl.NumberFormat('vi-VN').format(item.giaBan) + ' đ';
                    const imgUrl = item.hinhAnh ? item.hinhAnh : 'https://placehold.co/60x60?text=Tea';
                    html += '<a href="' + getContextPath() + '/product/detail?id=' + encodeURIComponent(item.maSp) + '" class="d-flex align-items-center gap-2.5 p-2 text-decoration-none text-dark border-bottom hover-bg-light rounded-2 transition-all">';
                    html += '  <img src="' + imgUrl + '" class="rounded border" style="width: 44px; height: 44px; object-fit: cover;" alt="">';
                    html += '  <div class="flex-grow-1 text-start overflow-hidden">';
                    html += '    <div class="fw-semibold text-truncate small">' + item.tenSp + '</div>';
                    html += '    <div class="text-success fw-bold small">' + priceFormatted + '</div>';
                    html += '  </div>';
                    if (item.isHot) {
                        html += '  <span class="badge bg-danger small" style="font-size:9px;">HOT</span>';
                    } else if (item.isNew) {
                        html += '  <span class="badge bg-warning text-dark small" style="font-size:9px;">MỚI</span>';
                    }
                    html += '</a>';
                });
                dropdown.innerHTML = html;
            })
            .catch(err => {
                console.error("Live search error:", err);
                dropdown.innerHTML = '<div class="p-2 text-center text-danger small">Lỗi kết nối tra cứu</div>';
            });
    }, 250);
}

// Close live search dropdown when clicking outside
document.addEventListener('click', function(e) {
    const dropdown = document.getElementById("portalLiveSearchDropdown");
    const input = document.getElementById("portalSearchInput");
    if (dropdown && input && !dropdown.contains(e.target) && !input.contains(e.target)) {
        dropdown.style.display = "none";
    }
});

// =========================================================================
// 5. PORTAL WISHLIST (MÓN YÊU THÍCH)
// =========================================================================
function getWishlist() {
    try {
        const stored = localStorage.getItem('tea_pos_wishlist');
        return stored ? JSON.parse(stored) : [];
    } catch (e) {
        return [];
    }
}

function saveWishlist(list) {
    localStorage.setItem('tea_pos_wishlist', JSON.stringify(list));
    updateWishlistUI();
}


function toggleWishlistBtn(btn, event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
    }
    if (!btn) return;
    const maSp = btn.dataset.masp;
    const tenSp = btn.dataset.tensp || '';
    const giaBan = parseInt(btn.dataset.giaban) || 0;
    const hinhAnh = btn.dataset.hinhanh || '';
    toggleWishlist(maSp, tenSp, giaBan, hinhAnh, event);
}
function toggleWishlist(maSp, tenSp, giaBan, hinhAnh, event) {
    if (event) {
        event.preventDefault();
        event.stopPropagation();
    }
    let list = getWishlist();
    const idx = list.findIndex(item => item.maSp === maSp);
    if (idx >= 0) {
        list.splice(idx, 1);
        saveWishlist(list);
        showToast('info', 'Đã bỏ "' + tenSp + '" khỏi món yêu thích');
    } else {
        list.push({
            maSp: maSp,
            tenSp: tenSp,
            giaBan: giaBan || 0,
            hinhAnh: hinhAnh || ''
        });
        saveWishlist(list);
        showToast('success', 'Đã thêm "' + tenSp + '" vào yêu thích ❤️');
    }
}

function updateWishlistUI() {
    const list = getWishlist();
    const badge = document.getElementById('portalWishlistBadge');
    const headerHeart = document.getElementById('headerWishlistHeartIcon');
    if (badge) {
        if (list.length > 0) {
            badge.innerText = list.length;
            badge.style.setProperty('display', 'flex', 'important');
        } else {
            badge.style.setProperty('display', 'none', 'important');
        }
    }
    if (headerHeart) {
        if (list.length > 0) {
            headerHeart.className = 'bi bi-heart-fill fs-5 text-danger';
        } else {
            headerHeart.className = 'bi bi-heart fs-5';
        }
    }
    // Update heart buttons on card items
    document.querySelectorAll('.btn-wishlist-toggle').forEach(btn => {
        const maSp = btn.dataset.masp;
        const exists = list.some(item => item.maSp === maSp);
        const icon = btn.querySelector('i');
        if (icon) {
            if (exists) {
                icon.className = 'bi bi-heart-fill text-danger';
                btn.title = 'Bỏ khỏi yêu thích';
            } else {
                icon.className = 'bi bi-heart text-muted';
                btn.title = 'Thêm vào yêu thích';
            }
        }
    });
}

function openWishlistModal() {
    const list = getWishlist();
    const container = document.getElementById('portalWishlistContent');
    if (!container) return;

    if (list.length === 0) {
        container.innerHTML = '<div class="text-center py-5 text-muted">' +
            '  <i class="bi bi-heartbreak fs-1 text-secondary opacity-50"></i>' +
            '  <p class="small mt-2 fw-semibold">Bạn chưa lưu món đồ uống yêu thích nào.<br>Hãy bấm vào biểu tượng trái tim ở các món uống để lưu lại nhé!</p>' +
            '</div>';
    } else {
        let html = '<div class="d-flex flex-column gap-2">';
        list.forEach((item, idx) => {
            const priceFormatted = new Intl.NumberFormat('vi-VN').format(item.giaBan) + ' đ';
            const imgUrl = item.hinhAnh ? item.hinhAnh : 'https://placehold.co/60x60?text=Tea';
            html += '<div class="d-flex align-items-center justify-content-between p-2.5 bg-white rounded-3 shadow-sm border">';
            html += '  <div class="d-flex align-items-center gap-2.5">';
            html += '    <img src="' + imgUrl + '" class="rounded border" style="width: 48px; height: 48px; object-fit: cover;" alt="">';
            html += '    <div>';
            html += '      <strong class="d-block small text-dark">' + item.tenSp + '</strong>';
            html += '      <span class="text-success fw-bold small">' + priceFormatted + '</span>';
            html += '    </div>';
            html += '  </div>';
            html += '  <div class="d-flex align-items-center gap-1.5">';
            html += '    <a href="' + getContextPath() + '/product/detail?id=' + encodeURIComponent(item.maSp) + '" class="btn btn-sm btn-outline-success fw-semibold py-1 px-2.5" style="font-size:11px;">Chọn size</a>';
            html += '    <button type="button" class="btn btn-sm btn-outline-danger border-0 py-1 px-2" onclick="toggleWishlist(\'' + item.maSp + '\', \'' + item.tenSp + '\', ' + item.giaBan + ', \'' + item.hinhAnh + '\'); openWishlistModal();" title="Xóa khỏi yêu thích"><i class="bi bi-trash3"></i></button>';
            html += '  </div>';
            html += '</div>';
        });
        html += '</div>';
        container.innerHTML = html;
    }

    const modalEl = document.getElementById('portalWishlistModal');
    if (modalEl && typeof bootstrap !== 'undefined') {
        const modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
        modal.show();
    }
}

function clearWishlist() {
    saveWishlist([]);
    openWishlistModal();
    showToast('info', 'Đã xóa toàn bộ danh sách món yêu thích');
}

// Initialize Wishlist badges on page load
document.addEventListener("DOMContentLoaded", function() {
    updateWishlistUI();
});
