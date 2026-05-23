package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncItemDao {
    @Query("SELECT * FROM sync_items ORDER BY timestamp DESC")
    fun getAllItems(): Flow<List<SyncItem>>

    @Query("SELECT * FROM sync_items WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchItems(query: String): Flow<List<SyncItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: SyncItem)

    @Query("DELETE FROM sync_items WHERE id = :id")
    suspend fun deleteItemById(id: Int)
    
    @Query("DELETE FROM sync_items")
    suspend fun deleteAll()
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLog)
    
    @Query("DELETE FROM audit_logs")
    suspend fun deleteLogs()
}

@Dao
interface AudioTrackDao {
    @Query("SELECT * FROM audio_tracks ORDER BY timestamp DESC")
    fun getAllTracks(): Flow<List<AudioTrack>>

    @Query("SELECT * FROM audio_tracks WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' OR genre LIKE '%' || :query || '%' OR mood LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchTracks(query: String): Flow<List<AudioTrack>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: AudioTrack)

    @Query("DELETE FROM audio_tracks WHERE id = :id")
    suspend fun deleteTrackById(id: Int)
    
    @Query("DELETE FROM audio_tracks")
    suspend fun deleteAll()
}
