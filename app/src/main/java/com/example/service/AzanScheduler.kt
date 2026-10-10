package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.repository.UserPreferences
import java.util.Calendar

object AzanScheduler {

    const val ACTION_AZAN = "com.aistudio.tasbihcounter.ACTION_AZAN"
    const val EXTRA_PRAYER_NAME = "extra_prayer_name"

    private val PRAYER_REQUEST_CODES = mapOf(
        "Fajr" to 6001,
        "Dhuhr" to 6002,
        "Asr" to 6003,
        "Maghrib" to 6004,
        "Isha" to 6005
    )

    fun scheduleAllPrayers(context: Context, prefs: UserPreferences) {
        if (!prefs.azanEnabled) {
            cancelAllPrayers(context)
            return
        }

        schedulePrayer(context, "Fajr", prefs.azanFajrEnabled, prefs.azanFajrHour, prefs.azanFajrMinute)
        schedulePrayer(context, "Dhuhr", prefs.azanDhuhrEnabled, prefs.azanDhuhrHour, prefs.azanDhuhrMinute)
        schedulePrayer(context, "Asr", prefs.azanAsrEnabled, prefs.azanAsrHour, prefs.azanAsrMinute)
        schedulePrayer(context, "Maghrib", prefs.azanMaghribEnabled, prefs.azanMaghribHour, prefs.azanMaghribMinute)
        schedulePrayer(context, "Isha", prefs.azanIshaEnabled, prefs.azanIshaHour, prefs.azanIshaMinute)
    }

    fun schedulePrayer(context: Context, prayerName: String, enabled: Boolean, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val requestCode = PRAYER_REQUEST_CODES[prayerName] ?: 6000

        val intent = Intent(context, AzanReceiver::class.java).apply {
            action = ACTION_AZAN
            putExtra(EXTRA_PRAYER_NAME, prayerName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        if (!enabled) {
            try {
                alarmManager.cancel(pendingIntent)
            } catch (_: Exception) {
            }
            return
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val triggerTime = calendar.timeInMillis

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    } else {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (_: Exception) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun cancelAllPrayers(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        for ((prayerName, requestCode) in PRAYER_REQUEST_CODES) {
            val intent = Intent(context, AzanReceiver::class.java).apply {
                action = ACTION_AZAN
                putExtra(EXTRA_PRAYER_NAME, prayerName)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            )
            try {
                alarmManager.cancel(pendingIntent)
            } catch (_: Exception) {
            }
        }
    }
}
