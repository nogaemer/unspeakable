package de.nogaemer.unspeakable.db

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
private fun getIosDbPath(fileName: String): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null
    )
    return requireNotNull(documentDirectory?.URLByAppendingPathComponent(fileName)?.path)
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<UnspeakableDatabase> {
    val dbPath = getIosDbPath("taboo.db")
    return Room.databaseBuilder<UnspeakableDatabase>(
        name = dbPath
    )
}

actual fun isDatabaseFileCopied(fileName: String): Boolean {
    return NSFileManager.defaultManager.fileExistsAtPath(getIosDbPath(fileName))
}

@OptIn(ExperimentalForeignApi::class)
actual fun writeDatabaseFile(fileName: String, bytes: ByteArray) {
    val data = NSMutableData.create(length = bytes.size.toULong()) ?: return
    bytes.usePinned { pinned ->
        memcpy(data.mutableBytes, pinned.addressOf(0), bytes.size.toULong())
    }
    data.writeToFile(getIosDbPath(fileName), atomically = true)
}
