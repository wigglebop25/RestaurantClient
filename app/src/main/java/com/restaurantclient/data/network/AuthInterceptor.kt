package com.restaurantclient.data.network

import android.util.Log
import com.restaurantclient.data.TokenManager
import com.restaurantclient.data.dto.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Provider

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiServiceProvider: Provider<ApiService>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        
        if (url.contains("/ws") || url.startsWith("ws") || url.startsWith("wss") || url.contains("/auth/login") || url.contains("/auth/register") || url.contains("/auth/refresh")) {
            return chain.proceed(request)
        }

        val requestBuilder = request.newBuilder()
        
        tokenManager.getToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        
        var response = chain.proceed(requestBuilder.build())
        
        if (response.code == 401) {
            Log.w("AuthInterceptor", "Received 401 Unauthorized - attempting token refresh")
            
            synchronized(this) {
                val currentToken = tokenManager.getToken()
                val refreshToken = tokenManager.getRefreshToken()
                
                if (refreshToken != null) {
                    // Try to refresh token
                    val refreshResponse = runBlocking {
                        try {
                            apiServiceProvider.get().refreshToken(RefreshTokenRequest(refreshToken))
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    if (refreshResponse != null && refreshResponse.isSuccessful) {
                        val newLoginResponse = refreshResponse.body()
                        if (newLoginResponse?.token != null) {
                            Log.d("AuthInterceptor", "Token refreshed successfully")
                            tokenManager.saveToken(newLoginResponse.token, newLoginResponse.refreshToken)
                            
                            // Close the previous response
                            response.close()
                            
                            // Retry the original request with the new token
                            val newRequest = request.newBuilder()
                                .header("Authorization", "Bearer ${newLoginResponse.token}")
                                .build()
                            return chain.proceed(newRequest)
                        }
                    }
                }
                
                Log.e("AuthInterceptor", "Token refresh failed - clearing token")
                tokenManager.deleteToken()
            }
        }
        
        return response
    }
}
