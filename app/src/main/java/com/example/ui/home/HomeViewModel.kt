package com.example.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.TasbihApp
import com.example.data.local.AppDatabase
import com.example.data.local.entity.TasbihSessionEntity
import com.example.data.local.entity.ZikrEntity
import com.example.data.model.VolumeMode
import com.example.service.FeedbackManager
import com.example.service.TasbihCounterService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val currentZikr: ZikrEntity? = null,
    val count: Int = 0,
    val target: Int = 33,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isTargetCompleted: Boolean = false,
    val volumeMode: VolumeMode = VolumeMode.BOTH,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val activeSessionId: Long? = null
) {
    val progress: Float
        get() = if (target > 0) (count.toFloat() / target).coerceIn(0f, 1f) else 0f

    val remaining: Int
        get() = maxOf(0, target - count)
}

sealed interface HomeEvent {
    data object TargetReached : HomeEvent
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as TasbihApp
    private val zikrRepository = app.zikrRepository
    private val sessionRepository = app.sessionRepository
    private val preferencesRepository = app.userPreferencesRepository
    private val feedbackManager = FeedbackManager(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    private var lastKeyTimestamp: Long = 0L

    init {
        initializeSession()
        observePreferences()
    }

    private fun initializeSession() {
        viewModelScope.launch {
            try {
                // Ensure initial dhikr are populated
                zikrRepository.ensureInitialData()

                // Check active session or create initial one
                var session = sessionRepository.getActiveSessionSync()
                val prefs = preferencesRepository.userPreferencesFlow.first()

                if (session == null) {
                    val activeZikr = zikrRepository.getZikrByIdSync(prefs.activeZikrId)
                        ?: AppDatabase.INITIAL_ZIKR_LIST.first()
                    val target = activeZikr.defaultTarget ?: prefs.activeTarget
                    val newSession = TasbihSessionEntity(
                        zikrId = activeZikr.id,
                        zikrNameSnapshot = activeZikr.name,
                        count = 0,
                        target = target,
                        status = "ACTIVE"
                    )
                    val id = sessionRepository.insertSession(newSession)
                    session = newSession.copy(id = id)
                }

                val zikr = session.zikrId?.let { zikrRepository.getZikrByIdSync(it) }
                    ?: ZikrEntity(
                        id = 0,
                        name = session.zikrNameSnapshot,
                        arabicText = "",
                        urduTranslation = "",
                        defaultTarget = session.target
                    )

                _uiState.value = _uiState.value.copy(
                    currentZikr = zikr,
                    count = session.count,
                    target = session.target,
                    activeSessionId = session.id,
                    isTargetCompleted = session.count >= session.target,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Unable to load counter session: ${e.localizedMessage}"
                )
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.userPreferencesFlow.collectLatest { prefs ->
                _uiState.value = _uiState.value.copy(
                    volumeMode = prefs.volumeMode,
                    vibrationEnabled = prefs.vibrationEnabled,
                    soundEnabled = prefs.soundEnabled
                )
            }
        }
    }

    fun increment() {
        val state = _uiState.value
        val newCount = state.count + 1
        val wasCompletedBefore = state.count >= state.target
        val isCompletedNow = newCount >= state.target && !wasCompletedBefore

        _uiState.value = state.copy(
            count = newCount,
            isTargetCompleted = newCount >= state.target
        )

        // Feedback
        feedbackManager.onCountIncrement(state.vibrationEnabled, state.soundEnabled)
        if (isCompletedNow) {
            feedbackManager.onTargetCompleted(state.vibrationEnabled, state.soundEnabled)
            viewModelScope.launch {
                _events.emit(HomeEvent.TargetReached)
            }
        }

        // Persist
        persistCount(newCount, isCompletedNow)
    }

    fun undo() {
        val state = _uiState.value
        if (state.count <= 0) return
        val newCount = state.count - 1

        _uiState.value = state.copy(
            count = newCount,
            isTargetCompleted = newCount >= state.target
        )

        feedbackManager.onCountIncrement(state.vibrationEnabled, false)
        persistCount(newCount, false)
    }

    fun reset() {
        val state = _uiState.value
        viewModelScope.launch {
            // Archive current session if it had counts
            val currentSessionId = state.activeSessionId
            if (currentSessionId != null && state.count > 0) {
                val currentSession = sessionRepository.getActiveSessionSync()
                if (currentSession != null) {
                    sessionRepository.updateSession(
                        currentSession.copy(
                            endedAt = System.currentTimeMillis(),
                            status = if (currentSession.count >= currentSession.target) "COMPLETED" else "PAUSED"
                        )
                    )
                }
            }

            // Create new fresh active session
            val zikr = state.currentZikr
            val newSession = TasbihSessionEntity(
                zikrId = zikr?.id,
                zikrNameSnapshot = zikr?.name ?: "Tasbih",
                count = 0,
                target = state.target,
                status = "ACTIVE"
            )
            val newId = sessionRepository.insertSession(newSession)

            _uiState.value = state.copy(
                count = 0,
                activeSessionId = newId,
                isTargetCompleted = false
            )

            TasbihCounterService.startOrUpdate(
                app,
                0,
                state.target,
                zikr?.name ?: "Tasbih"
            )
        }
    }

    fun selectZikr(zikr: ZikrEntity) {
        val state = _uiState.value
        if (state.currentZikr?.id == zikr.id) return

        viewModelScope.launch {
            // Archive previous active session if it had progress
            val activeSession = sessionRepository.getActiveSessionSync()
            if (activeSession != null && activeSession.count > 0) {
                sessionRepository.updateSession(
                    activeSession.copy(
                        endedAt = System.currentTimeMillis(),
                        status = if (activeSession.count >= activeSession.target) "COMPLETED" else "PAUSED"
                    )
                )
            }

            val target = zikr.defaultTarget ?: state.target
            val newSession = TasbihSessionEntity(
                zikrId = zikr.id,
                zikrNameSnapshot = zikr.name,
                count = 0,
                target = target,
                status = "ACTIVE"
            )
            val newId = sessionRepository.insertSession(newSession)
            preferencesRepository.setActiveZikrId(zikr.id)
            preferencesRepository.setActiveTarget(target)

            _uiState.value = state.copy(
                currentZikr = zikr,
                count = 0,
                target = target,
                activeSessionId = newId,
                isTargetCompleted = false
            )

            TasbihCounterService.startOrUpdate(app, 0, target, zikr.name)
        }
    }

    fun updateTarget(newTarget: Int) {
        if (newTarget <= 0) return
        val state = _uiState.value
        viewModelScope.launch {
            preferencesRepository.setActiveTarget(newTarget)
            val activeSession = sessionRepository.getActiveSessionSync()
            if (activeSession != null) {
                sessionRepository.updateSession(activeSession.copy(target = newTarget))
            }
            _uiState.value = state.copy(
                target = newTarget,
                isTargetCompleted = state.count >= newTarget
            )
            TasbihCounterService.startOrUpdate(
                app,
                state.count,
                newTarget,
                state.currentZikr?.name ?: "Tasbih"
            )
        }
    }

    fun onVolumeKeyPressed(isVolumeUp: Boolean): Boolean {
        val mode = _uiState.value.volumeMode
        if (mode == VolumeMode.DISABLED) return false

        val shouldCount = when (mode) {
            VolumeMode.BOTH -> true
            VolumeMode.VOLUME_UP -> isVolumeUp
            VolumeMode.VOLUME_DOWN -> !isVolumeUp
            VolumeMode.DISABLED -> false
        }

        if (shouldCount) {
            val now = System.currentTimeMillis()
            // 150ms debounce to prevent repeated triggers
            if (now - lastKeyTimestamp > 150) {
                lastKeyTimestamp = now
                increment()
            }
            return true
        }
        return false
    }

    private fun persistCount(count: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                val session = sessionRepository.getActiveSessionSync()
                if (session != null) {
                    val completedTime = if (isCompleted) System.currentTimeMillis() else session.completedAt
                    sessionRepository.updateSession(
                        session.copy(
                            count = count,
                            completedAt = completedTime,
                            status = if (count >= session.target) "COMPLETED" else "ACTIVE"
                        )
                    )
                }
                _uiState.value.currentZikr?.let { zikr ->
                    TasbihCounterService.startOrUpdate(app, count, _uiState.value.target, zikr.name)
                }
            } catch (_: Exception) {
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        feedbackManager.release()
    }
}
