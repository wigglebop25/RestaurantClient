# Admin Role Detection Debug Guide

## 🚨 **Current Issue**
- Database shows role: "admin" 
- App detects role: "customer"
- Expected: Admin user should go to AdminDashboardActivity
- Actual: User goes to ProductListActivity

---

## 🔧 **Debug Steps Implemented**

### **1. Enhanced Logging System**
Added comprehensive logging at every level:

**AuthRepository Level:**
```
🔐 Attempting login for: [username]
📡 Login response code: [code]  
📦 Raw response body: [full response]
👤 User: [user object]
🔍 User details: ID, Username, Role, Created
```

**AuthViewModel Level:**
```  
✅ Backend returned user info!
Raw user object: [full object]
Role (raw): [role value]
✅ SAVED ROLE: [role] for user: [username]
```

**TokenManager Level:**
```
Saving user role: [role]
Normalized role: [normalized]
Retrieved role: [stored role]
```

**MainActivity Level:**
```
🔍 Checking stored role: [role]
🎯 Role found: [role] 
⚠️ No role found, using fallback detection
```

### **2. Case-Insensitive Role Handling**
Fixed potential case-sensitivity issues:

**RoleDTO Helper:**
```kotlin
companion object {
    fun fromString(roleString: String?): RoleDTO? {
        return when (roleString?.lowercase()?.trim()) {
            "admin" -> Admin
            "customer" -> Customer  
            "user" -> Customer // Fallback
            else -> null
        }
    }
}
```

**TokenManager Normalization:**
```kotlin
fun saveUserRole(role: String) {
    val normalizedRole = when (role.lowercase()) {
        "admin" -> "Admin"
        "customer" -> "Customer"  
        else -> role
    }
    // Store normalized version
}

fun getUserRole(): RoleDTO? {
    return when (roleString?.lowercase()) {
        "admin" -> RoleDTO.Admin
        "customer" -> RoleDTO.Customer
        else -> null
    }
}
```

### **3. Improved Role Detection Flow**
Enhanced the role determination logic:

```kotlin
// 1. Check if backend returns user info
if (result.data.user != null) {
    // Use backend role directly
    currentUser = result.data.user
    tokenManager.saveUserRole(result.data.user.role)
} else {
    // Fallback: Test admin endpoint access
    determineUserRole(loginDto.username)
}
```

---

## 🔍 **Debugging Your Issue**

### **Step 1: Check Login Logs**
Look for these log entries during login:

```
AuthRepository: 📦 Raw response body: [JSON]
AuthRepository: 👤 User: [user object] 
AuthRepository: 🔍 User details: Role: [role value]
```

**Questions:**
- Does the backend return user info?
- What is the exact role value returned?
- Is it "admin", "Admin", "ADMIN", or something else?

### **Step 2: Check Role Processing**  
Look for these AuthViewModel logs:

```
AuthViewModel: ✅ Backend returned user info!
AuthViewModel: Role (raw): [role value]
AuthViewModel: ✅ SAVED ROLE: [role] for user: [username]
```

**Questions:**
- Is the role being processed correctly?
- What value is being saved?

### **Step 3: Check Role Retrieval**
Look for MainActivity logs:

```
MainActivity: 🔍 Checking stored role: [role]
MainActivity: 🎯 Role found: [role]
```

**Questions:**
- What role is retrieved from storage?
- Is it matching the expected enum values?

---

## 🔬 **Possible Root Causes**

### **1. Backend Response Format**
**Problem:** Backend returns `{token, message}` without user info
**Evidence:** Log shows "Backend didn't return user info"
**Fix:** Backend needs to include user object in login response

### **2. Role Field Mismatch** 
**Problem:** Backend uses different role field name
**Evidence:** User object exists but role is null
**Fix:** Check if backend uses "role", "userRole", "authority", etc.

### **3. Case Sensitivity**
**Problem:** Database has "admin" but enum expects "Admin"
**Evidence:** Role shows as lowercase in logs
**Fix:** Already implemented normalization

### **4. JSON Deserialization**
**Problem:** Gson cannot map role string to enum
**Evidence:** Role shows as null despite being in JSON
**Fix:** Need custom deserializer or different approach

### **5. Async Timing Issue**
**Problem:** Role determination happens async, navigation happens sync
**Evidence:** Role is null initially but appears later
**Fix:** Wait for async role determination

---

## 📋 **Debug Checklist**

Run the app with your admin credentials and check:

- [ ] **Login Response**: Does backend return user object?
- [ ] **Role Value**: What exact string is returned for role?
- [ ] **Case Match**: Is role "admin", "Admin", or other case?
- [ ] **Storage**: Is role saved correctly to SharedPreferences?
- [ ] **Retrieval**: Is role retrieved correctly from storage?
- [ ] **Navigation**: Does `navigateBasedOnRole()` get called?
- [ ] **Fallback**: Does fallback detection trigger?

---

## 🛠️ **Quick Fixes to Try**

### **1. Force Admin Role (Testing)**
Temporarily override role detection:
```kotlin
// In MainActivity.determineUserRoleAndNavigate()
Log.d("MainActivity", "🧪 FORCING ADMIN ROLE FOR TESTING")
authViewModel.setUserRole(RoleDTO.Admin)
navigateBasedOnRole()
```

### **2. Check Raw Backend Response**
Test the backend directly:
```bash
curl -X POST [your_backend]/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"your_admin_username","password":"your_password"}'
```

### **3. Clear App Data**
Clear stored preferences and try fresh login:
```kotlin
// In AuthViewModel or TokenManager
tokenManager.clearAll() // Clear all stored data
```

---

## 📱 **Testing Steps**

1. **Clear app data** (Settings → Apps → Orderly → Storage → Clear Data)
2. **Open app** (should go to login)
3. **Enable logcat** filtering for "AuthRepository", "AuthViewModel", "MainActivity"
4. **Login with admin credentials**
5. **Check logs** for the patterns above
6. **Report findings**: What do you see in the logs?

---

## 🎯 **Expected vs Actual Flow**

**Expected for Admin:**
```
Login → Backend returns role:"admin" → Saved as Admin → Navigate to AdminDashboard
```

**Current Issue:**
```  
Login → ??? → Role becomes Customer → Navigate to ProductList
```

**Let's find where the ??? breaks down!** 🔍

The enhanced logging will help us pinpoint exactly where the role detection is failing.