package co.devhack.reciperecommendergemini.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import co.devhack.reciperecommendergemini.ui.activities.NavTopBar
import co.devhack.reciperecommendergemini.ui.activities.RecipeScreens
import co.devhack.reciperecommendergemini.ui.theme.RecipeRecommenderGeminiTheme


@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    onClickScreen: (RecipeScreens) -> Unit
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            NavTopBar(
                title = "Recipes AI",
                canNavigateBack = false,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight(),
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    onClick = { onClickScreen(RecipeScreens.RECIPE_INPUT) }) {
                    Text(
                        text = "Getting the best recipes",
                        textAlign = TextAlign.Center,
                        style = typography.headlineSmall,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    onClick = { onClickScreen(RecipeScreens.CHAT_CHEF_STREAM) }) {
                    Text(
                        text = "Chatting with a chef",
                        textAlign = TextAlign.Center,
                        style = typography.headlineSmall,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight(),
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    onClick = { onClickScreen(RecipeScreens.CHAT_CHEF) }) {
                    Text(
                        text = "Chatting with a chef and your data",
                        textAlign = TextAlign.Center,
                        style = typography.headlineSmall,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    shape = MaterialTheme.shapes.extraSmall,
                    onClick = { onClickScreen(RecipeScreens.VIDEO_SUMMARY) }) {
                    Text(
                        text = "Getting a summary of your favorite recipe videos",
                        textAlign = TextAlign.Center,
                        style = typography.headlineSmall,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    RecipeRecommenderGeminiTheme {
        MenuScreen {
            println("MenuScreenPreview $it")
        }
    }
}
