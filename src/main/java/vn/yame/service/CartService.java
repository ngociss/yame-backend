package vn.yame.service;

import vn.yame.dto.request.AddToCartRequest;
import vn.yame.dto.request.UpdateCartItemRequest;
import vn.yame.dto.reponse.CartResponse;

public interface CartService {

    CartResponse getOrCreateCart(Long userId);

    CartResponse getCart(Long userId);

    CartResponse addToCart(Long userId, AddToCartRequest request);

    CartResponse updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request);

    CartResponse removeCartItem(Long userId, Long cartItemId);

    CartResponse clearCart(Long userId);

    int getCartItemCount(Long userId);
}

