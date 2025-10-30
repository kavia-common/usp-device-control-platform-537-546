package org.example.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import org.example.app.data.model.AppConfig
import org.example.app.data.model.MqttConfig
import org.example.app.data.model.WsConfig

/**
 * PUBLIC_INTERFACE
 * Persist and retrieve application configuration using SharedPreferences.
 */
class ConfigStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun save(config: AppConfig) {
        prefs.edit(commit = true) {
            putString(KEY_WS_URL, config.ws.url)
            putString(KEY_WS_TOKEN, config.ws.authToken)

            putString(KEY_MQTT_URI, config.mqtt.brokerUri)
            putString(KEY_MQTT_CLIENT_ID, config.mqtt.clientId)
            putString(KEY_MQTT_USERNAME, config.mqtt.username)
            putString(KEY_MQTT_PASSWORD, config.mqtt.password)
            putString(KEY_MQTT_TOPIC, config.mqtt.defaultTopic)
            putInt(KEY_MQTT_QOS, config.mqtt.defaultQos)
            putBoolean(KEY_MQTT_RETAIN, config.mqtt.defaultRetain)
        }
    }

    fun load(): AppConfig {
        val ws = WsConfig(
            url = prefs.getString(KEY_WS_URL, "") ?: "",
            authToken = prefs.getString(KEY_WS_TOKEN, null)
        )
        val mqtt = MqttConfig(
            brokerUri = prefs.getString(KEY_MQTT_URI, "") ?: "",
            clientId = prefs.getString(KEY_MQTT_CLIENT_ID, "") ?: "",
            username = prefs.getString(KEY_MQTT_USERNAME, null),
            password = prefs.getString(KEY_MQTT_PASSWORD, null),
            defaultTopic = prefs.getString(KEY_MQTT_TOPIC, "") ?: "",
            defaultQos = prefs.getInt(KEY_MQTT_QOS, 0),
            defaultRetain = prefs.getBoolean(KEY_MQTT_RETAIN, false)
        )
        return AppConfig(ws, mqtt)
    }

    companion object {
        private const val PREFS = "usp_config"

        private const val KEY_WS_URL = "ws_url"
        private const val KEY_WS_TOKEN = "ws_token"

        private const val KEY_MQTT_URI = "mqtt_uri"
        private const val KEY_MQTT_CLIENT_ID = "mqtt_client_id"
        private const val KEY_MQTT_USERNAME = "mqtt_username"
        private const val KEY_MQTT_PASSWORD = "mqtt_password"
        private const val KEY_MQTT_TOPIC = "mqtt_topic"
        private const val KEY_MQTT_QOS = "mqtt_qos"
        private const val KEY_MQTT_RETAIN = "mqtt_retain"
    }
}
