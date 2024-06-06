package co.devhack.reciperecommendergemini.viewmodels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.devhack.reciperecommendergemini.data.GeminiRepositoryImp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

data class ChatChefUiState(
    val screenState: ScreenState = ScreenState.Empty,
    val errorMessage: String = String(),
    val message: String = String(),
)

class ChatChefViewModel : ViewModel() {

    private var geminiRepository: GeminiRepository = GeminiRepositoryImp()

    private val _uiState = MutableStateFlow(ChatChefUiState(screenState = ScreenState.Empty))
    val uiState: StateFlow<ChatChefUiState> = _uiState

    var uiMessage by mutableStateOf(ChatChefUiState(screenState = ScreenState.Empty))
        private set

    fun initChat(withTools: Boolean) {
        Timber.i("initChat -> withTools: $withTools")
        _uiState.value = _uiState.value.copy(screenState = ScreenState.Loading)
        viewModelScope.launch {
            try {
                geminiRepository.initChat(withTools)
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

    fun sendMessageStream(message: String) {
        _uiState.value = _uiState.value.copy(screenState = ScreenState.Loading)
        viewModelScope.launch {
            try {
                geminiRepository.sendMessageStream(message).collect { modelMessage ->
                    Timber.i("Message: $modelMessage")
                    _uiState.value =
                        _uiState.value.copy(
                            message = modelMessage,
                            screenState = ScreenState.Success,
                        )
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

    fun sendMessage(message: String) {
        uiMessage = uiMessage.copy(screenState = ScreenState.Loading)
        viewModelScope.launch {
            try {
                geminiRepository.sendMessage(message).let { modelMessage ->
                    Timber.i("Message: $modelMessage")
                    uiMessage =
                        uiMessage.copy(
                            message = modelMessage,
                            screenState = ScreenState.Success,
                        )
                }
            } catch (e: Exception) {
                Timber.e("Exception: ${e.message}")
                uiMessage =
                    uiMessage.copy(
                        screenState = ScreenState.Error,
                        errorMessage = e.message.toString()
                    )
            }
        }
    }
}
