package com.theshan.codeai.data.api

import com.google.gson.annotations.SerializedName

// Request Models
data class GeminiRequest(
    val contents: List<Content>,
    @SerializedName("generationConfig")
    val generationConfig: GenerationConfig = GenerationConfig()
)

data class Content(
    val role: String = "user",
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GenerationConfig(
    val temperature: Float = 0.7f,
    @SerializedName("maxOutputTokens")
    val maxOutputTokens: Int = 8192,
    @SerializedName("topP")
    val topP: Float = 0.95f
)

// Response Models
data class GeminiResponse(
    val candidates: List<Candidate>?,
    val error: GeminiError?
)

data class Candidate(
    val content: ContentResponse?,
    @SerializedName("finishReason")
    val finishReason: String?
)

data class ContentResponse(
    val parts: List<PartResponse>?,
    val role: String?
)

data class PartResponse(
    val text: String?
)

data class GeminiError(
    val code: Int,
    val message: String,
    val status: String
)
