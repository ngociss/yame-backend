-- =============================================================
-- V1__init_schema.sql
-- Khởi tạo toàn bộ schema cho hệ thống Yame Backend
-- Author: Yame Team
-- Date: 2026-03-04
-- =============================================================

-- -------------------------------------------------------------
-- 1. BẢNG resources (quản lý tài nguyên hệ thống)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS resources (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_resource_name UNIQUE (name)
);

-- -------------------------------------------------------------
-- 2. BẢNG permissions (quyền hạn)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS permissions (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    code        VARCHAR(255) NOT NULL UNIQUE,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    is_verified BOOLEAN      NOT NULL DEFAULT FALSE,
    resource_id BIGINT       REFERENCES resources (id) ON DELETE SET NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 3. BẢNG roles (vai trò)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    description VARCHAR(255),
    status      VARCHAR(20) DEFAULT 'INACTIVE',
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 4. BẢNG role_permission (quan hệ nhiều-nhiều role - permission)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS role_permission (
    role_id       BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- -------------------------------------------------------------
-- 5. BẢNG users (người dùng)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id           BIGSERIAL PRIMARY KEY,
    password     VARCHAR(255),
    email        VARCHAR(255),
    full_name    VARCHAR(255),
    image_url    VARCHAR(500),
    phone_number VARCHAR(20),
    gender       VARCHAR(10),
    status       VARCHAR(20),
    birthday     DATE,
    is_verified  BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 6. BẢNG role_user (quan hệ nhiều-nhiều user - role)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS role_user (
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- -------------------------------------------------------------
-- 7. BẢNG tokens (JWT tokens)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tokens (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(255) UNIQUE,
    access_token  TEXT,
    refresh_token TEXT,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 8. BẢNG addresses (địa chỉ người dùng)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS addresses (
    id             BIGSERIAL PRIMARY KEY,
    recipient_name VARCHAR(255),
    phone_number   VARCHAR(20),
    street_address VARCHAR(500),
    ward           VARCHAR(255),
    province       VARCHAR(255),
    is_default     BOOLEAN   NOT NULL DEFAULT FALSE,
    user_id        BIGINT    REFERENCES users (id) ON DELETE CASCADE,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 9. BẢNG categories (danh mục sản phẩm)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    slug        VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    status      VARCHAR(20) DEFAULT 'ACTIVE',
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 10. BẢNG materials (chất liệu)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS materials (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    description VARCHAR(500),
    status      VARCHAR(20) DEFAULT 'ACTIVE',
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 11. BẢNG product_groups (nhóm sản phẩm)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product_groups (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    description VARCHAR(500),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 12. BẢNG colors (màu sắc)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS colors (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    hex_code    VARCHAR(10),
    description VARCHAR(500),
    status      VARCHAR(20) DEFAULT 'ACTIVE',
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 13. BẢNG sizes (kích cỡ)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sizes (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50),
    description VARCHAR(500),
    status      VARCHAR(20) DEFAULT 'ACTIVE',
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 14. BẢNG products (sản phẩm)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255),
    slug            VARCHAR(255) NOT NULL UNIQUE,
    base_price      NUMERIC(19, 2),
    discount_price  NUMERIC(19, 2),
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    product_status  VARCHAR(30),
    category_id     BIGINT REFERENCES categories (id) ON DELETE SET NULL,
    material_id     BIGINT REFERENCES materials (id) ON DELETE SET NULL,
    product_group_id BIGINT REFERENCES product_groups (id) ON DELETE SET NULL,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 15. BẢNG product_images (ảnh sản phẩm)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product_images (
    id            BIGSERIAL PRIMARY KEY,
    image_url     VARCHAR(500),
    alt_text      VARCHAR(255),
    is_primary    BOOLEAN   NOT NULL DEFAULT FALSE,
    display_order INT       NOT NULL DEFAULT 0,
    product_id    BIGINT    REFERENCES products (id) ON DELETE CASCADE,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 16. BẢNG product_variants (biến thể sản phẩm)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product_variants (
    id             BIGSERIAL PRIMARY KEY,
    stock_quantity INT          NOT NULL DEFAULT 0,
    sku_code       VARCHAR(255),
    status         VARCHAR(20) DEFAULT 'ACTIVE',
    color_id       BIGINT REFERENCES colors (id) ON DELETE SET NULL,
    size_id        BIGINT REFERENCES sizes (id) ON DELETE SET NULL,
    product_id     BIGINT REFERENCES products (id) ON DELETE CASCADE,
    created_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 17. BẢNG carts (giỏ hàng)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS carts (
    id          BIGSERIAL PRIMARY KEY,
    is_verified BOOLEAN   NOT NULL DEFAULT FALSE,
    user_id     BIGINT    UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 18. BẢNG cart_items (chi tiết giỏ hàng)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cart_items (
    id                 BIGSERIAL PRIMARY KEY,
    quantity           INT       NOT NULL DEFAULT 1,
    cart_id            BIGINT    REFERENCES carts (id) ON DELETE CASCADE,
    product_variant_id BIGINT    NOT NULL REFERENCES product_variants (id) ON DELETE CASCADE,
    created_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 19. BẢNG discounts (mã giảm giá)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS discounts (
    id                  BIGSERIAL PRIMARY KEY,
    code                VARCHAR(100),
    description         VARCHAR(500),
    type                VARCHAR(20),
    discount_status     VARCHAR(20),
    status              VARCHAR(20) DEFAULT 'ACTIVE',
    discount_value      NUMERIC(10, 2),
    min_order_amount    DOUBLE PRECISION,
    max_discount_amount DOUBLE PRECISION,
    usage_limit         INT,
    start_at            TIMESTAMP,
    end_at              TIMESTAMP,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 20. BẢNG shipping_methods (phương thức vận chuyển)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shipping_methods (
    id             BIGSERIAL PRIMARY KEY,
    corp_name      VARCHAR(255),
    description    VARCHAR(500),
    cost           NUMERIC(19, 2),
    estimated_days INT       NOT NULL DEFAULT 0,
    status         VARCHAR(20) DEFAULT 'ACTIVE',
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 21. BẢNG order_status (trạng thái đơn hàng)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_status (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100),
    description VARCHAR(255),
    code        VARCHAR(50),
    is_final    BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 22. BẢNG orders (đơn hàng)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id                 BIGSERIAL PRIMARY KEY,
    discount_amount    INT       NOT NULL DEFAULT 0,
    total_cost         NUMERIC(19, 2),
    is_free_ship       BOOLEAN   NOT NULL DEFAULT FALSE,
    shipped_at         TIMESTAMP,
    delivered_at       TIMESTAMP,
    shipping_cost      NUMERIC(19, 2),
    "orderStatus_id"   BIGINT REFERENCES order_status (id) ON DELETE SET NULL,
    shipping_method_id BIGINT REFERENCES shipping_methods (id) ON DELETE SET NULL,
    discount_id        BIGINT REFERENCES discounts (id) ON DELETE SET NULL,
    user_id            BIGINT REFERENCES users (id) ON DELETE SET NULL,
    created_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 23. BẢNG order_items (chi tiết đơn hàng)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_items (
    id                 BIGSERIAL PRIMARY KEY,
    quantity           INT              NOT NULL DEFAULT 1,
    price              DOUBLE PRECISION NOT NULL DEFAULT 0,
    subtotal           DOUBLE PRECISION NOT NULL DEFAULT 0,
    is_verified        BOOLEAN          NOT NULL DEFAULT FALSE,
    order_id           BIGINT REFERENCES orders (id) ON DELETE CASCADE,
    product_variant_id BIGINT REFERENCES product_variants (id) ON DELETE SET NULL,
    created_at         TIMESTAMP        NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP        NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 24. BẢNG payments (thanh toán)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS payments (
    id             BIGSERIAL PRIMARY KEY,
    method         VARCHAR(30),
    status         VARCHAR(30),
    amount         DOUBLE PRECISION,
    transaction_id VARCHAR(255),
    paid_at        TIMESTAMP,
    order_id       BIGINT UNIQUE REFERENCES orders (id) ON DELETE CASCADE,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 25. BẢNG reviews (đánh giá sản phẩm)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reviews (
    id         BIGSERIAL PRIMARY KEY,
    rating     INT       NOT NULL DEFAULT 5,
    comment    TEXT,
    title      VARCHAR(255),
    status     VARCHAR(20),
    user_id    BIGINT    REFERENCES users (id) ON DELETE SET NULL,
    product_id BIGINT    REFERENCES products (id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 26. BẢNG review_images (ảnh đánh giá)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS review_images (
    id         BIGSERIAL PRIMARY KEY,
    image_url  VARCHAR(500),
    review_id  BIGINT    REFERENCES reviews (id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 27. BẢNG suppliers (nhà cung cấp)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS suppliers (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    address       VARCHAR(500),
    status        VARCHAR(20) DEFAULT 'ACTIVE',
    created_at    TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 28. BẢNG purchase_orders (đơn nhập hàng)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS purchase_orders (
    id           BIGSERIAL PRIMARY KEY,
    order_date   DATE,
    status       VARCHAR(30),
    total_amount NUMERIC(19, 2),
    supplier_id  BIGINT    REFERENCES suppliers (id) ON DELETE SET NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- -------------------------------------------------------------
-- 29. BẢNG purchase_order_items (chi tiết đơn nhập hàng)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS purchase_order_items (
    id                 BIGSERIAL PRIMARY KEY,
    quantity           INT            NOT NULL DEFAULT 0,
    unit_price         NUMERIC(19, 2),
    total_price        NUMERIC(19, 2),
    product_variant_id BIGINT REFERENCES product_variants (id) ON DELETE SET NULL,
    purchase_order_id  BIGINT REFERENCES purchase_orders (id) ON DELETE CASCADE,
    created_at         TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- =============================================================
-- END OF V1 SCHEMA
-- =============================================================

