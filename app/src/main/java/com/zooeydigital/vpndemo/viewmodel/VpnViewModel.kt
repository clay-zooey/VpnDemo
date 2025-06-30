package com.zooeydigital.vpndemo.viewmodel

import android.app.Application
import android.content.Intent
import android.net.VpnService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zooeydigital.vpndemo.model.VpnState
import com.zooeydigital.vpndemo.service.MyVpnService
import com.zooeydigital.vpndemo.service.VpnEventBus
import com.zooeydigital.vpndemo.service.VpnServiceEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Viewmodel layer that controls interaction with VpnService
 * Communicates to UI with Flows, the status of VpnService
 *
 * Created by Clayton Hatathlie on 6/30/25
 **/
@HiltViewModel
class VpnViewModel @Inject constructor(
    private val app: Application
): AndroidViewModel(app) {

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents

    private val _vpnState = MutableStateFlow(VpnState.NOT_RUNNING)
    val vpnState: StateFlow<VpnState> = _vpnState.asStateFlow()

    init {
        viewModelScope.launch {
            VpnEventBus.events.collect { event ->
                when (event) {
                    is VpnServiceEvent.Error ->
                        _uiEvents.emit(UiEvent.ShowDialogMessage(event.message))
                    VpnServiceEvent.NotRunning -> _vpnState.value = VpnState.NOT_RUNNING
                    VpnServiceEvent.Running -> _vpnState.value = VpnState.RUNNING
                }
            }
        }
    }

    fun toggleVpn() {
        when (_vpnState.value) {
            VpnState.NOT_RUNNING -> checkVpnPermission()
            VpnState.RUNNING -> stopVpn()
        }
    }

    private fun checkVpnPermission() {
        val intent = VpnService.prepare(app)
        if (intent != null) {
            viewModelScope.launch { _uiEvents.emit(UiEvent.LaunchVpnConsent) }
        } else {
            startVpn()
        }
    }

    fun onVpnPermissionGranted() = startVpn()

    private fun startVpn() {
        app.startService(Intent(app, MyVpnService::class.java))
    }

    private fun stopVpn() {
        Intent(app, MyVpnService::class.java).apply {
            action = MyVpnService.ACTION_DISCONNECT
            app.startService(this)
        }
    }
}

sealed class UiEvent {
    data class ShowDialogMessage(val message: String) : UiEvent()
    data object LaunchVpnConsent : UiEvent()
}