# Admin-First Registration Plan

## Overview
With the database reset, the first user to register becomes the admin with special privileges to add new users.

## Implementation Strategy

### 1. Registration Flow Changes
- **First Registration**: Automatically assigns admin role
- **Subsequent Registrations**: Blocked unless initiated by admin
- **Admin Registration**: Admin can create new user accounts with role assignment

### 2. Backend Changes Required

#### Authentication Service Updates
- Modify registration endpoint to check if any users exist
- If no users exist, assign admin role to first registrant
- If users exist, require admin authentication for new registrations

#### New Endpoints Needed
```
POST /api/admin/users - Create new user (admin only)
GET /api/admin/users - List all users (admin only)
PUT /api/admin/users/{id}/role - Update user role (admin only)
DELETE /api/admin/users/{id} - Delete user (admin only)
```

#### Role Management
- Implement role-based access control (RBAC)
- Roles: ADMIN, USER
- Admin permissions: Full access to user management
- User permissions: Standard app functionality only

### 3. Android App Changes

#### UI Components
- **Admin Dashboard**: New screen for user management
- **User Registration Form**: Admin-initiated user creation
- **Role Indicators**: Show user role in profile/settings
- **Admin Menu**: Additional navigation options for admin users

#### Authentication Updates
- Store user role in SharedPreferences/secure storage
- Add role-based navigation logic
- Implement admin-only screens and features

#### New Activities/Fragments
- `AdminDashboardActivity` - Main admin control panel
- `CreateUserFragment` - Form for admin to create new users
- `UserManagementFragment` - List and manage existing users

### 4. Security Considerations
- Validate admin role server-side for all admin operations
- Implement proper session management
- Add audit logging for admin actions
- Secure admin endpoints with proper authentication

### 5. Implementation Steps

#### Phase 1: Backend Foundation
1. Update user model with role field
2. Modify registration endpoint logic
3. Implement admin-only user creation endpoint
4. Add role-based middleware/guards

#### Phase 2: Android Integration
1. Update authentication service to handle roles
2. Modify login flow to store user role
3. Add role-based navigation logic
4. Create admin dashboard UI

#### Phase 3: User Management Features
1. Implement admin user creation form
2. Add user listing and management
3. Implement role assignment functionality
4. Add user deletion capability

#### Phase 4: Testing & Validation
1. Test first-user-admin assignment
2. Verify admin-only access controls
3. Test user creation by admin
4. Validate security measures

### 6. Testing Strategy
- Fresh database testing for first admin registration
- Role-based access testing
- Security testing for unauthorized access attempts
- End-to-end testing of complete user management flow

## Success Criteria
- ✅ First user automatically becomes admin
- ✅ Only admin can create new users
- ✅ Role-based access controls work properly
- ✅ Admin dashboard provides full user management
- ✅ Security measures prevent unauthorized access