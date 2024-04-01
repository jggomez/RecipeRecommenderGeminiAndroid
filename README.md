# Recipe Recommender Gemini in Android

A large language model [LLM](https://cloud.google.com/ai/llms?hl=en)  is a statistical language model, trained on a massive amount of data, that can be used to generate and translate text and other content and perform other natural language processing (NLP) tasks. LLMs are typically based on deep learning architectures, such as the [Transformer](https://arxiv.org/abs/1706.03762) developed by Google in 2017, and can be trained on billions of text and other content.

Google offers an LLM called Gemini

[Gemini](https://blog.google/technology/ai/google-gemini-ai/#sundar-note) is the result of large-scale collaborative efforts by teams across Google, including our colleagues at Google Research. It was built from the ground up to be multimodal, which means it can generalize and seamlessly understand, operate across, and combine different types of information including text, code, audio, image, and video.

[Gemini API in Android](https://ai.google.dev/tutorials/get_started_android) offers access directly from your Android app using the Google AI client SDK for Android. You can use this client SDK if you don't want to work directly with REST APIs or server-side code (like Python) for accessing Gemini models in your Android app.

This App uses the Gemini API on Android to recommend recipes with multiple parameters. There is also a parameter to send an image and get recommendations based on it.


These are some parameters to obtain recommendations:
- Add some basic ingredients.
- Choose types of food.
- Choose Region
- Choose the output language
<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/c1958613-fa42-4dd4-b0ed-657f9cb082fd" width="400" height="900">


The app will show three options with the following information:
- Name
- Calories
- Ingredients
- Instructions
- Videos
- References
  
<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/25137b9b-86b9-42c5-8aa6-e8b52a485009" width="400" height="900">

Output Options:

<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/fc515124-81e1-4db7-8a57-960f09a9c57a" width="400" height="900">

Output Options in another language:

<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/2293c2cb-bb97-4231-8ad4-0ad205c4ad4d" width="400" height="900">

There is also a parameter to send an image and get recommendations based on it:

<img src="https://github.com/jggomez/RecipeRecommenderGeminiAndroid/assets/661231/5e4b473f-1556-47c4-9dc5-0315f84f51b9" width="400" height="900">

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
