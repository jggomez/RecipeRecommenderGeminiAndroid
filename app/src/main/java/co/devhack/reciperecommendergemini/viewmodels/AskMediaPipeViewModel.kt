package co.devhack.reciperecommendergemini.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.devhack.reciperecommendergemini.data.GeminiRepositoryImp
import co.devhack.reciperecommendergemini.viewmodels.domain.ScreenState
import co.devhack.reciperecommendergemini.viewmodels.repositories.GeminiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

data class AskLlmMediaPipeUiState(
    val screenState: ScreenState = ScreenState.Empty,
    val errorMessage: String = String(),
    val message: String = String(),
)

class AskMediaPipeViewModel : ViewModel() {

    private var geminiRepository: GeminiRepository = GeminiRepositoryImp()

    private val _uiState =
        MutableStateFlow(AskLlmMediaPipeUiState(screenState = ScreenState.Empty))

    val uiState: StateFlow<AskLlmMediaPipeUiState> = _uiState

    fun initModel(context: Context) {
        _uiState.value = _uiState.value.copy(screenState = ScreenState.Loading)
        viewModelScope.launch {
            try {
                geminiRepository.initLlmMediaPipe(context)
                _uiState.value =
                    _uiState.value.copy(
                        screenState = ScreenState.Success,
                    )
            } catch (e: Exception) {
                Timber.e("Exception: ${e.message}")
                _uiState.value =
                    _uiState.value.copy(
                        screenState = ScreenState.Error,
                        errorMessage = e.message.toString()
                    )
            }
        }
    }

    fun sendMessageLlmMediaPipe(prompt: String) {
        _uiState.value = _uiState.value.copy(screenState = ScreenState.Loading)
        viewModelScope.launch {
            try {
                geminiRepository.sendMessageLlmMediaPipe(prompt)
            } catch (e: Exception) {
                Timber.e("Exception: ${e.message}")
                _uiState.value =
                    _uiState.value.copy(
                        screenState = ScreenState.Error,
                        errorMessage = e.message.toString()
                    )
            }
        }
    }

    fun getResultLlmMediaPipe() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                geminiRepository.resultLlmMediaPipe()
                    .collect { message ->
                        val partText = message.first.trim().trimMargin()
                        val done = message.second
                        Timber.i("message viewmodel: $message")
                        if (done.not()) {
                            Timber.i("partText viewmodel: $partText")
                            _uiState.value =
                                _uiState.value.copy(
                                    screenState = ScreenState.Loading,
                                    message = partText
                                )
                        } else {
                            _uiState.value =
                                _uiState.value.copy(
                                    screenState = ScreenState.Success,
                                )
                        }
                    }
            } catch (e: Exception) {
                Timber.e("Exception: ${e.message}")
                _uiState.value =
                    _uiState.value.copy(
                        screenState = ScreenState.Error,
                        errorMessage = e.message.toString()
                    )
            }
        }
    }
}
