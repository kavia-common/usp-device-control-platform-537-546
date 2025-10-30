package org.example.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.example.app.R
import org.example.app.data.ConfigStore
import org.example.app.net.mqtt.MqttManager
import org.example.app.util.LogEntry

/**
 * PUBLIC_INTERFACE
 * UI fragment for MQTT controls and logs.
 */
class MQTTFragment : Fragment() {

    private lateinit var vm: MqttVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_mqtt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm = ViewModelProvider(requireActivity()).get(MqttVM::class.java)
        // Ensure manager exists early to avoid lateinit access before onResume
        vm.initIfNeeded(requireContext())
        val btnConnect = view.findViewById<MaterialButton>(R.id.btn_mqtt_connect)
        val btnDisconnect = view.findViewById<MaterialButton>(R.id.btn_mqtt_disconnect)
        val btnSub = view.findViewById<MaterialButton>(R.id.btn_subscribe)
        val btnUnsub = view.findViewById<MaterialButton>(R.id.btn_unsubscribe)
        val btnPub = view.findViewById<MaterialButton>(R.id.btn_publish)
        val inputTopic = view.findViewById<TextInputEditText>(R.id.input_topic)
        val inputPayload = view.findViewById<TextInputEditText>(R.id.input_payload)
        val inputQos = view.findViewById<TextInputEditText>(R.id.input_qos)
        val checkRetain = view.findViewById<MaterialCheckBox>(R.id.check_retain)
        val status = view.findViewById<TextView>(R.id.mqtt_status)
        val brokerPreview = view.findViewById<TextView>(R.id.mqtt_broker_preview)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_logs_mqtt)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = LogsAdapter()
        recycler.adapter = adapter

        vm.logs.observe(viewLifecycleOwner) { adapter.submitList(it.toList()) }
        vm.connected.observe(viewLifecycleOwner) {
            status.text = if (it) getString(R.string.status_connected) else getString(R.string.status_disconnected)
        }

        // Update preview from config
        val cfg = ConfigStore(requireContext()).load().mqtt
        brokerPreview.text = cfg.brokerUri
        inputTopic.setText(cfg.defaultTopic)
        inputQos.setText(cfg.defaultQos.toString())
        checkRetain.isChecked = cfg.defaultRetain

        btnConnect.setOnClickListener {
            val c = ConfigStore(requireContext()).load().mqtt
            vm.manager.applyConfig(c)
            vm.manager.connect()
        }
        btnDisconnect.setOnClickListener { vm.manager.disconnect() }
        btnSub.setOnClickListener {
            val topic = inputTopic.text?.toString().orEmpty()
            val qos = inputQos.text?.toString()?.toIntOrNull() ?: 0
            if (topic.isBlank()) {
                Snackbar.make(view, getString(R.string.topic) + " required", Snackbar.LENGTH_SHORT).show()
            } else vm.manager.subscribe(topic, qos)
        }
        btnUnsub.setOnClickListener {
            val topic = inputTopic.text?.toString().orEmpty()
            if (topic.isBlank()) {
                Snackbar.make(view, getString(R.string.topic) + " required", Snackbar.LENGTH_SHORT).show()
            } else vm.manager.unsubscribe(topic)
        }
        btnPub.setOnClickListener {
            val topic = inputTopic.text?.toString().orEmpty()
            val qos = inputQos.text?.toString()?.toIntOrNull() ?: 0
            val retain = checkRetain.isChecked
            val payload = inputPayload.text?.toString().orEmpty()
            if (topic.isBlank()) {
                Snackbar.make(view, getString(R.string.topic), Snackbar.LENGTH_SHORT).show()
            } else vm.manager.publish(topic, payload, qos, retain)
        }
    }

    class MqttVM : ViewModel() {
        lateinit var manager: MqttManager
            private set

        val connected get() = manager.connected
        val logs: MutableLiveData<List<LogEntry>> get() = manager.logs as MutableLiveData<List<LogEntry>>

        fun initIfNeeded(context: android.content.Context) {
            if (!::manager.isInitialized) {
                manager = MqttManager(context.applicationContext)
            }
        }

        override fun onCleared() {
            if (::manager.isInitialized) manager.disconnect()
            super.onCleared()
        }
    }

    override fun onResume() {
        super.onResume()
        vm.initIfNeeded(requireContext())
    }
}
