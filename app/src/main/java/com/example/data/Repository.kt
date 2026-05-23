package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val db: AppDatabase) {
    val items: Flow<List<SyncItem>> = db.syncItemDao().getAllItems()
    val auditLogs: Flow<List<AuditLog>> = db.auditLogDao().getLogs()
    val audioTracks: Flow<List<AudioTrack>> = db.audioTrackDao().getAllTracks()

    fun searchItems(query: String) = db.syncItemDao().searchItems(query)
    fun searchAudio(query: String) = db.audioTrackDao().searchTracks(query)
    
    suspend fun addAudioTrack(title: String, artist: String, genre: String, mood: String) {
        db.audioTrackDao().insertTrack(AudioTrack(title = title, artist = artist, genre = genre, mood = mood))
        db.auditLogDao().insertLog(AuditLog(action = "Imported audio: $title"))
    }
    
    suspend fun togglePlaylist(track: AudioTrack) {
        db.audioTrackDao().insertTrack(track.copy(inPlaylist = !track.inPlaylist))
    }

    suspend fun addSyncItem(title: String, content: String) {
        db.syncItemDao().insertItem(SyncItem(title = title, content = content))
        db.auditLogDao().insertLog(AuditLog(action = "Created item: $title"))
    }
    
    suspend fun toggleSync(item: SyncItem) {
        db.syncItemDao().insertItem(item.copy(isSynced = !item.isSynced))
        val action = if (!item.isSynced) "Synced item: ${item.title}" else "Un-synced item: ${item.title}"
        db.auditLogDao().insertLog(AuditLog(action = action))
    }

    suspend fun deleteItem(id: Int) {
        db.syncItemDao().deleteItemById(id)
        db.auditLogDao().insertLog(AuditLog(action = "Deleted item ID: $id"))
    }
    
    suspend fun clearAllData() {
        db.syncItemDao().deleteAll()
        db.auditLogDao().insertLog(AuditLog(action = "Cleared all user data per Data Deletion Policy"))
    }
}
