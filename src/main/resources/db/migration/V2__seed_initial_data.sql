-- =============================================================
-- V2__seed_initial_data.sql
-- Dữ liệu mẫu khởi tạo cho hệ thống Yame Backend
-- Author: Yame Team
-- Date: 2026-03-04
-- =============================================================

-- -------------------------------------------------------------
-- 1. Resources (tài nguyên API)
-- -------------------------------------------------------------
INSERT INTO resources (name, description, status, created_at, updated_at) VALUES
('USER',            'Quản lý người dùng',            'ACTIVE', NOW(), NOW()),
('ROLE',            'Quản lý vai trò',                'ACTIVE', NOW(), NOW()),
('PERMISSION',      'Quản lý quyền hạn',              'ACTIVE', NOW(), NOW()),
('PRODUCT',         'Quản lý sản phẩm',               'ACTIVE', NOW(), NOW()),
('CATEGORY',        'Quản lý danh mục',               'ACTIVE', NOW(), NOW()),
('ORDER',           'Quản lý đơn hàng',               'ACTIVE', NOW(), NOW()),
('CART',            'Quản lý giỏ hàng',               'ACTIVE', NOW(), NOW()),
('DISCOUNT',        'Quản lý mã giảm giá',            'ACTIVE', NOW(), NOW()),
('SHIPPING_METHOD', 'Quản lý phương thức vận chuyển', 'ACTIVE', NOW(), NOW()),
('REVIEW',          'Quản lý đánh giá',               'ACTIVE', NOW(), NOW()),
('SUPPLIER',        'Quản lý nhà cung cấp',           'ACTIVE', NOW(), NOW()),
('ADDRESS',         'Quản lý địa chỉ',                'ACTIVE', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- -------------------------------------------------------------
-- 2. Roles
-- -------------------------------------------------------------
INSERT INTO roles (name, description, status, created_at, updated_at) VALUES
('ADMIN',    'Quản trị viên hệ thống',  'ACTIVE', NOW(), NOW()),
('CUSTOMER', 'Khách hàng thông thường', 'ACTIVE', NOW(), NOW()),
('STAFF',    'Nhân viên bán hàng',      'ACTIVE', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- -------------------------------------------------------------
-- 3. Order Statuses
-- -------------------------------------------------------------
INSERT INTO order_status (name, description, code, is_final, created_at, updated_at) VALUES
('Chờ xác nhận',   'Đơn hàng mới tạo, chờ xác nhận',    'PENDING',    FALSE, NOW(), NOW()),
('Đã xác nhận',    'Đơn hàng đã được xác nhận',          'CONFIRMED',  FALSE, NOW(), NOW()),
('Đang xử lý',     'Đơn hàng đang được chuẩn bị',        'PROCESSING', FALSE, NOW(), NOW()),
('Đang giao hàng', 'Đơn hàng đang trên đường giao',      'SHIPPING',   FALSE, NOW(), NOW()),
('Đã giao hàng',   'Đơn hàng đã giao thành công',        'DELIVERED',  TRUE,  NOW(), NOW()),
('Đã hủy',         'Đơn hàng đã bị hủy',                 'CANCELLED',  TRUE,  NOW(), NOW()),
('Hoàn trả',       'Đơn hàng đang được hoàn trả',        'REFUNDED',   TRUE,  NOW(), NOW())
ON CONFLICT DO NOTHING;

-- -------------------------------------------------------------
-- 4. Shipping Methods
-- -------------------------------------------------------------
INSERT INTO shipping_methods (corp_name, description, cost, estimated_days, status, created_at, updated_at) VALUES
('GHN - Giao Hàng Nhanh',         'Giao hàng nhanh 1-2 ngày nội thành',  30000.00, 2, 'ACTIVE', NOW(), NOW()),
('GHTK - Giao Hàng Tiết Kiệm',    'Giao hàng tiết kiệm 3-5 ngày',        20000.00, 5, 'ACTIVE', NOW(), NOW()),
('Viettel Post',                   'Dịch vụ bưu chính Viettel',           25000.00, 3, 'ACTIVE', NOW(), NOW()),
('J&T Express',                   'Giao hàng nhanh J&T 1-3 ngày',        28000.00, 3, 'ACTIVE', NOW(), NOW()),
('Vietnam Post',                   'Bưu điện Việt Nam, giao toàn quốc',   15000.00, 7, 'ACTIVE', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- -------------------------------------------------------------
-- 5. Categories
-- -------------------------------------------------------------
INSERT INTO categories (name, slug, description, status, created_at, updated_at) VALUES
('Áo',       'ao',        'Các loại áo thời trang',       'ACTIVE', NOW(), NOW()),
('Quần',     'quan',      'Các loại quần thời trang',      'ACTIVE', NOW(), NOW()),
('Váy/Đầm',  'vay-dam',   'Váy và đầm nữ',                'ACTIVE', NOW(), NOW()),
('Phụ kiện', 'phu-kien',  'Phụ kiện thời trang',          'ACTIVE', NOW(), NOW()),
('Giày dép', 'giay-dep',  'Giày dép các loại',            'ACTIVE', NOW(), NOW()),
('Túi xách', 'tui-xach',  'Túi xách và ba lô thời trang', 'ACTIVE', NOW(), NOW())
ON CONFLICT (slug) DO NOTHING;

-- -------------------------------------------------------------
-- 6. Materials
-- -------------------------------------------------------------
INSERT INTO materials (name, description, status, created_at, updated_at) VALUES
('Cotton 100%',          'Chất liệu cotton tự nhiên thoáng mát',    'ACTIVE', NOW(), NOW()),
('Polyester',            'Chất liệu polyester bền đẹp',             'ACTIVE', NOW(), NOW()),
('Linen',                'Chất liệu linen mát mẻ phù hợp mùa hè',  'ACTIVE', NOW(), NOW()),
('Denim',                'Chất liệu denim chắc chắn, bền bỉ',       'ACTIVE', NOW(), NOW()),
('Silk',                 'Chất liệu lụa cao cấp',                   'ACTIVE', NOW(), NOW()),
('Cotton-Polyester Mix', 'Hỗn hợp cotton và polyester 65/35',       'ACTIVE', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- -------------------------------------------------------------
-- 7. Colors
-- -------------------------------------------------------------
INSERT INTO colors (name, hex_code, description, status, created_at, updated_at) VALUES
('Đen',        '#000000', 'Màu đen cơ bản',        'ACTIVE', NOW(), NOW()),
('Trắng',      '#FFFFFF', 'Màu trắng tinh khiết',  'ACTIVE', NOW(), NOW()),
('Đỏ',         '#FF0000', 'Màu đỏ tươi',            'ACTIVE', NOW(), NOW()),
('Xanh dương', '#0000FF', 'Màu xanh dương',         'ACTIVE', NOW(), NOW()),
('Xanh lá',    '#008000', 'Màu xanh lá cây',        'ACTIVE', NOW(), NOW()),
('Vàng',       '#FFD700', 'Màu vàng ánh kim',       'ACTIVE', NOW(), NOW()),
('Hồng',       '#FFC0CB', 'Màu hồng nhạt',          'ACTIVE', NOW(), NOW()),
('Xám',        '#808080', 'Màu xám trung tính',     'ACTIVE', NOW(), NOW()),
('Nâu',        '#8B4513', 'Màu nâu đất',            'ACTIVE', NOW(), NOW()),
('Navy',       '#000080', 'Màu xanh navy',          'ACTIVE', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- -------------------------------------------------------------
-- 8. Sizes
-- -------------------------------------------------------------
INSERT INTO sizes (name, description, status, created_at, updated_at) VALUES
('XS',  'Extra Small - Cực nhỏ',    'ACTIVE', NOW(), NOW()),
('S',   'Small - Nhỏ',               'ACTIVE', NOW(), NOW()),
('M',   'Medium - Vừa',              'ACTIVE', NOW(), NOW()),
('L',   'Large - Lớn',               'ACTIVE', NOW(), NOW()),
('XL',  'Extra Large - Cực lớn',     'ACTIVE', NOW(), NOW()),
('XXL', 'Double Extra Large - 2XL',  'ACTIVE', NOW(), NOW()),
('3XL', 'Triple Extra Large - 3XL',  'ACTIVE', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================
-- END OF V2 SEED DATA
-- =============================================================

