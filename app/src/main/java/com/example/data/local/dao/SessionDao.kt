package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TasbihSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM tasbih_sessions ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<TasbihSessionEntity>>

    @Query("SELECT * FROM tasbih_sessions WHERE status != 'ACTIVE' ORDER BY startedAt DESC")
    fun getHistoricalSessions(): Flow<List<TasbihSessionEntity>>

    @Query("SELECT * FROM tasbih_sessions WHERE status = 'ACTIVE' LIMIT 1")
    fun getActiveSession(): Flow<TasbihSessionEntity?>

    @Query("SELECT * FROM tasbih_sessions WHERE status = 'ACTIVE' LIMIT 1")
    suspend fun getActiveSessionSync(): TasbihSessionEntity?

    @Query("SELECT * FROM tasbih_sessions WHERE id = :id")
    fun getSessionById(id: Long): Flow<TasbihSessionEntity?>

    @Query("SELECT * FROM tasbih_sessions WHERE startedAt >= :startOfDay ORDER BY startedAt DESC")
    fun getTodaySessions(startOfDay: Long): Flow<List<TasbihSessionEntity>>

    @Query("SELECT * FROM tasbih_sessions WHERE startedAt >= :fromTimestamp ORDER BY startedAt ASC")
    fun getSessionsSince(fromTimestamp: Long): Flow<List<TasbihSessionEntity>>

    @Query("SELECT COALESCE(SUM(count), 0) FROM tasbih_sessions")
    fun getTotalCountAllTime(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasbih_sessions WHERE status = 'COMPLETED'")
    fun getCompletedSessionsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TasbihSessionEntity): Long

    @Update
    suspend fun updateSession(session: TasbihSessionEntity)

    @Query("DELETE FROM tasbih_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)

    @Query("DELETE FROM tasbih_sessions WHERE status != 'ACTIVE'")
    suspend fun clearHistory()
}
