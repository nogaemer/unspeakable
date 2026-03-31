package de.nogaemer.unspeakable.db

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Holds process-wide Android context needed by shared database setup calls.
 */
object AndroidAppContext {
    lateinit var application: Application
}

/**
 * Builds Room using Android's managed databases directory.
 * Android: uses `Context.getDatabasePath` and Room's context-aware builder.
 */
actual fun getDatabaseBuilder(): RoomDatabase.Builder<UnspeakableDatabase> {
    val context = AndroidAppContext.application
    val dbFile = context.getDatabasePath("taboo.db")
    return Room.databaseBuilder<UnspeakableDatabase>(context, dbFile.absolutePath)
}

/**
 * Checks whether the seeded database file exists in Android app storage.
 */
actual fun isDatabaseFileCopied(fileName: String): Boolean {
    return AndroidAppContext.application.getDatabasePath(fileName).exists()
}

/**
 * Writes seeded database bytes into Android app storage.
 */
actual fun writeDatabaseFile(fileName: String, bytes: ByteArray) {
    val dbFile = AndroidAppContext.application.getDatabasePath(fileName)
    dbFile.parentFile?.mkdirs() // Ensure databases folder exists
    dbFile.writeBytes(bytes)
}
