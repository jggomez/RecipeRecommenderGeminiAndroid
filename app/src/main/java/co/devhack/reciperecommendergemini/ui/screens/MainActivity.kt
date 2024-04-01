package co.devhack.reciperecommendergemini.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.devhack.reciperecommendergemini.ui.theme.RecipeRecommenderGeminiTheme
import co.devhack.reciperecommendergemini.viewmodels.RecipeViewModel
import co.devhack.reciperecommendergemini.viewmodels.Recipes
import com.google.gson.Gson
import timber.log.Timber

enum class RecipeScreens {
    RECIPE_INPUT,
    RECIPES,
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
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopBar(
                title = "Recipe Recommender",
                canNavigateBack = true,
                onBackButtonPress = {
                    navController.navigate(RecipeScreens.RECIPE_INPUT.name)
                })
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = RecipeScreens.RECIPE_INPUT.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
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
        }
    }
}

@Composable
fun TopBar(
    title: String,
    canNavigateBack: Boolean,
    onBackButtonPress: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "ArrowBack",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { onBackButtonPress() }
            )
        } else {
            Spacer(modifier = Modifier)
        }

        Text(
            text = title,
        )
        Spacer(modifier = Modifier)
    }
}
