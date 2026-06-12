package com.example.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<RequestContent>
)

@JsonClass(generateAdapter = true)
data class RequestContent(
    val parts: List<RequestPart>
)

@JsonClass(generateAdapter = true)
data class RequestPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: ResponseContent? = null,
    val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class ResponseContent(
    val parts: List<ResponsePart>? = null
)

@JsonClass(generateAdapter = true)
data class ResponsePart(
    val text: String? = null
)
