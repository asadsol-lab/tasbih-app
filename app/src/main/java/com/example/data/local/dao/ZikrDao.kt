package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ZikrEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ZikrDao {
    @Query("SELECT * FROM zikr_table ORDER BY isCustom ASC, id ASC")
    fun getAllZikr(): Flow<List<ZikrEntity>>

    @Query("SELECT * FROM zikr_table WHERE id = :id")
    fun getZikrById(id: Long): Flow<ZikrEntity?>

    @Query("SELECT * FROM zikr_table WHERE id = :id")
    suspend fun getZikrByIdSync(id: Long): ZikrEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZikr(zikr: ZikrEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(zikrs: List<ZikrEntity>)

    @Update
    suspend fun updateZikr(zikr: ZikrEntity)

    @Delete
    suspend fun deleteZikr(zikr: ZikrEntity)

    @Query("DELETE FROM zikr_table WHERE id = :id")
    suspend fun deleteZikrById(id: Long)

    @Query("SELECT COUNT(*) FROM zikr_table")
    suspend fun countZikr(): Int
}
