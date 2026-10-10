package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.VolumeMode
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tasbih_preferences")

data class UserPreferences(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val volumeMode: VolumeMode = VolumeMode.BOTH,
    val dailyGoal: Int = 300,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val reminderTune: String = "tune_subhanallah",
    val activeZikrId: Long = 1L,
    val activeTarget: Int = 33,
    // Azan Settings
    val azanEnabled: Boolean = false,
    val azanTune: String = "tune_azan_alert",
    val azanFajrEnabled: Boolean = true,
    val azanFajrHour: Int = 5,
    val azanFajrMinute: Int = 0,
    val azanDhuhrEnabled: Boolean = true,
    val azanDhuhrHour: Int = 13,
    val azanDhuhrMinute: Int = 15,
    val azanAsrEnabled: Boolean = true,
    val azanAsrHour: Int = 16,
    val azanAsrMinute: Int = 45,
    val azanMaghribEnabled: Boolean = true,
    val azanMaghribHour: Int = 18,
    val azanMaghribMinute: Int = 30,
    val azanIshaEnabled: Boolean = true,
    val azanIshaHour: Int = 20,
    val azanIshaMinute: Int = 0
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VOLUME_MODE = stringPreferencesKey("volume_mode")
        val DAILY_GOAL = intPreferencesKey("daily_goal")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val REMINDER_TUNE = stringPreferencesKey("reminder_tune")
        val ACTIVE_ZIKR_ID = longPreferencesKey("active_zikr_id")
        val ACTIVE_TARGET = intPreferencesKey("active_target")

        // Azan Keys
        val AZAN_ENABLED = booleanPreferencesKey("azan_enabled")
        val AZAN_TUNE = stringPreferencesKey("azan_tune")
        val AZAN_FAJR_ENABLED = booleanPreferencesKey("azan_fajr_enabled")
        val AZAN_FAJR_HOUR = intPreferencesKey("azan_fajr_hour")
        val AZAN_FAJR_MINUTE = intPreferencesKey("azan_fajr_minute")
        val AZAN_DHUHR_ENABLED = booleanPreferencesKey("azan_dhuhr_enabled")
        val AZAN_DHUHR_HOUR = intPreferencesKey("azan_dhuhr_hour")
        val AZAN_DHUHR_MINUTE = intPreferencesKey("azan_dhuhr_minute")
        val AZAN_ASR_ENABLED = booleanPreferencesKey("azan_asr_enabled")
        val AZAN_ASR_HOUR = intPreferencesKey("azan_asr_hour")
        val AZAN_ASR_MINUTE = intPreferencesKey("azan_asr_minute")
        val AZAN_MAGHRIB_ENABLED = booleanPreferencesKey("azan_maghrib_enabled")
        val AZAN_MAGHRIB_HOUR = intPreferencesKey("azan_maghrib_hour")
        val AZAN_MAGHRIB_MINUTE = intPreferencesKey("azan_maghrib_minute")
        val AZAN_ISHA_ENABLED = booleanPreferencesKey("azan_isha_enabled")
        val AZAN_ISHA_HOUR = intPreferencesKey("azan_isha_hour")
        val AZAN_ISHA_MINUTE = intPreferencesKey("azan_isha_minute")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            val themeStr = preferences[PreferencesKeys.THEME_MODE] ?: AppThemeMode.SYSTEM.name
            val themeMode = runCatching { AppThemeMode.valueOf(themeStr) }.getOrDefault(AppThemeMode.SYSTEM)

            val volumeStr = preferences[PreferencesKeys.VOLUME_MODE] ?: VolumeMode.BOTH.name
            val volumeMode = runCatching { VolumeMode.valueOf(volumeStr) }.getOrDefault(VolumeMode.BOTH)

            UserPreferences(
                themeMode = themeMode,
                vibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true,
                soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
                volumeMode = volumeMode,
                dailyGoal = preferences[PreferencesKeys.DAILY_GOAL] ?: 300,
                reminderEnabled = preferences[PreferencesKeys.REMINDER_ENABLED] ?: false,
                reminderHour = preferences[PreferencesKeys.REMINDER_HOUR] ?: 20,
                reminderMinute = preferences[PreferencesKeys.REMINDER_MINUTE] ?: 0,
                reminderTune = preferences[PreferencesKeys.REMINDER_TUNE] ?: "tune_subhanallah",
                activeZikrId = preferences[PreferencesKeys.ACTIVE_ZIKR_ID] ?: 1L,
                activeTarget = preferences[PreferencesKeys.ACTIVE_TARGET] ?: 33,

                // Azan
                azanEnabled = preferences[PreferencesKeys.AZAN_ENABLED] ?: false,
                azanTune = preferences[PreferencesKeys.AZAN_TUNE] ?: "tune_azan_alert",
                azanFajrEnabled = preferences[PreferencesKeys.AZAN_FAJR_ENABLED] ?: true,
                azanFajrHour = preferences[PreferencesKeys.AZAN_FAJR_HOUR] ?: 5,
                azanFajrMinute = preferences[PreferencesKeys.AZAN_FAJR_MINUTE] ?: 0,
                azanDhuhrEnabled = preferences[PreferencesKeys.AZAN_DHUHR_ENABLED] ?: true,
                azanDhuhrHour = preferences[PreferencesKeys.AZAN_DHUHR_HOUR] ?: 13,
                azanDhuhrMinute = preferences[PreferencesKeys.AZAN_DHUHR_MINUTE] ?: 15,
                azanAsrEnabled = preferences[PreferencesKeys.AZAN_ASR_ENABLED] ?: true,
                azanAsrHour = preferences[PreferencesKeys.AZAN_ASR_HOUR] ?: 16,
                azanAsrMinute = preferences[PreferencesKeys.AZAN_ASR_MINUTE] ?: 45,
                azanMaghribEnabled = preferences[PreferencesKeys.AZAN_MAGHRIB_ENABLED] ?: true,
                azanMaghribHour = preferences[PreferencesKeys.AZAN_MAGHRIB_HOUR] ?: 18,
                azanMaghribMinute = preferences[PreferencesKeys.AZAN_MAGHRIB_MINUTE] ?: 30,
                azanIshaEnabled = preferences[PreferencesKeys.AZAN_ISHA_ENABLED] ?: true,
                azanIshaHour = preferences[PreferencesKeys.AZAN_ISHA_HOUR] ?: 20,
                azanIshaMinute = preferences[PreferencesKeys.AZAN_ISHA_MINUTE] ?: 0
            )
        }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setVolumeMode(mode: VolumeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VOLUME_MODE] = mode.name
        }
    }

    suspend fun setDailyGoal(goal: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_GOAL] = goal
        }
    }

    suspend fun setReminderSettings(enabled: Boolean, hour: Int, minute: Int, tune: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_ENABLED] = enabled
            preferences[PreferencesKeys.REMINDER_HOUR] = hour
            preferences[PreferencesKeys.REMINDER_MINUTE] = minute
            if (tune != null) {
                preferences[PreferencesKeys.REMINDER_TUNE] = tune
            }
        }
    }

    suspend fun setReminderTune(tune: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_TUNE] = tune
        }
    }

    suspend fun setActiveZikrId(zikrId: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACTIVE_ZIKR_ID] = zikrId
        }
    }

    suspend fun setActiveTarget(target: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACTIVE_TARGET] = target
        }
    }

    // Azan preference updates
    suspend fun setAzanEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AZAN_ENABLED] = enabled
        }
    }

    suspend fun setAzanTune(tune: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AZAN_TUNE] = tune
        }
    }

    suspend fun setPrayerTime(prayerName: String, enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            when (prayerName.lowercase()) {
                "fajr" -> {
                    preferences[PreferencesKeys.AZAN_FAJR_ENABLED] = enabled
                    preferences[PreferencesKeys.AZAN_FAJR_HOUR] = hour
                    preferences[PreferencesKeys.AZAN_FAJR_MINUTE] = minute
                }
                "dhuhr" -> {
                    preferences[PreferencesKeys.AZAN_DHUHR_ENABLED] = enabled
                    preferences[PreferencesKeys.AZAN_DHUHR_HOUR] = hour
                    preferences[PreferencesKeys.AZAN_DHUHR_MINUTE] = minute
                }
                "asr" -> {
                    preferences[PreferencesKeys.AZAN_ASR_ENABLED] = enabled
                    preferences[PreferencesKeys.AZAN_ASR_HOUR] = hour
                    preferences[PreferencesKeys.AZAN_ASR_MINUTE] = minute
                }
                "maghrib" -> {
                    preferences[PreferencesKeys.AZAN_MAGHRIB_ENABLED] = enabled
                    preferences[PreferencesKeys.AZAN_MAGHRIB_HOUR] = hour
                    preferences[PreferencesKeys.AZAN_MAGHRIB_MINUTE] = minute
                }
                "isha" -> {
                    preferences[PreferencesKeys.AZAN_ISHA_ENABLED] = enabled
                    preferences[PreferencesKeys.AZAN_ISHA_HOUR] = hour
                    preferences[PreferencesKeys.AZAN_ISHA_MINUTE] = minute
                }
            }
        }
    }
}
