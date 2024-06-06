package co.devhack.reciperecommendergemini.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import co.devhack.reciperecommendergemini.ui.activities.NavTopBar
import co.devhack.reciperecommendergemini.ui.theme.RecipeRecommenderGeminiTheme
import co.devhack.reciperecommendergemini.ui.util.ImageUtils
import co.devhack.reciperecommendergemini.viewmodels.RecipeViewModel
import co.devhack.reciperecommendergemini.viewmodels.Recipes
import co.devhack.reciperecommendergemini.viewmodels.ScreenState
import coil.compose.AsyncImage
import timber.log.Timber

@Composable
fun RecipeInputScreen(
    modifier: Modifier = Modifier,
    recipeViewModel: RecipeViewModel? = null,
    onClickGetRecipes: (recipes: Recipes) -> Unit = {}
) {
    val ingredients = remember { mutableStateListOf<String>() }
    val selectedType = remember { mutableStateOf("All") }
    val selectedRegion = remember { mutableStateOf("All") }
    val selectedLanguage = remember { mutableStateOf("en") }
    val imagePath = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            NavTopBar(
                title = "Recipe Recommender",
                canNavigateBack = false,
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .height(600.dp)
                    .verticalScroll(scrollState)
            ) {
                IngredientInput(
                    onClick = { ingredient ->
                        ingredients.add(ingredient)
                    },
                    onClickImage = {
                        imagePath.value = it ?: ""
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                ItemList(
                    items = ingredients, //listOf("Vegan", "Vegetarian", "Low in Calories", "Low fat"),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Types of food")
                ItemSelectedList(
                    items = listOf("Vegan", "Vegetarian", "Low in Calories", "Low fat", "All"),
                ) {
                    selectedType.value = it
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Region")
                ItemSelectedList(
                    items = listOf("Mexican", "Italian", "France", "Spain", "India"),
                ) {
                    selectedRegion.value = it
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Language")
                ItemSelectedList(
                    items = listOf("English", "Español"),
                ) {
                    selectedLanguage.value = it
                }
                Spacer(modifier = Modifier.height(16.dp))
                when {
                    recipeViewModel?.uiState?.errorMessage?.isBlank() == true -> {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    else -> recipeViewModel?.uiState?.errorMessage?.let {
                        Text(
                            text = it,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Row(modifier = Modifier.align(Alignment.BottomEnd)) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        recipeViewModel?.getRecipes(
                            recipeType = selectedType.value,
                            region = selectedRegion.value,
                            ingredients = ingredients,
                            language = selectedLanguage.value,
                            imagePath = imagePath.value
                        )
                    }) {
                    when (recipeViewModel?.uiState?.screenState == ScreenState.Loading) {
                        false -> Text(text = "Get Recipes")
                        true -> CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            }

            when (recipeViewModel?.uiState?.screenState == ScreenState.Success) {
                true -> {
                    if (recipeViewModel?.uiState?.recipes?.isNotEmpty() == true) {
                        onClickGetRecipes(
                            Recipes(
                                recipes = recipeViewModel.uiState.recipes
                            )
                        )
                    } else {
                        Text(
                            text = "Recipes Not Found, Should change the parameters",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                false -> {}
            }
        }
    }
}

@Composable
fun IngredientInput(
    modifier: Modifier = Modifier,
    onClick: (text: String) -> Unit = {},
    onClickImage: (absolutPath: String?) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }

    val context = LocalContext.current

    val imageUtils = ImageUtils(context)

    var currentPhoto by remember {
        mutableStateOf<String?>(null)
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data?.data
                currentPhoto = if (data == null) {
                    imageUtils.currentPhotoPath
                } else {
                    imageUtils.getPathFromGalleryUri(data)
                }
            }
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            launcher.launch(imageUtils.getIntent())
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = modifier) {
        Text(text = "Add an ingredient")
        Spacer(modifier = Modifier.width(16.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Ingredient") },
            value = text,
            onValueChange = { text = it })
        Spacer(modifier = Modifier.width(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterVertically),
                onClick = {
                    if (text != "") {
                        onClick(text)
                        text = ""
                    }
                }) {
                Text(text = "Add")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                modifier = modifier
                    .fillMaxWidth(),
                onClick = {
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        launcher.launch(imageUtils.getIntent())
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                Text(text = "Load Image")
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
        ) {
            Timber.i("currentPhoto -> $currentPhoto")
            if (currentPhoto != null) {
                Timber.i("currentPhoto -> $currentPhoto")
                AsyncImage(
                    model = currentPhoto,
                    contentDescription = "icon",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier.size(150.dp)
                )
                onClickImage(currentPhoto)
            }
        }
    }
}

@Composable
fun ItemList(
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
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Viñeta",
                )
                Text(text = item)
            }
        }
    }
}

@Composable
fun ItemSelectedList(
    items: List<String>,
    modifier: Modifier = Modifier,
    onSelectedOption: (String) -> Unit = { },
) {
    val selectedOption = remember { mutableStateOf(items[0]) }
    Column(modifier = modifier.wrapContentHeight()) {
        items.forEach { item ->
            Row(
                modifier = modifier
                    .padding(horizontal = 8.dp)
                    .height(25.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    modifier = Modifier.padding(0.dp),
                    selected = selectedOption.value == item,
                    onClick = {
                        selectedOption.value = item
                        onSelectedOption(item)
                    }
                )

                Text(text = item)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipePreview() {
    RecipeRecommenderGeminiTheme {
        RecipeInputScreen()
    }
}
