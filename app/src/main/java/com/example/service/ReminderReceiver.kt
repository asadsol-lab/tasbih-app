package com.example.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.TasbihApp
import com.example.util.IslamicTunePlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val app = context.applicationContext as? TasbihApp

        CoroutineScope(Dispatchers.IO).launch {
            val prefs = app?.userPreferencesRepository?.userPreferencesFlow?.first()
            val tuneId = prefs?.reminderTune ?: "tune_subhanallah"
            val tuneTitle = IslamicTunePlayer.getTuneTitle(tuneId)

            // Play the Islamic tune
            IslamicTunePlayer.playAlarm(context, tuneId)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return@launch

            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                2001,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )

            val notification = NotificationCompat.Builder(context, TasbihApp.CHANNEL_REMINDERS_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Daily Dhikr Reminder • ذکر کا وقت")
                .setContentText("Dedicate a peaceful moment for your daily tasbih & remembrance of Allah.")
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        "Dedicate a peaceful moment for your daily tasbih and remembrance of Allah.\n" +
                        "Tune: $tuneTitle\n" +
                        "«أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ»"
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 300, 200, 300))
                .build()

            notificationManager.notify(3001, notification)

            // Reschedule for next day to ensure continuous daily chain even when app stays closed
            if (prefs?.reminderEnabled == true) {
                ReminderScheduler.scheduleDailyReminder(
                    context,
                    prefs.reminderHour,
                    prefs.reminderMinute
                )
            }
        }
    }
}
