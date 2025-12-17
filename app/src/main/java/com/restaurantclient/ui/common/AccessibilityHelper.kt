package com.restaurantclient.ui.common

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

/**
 * Accessibility utilities for glassmorphic UI elements
 * Ensures WCAG AA compliance and TalkBack compatibility
 */
object AccessibilityHelper {
    
    private const val MIN_CONTRAST_RATIO_AA = 4.5  // WCAG AA standard
    private const val MIN_CONTRAST_RATIO_AAA = 7.0  // WCAG AAA standard
    private const val MIN_TOUCH_TARGET_SIZE_DP = 48
    
    /**
     * Check if accessibility services are enabled
     */
    fun isAccessibilityEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
        return am?.isEnabled == true
    }
    
    /**
     * Check if TalkBack is enabled
     */
    fun isTalkBackEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
        return am?.isTouchExplorationEnabled == true
    }
    
    /**
     * Calculate contrast ratio between two colors
     * @return contrast ratio (1.0 to 21.0)
     */
    fun calculateContrastRatio(foreground: Int, background: Int): Double {
        val fgLuminance = calculateRelativeLuminance(foreground)
        val bgLuminance = calculateRelativeLuminance(background)
        
        val lighter = maxOf(fgLuminance, bgLuminance)
        val darker = minOf(fgLuminance, bgLuminance)
        
        return (lighter + 0.05) / (darker + 0.05)
    }
    
    /**
     * Calculate relative luminance of a color (WCAG formula)
     */
    private fun calculateRelativeLuminance(color: Int): Double {
        val r = Color.red(color) / 255.0
        val g = Color.green(color) / 255.0
        val b = Color.blue(color) / 255.0
        
        val rLinear = if (r <= 0.03928) r / 12.92 else Math.pow((r + 0.055) / 1.055, 2.4)
        val gLinear = if (g <= 0.03928) g / 12.92 else Math.pow((g + 0.055) / 1.055, 2.4)
        val bLinear = if (b <= 0.03928) b / 12.92 else Math.pow((b + 0.055) / 1.055, 2.4)
        
        return 0.2126 * rLinear + 0.7152 * gLinear + 0.0722 * bLinear
    }
    
    /**
     * Check if color combination meets WCAG AA standards
     */
    fun meetsWCAGAA(foreground: Int, background: Int): Boolean {
        return calculateContrastRatio(foreground, background) >= MIN_CONTRAST_RATIO_AA
    }
    
    /**
     * Check if color combination meets WCAG AAA standards
     */
    fun meetsWCAGAAA(foreground: Int, background: Int): Boolean {
        return calculateContrastRatio(foreground, background) >= MIN_CONTRAST_RATIO_AAA
    }
    
    /**
     * Get accessible color for text over glass background
     */
    fun getAccessibleTextColor(context: Context, backgroundColor: Int): Int {
        val white = ContextCompat.getColor(context, android.R.color.white)
        val black = ContextCompat.getColor(context, android.R.color.black)
        
        val whiteContrast = calculateContrastRatio(white, backgroundColor)
        val blackContrast = calculateContrastRatio(black, backgroundColor)
        
        return if (whiteContrast >= blackContrast) white else black
    }
    
    /**
     * Ensure minimum touch target size for accessibility
     */
    fun View.ensureMinimumTouchTarget() {
        val density = resources.displayMetrics.density
        val minSize = (MIN_TOUCH_TARGET_SIZE_DP * density).toInt()
        
        post {
            if (width < minSize || height < minSize) {
                val widthPadding = maxOf(0, (minSize - width) / 2)
                val heightPadding = maxOf(0, (minSize - height) / 2)
                
                setPadding(
                    paddingLeft + widthPadding,
                    paddingTop + heightPadding,
                    paddingRight + widthPadding,
                    paddingBottom + heightPadding
                )
            }
        }
    }
    
    /**
     * Set up accessibility for glass card
     */
    fun View.setupGlassAccessibility(
        contentDescription: String,
        isClickable: Boolean = true,
        actionLabel: String? = null
    ) {
        this.contentDescription = contentDescription
        this.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        
        if (isClickable) {
            this.isClickable = true
            this.isFocusable = true
            
            ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    
                    // Add custom action if provided
                    actionLabel?.let { label ->
                        info.addAction(
                            AccessibilityNodeInfoCompat.AccessibilityActionCompat(
                                AccessibilityNodeInfoCompat.ACTION_CLICK,
                                label
                            )
                        )
                    }
                    
                    // Mark as a card/container
                    info.roleDescription = "Card"
                }
            })
        }
    }
    
    /**
     * Announce message to accessibility services
     */
    fun View.announceForAccessibility(message: String) {
        if (context.isAccessibilityEnabled()) {
            announceForAccessibility(message)
            sendAccessibilityEvent(AccessibilityEvent.TYPE_ANNOUNCEMENT)
        }
    }
    
    /**
     * Set up accessibility for button in glass container
     */
    fun View.setupGlassButtonAccessibility(
        label: String,
        hint: String? = null
    ) {
        contentDescription = label
        importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
        
        hint?.let {
            ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.hintText = hint
                }
            })
        }
        
        // Ensure minimum touch target
        ensureMinimumTouchTarget()
    }
    
    /**
     * Check if reduce motion is enabled
     */
    fun isReduceMotionEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
        return try {
            val method = AccessibilityManager::class.java.getMethod("isReduceMotionEnabled")
            method.invoke(am) as? Boolean ?: false
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get safe animation duration based on accessibility settings
     */
    fun getSafeAnimationDuration(context: Context, defaultDuration: Long): Long {
        return if (isReduceMotionEnabled(context) || !BlurPerformanceManager.shouldEnableAnimations()) {
            0L
        } else {
            defaultDuration
        }
    }
}

/**
 * Extension function to check if context has accessibility enabled
 */
fun Context.isAccessibilityEnabled(): Boolean {
    return AccessibilityHelper.isAccessibilityEnabled(this)
}

/**
 * Extension function to check if TalkBack is enabled
 */
fun Context.isTalkBackEnabled(): Boolean {
    return AccessibilityHelper.isTalkBackEnabled(this)
}

/**
 * Extension to apply accessible glass styling
 */
fun View.applyAccessibleGlassStyle(
    description: String,
    textColor: Int? = null
) {
    contentDescription = description
    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    
    // Ensure text has sufficient contrast
    textColor?.let { color ->
        val backgroundColor = (background as? android.graphics.drawable.ColorDrawable)?.color
        backgroundColor?.let { bg ->
            if (!AccessibilityHelper.meetsWCAGAA(color, bg)) {
                // Use accessible color
                val accessibleColor = AccessibilityHelper.getAccessibleTextColor(context, bg)
                if (this is android.widget.TextView) {
                    setTextColor(accessibleColor)
                }
            }
        }
    }
}
