package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.dao.ZikrDao
import com.example.data.local.entity.ZikrEntity
import kotlinx.coroutines.flow.Flow

class ZikrRepository(private val zikrDao: ZikrDao) {

    val allZikrs: Flow<List<ZikrEntity>> = zikrDao.getAllZikr()

    fun getZikrById(id: Long): Flow<ZikrEntity?> = zikrDao.getZikrById(id)

    suspend fun getZikrByIdSync(id: Long): ZikrEntity? = zikrDao.getZikrByIdSync(id)

    suspend fun insertZikr(zikr: ZikrEntity): Long = zikrDao.insertZikr(zikr)

    suspend fun updateZikr(zikr: ZikrEntity) = zikrDao.updateZikr(zikr)

    suspend fun deleteZikr(zikr: ZikrEntity) = zikrDao.deleteZikr(zikr)

    suspend fun deleteZikrById(id: Long) = zikrDao.deleteZikrById(id)

    suspend fun ensureInitialData() {
        if (zikrDao.countZikr() == 0) {
            zikrDao.insertAll(AppDatabase.INITIAL_ZIKR_LIST)
        }
    }
}
