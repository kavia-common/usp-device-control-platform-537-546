package org.example.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.example.app.R
import org.example.app.data.ConfigStore
import org.example.app.data.model.AppConfig
import org.example.app.data.model.MqttConfig
import org.example.app.data.model.WsConfig

/**
 * PUBLIC_INTERFACE
 * UI fragment for configuration persistence and apply.
 */
class ConfigFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val store = ConfigStore(requireContext())
        val cfg = store.load()

        val wsUrl = view.findViewById<TextInputEditText>(R.id.input_ws_url)
        val wsToken = view.findViewById<TextInputEditText>(R.id.input_auth_token)
        val mqttUri = view.findViewById<TextInputEditText>(R.id.input_mqtt_uri)
        val clientId = view.findViewById<TextInputEditText>(R.id.input_client_id)
        val username = view.findViewById<TextInputEditText>(R.id.input_username)
        val password = view.findViewById<TextInputEditText>(R.id.input_password)
        val topic = view.findViewById<TextInputEditText>(R.id.input_default_topic)
        val qos = view.findViewById<TextInputEditText>(R.id.input_default_qos)
        val retain = view.findViewById<MaterialCheckBox>(R.id.check_default_retain)

        // Pre-fill
        wsUrl.setText(cfg.ws.url)
        wsToken.setText(cfg.ws.authToken ?: "")
        mqttUri.setText(cfg.mqtt.brokerUri)
        clientId.setText(cfg.mqtt.clientId)
        username.setText(cfg.mqtt.username ?: "")
        password.setText(cfg.mqtt.password ?: "")
        topic.setText(cfg.mqtt.defaultTopic)
        qos.setText(cfg.mqtt.defaultQos.toString())
        retain.isChecked = cfg.mqtt.defaultRetain

        fun readConfig(): AppConfig? {
            val ws = wsUrl.text?.toString().orEmpty()
            val token = wsToken.text?.toString().orEmpty().ifBlank { null }

            val mqtt = mqttUri.text?.toString().orEmpty()
            val cid = clientId.text?.toString().orEmpty()
            val user = username.text?.toString().orEmpty().ifBlank { null }
            val pass = password.text?.toString().orEmpty().ifBlank { null }
            val t = topic.text?.toString().orEmpty()
            val q = qos.text?.toString()?.toIntOrNull() ?: 0
            val r = retain.isChecked

            if (ws.isBlank() || !ws.startsWith("ws")) {
                Snackbar.make(view, getString(R.string.error_invalid_ws), Snackbar.LENGTH_SHORT).show()
                return null
            }
            if (mqtt.isBlank() || !(mqtt.startsWith("tcp://") || mqtt.startsWith("ssl://"))) {
                Snackbar.make(view, getString(R.string.error_invalid_mqtt), Snackbar.LENGTH_SHORT).show()
                return null
            }
            if (cid.isBlank()) {
                Snackbar.make(view, "Client ID required", Snackbar.LENGTH_SHORT).show()
                return null
            }
            return AppConfig(
                ws = WsConfig(ws, token),
                mqtt = MqttConfig(mqtt, cid, user, pass, t, q, r)
            )
        }

        view.findViewById<MaterialButton>(R.id.btn_save).setOnClickListener {
            val newCfg = readConfig() ?: return@setOnClickListener
            store.save(newCfg)
            Snackbar.make(view, getString(R.string.saved_ok), Snackbar.LENGTH_SHORT).show()
        }

        view.findViewById<MaterialButton>(R.id.btn_apply).setOnClickListener {
            val newCfg = readConfig() ?: return@setOnClickListener
            store.save(newCfg)
            Snackbar.make(view, "Applied", Snackbar.LENGTH_SHORT).show()
        }
    }
}
