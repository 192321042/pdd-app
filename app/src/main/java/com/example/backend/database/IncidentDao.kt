package com.example.backend.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<IncidentReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentReport): Long

    @Query("DELETE FROM incidents WHERE id = :id")
    suspend fun deleteIncidentById(id: Int)

    @Query("SELECT * FROM incidents WHERE id = :id LIMIT 1")
    suspend fun getIncidentById(id: Int): IncidentReport?

    @Query("DELETE FROM incidents")
    suspend fun clearAllIncidents()
}
