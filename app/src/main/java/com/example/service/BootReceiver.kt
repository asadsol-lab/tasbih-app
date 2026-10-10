package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.TasbihApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED || intent?.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val app = context.applicationContext as? TasbihApp ?: return
            CoroutineScope(Dispatchers.IO).launch {
                val prefs = app.userPreferencesRepository.userPreferencesFlow.first()
                // Reschedule Daily Reminders if active
                if (prefs.reminderEnabled) {
                    ReminderScheduler.scheduleDailyReminder(
                        context,
                        prefs.reminderHour,
                        prefs.reminderMinute
                    )
                }
                // Reschedule Azan prayer calls if active
                if (prefs.azanEnabled) {
                    AzanScheduler.scheduleAllPrayers(context, prefs)
                }
            }
        }
    }
}
