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
    val activeZikrId: Long = 1L,
    val activeTarget: Int = 33
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
        val ACTIVE_ZIKR_ID = longPreferencesKey("active_zikr_id")
        val ACTIVE_TARGET = intPreferencesKey("active_target")
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
                activeZikrId = preferences[PreferencesKeys.ACTIVE_ZIKR_ID] ?: 1L,
                activeTarget = preferences[PreferencesKeys.ACTIVE_TARGET] ?: 33
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

    suspend fun setReminderSettings(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_ENABLED] = enabled
            preferences[PreferencesKeys.REMINDER_HOUR] = hour
            preferences[PreferencesKeys.REMINDER_MINUTE] = minute
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
}
