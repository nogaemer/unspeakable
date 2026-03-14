package de.nogaemer.unspeakable.db

import de.nogaemer.unspeakable.core.util.settings.AppSettingsController

object Graph {
    lateinit var database: UnspeakableDatabase

    lateinit var settings: AppSettingsController

    val dao: UnspeakableCardsDao
        get() = database.unspeakableCardsDao()
}
