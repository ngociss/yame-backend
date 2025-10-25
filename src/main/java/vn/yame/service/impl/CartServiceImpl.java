package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.request.AddToCartRequest;
import vn.yame.dto.request.UpdateCartItemRequest;
import vn.yame.dto.reponse.CartItemResponse;
import vn.yame.dto.reponse.CartResponse;
import vn.yame.exception.InvalidDataException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.model.*;
import vn.yame.repository.*;
import vn.yame.service.CartService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    @Override
    public CartResponse getOrCreateCart(Long userId) {
        log.info("Getting or creating cart for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NotFoundResourcesException(
                                    ErrorCode.USER_NOT_FOUND,
                                    "User not found with id: " + userId
                            ));

                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setVerified(false);
                    return cartRepository.save(newCart);
                });

        return buildCartResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        log.info("Getting cart for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundResourcesException(
                        ErrorCode.INVALID_REQUEST,
                        "Cart not found for user: " + userId
                ));

        return buildCartResponse(cart);
    }

    @Override
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        log.info("Adding product variant {} to cart for user {}", request.getProductVariantId(), userId);

        // Get or create cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NotFoundResourcesException(
                                    ErrorCode.USER_NOT_FOUND,
                                    "User not found with id: " + userId
                            ));
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setVerified(false);
                    return cartRepository.save(newCart);
                });

        // Validate product variant exists
        ProductVariant productVariant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new NotFoundResourcesException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "Product variant not found with id: " + request.getProductVariantId()
                ));

        // Check stock availability
        if (productVariant.getStockQuantity() < request.getQuantity()) {
            throw new InvalidDataException(
                    ErrorCode.INVALID_REQUEST,
                    "Not enough stock. Available: " + productVariant.getStockQuantity() +
                    ", requested: " + request.getQuantity()
            );
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductVariantId(
                cart.getId(), request.getProductVariantId()
        );

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();

            // Check stock for new quantity
            if (productVariant.getStockQuantity() < newQuantity) {
                throw new InvalidDataException(
                        ErrorCode.INVALID_REQUEST,
                        "Not enough stock. Available: " + productVariant.getStockQuantity() +
                        ", total requested: " + newQuantity
                );
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
            log.info("Updated cart item quantity to {}", newQuantity);
        } else {
            // Create new cart item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductVariant(productVariant);
            newItem.setQuantity(request.getQuantity());
            cartItemRepository.save(newItem);
            log.info("Added new item to cart");
        }

        return buildCartResponse(cart);
    }

    @Override
    public CartResponse updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        log.info("Updating cart item {} for user {}", cartItemId, userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundResourcesException(
                        ErrorCode.INVALID_REQUEST,
                        "Cart not found for user: " + userId
                ));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundResourcesException(
                        ErrorCode.INVALID_REQUEST,
                        "Cart item not found with id: " + cartItemId
                ));

        // Validate cart item belongs to user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new InvalidDataException(
                    ErrorCode.INVALID_REQUEST,
                    "Cart item does not belong to user's cart"
            );
        }

        // Check stock availability
        ProductVariant productVariant = cartItem.getProductVariant();
        if (productVariant.getStockQuantity() < request.getQuantity()) {
            throw new InvalidDataException(
                    ErrorCode.INVALID_REQUEST,
                    "Not enough stock. Available: " + productVariant.getStockQuantity() +
                    ", requested: " + request.getQuantity()
            );
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        log.info("Updated cart item quantity to {}", request.getQuantity());
        return buildCartResponse(cart);
    }

    @Override
    public CartResponse removeCartItem(Long userId, Long cartItemId) {
        log.info("Removing cart item {} for user {}", cartItemId, userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundResourcesException(
                        ErrorCode.INVALID_REQUEST,
                        "Cart not found for user: " + userId
                ));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundResourcesException(
                        ErrorCode.INVALID_REQUEST,
                        "Cart item not found with id: " + cartItemId
                ));

        // Validate cart item belongs to user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new InvalidDataException(
                    ErrorCode.INVALID_REQUEST,
                    "Cart item does not belong to user's cart"
            );
        }

        cartItemRepository.delete(cartItem);
        log.info("Removed cart item successfully");

        return buildCartResponse(cart);
    }

    @Override
    public CartResponse clearCart(Long userId) {
        log.info("Clearing cart for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundResourcesException(
                        ErrorCode.INVALID_REQUEST,
                        "Cart not found for user: " + userId
                ));

        cartItemRepository.deleteByCartId(cart.getId());
        log.info("Cart cleared successfully");

        return buildCartResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(null);

        if (cart == null) {
            return 0;
        }

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    // Helper method to build CartResponse
    private CartResponse buildCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUser().getId());
        response.setUserEmail(cart.getUser().getEmail());
        response.setVerified(cart.isVerified());

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        List<CartItemResponse> itemResponses = new ArrayList<>();

        BigDecimal subtotal = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartItem item : cartItems) {
            CartItemResponse itemResponse = buildCartItemResponse(item);
            itemResponses.add(itemResponse);

            subtotal = subtotal.add(itemResponse.getTotalPrice());
            totalQuantity += item.getQuantity();
        }

        response.setItems(itemResponses);
        response.setTotalItems(itemResponses.size());
        response.setTotalQuantity(totalQuantity);
        response.setSubtotal(subtotal);
        response.setDiscountAmount(BigDecimal.ZERO); // TODO: Implement discount logic
        response.setTotal(subtotal);

        return response;
    }

    // Helper method to build CartItemResponse
    private CartItemResponse buildCartItemResponse(CartItem item) {
        CartItemResponse response = new CartItemResponse();

        ProductVariant variant = item.getProductVariant();
        Product product = variant.getProduct();

        response.setId(item.getId());
        response.setQuantity(item.getQuantity());

        // Variant info
        response.setProductVariantId(variant.getId());
        response.setSkuCode(variant.getSkuCode());
        response.setStockQuantity(variant.getStockQuantity());

        // Product info
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setProductSlug(product.getSlug());
        response.setProductPrice(product.getBasePrice());
        response.setProductDiscountPrice(product.getDiscountPrice());

        // Color & Size info
        if (variant.getColor() != null) {
            response.setColorName(variant.getColor().getName());
            response.setColorHexCode(variant.getColor().getHexCode());
        }
        if (variant.getSize() != null) {
            response.setSizeName(variant.getSize().getName());
        }

        // Get primary image
        String primaryImageUrl = productRepository.getPriImageUrlById(product.getId());
        response.setPrimaryImageUrl(primaryImageUrl);

        // Calculate prices
        BigDecimal itemPrice = product.getDiscountPrice() != null ?
                               product.getDiscountPrice() : product.getBasePrice();
        BigDecimal totalPrice = itemPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        response.setItemPrice(itemPrice);
        response.setTotalPrice(totalPrice);

        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());

        return response;
    }
}

