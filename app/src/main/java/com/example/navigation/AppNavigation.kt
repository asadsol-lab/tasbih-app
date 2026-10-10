package com.example.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.ai.AiFutureScreen
import com.example.ui.azan.AzanSettingsScreen
import com.example.ui.history.HistoryScreen
import com.example.ui.history.HistoryViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.home.TargetSetupScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.reminders.RemindersScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.settings.SettingsViewModel
import com.example.ui.statistics.StatisticsScreen
import com.example.ui.statistics.StatisticsViewModel
import com.example.ui.volume.VolumeSettingsScreen
import com.example.ui.zikr.AddEditZikrScreen
import com.example.ui.zikr.ZikrListScreen
import com.example.ui.zikr.ZikrViewModel

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun AppNavigation(
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val zikrViewModel: ZikrViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()
    val statisticsViewModel: StatisticsViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    val prefs by settingsViewModel.preferences.collectAsStateWithLifecycle()

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home.route, "Counter", Icons.Default.RadioButtonChecked, "tab_counter"),
        BottomNavItem(Screen.ZikrList.route, "Dhikr", Icons.Default.MenuBook, "tab_dhikr"),
        BottomNavItem(Screen.History.route, "History", Icons.Default.History, "tab_history"),
        BottomNavItem(Screen.Statistics.route, "Stats", Icons.Default.BarChart, "tab_stats"),
        BottomNavItem(Screen.Settings.route, "Settings", Icons.Default.Settings, "tab_settings")
    )

    val isTopLevelDestination = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (isTopLevelDestination) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            modifier = Modifier.testTag(item.tag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (prefs.hasCompletedOnboarding) Screen.Home.route else Screen.Onboarding.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        settingsViewModel.setHasCompletedOnboarding(true)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToZikrList = { navController.navigate(Screen.ZikrList.route) },
                    onNavigateToTargetSetup = { navController.navigate(Screen.TargetSetup.route) },
                    onNavigateToVolumeSettings = { navController.navigate(Screen.VolumeSettings.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.ZikrList.route) {
                ZikrListScreen(
                    viewModel = zikrViewModel,
                    onZikrSelected = { zikr ->
                        homeViewModel.selectZikr(zikr)
                    },
                    onNavigateToAddEdit = { id ->
                        navController.navigate(Screen.AddEditZikr.createRoute(id))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.AddEditZikr.route,
                arguments = listOf(
                    navArgument("zikrId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val zikrIdString = backStackEntry.arguments?.getString("zikrId")
                val zikrId = zikrIdString?.toLongOrNull()
                AddEditZikrScreen(
                    zikrId = zikrId,
                    viewModel = zikrViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.TargetSetup.route) {
                TargetSetupScreen(
                    viewModel = homeViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = historyViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen(
                    viewModel = statisticsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateToVolumeSettings = { navController.navigate(Screen.VolumeSettings.route) },
                    onNavigateToReminders = { navController.navigate(Screen.Reminders.route) },
                    onNavigateToAzanSettings = { navController.navigate(Screen.AzanSettings.route) },
                    onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) },
                    onNavigateToAiFuture = { navController.navigate(Screen.AiFuture.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.VolumeSettings.route) {
                VolumeSettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Reminders.route) {
                RemindersScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AzanSettings.route) {
                AzanSettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AiFuture.route) {
                AiFutureScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
