package com.example.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.TasbihApp
import com.example.data.model.VolumeMode
import com.example.data.repository.UserPreferences
import com.example.service.AzanScheduler
import com.example.service.ReminderScheduler
import com.example.ui.theme.AppThemeMode
import com.example.util.IslamicTunePlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

    private val _playingTuneId = MutableStateFlow<String?>(null)
    val playingTuneId: StateFlow<String?> = _playingTuneId.asStateFlow()

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

    fun setReminder(enabled: Boolean, hour: Int, minute: Int, tune: String? = null) {
        viewModelScope.launch {
            preferencesRepository.setReminderSettings(enabled, hour, minute, tune)
            if (enabled) {
                ReminderScheduler.scheduleDailyReminder(app, hour, minute)
            } else {
                ReminderScheduler.cancelReminder(app)
            }
        }
    }

    fun setReminderTune(tune: String) {
        viewModelScope.launch {
            preferencesRepository.setReminderTune(tune)
        }
    }

    // Azan / Prayer Times
    fun setAzanEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAzanEnabled(enabled)
            val updatedPrefs = preferencesRepository.userPreferencesFlow.first().copy(azanEnabled = enabled)
            AzanScheduler.scheduleAllPrayers(app, updatedPrefs)
        }
    }

    fun setAzanTune(tune: String) {
        viewModelScope.launch {
            preferencesRepository.setAzanTune(tune)
        }
    }

    fun setPrayerTime(prayerName: String, enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesRepository.setPrayerTime(prayerName, enabled, hour, minute)
            val currentPrefs = preferencesRepository.userPreferencesFlow.first()
            if (currentPrefs.azanEnabled) {
                AzanScheduler.schedulePrayer(app, prayerName, enabled, hour, minute)
            }
        }
    }

    // Audio preview
    fun playTunePreview(tuneId: String) {
        if (_playingTuneId.value == tuneId) {
            stopTunePreview()
        } else {
            _playingTuneId.value = tuneId
            IslamicTunePlayer.playPreview(app, tuneId) {
                _playingTuneId.value = null
            }
        }
    }

    fun stopTunePreview() {
        IslamicTunePlayer.stop()
        _playingTuneId.value = null
    }

    override fun onCleared() {
        super.onCleared()
        IslamicTunePlayer.stop()
    }
}
