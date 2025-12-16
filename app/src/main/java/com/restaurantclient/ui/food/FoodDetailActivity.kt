package com.restaurantclient.ui.food

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.restaurantclient.R
import com.restaurantclient.data.CartManager
import com.restaurantclient.databinding.ActivityFoodDetailBinding
import com.restaurantclient.databinding.DialogSuccessBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FoodDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodDetailBinding
    private var quantity: Int = 2
    private var spicyLevel: Int = 50
    private var productId: String = ""
    private var productName: String = ""
    private var productPrice: String = ""
    private var productDescription: String = ""
    
    @Inject
    lateinit var cartManager: CartManager

    companion object {
        const val EXTRA_PRODUCT_ID = "product_id"
        const val EXTRA_PRODUCT_NAME = "product_name"
        const val EXTRA_PRODUCT_PRICE = "product_price"
        const val EXTRA_PRODUCT_DESCRIPTION = "product_description"
        const val EXTRA_PRODUCT_IMAGE = "product_image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProductData()
        setupClickListeners()
        setupSpicySlider()
        updateQuantityDisplay()
    }

    private fun loadProductData() {
        productId = intent.getStringExtra(EXTRA_PRODUCT_ID) ?: ""
        productName = intent.getStringExtra(EXTRA_PRODUCT_NAME) ?: ""
        productPrice = intent.getStringExtra(EXTRA_PRODUCT_PRICE) ?: "0.00"
        productDescription = intent.getStringExtra(EXTRA_PRODUCT_DESCRIPTION) ?: ""

        binding.productName.text = productName
        binding.productDescription.text = productDescription
        
        // Generate random rating and time for demo
        val rating = String.format("%.1f", Random.nextDouble(3.5, 5.0))
        val time = Random.nextInt(15, 40)
        binding.ratingText.text = rating
        binding.timeText.text = getString(R.string.estimated_time, time)
        
        updatePriceDisplay()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.searchButton.setOnClickListener {
            // TODO: Implement search
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show()
        }

        binding.decrementButton.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantityDisplay()
                updatePriceDisplay()
            }
        }

        binding.incrementButton.setOnClickListener {
            if (quantity < 99) {
                quantity++
                updateQuantityDisplay()
                updatePriceDisplay()
            }
        }

        binding.orderNowButton.setOnClickListener {
            placeOrder()
        }

        binding.priceButton.setOnClickListener {
            // Same action as order now
            placeOrder()
        }
    }

    private fun setupSpicySlider() {
        binding.spicySlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                spicyLevel = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateQuantityDisplay() {
        binding.quantityText.text = quantity.toString()
    }

    private fun updatePriceDisplay() {
        val basePrice = productPrice.toDoubleOrNull() ?: 0.0
        val totalPrice = basePrice * quantity
        binding.priceButton.text = String.format("$%.2f", totalPrice)
    }

    private fun placeOrder() {
        // Add to cart
        // For demo, we'll show the success dialog
        showSuccessDialog()
    }

    private fun showSuccessDialog() {
        val dialogBinding = DialogSuccessBinding.inflate(layoutInflater)
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialogBinding.goBackButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
        
        // Make dialog background transparent for rounded corners
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
