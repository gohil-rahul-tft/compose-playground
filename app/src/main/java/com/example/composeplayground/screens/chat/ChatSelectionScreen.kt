package com.example.composeplayground.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.composeplayground.navigation.ROUTE_CHAT
import com.example.composeplayground.navigation.ROUTE_EXPERT_CHAT
import com.example.composeplayground.utils.Constants
import kotlinx.coroutines.launch

@Composable
fun ChatSelectionScreen(navController: NavController) {

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(onClick = {

            scope.launch {
                navController.navigate(
                    "$ROUTE_CHAT?senderId=${Constants.USER_ID}&receiverId=${Constants.EXPERT_ID}"
                )
            }
        }) {
            Text(text = "Go with Asker ID - ${Constants.USER_ID}")
        }

        Button(onClick = {
            scope.launch {
                navController.navigate(
                    "$ROUTE_CHAT?senderId=${Constants.EXPERT_ID}&receiverId=${Constants.USER_ID}"
                )
            }
        }) {
            Text(text = "Go with Expert ID - ${Constants.EXPERT_ID}")
        }

        Button(onClick = {
            scope.launch {
                navController.navigate(
                    "$ROUTE_EXPERT_CHAT?senderId=${Constants.EXPERT_ID}&receiverId=${Constants.USER_ID}"
                )
            }
        }) {
            Text(text = "Hey Expert, Chat with User AT - ${Constants.USER_ID}")
        }

        Button(onClick = {
            scope.launch {
                navController.navigate(
                    "$ROUTE_EXPERT_CHAT?senderId=${Constants.USER_ID}&receiverId=${Constants.EXPERT_ID}"
                )
            }
        }) {
            Text(text = "Hey User, Chat with Expert AT - ${Constants.EXPERT_ID}")
        }

    }
}


@Composable
@Preview(showBackground = true)
fun ChatSelectionScreenPreview() {
    ChatSelectionScreen(rememberNavController())
}

