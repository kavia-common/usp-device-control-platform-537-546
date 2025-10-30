package org.example.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.example.app.R
import org.example.app.data.ConfigStore
import org.example.app.net.ws.WebSocketManager
import org.example.app.util.LogEntry

/**
 * PUBLIC_INTERFACE
 * UI fragment for WebSocket controls and logs.
 */
class WSFragment : Fragment() {

    private lateinit var vm: WsVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_ws, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm = ViewModelProvider(requireActivity()).get(WsVM::class.java)
        val btnConnect = view.findViewById<MaterialButton>(R.id.btn_ws_connect)
        val btnDisconnect = view.findViewById<MaterialButton>(R.id.btn_ws_disconnect)
        val btnSend = view.findViewById<MaterialButton>(R.id.btn_ws_send)
        val inputMsg = view.findViewById<TextInputEditText>(R.id.input_ws_message)
        val status = view.findViewById<TextView>(R.id.ws_status)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_logs_ws)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = LogsAdapter()
        recycler.adapter = adapter

        vm.logs.observe(viewLifecycleOwner) { adapter.submitList(it.toList()) }
        vm.connected.observe(viewLifecycleOwner) {
            status.text = if (it) getString(R.string.status_connected) else getString(R.string.status_disconnected)
            btnSend.isEnabled = it
        }

        btnConnect.setOnClickListener {
            val cfg = ConfigStore(requireContext()).load().ws
            vm.manager.applyConfig(cfg)
            if (!vm.manager.connect()) {
                Snackbar.make(view, getString(R.string.error_invalid_ws), Snackbar.LENGTH_SHORT).show()
            }
        }
        btnDisconnect.setOnClickListener { vm.manager.disconnect() }
        btnSend.setOnClickListener {
            val msg = inputMsg.text?.toString().orEmpty()
            if (msg.isNotBlank()) {
                vm.manager.send(msg)
                inputMsg.setText("")
            }
        }
    }

    class WsVM : ViewModel() {
        val manager = WebSocketManager()
        val connected = manager.connected
        val logs: MutableLiveData<List<LogEntry>> = manager.logs as MutableLiveData<List<LogEntry>>
    }
}
