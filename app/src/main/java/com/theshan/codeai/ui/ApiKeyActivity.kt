package com.theshan.codeai.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.theshan.codeai.databinding.ActivityApiKeyBinding

class ApiKeyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApiKeyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApiKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveKey.setOnClickListener {
            val key = binding.etApiKey.text?.toString()?.trim() ?: ""
            if (key.isEmpty()) {
                Toast.makeText(this, "Please enter your API key", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (key.length < 20) {
                Toast.makeText(this, "Invalid API key format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save to SharedPreferences
            val prefs = getSharedPreferences("codeai_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("gemini_api_key", key).apply()

            Toast.makeText(this, "✅ API Key saved!", Toast.LENGTH_SHORT).show()

            // Go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
