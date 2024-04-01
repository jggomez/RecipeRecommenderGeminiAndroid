package co.devhack.reciperecommendergemini.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.devhack.reciperecommendergemini.data.GeminiRepositoryImp
import kotlinx.coroutines.launch

data class RecipeUiState(
    val screenState: ScreenState = ScreenState.Empty,
    val errorMessage: String = String(),
    val recipes: List<Recipe> = emptyList(),
)

data class Recipes(
    val recipes: List<Recipe>
)

class RecipeViewModel : ViewModel() {

    private var geminiRepository: GeminiRepository = GeminiRepositoryImp()

    var uiState by mutableStateOf(RecipeUiState(screenState = ScreenState.Empty))
        private set

    fun getRecipes(
        recipeType: String,
        region: String,
        ingredients: List<String>,
        language: String,
        imagePath: String,
    ) {
        uiState = uiState.copy(screenState = ScreenState.Loading)
        viewModelScope.launch {
            try {

                val recipes = geminiRepository.getRecipes(
                    recipeType,
                    region,
                    ingredients,
                    language,
                    imagePath
                )
                uiState =
                    uiState.copy(
                        screenState = ScreenState.Success,
                        recipes = recipes,
                    )
            } catch (e: Exception) {
                uiState =
                    uiState.copy(
                        screenState = ScreenState.Error,
                        errorMessage = e.message.toString()
                    )
            }
        }
    }
}
