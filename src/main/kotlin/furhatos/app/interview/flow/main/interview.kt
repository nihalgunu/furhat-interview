package furhatos.app.interview.flow.main

import furhatos.flow.kotlin.*
import furhatos.nlu.common.*
import okhttp3.*
import org.json.JSONObject

// Helper function to interact with Hugging Face LLM
fun reframeNegativeResponse(input: String): String {
    val client = OkHttpClient()
    val apiUrl = "https://api-inference.huggingface.co/models/your-model-name" // Replace with your Hugging Face model URL
    val apiKey = "your-hugging-face-api-key" // Replace with your API key

    val json = JSONObject()
    json.put("inputs", "Reframe this statement positively: \"$input\"")

    val requestBody = RequestBody.create(MediaType.get("application/json"), json.toString())
    val request = Request.Builder()
        .url(apiUrl)
        .addHeader("Authorization", "Bearer $apiKey")
        .post(requestBody)
        .build()

    client.newCall(request).execute().use { response ->
        val responseBody = response.body()?.string() ?: return "I'm here to help you see the brighter side!"
        val outputJson = JSONObject(responseBody)
        return outputJson.getJSONArray("generated_text").getString(0)
    }
}

// Interview state
val Interview: State = state {
    onEntry {
        furhat.setTexture("female")
        furhat.attend(users.random)
        furhat.say("Welcome! Let's have a conversation. I'm here to help you see things in a more positive light.")
        furhat.ask("To start, tell me about something that's been challenging you lately.")
    }

    onResponse {
        val userResponse = it.text
        if (userResponse.containsNegativeSentiment()) { // Check if the statement seems negative
            val reframedResponse = reframeNegativeResponse(userResponse)
            furhat.say("I understand how that feels. Here's another way to look at it: $reframedResponse")
        } else {
            furhat.say("Thank you for sharing that!")
        }
        furhat.ask("Would you like to talk about something else?")
    }
}

// End state
val EndConversation: State = state {
    onEntry {
        furhat.say("This has been a great conversation. Remember, there's always a positive side to everything. Have a wonderful day!")
        goto(Idle)
    }
}

// Idle state
val Idle: State = state {
    onEntry {
        furhat.say("Goodbye!")
        furhat.listen()
    }
}

// Extension function to detect negative sentiment
fun String.containsNegativeSentiment(): Boolean {
    val negativeKeywords = listOf("difficult", "hard", "stressful", "bad", "unfortunate", "impossible", "failure", "sad", "angry", "frustrated")
    return negativeKeywords.any { this.contains(it, ignoreCase = true) }
}
