package de.nogaemer.unspeakable.db

import de.nogaemer.unspeakable.core.util.settings.AppSettingsController

object Graph {
    lateinit var database: UnspeakableDatabase

    lateinit var settings: AppSettingsController

    @Suppress("unused")
    val dao: UnspeakableCardsDao
        get() = database.unspeakableCardsDao()

    @Suppress("unused")
    val categoriesDao: UnspeakableCategoriesDao
        get() = database.unspeakableCategoriesDao()
}
