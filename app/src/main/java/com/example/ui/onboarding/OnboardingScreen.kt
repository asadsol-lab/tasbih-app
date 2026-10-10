package com.example.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DeviceSettingsVisualMockup
import com.example.ui.theme.EmeraldPrimary
import com.example.util.DeviceUtils

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(0) }
    val guide = remember { DeviceUtils.getDeviceGuide() }

    var isBatteryWhitelisted by remember {
        mutableStateOf(DeviceUtils.isBatteryOptimizationIgnored(context))
    }

    Surface(
        modifier = modifier.fillMaxSize().testTag("onboarding_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Skip and Step Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step Dots
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (currentStep == index) 22.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (currentStep == index) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                if (currentStep < 2) {
                    TextButton(onClick = onComplete) {
                        Text("Skip Setup")
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Content
            when (currentStep) {
                0 -> {
                    // Step 1: Features Overview
                    StepFeaturesTour()
                }
                1 -> {
                    // Step 2: Device Whitelisting & Restrictions
                    StepDeviceOptimization(
                        guide = guide,
                        isBatteryWhitelisted = isBatteryWhitelisted,
                        onWhitelistClick = {
                            DeviceUtils.requestIgnoreBatteryOptimization(context)
                            isBatteryWhitelisted = DeviceUtils.isBatteryOptimizationIgnored(context)
                        },
                        onAutostartClick = {
                            DeviceUtils.openAutostartSettings(context)
                        }
                    )
                }
                2 -> {
                    // Step 3: Ready to start
                    StepReadyToBegin()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Previous")
                    }
                }

                Button(
                    onClick = {
                        if (currentStep < 2) {
                            currentStep++
                        } else {
                            onComplete()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(50.dp).testTag("onboarding_next_button")
                ) {
                    Text(if (currentStep == 2) "Start Tasbih (شروع کریں)" else "Next Step")
                }
            }
        }
    }
}

@Composable
private fun StepFeaturesTour() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TouchApp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(18.dp).fillMaxSize()
            )
        }

        Text(
            text = "Welcome to Tasbih Counter",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "A distraction-free Islamic tasbih with exact Azan alarms and physical counting.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        FeatureHighlightCard(
            icon = Icons.Default.DarkMode,
            title = "Pocket Stealth Touch Mode",
            description = "Turns display pitch-black (AMOLED 0% battery). Tap anywhere in your hand or pocket to count with tactile vibration!"
        )

        FeatureHighlightCard(
            icon = Icons.Default.Campaign,
            title = "Exact Azan Prayer Calls",
            description = "Authentic Makkah, Madinah & Fajr Azan alerts that wake up your device on time even when the app is completely closed."
        )

        FeatureHighlightCard(
            icon = Icons.Default.MenuBook,
            title = "Authentic Dhikr & Urdu Translation",
            description = "Curated sunnah dhikr, custom goals (33, 99, 1000), session history, and weekly progress charts."
        )
    }
}

@Composable
private fun StepDeviceOptimization(
    guide: com.example.util.DeviceGuide,
    isBatteryWhitelisted: Boolean,
    onWhitelistClick: () -> Unit,
    onAutostartClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = CircleShape,
            color = if (isBatteryWhitelisted) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = if (isBatteryWhitelisted) Icons.Default.CheckCircle else Icons.Default.BatteryAlert,
                contentDescription = null,
                tint = if (isBatteryWhitelisted) EmeraldPrimary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp).fillMaxSize()
            )
        }

        Text(
            text = "Device Setup: ${guide.brand.displayName}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Your phone (${guide.deviceModelName}) may aggressively kill background apps unless exempted. Follow this 1-step setup so Azan & Reminders ring reliably when closed:",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Visual Mockup of exact settings
        DeviceSettingsVisualMockup(brand = guide.brand)

        // Whitelist Action Buttons
        Button(
            onClick = onWhitelistClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isBatteryWhitelisted) EmeraldPrimary else MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(
                imageVector = if (isBatteryWhitelisted) Icons.Default.Check else Icons.Default.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                if (isBatteryWhitelisted)
                    "Battery Whitelisted ✓ (پابندی ہٹ چکی ہے)"
                else
                    "Remove Battery Limits Now (ایک کلک میں ہٹائیں)"
            )
        }

        OutlinedButton(
            onClick = onAutostartClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) {
            Text("Open Autostart Settings (خودکار اجازت)")
        }
    }
}

@Composable
private fun StepReadyToBegin() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = CircleShape,
            color = EmeraldPrimary.copy(alpha = 0.2f),
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.padding(20.dp).fillMaxSize()
            )
        }

        Text(
            text = "You are Ready! • تسبیح تیار ہے",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = EmeraldPrimary
        )

        Text(
            text = "All setup is complete! You can count using the main screen, physical volume buttons, or Pocket Stealth mode. This setup will not appear again.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Quick Tips for Daily Dhikr:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• Tap the moon icon on the home counter for Pocket Stealth Mode.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "• Customize 5 daily Azan prayer times in Settings -> Azan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "• Switch between English, Urdu, and Roman Urdu anytime in Settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun FeatureHighlightCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
