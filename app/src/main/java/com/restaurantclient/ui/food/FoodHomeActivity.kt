package com.restaurantclient.ui.food

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.restaurantclient.R
import com.restaurantclient.data.Result
import com.restaurantclient.data.dto.ProductResponse
import com.restaurantclient.databinding.ActivityFoodHomeBinding
import com.restaurantclient.ui.cart.ShoppingCartActivity
import com.restaurantclient.ui.order.MyOrdersActivity
import com.restaurantclient.ui.product.ProductViewModel
import com.restaurantclient.ui.user.UserProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodHomeBinding
    private val productViewModel: ProductViewModel by viewModels()
    private lateinit var foodProductAdapter: FoodProductAdapter
    private var allProducts: List<ProductResponse> = emptyList()
    private var selectedCategory: String = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        setupCategoryChips()
        setupObservers()
        
        // Load products
        productViewModel.fetchProducts()
    }

    private fun setupRecyclerView() {
        foodProductAdapter = FoodProductAdapter(
            onClick = { product ->
                val intent = Intent(this, FoodDetailActivity::class.java).apply {
                    putExtra(FoodDetailActivity.EXTRA_PRODUCT_ID, product.product_id)
                    putExtra(FoodDetailActivity.EXTRA_PRODUCT_NAME, product.name)
                    putExtra(FoodDetailActivity.EXTRA_PRODUCT_PRICE, product.price)
                    putExtra(FoodDetailActivity.EXTRA_PRODUCT_DESCRIPTION, product.description)
                    putExtra(FoodDetailActivity.EXTRA_PRODUCT_IMAGE, product.product_image_uri)
                }
                startActivity(intent)
            },
            onFavoriteClick = { product ->
                // TODO: Implement favorite functionality
                Toast.makeText(this, "Added ${product.name} to favorites", Toast.LENGTH_SHORT).show()
            }
        )
        
        binding.productsRecyclerView.apply {
            layoutManager = GridLayoutManager(this@FoodHomeActivity, 2)
            adapter = foodProductAdapter
        }
    }

    private fun setupClickListeners() {
        binding.filterButton.setOnClickListener {
            // TODO: Implement filter functionality
            Toast.makeText(this, "Filter clicked", Toast.LENGTH_SHORT).show()
        }

        binding.searchInput.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchInput.text.toString()
            if (query.isNotEmpty()) {
                filterProducts(query)
            }
            true
        }

        // Bottom navigation
        binding.navHome.setOnClickListener {
            // Already on home
        }

        binding.navProfile.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, ShoppingCartActivity::class.java))
        }

        binding.navOrders.setOnClickListener {
            startActivity(Intent(this, MyOrdersActivity::class.java))
        }

        binding.navFavorites.setOnClickListener {
            // TODO: Implement favorites screen
            Toast.makeText(this, "Favorites coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.profileImage.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }
    }

    private fun setupCategoryChips() {
        binding.categoryChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds[0])
                selectedCategory = chip?.text.toString()
                filterByCategory(selectedCategory)
            }
        }
    }

    private fun setupObservers() {
        productViewModel.products.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            when (result) {
                is Result.Success -> {
                    allProducts = result.data
                    filterByCategory(selectedCategory)
                }
                is Result.Error -> {
                    Toast.makeText(
                        this,
                        "Failed to load products: ${result.exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun filterByCategory(category: String) {
        val filteredProducts = if (category == "All") {
            allProducts
        } else {
            // Simple filtering - you can enhance this based on your product categories
            allProducts.filter { product ->
                product.name.contains(category, ignoreCase = true) ||
                product.description.contains(category, ignoreCase = true)
            }
        }
        foodProductAdapter.submitList(filteredProducts)
    }

    private fun filterProducts(query: String) {
        val filteredProducts = allProducts.filter { product ->
            product.name.contains(query, ignoreCase = true) ||
            product.description.contains(query, ignoreCase = true)
        }
        foodProductAdapter.submitList(filteredProducts)
    }
}
