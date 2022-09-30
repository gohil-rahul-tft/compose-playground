package com.example.composeplayground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.composeplayground.databinding.LayoutForCompose2Binding
import com.example.composeplayground.screens.DialogScreen

class ComposeXmlActivity : AppCompatActivity() {

    private lateinit var binding: LayoutForCompose2Binding

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
    }
}