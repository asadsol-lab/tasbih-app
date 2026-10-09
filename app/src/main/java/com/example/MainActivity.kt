package com.example

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.navigation.AppNavigation
import com.example.ui.home.HomeViewModel
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.TasbihTheme

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as TasbihApp
        val preferencesRepo = app.userPreferencesRepository

        setContent {
            val userPrefs by preferencesRepo.userPreferencesFlow.collectAsStateWithLifecycle(
                initialValue = com.example.data.repository.UserPreferences()
            )

            TasbihTheme(themeMode = userPrefs.themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(homeViewModel = homeViewModel)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    if (homeViewModel.onVolumeKeyPressed(isVolumeUp = true)) {
                        return true
                    }
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    if (homeViewModel.onVolumeKeyPressed(isVolumeUp = false)) {
                        return true
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
