package org.vivlaniv.nexohub.mobile.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.vivlaniv.nexohub.mobile.AppState
import org.vivlaniv.nexohub.common.DeviceInfo
import org.vivlaniv.nexohub.common.task.SearchDevicesTask
import org.vivlaniv.nexohub.common.task.SearchDevicesTaskResult
import org.vivlaniv.nexohub.mobile.TAG
import org.vivlaniv.nexohub.mobile.util.publish
import org.vivlaniv.nexohub.mobile.util.subscribe
import org.vivlaniv.nexohub.mobile.widgets.NewDeviceTabsList
import org.vivlaniv.nexohub.mobile.widgets.ReturnTopAppBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SearchPage(state: AppState, navigateBack: () -> Unit) {
    val client = state.mqttClient
    val token = state.userToken

    val searchDevices = remember { mutableStateListOf<DeviceInfo>() }
    var searchDevicesLoading by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedDevice by remember { mutableStateOf<DeviceInfo?>(null) }

    var delayedError by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    client.subscribe<SearchDevicesTaskResult>("$token/search/out") { response ->
        if (response.code != 0) {
            Log.e(TAG, "search devices failed, code ${response.code} message ${response.errorMessage}")
            delayedError = true
            errorText = response.errorMessage ?: "some error occurred"
            return@subscribe
        }
        searchDevices.clear()
        searchDevices.addAll(response.devices!!)
        searchDevicesLoading = false
        Log.i(TAG, "search performed, ${response.devices!!.size} devices found")
    }

    fun sendSearchDevices() {
        Log.i(TAG, "fetch button tapped")
        searchDevicesLoading = true
        val request = SearchDevicesTask()
        client.publish("$token/search/in", request)
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = searchDevicesLoading,
        onRefresh = ::sendSearchDevices
    )

    LaunchedEffect(true) {
        sendSearchDevices()
    }

    LaunchedEffect(key1 = searchDevicesLoading) {
        if (!searchDevicesLoading) return@LaunchedEffect
        delay(8_000)
        if (!searchDevicesLoading) return@LaunchedEffect
        searchDevices.clear()
        isError = true
        errorText = "server is not responding, try again later"
        searchDevicesLoading = false
    }

    LaunchedEffect(key1 = delayedError) {
        if (!delayedError) return@LaunchedEffect
        delay(1_500)
        searchDevices.clear()
        isError = true
        searchDevicesLoading = false
        delayedError = false
    }

    Scaffold(
        topBar = {
            ReturnTopAppBar(
                title = "Add new device",
                showBackIcon = true,
                onIconClick = { navigateBack() }
            )
        }, content = { padding ->
            Box (
                modifier = Modifier.padding(padding)
            ) {
                Box(
                    modifier = Modifier.padding(PaddingValues(horizontal = 5.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(pullRefreshState)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (isError) {
                            Text(
                                text = errorText,
                                modifier = Modifier.padding(PaddingValues(4.dp)),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            NewDeviceTabsList(
                                devices = searchDevices,
                                onSelectDevice = { device ->
                                    selectedDevice = device
                                    showBottomSheet = true
                                }
                            )
                        }
                        Button(
                            onClick = { sendSearchDevices() },
                            modifier = Modifier
                                .width(100.dp)
                                .height(40.dp),
                            enabled = !searchDevicesLoading,
                            content = {
                                if (searchDevicesLoading) {
                                    LinearProgressIndicator()
                                } else {
                                    Text(text = "Search")
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                PullRefreshIndicator(
                    refreshing = searchDevicesLoading,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    content = {
                        SavePage(
                            state = state,
                            device = selectedDevice!!,
                            onSaved = { navigateBack() }
                        )
                    }
                )
            }
        }
    )
}