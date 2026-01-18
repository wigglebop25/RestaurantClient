package com.restaurantclient.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.ProductResponse
import com.restaurantclient.data.dto.ProductRequest
import com.restaurantclient.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.restaurantclient.data.network.WebSocketEvent
import com.restaurantclient.data.network.WebSocketManager
import com.restaurantclient.BuildConfig
import kotlinx.coroutines.Job

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    private val _products = MutableLiveData<Result<List<ProductResponse>>>()
    val products: LiveData<Result<List<ProductResponse>>> = _products

    private val _selectedProduct = MutableLiveData<Result<ProductResponse>>()
    val selectedProduct: LiveData<Result<ProductResponse>> = _selectedProduct

    private val _productMutation = MutableLiveData<Result<Unit>>()
    val productMutation: LiveData<Result<Unit>> = _productMutation

    private val _mutationLoading = MutableLiveData<Boolean>()
    val mutationLoading: LiveData<Boolean> = _mutationLoading
    
    private var monitoringJob: Job? = null

    init {
        startMonitoringProducts()
    }

    fun fetchProducts(forceRefresh: Boolean = false, showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _mutationLoading.value = true // Reusing mutation loading for simplicity or add fetchLoading
            try {
                val result = productRepository.getAllProducts(forceRefresh)
                _products.value = result // Use .value instead of .postValue for immediate updates in main thread
            } finally {
                if (showLoading) _mutationLoading.value = false
            }
        }
    }
    
    private fun startMonitoringProducts() {
        if (monitoringJob?.isActive == true) return
        
        webSocketManager.connect(BuildConfig.BASE_URL)
        
        monitoringJob = viewModelScope.launch {
            webSocketManager.events.collect { event ->
                if (event is WebSocketEvent.RefreshRequired) {
                    fetchProducts(forceRefresh = true, showLoading = false)
                }
            }
        }
    }

    fun fetchProductDetails(productId: Int) {
        viewModelScope.launch {
            val result = productRepository.getProductById(productId)
            _selectedProduct.postValue(result)
        }
    }

    fun createProduct(name: String, description: String, price: String, imageUrl: String) {
        mutateProduct { productRepository.createProduct(ProductRequest(name, description, price, imageUrl, emptyList())) }
    }

    fun updateProduct(productId: Int, name: String, description: String, price: String, imageUrl: String) {
        mutateProduct { productRepository.updateProduct(productId, ProductRequest(name, description, price, imageUrl, emptyList())) }
    }

    fun deleteProduct(productId: Int) {
        mutateProduct { productRepository.deleteProduct(productId) }
    }

    suspend fun getProductsByCategory(categoryId: Int): Result<List<ProductResponse>> {
        return productRepository.getProductsByCategory(categoryId)
    }

    private fun mutateProduct(block: suspend () -> Result<*>) {
        viewModelScope.launch {
            _mutationLoading.value = true
            val result = try {
                block()
            } catch (e: Exception) {
                Result.Error(e)
            }

            when (result) {
                is Result.Success<*> -> {
                    _productMutation.value = Result.Success(Unit)
                    productRepository.clearCache() // Invalidate cache
                    fetchProducts(true) // Refresh products from API
                }
                is Result.Error -> _productMutation.value = Result.Error(result.exception)
            }

            _mutationLoading.value = false
        }
    }
}
