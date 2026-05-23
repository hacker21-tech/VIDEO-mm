package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.AuditLog
import com.example.data.SyncItem
import com.example.data.AudioTrack
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database)
        
        // Add sample audio data if needed
        viewModelScope.launch {
            repository.addAudioTrack("Epic Cinematic", "John Doe", "Orchestral", "Epic")
            repository.addAudioTrack("Cyberpunk Chase", "Neon M", "Electronic", "Intense")
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val items: StateFlow<List<SyncItem>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) repository.items else repository.searchItems(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val audioTracks: StateFlow<List<AudioTrack>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) repository.audioTracks else repository.searchAudio(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLog>> = repository.auditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addAudioTrack(title: String, artist: String, genre: String, mood: String) = viewModelScope.launch {
        repository.addAudioTrack(title, artist, genre, mood)
    }

    fun addItem(title: String, content: String) = viewModelScope.launch {
        repository.addSyncItem(title, content)
    }

    fun toggleSync(item: SyncItem) = viewModelScope.launch {
        repository.toggleSync(item)
    }

    fun deleteItem(id: Int) = viewModelScope.launch {
        repository.deleteItem(id)
    }

    fun clearAllData() = viewModelScope.launch {
        repository.clearAllData()
    }
    
    fun togglePlaylist(track: AudioTrack) = viewModelScope.launch {
        repository.togglePlaylist(track)
    }

    suspend fun generateVideoAndSave(prompt: String, musicGenre: String): String? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val apiKey = com.example.BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty()) {
                android.util.Log.e("MainViewModel", "Gemini API key is missing. Ensure it's set in the Secrets panel.")
                return@withContext "API Key missing"
            }
            
            // 1. Initial request to Veo
            val req = com.example.api.PredictRequest(
                instances = listOf(com.example.api.PredictInstance(prompt = prompt)),
                parameters = com.example.api.PredictParameters(sampleCount = 1)
            )
            
            var op = com.example.api.RetrofitClient.service.predictLongRunning(
                model = "veo-2.0-generate-001",
                apiKey = apiKey,
                request = req
            )
            
            // 2. Polling for completion
            var retries = 0
            while (op.done != true && retries < 120) { // Max wait around 10 minutes (120 * 5s)
                kotlinx.coroutines.delay(5000)
                op = com.example.api.RetrofitClient.service.getOperation(
                    operationName = op.name,
                    apiKey = apiKey
                )
                retries++
            }
            
            if (op.done == true && op.response?.generatedVideos?.isNotEmpty() == true) {
                val videoUri = op.response.generatedVideos[0].video?.uri
                if (videoUri != null) {
                    repository.addSyncItem("Video: ${prompt.take(15)}...", videoUri)
                    return@withContext null // Success
                }
            }
            
            repository.addSyncItem("Video: ${prompt.take(15)}...", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4")
            return@withContext "API Limit/Billing: Generated sample video fallback"
        } catch (e: retrofit2.HttpException) {
            e.printStackTrace()
            repository.addSyncItem("Video: ${prompt.take(15)}...", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4")
            return@withContext "API Limit/Billing: Generated sample video fallback"
        } catch (e: Exception) {
            e.printStackTrace()
            repository.addSyncItem("Video: ${prompt.take(15)}...", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
            return@withContext "Error: Generated sample video fallback"
        }
    }
}
