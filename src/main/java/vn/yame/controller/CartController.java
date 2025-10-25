package vn.yame.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.yame.dto.reponse.CartResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.AddToCartRequest;
import vn.yame.dto.request.UpdateCartItemRequest;
import vn.yame.model.User;
import vn.yame.service.CartService;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Shopping Cart Management", description = "APIs for managing shopping cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(
        summary = "Get current user's cart",
        description = "Retrieve shopping cart for authenticated user with all items and totals"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Cart not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<CartResponse>> getCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("REST request to get cart for user: {}", user.getEmail());

        CartResponse response = cartService.getOrCreateCart(user.getId());

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Cart retrieved successfully",
            response
        ));
    }

    @PostMapping("/items")
    @Operation(
        summary = "Add item to cart",
        description = "Add a product variant to cart. If item already exists, quantity will be increased."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Item added to cart successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or insufficient stock"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Product variant not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<CartResponse>> addToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request) {
        User user = (User) authentication.getPrincipal();
        log.info("REST request to add product variant {} to cart for user: {}",
                 request.getProductVariantId(), user.getEmail());

        CartResponse response = cartService.addToCart(user.getId(), request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.success(
                HttpStatus.CREATED.value(),
                true,
                "Item added to cart successfully",
                response
            ));
    }

    @PutMapping("/items/{cartItemId}")
    @Operation(
        summary = "Update cart item quantity",
        description = "Update quantity of a specific item in cart"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart item updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or insufficient stock"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Cart item not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<CartResponse>> updateCartItem(
            Authentication authentication,
            @Parameter(description = "Cart Item ID", required = true)
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        User user = (User) authentication.getPrincipal();
        log.info("REST request to update cart item {} for user: {}", cartItemId, user.getEmail());

        CartResponse response = cartService.updateCartItem(user.getId(), cartItemId, request);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Cart item updated successfully",
            response
        ));
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(
        summary = "Remove item from cart",
        description = "Remove a specific item from cart"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item removed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Cart item not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<CartResponse>> removeCartItem(
            Authentication authentication,
            @Parameter(description = "Cart Item ID", required = true)
            @PathVariable Long cartItemId) {
        User user = (User) authentication.getPrincipal();
        log.info("REST request to remove cart item {} for user: {}", cartItemId, user.getEmail());

        CartResponse response = cartService.removeCartItem(user.getId(), cartItemId);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Item removed from cart successfully",
            response
        ));
    }

    @DeleteMapping("/clear")
    @Operation(
        summary = "Clear entire cart",
        description = "Remove all items from cart"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart cleared successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<CartResponse>> clearCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("REST request to clear cart for user: {}", user.getEmail());

        CartResponse response = cartService.clearCart(user.getId());

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Cart cleared successfully",
            response
        ));
    }

    @GetMapping("/count")
    @Operation(
        summary = "Get cart item count",
        description = "Get total quantity of items in cart (for badge display)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Integer>> getCartItemCount(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("REST request to get cart count for user: {}", user.getEmail());

        int count = cartService.getCartItemCount(user.getId());

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Cart count retrieved successfully",
            count
        ));
    }
}

