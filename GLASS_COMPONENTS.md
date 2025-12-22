# Glass Components Library

This document describes the reusable glassmorphism components available in the Restaurant Client app.

## Overview

The glass components provide a consistent, modern glassmorphism design pattern across the app with:
- Blur effects
- Semi-transparent overlays
- Smooth animations
- Material Design 3 integration

---

## Components

### 1. GlassCard

A reusable card component with built-in BlurView.

#### Usage

**In XML:**
```xml
<com.restaurantclient.ui.common.GlassCard
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="16dp">
    
    <!-- Your content here -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Content" />
        
</com.restaurantclient.ui.common.GlassCard>
```

**In Kotlin:**
```kotlin
val glassCard = GlassCard(context)
glassCard.setBlurRadius(25f)
glassCard.setOverlayColor(Color.parseColor("#33FFFFFF"))
```

---

### 2. Glass Dialogs

Glassmorphic dialogs with blur effects.

#### Confirm Dialog

```kotlin
// Using helper method
context.showGlassConfirmDialog(
    title = "Delete Item",
    message = "Are you sure you want to delete this item?",
    positiveText = "Delete",
    negativeText = "Cancel",
    onPositive = {
        // Handle delete
    }
)

// Using helper class
GlassDialogHelper.showConfirmDialog(
    context = this,
    title = "Confirm Action",
    message = "This action cannot be undone",
    onPositive = { /* action */ }
)
```

#### Input Dialog

```kotlin
context.showGlassInputDialog(
    title = "Enter Name",
    hint = "Product name",
    initialValue = "Current Name",
    onInput = { name ->
        // Handle input
    }
)
```

#### Info Dialog

```kotlin
context.showGlassInfoDialog(
    title = "Success",
    message = "Operation completed successfully",
    buttonText = "OK"
)
```

---

### 3. Glass Snackbar

Modern snackbars with glassmorphism styling.

#### Basic Usage

```kotlin
// Show default glass snackbar
view.showGlassSnackbar("Operation successful")

// Show with duration
view.showGlassSnackbar("Message", Snackbar.LENGTH_LONG)
```

#### Styled Variants

```kotlin
// Success snackbar (green)
view.showSuccessSnackbar("Item saved successfully")

// Error snackbar (red)
view.showErrorSnackbar("Failed to load data")

// Warning snackbar (yellow)
view.showWarningSnackbar("Connection unstable")

// Info snackbar (blue)
view.showInfoSnackbar("New update available")
```

#### Advanced Usage

```kotlin
GlassSnackbar.make(view, "Custom message", Snackbar.LENGTH_INDEFINITE)
    .setAction("UNDO") {
        // Handle action
    }
    .show()
```

---

## Styles

### Glass Button Styles

Available button styles:

```xml
<!-- Outlined glass button -->
<com.google.android.material.button.MaterialButton
    style="@style/GlassButton"
    android:text="Button" />

<!-- Filled glass button -->
<com.google.android.material.button.MaterialButton
    style="@style/GlassButton.Filled"
    android:text="Button" />

<!-- Primary glass button -->
<com.google.android.material.button.MaterialButton
    style="@style/GlassButton.Primary"
    android:text="Button" />

<!-- Accent glass button -->
<com.google.android.material.button.MaterialButton
    style="@style/GlassButton.Accent"
    android:text="Button" />

<!-- Success button (green) -->
<com.google.android.material.button.MaterialButton
    style="@style/GlassButton.Success"
    android:text="Save" />

<!-- Error button (red) -->
<com.google.android.material.button.MaterialButton
    style="@style/GlassButton.Error"
    android:text="Delete" />

<!-- Warning button (yellow) -->
<com.google.android.material.button.MaterialButton
    style="@style/GlassButton.Warning"
    android:text="Warning" />

<!-- Info button (blue) -->
<com.google.android.material.button.MaterialButton
    style="@style/GlassButton.Info"
    android:text="Info" />

<!-- Text button -->
<com.google.android.material.button.MaterialButton
    style="@style/GlassButton.TextButton"
    android:text="Cancel" />
```

### Glass Card Styles

```xml
<!-- Default glass card -->
<com.google.android.material.card.MaterialCardView
    style="@style/GlassCard">
    <!-- Content -->
</com.google.android.material.card.MaterialCardView>

<!-- Elevated glass card -->
<com.google.android.material.card.MaterialCardView
    style="@style/GlassCard.Elevated">
    <!-- Content -->
</com.google.android.material.card.MaterialCardView>

<!-- Large glass card -->
<com.google.android.material.card.MaterialCardView
    style="@style/GlassCard.Large">
    <!-- Content -->
</com.google.android.material.card.MaterialCardView>
```

### Glass FAB Styles

```xml
<!-- Default glass FAB -->
<com.google.android.material.floatingactionbutton.FloatingActionButton
    style="@style/GlassFAB"
    app:srcCompat="@drawable/ic_add" />

<!-- Accent FAB -->
<com.google.android.material.floatingactionbutton.FloatingActionButton
    style="@style/GlassFAB.Accent"
    app:srcCompat="@drawable/ic_add" />

<!-- Primary FAB -->
<com.google.android.material.floatingactionbutton.FloatingActionButton
    style="@style/GlassFAB.Primary"
    app:srcCompat="@drawable/ic_add" />

<!-- Large FAB -->
<com.google.android.material.floatingactionbutton.FloatingActionButton
    style="@style/GlassFAB.Large"
    app:srcCompat="@drawable/ic_add" />
```

### Other Glass Styles

```xml
<!-- Glass chip -->
<com.google.android.material.chip.Chip
    style="@style/GlassChip"
    android:text="Filter" />

<!-- Glass text input -->
<com.google.android.material.textfield.TextInputLayout
    style="@style/GlassTextInputLayout"
    android:hint="Enter text">
    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</com.google.android.material.textfield.TextInputLayout>

<!-- Glass divider -->
<View style="@style/GlassDivider" />

<!-- Glass progress bar -->
<com.google.android.material.progressindicator.CircularProgressIndicator
    style="@style/GlassProgressBar" />

<!-- Glass switch -->
<com.google.android.material.switchmaterial.SwitchMaterial
    style="@style/GlassSwitch"
    android:text="Enable feature" />
```

---

## BlurView Extensions

Helper functions for BlurView setup:

```kotlin
// Setup glass effect with default radius (20f)
blurView.setupGlassEffect()

// Custom blur radius
blurView.setupGlassEffect(25f)

// Set blur intensity (animated)
blurView.setBlurIntensity(0.5f) // 0.0 to 1.0

// Apply general glass styling
view.applyGlassEffect(elevation = 8f, alpha = 0.95f)
```

---

## Color Resources

Glass-related colors:

```xml
<!-- Overlays -->
<color name="glass_overlay_light">#33FFFFFF</color>  <!-- 20% white -->
<color name="glass_overlay_dark">#33000000</color>   <!-- 20% black -->

<!-- Borders -->
<color name="glass_border_light">#44FFFFFF</color>   <!-- 27% white -->
<color name="glass_border_dark">#44000000</color>    <!-- 27% black -->

<!-- Blur tints -->
<color name="blur_tint_primary">#1A6750A4</color>    <!-- Primary with alpha -->
<color name="blur_tint_secondary">#1A625B71</color>  <!-- Secondary with alpha -->
<color name="blur_tint_accent">#1AEF4444</color>     <!-- Accent with alpha -->

<!-- White glass overlay -->
<color name="white_glass_overlay">#CCFFFFFF</color>  <!-- 80% white -->
```

---

## Dimensions

Glass component dimensions:

```xml
<!-- Card -->
<dimen name="glass_card_corner_radius">16dp</dimen>
<dimen name="glass_card_elevation">4dp</dimen>
<dimen name="glass_card_margin">8dp</dimen>

<!-- Button -->
<dimen name="glass_button_corner_radius">12dp</dimen>
<dimen name="glass_button_elevation">4dp</dimen>

<!-- Dialog -->
<dimen name="glass_dialog_corner_radius">20dp</dimen>
<dimen name="glass_dialog_margin">24dp</dimen>
<dimen name="glass_dialog_padding">24dp</dimen>

<!-- Blur effect -->
<dimen name="blur_radius_small">15dp</dimen>
<dimen name="blur_radius_normal">20dp</dimen>
<dimen name="blur_radius_large">25dp</dimen>
```

---

## Best Practices

### 1. Blur Radius
- Use `15-20f` for subtle effects
- Use `20-25f` for prominent glass elements
- Avoid > `30f` for performance

### 2. Overlay Opacity
- `10-30%` for transparent glass
- `40-60%` for frosted glass
- `70-90%` for semi-opaque elements

### 3. Performance
- Reuse BlurView instances when possible
- Avoid excessive nesting of glass elements
- Consider disabling blur on low-end devices

### 4. Accessibility
- Ensure sufficient contrast for text on glass
- Provide alternative themes if needed
- Test with TalkBack enabled

---

## Examples

### Complete Glass Form

```xml
<com.restaurantclient.ui.common.GlassCard
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="16dp">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="20dp">
        
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Login"
            android:textAppearance="?attr/textAppearanceHeadline5"
            android:layout_marginBottom="16dp" />
        
        <com.google.android.material.textfield.TextInputLayout
            style="@style/GlassTextInputLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Username">
            <com.google.android.material.textfield.TextInputEditText
                android:layout_width="match_parent"
                android:layout_height="wrap_content" />
        </com.google.android.material.textfield.TextInputLayout>
        
        <com.google.android.material.button.MaterialButton
            style="@style/GlassButton.Primary"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Login"
            android:layout_marginTop="16dp" />
            
    </LinearLayout>
    
</com.restaurantclient.ui.common.GlassCard>
```

### Glass Dashboard Card

```kotlin
class DashboardActivity : AppCompatActivity() {
    private fun setupStatCards() {
        val card = GlassCard(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }
            
            addView(createStatContent())
        }
        
        container.addView(card)
    }
}
```

---

## Troubleshooting

### Blur not visible
- Ensure background has content to blur
- Check overlay color alpha (should be semi-transparent)
- Verify blur radius is set (> 0)

### Performance issues
- Reduce blur radius
- Limit number of blur views on screen
- Consider static blur for RecyclerView items

### Build errors
- Verify BlurView dependency is added
- Check imports: `com.eightbitlab.com.blurview.BlurView`
- Ensure JitPack repository is configured

---

## Migration Guide

### From Standard Cards to Glass Cards

**Before:**
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    <!-- Content -->
</com.google.android.material.card.MaterialCardView>
```

**After:**
```xml
<com.restaurantclient.ui.common.GlassCard
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    <!-- Content -->
</com.restaurantclient.ui.common.GlassCard>
```

### From Toast to Glass Snackbar

**Before:**
```kotlin
Toast.makeText(context, "Message", Toast.LENGTH_SHORT).show()
```

**After:**
```kotlin
view.showGlassSnackbar("Message")
// or
view.showSuccessSnackbar("Success message")
```

---

## License

These components use the BlurView library which is licensed under Apache 2.0.
See [LICENSE](../../LICENSE) for details.
