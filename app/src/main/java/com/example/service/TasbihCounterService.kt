package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.TasbihApp

class TasbihCounterService : Service() {

    companion object {
        const val ACTION_START_OR_UPDATE = "com.aistudio.tasbihcounter.ACTION_UPDATE_SERVICE"
        const val ACTION_STOP = "com.aistudio.tasbihcounter.ACTION_STOP_SERVICE"
        const val EXTRA_COUNT = "extra_count"
        const val EXTRA_TARGET = "extra_target"
        const val EXTRA_ZIKR_NAME = "extra_zikr_name"
        private const val NOTIFICATION_ID = 4001

        fun startOrUpdate(context: Context, count: Int, target: Int, zikrName: String) {
            val intent = Intent(context, TasbihCounterService::class.java).apply {
                action = ACTION_START_OR_UPDATE
                putExtra(EXTRA_COUNT, count)
                putExtra(EXTRA_TARGET, target)
                putExtra(EXTRA_ZIKR_NAME, zikrName)
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, TasbihCounterService::class.java).apply {
                action = ACTION_STOP
            }
            try {
                context.startService(intent)
            } catch (_: Exception) {
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val count = intent?.getIntExtra(EXTRA_COUNT, 0) ?: 0
        val target = intent?.getIntExtra(EXTRA_TARGET, 33) ?: 33
        val zikrName = intent?.getStringExtra(EXTRA_ZIKR_NAME) ?: "Tasbih"

        val notification = buildNotification(count, target, zikrName)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        return START_NOT_STICKY
    }

    private fun buildNotification(count: Int, target: Int, zikrName: String): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            this,
            5001,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val incrementIntent = Intent(this, CounterActionReceiver::class.java).apply {
            action = CounterActionReceiver.ACTION_INCREMENT
        }
        val incrementPendingIntent = PendingIntent.getBroadcast(
            this,
            5002,
            incrementIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val stopIntent = Intent(this, TasbihCounterService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            5003,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        return NotificationCompat.Builder(this, TasbihApp.CHANNEL_SERVICE_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(zikrName)
            .setContentText("Count: $count / $target (Remaining: ${maxOf(0, target - count)})")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(openPendingIntent)
            .addAction(0, "+1 Count", incrementPendingIntent)
            .addAction(0, "End Session", stopPendingIntent)
            .setProgress(target, count % (target + 1), false)
            .build()
    }
}
