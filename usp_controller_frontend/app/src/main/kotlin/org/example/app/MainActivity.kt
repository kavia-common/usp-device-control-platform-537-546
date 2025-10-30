package org.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import android.view.View
import org.example.app.ui.ConfigFragment
import org.example.app.ui.MQTTFragment
import org.example.app.ui.WSFragment

/**
 * PUBLIC_INTERFACE
 * Main activity setting up Drawer, Tabs, and fragments; shows WS/MQTT status dots.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var drawer: DrawerLayout
    private lateinit var nav: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tabs: TabLayout
    private lateinit var pager: ViewPager2

    private lateinit var dotWs: View
    private lateinit var dotMqtt: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawer = findViewById(R.id.drawer_layout)
        nav = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        tabs = findViewById(R.id.tab_layout)
        pager = findViewById(R.id.view_pager)
        dotWs = findViewById(R.id.dot_ws)
        dotMqtt = findViewById(R.id.dot_mqtt)

        toolbar.setNavigationOnClickListener { drawer.openDrawer(GravityCompat.START) }

        pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3
            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> WSFragment()
                1 -> MQTTFragment()
                else -> ConfigFragment()
            }
        }

        TabLayoutMediator(tabs, pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_ws)
                1 -> getString(R.string.tab_mqtt)
                else -> getString(R.string.tab_config)
            }
        }.attach()

        nav.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_ws -> pager.currentItem = 0
                R.id.nav_mqtt -> pager.currentItem = 1
                R.id.nav_config -> pager.currentItem = 2
            }
            drawer.closeDrawers()
            true
        }

        // Observe ViewModels scoped to Activity to drive toolbar status dots
        val vmStore = androidx.lifecycle.ViewModelProvider(this)
        val wsVM = vmStore.get(WSFragment.WsVM::class.java)
        val mqttVM = vmStore.get(MQTTFragment.MqttVM::class.java).apply { initIfNeeded(this@MainActivity) }

        wsVM.connected.observe(this) { connected ->
            dotWs.setBackgroundResource(
                if (connected) R.drawable.status_dot_connected else R.drawable.status_dot_disconnected
            )
            // Update accessibility description
            dotWs.contentDescription = getString(
                if (connected) R.string.ws_status_connected else R.string.ws_status_disconnected
            )
        }
        mqttVM.connected.observe(this) { connected ->
            dotMqtt.setBackgroundResource(
                if (connected) R.drawable.status_dot_connected else R.drawable.status_dot_disconnected
            )
            // Update accessibility description
            dotMqtt.contentDescription = getString(
                if (connected) R.string.mqtt_status_connected else R.string.mqtt_status_disconnected
            )
        }
    }
}
