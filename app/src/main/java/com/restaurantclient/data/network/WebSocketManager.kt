package com.restaurantclient.data.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.json.JSONObject

import com.restaurantclient.data.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Singleton
class WebSocketManager @Inject constructor(
    private val client: OkHttpClient,
    private val tokenManager: TokenManager
) {
    private var webSocket: WebSocket? = null
    private val _events = MutableSharedFlow<WebSocketEvent>(extraBufferCapacity = 10)
    val events: SharedFlow<WebSocketEvent> = _events.asSharedFlow()
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentUrl: String? = null
    private var isManualDisconnect = false

    fun connect(baseUrl: String) {
        isManualDisconnect = false
        val token = tokenManager.getToken()
        if (token == null) {
            Log.e("WebSocketManager", "Cannot connect: Token is null")
            return
        }

        try {
            // Robust URL construction: Force root /ws path and handle scheme upgrade
            val uri = java.net.URI(baseUrl)
            val scheme = if (uri.scheme == "https") "wss" else "ws"
            val portPart = if (uri.port != -1) ":${uri.port}" else ""
            val fullUrl = "$scheme://${uri.host}$portPart/ws?token=$token"
            
            Log.d("WebSocketManager", "Connecting to WebSocket URL: $scheme://${uri.host}$portPart/ws?token=***")
            
            currentUrl = baseUrl // Store base for reconnection
            
            if (webSocket != null) return

            val request = Request.Builder()
                .url(fullUrl)
                .build()
            
            webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.i("WebSocketManager", "WebSocket Connected")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleMessage()
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                this@WebSocketManager.webSocket = null
                if (!isManualDisconnect) reconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                this@WebSocketManager.webSocket = null
                if (!isManualDisconnect) reconnect()
            }
        })
        } catch (e: Exception) {
            Log.e("WebSocketManager", "Error connecting to WebSocket", e)
            this@WebSocketManager.webSocket = null
            if (!isManualDisconnect) reconnect()
        }
    }

    private var reconnectJob: kotlinx.coroutines.Job? = null

    private fun reconnect() {
        if (reconnectJob?.isActive == true) return
        
        currentUrl?.let { url ->
            reconnectJob = scope.launch {
                // Exponential backoff or fixed delay to prevent spamming
                delay(5000)
                Log.d("WebSocketManager", "Attempting to reconnect...")
                connect(url)
            }
        }
    }

    fun disconnect() {
        isManualDisconnect = true
        webSocket?.close(1000, "User logout/disconnect")
        webSocket = null
    }

    private fun handleMessage() {
        try {
            // Any message received acts as a signal to refresh data
            _events.tryEmit(WebSocketEvent.RefreshRequired)
        } catch (e: Exception) {
            Log.e("WebSocketManager", "Error handling message", e)
        }
    }
}

sealed class WebSocketEvent {
    object RefreshRequired : WebSocketEvent()
}
