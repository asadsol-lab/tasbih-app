package com.example.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

enum class DeviceBrand(val displayName: String) {
    SAMSUNG("Samsung (One UI)"),
    XIAOMI("Xiaomi / Redmi / POCO (MIUI / HyperOS)"),
    OPPO_REALME_ONEPLUS("OPPO / Realme / OnePlus (ColorOS / OxygenOS)"),
    VIVO("Vivo / iQOO (Funtouch OS)"),
    HUAWEI("Huawei / Honor (EMUI / MagicOS)"),
    PIXEL_STOCK("Google Pixel / Stock Android"),
    OTHER("Android Device")
}

data class DeviceGuide(
    val brand: DeviceBrand,
    val deviceModelName: String,
    val androidVersion: String,
    val stepsUrdu: List<String>,
    val stepsEnglish: List<String>,
    val screenOffVolumeFactUrdu: String,
    val screenOffVolumeFactEnglish: String
)

object DeviceUtils {

    fun detectBrand(): DeviceBrand {
        val man = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()
        return when {
            man.contains("samsung") || brand.contains("samsung") -> DeviceBrand.SAMSUNG
            man.contains("xiaomi") || man.contains("redmi") || man.contains("poco") ||
            brand.contains("xiaomi") || brand.contains("redmi") || brand.contains("poco") -> DeviceBrand.XIAOMI
            man.contains("oppo") || man.contains("realme") || man.contains("oneplus") ||
            brand.contains("oppo") || brand.contains("realme") || brand.contains("oneplus") -> DeviceBrand.OPPO_REALME_ONEPLUS
            man.contains("vivo") || man.contains("iqoo") || brand.contains("vivo") || brand.contains("iqoo") -> DeviceBrand.VIVO
            man.contains("huawei") || man.contains("honor") || brand.contains("huawei") || brand.contains("honor") -> DeviceBrand.HUAWEI
            man.contains("google") || brand.contains("google") || man.contains("motorola") -> DeviceBrand.PIXEL_STOCK
            else -> DeviceBrand.OTHER
        }
    }

    fun isBatteryOptimizationIgnored(context: Context): Boolean {
        return try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false
        } catch (_: Exception) {
            false
        }
    }

    fun getDeviceGuide(): DeviceGuide {
        val brand = detectBrand()
        val model = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"
        val androidVer = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

        val (stepsUrdu, stepsEnglish) = when (brand) {
            DeviceBrand.SAMSUNG -> Pair(
                listOf(
                    "موبائل کی Settings میں جائیں -> Apps -> Tasbih Counter منتخب کریں۔",
                    "Battery پر ٹیپ کریں اور اسے 'Unrestricted' (غیر محدود) پر سیٹ کریں۔",
                    "Lock Screen سیٹنگز میں جائیں اور 'Show Notifications' (نوٹیفکیشنز آن) رکھیں۔",
                    "Sounds & Vibration میں 'Use Volume keys for media' آن رکھیں۔"
                ),
                listOf(
                    "Go to Phone Settings -> Apps -> Tasbih Counter.",
                    "Tap Battery and change it from 'Optimized' to 'Unrestricted'.",
                    "In Lock Screen Settings, enable 'Show Notifications'.",
                    "In Sounds & Vibration, ensure 'Use Volume keys for media' is active."
                )
            )
            DeviceBrand.XIAOMI -> Pair(
                listOf(
                    "موبائل Settings -> Apps -> Manage Apps -> Tasbih Counter کھولیں۔",
                    "'Autostart' (خودکار آغاز) کو لازمی آن (Enable) کریں۔",
                    "'Battery Saver' میں جا کر 'No restrictions' (کوئی پابندی نہیں) منتخب کریں۔",
                    "Lock Screen سیٹنگ میں 'Wake Lock screen for notifications' آن کریں تاکہ لاک اسکرین پر کاؤنٹ کر سکیں۔"
                ),
                listOf(
                    "Open Phone Settings -> Apps -> Manage Apps -> Tasbih Counter.",
                    "Enable 'Autostart' permission.",
                    "Tap 'Battery Saver' and select 'No restrictions'.",
                    "In Lock Screen settings, turn on 'Wake Lock screen for notifications'."
                )
            )
            DeviceBrand.OPPO_REALME_ONEPLUS -> Pair(
                listOf(
                    "Settings -> Apps -> App management -> Tasbih Counter پر جائیں۔",
                    "Battery usage میں جا کر 'Allow background activity' اور 'Allow auto-launch' دونوں آن کریں۔",
                    "Lock screen notifications کو 'Show banner and content' پر سیٹ کریں۔"
                ),
                listOf(
                    "Settings -> Apps -> App management -> Tasbih Counter.",
                    "In Battery usage, enable 'Allow background activity' and 'Allow auto-launch'.",
                    "Set Lock screen notifications to 'Show banner and details'."
                )
            )
            DeviceBrand.VIVO -> Pair(
                listOf(
                    "Settings -> Battery -> 'High background power consumption' میں Tasbih Counter کو آن کریں۔",
                    "Settings -> Applications -> 'Autostart' میں Tasbih Counter کی اجازت دیں۔",
                    "لاک اسکرین پر کاؤنٹر نوٹیفکیشن کو فعال رکھیں۔"
                ),
                listOf(
                    "Settings -> Battery -> 'High background power consumption' -> Enable Tasbih Counter.",
                    "Settings -> Applications -> 'Autostart' -> Allow Tasbih Counter.",
                    "Ensure lock screen notifications are allowed."
                )
            )
            DeviceBrand.HUAWEI -> Pair(
                listOf(
                    "Settings -> Battery -> App Launch -> Tasbih Counter پر جائیں۔",
                    "'Manage automatically' کو بند کر کے 'Manage manually' کریں اور Auto-launch، Secondary launch اور Run in background تینوں آن کریں۔"
                ),
                listOf(
                    "Settings -> Battery -> App Launch -> Tasbih Counter.",
                    "Disable 'Manage automatically' and manually enable: Auto-launch, Secondary launch, and Run in background."
                )
            )
            else -> Pair(
                listOf(
                    "Settings -> Apps -> Tasbih Counter -> Battery میں جائیں۔",
                    "'Unrestricted' یا 'Don't optimize' کا انتخاب کریں۔",
                    "لاک اسکرین پر نوٹیفکیشن ایکشنز کو آن رکھیں۔"
                ),
                listOf(
                    "Settings -> Apps -> Tasbih Counter -> App battery usage.",
                    "Select 'Unrestricted' (Don't optimize).",
                    "Allow Lock screen notifications with actions."
                )
            )
        }

        val factUrdu = "حقیقت: تمام اینڈرائڈ برانڈز سیکیورٹی اور بیٹری کے لیے اسکرین بند ہونے پر ہارڈویئر بٹن بلاک کر دیتے ہیں۔ تسبیح کاؤنٹر میں بیٹری کی پابندی ہٹانے کے بعد لاک اسکرین کے نوٹیفکیشن بٹن (+1 Count) اور اسکرین آن والیوم بٹن سے بلا رکاوٹ کاؤنٹ کریں!"
        val factEnglish = "Technical Note: All Android OEMs restrict raw hardware volume keys when screen is deeply sleeping to save battery and protect privacy. Whitelisting the app ensures the background counter stays awake and lock screen [+1 Count] actions remain always responsive."

        return DeviceGuide(
            brand = brand,
            deviceModelName = model,
            androidVersion = androidVer,
            stepsUrdu = stepsUrdu,
            stepsEnglish = stepsEnglish,
            screenOffVolumeFactUrdu = factUrdu,
            screenOffVolumeFactEnglish = factEnglish
        )
    }

    fun openAppDetailsSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            openGeneralSettings(context)
        }
    }

    fun requestIgnoreBatteryOptimization(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } else {
                openAppDetailsSettings(context)
            }
        } catch (_: Exception) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } else {
                    openAppDetailsSettings(context)
                }
            } catch (_: Exception) {
                openAppDetailsSettings(context)
            }
        }
    }

    fun openAutostartSettings(context: Context) {
        val intents = listOf(
            // Xiaomi
            Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            // Oppo
            Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            // Vivo
            Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            // Huawei
            Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            // Samsung
            Intent().setComponent(ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity"))
        )

        for (intent in intents) {
            try {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return
            } catch (_: Exception) {
            }
        }
        // Fallback to app details
        openAppDetailsSettings(context)
    }

    private fun openGeneralSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }
}
