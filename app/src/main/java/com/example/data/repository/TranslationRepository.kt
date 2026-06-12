package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.GeminiApiService
import com.example.data.api.GeminiRequest
import com.example.data.api.RequestContent
import com.example.data.api.RequestPart
import com.example.data.local.TranslationDao
import com.example.data.local.TranslationEntity
import kotlinx.coroutines.flow.Flow

class TranslationRepository(
    private val translationDao: TranslationDao,
    private val apiService: GeminiApiService
) {
    val allTranslations: Flow<List<TranslationEntity>> = translationDao.getAllTranslations()

    suspend fun translateCode(
        sourceLanguage: String,
        targetLanguage: String,
        sourceCode: String,
        addComments: Boolean,
        addExplanation: Boolean
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "YOUR_GEMINI_API_KEY") {
            throw IllegalStateException("Gemini API key is not configured in BuildConfig")
        }

        val prompt = buildPrompt(sourceLanguage, targetLanguage, sourceCode, addComments, addExplanation)
        val request = GeminiRequest(
            contents = listOf(
                RequestContent(
                    parts = listOf(
                        RequestPart(text = prompt)
                    )
                )
            )
        )

        val response = apiService.generateContent(apiKey, request)
        val resultText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw Exception("Empty or invalid response received from Gemini API")

        // Save to database
        val translationEntity = TranslationEntity(
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            sourceCode = sourceCode,
            translatedCode = resultText
        )
        translationDao.insertTranslation(translationEntity)

        return resultText
    }

    private fun buildPrompt(
        sourceLanguage: String,
        targetLanguage: String,
        sourceCode: String,
        addComments: Boolean,
        addExplanation: Boolean
    ): String {
        val sourceInstruction = if (sourceLanguage == "Auto-detect") "" else "written in $sourceLanguage"
        val commentInstruction = if (addComments) "Add helpful comments inline in the translated code explaining complex parts." else "Keep code comments minimal or preserve original comments."
        val explanationInstruction = if (addExplanation) "Following the code, provide a block in the same tone describing: 1. Main logic changes, 2. Performance or architectural considerations between the two languages." else "Do not output any explanation beyond the translated code."

        return """
            You are an expert polyglot programmer and software architect.
            Translate the following source code $sourceInstruction to $targetLanguage.
            
            Strict requirements:
            - Respect the syntax, structures, idiomatic practices, and conventions of the target language ($targetLanguage).
            - Ensure the code translations are functionally identical, syntax-error-free, and compiles.
            - $commentInstruction
            - $explanationInstruction
            - Return the code wrapped inside Markdown code blocks. If you are outputting code mixed with explanation, clearly format the code block first, then the explanation below it.
            
            Source code to translate:
            ```
            $sourceCode
            ```
        """.trimIndent()
    }

    suspend fun deleteTranslation(id: Int) {
        translationDao.deleteTranslationById(id)
    }

    suspend fun clearAllHistory() {
        translationDao.clearAllTranslations()
    }
}
