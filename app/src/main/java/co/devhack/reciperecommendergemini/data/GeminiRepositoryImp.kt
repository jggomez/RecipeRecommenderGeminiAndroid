package co.devhack.reciperecommendergemini.data

import android.graphics.BitmapFactory
import co.devhack.RecipeRecommenderGemini.BuildConfig
import co.devhack.reciperecommendergemini.viewmodels.GeminiRepository
import co.devhack.reciperecommendergemini.viewmodels.Recipe
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.serialization.json.Json
import timber.log.Timber


class GeminiRepositoryImp : GeminiRepository {

    private var config: GenerationConfig = generationConfig {
        temperature = 0.2f
        maxOutputTokens = 10000
    }

    override suspend fun getRecipes(
        recipeType: String,
        region: String,
        ingredients: List<String>,
        language: String,
        imagePath: String,
    ): List<Recipe> {
        val prompt = """
            You are an expert cooking and the best chef. 
            Create 3 recipes with these food ingredients:
            ${
            ingredients.map { item ->
                "$item, "
            }
        }
           Also please the recipes should be '$recipeType and of$region' and all fields are mandatory
           Please each recipe must has the following and in this code language '$language':
           - Name
           - Ingredients
           - Steps Instructions
           - Total Calories
           - Reference Link Videos
           - Other Link references
           
           The output values must be in this language 'SPANISH' structure and must be a JSON array of objects like this (Not markdown):
           And translate the content to this code language '$language' :
           Example output:
           "[{
                "name": "Test",
                "ingredients": [
                    "Ingredient 1",
                    "Ingredient 2"
                ],
                "instructions": [
                    "instructions 1",
                    "instructions 2"
                ],
                "totalCalories": 1289,
                "videos": [
                    "Video 1",
                    "Video 2",
                    "Video 3"
                ],
                "references": [
                    "reference 1",
                    "reference 2",
                    "reference 3"
                ]
            }]"
        """
        Timber.i("Prompt -> $prompt")

        Timber.i("ImagePath => $imagePath")

        val response = if (imagePath.isNotEmpty()) {
            Timber.i("Using IMAGE_MODEL_NAME")
            val generativeModel = GenerativeModel(
                modelName = BuildConfig.IMAGE_MODEL_NAME,
                apiKey = BuildConfig.API_KEY,
                generationConfig = config
            )
            val bmOptions = BitmapFactory.Options()
            val bitmap = BitmapFactory.decodeFile(imagePath, bmOptions)
            val inputContent = content {
                image(bitmap)
                text("Take some food ingredients of this picture and $prompt")
            }
            generativeModel.generateContent(inputContent)
        } else {
            Timber.i("Using TEXT_MODEL_NAME")
            val generativeModel = GenerativeModel(
                modelName = BuildConfig.TEXT_MODEL_NAME,
                apiKey = BuildConfig.API_KEY,
                generationConfig = config
            )
            generativeModel.generateContent(prompt)
        }

        Timber.i("Gemini Response -> $response")
        response.text?.let {
            Timber.i(response.text)
            var responseText = response.text ?: ""
            if(responseText.contains("json")){
                responseText = responseText.substring(7, responseText.length - 3)
            }
            Timber.i(responseText)
            return Json.decodeFromString<List<Recipe>>(responseText)
        } ?: return emptyList()
    }
}