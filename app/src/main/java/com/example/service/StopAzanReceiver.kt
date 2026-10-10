package com.example.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.util.IslamicTunePlayer

class StopAzanReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        IslamicTunePlayer.stop()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val notifId = intent?.getIntExtra("notif_id", -1) ?: -1
        if (notifId != -1) {
            notificationManager?.cancel(notifId)
        }
    }
}
