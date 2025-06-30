package com.zooeydigital.vpndemo

import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zooeydigital.vpndemo.ui.screen.VpnScreen
import com.zooeydigital.vpndemo.ui.theme.VpnDemoTheme
import com.zooeydigital.vpndemo.viewmodel.UiEvent
import com.zooeydigital.vpndemo.viewmodel.VpnViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val vpnViewModel: VpnViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val vpnConsentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                vpnViewModel.onVpnPermissionGranted()
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vpnViewModel.uiEvents.collect { event ->
                    when(event) {
                        UiEvent.LaunchVpnConsent -> {
                            val vpnIntent = VpnService.prepare(this@MainActivity)
                            if (vpnIntent != null) {
                                vpnConsentLauncher.launch(vpnIntent)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }

        setContent {
            VpnDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VpnScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = vpnViewModel
                    )
                }
            }
        }
    }
}