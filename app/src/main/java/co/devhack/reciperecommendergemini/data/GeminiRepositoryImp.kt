package co.devhack.reciperecommendergemini.data

import android.content.Context
import android.graphics.BitmapFactory
import co.devhack.RecipeRecommenderGemini.BuildConfig
import co.devhack.reciperecommendergemini.viewmodels.domain.CountryRecipe
import co.devhack.reciperecommendergemini.viewmodels.domain.Recipe
import co.devhack.reciperecommendergemini.viewmodels.repositories.GeminiRepository
import com.google.firebase.Firebase
import com.google.firebase.vertexai.Chat
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.FunctionDeclaration
import com.google.firebase.vertexai.type.FunctionResponsePart
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.Tool
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber


class GeminiRepositoryImp : GeminiRepository {

    companion object {
        const val FUNCTION_GET_RECIPE_FROM_COUNTRY = "getRecipeFromCountry"
        const val FUNCTION_PARAM_COUNTRY_FROM = "countryFrom"
    }

    private lateinit var chat: Chat
    private lateinit var llmInference: LlmInference
    private val resultLLMMediaPipe = MutableSharedFlow<Pair<String, Boolean>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

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

    private val recipeSchema = Schema.array(
        Schema.obj(
            mapOf(
                "name" to Schema.string("Name of the recipe"),
                "ingredients" to Schema.array(Schema.string("Ingredients of the recipe")),
                "instructions" to Schema.array(Schema.string("Instructions of the recipe")),
                "totalCalories" to Schema.integer("Total calories of the recipe"),
                "videos" to Schema.array(Schema.string("Videos of the recipe")),
                "references" to Schema.array(Schema.string("References of the recipe")),
            )
        )
    )

    private val getRecipeFromCountry = FunctionDeclaration(
        name = FUNCTION_GET_RECIPE_FROM_COUNTRY,
        description = "Get recipes from country cuisines example mexican recipes",
        parameters = mapOf(
            FUNCTION_PARAM_COUNTRY_FROM to Schema.string("country cuisines example mexican or italian or spanish"),
        ),
    )

    private var generativeModelWithTools: GenerativeModel = Firebase.vertexAI.generativeModel(
        modelName = BuildConfig.IMAGE_MODEL_NAME,
        tools = listOf(Tool.functionDeclarations(listOf(getRecipeFromCountry))),
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

    private var generativeModelWithSchema: GenerativeModel = Firebase.vertexAI.generativeModel(
        modelName = BuildConfig.IMAGE_MODEL_NAME,
        generationConfig = generationConfig {
            temperature = 0.8f
            responseMimeType = "application/json"
            responseSchema = recipeSchema
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
        photos: List<String>,
    ): List<Recipe> {
        val prompt = """
            You are an expert cooking and the best chef. 
            Create 3 recipes with these food ingredients:
            ${
            ingredients.map { item ->
                "$item, "
            }
        }
           Also please the recipes should be '$recipeType and of $region' and all fields are mandatory
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

        Timber.i("ImagePath => $photos")

        val response = if (photos.isNotEmpty()) {
            Timber.i("Using IMAGE")
            val inputContent = content {
                for (photo in photos) {
                    val bmOptions = BitmapFactory.Options()
                    val bitmap = BitmapFactory.decodeFile(photo, bmOptions)
                    image(bitmap)
                }
                text("Detect the food objects of these pictures and take these as ingredients and $prompt")
            }
            generativeModelWithSchema.generateContent(inputContent)
        } else {
            Timber.i("Using TEXT")
            generativeModelWithSchema.generateContent(prompt)
        }

        Timber.i("Gemini Response -> $response")
        response.text?.let {
            Timber.i(response.text)
            return Json.decodeFromString<List<Recipe>>(response.text ?: "")
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

        val functionGetRecipeFromCountry =
            response.functionCalls.find { it.name == FUNCTION_GET_RECIPE_FROM_COUNTRY }

        Timber.i("functionGetRecipeFromCountry: $functionGetRecipeFromCountry")

        functionGetRecipeFromCountry?.let {
            val countryFromParam =
                it.args[FUNCTION_PARAM_COUNTRY_FROM]?.jsonPrimitive?.content ?: ""
            if (countryFromParam.isEmpty().not()) {
                Timber.i("Calling API with country cuisine-> $countryFromParam")
                val apiResponse = getRecipesCountry(countryFromParam)
                if (apiResponse.contains("error").not()) {
                    apiResponse.let {
                        Timber.i("API Response -> $apiResponse")
                        response = chat.sendMessage(
                            content(role = "function") {
                                part(
                                    FunctionResponsePart(
                                        FUNCTION_GET_RECIPE_FROM_COUNTRY,
                                        apiResponse
                                    )
                                )
                            }
                        )
                    }
                } else {
                    Timber.e("Error API Response -> $apiResponse")
                    response = chat.sendMessage(userMessage)
                }
            }
        }
        if (response.text.isNullOrEmpty()) {
            return "I'm sorry, I don't understand or There is a problem with the tools"
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

    override suspend fun sendMessageLlmMediaPipe(message: String) {
        llmInference.generateResponseAsync(message)
    }

    override suspend fun initLlmMediaPipe(context: Context) {
        Timber.i("init LLM Media pipe")
        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath("/data/local/tmp/llm/model.bin")
            .setMaxTokens(1024)
            .setResultListener { partialResult, done ->
                Timber.i("partial result: $partialResult")
                Timber.i("done: $done")
                resultLLMMediaPipe.tryEmit(partialResult to done)
            }
            .setErrorListener {
                Timber.e(it)
            }
            .build()

        llmInference = LlmInference.createFromOptions(context, options)
    }

    override suspend fun resultLlmMediaPipe() = resultLLMMediaPipe.asSharedFlow()

    private suspend fun getRecipesCountry(countryFrom: String): JsonObject {
        try {
            Timber.i("Calling API")
            val response: CountryRecipe =
                clientHttp.get("${BuildConfig.API_URL_GET_FOOD_MEXICAN}${countryFrom}").body()
            Timber.i("API Response -> $response")

            if (response.meals.isNullOrEmpty()) {
                return JsonObject(
                    mapOf("error" to JsonPrimitive("No recipes found"))
                )
            }

            val meals = ArrayList<JsonObject>()

            response.meals.forEach { meal ->
                meals.add(
                    JsonObject(
                        mapOf(
                            "name" to JsonPrimitive(meal.strMeal),
                            "image" to JsonPrimitive(meal.strMealThumb)
                        )
                    )
                )
            }

            return JsonObject(
                mapOf("meals" to JsonArray(meals))
            )
        } catch (e: Exception) {
            Timber.e(e)
        }
        return JsonObject(
            mapOf("error" to JsonPrimitive("Something went wrong, create you an optimal answer"))
        )
    }
}
