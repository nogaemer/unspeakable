package de.nogaemer.unspeakable.db

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase

object AndroidAppContext {
    lateinit var application: Application
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<UnspeakableDatabase> {
    val context = AndroidAppContext.application
    val dbFile = context.getDatabasePath("taboo.db")
    return Room.databaseBuilder<UnspeakableDatabase>(context, dbFile.absolutePath)
}

actual fun isDatabaseFileCopied(fileName: String): Boolean {
    return AndroidAppContext.application.getDatabasePath(fileName).exists()
}

actual fun writeDatabaseFile(fileName: String, bytes: ByteArray) {
    val dbFile = AndroidAppContext.application.getDatabasePath(fileName)
    dbFile.parentFile?.mkdirs() // Ensure databases folder exists
    dbFile.writeBytes(bytes)
}
