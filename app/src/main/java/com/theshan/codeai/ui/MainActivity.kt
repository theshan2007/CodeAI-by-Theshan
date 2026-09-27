package com.theshan.codeai.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.theshan.codeai.R
import com.theshan.codeai.databinding.ActivityMainBinding
import com.theshan.codeai.ui.adapter.ChatAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var apiKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check for API key
        val prefs = getSharedPreferences("codeai_prefs", Context.MODE_PRIVATE)
        apiKey = prefs.getString("gemini_api_key", "") ?: ""

        if (apiKey.isEmpty()) {
            startActivity(Intent(this, ApiKeyActivity::class.java))
            finish()
            return
        }

        setupRecyclerView()
        setupModeChips()
        setupSendButton()
        setupSettingsButton()
        observeViewModel()

        // Show welcome message
        showWelcomeMessage()
    }

    private fun showWelcomeMessage() {
        val welcomeMsg = ChatMessage(
            userMessage = "👋 Hello! I'm ready to help you!",
            aiResponse = getString(R.string.welcome_message),
            isLoading = false,
            hasCode = false
        )
        chatAdapter.addMessage(welcomeMsg)
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.recyclerChat.apply {
            this.layoutManager = layoutManager
            adapter = chatAdapter
        }
    }

    private fun setupModeChips() {
        binding.modeChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            viewModel.currentMode = when {
                checkedIds.contains(R.id.chipAndroid) -> "android"
                checkedIds.contains(R.id.chipWeb) -> "web"
                checkedIds.contains(R.id.chipPython) -> "python"
                checkedIds.contains(R.id.chipGeneral) -> "chat"
                else -> "android"
            }
            updateHint()
        }
    }

    private fun updateHint() {
        val hint = when (viewModel.currentMode) {
            "android" -> "Describe your Android app idea..."
            "web" -> "Describe your website or web app..."
            "python" -> "Describe your Python script or app..."
            else -> "Ask me anything..."
        }
        binding.etPrompt.hint = hint
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val prompt = binding.etPrompt.text?.toString()?.trim() ?: ""
            if (prompt.isNotEmpty()) {
                binding.etPrompt.setText("")
                hideKeyboard()
                viewModel.sendMessage(apiKey, prompt)
            } else {
                Toast.makeText(this, "Please enter a prompt", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSettingsButton() {
        binding.btnSettings.setOnClickListener {
            // Show options dialog
            val options = arrayOf("🔑 Change API Key", "🗑️ Clear Chat", "ℹ️ About")
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Settings")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> {
                            // Clear key and restart
                            getSharedPreferences("codeai_prefs", Context.MODE_PRIVATE)
                                .edit().remove("gemini_api_key").apply()
                            startActivity(Intent(this, ApiKeyActivity::class.java))
                            finish()
                        }
                        1 -> {
                            viewModel.clearChat()
                            showWelcomeMessage()
                        }
                        2 -> {
                            androidx.appcompat.app.AlertDialog.Builder(this)
                                .setTitle("CodeAI by Theshan")
                                .setMessage("Version 1.0\n\nAn AI-powered code generator built with:\n• Google Gemini AI\n• Kotlin & Android\n\nMade with ❤️ by Theshan")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.chatMessages.observe(this) { messages ->
            chatAdapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.recyclerChat.scrollToPosition(messages.size - 1)
            }
        }

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSend.isEnabled = false
                }
                is UiState.Success, is UiState.Error, is UiState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSend.isEnabled = true
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}
