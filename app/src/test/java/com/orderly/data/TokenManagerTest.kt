package com.orderly.data

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.orderly.data.dto.RoleDTO
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class TokenManagerTest {

    private lateinit var tokenManager: TokenManager
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        sharedPreferences = context.getSharedPreferences("test_prefs", Context.MODE_PRIVATE)
        tokenManager = TokenManager(sharedPreferences)
    }

    @After
    fun teardown() {
        sharedPreferences.edit().clear().apply()
    }

    // Helper to create a JWT for testing
    private fun createJwt(payloadJson: String): String {
        val header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}"
        val encodedHeader = android.util.Base64.encodeToString(header.toByteArray(), android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING).trimEnd('=')
        val encodedPayload = android.util.Base64.encodeToString(payloadJson.toByteArray(), android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING).trimEnd('=')
        return "$encodedHeader.$encodedPayload.signature"
    }

    @Test
    fun `saveToken defaults to Customer when roles field is null`() {
        val jwtPayload = "{\"sub\":1, \"iat\":12345, \"exp\":22345, \"roles\": null}" // Note: This is a raw string literal, not a JSON string.
        val jwt = createJwt(jwtPayload)
        tokenManager.saveToken(jwt)
        assert(tokenManager.getUserRole() == RoleDTO.Customer)
    }

    @Test
    fun `saveToken defaults to Customer when no role or roles field is present`() {
        val jwtPayload = "{\"sub\":1, \"iat\":12345, \"exp\":22345}" // Note: This is a raw string literal, not a JSON string.
        val jwt = createJwt(jwtPayload)
        tokenManager.saveToken(jwt)
        assert(tokenManager.getUserRole() == RoleDTO.Customer)
    }

    @Test
    fun `saveToken correctly extracts Admin role from roles array`() {
        val jwtPayload = "{\"sub\":1, \"iat\":12345, \"exp\":22345, \"roles\":[2]}" // Admin role ID is 2
        val jwt = createJwt(jwtPayload)
        tokenManager.saveToken(jwt)
        assert(tokenManager.getUserRole() == RoleDTO.Admin)
    }

    @Test
    fun `saveToken correctly extracts Customer role from roles array`() {
        val jwtPayload = "{\"sub\":1, \"iat\":12345, \"exp\":22345, \"roles\":[1]}" // Customer role ID is 1
        val jwt = createJwt(jwtPayload)
        tokenManager.saveToken(jwt)
        assert(tokenManager.getUserRole() == RoleDTO.Customer)
    }

    @Test
    fun `saveToken correctly extracts role from role string field`() {
        val jwtPayload = "{\"sub\":1, \"iat\":12345, \"exp\":22345, \"role\":\"Admin\"}" // Note: This is a raw string literal, not a JSON string.
        val jwt = createJwt(jwtPayload)
        tokenManager.saveToken(jwt)
        assert(tokenManager.getUserRole() == RoleDTO.Admin)
    }

    @Test
    fun `saveToken handles empty roles array and defaults to Customer`() {
        val jwtPayload = "{\"sub\":1, \"iat\":12345, \"exp\":22345, \"roles\":[]}" // Note: This is a raw string literal, not a JSON string.
        val jwt = createJwt(jwtPayload)
        tokenManager.saveToken(jwt)
        assert(tokenManager.getUserRole() == RoleDTO.Customer)
    }
    
    @Test
    fun `getToken returns saved token`() {
        val testToken = "test.token.value"
        tokenManager.saveToken(createJwt("{\"sub\":\"testuser\",\"exp\":${(System.currentTimeMillis() / 1000) + 3600}}"))
        sharedPreferences.edit().putString("auth_token", testToken).apply()
        assert(tokenManager.getToken() == testToken)
    }

    @Test
    fun `deleteToken clears token and username`() {
        tokenManager.saveToken(createJwt("{\"sub\":\"testuser\",\"exp\":${(System.currentTimeMillis() / 1000) + 3600}}"))
        tokenManager.saveUsername("testuser")
        tokenManager.saveUserRole("Admin")
        tokenManager.deleteToken()
        assert(tokenManager.getToken() == null)
        assert(tokenManager.getUsername() == null)
        assert(tokenManager.getUserRole() == null)
    }

    @Test
    fun `getUsername returns saved username`() {
        tokenManager.saveUsername("testuser")
        assert(tokenManager.getUsername() == "testuser")
    }

    @Test
    fun `saveUsername saves the username`() {
        tokenManager.saveUsername("anotheruser")
        assert(sharedPreferences.getString("username", null) == "anotheruser")
    }

    @Test
    fun `clearAll clears all preferences`() {
        tokenManager.saveToken(createJwt("{\"sub\":\"testuser\",\"exp\":${(System.currentTimeMillis() / 1000) + 3600}}"))
        tokenManager.saveUsername("testuser")
        tokenManager.saveUserRole("Admin")
        tokenManager.clearAll()
        assert(tokenManager.getToken() == null)
        assert(tokenManager.getUsername() == null)
        assert(tokenManager.getUserRole() == null)
    }

    @Test
    fun `isAdmin returns true for Admin role`() {
        tokenManager.saveUserRole("Admin")
        assert(tokenManager.isAdmin())
        assert(!tokenManager.isCustomer())
    }

    @Test
    fun `isCustomer returns true for Customer role`() {
        tokenManager.saveUserRole("Customer")
        assert(tokenManager.isCustomer())
        assert(!tokenManager.isAdmin())
    }

    @Test
    fun `isTokenValid returns true for valid token`() {
        val validJwt = createJwt("{\"sub\":\"testuser\",\"exp\":${(System.currentTimeMillis() / 1000) + 3600}}") // Expires in 1 hour
        tokenManager.saveToken(validJwt)
        assert(tokenManager.isTokenValid())
    }

    @Test
    fun `isTokenValid returns false for expired token`() {
        val expiredJwt = createJwt("{\"sub\":\"testuser\",\"exp\":${(System.currentTimeMillis() / 1000) - 3600}}") // Expired 1 hour ago
        tokenManager.saveToken(expiredJwt)
        assert(!tokenManager.isTokenValid())
    }

    @Test
    fun `shouldRefreshToken returns true when token is near expiry`() {
        // Expires in 5 minutes (300 seconds)
        val nearExpiryJwt = createJwt("{\"sub\":\"testuser\",\"exp\":${(System.currentTimeMillis() / 1000) + 300}}")
        tokenManager.saveToken(nearExpiryJwt)
        assert(tokenManager.shouldRefreshToken())
    }

    @Test
    fun `shouldRefreshToken returns false when token is not near expiry`() {
        // Expires in 15 minutes (900 seconds)
        val notNearExpiryJwt = createJwt("{\"sub\":\"testuser\",\"exp\":${(System.currentTimeMillis() / 1000) + 900}}")
        tokenManager.saveToken(notNearExpiryJwt)
        assert(!tokenManager.shouldRefreshToken())
    }

    @Test
    fun `shouldRefreshToken returns false when no token is present`() {
        tokenManager.deleteToken()
        assert(!tokenManager.shouldRefreshToken())
    }
}
