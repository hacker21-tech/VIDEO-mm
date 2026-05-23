package com.example

import org.junit.Test
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class ModelListTest {
    @Test
    fun testGenerateVideo() = runBlocking {
        try {
            val apiKey = "AIzaSyBuywjOFPyGc55Q9wvlTkQmR8KtjkcPSkU"
            val json = """
                {
                  "instances": [
                    { "prompt": "A cute cat playing with yarn" }
                  ],
                  "parameters": {
                    "sampleCount": 1
                  }
                }
            """.trimIndent()
            val mediaType = "application/json".toMediaTypeOrNull()
            val body = okhttp3.RequestBody.create(mediaType, json)

            val okHttpClient = okhttp3.OkHttpClient()
            val request = okhttp3.Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/veo-3.1-fast-generate-preview:predictLongRunning?key=$apiKey")
                .post(body)
                .build()
            
            val response = okHttpClient.newCall(request).execute()
            println("Status: ${response.code}")
            println("Response: ${response.body?.string()}")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}
