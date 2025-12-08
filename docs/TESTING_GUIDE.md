# Orderly Android App - Testing Guide

## 📋 Pre-Testing Requirements

### **Backend Status Check**
Before testing the app, verify your Arrow Server is running:
- **URL**: `https://arrow-server-v1.nicerock-8289607a.southeastasia.azurecontainerapps.io/`
- **Test in browser**: Visit the URL - should return a basic response (not 404)
- **API Health Check**: `GET /api` endpoint should be accessible

### **Device Requirements**
- Android device/emulator with **API level 33+** (Android 13+)
- **Internet connection** for API calls
- **Android Studio** for debugging (recommended)

---

## 🔐 Step 1: Account Creation & Authentication

### **A. First-Time Setup (Database Recently Reset)**

1. **Install and Launch App**
   ```
   - Install APK on device/emulator
   - Launch "Orderly" app
   - Should automatically redirect to Login screen
   ```

2. **Create First Admin Account** ⚠️ **CRITICAL: First user becomes admin**
   ```
   - Since database was reset, first registration = admin
   - Enter admin credentials in the login form:
     Username: [admin]
     Password: [meowmeow25]
   - Tap "Register" button (below Login button)
   - Wait for processing (progress bar will show)
   ```
   
   **Expected Result**: ✅
   - Registration successful message
   - Automatic assignment of ADMIN role
   - Automatic redirect to Product List screen
   - **IMPORTANT**: Public registration is now DISABLED
   - Only this admin can create new users

3. **Verify Admin Status**
   ```
   - Check if admin menu/features are visible
   - Navigate to Profile/Settings
   - Should see admin-specific options
   - Test admin dashboard access (if implemented)
   ```

4. **Test Admin Login Flow**
   ```
   - Force close app
   - Reopen app
   - Should automatically log in (token saved)
   - OR manually logout and login again with admin credentials
   ```

### **B. Admin User Management Testing**

1. **Create Additional Users** (Admin Only)
   ```
   - Navigate to Admin Dashboard
   - Tap "Add New User" or similar
   - Fill user creation form:
     Username: testuser1
     Password: testpass123
     Role: USER (or ADMIN for additional admin)
   - Tap "Create User"
   ```
   
   **Expected Result**: ✅
   - User creation successful
   - New user appears in user management list
   - New user can now login

2. **Test Regular User Login**
   ```
   - Logout from admin account
   - Login with created user credentials
   - Verify regular user experience (no admin features)
   ```

### **C. If Admin User Already Exists**

1. **Get Admin Credentials**
   ```
   - Use the first admin account created
   - Or contact whoever set up the first admin
   ```

2. **Admin Login Test**
   ```
   - Launch app
   - Enter admin username/password
   - Tap "Login"
   - Verify admin dashboard access
   ```

### **D. Authentication Error Testing**
```
Test Case 1: Wrong credentials
- Enter: username="wrong", password="wrong"
- Expected: Error message "Invalid credentials"

Test Case 2: Empty fields
- Leave fields blank, tap Login
- Expected: Validation error messages

Test Case 3: Network error
- Turn off internet, try login
- Expected: Network error message

Test Case 4: Public registration attempt (after first user)
- Try to register a new account without admin
- Expected: Registration should be blocked/disabled

Test Case 5: Unauthorized admin access
- Login as regular user
- Try to access admin features
- Expected: Access denied or features not visible
```

---

## 🛒 Step 2: Product Browsing

### **A. Product List Screen**
```
1. After login, verify Product List loads
2. Check elements:
   ✓ Product names display
   ✓ Product prices display (format: $X.XX)
   ✓ Product images load (if available)
   ✓ "My Orders" button visible
   ✓ Profile/Settings button visible
```

### **B. Product Details**
```
1. Tap any product from list
2. Verify Product Detail screen:
   ✓ Product name and description
   ✓ Price displayed correctly
   ✓ Product image (if available)
   ✓ "Add to Cart" button functional
   ✓ Quantity selector (if implemented)
```

### **C. Product Interaction Test**
```
Test Case 1: Add to Cart
- Tap "Add to Cart" on product detail
- Expected: Success message or cart update

Test Case 2: Multiple Products
- Add several different products
- Verify each addition works

Test Case 3: Empty State
- If no products exist on backend
- Expected: "No products available" message
```

---

## 🛍️ Step 3: Shopping Cart & Orders

### **A. Cart Functionality** (If implemented)
```
1. Navigate to Shopping Cart
2. Verify cart contents:
   ✓ Added products appear
   ✓ Quantities correct
   ✓ Prices calculate correctly
   ✓ Total amount accurate
   ✓ Remove/update quantity works
```

### **B. Order Creation**
```
1. From cart or product detail, place order
2. Test order flow:
   ✓ Order confirmation screen
   ✓ Order ID generated
   ✓ Success message displayed
   ✓ Redirect to order history OR product list
```

### **C. Order History**
```
1. Tap "My Orders" button
2. Verify order list:
   ✓ Orders display with correct info
   ✓ Order IDs, dates, status shown
   ✓ Order details accessible
   ✓ Most recent orders first
```

---

## 👤 Step 4: User Profile & Admin Management

### **A. Profile Screen (All Users)**
```
1. Navigate to Profile/Settings
2. Test functionality:
   ✓ User information displayed
   ✓ User role displayed (ADMIN/USER)
   ✓ Logout button present
   ✓ App settings (if any)
```

### **B. Admin Dashboard Testing** (Admin Only)
```
1. Login as admin user
2. Navigate to Admin Dashboard
3. Test admin features:
   ✓ User management interface
   ✓ Create new user form
   ✓ View all users list
   ✓ Edit user roles (if implemented)
   ✓ Delete users (if implemented)
```

### **C. User Management Testing** (Admin Only)
```
1. Create multiple test users:
   - testuser1 (USER role)
   - testuser2 (USER role)
   - testadmin2 (ADMIN role, if allowed)

2. Verify user list displays:
   ✓ All created users shown
   ✓ Usernames and roles visible
   ✓ Creation dates (if displayed)

3. Test role management:
   ✓ Assign different roles
   ✓ Update existing user roles
   ✓ Verify role changes take effect
```

### **D. Logout Flow**
```
1. Tap "Logout" button
2. Verify logout process:
   ✓ Confirmation dialog (if implemented)
   ✓ Redirect to login screen
   ✓ Token cleared (can't access protected screens)
   ✓ Role-based features cleared
   ✓ Login required to re-enter app
```

---

## 🔄 Step 5: Token Management Testing

### **A. Token Refresh**
```
1. Use app normally for 50+ minutes
2. Verify automatic token refresh:
   ✓ No sudden logouts
   ✓ API calls continue working
   ✓ No user intervention needed
```

### **B. Token Expiration**
```
1. Wait for token to expire (60 minutes)
2. OR manually clear app data
3. Verify behavior:
   ✓ Automatic redirect to login
   ✓ Previous session cleared
   ✓ No crash or freeze
```

---

## 🐛 Step 6: Error Handling Testing

### **A. Network Error Tests**
```
1. Turn off internet during various operations
2. Test scenarios:
   ✓ Login with no internet
   ✓ Loading products with no internet
   ✓ Placing order with no internet
   ✓ Appropriate error messages shown
```

### **B. Backend Error Tests**
```
1. Invalid API responses (if possible to simulate)
2. Server downtime
3. Malformed data
4. Verify app doesn't crash
```

---

## 📊 Step 7: Performance Testing

### **A. App Responsiveness**
```
✓ App launches within 3 seconds
✓ Screen transitions are smooth
✓ No UI freezing during API calls
✓ Loading indicators shown during waits
```

### **B. Memory Usage**
```
✓ No memory leaks during extended use
✓ App doesn't slow down after multiple operations
✓ Background/foreground transitions work
```

---

## 🔍 Debugging & Troubleshooting

### **Android Studio Debugging**
1. **Connect device to Android Studio**
2. **Open Logcat** to view logs
3. **Filter by "Orderly"** to see app-specific logs
4. **Look for these log patterns:**
   ```
   - HTTP requests/responses (OkHttp logs)
   - Authentication success/failure
   - Token refresh operations
   - API response parsing
   ```

### **Common Issues & Solutions**

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| Can't register | Admin user already exists | Use admin login, then create user via admin dashboard |
| Login fails | Wrong credentials | Check with backend admin or first admin user |
| No admin features | Not logged in as admin | Ensure using admin account, not regular user |
| Registration blocked | Public registration disabled | Only admin can create new users |
| No products show | Empty backend database | Add products via admin interface |
| Network errors | Backend down | Check Azure server status |
| App crashes on launch | Dependency issue | Check Logcat for stack traces |
| Can't create users | Not admin role | Must be logged in as admin user |

### **API Testing with cURL**
Test backend directly before testing app:

```bash
# Test admin login endpoint
curl -X POST https://arrow-server-v1.nicerock-8289607a.southeastasia.azurecontainerapps.io/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"your_admin_username","password":"your_admin_password"}'

# Test products endpoint (with token)
curl -X GET https://arrow-server-v1.nicerock-8289607a.southeastasia.azurecontainerapps.io/api/v1/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"

# Test admin user creation (admin only)
curl -X POST https://arrow-server-v1.nicerock-8289607a.southeastasia.azurecontainerapps.io/api/v1/admin/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -d '{"username":"newuser","password":"newpass123","role":"USER"}'

# Test user list (admin only)  
curl -X GET https://arrow-server-v1.nicerock-8289607a.southeastasia.azurecontainerapps.io/api/v1/admin/users \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN"
```

---

## ✅ Testing Checklist

### **Core Functionality**
- [ ] App launches without crashes
- [ ] First user becomes admin automatically
- [ ] Admin login works with backend
- [ ] Admin can create new users
- [ ] Regular user login works
- [ ] Products load from API
- [ ] Product details display correctly
- [ ] Add to cart functionality works
- [ ] Order creation successful
- [ ] Order history displays
- [ ] Logout clears session properly

### **Security**
- [ ] Tokens stored securely
- [ ] Automatic token refresh works
- [ ] Invalid tokens handled properly
- [ ] HTTPS communication only
- [ ] Role-based access controls work
- [ ] Admin features blocked for regular users
- [ ] Public registration properly disabled

### **User Experience**
- [ ] Intuitive navigation
- [ ] Appropriate loading indicators
- [ ] Error messages are helpful
- [ ] No unexpected crashes
- [ ] Smooth animations/transitions

### **Edge Cases**
- [ ] Empty product list handled
- [ ] Network errors handled gracefully
- [ ] Invalid input validation
- [ ] Large order quantities work
- [ ] Rapid successive API calls handled

---

## 📝 Test Results Template

```
## Test Session: [Date/Time]
**Tester**: [Your Name]
**Device**: [Device Model/Emulator]
**Android Version**: [API Level]
**App Version**: [Build/Version]

### Authentication & User Management
- [ ] First user admin registration: ✅ Pass / ❌ Fail / ⏭️ Skip
- [ ] Admin login: ✅ Pass / ❌ Fail
- [ ] Admin creates new users: ✅ Pass / ❌ Fail / ⏭️ Skip
- [ ] Regular user login: ✅ Pass / ❌ Fail / ⏭️ Skip
- [ ] Role-based access control: ✅ Pass / ❌ Fail
- [ ] Public registration blocked: ✅ Pass / ❌ Fail
- [ ] Logout: ✅ Pass / ❌ Fail

### Product Management
- [ ] Product List: ✅ Pass / ❌ Fail
- [ ] Product Details: ✅ Pass / ❌ Fail
- [ ] Add to Cart: ✅ Pass / ❌ Fail

### Order Management
- [ ] Create Order: ✅ Pass / ❌ Fail
- [ ] Order History: ✅ Pass / ❌ Fail

### Issues Found
1. [Description of any issues]
2. [Steps to reproduce]
3. [Expected vs actual behavior]

### Overall Assessment
- App Stability: [Excellent/Good/Poor]
- Performance: [Fast/Acceptable/Slow]
- User Experience: [Excellent/Good/Needs Improvement]
```

---

## 🚀 Ready for Production?

Before deploying to users, ensure:
- [ ] All test cases pass
- [ ] No critical bugs found  
- [ ] Performance is acceptable
- [ ] Security measures verified
- [ ] User experience is smooth
- [ ] Backend is stable and scalable

**Happy Testing! 🎉**