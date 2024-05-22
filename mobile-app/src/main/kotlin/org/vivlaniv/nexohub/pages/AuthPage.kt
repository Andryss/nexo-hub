package org.vivlaniv.nexohub.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.vivlaniv.nexohub.AppState
import org.vivlaniv.nexohub.AuthUserTask
import org.vivlaniv.nexohub.AuthUserTaskResult
import org.vivlaniv.nexohub.TAG
import org.vivlaniv.nexohub.util.android.mqtt.publish
import org.vivlaniv.nexohub.util.android.mqtt.subscribe

@Composable
fun AuthPage(state: AppState, onAuthSuccess: () -> Unit, navigateToRegisterPage: () -> Unit) {
    val client = state.mqttClient

    var username by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }

    var authUserLoading by remember { mutableStateOf(false) }

    var delayedError by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    var isSuccess by remember { mutableStateOf(false) }

    val signUpString = buildAnnotatedString {
        append("New to Nexo hub? ")

        pushStringAnnotation("sign up", "Navigate to registration page")
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append("Create an account")
        }
        pop()
    }

    fun onSignInClicked() {
        Log.i(TAG, "sign in button tapped")
        isError = false

        val user = username.text.trim()
        if (user == "") {
            isError = true
            errorText = "username is required"
            return
        }

        val pass = password.text.trim()
        if (pass == "") {
            isError = true
            errorText = "password is required"
            return
        }

        authUserLoading = true
        val request = AuthUserTask(username = user, password = pass)

        client.subscribe<AuthUserTaskResult>("${request.id}/signin/out") { response ->
            if (response.code != 0) {
                delayedError = true
                errorText = response.errorMessage ?: "some error occurred"
            } else {
                isSuccess = true
                state.username = user
                state.userToken = response.token!!
            }
        }

        client.publish("${request.id}/signin/in", request)
    }

    LaunchedEffect(key1 = isSuccess) {
        if (!isSuccess) return@LaunchedEffect
        onAuthSuccess()
    }

    LaunchedEffect(key1 = authUserLoading) {
        if (!authUserLoading) return@LaunchedEffect
        delay(8_000)
        if (!authUserLoading) return@LaunchedEffect
        isError = true
        errorText = "server is not responding, try again later"
        authUserLoading = false
    }

    LaunchedEffect(key1 = delayedError) {
        if (!delayedError) return@LaunchedEffect
        delay(1_500)
        isError = true
        password = TextFieldValue()
        authUserLoading = false
        delayedError = false
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nexo hub",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                readOnly = authUserLoading,
                label = { Text(text = "Username") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                readOnly = authUserLoading,
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            if (isError) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = errorText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        Button(
            onClick = { onSignInClicked() },
            modifier = Modifier
                .width(100.dp)
                .height(40.dp),
            enabled = !authUserLoading,
            content = {
                if (authUserLoading) {
                    LinearProgressIndicator()
                } else {
                    Text(text = "Sign in")
                }
            }
        )
        ClickableText(
            text = signUpString,
            onClick = {
                signUpString.getStringAnnotations("sign up", it, it).firstOrNull()?.let {
                    navigateToRegisterPage()
                }
            }
        )
    }
}