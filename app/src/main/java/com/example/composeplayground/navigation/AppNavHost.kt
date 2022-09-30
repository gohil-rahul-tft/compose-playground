package com.example.composeplayground.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeplayground.screens.ChatScreen
import com.example.composeplayground.screens.ComposeInXML
import com.example.composeplayground.screens.DialogScreen
import com.example.composeplayground.screens.HomeScreen
import com.example.composeplayground.screens.LoginScreen
import com.example.composeplayground.screens.ProgressbarScreen
import com.example.composeplayground.screens.SocketSampleScreen
import com.example.composeplayground.screens.ValidationScreen
import com.example.composeplayground.screens.XMLInCompose
import com.example.composeplayground.view_models.LoginViewModel

@Composable
fun AppNavHost(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = SOCKET_SCREEN
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

        composable(VALIDATION_SCREEN) {
            ValidationScreen()
        }

        composable(XML_IN_COMPOSE_SCREEN) {
            XMLInCompose()
        }

        composable(COMPOSE_IN_XML_SCREEN) {
            ComposeInXML()
        }

        composable(SOCKET_SCREEN) {
            SocketSampleScreen()
        }
    }
}