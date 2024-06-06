package co.devhack.reciperecommendergemini.data

import android.graphics.BitmapFactory
import co.devhack.RecipeRecommenderGemini.BuildConfig
import co.devhack.reciperecommendergemini.viewmodels.CountryRecipe
import co.devhack.reciperecommendergemini.viewmodels.GeminiRepository
import co.devhack.reciperecommendergemini.viewmodels.Recipe
import com.google.firebase.Firebase
import com.google.firebase.vertexai.Chat
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.FunctionResponsePart
import com.google.firebase.vertexai.type.InvalidStateException
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.Tool
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.defineFunction
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.json.JSONObject
import timber.log.Timber


class GeminiRepositoryImp : GeminiRepository {

    private lateinit var chat: Chat
    private var clientHttp: HttpClient = HttpClient(Android) {
        install(Logging)
        engine {
            connectTimeout = 100_000
            socketTimeout = 100_000
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val getRecipeFromCountry = defineFunction(
        name = "getRecipeFromCountry",
        description = "Get recipes from a country example mexican recipes",
        Schema.str("countryFrom", "Get recipes from this country"),
    ) { countryFrom ->
        getRecipesCountry(countryFrom)
    }

    private var generativeModelWithTools: GenerativeModel = Firebase.vertexAI.generativeModel(
        modelName = BuildConfig.IMAGE_MODEL_NAME,
        tools = listOf(Tool(listOf(getRecipeFromCountry))),
        generationConfig = generationConfig {
            temperature = 0.8f
        },
        systemInstruction = content {
            text(
                "You are an expert cooking and the best chef. Create recipes with these food ingredients." +
                        "You don't know about other any topic except cooking. " +
                        "With another topic you should answer with 'I don't know about this topic' "
            )
        }
    )

    private var generativeModel: GenerativeModel = Firebase.vertexAI.generativeModel(
        modelName = BuildConfig.IMAGE_MODEL_NAME,
        generationConfig = generationConfig {
            temperature = 0.8f
        },
        systemInstruction = content {
            text(
                "You are an expert cooking and the best chef. Create recipes with these food ingredients." +
                        "You don't know about other any topic except cooking. " +
                        "With another topic you should answer with 'I don't know about this topic' "
            )
        }
    )

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
           Please each recipe must has the following:
           - Name as a string
           - Ingredients as an array of strings
           - Steps Instructions as an array of strings
           - Total Calories as a string
           - Reference Link Videos as an array of strings
           - Other Link references as an array of strings
           
           The output values must be a JSON array of objects like this (Not markdown) and return the result values in this language '$language':
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
            Timber.i("Using IMAGE")
            val bmOptions = BitmapFactory.Options()
            val bitmap = BitmapFactory.decodeFile(imagePath, bmOptions)
            val inputContent = content {
                image(bitmap)
                text("Detect the food objects of this picture and take these as ingredients and $prompt")
            }
            generativeModel.generateContent(inputContent)
        } else {
            Timber.i("Using TEXT")
            generativeModel.generateContent(prompt)
        }

        Timber.i("Gemini Response -> $response")
        response.text?.let {
            Timber.i(response.text)
            var responseText = response.text ?: ""
            if (responseText.contains("json")) {
                responseText = responseText.substring(7, responseText.length - 3)
            }
            Timber.i(responseText)
            return Json.decodeFromString<List<Recipe>>(responseText)
        } ?: return emptyList()
    }

    override suspend fun initChat(withTools: Boolean) {
        Timber.i("initChat -> $withTools")
        val history = listOf(
            content(role = "user") {
                text("Hello, I want to know about recipes and cooking")
            },
            content(role = "model") {
                text("Great, I can help you with that because I am expert in cooking")
            }
        )
        chat = if (withTools) {
            generativeModelWithTools.startChat(
                history = history
            )
        } else {
            generativeModel.startChat(
                history = history
            )
        }
    }

    override suspend fun sendMessageStream(userMessage: String): Flow<String> {
        Timber.i("sendMessageStream: User Message -> $userMessage")
        return chat.sendMessageStream(userMessage).map { response ->
            response.text ?: "I'm sorry, I don't understand"
        }.catch {
            Timber.e(it)
        }
    }

    override suspend fun sendMessage(userMessage: String): String {
        Timber.i("sendMessage: User Message -> $userMessage")
        var response = chat.sendMessage(userMessage)
        response.functionCalls.forEach { functionCall ->
            Timber.i("tool called -> ${functionCall.name}")
            val matchedFunction =
                generativeModelWithTools.tools?.flatMap { it.functionDeclarations }
                    ?.first { it.name == functionCall.name }
                    ?: throw InvalidStateException("Function not found: ${functionCall.name}")

            Timber.i("matchedFunction -> $matchedFunction")

            // Call the lambda retrieved above
            val apiResponse: JSONObject = matchedFunction.execute(functionCall)
            Timber.i("apiResponse -> $apiResponse")

            // Send the API response back to the generative model
            // so that it generates a text response that can be displayed to the user
            response = chat.sendMessage(
                content(role = "function") {
                    part(FunctionResponsePart(functionCall.name, apiResponse))
                }
            )
        }

        return response.text ?: "I'm sorry, I don't understand"
    }

    override suspend fun getSummaryVideo(videoUrl: String, textPrompt: String): String {
        val prompt = content {
            fileData(mimeType = "video/mp4", uri = videoUrl)
            text(textPrompt)
        }

        val response = generativeModel.generateContent(prompt)
        Timber.i("Response -> ${response.text}")
        return response.text ?: "I'm sorry, I don't understand"
    }

    override suspend fun getCountTokens(videoUrl: String, textPrompt: String): Int {
        val prompt = content {
            fileData(mimeType = "video/mp4", uri = videoUrl)
            text(textPrompt)
        }
        return generativeModel.countTokens(prompt).totalTokens
    }

    private suspend fun getRecipesCountry(countryFrom: String): JSONObject {
        try {
            Timber.i("Calling API")
            val response: CountryRecipe =
                clientHttp.get("${BuildConfig.API_URL_GET_FOOD_MEXICAN}${countryFrom}").body()
            Timber.i("API Response -> $response")
            val meals = ArrayList<JSONObject>()
            response.meals.forEach { meal ->
                meals.add(
                    JSONObject().apply {
                        put("name", meal.strMeal)
                        put("image", meal.strMealThumb)
                    }
                )
            }
            return JSONObject().apply {
                put(
                    "meals", meals
                )
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return JSONObject().apply {
            put("error", "Something went wrong")
        }
    }
}
