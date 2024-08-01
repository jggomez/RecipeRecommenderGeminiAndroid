package co.devhack.reciperecommendergemini.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.devhack.RecipeRecommenderGemini.R
import co.devhack.reciperecommendergemini.ui.activities.NavTopBar
import co.devhack.reciperecommendergemini.ui.theme.RecipeRecommenderGeminiTheme
import co.devhack.reciperecommendergemini.viewmodels.ChatChefUiState
import co.devhack.reciperecommendergemini.viewmodels.ChatChefViewModel
import co.devhack.reciperecommendergemini.viewmodels.domain.ScreenState

data class Message(
    val text: String,
    val isUser: Boolean,
)

@Composable
fun ChatChefStreamScreen(
    modifier: Modifier = Modifier,
    chatChefViewModel: ChatChefViewModel? = null,
) {
    val messages = remember { mutableStateListOf<Message>() }

    LaunchedEffect(Unit) {
        chatChefViewModel?.initChat(false)
    }

    val uiState = chatChefViewModel?.uiState?.collectAsStateWithLifecycle(ChatChefUiState())
    val message = uiState?.value?.message

    if (message?.isNotBlank() == true) {
        messages.add(Message(message, false))
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            NavTopBar(
                title = "Chatting with a chef",
                canNavigateBack = false,
            )
        }
    ) { paddingValues ->
        ChatChefBody(
            modifier = Modifier.padding(paddingValues),
            screenState = uiState?.value?.screenState ?: ScreenState.Empty,
            messages = messages,
        ) {
            messages.add(Message(it, true))
            chatChefViewModel?.sendMessageStream(it)
        }
    }
}

@Composable
fun ChatChefScreen(
    modifier: Modifier = Modifier,
    chatChefViewModel: ChatChefViewModel? = null,
) {
    val messages = remember { mutableStateListOf<Message>() }

    LaunchedEffect(Unit) {
        chatChefViewModel?.initChat(true)
    }

    val uiState = chatChefViewModel?.uiMessage

    when (uiState?.screenState == ScreenState.Success) {
        true -> {
            if (uiState?.message?.isNotEmpty() == true) {
                messages.add(Message(uiState.message, false))
            }
        }

        false -> {}
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            NavTopBar(
                title = "Chatting with a chef and your data",
                canNavigateBack = false,
            )
        }
    ) { paddingValues ->
        ChatChefBody(
            modifier = Modifier.padding(paddingValues),
            screenState = uiState?.screenState ?: ScreenState.Empty,
            messages = messages,
        ) {
            messages.add(Message(it, true))
            chatChefViewModel?.sendMessage(it)
        }
    }
}

@Composable
fun ChatChefBody(
    modifier: Modifier = Modifier,
    screenState: ScreenState = ScreenState.Empty,
    errorMessage: String = String(),
    messages: List<Message> = emptyList(),
    onClick: (text: String) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .height(600.dp)
                .verticalScroll(scrollState)
        ) {
            Messages(
                messages = messages,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            Spacer(modifier = Modifier.width(16.dp))
            InputUserMessage(
                screenState = screenState,
            ) {
                onClick(it)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        when {
            errorMessage.isBlank() -> {
                Spacer(modifier = Modifier.height(16.dp))
            }

            else -> Text(
                text = errorMessage,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun InputUserMessage(
    screenState: ScreenState,
    modifier: Modifier = Modifier,
    onClick: (text: String) -> Unit = {},
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier.weight(0.9f),
            label = { Text(text = "Write your message") },
            value = text,
            onValueChange = { text = it },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
            ),
        )
        IconButton(
            modifier = Modifier
                .weight(0.1f)
                .padding(start = 8.dp),
            onClick = {
                onClick(text)
                text = ""
            }) {
            when (screenState == ScreenState.Loading) {
                false -> Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )

                true -> CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

@Composable
fun Messages(
    modifier: Modifier = Modifier,
    messages: List<Message> = emptyList(),
) {
    messages.forEach { message ->
        val backgroundColor = if (message.isUser) {
            MaterialTheme.colorScheme.tertiaryContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        }

        val bubbleShape = if (message.isUser) {
            RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
        } else {
            RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
        }

        val horizontalAlignment = if (message.isUser) {
            Alignment.End
        } else {
            Alignment.Start
        }

        val author = if (message.isUser) {
            stringResource(R.string.user_label)
        } else {
            stringResource(R.string.model_label)
        }

        Column(
            horizontalAlignment = horizontalAlignment,
            modifier = modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = author,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row {
                BoxWithConstraints {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        shape = bubbleShape,
                        modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
                    ) {
                        Text(
                            text = message.text,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatChefBodyPreview() {
    RecipeRecommenderGeminiTheme(darkTheme = true) {
        ChatChefBody(
            messages = listOf(
                Message("Hello", false),
                Message("Hi", true),
                Message("How are you?", false),
                Message("I'm fine", true),
                Message("I'm good", false),
                Message("How about you?", true),
                Message("I'm good too", false),
            ),
        )
    }
}
