package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.TasbihApp
import com.example.data.local.entity.TasbihSessionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CounterActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_INCREMENT = "com.aistudio.tasbihcounter.ACTION_INCREMENT"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION_INCREMENT) {
            val app = context.applicationContext as? TasbihApp ?: return
            CoroutineScope(Dispatchers.IO).launch {
                val session = app.sessionRepository.getActiveSessionSync()
                if (session != null) {
                    val newCount = session.count + 1
                    val isCompleted = newCount >= session.target && session.completedAt == null
                    val completedTime = if (isCompleted) System.currentTimeMillis() else session.completedAt
                    val updatedSession = session.copy(
                        count = newCount,
                        completedAt = completedTime,
                        status = if (isCompleted) "COMPLETED" else session.status
                    )
                    app.sessionRepository.updateSession(updatedSession)
                    TasbihCounterService.startOrUpdate(
                        context,
                        newCount,
                        session.target,
                        session.zikrNameSnapshot
                    )
                }
            }
        }
    }
}
