package com.zooeydigital.vpndemo.ui.screen

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zooeydigital.vpndemo.model.VpnState
import com.zooeydigital.vpndemo.model.label
import com.zooeydigital.vpndemo.viewmodel.UiEvent
import com.zooeydigital.vpndemo.viewmodel.VpnViewModel

/**
 * Minimal UI design to start/stop VPN
 * Displays the VPN status and any applicable dialog message
 *
 * Created by Clayton Hatathlie on 6/30/25
 **/
@Composable
fun VpnScreen(
    modifier: Modifier,
    viewModel: VpnViewModel
) {
    val vpnState by viewModel.vpnState.collectAsStateWithLifecycle()
    val dialogMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowDialogMessage -> {
                    dialogMessage.value = event.message
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "VPN Status: ${vpnState.label}",
            style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(16.dp))

        Button(onClick = { viewModel.toggleVpn() }) {
            Text(if (vpnState == VpnState.NOT_RUNNING) "Start VPN" else "Stop VPN")
        }
    }

    dialogMessage.value?.let { message ->
        AlertDialog(
            onDismissRequest = { dialogMessage.value = null },
            confirmButton = {
                TextButton(onClick = { dialogMessage.value = null }) {
                    Text("OK")
                }
            },
            title = { Text("VPN Error") },
            text  = { Text(message) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VpnScreenPreview() {
    VpnScreen(
        modifier = Modifier,
        viewModel = VpnViewModel(app = Application()))
}