package com.example.composeplayground

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.composeplayground.navigation.AppNavHost
import com.example.composeplayground.services.ExpertChatService
import com.example.composeplayground.ui.theme.ComposePlaygroundTheme
import com.example.composeplayground.view_models.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComposeActivity : ComponentActivity() {

    private val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel by viewModels<LoginViewModel>()

        manageService(action = "start")


        setContent {
            ComposePlaygroundTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(viewModel = viewModel)
                }
            }
        }

       /* Handler(Looper.getMainLooper()).postDelayed({
            Intent(context, ComposeXmlActivity::class.java).also {
                startActivity(it)
            }
        }, 5000)*/
    }


    private fun manageService(action: String) {
        Intent(context, ExpertChatService::class.java).also {
            it.action = action

            when (action) {
                "start" -> startService(it)
                "stop" -> stopService(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        manageService(action = "stop")
    }
}
