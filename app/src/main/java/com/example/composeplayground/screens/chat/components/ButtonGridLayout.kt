package com.example.composeplayground.screens.chat.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeplayground.data.response.Button
import com.google.accompanist.flowlayout.FlowRow


@Composable
fun ButtonGridLayout(
    buttons: List<Button>,
    isEnabled: Boolean = true,
    onClick: (button: Button) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .wrapContentHeight()
    ) {
        buttons.forEach { button ->
            OutlinedButton(
                modifier = Modifier
                    .padding(8.dp),
                onClick = { onClick(button) },
                enabled = isEnabled,
                shape = RoundedCornerShape(30),
                border = BorderStroke(1.dp, MaterialTheme.colors.secondary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colors.onSurface,
                    backgroundColor = MaterialTheme.colors.surface
                )

            ) {
                Text(
                    text = button.value,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }

}


@Composable
@Preview(showBackground = true)
fun ButtonGridLayoutPreview() {
    ButtonGridLayout(
        buttons = listOf(
            Button("1001", "Yes", "Yes", "Instant Expert Help"),
            Button("1001", "Yes", "Yes", "Online Resources"),
            Button("1001", "Yes", "Yes", "Exit Chat"),
            Button("1001", "Yes", "Yes", "Online Resources"),
        ),
        onClick = {}
    )
}