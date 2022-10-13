package com.example.composeplayground

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.composeplayground.databinding.LayoutForCompose2Binding
import com.example.composeplayground.screens.DialogScreen
import com.example.composeplayground.services.CoinbaseService

class ComposeXmlActivity : AppCompatActivity() {

    private lateinit var binding: LayoutForCompose2Binding
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_ComposePlayground)
        binding = LayoutForCompose2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {

        binding.button.setOnClickListener {
            binding.tvTitle.text = "Edited From Compose!"
        }

        binding.composeView.setContent {
            DialogScreen()
        }


        Handler(Looper.getMainLooper()).postDelayed({
            manageService("stop")
        }, 5000)
    }

    private fun manageService(action: String) {
        Intent(context, CoinbaseService::class.java).also {
            it.action = action

            when (action) {
                "start" -> startService(it)
                "stop" -> stopService(it)
            }
        }
    }

}