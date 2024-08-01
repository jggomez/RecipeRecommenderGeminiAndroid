package co.devhack.reciperecommendergemini.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.devhack.reciperecommendergemini.data.GeminiRepositoryImp
import co.devhack.reciperecommendergemini.viewmodels.domain.ScreenState
import co.devhack.reciperecommendergemini.viewmodels.repositories.GeminiRepository
import kotlinx.coroutines.launch
import timber.log.Timber

data class SummaryVideoUiState(
    val screenState: ScreenState = ScreenState.Empty,
    val errorMessage: String = String(),
    val summary: String = String(),
    val tokens: Int = 0,
)

class SummaryVideoViewModel : ViewModel() {

    private var geminiRepository: GeminiRepository = GeminiRepositoryImp()
    var uiState by mutableStateOf(SummaryVideoUiState(screenState = ScreenState.Empty))
        private set

    fun getSummary(videoUrl: String, textPrompt: String) {
        uiState = uiState.copy(screenState = ScreenState.Loading)
        viewModelScope.launch {
            try {
                val tokens = geminiRepository.getCountTokens(videoUrl, textPrompt)
                val summary = geminiRepository.getSummaryVideo(videoUrl, textPrompt)
                Timber.i("Summary: $summary")
                uiState =
                    uiState.copy(
                        summary = summary,
                        tokens = tokens,
                        screenState = ScreenState.Success,
                    )
            } catch (e: Exception) {
                Timber.e("Exception: ${e.message}")
                uiState =
                    uiState.copy(
                        screenState = ScreenState.Error,
                        errorMessage = e.message.toString()
                    )
            }
        }
    }
}
