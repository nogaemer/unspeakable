package de.nogaemer.unspeakable

import android.app.Application
import qrgenerator.AppContext

class UnspeakableApp : Application() {
    companion object {
        lateinit var INSTANCE: UnspeakableApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        AppContext.apply { set(applicationContext) }
    }
}

