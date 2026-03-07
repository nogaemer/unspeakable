
package de.nogaemer.unspeakable.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

private fun getDesktopDbFile(fileName: String): File {
    val appFolder = File(System.getProperty("user.home"), ".UnspeakableApp")
    if (!appFolder.exists()) appFolder.mkdirs()
    return File(appFolder, fileName)
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<UnspeakableDatabase> {
    val dbFile = getDesktopDbFile("taboo.db")
    return Room.databaseBuilder<UnspeakableDatabase>(name = dbFile.absolutePath)
}

actual fun isDatabaseFileCopied(fileName: String): Boolean {
    return getDesktopDbFile(fileName).exists()
}

actual fun writeDatabaseFile(fileName: String, bytes: ByteArray) {
    getDesktopDbFile(fileName).writeBytes(bytes)
}

