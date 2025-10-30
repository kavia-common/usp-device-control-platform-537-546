package org.example.app.net.ws

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.example.app.data.model.WsConfig
import org.example.app.util.LogEntry
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * PUBLIC_INTERFACE
 * Manages a single WebSocket connection via OkHttp, exposing connection state and logs.
 */
class WebSocketManager(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .pingInterval(20, TimeUnit.SECONDS)
        .build()
) {
    private val _connected = MutableLiveData(false)
    val connected: LiveData<Boolean> = _connected

    private val _logs = MutableLiveData<List<LogEntry>>(emptyList())
    val logs: LiveData<List<LogEntry>> = _logs

    private var ws: WebSocket? = null
    private var config: WsConfig? = null

    fun applyConfig(cfg: WsConfig) {
        this.config = cfg
        log("INFO", "WS config applied: ${cfg.url}")
    }

    fun connect(): Boolean {
        val cfg = config ?: run {
            log("ERROR", "WS config missing")
            return false
        }
        if (cfg.url.isBlank()) {
            log("ERROR", "WS url is blank")
            return false
        }
        if (_connected.value == true) {
            log("INFO", "Already connected")
            return true
        }

        val reqBuilder = Request.Builder().url(cfg.url)
        cfg.authToken?.takeIf { it.isNotBlank() }?.let {
            reqBuilder.addHeader("Authorization", "Bearer $it")
        }
        val request = reqBuilder.build()
        ws = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                _connected.postValue(true)
                log("INFO", "WebSocket connected")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                log("INFO", "RX: $text")
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                log("INFO", "RX bytes: ${bytes.hex()}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                log("WARN", "Closing: $code $reason")
                _connected.postValue(false)
                webSocket.close(code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                log("ERROR", "Failure: ${t.message}")
                _connected.postValue(false)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                log("INFO", "Closed: $code $reason")
                _connected.postValue(false)
            }
        })
        return true
    }

    fun send(message: String): Boolean {
        val socket = ws ?: run {
            log("ERROR", "No socket")
            return false
        }
        val ok = socket.send(message)
        if (ok) log("INFO", "TX: $message") else log("ERROR", "Send failed")
        return ok
    }

    fun disconnect() {
        ws?.close(1000, "User closed")
        ws = null
        _connected.postValue(false)
        log("INFO", "Disconnected")
    }

    private fun log(level: String, msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        val current = _logs.value?.toMutableList() ?: mutableListOf()
        current.add(LogEntry(time, level, msg))
        if (current.size > 500) current.removeAt(0)
        _logs.postValue(current)
    }
}
