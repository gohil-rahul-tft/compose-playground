package com.example.composeplayground.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ValidationScreen() {
    var text by rememberSaveable { mutableStateOf("") }
    var isError by rememberSaveable { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                isError = false
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Error,
                    tint = MaterialTheme.colors.error,
                    contentDescription = null
                )
            },
            singleLine = true,
            isError = isError
        )
        Text(
            text = "Error message",
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable()
@Preview(showBackground = true)
fun ValidationScreenPreview() {
    ValidationScreen()
}