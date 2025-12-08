
# Orderly App UI/UX Design (Material Design 3)

This document outlines the UI/UX design for the Orderly Android application, following Google's Material Design 3 principles. The goal is to create a modern, intuitive, and visually appealing user experience for a customer-facing ordering app.

*(Note: The referenced `@android_backend_plan.md` was not found. This design is based on the existing application structure and common e-commerce patterns.)*

## 1. Core Principles

- **Color Palette:** A simple, clean palette will be used. A primary color (e.g., a shade of blue or green) will be used for branding and key actions. Neutral surfaces with pops of the primary color will define the app's look. Dark theme will be fully supported.
- **Typography:** Standard Material Design type scale will be used (e.g., Display, Headline, Title, Body, Label) for clear hierarchy and readability.
- **Components:** We will use standard Material Design components like Cards, Buttons (standard, elevated, filled), Top App Bars, Badges, and Navigation components.

## 2. Screen-by-Screen Breakdown

### 2.1. Login Screen (`activity_login.xml`)

- **Layout:** A centered vertical layout to focus the user on the task.
- **Components:**
    - **App Logo:** A clean, simple logo for the "Orderly" app at the top.
    - **Text Fields:** Two `TextInputLayout` components for "Email" and "Password". They will use the "filled" style for a modern look, with clear hint text and error handling display.
    - **Buttons:**
        - A "Login" `Button` (filled style) as the primary call-to-action.
        - A "Sign Up" `Button` (text button style) for new users.
- **Interaction:** Clear focus states and error messages for validation (e.g., "Invalid email format", "Password cannot be empty").

### 2.2. Product List Screen (`activity_product_list.xml`)

- **Layout:** A `RecyclerView` to display a grid of products, which is ideal for showcasing product images.
- **Components:**
    - **Top App Bar:**
        - Title: "Products".
        - Actions: 
            - A search icon.
            - A shopping cart icon with a `Badge` to show the number of items in the cart.
            - A menu icon for "My Orders" and "Profile".
    - **Product Item (`item_product.xml`):**
        - Each product will be displayed in a `CardView`.
        - Inside the card:
            - An `ImageView` for the product image.
            - A `TextView` for the product name (Title style).
            - A `TextView` for the product price (Body style, with primary color).
- **Interaction:** Tapping a product card navigates to the Product Detail screen. Tapping the cart icon navigates to the Shopping Cart screen.

### 2.3. Product Detail Screen (`activity_product_detail.xml`)

- **Layout:** A vertically oriented layout with product details and clear action buttons.
- **Components:**
    - **Top App Bar:** The product name as the title and a back arrow.
    - **Image:** A large `ImageView` for the product image.
    - **Details Section:**
        - `TextView` for product name (Headline style).
        - `TextView` for product description (Body style).
        - `TextView` for price (Title style).
    - **Quantity Selector:** A component with "+" and "-" buttons and a number display to select the quantity.
    - **Action Button:** An "Add to Cart" `Button` (Extended FAB or filled button) at the bottom.
- **Interaction:** "Add to Cart" button adds the selected quantity of the item to the cart and updates the cart badge. A confirmation (e.g., a Snackbar) can be shown.

### 2.4. Shopping Cart Screen (New)

- **Layout:** A `RecyclerView` to list all items in the cart.
- **Components:**
    - **Top App Bar:** Title: "My Cart", and a back arrow.
    - **Cart Item (New layout file `item_cart.xml`):**
        - Each item will be in a `CardView`.
        - `ImageView` for the product thumbnail.
        - `TextView` for the product name.
        - Quantity selector to adjust the quantity.
        - `TextView` for the item total price.
        - A "Remove" icon button.
    - **Summary Section:** At the bottom, a summary of the total price.
    - **Action Button:** A "Proceed to Checkout" `Button`.

### 2.5. Checkout Screen (New)

- **Layout:** A simple confirmation screen. For this version, we assume the user's shipping/payment info is on file.
- **Components:**
    - **Top App Bar:** Title: "Confirm Order".
    - **Order Summary:** A non-editable summary of the items and total price.
    - **Action Button:** A "Place Order" `Button`.
- **Interaction:** Tapping "Place Order" submits the order and navigates the user to the "My Orders" screen or a thank you page.

### 2.6. My Orders Screen (`activity_my_orders.xml`)

- **Layout:** A `RecyclerView` to display a list of the user's past and current orders.
- **Components:**
    - **Top App Bar:** Title: "My Orders", and a back arrow.
    - **Order Item (`item_order.xml`):**
        - Each order in a `CardView`.
        - `TextView` for Order ID, date, and total price.
        - A `TextView` for the order status ("Processing", "Shipped", "Delivered") with distinct status colors.
- **Interaction:** Tapping on an order could navigate to an "Order Details" screen, showing the items in that specific order.

### 2.7. User Profile Screen (`activity_user_profile.xml`)

- **Layout:** A simple layout for displaying user information.
- **Components:**
    - **Top App Bar:** Title: "Profile", and a back arrow.
    - **Avatar:** `ImageView` for the user's profile picture or initials.
    - **User Info:** `TextView`s for user's name and email.
    - **Button:** A "Logout" `Button` at the bottom.
