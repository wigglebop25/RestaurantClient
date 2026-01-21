package com.restaurantclient.data

import com.restaurantclient.data.dto.OrderItemRequest
import com.restaurantclient.data.dto.ProductResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class CartItem(
    val product: ProductResponse,
    val quantity: Int
) {
    val totalPrice: Double
        get() = product.price.toDouble() * quantity
}

@Singleton
class CartManager @Inject constructor() {
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    
    val totalAmount: Double
        get() = _cartItems.value.sumOf { it.totalPrice }
    
    val totalItems: Int
        get() = _cartItems.value.sumOf { it.quantity }
    
    fun addToCart(product: ProductResponse, quantity: Int = 1) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { it.product.product_id == product.product_id }
        
        if (existingItemIndex != -1) {
            val existingItem = currentItems[existingItemIndex]
            currentItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            currentItems.add(CartItem(product, quantity))
        }
        
        _cartItems.value = currentItems
    }
    
    fun removeFromCart(productId: Int) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.removeAll { it.product.product_id == productId }
        _cartItems.value = currentItems
    }
    
    fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }
        
        val currentItems = _cartItems.value.toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.product.product_id == productId }
        
        if (itemIndex != -1) {
            currentItems[itemIndex] = currentItems[itemIndex].copy(quantity = quantity)
            _cartItems.value = currentItems
        }
    }
    
    fun clearCart() {
        _cartItems.value = emptyList()
    }
    
    fun toOrderRequest(): List<OrderItemRequest> {
        return _cartItems.value.map { cartItem ->
            OrderItemRequest(
                product_id = cartItem.product.product_id,
                quantity = cartItem.quantity
            )
        }
    }
}
