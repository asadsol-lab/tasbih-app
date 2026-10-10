package com.example.ui.azan

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.settings.SettingsViewModel
import com.example.ui.theme.EmeraldPrimary
import com.example.util.IslamicTunePlayer
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzanSettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val playingTuneId by viewModel.playingTuneId.collectAsStateWithLifecycle()

    var activeTimePickerPrayer by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopTunePreview()
        }
    }

    // Time picker dialog for prayer times
    activeTimePickerPrayer?.let { prayer ->
        val currentHour = when (prayer) {
            "Fajr" -> prefs.azanFajrHour
            "Dhuhr" -> prefs.azanDhuhrHour
            "Asr" -> prefs.azanAsrHour
            "Maghrib" -> prefs.azanMaghribHour
            "Isha" -> prefs.azanIshaHour
            else -> 12
        }
        val currentMinute = when (prayer) {
            "Fajr" -> prefs.azanFajrMinute
            "Dhuhr" -> prefs.azanDhuhrMinute
            "Asr" -> prefs.azanAsrMinute
            "Maghrib" -> prefs.azanMaghribMinute
            "Isha" -> prefs.azanIshaMinute
            else -> 0
        }
        val currentEnabled = when (prayer) {
            "Fajr" -> prefs.azanFajrEnabled
            "Dhuhr" -> prefs.azanDhuhrEnabled
            "Asr" -> prefs.azanAsrEnabled
            "Maghrib" -> prefs.azanMaghribEnabled
            "Isha" -> prefs.azanIshaEnabled
            else -> true
        }

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                viewModel.setPrayerTime(prayer, currentEnabled, hourOfDay, minute)
                activeTimePickerPrayer = null
            },
            currentHour,
            currentMinute,
            false
        ).apply {
            setOnDismissListener { activeTimePickerPrayer = null }
            show()
        }
    }

    Scaffold(
        modifier = modifier.testTag("azan_settings_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Azan & Prayer Times • اوقاتِ نماز و اذان") },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("azan_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Master Azan Toggle Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (prefs.azanEnabled)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (prefs.azanEnabled) Icons.Default.Campaign else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = if (prefs.azanEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(28.dp)
                        )
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = "Azan Reminders • اذان کی یاد دہانی",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (prefs.azanEnabled)
                                    "Active: Alerts will trigger on set prayer times"
                                else
                                    "Disabled: Turn on to get Azan alerts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = prefs.azanEnabled,
                        onCheckedChange = { viewModel.setAzanEnabled(it) },
                        modifier = Modifier.testTag("switch_azan_master")
                    )
                }
            }

            // 5 Daily Prayers Setup
            Text(
                text = "Daily 5 Prayers (اپنی مرضی سے ٹائم اور آن/آف سیٹ کریں)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            // Fajr
            PrayerTimeRow(
                name = "Fajr",
                urduName = "نمازِ فجر",
                hour = prefs.azanFajrHour,
                minute = prefs.azanFajrMinute,
                isEnabled = prefs.azanFajrEnabled,
                isGlobalEnabled = prefs.azanEnabled,
                onToggle = { viewModel.setPrayerTime("Fajr", it, prefs.azanFajrHour, prefs.azanFajrMinute) },
                onTimeClick = { activeTimePickerPrayer = "Fajr" }
            )

            // Dhuhr
            PrayerTimeRow(
                name = "Dhuhr",
                urduName = "نمازِ ظہر",
                hour = prefs.azanDhuhrHour,
                minute = prefs.azanDhuhrMinute,
                isEnabled = prefs.azanDhuhrEnabled,
                isGlobalEnabled = prefs.azanEnabled,
                onToggle = { viewModel.setPrayerTime("Dhuhr", it, prefs.azanDhuhrHour, prefs.azanDhuhrMinute) },
                onTimeClick = { activeTimePickerPrayer = "Dhuhr" }
            )

            // Asr
            PrayerTimeRow(
                name = "Asr",
                urduName = "نمازِ عصر",
                hour = prefs.azanAsrHour,
                minute = prefs.azanAsrMinute,
                isEnabled = prefs.azanAsrEnabled,
                isGlobalEnabled = prefs.azanEnabled,
                onToggle = { viewModel.setPrayerTime("Asr", it, prefs.azanAsrHour, prefs.azanAsrMinute) },
                onTimeClick = { activeTimePickerPrayer = "Asr" }
            )

            // Maghrib
            PrayerTimeRow(
                name = "Maghrib",
                urduName = "نمازِ مغرب",
                hour = prefs.azanMaghribHour,
                minute = prefs.azanMaghribMinute,
                isEnabled = prefs.azanMaghribEnabled,
                isGlobalEnabled = prefs.azanEnabled,
                onToggle = { viewModel.setPrayerTime("Maghrib", it, prefs.azanMaghribHour, prefs.azanMaghribMinute) },
                onTimeClick = { activeTimePickerPrayer = "Maghrib" }
            )

            // Isha
            PrayerTimeRow(
                name = "Isha",
                urduName = "نمازِ عشاء",
                hour = prefs.azanIshaHour,
                minute = prefs.azanIshaMinute,
                isEnabled = prefs.azanIshaEnabled,
                isGlobalEnabled = prefs.azanEnabled,
                onToggle = { viewModel.setPrayerTime("Isha", it, prefs.azanIshaHour, prefs.azanIshaMinute) },
                onTimeClick = { activeTimePickerPrayer = "Isha" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Azan Tune Selector
            Text(
                text = "Azan Calling Sound • اذان کی آواز",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    IslamicTunePlayer.AVAILABLE_TUNES.forEach { tune ->
                        val isSelected = prefs.azanTune == tune.id
                        val isPlaying = playingTuneId == tune.id

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setAzanTune(tune.id) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.setAzanTune(tune.id) }
                                    )
                                    Column(modifier = Modifier.padding(start = 8.dp)) {
                                        Text(
                                            text = tune.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = tune.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (tune.rawResId != null) {
                                    FilledTonalIconButton(
                                        onClick = { viewModel.playTunePreview(tune.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                            contentDescription = if (isPlaying) "Stop" else "Play",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Test Button
            OutlinedButton(
                onClick = {
                    IslamicTunePlayer.playAlarm(context, prefs.azanTune)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Campaign, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Test Azan Sound Now (آواز چیک کریں)")
            }
        }
    }
}

@Composable
private fun PrayerTimeRow(
    name: String,
    urduName: String,
    hour: Int,
    minute: Int,
    isEnabled: Boolean,
    isGlobalEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onTimeClick: () -> Unit
) {
    val hour12 = if (hour % 12 == 0) 12 else hour % 12
    val amPm = if (hour >= 12) "PM" else "AM"
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled && isGlobalEnabled)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isEnabled && isGlobalEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                )
                Text(
                    text = urduName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isEnabled && isGlobalEnabled)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable(enabled = isGlobalEnabled) { onTimeClick() }
                        .padding(end = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isEnabled && isGlobalEnabled)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isEnabled && isGlobalEnabled)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Switch(
                    checked = isEnabled,
                    enabled = isGlobalEnabled,
                    onCheckedChange = onToggle
                )
            }
        }
    }
}
