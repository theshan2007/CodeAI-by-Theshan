package com.theshan.codeai.data.repository

import com.theshan.codeai.data.api.Content
import com.theshan.codeai.data.api.GeminiApiService
import com.theshan.codeai.data.api.GeminiRequest
import com.theshan.codeai.data.api.Part

sealed class AiResult {
    data class Success(val text: String) : AiResult()
    data class Error(val message: String) : AiResult()
}

class GeminiRepository(private val apiService: GeminiApiService) {

    suspend fun generateCode(
        apiKey: String,
        userPrompt: String,
        mode: String
    ): AiResult {
        return try {
            val systemPrompt = buildSystemPrompt(mode, userPrompt)
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        role = "user",
                        parts = listOf(Part(systemPrompt))
                    )
                )
            )

            val response = apiService.generateContent(
                model = "gemini-1.5-flash",
                apiKey = apiKey,
                request = request
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.error != null) {
                    AiResult.Error("API Error: ${body.error.message}")
                } else {
                    val text = body?.candidates?.firstOrNull()
                        ?.content?.parts?.firstOrNull()?.text
                    if (text != null) {
                        AiResult.Success(text)
                    } else {
                        AiResult.Error("No response from AI")
                    }
                }
            } else {
                AiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            AiResult.Error("Network error: ${e.message}")
        }
    }

    suspend fun chat(
        apiKey: String,
        message: String
    ): AiResult {
        return generateCode(apiKey, message, "chat")
    }

    private fun buildSystemPrompt(mode: String, userPrompt: String): String {
        return when (mode) {
            "android" -> """
                You are an expert Android developer. Generate complete, working Android Kotlin code.
                
                User Request: $userPrompt
                
                Requirements:
                - Generate complete, production-ready Kotlin code
                - Include all necessary imports
                - Add XML layout files if needed
                - Use Material Design 3 components
                - Include clear comments explaining each section
                - Make the UI modern and attractive with dark theme support
                - Structure: First show the main Activity, then layouts, then any helper classes
                
                Format your response with clear sections marked with // === SECTION NAME ===
            """.trimIndent()

            "web" -> """
                You are an expert web developer. Generate complete HTML, CSS, and JavaScript code.
                
                User Request: $userPrompt
                
                Requirements:
                - Generate a complete, single-file or multi-file web app
                - Use modern CSS with animations and responsive design
                - Include all JavaScript functionality
                - Make it visually impressive
                - Add comments explaining key parts
                
                Format: Provide HTML, then CSS in <style>, then JS in <script>
            """.trimIndent()

            "python" -> """
                You are an expert Python developer. Generate complete, working Python code.
                
                User Request: $userPrompt
                
                Requirements:
                - Write clean, Pythonic code following PEP 8
                - Include all necessary imports
                - Add docstrings and comments
                - Handle errors properly
                - Include example usage
            """.trimIndent()

            else -> """
                You are CodeAI, a helpful and intelligent AI assistant created by Theshan.
                You are knowledgeable, friendly, and always provide detailed, accurate responses.
                
                User: $userPrompt
                
                Respond helpfully and clearly. If it involves code, format it properly.
            """.trimIndent()
        }
    }
}
