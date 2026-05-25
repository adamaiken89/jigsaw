package com.jigsaw.app.service

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.jigsaw.app.data.PreferencesManager

enum class MusicTrack(val resName: String) {
    TRACK_1("classical_piano"),
    TRACK_2("classical_british"),
    TRACK_3("classical_piano_alt");

    companion object {
        fun random() = entries.random()
    }
}

class MusicController(private val context: Context) {
    private var player: MediaPlayer? = null
    private var currentTrack: MusicTrack = MusicTrack.random()
    var isPlaying: Boolean = false
        private set

    fun play(prefs: PreferencesManager) {
        if (player == null) {
            val resId = context.resources.getIdentifier(
                currentTrack.resName, "raw", context.packageName
            )
            if (resId != 0) {
                player = MediaPlayer.create(context, resId).apply {
                    isLooping = true
                    setVolume(
                        if (prefs.isMusicMuted()) 0f else prefs.getMusicVolume(),
                        if (prefs.isMusicMuted()) 0f else prefs.getMusicVolume()
                    )
                    start()
                    isPlaying = true
                }
            }
        } else if (!player!!.isPlaying) {
            player!!.start()
            isPlaying = true
        }
    }

    fun pause() {
        player?.pause()
        isPlaying = false
    }

    fun stop() {
        player?.stop()
        player?.release()
        player = null
        isPlaying = false
    }

    fun setVolume(volume: Float, muted: Boolean) {
        val vol = if (muted) 0f else volume.coerceIn(0f, 1f)
        player?.setVolume(vol, vol)
    }

    fun nextTrack(prefs: PreferencesManager) {
        stop()
        currentTrack = MusicTrack.random()
        play(prefs)
    }
}
