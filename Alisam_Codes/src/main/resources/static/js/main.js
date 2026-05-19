/* MM Printers - Main JavaScript */

document.addEventListener('DOMContentLoaded', function () {

    // ===== Cart Count (from localStorage) =====
    function updateCartBadge() {
        const count = parseInt(localStorage.getItem('mmCartCount') || '0');
        const badge = document.getElementById('cartBadge');
        if (badge) {
            badge.textContent = count;
            badge.style.display = count > 0 ? 'flex' : 'none';
        }
    }
    updateCartBadge();

    // ===== Add to Cart Button =====
    document.querySelectorAll('.product-add-btn').forEach(btn => {
        btn.addEventListener('click', function (e) {
            // Show visual feedback
            const originalText = this.innerHTML;
            this.innerHTML = '<i class="fas fa-check"></i> Added!';
            this.style.background = 'linear-gradient(135deg, #10B981, #059669)';

            // Update cart count
            const current = parseInt(localStorage.getItem('mmCartCount') || '0');
            localStorage.setItem('mmCartCount', current + 1);
            updateCartBadge();

            setTimeout(() => {
                this.innerHTML = originalText;
                this.style.background = '';
            }, 1500);
        });
    });

    // ===== Category Nav active link =====
    const catNavLinks = document.querySelectorAll('.cat-nav-item');
    catNavLinks.forEach(link => {
        if (link.href === window.location.href) {
            link.classList.add('active');
        }
        link.addEventListener('click', function () {
            catNavLinks.forEach(l => l.classList.remove('active'));
            this.classList.add('active');
        });
    });

    // ===== Search redirect =====
    const searchBtn = document.getElementById('searchBtn');
    const searchInput = document.getElementById('searchInput');
    if (searchBtn && searchInput) {
        searchBtn.addEventListener('click', function () {
            const q = searchInput.value.trim();
            if (q) window.location.href = '/products?search=' + encodeURIComponent(q);
        });
        searchInput.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') searchBtn.click();
        });
    }

    // ===== Scroll-triggered animation =====
    const animElements = document.querySelectorAll('.product-card, .cat-card, .testimonial-card, .process-step, .promo-chip');
    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry, i) => {
            if (entry.isIntersecting) {
                setTimeout(() => {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                }, i * 80);
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.1 });

    animElements.forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(20px)';
        el.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
        observer.observe(el);
    });

    // ===== Sticky Header shadow on scroll =====
    const header = document.querySelector('.header');
    window.addEventListener('scroll', function () {
        if (header) {
            if (window.scrollY > 10) {
                header.style.boxShadow = '0 4px 20px rgba(0,0,0,0.12)';
            } else {
                header.style.boxShadow = '0 2px 20px rgba(0,0,0,0.08)';
            }
        }
    });

    // ===== Wishlist button toggle =====
    document.querySelectorAll('.quick-action').forEach(btn => {
        btn.addEventListener('click', function () {
            const icon = this.querySelector('i');
            if (icon && icon.classList.contains('fa-heart')) {
                icon.classList.toggle('far');
                icon.classList.toggle('fas');
                this.style.color = icon.classList.contains('fas') ? '#FF0066' : '';
            }
        });
    });

    // ===== Qty control for product detail page =====
    const qtyInput = document.querySelector('.qty-input');
    const qtyMinus = document.querySelector('.qty-minus');
    const qtyPlus = document.querySelector('.qty-plus');

    if (qtyInput && qtyMinus && qtyPlus) {
        qtyMinus.addEventListener('click', function () {
            let val = parseInt(qtyInput.value || 1);
            if (val > 1) qtyInput.value = val - 1;
        });
        qtyPlus.addEventListener('click', function () {
            let val = parseInt(qtyInput.value || 1);
            qtyInput.value = val + 1;
        });
    }

    // ===== Smooth scroll for anchor links =====
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                e.preventDefault();
                target.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        });
    });

    // ===== Product Thumbnail Gallery =====
    const thumbs = document.querySelectorAll('.thumb');
    const mainImg = document.querySelector('.main-product-img img');
    if (thumbs.length && mainImg) {
        thumbs.forEach(thumb => {
            thumb.addEventListener('click', function () {
                thumbs.forEach(t => t.classList.remove('active'));
                this.classList.add('active');
                mainImg.src = this.querySelector('img').src;
            });
        });
    }

});
