package org.vivlaniv.nexohub.mobile

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.vivlaniv.nexohub.mobile.pages.MainPage
import java.util.Properties

const val TAG = "mobile-app"

class AppState : Application() {
    lateinit var properties: Properties
    lateinit var mqttClient: MqttAndroidClient
    lateinit var username: String
    lateinit var userToken: String
}

fun AppState.configureWith(applicationContext: Context, onFinished: () -> Unit) {
    properties = Properties().apply {
        load(applicationContext.assets.open("app.properties"))
    }

    val mqttUtl = properties.getProperty("mqtt.url", "tcp://localhost:1883")
    mqttClient = MqttAndroidClient(
        applicationContext, mqttUtl, MqttClient.generateClientId()
    ).apply {
        setCallback(mqttCallbackObj())
    }
    mqttClient.connect(null, mqttActionListener(onFinished))
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appState = application as AppState
        var configurationFinished by mutableStateOf(false)

        appState.configureWith(applicationContext) { configurationFinished = true }

        setContent {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (!configurationFinished) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    MainPage(
                        state = appState,
                        navController = rememberNavController()
                    )
                }
            }
        }
    }
}

fun mqttCallbackObj() = object : MqttCallback {
    override fun connectionLost(cause: Throwable) {
        Log.e(TAG, "mqtt connection lost", cause)
    }

    override fun messageArrived(topic: String, message: MqttMessage) {
        Log.i(TAG, "mqtt message arrived $topic $message")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {
        Log.i(TAG, "mqtt delivery completed")
    }
}

fun mqttActionListener(onSuccess: () -> Unit) = object : IMqttActionListener {
    override fun onSuccess(asyncActionToken: IMqttToken) {
        Log.i(TAG, "mqtt client connection succeeded")
        onSuccess()
    }

    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
        Log.e(TAG, "mqtt client connection failed", exception)
    }
}
