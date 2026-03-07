package de.nogaemer.unspeakable.db

object Graph {
    lateinit var database: UnspeakableDatabase

    val dao: UnspeakableCardsDao
        get() = database.unspeakableCardsDao()
}
