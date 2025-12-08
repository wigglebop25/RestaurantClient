# JWT Role Extraction & Admin Endpoint Fix

## 🎯 **Issues Identified & Fixed**

### **Issue 1: Backend Doesn't Return User Info in Login Response**
**Problem**: Your backend only returns `{token, message}` without user details.
**Solution**: ✅ Extract role directly from JWT token.

### **Issue 2: Wrong Admin Endpoint URL**
**Problem**: App was calling `/api/v1/admin/users` → **404 Not Found**
**Solution**: ✅ Fixed to use correct URL `/api/v1/users`

---

## ✅ **JWT Role Extraction Implementation**

Your JWT token contains role information in the `roles` array:

```json
{
  "sub": 2,
  "iat": 1765177917,
  "exp": 1765181517,
  "roles": [2]  ← Admin role ID
}
```

**Added JWT Role Extraction in TokenManager:**

```kotlin
if (payload.has("roles")) {
    val rolesArray = payload.getJSONArray("roles")
    Log.d("TokenManager", "Found roles array in JWT: $rolesArray")
    
    if (rolesArray.length() > 0) {
        val roleId = rolesArray.getInt(0) // Get first role ID
        Log.d("TokenManager", "First role ID: $roleId")
        
        // Convert role ID to role name based on your system
        val roleName = when (roleId) {
            2 -> "Admin"     // Your admin role ID is 2
            1 -> "Customer"  // Assuming customer role ID is 1
            else -> "Customer" // Default to customer
        }
        Log.d("TokenManager", "Converted role ID $roleId to: $roleName")
        saveUserRole(roleName)
    }
}
```

---

## 🔧 **Admin Endpoint URL Fix**

**Updated ApiService endpoints from:**
```kotlin
@GET("api/v1/admin/users")  // ❌ 404 Not Found
```

**To:**
```kotlin
@GET("api/v1/users")        // ✅ Correct endpoint
```

**All admin endpoints fixed:**
- `GET api/v1/users` - Get all users
- `POST api/v1/users` - Create user
- `PUT api/v1/users/{id}` - Update user  
- `DELETE api/v1/users/{id}` - Delete user

---

## 🔄 **Complete Flow Now**

### **Primary Method - JWT Role Extraction:**
```
Login → JWT Token: {"roles":[2]} → Extract roleId=2 → Convert to "Admin" → AdminDashboard ✅
```

### **Fallback Method - Admin Endpoint Test:**
```
Login → No JWT role → Test GET /api/v1/users → Success → Admin role → AdminDashboard ✅
```

### **Default Method - Username Pattern:**
```
Login → No JWT role → Endpoint fails → Check username="admin" → Admin role → AdminDashboard ✅
```

---

## 📱 **Expected Logs After Fix**

When you login with admin credentials, you should see:

**JWT Role Extraction (Primary):**
```
TokenManager: JWT payload: {"sub":2,"iat":1765177917,"exp":1765181517,"roles":[2]}
TokenManager: Found roles array in JWT: [2]
TokenManager: First role ID: 2
TokenManager: Converted role ID 2 to: Admin
AuthViewModel: ✅ SAVED ROLE: Admin for user: admin
```

**OR Admin Endpoint Test (Fallback):**
```
AuthViewModel: 🔍 Testing admin access for user: admin
AuthViewModel: Making request to admin/users endpoint...
GET /api/v1/users → 200 Success
AuthViewModel: 🎯 DETERMINED ROLE for admin: Admin (admin access: true)
```

**Final Navigation:**
```
MainActivity: 🔍 Checking stored role: Admin
MainActivity: ✅ Found stored role: Admin
MainActivity: User is admin, navigating to AdminDashboard
```

---

## 🚀 **Benefits of This Fix**

1. **✅ Primary Role Source**: JWT token (most reliable)
2. **✅ Fallback Detection**: Admin endpoint testing (if JWT doesn't have role)
3. **✅ Emergency Fallback**: Username patterns (if endpoint fails)
4. **✅ Correct API URLs**: Fixed all admin endpoints
5. **✅ Multiple Redundancy**: Three ways to determine admin status

---

## 🧪 **Testing Instructions**

1. **Clear app data** (Settings → Apps → Orderly → Clear Data)
2. **Login with admin credentials** (username: "admin")
3. **Expected result**: Direct navigation to AdminDashboardActivity
4. **Check logs** for role extraction process

### **Test Scenarios:**

**Admin User:**
- JWT has `"roles":[2]` → Extracted as Admin → AdminDashboard ✅

**Customer User:**
- JWT has `"roles":[1]` or no roles → Customer → ProductList ✅

**Legacy User (no JWT roles):**
- Falls back to endpoint testing → Determined by `/api/v1/users` access ✅

---

## 🎯 **Result**

**Before (BROKEN):**
```
Admin login → JWT ignored → Wrong endpoint (404) → Customer role → ProductList ❌
```

**After (FIXED):**
```
Admin login → JWT role extracted → Admin role → AdminDashboard ✅
            OR
Admin login → Correct endpoint (200) → Admin role → AdminDashboard ✅
```

**Your admin login should now work perfectly with both the JWT extraction and the corrected admin endpoint! 🎉**

The fix ensures reliable admin detection through multiple methods, making the app robust against different backend response formats.