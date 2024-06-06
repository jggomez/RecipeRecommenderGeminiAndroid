package co.devhack.reciperecommendergemini.viewmodels

import kotlinx.coroutines.flow.Flow

interface GeminiRepository {
    suspend fun getRecipes(
        recipeType: String,
        region: String,
        ingredients: List<String>,
        language: String,
        imagePath: String,
    ): List<Recipe>

    suspend fun initChat(withTools: Boolean)

    suspend fun sendMessageStream(userMessage: String): Flow<String>

    suspend fun sendMessage(userMessage: String): String

    suspend fun getSummaryVideo(videoUrl: String, textPrompt: String): String

    suspend fun getCountTokens(videoUrl: String, textPrompt: String): Int
}
