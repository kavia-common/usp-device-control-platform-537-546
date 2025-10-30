package org.example.app.net.mqtt

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.example.app.data.model.MqttConfig
import org.example.app.util.LogEntry
import java.text.SimpleDateFormat
import java.util.*

/**
 * PUBLIC_INTERFACE
 * Manages a Paho MQTT client: connect, publish, subscribe and logs.
 */
class MqttManager(private val context: Context) {

    private val _connected = MutableLiveData(false)
    val connected: LiveData<Boolean> = _connected

    private val _logs = MutableLiveData<List<LogEntry>>(emptyList())
    val logs: LiveData<List<LogEntry>> = _logs

    private var cfg: MqttConfig? = null
    private var client: MqttClient? = null

    fun applyConfig(c: MqttConfig) {
        cfg = c
        log("INFO", "MQTT config applied: ${c.brokerUri}")
    }

    fun connect() {
        val c = cfg ?: run {
            log("ERROR", "No MQTT config")
            return
        }
        if (c.brokerUri.isBlank() || c.clientId.isBlank()) {
            log("ERROR", "Broker uri or clientId is blank")
            return
        }
        if (client?.isConnected == true) {
            log("INFO", "Already connected")
            return
        }
        try {
            val cl = MqttClient(c.brokerUri, c.clientId, null)
            val opts = MqttConnectOptions().apply {
                isAutomaticReconnect = true
                isCleanSession = true
                c.username?.let { userName = it }
                c.password?.let { password = it.toCharArray() }
            }
            cl.setCallback(object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    log("INFO", "RX ${topic}: ${message?.toString()}")
                }

                override fun connectionLost(cause: Throwable?) {
                    _connected.postValue(false)
                    log("ERROR", "Connection lost: ${cause?.message}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    log("INFO", "Delivery complete")
                }
            })
            client = cl
            try {
                cl.connect(opts)
                if (cl.isConnected) {
                    _connected.postValue(true)
                    log("INFO", "MQTT connected")
                } else {
                    _connected.postValue(false)
                    log("ERROR", "Connect returned not connected")
                }
            } catch (ex: Exception) {
                _connected.postValue(false)
                log("ERROR", "Connect failed: ${ex.message}")
            }
        } catch (e: Exception) {
            _connected.postValue(false)
            log("ERROR", "Exception: ${e.message}")
        }
    }

    fun disconnect() {
        try {
            client?.disconnect()
            client?.close()
            client = null
            _connected.postValue(false)
            log("INFO", "Disconnected")
        } catch (e: Exception) {
            log("ERROR", "Disconnect error: ${e.message}")
        }
    }

    fun subscribe(topic: String, qos: Int) {
        val cl = client ?: return log("ERROR", "Not connected")
        try {
            cl.subscribe(topic, qos)
            log("INFO", "Subscribed $topic qos=$qos")
        } catch (e: Exception) {
            log("ERROR", "Subscribe error: ${e.message}")
        }
    }

    fun unsubscribe(topic: String) {
        val cl = client ?: return log("ERROR", "Not connected")
        try {
            cl.unsubscribe(topic)
            log("INFO", "Unsubscribed $topic")
        } catch (e: Exception) {
            log("ERROR", "Unsubscribe error: ${e.message}")
        }
    }

    fun publish(topic: String, payload: String, qos: Int, retain: Boolean) {
        val cl = client ?: return log("ERROR", "Not connected")
        try {
            val msg = MqttMessage(payload.toByteArray()).apply {
                this.qos = qos
                isRetained = retain
            }
            cl.publish(topic, msg)
            log("INFO", "TX $topic qos=$qos retain=$retain: $payload")
        } catch (e: Exception) {
            log("ERROR", "Publish error: ${e.message}")
        }
    }

    private fun log(level: String, msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        val current = _logs.value?.toMutableList() ?: mutableListOf()
        current.add(LogEntry(time, level, msg))
        if (current.size > 500) current.removeAt(0)
        _logs.postValue(current)
    }
}
