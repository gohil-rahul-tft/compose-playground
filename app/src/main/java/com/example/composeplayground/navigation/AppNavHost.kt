package com.example.composeplayground.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeplayground.screens.ChatScreen
import com.example.composeplayground.screens.DialogScreen
import com.example.composeplayground.screens.HomeScreen
import com.example.composeplayground.screens.LoginScreen
import com.example.composeplayground.screens.ProgressbarScreen
import com.example.composeplayground.view_models.LoginViewModel

@Composable
fun AppNavHost(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_CHAT
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(viewModel, navController)
        }

        composable(ROUTE_HOME) {
            HomeScreen(viewModel, navController)
        }

        composable(ROUTE_CHAT) {
            ChatScreen()
        }

        composable(DIALOG_SCREEN) {
            DialogScreen()
        }

        composable(PROGRESSBAR_SCREEN) {
            ProgressbarScreen()
        }
    }
}