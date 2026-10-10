package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.local.AppDatabase
import com.example.data.repository.SessionRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.data.repository.ZikrRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasbihApp : Application() {

    companion object {
        const val CHANNEL_REMINDERS_ID = "tasbih_daily_reminders"
        const val CHANNEL_SERVICE_ID = "tasbih_active_counter"
        lateinit var instance: TasbihApp
            private set
    }

    val database by lazy { AppDatabase.getDatabase(this) }
    val zikrRepository by lazy { ZikrRepository(database.zikrDao()) }
    val sessionRepository by lazy { SessionRepository(database.sessionDao()) }
    val userPreferencesRepository by lazy { UserPreferencesRepository(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
        // Pre-populate if empty
        CoroutineScope(Dispatchers.IO).launch {
            zikrRepository.ensureInitialData()
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Daily Reminders channel
            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDERS_ID,
                "Daily Dhikr Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily notifications reminding you to recite your dhikr and tasbih"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(reminderChannel)

            // Azan Prayer Calls channel
            val azanChannel = NotificationChannel(
                com.example.service.AzanReceiver.CHANNEL_AZAN_ID,
                "Azan Prayer Calls",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily 5 Azan prayer call alerts"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(azanChannel)

            // Active counter notification channel
            val serviceChannel = NotificationChannel(
                CHANNEL_SERVICE_ID,
                "Active Counter Session",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows the active tasbih count in the notification drawer"
                enableVibration(false)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }
}
