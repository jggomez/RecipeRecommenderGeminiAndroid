package co.devhack.reciperecommendergemini.viewmodels.domain

import kotlinx.serialization.Serializable

sealed class ScreenState {
    data object Empty : ScreenState()
    data object Success : ScreenState()
    data object Error : ScreenState()
    data object Loading : ScreenState()
}

@Serializable
data class Recipe(
    val name: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val totalCalories: Int,
    val videos: List<String>,
    val references: List<String>
)

@Serializable
data class CountryRecipe(
    val meals: List<Meal>?
)

@Serializable
data class Meal(
    val strMeal: String,
    val strMealThumb: String,
    val idMeal: String,
)
