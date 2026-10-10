package com.example.service

import android.app.NotificationChannel
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

class AzanReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_AZAN_ID = "tasbih_azan_reminders"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val prayerName = intent?.getStringExtra(AzanScheduler.EXTRA_PRAYER_NAME) ?: "Prayer"
        val app = context.applicationContext as? TasbihApp

        CoroutineScope(Dispatchers.IO).launch {
            val prefs = app?.userPreferencesRepository?.userPreferencesFlow?.first()
            if (prefs?.azanEnabled != true) return@launch

            val tuneId = prefs.azanTune.ifBlank { "tune_azan_alert" }

            // Play the Azan Islamic tune
            IslamicTunePlayer.playAlarm(context, tuneId)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return@launch

            // Create Azan channel if needed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_AZAN_ID,
                    "Azan Prayer Calls",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for 5 daily prayer times (Azan)"
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                7001,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )

            val prayerUrdu = when (prayerName.lowercase()) {
                "fajr" -> "فجر"
                "dhuhr" -> "ظہر"
                "asr" -> "عصر"
                "maghrib" -> "مغرب"
                "isha" -> "عشاء"
                else -> prayerName
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_AZAN_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Azan Time: $prayerName • وقتِ اذان $prayerUrdu")
                .setContentText("Hayya 'ala-s-Salah! It is time for $prayerName prayer.")
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        "Hayya 'ala-s-Salah! It is time for $prayerName prayer.\n" +
                        "«حَيَّ عَلَى الصَّلَاةِ • حَيَّ عَلَى الْفَلَاحِ»\n" +
                        "نَماز قائم کریں اور اپنے رب کا شکر ادا کریں۔"
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500, 300, 500, 300, 500))
                .build()

            val notificationId = when (prayerName.lowercase()) {
                "fajr" -> 6101
                "dhuhr" -> 6102
                "asr" -> 6103
                "maghrib" -> 6104
                "isha" -> 6105
                else -> 6100
            }

            notificationManager.notify(notificationId, notification)

            // Reschedule this prayer for tomorrow
            val hour = when (prayerName.lowercase()) {
                "fajr" -> prefs.azanFajrHour
                "dhuhr" -> prefs.azanDhuhrHour
                "asr" -> prefs.azanAsrHour
                "maghrib" -> prefs.azanMaghribHour
                "isha" -> prefs.azanIshaHour
                else -> 12
            }
            val minute = when (prayerName.lowercase()) {
                "fajr" -> prefs.azanFajrMinute
                "dhuhr" -> prefs.azanDhuhrMinute
                "asr" -> prefs.azanAsrMinute
                "maghrib" -> prefs.azanMaghribMinute
                "isha" -> prefs.azanIshaMinute
                else -> 0
            }

            AzanScheduler.schedulePrayer(context, prayerName, true, hour, minute)
        }
    }
}
