package co.devhack.reciperecommendergemini.ui.screens

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
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.devhack.reciperecommendergemini.ui.activities.NavTopBar
import co.devhack.reciperecommendergemini.viewmodels.AskLlmMediaPipeUiState
import co.devhack.reciperecommendergemini.viewmodels.AskMediaPipeViewModel
import co.devhack.reciperecommendergemini.viewmodels.domain.ScreenState


@Composable
fun AskLlmMediaPipeScreen(
    modifier: Modifier = Modifier,
    askMediaPipeViewModel: AskMediaPipeViewModel? = null,
) {
    val context = LocalContext.current

    var question by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    var lastMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        askMediaPipeViewModel?.initModel(context = context)
        askMediaPipeViewModel?.getResultLlmMediaPipe()
    }

    val uiState =
        askMediaPipeViewModel?.uiState?.collectAsStateWithLifecycle(AskLlmMediaPipeUiState())

    when (uiState?.value?.screenState) {
        ScreenState.Loading -> {
            val message = uiState.value.message
            if (lastMessage != message) {
                response += when {
                    lastMessage.isEmpty() -> message.replaceFirstChar { it.uppercase() }
                    message.startsWith("**", ignoreCase = true) -> "\n$message"
                    message.startsWith("-**") -> "\n$message"
                    message.startsWith("-") -> "\n$message"
                    message.endsWith("**") -> "$message\n"
                    else -> " $message"
                }
                lastMessage = message
            }
        }

        else -> {}
    }

    AskBody(
        modifier = modifier,
        question = question,
        screenState = uiState?.value?.screenState ?: ScreenState.Empty,
        response = response,
    ) {
        question = it
        response = ""
        askMediaPipeViewModel?.sendMessageLlmMediaPipe(it)
    }
}

@Composable
fun AskBody(
    question: String,
    response: String,
    screenState: ScreenState,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    var text by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        topBar = {
            NavTopBar(
                title = "LLM on Device MediaPipe",
                canNavigateBack = false,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .height(600.dp)
                .verticalScroll(scrollState)
        ) {
            AskInput(
                screenState = screenState,
                text = text,
                textChange = { text = it },
                onClick = {
                    onClick(it)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ExampleOptions{
                text = it
            }
            Spacer(modifier = Modifier.width(16.dp))
            LlmResponse(
                response = response,
                question = question,
            )
        }
    }
}

@Composable
fun ExampleOptions(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    Column(modifier = modifier) {
        Button(onClick = { onClick("Get Mexican Recipes") }) {
            Text(text = "Get Mexican Recipes")
        }
        Spacer(modifier = Modifier.height(6.dp))
        Button(onClick = { onClick("How to cook a pizza") }) {
            Text(text = "How to cook a pizza")
        }
        Spacer(modifier = Modifier.height(6.dp))
        Button(onClick = { onClick("What are the ingredients of a Sancocho Valluno?") }) {
            Text(text = "What are the ingredients of a Sancocho Valluno?")
        }
    }
}

@Composable
fun AskInput(
    screenState: ScreenState,
    text: String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    textChange: (String) -> Unit,
) {

    Row(modifier.fillMaxWidth()) {
        TextField(
            modifier = Modifier.weight(0.9f),
            label = { Text(text = "What do you need?") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
            ),
            value = text,
            onValueChange = textChange)
        IconButton(
            modifier = Modifier
                .weight(0.1f)
                .padding(start = 8.dp),
            onClick = {
                onClick(text)
                textChange("")
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
fun LlmResponse(
    question: String,
    response: String,
    modifier: Modifier = Modifier,
) {
    if (response.isEmpty().not()) {
        Column(modifier = modifier) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Question:",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row {
                BoxWithConstraints {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp),
                        modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
                    ) {
                        Text(
                            text = question,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Response LLM:",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row {
                BoxWithConstraints {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp),
                        modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
                    ) {
                        Text(
                            text = response,
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
fun AskBodyPreview() {
    AskBody(
        question = "Create a poem for my mother",
        response = """lorempsim loremsim loremsim loremsim lorempsim loremsim loremsim loremsim
            lorempsim loremsim loremsim loremsimlorempsim loremsim loremsim loremsim
            lorempsim loremsim loremsim loremsim
                """,
        screenState = ScreenState.Empty
    ) {

    }
}
