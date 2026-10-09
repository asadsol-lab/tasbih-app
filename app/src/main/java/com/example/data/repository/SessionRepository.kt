package com.example.data.repository

import com.example.data.local.dao.SessionDao
import com.example.data.local.entity.TasbihSessionEntity
import kotlinx.coroutines.flow.Flow

class SessionRepository(private val sessionDao: SessionDao) {

    val allSessions: Flow<List<TasbihSessionEntity>> = sessionDao.getAllSessions()

    val historicalSessions: Flow<List<TasbihSessionEntity>> = sessionDao.getHistoricalSessions()

    val activeSession: Flow<TasbihSessionEntity?> = sessionDao.getActiveSession()

    suspend fun getActiveSessionSync(): TasbihSessionEntity? = sessionDao.getActiveSessionSync()

    suspend fun insertSession(session: TasbihSessionEntity): Long = sessionDao.insertSession(session)

    suspend fun updateSession(session: TasbihSessionEntity) = sessionDao.updateSession(session)

    suspend fun deleteSessionById(id: Long) = sessionDao.deleteSessionById(id)

    suspend fun clearHistory() = sessionDao.clearHistory()

    fun getTodaySessions(startOfDay: Long): Flow<List<TasbihSessionEntity>> =
        sessionDao.getTodaySessions(startOfDay)

    fun getSessionsSince(fromTimestamp: Long): Flow<List<TasbihSessionEntity>> =
        sessionDao.getSessionsSince(fromTimestamp)

    fun getTotalCountAllTime(): Flow<Int> = sessionDao.getTotalCountAllTime()

    fun getCompletedSessionsCount(): Flow<Int> = sessionDao.getCompletedSessionsCount()
}
