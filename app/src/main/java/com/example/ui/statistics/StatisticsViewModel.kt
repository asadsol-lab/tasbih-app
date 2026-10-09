package com.example.ui.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.TasbihApp
import com.example.util.DateTimeUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class DailyStat(
    val dayLabel: String,
    val count: Int,
    val timestamp: Long
)

data class StatisticsUiState(
    val todayTotal: Int = 0,
    val totalAllTime: Int = 0,
    val completedSessions: Int = 0,
    val totalSessions: Int = 0,
    val weeklyStats: List<DailyStat> = emptyList(),
    val perDhikrTotals: List<Pair<String, Int>> = emptyList()
)

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionRepository = (application as TasbihApp).sessionRepository

    val uiState: StateFlow<StatisticsUiState> = sessionRepository.allSessions
        .map { sessions ->
            val now = System.currentTimeMillis()
            val startOfToday = DateTimeUtils.getStartOfDay(now)

            // Today total count
            val todayTotal = sessions
                .filter { it.startedAt >= startOfToday }
                .sumOf { it.count }

            val totalAllTime = sessions.sumOf { it.count }
            val completedSessions = sessions.count { it.status == "COMPLETED" || it.count >= it.target }
            val totalSessions = sessions.size

            // Past 7 days calculation
            val calendar = Calendar.getInstance()
            val weeklyStats = (6 downTo 0).map { daysAgo ->
                val dayStart = DateTimeUtils.getStartOfDaysAgo(daysAgo)
                val dayEnd = dayStart + (24 * 60 * 60 * 1000L)
                val dayLabel = DateTimeUtils.getDayLabel(dayStart)
                val dayCount = sessions
                    .filter { it.startedAt in dayStart until dayEnd }
                    .sumOf { it.count }
                DailyStat(dayLabel = dayLabel, count = dayCount, timestamp = dayStart)
            }

            // Per dhikr totals
            val perDhikrTotals = sessions
                .groupBy { it.zikrNameSnapshot }
                .mapValues { entry -> entry.value.sumOf { it.count } }
                .toList()
                .sortedByDescending { it.second }
                .take(6)

            StatisticsUiState(
                todayTotal = todayTotal,
                totalAllTime = totalAllTime,
                completedSessions = completedSessions,
                totalSessions = totalSessions,
                weeklyStats = weeklyStats,
                perDhikrTotals = perDhikrTotals
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatisticsUiState()
        )
}
