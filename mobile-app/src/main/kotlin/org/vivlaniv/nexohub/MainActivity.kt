package org.vivlaniv.nexohub

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.rememberNavController
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.vivlaniv.nexohub.pages.MainPage

const val TAG = "mobile-app"

class AppState : Application() {
    lateinit var username: String
    lateinit var mqttClient: MqttAndroidClient
    var mqttConnected = mutableStateOf(false)
}

fun AppState.configureWith(applicationContext: Context) {
    username = "user"
    mqttClient = MqttAndroidClient(
        applicationContext, "tcp://192.168.0.101:1883", MqttClient.generateClientId()
    ).apply {
        setCallback(mqttCallbackObj())
    }
    mqttClient.connect(null, mqttActionListener(this))
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as AppState).configureWith(applicationContext)

        setContent {
            val navController = rememberNavController()

            MainPage(
                appState = application as AppState,
                navController = navController
            )
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

fun mqttActionListener(state: AppState) = object : IMqttActionListener {
    override fun onSuccess(asyncActionToken: IMqttToken) {
        Log.i(TAG, "mqtt client connection succeeded")
        state.mqttConnected.value = true
    }

    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
        Log.e(TAG, "mqtt client connection failed", exception)
    }
}
