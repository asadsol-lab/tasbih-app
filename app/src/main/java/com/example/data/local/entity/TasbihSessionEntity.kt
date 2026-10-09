package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasbih_sessions",
    indices = [
        Index(value = ["startedAt"]),
        Index(value = ["status"]),
        Index(value = ["zikrId"])
    ]
)
data class TasbihSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val zikrId: Long? = null,
    val zikrNameSnapshot: String,
    val count: Int = 0,
    val target: Int = 33,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,
    val completedAt: Long? = null,
    val status: String = "ACTIVE"
)
