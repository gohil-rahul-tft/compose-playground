package com.example.composeplayground.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.composeplayground.navigation.ROUTE_HOME
import com.example.composeplayground.navigation.ROUTE_LOGIN
import com.example.composeplayground.utils.normalText
import com.example.composeplayground.utils.toast
import com.example.composeplayground.view_models.LoginViewModel
import com.example.composeplayground.view_models.UIState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel? = null,
    navController: NavHostController
) {

    viewModel!!

    val username = viewModel.username
    val password = viewModel.password
    var passwordVisibility: Boolean by rememberSaveable { mutableStateOf(false) }
    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login Screen",
            fontSize = MaterialTheme.typography.h4.fontSize
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = { Text(text = "Username") },
            value = username,
            onValueChange = { viewModel.updateUsername(it.normalText()) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = { Text(text = "Password") },
            value = password,
            onValueChange = { viewModel.updatePassword(it.normalText()) },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(imageVector = Icons.Filled.Key, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)
        ) {
            val context = LocalContext.current
            uiState.value.let {
                when (it) {

                    is UIState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is UIState.Success -> {
                        LaunchedEffect(Unit) {
                            context.toast(it.message)
                            navController.navigate(ROUTE_HOME) {
                                popUpTo(ROUTE_LOGIN) { inclusive = true }
                            }
                        }
                    }

                    is UIState.Failure -> {
                        context.toast(it.message)
                        LoginButton {
                            viewModel.validateUser(username, password)
                        }
                    }

                    else -> LoginButton {
                        viewModel.validateUser(username, password)
                    }
                }
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        ClickableText(
            text = AnnotatedString("Forgot password?"),
            onClick = { },
            style = TextStyle(
                fontSize = MaterialTheme.typography.subtitle2.fontSize,
                color = MaterialTheme.colors.onSurface
            ),
        )
    }
}


@Composable
fun LoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(text = "Login".uppercase())
    }
}

@Composable
@Preview(showBackground = true)
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}