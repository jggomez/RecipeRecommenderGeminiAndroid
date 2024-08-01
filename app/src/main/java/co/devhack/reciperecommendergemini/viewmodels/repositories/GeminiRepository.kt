package co.devhack.reciperecommendergemini.viewmodels.repositories

import android.content.Context
import co.devhack.reciperecommendergemini.viewmodels.domain.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface GeminiRepository {
    suspend fun getRecipes(
        recipeType: String,
        region: String,
        ingredients: List<String>,
        language: String,
        photos: List<String>,
    ): List<Recipe>

    suspend fun initChat(withTools: Boolean)

    suspend fun sendMessageStream(userMessage: String): Flow<String>

    suspend fun sendMessage(userMessage: String): String

    suspend fun getSummaryVideo(videoUrl: String, textPrompt: String): String

    suspend fun getCountTokens(videoUrl: String, textPrompt: String): Int

    suspend fun initLlmMediaPipe(context: Context)

    suspend fun sendMessageLlmMediaPipe(message: String)

    suspend fun resultLlmMediaPipe(): SharedFlow<Pair<String, Boolean>>
}
