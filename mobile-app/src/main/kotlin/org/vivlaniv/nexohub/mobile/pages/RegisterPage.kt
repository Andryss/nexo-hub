package org.vivlaniv.nexohub.mobile.pages

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
import org.vivlaniv.nexohub.mobile.AppState
import org.vivlaniv.nexohub.common.task.RegisterUserTask
import org.vivlaniv.nexohub.common.task.RegisterUserTaskResult
import org.vivlaniv.nexohub.mobile.TAG
import org.vivlaniv.nexohub.mobile.util.publish
import org.vivlaniv.nexohub.mobile.util.subscribe

@Composable
fun RegisterPage(state: AppState, onRegisterSuccess: () -> Unit) {
    val client = state.mqttClient

    var username by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue()) }

    var registerUserLoading by remember { mutableStateOf(false) }

    var delayedError by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    var isSuccess by remember { mutableStateOf(false) }

    val signInString = buildAnnotatedString {
        append("Already have an account? ")

        pushStringAnnotation("sign in", "Navigate to auth page")
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append("Sign in")
        }
        pop()
    }

    fun onSignUpClicked() {
        Log.i(TAG, "sign up button tapped")
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

        if (confirmPassword.text.trim() != pass) {
            isError = true
            errorText = "passwords must match"
            return
        }

        registerUserLoading = true
        val request = RegisterUserTask(username = user, password = pass)

        client.subscribe<RegisterUserTaskResult>("${request.id}/signup/out") { response ->
            if (response.code != 0) {
                delayedError = true
                errorText = response.errorMessage ?: "some error occurred"
            } else {
                isSuccess = true
            }
        }

        client.publish("${request.id}/signup/in", request)
    }

    LaunchedEffect(key1 = isSuccess) {
        if (!isSuccess) return@LaunchedEffect
        onRegisterSuccess()
    }

    LaunchedEffect(key1 = registerUserLoading) {
        if (!registerUserLoading) return@LaunchedEffect
        delay(8_000)
        if (!registerUserLoading) return@LaunchedEffect
        isError = true
        errorText = "server is not responding, try again later"
        registerUserLoading = false
    }

    LaunchedEffect(key1 = delayedError) {
        if (!delayedError) return@LaunchedEffect
        delay(1_500)
        isError = true
        password = TextFieldValue()
        confirmPassword = TextFieldValue()
        registerUserLoading = false
        delayedError = false
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registration",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                readOnly = registerUserLoading,
                label = { Text(text = "Username") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                readOnly = registerUserLoading,
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                readOnly = registerUserLoading,
                label = { Text(text = "Confirm password") },
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
            onClick = { onSignUpClicked() },
            modifier = Modifier
                .width(100.dp)
                .height(40.dp),
            enabled = !registerUserLoading,
            content = {
                if (registerUserLoading) {
                    LinearProgressIndicator()
                } else {
                    Text(text = "Sign up")
                }
            }
        )
        ClickableText(
            text = signInString,
            onClick = {
                signInString.getStringAnnotations("sign in", it, it).firstOrNull()?.let {
                    onRegisterSuccess()
                }
            }
        )
    }
}