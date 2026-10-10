package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.util.DeviceBrand

@Composable
fun DeviceSettingsVisualMockup(
    brand: DeviceBrand,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E2621) // realistic dark phone settings background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Simulated Phone Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4EDE9A))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Phone Settings Preview (${brand.displayName})",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD6E5DC)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EmeraldPrimary.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = "How it looks on your phone",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color(0xFF4EDE9A),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (brand) {
                DeviceBrand.SAMSUNG -> {
                    MockupRowRadio(
                        title = "Unrestricted",
                        subtitle = "Allow this app to run in the background without restrictions",
                        isSelected = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    MockupRowRadio(
                        title = "Optimized",
                        subtitle = "Optimize based on usage (DO NOT SELECT)",
                        isSelected = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MockupRowSwitch(
                        title = "Lock screen notifications",
                        subtitle = "Show content on lock screen",
                        isChecked = true
                    )
                }
                DeviceBrand.XIAOMI -> {
                    MockupRowSwitch(
                        title = "Autostart (خودکار آغاز)",
                        subtitle = "Allow app to launch in background for reminders",
                        isChecked = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    MockupRowRadio(
                        title = "No restrictions (کوئی پابندی نہیں)",
                        subtitle = "Battery saver will not restrict app activity",
                        isSelected = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    MockupRowRadio(
                        title = "Battery saver (Recommended by MIUI - DO NOT SELECT)",
                        subtitle = "Identify apps and keep important processes",
                        isSelected = false
                    )
                }
                DeviceBrand.OPPO_REALME_ONEPLUS -> {
                    MockupRowSwitch(
                        title = "Allow background activity",
                        subtitle = "App can run when closed or in pocket",
                        isChecked = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    MockupRowSwitch(
                        title = "Allow auto-launch",
                        subtitle = "Launch automatically on scheduled Azan time",
                        isChecked = true
                    )
                }
                DeviceBrand.VIVO -> {
                    MockupRowSwitch(
                        title = "High background power consumption",
                        subtitle = "Allow Tasbih Counter continuous operation",
                        isChecked = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    MockupRowSwitch(
                        title = "Autostart permission",
                        subtitle = "Permit wakeup for daily prayer alarms",
                        isChecked = true
                    )
                }
                else -> {
                    MockupRowRadio(
                        title = "Unrestricted",
                        subtitle = "Allow app to run in the background without battery limits",
                        isSelected = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    MockupRowRadio(
                        title = "Optimized",
                        subtitle = "Optimize battery usage (May kill background alarms)",
                        isSelected = false
                    )
                }
            }
        }
    }
}

@Composable
private fun MockupRowRadio(
    title: String,
    subtitle: String,
    isSelected: Boolean
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0xFF28382E) else Color(0xFF232B25),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color(0xFF4EDE9A) else Color(0xFFADB5AF)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color(0xFF8B968E)
                )
            }

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (isSelected) Color(0xFF4EDE9A) else Color(0xFF6B756E), CircleShape)
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4EDE9A))
                    )
                }
            }
        }
    }
}

@Composable
private fun MockupRowSwitch(
    title: String,
    subtitle: String,
    isChecked: Boolean
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF28382E),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4EDE9A)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color(0xFF8B968E)
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF4EDE9A),
                modifier = Modifier.size(width = 38.dp, height = 22.dp)
            ) {
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }
        }
    }
}
