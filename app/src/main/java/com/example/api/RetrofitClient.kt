package com.example.api

import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
data class PredictRequest(
    val instances: List<PredictInstance>,
    val parameters: PredictParameters? = null
)

@JsonClass(generateAdapter = true)
data class PredictInstance(
    val prompt: String
)

@JsonClass(generateAdapter = true)
data class PredictParameters(
    val sampleCount: Int? = 1
)

@JsonClass(generateAdapter = true)
data class OperationResponse(
    val name: String,
    val done: Boolean? = false,
    val response: GenerateVideosResponse? = null // This might be wrong. Let's make it a Map
)

@JsonClass(generateAdapter = true)
data class GenerateVideosResponse(
    val generatedVideos: List<GeneratedVideo>? = null // Will this work?
)

@JsonClass(generateAdapter = true)
data class GeneratedVideo(
    val video: VideoUri? = null
)

@JsonClass(generateAdapter = true)
data class VideoUri(
    val uri: String? = null
)

interface VeoApiService {
    @POST("v1beta/models/{model}:predictLongRunning")
    suspend fun predictLongRunning(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: PredictRequest
    ): OperationResponse

    @GET("v1beta/{operationName}")
    suspend fun getOperation(
        @Path("operationName", encoded = true) operationName: String,
        @Query("key") apiKey: String
    ): OperationResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(okhttp3.logging.HttpLoggingInterceptor().apply { level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .build()

    val service: VeoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(VeoApiService::class.java)
    }
}
