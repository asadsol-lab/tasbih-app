package com.example.util

import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.example.R

data class IslamicTuneItem(
    val id: String,
    val title: String,
    val description: String,
    val rawResId: Int?
)

object IslamicTunePlayer {

    private var activePlayer: MediaPlayer? = null
    var currentlyPlayingId: String? = null
        private set

    val AVAILABLE_TUNES = listOf(
        IslamicTuneItem(
            id = "tune_subhanallah",
            title = "SubhanAllah Harmony",
            description = "سُبْحَانَ اللَّهِ • Soothing Spiritual Chime",
            rawResId = R.raw.tune_subhanallah
        ),
        IslamicTuneItem(
            id = "tune_takbeer",
            title = "Makkah Takbeer Melody",
            description = "اللَّهُ أَكْبَرُ • Melodic Prayer Chime",
            rawResId = R.raw.tune_takbeer
        ),
        IslamicTuneItem(
            id = "tune_fajr_dawn",
            title = "Spiritual Dawn Tone",
            description = "الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ • Calming Morning Bells",
            rawResId = R.raw.tune_fajr_dawn
        ),
        IslamicTuneItem(
            id = "tune_azan_alert",
            title = "Azan Prayer Call Chime",
            description = "أَذَان مُبَارَك • Dignified Azan Alert",
            rawResId = R.raw.tune_azan_alert
        ),
        IslamicTuneItem(
            id = "tune_system",
            title = "System Default Tone",
            description = "Device default notification chime",
            rawResId = null
        )
    )

    fun getTuneResId(tuneId: String): Int {
        return AVAILABLE_TUNES.firstOrNull { it.id == tuneId }?.rawResId ?: R.raw.tune_subhanallah
    }

    fun getTuneTitle(tuneId: String): String {
        return AVAILABLE_TUNES.firstOrNull { it.id == tuneId }?.title ?: "Islamic Chime"
    }

    fun getTuneUri(context: Context, tuneId: String): Uri? {
        val resId = getTuneResId(tuneId)
        return Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/$resId")
    }

    fun playPreview(context: Context, tuneId: String, onFinished: () -> Unit = {}) {
        stop()
        val resId = AVAILABLE_TUNES.firstOrNull { it.id == tuneId }?.rawResId ?: return
        try {
            val player = MediaPlayer.create(context.applicationContext, resId).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setOnCompletionListener {
                    stop()
                    onFinished()
                }
            }
            activePlayer = player
            currentlyPlayingId = tuneId
            player.start()
        } catch (_: Exception) {
            stop()
            onFinished()
        }
    }

    fun playAlarm(context: Context, tuneId: String) {
        stop()
        val resId = AVAILABLE_TUNES.firstOrNull { it.id == tuneId }?.rawResId ?: R.raw.tune_subhanallah
        try {
            val player = MediaPlayer.create(context.applicationContext, resId).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setOnCompletionListener {
                    stop()
                }
            }
            activePlayer = player
            currentlyPlayingId = tuneId
            player.start()
        } catch (_: Exception) {
            stop()
        }
    }

    fun stop() {
        try {
            activePlayer?.stop()
            activePlayer?.release()
        } catch (_: Exception) {
        } finally {
            activePlayer = null
            currentlyPlayingId = null
        }
    }
}
