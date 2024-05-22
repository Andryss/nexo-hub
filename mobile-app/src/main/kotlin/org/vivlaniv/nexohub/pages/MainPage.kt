package org.vivlaniv.nexohub.pages

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.vivlaniv.nexohub.AppState

enum class Routes(val route: String) {
    SIGN_IN("signIn"),
    SIGN_UP("signUp"),
    HOME("home"),
    SEARCH("search")
}

@Composable
fun MainPage(state: AppState, navController: NavHostController) {

    fun navigateTo(route: Routes): () -> Unit = { navController.navigate(route.route) }

    fun navigateBack() {
        navController.navigateUp()
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SIGN_IN.route,
        builder = {
            composable(Routes.SIGN_IN.route) {
                AuthPage(
                    state = state,
                    onAuthSuccess = navigateTo(Routes.HOME),
                    navigateToRegisterPage = navigateTo(Routes.SIGN_UP)
                )
            }
            composable(Routes.SIGN_UP.route) {
                RegisterPage(state = state, onRegisterSuccess = ::navigateBack)
            }
            composable(Routes.HOME.route) {
                HomePage(state = state, onSignOut = ::navigateBack, navigateToSearchPage = navigateTo(Routes.SEARCH))
            }
            composable(Routes.SEARCH.route) {
                SearchPage(state = state, navigateBack = ::navigateBack)
            }
        }
    )
}