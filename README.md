# Recipe Recommender Gemini in Android

A large language model [LLM](https://cloud.google.com/ai/llms?hl=en)  is a statistical language model, trained on a massive amount of data, that can be used to generate and translate text and other content and perform other natural language processing (NLP) tasks. LLMs are typically based on deep learning architectures, such as the [Transformer](https://arxiv.org/abs/1706.03762) developed by Google in 2017, and can be trained on billions of text and other content.

Google offers an LLM called Gemini

[Gemini](https://blog.google/technology/ai/google-gemini-ai/#sundar-note) is the result of large-scale collaborative efforts by teams across Google, including our colleagues at Google Research. It was built from the ground up to be multimodal, which means it can generalize and seamlessly understand, operate across, and combine different types of information including text, code, audio, image, and video.

[The Vertex AI Gemini API](https://firebase.google.com/docs/vertex-ai) gives you access to the latest generative AI models from Google: the Gemini models. If you need to call the Vertex AI Gemini API directly from your mobile or web app – rather than server-side — you can use the Vertex AI for Firebase SDKs


This App uses the Gemini API on Android to recommend recipes with multiple parameters. There is also a parameter to send an image and get recommendations based on it.


These are some parameters to obtain recommendations:
```
- Add some basic ingredients.
- Choose types of food.
- Choose Region
- Choose the output language
```

<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/c1958613-fa42-4dd4-b0ed-657f9cb082fd" width="300" height="650">


The app will show three options with the following information:
```
- Name
- Calories
- Ingredients
- Instructions
- Videos
- References
```

<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/25137b9b-86b9-42c5-8aa6-e8b52a485009" width="300" height="650">

Output Options:

<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/fc515124-81e1-4db7-8a57-960f09a9c57a" width="300" height="650">

Output Options in another language:

<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/2293c2cb-bb97-4231-8ad4-0ad205c4ad4d" width="300" height="650">

There is also a parameter to send an image and get recommendations based on it:

<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/5e4b473f-1556-47c4-9dc5-0315f84f51b9" width="300" height="650">

You can `chat` with a chef, ask for help cooking, and ask about specific recipes:

<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/1b6bcf53-e307-4ca1-948e-c2164fbaeb9e" width="300" height="650">
<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/7f018617-1e0c-433c-96d1-343ccf841692" width="300" height="650">

You can `chat` with a chef using `Functions Calling`. Function calling helps you connect generative models to external systems so that the generated content includes the most up-to-date and accurate information. This time this API is called `(https://themealdb.com/api/json/v1/1/filter.php?a=)`

<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/b1cbbb00-39fa-4021-827f-161a791a7373" width="300" height="650">

Additionally, you can get a `summary` of a video

<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/f893af2d-370d-489e-8286-f666c3d3b81f" width="300" height="650">


Made with ❤ by  [jggomez](https://devhack.co).

[![Twitter Badge](https://img.shields.io/badge/-@jggomezt-1ca0f1?style=flat-square&labelColor=1ca0f1&logo=twitter&logoColor=white&link=https://twitter.com/jggomezt)](https://twitter.com/jggomezt)
[![Linkedin Badge](https://img.shields.io/badge/-jggomezt-blue?style=flat-square&logo=Linkedin&logoColor=white&link=https://www.linkedin.com/in/jggomezt/)](https://www.linkedin.com/in/jggomezt/)
[![Medium Badge](https://img.shields.io/badge/-@jggomezt-03a57a?style=flat-square&labelColor=000000&logo=Medium&link=https://medium.com/@jggomezt)](https://medium.com/@jggomezt)

## License

    Copyright 2024 Juan Guillermo Gómez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
