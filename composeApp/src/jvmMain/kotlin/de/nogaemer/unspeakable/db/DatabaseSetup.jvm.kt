package de.nogaemer.unspeakable.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

private fun getDesktopDbFile(fileName: String): File {
    val appFolder = File(System.getProperty("user.home"), ".UnspeakableApp")
    if (!appFolder.exists()) appFolder.mkdirs()
    return File(appFolder, fileName)
}

/**
 * Builds Room using a per-user desktop storage location.
 * JVM: resolves file path under `~/.UnspeakableApp`.
 */
actual fun getDatabaseBuilder(): RoomDatabase.Builder<UnspeakableDatabase> {
    val dbFile = getDesktopDbFile("taboo.db")
    return Room.databaseBuilder<UnspeakableDatabase>(name = dbFile.absolutePath)
}

/**
 * Checks whether the seeded database file exists in desktop app storage.
 */
actual fun isDatabaseFileCopied(fileName: String): Boolean {
    return getDesktopDbFile(fileName).exists()
}

/**
 * Writes seeded database bytes into desktop app storage.
 */
actual fun writeDatabaseFile(fileName: String, bytes: ByteArray) {
    getDesktopDbFile(fileName).writeBytes(bytes)
}

