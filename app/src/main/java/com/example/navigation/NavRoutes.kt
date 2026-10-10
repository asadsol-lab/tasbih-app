package com.example.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ZikrList : Screen("zikr_list")
    data object AddEditZikr : Screen("add_edit_zikr?zikrId={zikrId}") {
        fun createRoute(zikrId: Long? = null): String {
            return if (zikrId != null) "add_edit_zikr?zikrId=$zikrId" else "add_edit_zikr"
        }
    }
    data object TargetSetup : Screen("target_setup")
    data object History : Screen("history")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")
    data object VolumeSettings : Screen("volume_settings")
    data object Reminders : Screen("reminders")
    data object AzanSettings : Screen("azan_settings")
    data object AiFuture : Screen("ai_future")
}
