package org.example.app.data.model

// PUBLIC_INTERFACE
data class WsConfig(
    /** WebSocket server URL, e.g. wss://example/ws */
    val url: String = "",
    /** Optional Bearer token header */
    val authToken: String? = null
)

// PUBLIC_INTERFACE
data class MqttConfig(
    /** Broker URI, e.g. tcp://host:1883 or ssl://host:8883 */
    val brokerUri: String = "",
    val clientId: String = "",
    val username: String? = null,
    val password: String? = null,
    val defaultTopic: String = "",
    val defaultQos: Int = 0,
    val defaultRetain: Boolean = false
)

// PUBLIC_INTERFACE
data class AppConfig(
    /** Combined configuration for WS and MQTT. */
    val ws: WsConfig = WsConfig(),
    val mqtt: MqttConfig = MqttConfig()
)
