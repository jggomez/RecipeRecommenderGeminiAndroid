package co.devhack.reciperecommendergemini.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.devhack.reciperecommendergemini.ui.theme.RecipeRecommenderGeminiTheme
import co.devhack.reciperecommendergemini.viewmodels.domain.Recipe
import timber.log.Timber

@Composable
fun RecipesScreen(
    recipes: List<Recipe>,
    modifier: Modifier = Modifier,
) {
    Timber.i("Rendering RecipesScreen with -> $recipes")
    var tabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf("Option 1", "Option 2", "Option 3")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ScrollableTabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    icon = {
                        when (index) {
                            0 -> Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null
                            )

                            1 -> Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null
                            )

                            2 -> Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
        when (tabIndex) {
            0 -> RecipeData(recipes[0])
            1 -> RecipeData(recipes[1])
            2 -> RecipeData(recipes[2])
        }
    }
}

@Composable
fun RecipeData(
    recipe: Recipe,
    modifier: Modifier = Modifier,
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
            RowText(
                label = "Name",
                text = recipe.name,
            )
            Spacer(modifier = Modifier.height(16.dp))
            RowText(
                label = "Total Calories",
                text = recipe.totalCalories.toString(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Ingredients")
            Items(
                items = recipe.ingredients
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Instructions")
            Items(
                items = recipe.instructions
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Videos")
            Items(
                items = recipe.videos
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "References")
            Items(
                items = recipe.references
            )
        }
    }
}

@Composable
fun RowText(
    label: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Text(text = "${label}: ")
        Text(text = text)
    }
}

@Composable
fun Items(
    items: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.wrapContentHeight()) {
        items.forEach { item ->
            Row(
                modifier = modifier
                    .padding(horizontal = 8.dp)
                    .height(25.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Viñeta",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(text = item)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipesPreview() {
    RecipeRecommenderGeminiTheme {
        RecipesScreen(
            listOf(
                Recipe(
                    name = "test1",
                    totalCalories = 1000,
                    ingredients = listOf(
                        "Ingredient 1",
                        "Ingredient 2",
                        "Ingredient 3",
                        "Ingredient 4"
                    ),
                    instructions = listOf(
                        "instructions 1",
                        "instructions 2",
                        "instructions 3",
                        "instructions 4"
                    ),
                    videos = listOf("video 1", "video 2", "video 3", "video 4"),
                    references = listOf("reference 1", "reference 2", "reference 3", "reference 4"),
                )
            )
        )
    }
}
