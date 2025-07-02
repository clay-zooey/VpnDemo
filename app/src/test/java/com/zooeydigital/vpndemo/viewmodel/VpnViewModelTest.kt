package com.zooeydigital.vpndemo.viewmodel

import android.app.Application
import android.content.Intent
import android.net.VpnService
import com.zooeydigital.vpndemo.model.VpnState
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Clayton Hatathlie on 7/2/25
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VpnViewModelTest {

 private val testDispatcher = StandardTestDispatcher()
 private lateinit var app: Application
 private lateinit var viewModel: VpnViewModel

 @Before fun setUp() {
  Dispatchers.setMain(testDispatcher)
  app = mockk(relaxed = true)
 }

 @After fun tearDown() {
  Dispatchers.resetMain()
  unmockkAll()
 }

 @Test fun `initial state is NOT_RUNNING`() = runTest {
  viewModel = VpnViewModel(app)
  assertEquals(VpnState.NOT_RUNNING, viewModel.vpnState.value)
 }

 @Test fun `toggleVpn emits LaunchVpnConsent when permission missing`() = runTest {
  //mock VpnService.prepare to return a non-null Intent
  mockkStatic(VpnService::class)
  every { VpnService.prepare(app) } returns Intent("testAction")

  viewModel = VpnViewModel(app)

  // waits for first event
  val event = async { viewModel.uiEvents.first() }
  viewModel.toggleVpn()

  assertTrue(event.await() is UiEvent.LaunchVpnConsent)
 }

}