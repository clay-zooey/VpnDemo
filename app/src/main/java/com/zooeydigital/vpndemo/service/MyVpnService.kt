package com.zooeydigital.vpndemo.service

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Created by Clayton Hatathlie on 6/30/25
 **/
class MyVpnService: VpnService() {
    companion object {
        const val ACTION_DISCONNECT = "VPN_DISCONNECT"
        const val TAG = "MyVpnService"
    }

    private var vpnInterface: ParcelFileDescriptor? = null
    private var vpnJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_DISCONNECT) {
            teardownVpn()
            stopSelf()
            return START_NOT_STICKY
        }

        vpnInterface = Builder()
            .setSession("DemoVPN")
            .addAddress("10.0.2.0", 24)
            .addDnsServer("8.8.8.8")
            .addRoute("0.0.0.0", 0)
            .establish()

        // Do background work
        vpnJob = CoroutineScope(Dispatchers.IO).launch {

        }

        VpnEventBus.events.tryEmit(VpnServiceEvent.Running)
        return START_STICKY
    }

    override fun onDestroy() {
        teardownVpn()
        super.onDestroy()
    }

    private fun teardownVpn() {
        try {
            vpnInterface?.close()
            vpnInterface = null
            vpnJob?.cancel()
            vpnJob = null
            VpnEventBus.events.tryEmit(VpnServiceEvent.NotRunning)
        } catch (e: IOException) {
            Log.e(TAG, "Error closing VPN interface", e)
            VpnEventBus.events.tryEmit(VpnServiceEvent.Error("VPN closed with error: ${e.message}"))
        }
    }
}

/**
 * Communicate errors as a singleton eventbus,
 * A more robust approach would bind the service to a repo
 * for clearer communication upstream.
 */
object VpnEventBus {
    val events = MutableSharedFlow<VpnServiceEvent>(extraBufferCapacity = 1)
}

sealed class VpnServiceEvent {
    data object Running : VpnServiceEvent()
    data object NotRunning : VpnServiceEvent()
    data class Error(val message: String) : VpnServiceEvent()
}
