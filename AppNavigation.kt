package com.schoolmanagement.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schoolmanagement.app.presentation.auth.AuthViewModel
import com.schoolmanagement.app.ui.screens.AuthScreen
import com.schoolmanagement.app.ui.screens.DashboardScreen

object Routes { const val SETUP = "setup"; const val LOGIN = "login"; const val DASHBOARD = "dashboard" }

@Composable
fun AppNavigation(vm: AuthViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val nav = rememberNavController()

    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    // Auth state drives navigation, so logout/session loss always returns to login.
    LaunchedEffect(state.user, state.needsSetup) {
        val target = when {
            state.user != null -> Routes.DASHBOARD
            state.needsSetup -> Routes.SETUP
            else -> Routes.LOGIN
        }
        nav.navigate(target) { popUpTo(0) { inclusive = true }; launchSingleTop = true }
    }

    NavHost(nav, startDestination = if (state.user != null) Routes.DASHBOARD else if (state.needsSetup) Routes.SETUP else Routes.LOGIN) {
        composable(Routes.SETUP) { AuthScreen(state, true) { u, n, p -> vm.setup(u, n, p) } }
        composable(Routes.LOGIN) { AuthScreen(state, false) { u, _, p -> vm.login(u, p) } }
        composable(Routes.DASHBOARD) {
            state.user?.let { DashboardScreen(it, onLogout = vm::logout) }
        }
    }
}
