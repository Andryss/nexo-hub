package org.vivlaniv.nexohub.mobile.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTopAppBar(
    user: String,
    onSignOut: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = user,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
        },
        actions = {
            Button(
                onClick = { onSignOut() },
                content = {
                    Text(text = "Sign out")
                }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    )
}