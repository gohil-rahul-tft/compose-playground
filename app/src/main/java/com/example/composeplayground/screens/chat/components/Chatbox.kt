package com.example.composeplayground.screens.chat.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ChatBoxEditText(
    message: String,
    onChange: (message: String) -> Unit,
    onSend: (message: String) -> Unit,
) {

    Row {
        TextField(
            value = message,
            onValueChange = { onChange(it) },
            modifier = Modifier.weight(1f),
            placeholder = { Text(text = "Type your message...") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions { onSend(message) },
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.onSurface,
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        Button(
            onClick = { onSend(message) },
            enabled = true,
            modifier = Modifier.height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun ChatBoxEditTextPreview() {
    ChatBoxEditText(message = "Amazing World!", onChange = {}, onSend = {})
}