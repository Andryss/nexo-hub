package org.vivlaniv.nexohub.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import org.vivlaniv.nexohub.AppState

@Composable
fun MainPage(appState: AppState, navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!appState.mqttConnected.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(navController = navController)
                }, content = { padding ->
                    NavHostContainer(
                        state = appState,
                        navController = navController,
                        padding = padding
                    )
                }
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = (currentRoute == "home"),
            onClick = {
                navController.navigate("home")
            }, icon = {
                Icon(imageVector = Icons.Filled.Home, contentDescription = "Home")
            }, label = {
                Text(text = "Home")
            }
        )
        NavigationBarItem(
            selected = (currentRoute == "search"),
            onClick = {
                navController.navigate("search")
            }, icon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
            }, label = {
                Text(text = "Search")
            }
        )
    }
}

@Composable
fun NavHostContainer(
    state: AppState,
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(paddingValues = padding),
        builder = {
            composable("home") {
                HomePage(state = state)
            }
            composable("search") {
                SearchPage(state = state, navController = navController)
            }
            composable("save/{device}") { backStackEntry ->
                val device = backStackEntry.arguments?.getString("device")
                if (device != null) {
                    SavePage(state = state, navController = navController, device = device)
                }
            }
        }
    )
}