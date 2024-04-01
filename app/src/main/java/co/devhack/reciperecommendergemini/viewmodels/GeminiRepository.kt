package co.devhack.reciperecommendergemini.viewmodels

interface GeminiRepository {
    suspend fun getRecipes(
        recipeType: String,
        region: String,
        ingredients: List<String>,
        language: String,
        imagePath: String,
    ): List<Recipe>
}