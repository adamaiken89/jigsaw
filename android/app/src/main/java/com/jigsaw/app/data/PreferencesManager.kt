package com.jigsaw.app.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("jigsaw_prefs", Context.MODE_PRIVATE)

    fun isPuzzleCompleted(index: Int): Boolean =
        prefs.getBoolean("puzzle_$index", false)

    fun setPuzzleCompleted(index: Int) {
        prefs.edit().putBoolean("puzzle_$index", true).apply()
    }

    fun getLastUnlockedPuzzle(): Int =
        prefs.getInt("last_unlocked", 0)

    fun unlockNextPuzzle(index: Int) {
        val current = getLastUnlockedPuzzle()
        if (index >= current) {
            prefs.edit().putInt("last_unlocked", index + 1).apply()
        }
    }

    fun getMusicVolume(): Float =
        prefs.getFloat("music_volume", 0.5f)

    fun setMusicVolume(volume: Float) {
        prefs.edit().putFloat("music_volume", volume).apply()
    }

    fun isMusicMuted(): Boolean =
        prefs.getBoolean("music_muted", false)

    fun setMusicMuted(muted: Boolean) {
        prefs.edit().putBoolean("music_muted", muted).apply()
    }

    fun resetAll() {
        prefs.edit().clear().apply()
    }
}
