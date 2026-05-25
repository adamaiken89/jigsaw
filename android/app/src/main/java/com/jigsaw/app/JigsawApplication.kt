package com.jigsaw.app

import android.app.Application
import com.jigsaw.app.data.PreferencesManager
import com.jigsaw.app.service.MusicController

class JigsawApplication : Application() {
    lateinit var prefs: PreferencesManager
        private set
    lateinit var musicController: MusicController
        private set

    override fun onCreate() {
        super.onCreate()
        prefs = PreferencesManager(this)
        musicController = MusicController(this)
    }
}
