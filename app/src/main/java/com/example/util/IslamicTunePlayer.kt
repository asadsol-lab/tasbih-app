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
            id = "azan_makkah",
            title = "Azan Makkah Al-Mukarramah",
            description = "أَذَان مَكَّة الْمُكَرَّمَة • Authentic Maqam Hijaz Adhan",
            rawResId = R.raw.azan_makkah
        ),
        IslamicTuneItem(
            id = "azan_madinah",
            title = "Azan Madinah Al-Munawwarah",
            description = "أَذَان الْمَدِينَة الْمُنَوَّرَة • Melodious Maqam Bayati Adhan",
            rawResId = R.raw.azan_madinah
        ),
        IslamicTuneItem(
            id = "azan_fajr",
            title = "Azan Fajr (Al-Salatu Khayrun)",
            description = "أَذَان الفَجْر • With الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ",
            rawResId = R.raw.azan_fajr
        ),
        IslamicTuneItem(
            id = "azan_takbeer_short",
            title = "Allahu Akbar Takbeerat",
            description = "تَكْبِيرَات العِيد وَالأَذَان • Powerful Adhan Call Alert",
            rawResId = R.raw.azan_takbeer_short
        ),
        IslamicTuneItem(
            id = "tune_subhanallah",
            title = "SubhanAllah Harmony",
            description = "سُبْحَانَ اللَّهِ • Peaceful Spiritual Chime",
            rawResId = R.raw.tune_subhanallah
        ),
        IslamicTuneItem(
            id = "tune_takbeer",
            title = "Takbeer Melody Chime",
            description = "اللَّهُ أَكْبَرُ • Melodic Prayer Chime",
            rawResId = R.raw.tune_takbeer
        ),
        IslamicTuneItem(
            id = "tune_system",
            title = "System Default Tone",
            description = "Device default notification sound",
            rawResId = null
        )
    )

    fun getTuneResId(tuneId: String): Int {
        return AVAILABLE_TUNES.firstOrNull { it.id == tuneId }?.rawResId ?: R.raw.azan_makkah
    }

    fun getTuneTitle(tuneId: String): String {
        return AVAILABLE_TUNES.firstOrNull { it.id == tuneId }?.title ?: "Azan Makkah"
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
        val resId = AVAILABLE_TUNES.firstOrNull { it.id == tuneId }?.rawResId ?: R.raw.azan_makkah
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
