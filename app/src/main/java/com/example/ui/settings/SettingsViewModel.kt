package com.example.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.TasbihApp
import com.example.data.model.VolumeMode
import com.example.data.repository.UserPreferences
import com.example.service.ReminderScheduler
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as TasbihApp
    private val preferencesRepository = app.userPreferencesRepository

    val preferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setVibrationEnabled(enabled)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setSoundEnabled(enabled)
        }
    }

    fun setVolumeMode(mode: VolumeMode) {
        viewModelScope.launch {
            preferencesRepository.setVolumeMode(mode)
        }
    }

    fun setReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesRepository.setReminderSettings(enabled, hour, minute)
            if (enabled) {
                ReminderScheduler.scheduleDailyReminder(app, hour, minute)
            } else {
                ReminderScheduler.cancelReminder(app)
            }
        }
    }
}
