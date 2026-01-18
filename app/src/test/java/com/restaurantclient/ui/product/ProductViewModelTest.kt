package com.restaurantclient.ui.product

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.ProductResponse
import com.restaurantclient.data.repository.ProductRepository
import com.restaurantclient.data.network.WebSocketManager
import com.restaurantclient.util.MainCoroutineRule // Added import
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ProductViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule // Added back
    var mainCoroutineRule = MainCoroutineRule() // Added back

    private lateinit var productViewModel: ProductViewModel
    private val productRepository: ProductRepository = mock()
    private val webSocketManager: WebSocketManager = mock()

    @Before
    fun setUp() {
        // Mock WebSocket events flow to avoid NPE or stuck collection
        whenever(webSocketManager.events).thenReturn(MutableSharedFlow())
        productViewModel = ProductViewModel(productRepository, webSocketManager)
    }

    @Test
    fun `fetchProducts success posts success result`() = runTest {
        // Given
        val products = listOf(ProductResponse(1, "p1", "d1", "1.0", ""))
        whenever(productRepository.getAllProducts()).thenReturn(Result.Success(products))

        // When
        productViewModel.fetchProducts()

        // Then
        assert(productViewModel.products.value is Result.Success)
    }

    @Test
    fun `fetchProducts failure posts error result`() = runTest {
        // Given
        val exception = Exception("Failed to fetch")
        whenever(productRepository.getAllProducts()).thenReturn(Result.Error(exception))

        // When
        productViewModel.fetchProducts()

        // Then
        assert(productViewModel.products.value is Result.Error)
    }

    @Test
    fun `fetchProductDetails success posts success result`() = runTest {
        // Given
        val product = ProductResponse(1, "p1", "d1", "1.0", "")
        whenever(productRepository.getProductById(1)).thenReturn(Result.Success(product))

        // When
        productViewModel.fetchProductDetails(1)

        // Then
        assert(productViewModel.selectedProduct.value is Result.Success)
    }
}
