# E-Commerce Backend Roadmap (Dựa trên toàn bộ Entity hiện có)

Cập nhật: 2025-09-29  
Mục tiêu: Hoàn thiện hệ thống thương mại điện tử sử dụng đầy đủ các entity đã khai báo trong project hiện tại.

---
## 1. Nguyên tắc & Giả định
- DB schema tương ứng với các entity hiện có (User, Product, ProductVariant, Order, v.v.).
- Sẽ bổ sung Flyway để version hóa thay đổi (nếu chưa tồn tại migration).
- JWT Auth đã chạy cơ bản → chỉ mở rộng dần (refresh, revoke, phân quyền). 
- Stock sẽ trừ khi tạo Order (đơn giản hóa) và được hoàn lại khi hủy đơn ở trạng thái hợp lệ.
- Không triển khai tích hợp thanh toán thật trong giai đoạn đầu (dùng giả lập / stub).

---
## 2. Phân nhóm Entity theo Domain
**User & Auth:** User, Role, Permission, Resource, Token  
**Catalog:** Product, ProductVariant, ProductImage, Category, Size, Color, Material, Supplier  
**Pricing & Promotion:** Discount  
**Cart & Checkout:** Cart, CartItem  
**Order & Fulfillment:** Order, OrderItem, OrderStatus, ShippingMethod  
**Payment:** Payment, PaymentMethod, PaymentStatus  
**Procurement:** PurchaseOrder, PurchaseOrderItem, Supplier  
**Engagement:** Review, ReviewImage  
**Address & Support:** Address  

---
## 3. Luồng nghiệp vụ tổng quát
1. Admin thiết lập Catalog (Category → Product → Variants → Images).  
2. Người dùng duyệt sản phẩm, thêm vào giỏ.  
3. Áp mã giảm giá (Discount).  
4. Tạo đơn hàng (Order) từ giỏ + địa chỉ + phương thức giao hàng.  
5. Thanh toán (stub) → cập nhật Order & Payment.  
6. Giao hàng / cập nhật trạng thái.  
7. Người dùng đánh giá sản phẩm (Review).  
8. Nội bộ nhập hàng (PurchaseOrder) để bổ sung tồn kho.  
9. Báo cáo & phân quyền nâng cao.  

---
## 4. Các Phase Triển Khai
| Phase | Tên | Mục tiêu chính |
|-------|-----|-----------------|
| 0 | Nền tảng & Chuẩn hóa | BaseEntity, auditing, Flyway, error handling |
| 1 | Catalog Core | Category & Product CRUD + tìm kiếm cơ bản |
| 2 | Variant & Media | ProductVariant, Size/Color, ProductImage |
| 3 | Cart & Discount | Giỏ hàng + áp mã giảm giá |
| 4 | Order & Payment Stub | Tạo order, thanh toán giả lập, shipping method cơ bản, Address |
| 5 | Order Lifecycle | Mở rộng trạng thái, hủy đơn, phục hồi stock |
| 6 | Review & Rating | Đánh giá sản phẩm + cập nhật avgRating |
| 7 | Procurement | PurchaseOrder nhập kho, tăng stock |
| 8 | RBAC chi tiết | Permission/Resource, method-level security |
| 9 | Reporting & Optimization | Báo cáo bán hàng + cache nhẹ |
| 10 | Hardening & Observability | Actuator, logging, rate limit |

---
## 5. Chi tiết Từng Phase
### Phase 0 – Nền tảng (1–2 ngày)
**Tasks:**
- BaseEntity (id, createdAt, updatedAt) + @MappedSuperclass
- @EnableJpaAuditing + AuditorAware (nếu cần)
- Thêm Flyway + V1__baseline.sql (snapshot schema hiện tại)
- GlobalExceptionHandler chuẩn JSON: {timestamp, path, errorCode, message, details[]}
- ErrorCode enum (VALIDATION_ERROR, PRODUCT_NOT_FOUND, ORDER_NOT_FOUND, ...)
- Validation annotation DTO: @NotBlank, @Email, @Positive
- Đổi Forgot/Reset password body → ForgotPasswordRequest, ResetPasswordConfirmRequest
- Ẩn password / token trong log (@JsonIgnore)
**DoD:** Build & migrate OK, Auth API chạy bình thường.

### Phase 1 – Catalog Core (2–4 ngày)
**Tasks:**
- Category CRUD (parentId optional)
- Product CRUD (status: DRAFT / ACTIVE / INACTIVE)
- Product search: categoryId, name (contains), status, supplierId
- PageResponse DTO (page, size, totalElements, totalPages, items)
- ProductMapper & CategoryMapper
- Index DB: product(name), product(category_id)
- Unit test: ProductService create/update/not found
**DoD:** GET /products?categoryId=.. có pagination & filter đúng.

### Phase 2 – Variant & Media (2–4 ngày)
**Tasks:**
- CRUD Size, Color
- Add ProductVariant (sku, priceOverride, stock, sizeId, colorId)
- Unique sku constraint (Flyway V3)
- ProductImage add/list (url, orderIndex)
- Product detail trả variants + images
- Stock validation >= 0
- Test: duplicate SKU → fail
**DoD:** Product detail hiển thị đầy đủ variants, images.

### Phase 3 – Cart & Discount (3–4 ngày)
**Tasks:**
- CartService: add/update/remove/clear
- Check stock (variant.stock >= quantity)
- Discount CRUD: code, type (PERCENT/FIXED), value, startAt, endAt, active
- Apply discount: attach discountId + compute discountAmount vào cart
- Cart totals: subtotal, discountAmount, finalTotal
- Test: apply expired discount, add vượt stock
**DoD:** GET /cart trả totals chính xác.

### Phase 4 – Order & Payment Stub (4–6 ngày)
**Tasks:**
- Address CRUD (multi-address + default)
- ShippingMethod CRUD (flat fee)
- Create Order từ cart (snapshot: price, product/variant info)
- Copy discount từ cart sang order
- Stock deduction tại thời điểm tạo order
- Payment record (PENDING) → pay() => PAID
- Order status: CREATED → PAID → COMPLETED (tối giản)
- Integration test: cart → order → pay → complete
**DoD:** Order & Payment flow hoạt động, stock giảm đúng.

### Phase 5 – Order Lifecycle Mở Rộng (3–5 ngày)
**Tasks:**
- OrderStatus: CREATED, PAID, FULFILLING, COMPLETED, CANCELED
- Transition endpoint (admin)
- Cancel BEFORE PAID: restore stock
- (Optional) Cancel AFTER PAID: chính sách riêng
- (Optional) OrderStatusHistory table
- Test invalid transition
**DoD:** Không chuyển trực tiếp CREATED → COMPLETED.

### Phase 6 – Review & Rating (2–3 ngày)
**Tasks:**
- Review create: user phải có order PAID/COMPLETED chứa product
- ReviewImage (optional phase này)
- Update product.avgRating (sử dụng ratingSum & ratingCount để tối ưu)
- GET product reviews (pagination)
- Test unauthorized review
**DoD:** Product detail hiển thị avgRating & totalReviews.

### Phase 7 – Procurement (4–6 ngày)
**Tasks:**
- PurchaseOrder CRUD (status: CREATED → RECEIVED → CLOSED)
- PurchaseOrderItem: variant + expectedQty + receivedQty
- Receive: tăng variant.stock theo receivedQty
- Test: stock increase
**DoD:** Stock tăng sau receive.

### Phase 8 – RBAC chi tiết (3–5 ngày)
**Tasks:**
- Permission & Resource seed (Flyway Vx__seed_permissions.sql)
- Role ↔ Permission mapping
- @PreAuthorize cho admin endpoints (product create, order status change, purchase order)
- Cache permission (in-memory TTL)
- Test: user thường bị chặn
**DoD:** 403 khi thiếu quyền.

### Phase 9 – Reporting & Optimization (4–7 ngày)
**Tasks:**
- /reports/daily-sales (group by order date, status COMPLETED)
- /reports/top-products (SUM quantity)
- Cache product detail & category tree (@Cacheable)
- Invalidation khi update
- Test: report accuracy với seed data
**DoD:** Report trả dữ liệu đúng, cache giảm query.

### Phase 10 – Hardening & Observability (3–5 ngày)
**Tasks:**
- Spring Actuator (health, metrics)
- Correlation ID filter (MDC)
- Mask sensitive fields trong log
- Rate limit login (in-memory) cơ bản
- Smoke performance test (browse → cart → order)
**DoD:** /actuator/health UP, log có traceId, rate limit hoạt động.

---
## 6. Backlog (Txx – Task – Phase – Priority)
T01 BaseEntity & auditing (0) HIGH  
T02 Flyway baseline (0) HIGH  
T03 Error handling unify (0) HIGH  
T04 Category CRUD (1) HIGH  
T05 Product CRUD (1) HIGH  
T06 Product search + pagination (1) HIGH  
T07 Variant management (2) HIGH  
T08 Product image attach (2) MED  
T09 Cart operations (3) HIGH  
T10 Discount apply (3) MED  
T11 Order create (4) HIGH  
T12 Payment stub (4) HIGH  
T13 Stock deduction (4) HIGH  
T14 ShippingMethod basic (4) MED  
T15 Order status transitions (5) HIGH  
T16 Cancel restore stock (5) MED  
T17 Review create + avg rating (6) HIGH  
T18 Purchase order receive (7) MED  
T19 RBAC permissions (8) MED  
T20 Reporting sales (9) MED  
T21 Cache product/category (9) LOW  
T22 Actuator observability (10) MED  
T23 Rate limit login (10) LOW  

---
## 7. Mapping Entity → Phase
| Entity | Phase | Ghi chú |
|--------|-------|---------|
| User, Role, Token | 0 | Auth nền tảng |
| Permission, Resource | 8 | RBAC chi tiết |
| Category | 1 | Catalog core |
| Product | 1 | Catalog core |
| Supplier | 1 / 7 | Gắn sản phẩm + procurement |
| Size, Color, Material | 2 | Variant & thuộc tính |
| ProductVariant | 2 | SKU, stock, priceOverride |
| ProductImage | 2 | Media |
| Cart, CartItem | 3 | Giỏ hàng |
| Discount | 3 | Mã giảm giá |
| Address | 4 | Giao hàng |
| ShippingMethod | 4/5 | Phí giao hàng |
| Order, OrderItem, OrderStatus | 4/5 | Đơn hàng & vòng đời |
| Payment, PaymentMethod, PaymentStatus | 4 | Thanh toán giả lập |
| Review, ReviewImage | 6 | Đánh giá sản phẩm |
| PurchaseOrder, PurchaseOrderItem | 7 | Nhập kho |

---
## 8. Test Strategy Theo Phase
- Phase 1: ProductServiceTest (create/update), CategoryServiceTest.
- Phase 3: CartServiceTest (add/remove, discount expired).
- Phase 4: EndToEndTest (product → cart → order → pay).
- Phase 6: ReviewAuthorizationTest.
- Phase 7: PurchaseOrderReceiveTest.
- Phase 9: ReportAccuracyTest.

---
## 9. Rủi ro & Giảm thiểu
| Rủi ro | Mô tả | Giảm thiểu |
|--------|-------|------------|
| Stock race | Cạnh tranh cập nhật stock | Optimistic lock / synchronized đoạn critical sau |
| Migration lệch | Quên cập nhật Flyway | Checklist review trước merge |
| Permission phình | Quá nhiều string code | Dùng enum PermissionCode |
| N+1 Query | Tải chậm product detail | DTO + fetch join / batch size |
| Cache stale | Dữ liệu lỗi thời | TTL ngắn + explicit evict |

---
## 10. Definition of Done (MVP mở rộng tới Phase 6)
- Phase 0–6 hoàn thành.
- Order flow hoàn tất tới COMPLETED.
- Stock giảm & phục hồi đúng khi hủy trước PAID.
- Review chỉ cho user đủ điều kiện.
- >= 10 test pass ổn định.
- Không còn stacktrace chưa xử lý ở flow chính.

---
## 11. Kế hoạch Sprint Gợi Ý
| Sprint | Nội dung |
|--------|----------|
| 1 | Phase 0 + 1 |
| 2 | Phase 2 + 3 |
| 3 | Phase 4 |
| 4 | Phase 5 + 6 |
| 5 | Phase 7 + 8 |
| 6 | Phase 9 + 10 |

---
## 12. Convention Commit
- feat(catalog): add product CRUD
- feat(variant): implement variant add & sku uniqueness
- feat(cart): cart service add/update/remove
- feat(order): order create + stock deduction
- feat(payment): stub payment processing
- feat(review): add review & rating update
- refactor(core): introduce BaseEntity
- chore(db): add migration V3 unique sku
- test(order): add order create flow test
- docs(roadmap): initial roadmap file

---
## 13. JSON Tracking Mẫu
```json
{
  "phase": 4,
  "completed": ["T01","T02","T03","T04","T05","T06","T07","T08","T09","T10"],
  "next": ["T11","T12","T13"]
}
```

---
## 14. Next Steps Đề Xuất
1. Xác nhận Phase 0 bắt đầu.  
2. Export schema hiện tại → viết V1__baseline.sql.  
3. Tạo BaseEntity + áp dụng vào entity mẫu (Product) rồi lan rộng.  
4. Viết ErrorCode enum + sửa GlobalExceptionHandler.

---
**Liên hệ / Ghi chú:** File này dùng làm định hướng, cập nhật khi có thay đổi yêu cầu.

