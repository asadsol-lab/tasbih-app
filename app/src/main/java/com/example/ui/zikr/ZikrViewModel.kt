package com.example.ui.zikr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.TasbihApp
import com.example.data.local.entity.ZikrEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ZikrUiState(
    val searchQuery: String = "",
    val errorMessage: String? = null
)

class ZikrViewModel(application: Application) : AndroidViewModel(application) {

    private val zikrRepository = (application as TasbihApp).zikrRepository

    private val _uiState = MutableStateFlow(ZikrUiState())
    val uiState: StateFlow<ZikrUiState> = _uiState.asStateFlow()

    val zikrList: StateFlow<List<ZikrEntity>> = combine(
        zikrRepository.allZikrs,
        _uiState
    ) { list, state ->
        if (state.searchQuery.isBlank()) {
            list
        } else {
            val q = state.searchQuery.trim().lowercase()
            list.filter {
                it.name.lowercase().contains(q) ||
                it.urduTranslation.lowercase().contains(q) ||
                it.arabicText.contains(state.searchQuery.trim())
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    suspend fun getZikrById(id: Long): ZikrEntity? {
        return zikrRepository.getZikrByIdSync(id)
    }

    fun saveZikr(
        id: Long?,
        name: String,
        arabicText: String,
        urduTranslation: String,
        target: Int?,
        onSuccess: () -> Unit
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Dhikr name cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                if (id == null || id == 0L) {
                    val newEntity = ZikrEntity(
                        name = trimmedName,
                        arabicText = arabicText.trim(),
                        urduTranslation = urduTranslation.trim(),
                        defaultTarget = target?.takeIf { it > 0 },
                        isCustom = true
                    )
                    zikrRepository.insertZikr(newEntity)
                } else {
                    val existing = zikrRepository.getZikrByIdSync(id)
                    if (existing != null) {
                        val updated = existing.copy(
                            name = trimmedName,
                            arabicText = arabicText.trim(),
                            urduTranslation = urduTranslation.trim(),
                            defaultTarget = target?.takeIf { it > 0 },
                            updatedAt = System.currentTimeMillis()
                        )
                        zikrRepository.updateZikr(updated)
                    }
                }
                _uiState.value = _uiState.value.copy(errorMessage = null)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Failed to save: ${e.localizedMessage}")
            }
        }
    }

    fun deleteZikr(zikr: ZikrEntity) {
        if (!zikr.isCustom) return
        viewModelScope.launch {
            zikrRepository.deleteZikr(zikr)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
