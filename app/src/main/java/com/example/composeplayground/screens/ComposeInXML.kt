package com.example.composeplayground.screens

import android.content.Intent
import android.view.LayoutInflater
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.example.composeplayground.ComposeXmlActivity
import com.example.composeplayground.databinding.LayoutForComposeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ComposeInXML() {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        scope.launch {
            delay(1000)
            context.startActivity(Intent(context, ComposeXmlActivity::class.java))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ComposeInXMLPreview() {
    ComposeInXML()
}