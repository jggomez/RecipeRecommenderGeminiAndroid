package co.devhack.reciperecommendergemini.ui.screens

import android.annotation.SuppressLint
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.ui.PlayerView
import co.devhack.reciperecommendergemini.ui.theme.RecipeRecommenderGeminiTheme
import co.devhack.reciperecommendergemini.viewmodels.domain.ScreenState
import co.devhack.reciperecommendergemini.viewmodels.SummaryVideoViewModel


const val VIDEO_URL_PROMPT =
    "gs://devhack-3f0c2.appspot.com/colombianfood.mp4"
const val PROMPT = "Please give a largeness summary of this video:"
const val VIDEO_URL =
    "https://firebasestorage.googleapis.com/v0/b/devhack-3f0c2.appspot.com/o/colombianfood.mp4?alt=media&token=bb22ab01-5e9d-48e3-8d67-7bce65659bb0"

@Composable
fun SummaryScreen(
    modifier: Modifier = Modifier,
    summaryVideoViewModel: SummaryVideoViewModel? = null,
) {

    val summary = remember { mutableStateOf("") }

    val uiState = summaryVideoViewModel?.uiState

    if (uiState?.screenState == ScreenState.Success) {
        summary.value = uiState.summary
    }

    SummaryBody(
        modifier = modifier,
        summary = summary.value,
        tokens = uiState?.tokens ?: 0,
        screenState = uiState?.screenState ?: ScreenState.Empty,
    ) {
        summary.value = ""
        summaryVideoViewModel?.getSummary(
            VIDEO_URL_PROMPT,
            PROMPT
        )
    }
}

@Composable
fun SummaryBody(
    summary: String,
    tokens: Int,
    screenState: ScreenState,
    modifier: Modifier = Modifier,
    onClickSummary: () -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = modifier
                .height(600.dp)
                .verticalScroll(scrollState)
        ) {
            VideoHeader(screenState = screenState, onClickSummary = onClickSummary)
            Spacer(modifier = Modifier.height(30.dp))
            Text(modifier = Modifier.fillMaxWidth(), text = "Amount of tokens used: $tokens")
            Spacer(modifier = Modifier.height(30.dp))
            Text(modifier = Modifier.fillMaxWidth(), text = summary)
        }
    }
}

@Composable
fun VideoHeader(
    modifier: Modifier = Modifier,
    screenState: ScreenState,
    onClickSummary: () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = PROMPT, modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(30.dp))
        VideoPlayer()
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClickSummary
        ) {
            when (screenState == ScreenState.Loading) {
                false -> Text(text = "Summarize")

                true -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

@SuppressLint("OpaqueUnitKey")
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer() {
    val context = LocalContext.current
    val player = SimpleExoPlayer.Builder(context).build()
    val playerView = PlayerView(context)
    val mediaItem = MediaItem.fromUri(VIDEO_URL)
    val playWhenReady = remember {
        mutableStateOf(true)
    }
    player.setMediaItem(mediaItem)
    playerView.player = player
    LaunchedEffect(player) {
        player.prepare()
        player.playWhenReady = playWhenReady.value
    }
    DisposableEffect(
        AndroidView(modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
            factory = {
                playerView
            })
    ) {
        onDispose { player.release() }
    }
}

@Preview(showBackground = true)
@Composable
fun SummaryBodyPreview() {
    RecipeRecommenderGeminiTheme(darkTheme = true) {
        SummaryBody(
            summary = "This is a test summary",
            screenState = ScreenState.Empty,
            tokens = 1000
        )
    }
}
