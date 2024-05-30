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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.vivlaniv.nexohub.common.SavedDevice
import org.vivlaniv.nexohub.common.task.FetchDevicesPropertiesTask
import org.vivlaniv.nexohub.common.task.FetchDevicesPropertiesTaskResult
import org.vivlaniv.nexohub.common.task.FetchSavedDevicesTask
import org.vivlaniv.nexohub.common.task.FetchSavedDevicesTaskResult
import org.vivlaniv.nexohub.common.task.PutDevicePropertyTask
import org.vivlaniv.nexohub.common.task.PutDevicePropertyTaskResult
import org.vivlaniv.nexohub.mobile.AppState
import org.vivlaniv.nexohub.mobile.TAG
import org.vivlaniv.nexohub.mobile.util.publish
import org.vivlaniv.nexohub.mobile.util.subscribe
import org.vivlaniv.nexohub.mobile.widgets.DeviceTabsList
import org.vivlaniv.nexohub.mobile.widgets.UserTopAppBar

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomePage(state: AppState, onSignOut: () -> Unit, navigateToSearchPage: () -> Unit) {
    val client = state.mqttClient
    val user = state.username
    val token = state.userToken

    val fetchDevices = remember { mutableStateListOf<SavedDevice>() }
    var fetchDevicesLoading by remember { mutableStateOf(false) }
    var handledFetchPropertiesCount by remember { mutableIntStateOf(0) }

    var delayedError by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    client.subscribe<FetchSavedDevicesTaskResult>("$token/fetch/devices/out") { response ->
        if (response.code != 0) {
            Log.e(TAG, "fetch saved failed, code ${response.code} message ${response.errorMessage}")
            delayedError = true
            errorText = response.errorMessage ?: "some error occurred"
            return@subscribe
        }
        fetchDevices.clear()
        fetchDevices.addAll(response.devices!!)
        fetchDevicesLoading = false
        Log.i(TAG, "fetch saved performed, ${response.devices!!.size} devices found")
        handledFetchPropertiesCount++
    }

    client.subscribe<PutDevicePropertyTaskResult>("$token/property/out") { response ->
        if (response.code != 0) {
            Log.e(TAG, "put property failed, code ${response.code} message ${response.errorMessage}")
            delayedError = true
            errorText = response.errorMessage ?: "some error occurred"
            return@subscribe
        }
        Log.i(TAG, "property of ${response.device} changed successfully")
        val request = FetchDevicesPropertiesTask(include = listOf(response.device))
        client.publish("$token/fetch/props/in", request)
    }

    client.subscribe<FetchDevicesPropertiesTaskResult>("$token/fetch/props/out") { response ->
        if (response.code != 0) {
            Log.e(TAG, "fetch all failed, code ${response.code} message ${response.errorMessage}")
            delayedError = true
            errorText = response.errorMessage ?: "some error occurred"
            return@subscribe
        }
        for (device in fetchDevices) {
            val newProperties = response.properties!![device.id] ?: continue
            device.properties = newProperties
        }
        Log.i(TAG, "fetch all performed, ${response.properties!!.size} devices found")
        handledFetchPropertiesCount++
    }

    fun sendFetchSavedDevices() {
        Log.i(TAG, "fetch saved button tapped")
        fetchDevicesLoading = true
        val request = FetchSavedDevicesTask()
        client.publish("$token/fetch/devices/in", request)
    }

    fun sendFetchDevicesProperties() {
        Log.i(TAG, "fetch all properties tapped")
        val request = FetchDevicesPropertiesTask()
        client.publish("$token/fetch/props/in", request)
    }

    fun sendPutDeviceProperty(device: String, property: String, value: Int) {
        Log.i(TAG, "put property $property tapped")
        val request = PutDevicePropertyTask(device = device, property = property, value = value)
        client.publish("$token/property/in", request)
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = fetchDevicesLoading,
        onRefresh = ::sendFetchSavedDevices
    )

    LaunchedEffect(true) {
        sendFetchSavedDevices()
    }

    LaunchedEffect(key1 = handledFetchPropertiesCount) {
        delay(10_000)
        sendFetchDevicesProperties()
    }

    LaunchedEffect(key1 = fetchDevicesLoading) {
        if (!fetchDevicesLoading) return@LaunchedEffect
        delay(8_000)
        if (!fetchDevicesLoading) return@LaunchedEffect
        fetchDevices.clear()
        isError = true
        errorText = "server is not responding, try again later"
        fetchDevicesLoading = false
    }

    LaunchedEffect(key1 = delayedError) {
        if (!delayedError) return@LaunchedEffect
        delay(1_500)
        fetchDevices.clear()
        isError = true
        fetchDevicesLoading = false
        delayedError = false
    }

    Scaffold (
        topBar = {
            UserTopAppBar(
                user = user,
                onSignOut = { onSignOut() }
            )
        }, content = { padding ->
            Box(
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
                                color = MaterialTheme.colorScheme.error,
                            )
                        } else {
                            DeviceTabsList(
                                devices = fetchDevices,
                                onPutProperty = ::sendPutDeviceProperty,
                                shouldRefresh = handledFetchPropertiesCount
                            )
                        }
                        Button(
                            onClick = { navigateToSearchPage() },
                            modifier = Modifier
                                .width(120.dp)
                                .height(40.dp),
                            enabled = !fetchDevicesLoading,
                            content = {
                                if (fetchDevicesLoading) {
                                    LinearProgressIndicator()
                                } else {
                                    Text(text = "Add new")
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
                PullRefreshIndicator(
                    refreshing = fetchDevicesLoading,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    )
}