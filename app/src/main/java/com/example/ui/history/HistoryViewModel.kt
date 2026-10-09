package com.example.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.TasbihApp
import com.example.data.local.entity.TasbihSessionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionRepository = (application as TasbihApp).sessionRepository

    val historySessions: StateFlow<List<TasbihSessionEntity>> = sessionRepository.historicalSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            sessionRepository.deleteSessionById(sessionId)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            sessionRepository.clearHistory()
        }
    }
}
