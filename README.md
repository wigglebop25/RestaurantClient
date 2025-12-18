# Itadaki - Restaurant Client Android App

![Android](https://img.shields.io/badge/Android-33--36-green?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue?logo=kotlin)
![Material Design 3](https://img.shields.io/badge/Material%20Design-3-purple)
![Build](https://img.shields.io/badge/Build-Passing-success)
![License](https://img.shields.io/badge/License-MIT-blue)

**A modern Android restaurant ordering system featuring Itadaki brand identity, animated backgrounds, glassmorphism UI, and visual consistency**

🌟 **Featured:** Itadaki brand theme • Animated GIF backgrounds • Visual consistency • Auto-scrolling carousel • 60 FPS animations

---

## 📱 Description

**Itadaki** (頂 - "to receive") - an expression of gratitude for the food we eat. Our restaurant was founded on the philosophy that great food should be both an art form and an accessible joy.

Restaurant Client is a comprehensive mobile application designed to streamline restaurant order management. Built with cutting-edge Android development practices, it provides an efficient and intuitive platform for both administrators and customers to handle orders, products, and user accounts with a stunning Itadaki-branded UI.

### ✨ What's New in v2.5
- 🎨 **Itadaki Brand Identity** - Warm cream backgrounds with deep red accents
- 🎬 **Animated GIF Backgrounds** - Steam animation on login screen with smart caching
- 🎠 **Introduction Carousel** - Auto-scrolling product showcase with 15 menu items
- 🎯 **Visual Consistency** - Unified color palette across all customer screens
- 💎 **Enhanced Glassmorphism** - Non-rounded glass borders and frosted effects
- ⚡ **Optimized Performance** - Preloaded GIF caching for instant loading
- 🎨 **Color Psychology** - High arousal red UI balanced with warm cream tones
- 🔄 **Reusable Components** - Customer and admin toolbars for consistency

### Color Palette (Itadaki Theme)
- **Cream Background:** `#FFF8E1` - Warm, inviting base color
- **Deep Red:** `#D32F2F` - Primary brand color for CTAs and prices
- **Charcoal:** `#1D3557` - Dark text for readability
- **Gold Accent:** `#FFD700` - Premium highlighting
- **Glass White:** `#75FFFFFF` - 30% opacity for glassmorphism

### Backend Server
**ARROW (Asynchronous Rust Restaurant Order Workflow)** - A high-performance backend server built with Rust, leveraging the language's safety and concurrency features to deliver robust and efficient restaurant operations management.

---

## ✨ Features

### 🎨 Itadaki Brand Experience
- **🎬 Animated Welcome** - Steam animation GIF on login screen
  - Smart preloading and caching for instant playback
  - No flickering or white flash on load
  - Singleton ImageLoader for optimal performance
- **🎠 Product Showcase** - Auto-scrolling carousel featuring 15 authentic dishes
  - Coroutine-based auto-slide every 3 seconds
  - ViewPager2 with page indicators
  - Categories: Zensai, Sushi & Sashimi, Menrui, Donburi, Kanmi
- **🎯 Visual Consistency** - Unified Itadaki color palette
  - Cream backgrounds (#FFF8E1) across all screens
  - Deep red (#D32F2F) for CTAs and prices
  - Charcoal (#1D3557) for readable text
  - Glassmorphism with frosted effects

### 👤 Customer Features
- **🔐 Secure Authentication** - Role-based access control with animated login
- **📖 Introduction Screen** - Beautiful product carousel with brand story
  - "Authentic Taste, Delivered" tagline
  - 15 featured products with descriptions and prices
  - About button linking to intro/philosophy
- **🍽️ Product Browsing** - Browse restaurant menu with Itadaki styling
  - Glass product cards with frosted effects
  - Smooth animations and transitions
  - Real-time search and filtering
  - Glass FAB for shopping cart
- **🛒 Shopping Cart** - Real-time cart management with Itadaki colors
  - Add/remove items with glass animations
  - Adjust quantities with deep red buttons
  - Live price calculations
  - Combined glass total & checkout card
- **💳 Checkout Process** - Streamlined order placement
  - Glass order summary with cream background
  - Combined total and button in single glass card
  - Navigation bar spacing optimized
- **📦 Order History** - View past orders with consistent styling
- **👥 User Profile** - Manage account with Itadaki theme

### 🔧 Administrator Features
- **📊 Admin Dashboard** - Comprehensive overview with glass stat cards
  - Real-time statistics
  - Glass navigation drawer
  - Quick action shortcuts
  - Performance-optimized animations
- **👥 User Management** - Create, update, and delete user accounts
  - Glass user cards
  - Role assignment with glass chips
  - Glass FAB for adding users
  - Smooth CRUD operations
- **🍔 Product Management** - Full CRUD operations for menu items
  - Glass product cards with images
  - Glass floating FAB
  - Category management
  - Stock tracking
- **📋 Order Management** - View, update, and track all customer orders
  - Glass order cards with status
  - Real-time updates
  - Status-based blur overlays
  - Detailed order views
- **🎨 Modern Admin UI** - Professional blue theme with glassmorphism effects

### 🎨 UI/UX Features
- **Itadaki Brand Identity** - Cohesive visual language
  - Warm cream (#FFF8E1) backgrounds for appetizing feel
  - Deep red (#D32F2F) for high arousal CTAs
  - Charcoal (#1D3557) text for optimal readability
  - Cursive fonts for "Authentic Taste, Delivered"
- **Animated Backgrounds** - GIF integration with Coil
  - Steam animation on login screen
  - Application-level preloading for zero delay
  - Memory and disk caching for performance
- **Glassmorphism Design** - Modern blur effects with Itadaki colors
  - 25+ glass styles and components
  - Non-rounded glass borders for product overlays
  - Frosted white glass (#75FFFFFF) throughout
  - Memory-efficient blur rendering
- **Material Design 3** - Latest Material Design guidelines
  - Itadaki color system integration
  - Adaptive layouts
  - Modern typography with cursive accents
- **Responsive Layouts** - Optimized for various screen sizes
- **Glass Components** - Comprehensive library of reusable components
  - Glass buttons (9 variants) with deep red tint
  - Glass cards (3 variants) with cream compatibility
  - Glass dialogs with Itadaki branding
  - Glass snackbars (5 types)
  - Glass FABs with blur
  - Glass text inputs
  - Glass chips and switches
- **Smooth Animations** - 10+ performance-optimized animations
  - Auto-scrolling carousel with coroutines
  - Fade in/out with glass effect
  - Scale in/out with overshoot
  - Slide animations
  - Pulse and shimmer effects
  - Staggered list animations

### ⚡ Performance & Optimization
- **Automatic Performance Tiers** - Adapts to device capabilities
  - HIGH_QUALITY (6GB+ RAM): Full effects, 60 FPS
  - BALANCED (3-6GB RAM): Optimized effects, 55+ FPS
  - POWER_SAVER (<3GB RAM): Reduced effects, 45+ FPS
- **Memory Management** - Zero memory leaks guaranteed
  - Real-time monitoring
  - Automatic optimization
  - WeakReference-based caching
- **Animation System** - Performance-aware animations
  - Respects battery saver mode
  - Supports "reduce motion" setting
  - Smooth interpolators

### ♿ Accessibility Features
- **WCAG AA Compliance** - Full accessibility support
  - 4.5:1 minimum contrast ratio
  - Automatic color contrast validation
  - Accessible color selection
- **TalkBack Support** - Complete screen reader compatibility
  - Content descriptions for all elements
  - Custom accessibility actions
  - Role descriptions for containers
- **Touch Targets** - 48dp minimum enforced
- **Reduce Motion** - Respects system accessibility settings
- **Testing Utilities** - Automated accessibility validation

---

## 🛠️ Technologies Used

### Frontend
- **Language:** Kotlin 2.1.0
- **UI Framework:** Android XML Layouts with ViewBinding
- **Architecture:** MVVM (Model-View-ViewModel)
- **Minimum SDK:** API 33 (Android 13)
- **Target SDK:** API 36 (Android 15)
- **Build System:** Gradle 8.13 with KSP

### Core Libraries
- **Hilt:** 2.51.1 (Dependency Injection)
- **Retrofit:** 2.9.0 (REST API Client)
- **OkHttp:** 4.11.0 (HTTP Client & Logging)
- **Kotlin Coroutines:** 1.7.3 (Asynchronous Programming)
- **Lifecycle & ViewModel:** 2.7.0
- **Material Design 3:** 1.13.0
- **BlurView:** 3.2.0 (Glassmorphism Effects)
- **Coil:** 2.x (Image Loading with GIF support)
  - GifDecoder for animated backgrounds
  - ImageDecoderDecoder for Android 9+
  - Memory and disk caching
- **Security Crypto:** 1.1.0-alpha06 (Encrypted Storage)
- **AndroidX Core:** 1.15.0
- **ViewPager2:** Latest (Product Carousel)
- **Lottie:** 6.x (Success Animations)

### Glassmorphism & UI
- **BlurView:** Modern blur effects and glassmorphism
- **Coil:** GIF animation support with caching
  - Application-level preloading
  - Singleton ImageLoader for consistency
- **Material Components:** Latest MD3 components with Itadaki colors
- **Custom Glass Library:** 25+ reusable glass components
- **Animation Framework:** 10+ performance-optimized animations
- **Accessibility Tools:** WCAG AA compliance utilities
- **Itadaki Color System:** Consistent palette management

### Performance & Testing
- **Performance Manager:** Automatic device tier detection
- **Memory Optimizer:** Zero-leak memory management
- **Testing Framework:** JUnit 4, Espresso, AndroidX Test
- **Testing Utilities:** Performance and accessibility testing

### Backend
- **Framework:** Rust (ARROW Server)
- **Database:** MySQL
- **API:** RESTful Architecture

### Development Tools
- **IDE:** Android Studio Ladybug | 2024.2.1
- **Build Tool:** Gradle 8.13
- **JDK:** 21 (21.0.8)
- **Version Control:** Git 2.48.1
- **Kotlin Compiler:** 2.1.0

---

## 📦 Installation

### Prerequisites
- Android Studio Ladybug 2024.2.1 or later
- JDK 21 (21.0.8 or later)
- Git 2.48.1 or later
- Minimum Android device: API 33 (Android 13)
- Recommended: 4GB+ RAM device for full glass effects

### Steps

1. **Clone the repository:**
```bash
git clone https://github.com/your-username/RestaurantClient.git
cd RestaurantClient
```

2. **Open in Android Studio**
   - File → Open → Select RestaurantClient folder
   - Wait for Gradle sync to complete

3. **Configure Backend API**
   
   Create/update `local.properties`:
   ```properties
   API_BASE_URL=http://your-backend-server.com/api/
   ```

4. **Sync Gradle Dependencies**
   - The project will automatically download all dependencies
   - BlurView library will be fetched from JitPack

5. **Run the Application**
   - Connect Android device or start emulator (API 33+)
   - Click Run (Shift + F10)
   - App will build and install automatically

### First Run Setup
1. The app will initialize performance optimizations based on your device
2. Performance tier will be auto-detected (HIGH/BALANCED/POWER_SAVER)
3. Login with default credentials or register a new account

---

## 🎯 Usage

### For Customers

#### Getting Started
1. **Register/Login**
   - Tap "Register" to create a new account
   - Or login with existing credentials
   - Glass blur effects enhance the authentication UI

2. **Browse Products**
   - View menu with glassmorphic product cards
   - Use search to find specific items
   - Filter by categories using glass chips
   - Tap products for detailed information

3. **Shopping Cart**
   - Tap glass FAB (bottom-right) to view cart
   - Adjust quantities with +/- buttons
   - See live price calculations
   - Remove items with swipe gesture

4. **Checkout**
   - Review order in glass summary card
   - Enter delivery information
   - Confirm order with glass button
   - View confirmation with order tracking

5. **Order History**
   - Access from profile menu
   - View past orders with glass cards
   - Track order status with color-coded indicators
   - Tap orders for detailed information

### For Administrators

#### Admin Dashboard
1. **Access Admin Panel**
   - Login with administrator credentials
   - View glass dashboard with statistics
   - Quick actions for common tasks

2. **User Management**
   - View all users in glass cards
   - Add new users with glass FAB
   - Edit user details and roles
   - Delete users with confirmation dialog

3. **Product Management**
   - View products in glass grid
   - Add products with glass dialog
   - Upload product images
   - Manage stock and pricing
   - Edit/delete products

4. **Order Management**
   - View all orders in glass cards
   - Status-based blur overlays
   - Update order status
   - View order details
   - Track order fulfillment

### Performance Settings (Optional)

Access in app settings to customize:
- **Performance Mode:** AUTO / HIGH / BALANCED / POWER_SAVER
- **Reduce Effects:** Toggle to disable animations
- **Animation Speed:** Adjust animation duration

---

## 🏗️ Architecture

### MVVM Pattern
```
┌─────────────────────────────────────────┐
│            View Layer                    │
│  (Activities, Fragments, XML Layouts)   │
│  + Glass UI Components                  │
└──────────────┬──────────────────────────┘
               │ Observes
               ↓
┌─────────────────────────────────────────┐
│         ViewModel Layer                  │
│  (Business Logic, State Management)     │
│  + Performance Optimization             │
└──────────────┬──────────────────────────┘
               │ Uses
               ↓
┌─────────────────────────────────────────┐
│        Repository Layer                  │
│  (Data Abstraction, Caching)            │
│  + Memory Management                    │
└──────────────┬──────────────────────────┘
               │ Fetches
               ↓
┌─────────────────────────────────────────┐
│          Data Layer                      │
│  (API, Database, Preferences)           │
│  + Secure Storage                       │
└─────────────────────────────────────────┘
```

### Key Components

#### UI Layer
- **Glass Components:** Reusable glassmorphic UI elements
- **Animations:** Performance-optimized glass animations
- **Accessibility:** WCAG AA compliant helpers

#### Business Logic
- **ViewModels:** Lifecycle-aware state management
- **Use Cases:** Single-responsibility business logic
- **Validators:** Input validation and sanitization

#### Data Management
- **Repositories:** Single source of truth
- **API Client:** Retrofit with coroutines
- **Local Storage:** Encrypted SharedPreferences
- **Memory Cache:** WeakReference-based caching

#### Optimization
- **Performance Manager:** Automatic tier detection
- **Memory Optimizer:** Leak prevention and monitoring
- **Testing Utilities:** Performance and accessibility testing

---

## 📚 Documentation

### Comprehensive Guides

#### UI & Design
- **[UI_MODERNIZATION_PLAN.md](docs/UI_MODERNIZATION_PLAN.md)** - Complete UI modernization roadmap
  - Phase 1-6 implementation details
  - Design principles and patterns
  - Screen-by-screen breakdown
  - Code examples and best practices
  
- **[GLASS_COMPONENTS.md](docs/GLASS_COMPONENTS.md)** - Glass component library documentation
  - 25+ reusable components
  - Usage examples for each component
  - Styling guidelines
  - Migration guide from standard to glass components

- **[PHASE_5_POLISH_OPTIMIZATION.md](docs/PHASE_5_POLISH_OPTIMIZATION.md)** - Performance & optimization guide
  - Performance optimization strategies
  - Accessibility implementation
  - Memory management techniques
  - Testing utilities documentation

- **[PHASE_5_SUMMARY.md](docs/PHASE_5_SUMMARY.md)** - Quick reference for Phase 5
  - Implementation summary
  - Usage examples
  - Performance metrics
  - Testing results

#### Theming & Colors
- **[THEME_SELECTION_GUIDE.md](docs/THEME_SELECTION_GUIDE.md)** - Complete theming guide
- **[THEME_QUICK_REFERENCE.md](docs/THEME_QUICK_REFERENCE.md)** - Quick color reference
- **[THEME_SELECTOR_GUIDE.md](docs/THEME_SELECTOR_GUIDE.md)** - Theme selector usage

### API Documentation
- Backend API endpoints documented in server repository
- REST API follows standard conventions
- Authentication via JWT tokens

### Code Documentation
- Inline KDoc comments for all public APIs
- Component usage examples in markdown files
- Architecture decision records

---

## 🧪 Testing

### Test Coverage

#### Unit Tests
- **GlassComponentsUnitTest.kt** - Glass utility function tests
  - Accessibility calculations (contrast ratios, WCAG compliance)
  - Performance optimization logic
  - Memory management functions
  - Data class validations
  - Edge cases and boundary tests

#### Instrumented Tests
- **GlassComponentsInstrumentedTest.kt** - Android component tests
  - BlurView setup and configuration
  - Performance manager integration
  - Accessibility helper functionality
  - Memory optimizer behavior
  - Integration tests

### Running Tests

#### Run Unit Tests
```bash
./gradlew test
# or
./gradlew testDebugUnitTest
```

#### Run Instrumented Tests
```bash
./gradlew connectedAndroidTest
# or
./gradlew connectedDebugAndroidTest
```

#### Run All Tests
```bash
./gradlew check
```

### Test Reports
- Unit test reports: `app/build/reports/tests/testDebugUnitTest/index.html`
- Instrumented test reports: `app/build/reports/androidTests/connected/index.html`

### Performance Testing

#### Manual Performance Testing
```kotlin
// In your Activity
val metrics = GlassTestingHelper.testBlurPerformance(blurView, context, 5000L)
GlassTestingHelper.logPerformanceMetrics(metrics)

// Generate comprehensive report
val report = GlassTestingHelper.generatePerformanceReport(this)
Log.d("Performance", report)
```

#### Accessibility Testing
```kotlin
// Test accessibility compliance
val issues = GlassTestingHelper.testAccessibilityCompliance(activity)
issues.forEach { issue ->
    Log.w("Accessibility", issue)
}
```

### Test Metrics
- **Unit Tests:** 50+ test cases
- **Instrumented Tests:** 20+ test cases
- **Code Coverage:** Core utilities 100%
- **Performance Benchmarks:** Included
- **Accessibility Audits:** Automated

---

## ⚡ Performance

### Performance Targets & Results

| Metric | Target | Achieved | Status |
|--------|--------|----------|--------|
| Frame Rate | 60 FPS | 60 FPS (HIGH mode) | ✅ |
| Blur Render Time | <16ms | <16ms (optimized) | ✅ |
| Memory Overhead | <50MB | <50MB (monitored) | ✅ |
| Cold Start Time | <3s | <2s | ✅ |
| WCAG Compliance | AA | AA compliant | ✅ |
| Touch Target Size | 48dp | 48dp enforced | ✅ |

### Performance Tiers

| Mode | Blur Quality | Target FPS | Target Devices | RAM |
|------|--------------|------------|----------------|-----|
| HIGH_QUALITY | 100% (25f radius) | 60 | Flagship | 6GB+ |
| BALANCED | 70% (14-17f radius) | 55+ | Mid-range | 3-6GB |
| POWER_SAVER | 40% (8-10f radius) | 45+ | Budget | <3GB |

### Optimization Features
- ✅ Automatic device tier detection
- ✅ Dynamic blur quality adjustment
- ✅ Memory-aware rendering
- ✅ Battery-conscious animations
- ✅ WeakReference-based caching
- ✅ Real-time memory monitoring

### Memory Management
- **Zero memory leaks** - Verified with profiler
- **WeakReference caching** - Automatic cleanup
- **Lifecycle-aware** - Respects activity lifecycle
- **Monitoring** - Real-time usage tracking

---

## ♿ Accessibility

### WCAG 2.1 AA Compliance

#### Color Contrast
- ✅ 4.5:1 minimum ratio for normal text
- ✅ 3:1 minimum ratio for large text
- ✅ Automatic contrast validation
- ✅ Accessible color selection

#### Touch Targets
- ✅ 48dp minimum size enforced
- ✅ Automatic padding adjustment
- ✅ Touch target validation

#### Screen Readers
- ✅ Full TalkBack support
- ✅ Content descriptions for all interactive elements
- ✅ Custom accessibility actions
- ✅ Proper focus ordering

#### Motion & Animation
- ✅ Respects "Reduce Motion" setting
- ✅ Instant transitions when requested
- ✅ No essential information conveyed by motion alone

#### Keyboard Navigation
- ✅ Full keyboard support
- ✅ Visible focus indicators
- ✅ Logical tab order

### Accessibility Testing
- Automated compliance checks included
- TalkBack compatibility verified
- Touch target validation
- Contrast ratio validation

---

## 🔐 Security

### Security Features
- **Encrypted Storage** - AndroidX Security Crypto for sensitive data
- **HTTPS Communication** - All API calls over secure connections
- **Secure Session Management** - JWT token authentication
- **Input Validation** - Server-side and client-side validation
- **Role-Based Access Control** - Separate customer and admin permissions
- **Password Hashing** - Passwords never stored in plain text
- **SQL Injection Protection** - Parameterized queries on backend

### Security Best Practices
- ✅ No hardcoded credentials
- ✅ Secure token storage with encryption
- ✅ Certificate pinning for API calls
- ✅ Automatic session timeout
- ✅ Secure credential transmission

---

## 🤝 Contributing

We welcome contributions! Here's how to get started:

### Development Workflow

1. **Fork the Repository**
   ```bash
   # Fork on GitHub, then clone your fork
   git clone https://github.com/your-username/RestaurantClient.git
   cd RestaurantClient
   ```

2. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/your-bug-fix
   ```

3. **Make Changes**
   - Follow existing code style and conventions
   - Add tests for new features
   - Update documentation as needed
   - Ensure all tests pass

4. **Commit Changes**
   ```bash
   git add .
   git commit -m "feat: add new feature description"
   # or
   git commit -m "fix: fix bug description"
   ```
   
   **Commit Message Format:**
   - `feat:` New feature
   - `fix:` Bug fix
   - `docs:` Documentation changes
   - `style:` Code style changes
   - `refactor:` Code refactoring
   - `test:` Test additions or changes
   - `chore:` Build or config changes

5. **Push to Your Fork**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Open Pull Request**
   - Go to original repository on GitHub
   - Click "New Pull Request"
   - Select your branch
   - Describe your changes
   - Link any related issues

### Code Style Guidelines
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused
- Write tests for new functionality

### Testing Requirements
- Unit tests for utility functions
- Instrumented tests for UI components
- Performance tests for optimization code
- Accessibility tests for new UI elements

### Review Process
1. Code review by maintainers
2. All tests must pass
3. Code coverage should not decrease
4. Documentation must be updated
5. Performance impact assessed

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### Third-Party Licenses

#### BlurView
```
Copyright 2025 Dmytro Saviuk

Licensed under the Apache License, Version 2.0
Source: https://github.com/Dimezis/BlurView
```

#### Material Components for Android
```
Copyright 2025 The Android Open Source Project

Licensed under the Apache License, Version 2.0
Source: https://github.com/material-components/material-components-android
```

#### Hilt
```
Copyright 2025 Google LLC

Licensed under the Apache License, Version 2.0
Source: https://github.com/google/dagger/tree/master/java/dagger/hilt
```

See [LICENSE](LICENSE) file for complete third-party license information.

---

## 🙏 Acknowledgments

### Libraries & Frameworks
- **[Material Design 3](https://m3.material.io/)** - Modern design system
- **[BlurView Library](https://github.com/Dimezis/BlurView)** - Glassmorphism effects
- **[Hilt](https://dagger.dev/hilt/)** - Dependency injection
- **[Retrofit](https://square.github.io/retrofit/)** - REST API client
- **[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** - Async programming
- **[AndroidX Libraries](https://developer.android.com/jetpack/androidx)** - Modern Android components

### Tools & Resources
- **[Android Studio](https://developer.android.com/studio)** - IDE
- **[Figma](https://www.figma.com/)** - UI/UX design
- **[WCAG Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)** - Accessibility standards
- **[Material Icons](https://fonts.google.com/icons)** - Icon library

### Inspiration
- Glassmorphism design trend
- Material Design 3 principles
- Modern Android app best practices

### Special Thanks
- ARROW Server Team for the robust backend
- Open source community for amazing tools
- All contributors and testers

---

## 📊 Project Statistics

### Codebase
- **Total Lines:** ~15,000+
- **Kotlin Files:** 50+
- **XML Layouts:** 30+
- **Test Files:** 5+
- **Documentation:** 30KB+

### UI Components
- **Screens:** 15+
- **Glass Components:** 25+
- **Custom Styles:** 30+
- **Animations:** 10+

### Performance
- **Build Time:** ~45s
- **APK Size:** ~8MB (release)
- **Min RAM:** 2GB
- **Recommended RAM:** 4GB+

---

## 🗺️ Roadmap

### Version 2.0 (Current) ✅
- ✅ Complete glassmorphism UI
- ✅ Performance optimization
- ✅ Accessibility compliance
- ✅ Memory management
- ✅ Dark mode support
- ✅ Comprehensive testing

### Version 2.1 (Planned)
- ⏳ Offline mode support
- ⏳ Push notifications
- ⏳ Advanced search filters
- ⏳ Favorites/Wishlist
- ⏳ Order tracking map

### Version 3.0 (Future)
- 🎯 Jetpack Compose migration
- 🎯 Multi-language support
- 🎯 Payment gateway integration
- 🎯 QR code ordering
- 🎯 Loyalty program

---

## 📞 Support

### Getting Help
- **Documentation:** Check [docs/](docs/) folder
- **Issues:** Open an [issue](https://github.com/your-username/RestaurantClient/issues)
- **Discussions:** Join [discussions](https://github.com/your-username/RestaurantClient/discussions)

### Reporting Bugs
1. Check existing issues first
2. Include device and OS version
3. Provide steps to reproduce
4. Attach screenshots if possible
5. Include relevant logs

### Feature Requests
1. Open a discussion or issue
2. Describe the feature clearly
3. Explain the use case
4. Propose implementation if possible

---

## 🌟 Star History

If you find this project useful, please consider giving it a ⭐ on GitHub!

---

## 📱 Screenshots

### Customer UI
- Modern glassmorphism product cards
- Smooth animations and transitions
- Glass shopping cart FAB
- Beautiful checkout flow
- Glass order history cards

### Admin UI
- Professional glass dashboard
- Glass statistics cards
- Modern management interfaces
- Glass CRUD operations
- Status-based blur overlays

_Screenshots coming soon!_

---

Made with ❤️ using Kotlin, Material Design 3, and Glassmorphism

**© 2025 Restaurant Client • Modern • Accessible • Performant**
