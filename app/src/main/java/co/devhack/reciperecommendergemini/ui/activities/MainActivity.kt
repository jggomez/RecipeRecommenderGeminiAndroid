package co.devhack.reciperecommendergemini.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.devhack.reciperecommendergemini.ui.screens.ChatChefScreen
import co.devhack.reciperecommendergemini.ui.screens.ChatChefStreamScreen
import co.devhack.reciperecommendergemini.ui.screens.MenuScreen
import co.devhack.reciperecommendergemini.ui.screens.RecipeInputScreen
import co.devhack.reciperecommendergemini.ui.screens.RecipesScreen
import co.devhack.reciperecommendergemini.ui.screens.SummaryScreen
import co.devhack.reciperecommendergemini.ui.theme.RecipeRecommenderGeminiTheme
import co.devhack.reciperecommendergemini.viewmodels.ChatChefViewModel
import co.devhack.reciperecommendergemini.viewmodels.RecipeViewModel
import co.devhack.reciperecommendergemini.viewmodels.Recipes
import co.devhack.reciperecommendergemini.viewmodels.SummaryVideoViewModel
import com.google.gson.Gson
import timber.log.Timber

enum class RecipeScreens {
    RECIPE_INPUT,
    RECIPES,
    CHAT_CHEF_STREAM,
    CHAT_CHEF,
    VIDEO_SUMMARY,
    MENU,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContent {
            RecipeRecommenderGeminiTheme {
                RecipeApp()
            }
        }
    }
}

@Composable
fun RecipeApp(
    recipeViewModel: RecipeViewModel = viewModel(),
    chatChefViewModel: ChatChefViewModel = viewModel(),
    summaryVideoViewModel: SummaryVideoViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = RecipeScreens.MENU.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(
                route = RecipeScreens.MENU.name
            ) {
                Timber.i("Navigation in ${RecipeScreens.MENU.name}")
                MenuScreen {
                    navController.navigate(it.name)
                }
            }
            composable(route = RecipeScreens.RECIPE_INPUT.name) {
                Timber.i("Navigation in ${RecipeScreens.RECIPE_INPUT.name}")
                RecipeInputScreen(
                    recipeViewModel = recipeViewModel
                ) { recipes ->
                    val json = Gson().toJson(recipes)
                    Timber.i("Navigation to ${RecipeScreens.RECIPES.name}")
                    navController.navigate("${RecipeScreens.RECIPES.name}?recipes=${json}")
                }
            }
            composable(
                route = "${RecipeScreens.RECIPES.name}?recipes={recipes}",
            ) { backStackEntry ->
                backStackEntry.arguments?.getString("recipes")?.let {
                    Timber.i("Navigation in ${RecipeScreens.RECIPES.name}")
                    Timber.i("Navigation -> $it")
                    val recipes = Gson().fromJson(it, Recipes::class.java)
                    RecipesScreen(
                        recipes = recipes.recipes
                    )
                }
            }
            composable(
                route = RecipeScreens.CHAT_CHEF.name
            ) {
                Timber.i("Navigation in ${RecipeScreens.CHAT_CHEF.name}")
                ChatChefScreen(
                    chatChefViewModel = chatChefViewModel
                )
            }
            composable(
                route = RecipeScreens.CHAT_CHEF_STREAM.name
            ) {
                Timber.i("Navigation in ${RecipeScreens.CHAT_CHEF_STREAM.name}")
                ChatChefStreamScreen(
                    chatChefViewModel = chatChefViewModel
                )
            }
            composable(
                route = RecipeScreens.VIDEO_SUMMARY.name
            ) {
                Timber.i("Navigation in ${RecipeScreens.VIDEO_SUMMARY.name}")
                SummaryScreen(
                    summaryVideoViewModel = summaryVideoViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavTopBar(
    modifier: Modifier = Modifier,
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    actions: @Composable () -> Unit = {}
) {
    if (canNavigateBack) {
        TopAppBar(
            title = {
                Text(text = title)
            },
            actions = { actions() },
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "back"
                    )
                }
            },
            modifier = modifier
        )
    } else {
        TopAppBar(
            title = {
                Text(text = title)
            },
            actions = { actions() },
            modifier = modifier
        )
    }
}
