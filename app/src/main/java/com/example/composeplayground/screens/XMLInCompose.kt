package com.example.composeplayground.screens

import android.view.LayoutInflater
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.example.composeplayground.databinding.LayoutForComposeBinding

@Composable
fun XMLInCompose() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "This is using Android View",
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.subtitle2
        )

        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                val binding =
                    LayoutForComposeBinding.inflate(LayoutInflater.from(context), null, false)
                binding.button.setOnClickListener {
                    binding.tvTitle.text = "Edited From Compose!"
                }

                binding.root
            }, update = { view ->

            }
        )

        Divider()

        Text(
            text = "This is using Android View Binding",
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
            style = MaterialTheme.typography.subtitle2
        )
        AndroidViewBinding(LayoutForComposeBinding::inflate) {
            button.setOnClickListener {
                tvTitle.text = "Edited From Compose!"
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun XMLInComposePreview() {
    XMLInCompose()
}