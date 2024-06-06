package co.devhack.reciperecommendergemini.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.devhack.reciperecommendergemini.ui.activities.NavTopBar
import co.devhack.reciperecommendergemini.ui.theme.RecipeRecommenderGeminiTheme
import co.devhack.reciperecommendergemini.viewmodels.ChatChefUiState
import co.devhack.reciperecommendergemini.viewmodels.ChatChefViewModel
import co.devhack.reciperecommendergemini.viewmodels.ScreenState

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

    val uiState = chatChefViewModel?.uiState?.collectAsState(initial = ChatChefUiState())
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
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(0.8f),
            label = { Text(text = "Write your message") },
            value = text,
            onValueChange = { text = it },
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() })
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onClick(text)
                text = ""
            }) {
            when (screenState == ScreenState.Loading) {
                false -> Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth()
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
        if (message.isUser) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(
                        Color.LightGray,
                        shape = RoundedCornerShape(3.dp)
                    ),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = message.text,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
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
