package de.nogaemer.unspeakable.db

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSMutableData
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile
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

/**
 * Builds Room using a writable file inside the iOS app sandbox.
 * iOS: resolves path via Foundation `NSFileManager` APIs.
 */
actual fun getDatabaseBuilder(): RoomDatabase.Builder<UnspeakableDatabase> {
    val dbPath = getIosDbPath("taboo.db")
    return Room.databaseBuilder<UnspeakableDatabase>(
        name = dbPath
    )
}

/**
 * Checks whether the seeded database file exists in iOS sandbox storage.
 */
actual fun isDatabaseFileCopied(fileName: String): Boolean {
    return NSFileManager.defaultManager.fileExistsAtPath(getIosDbPath(fileName))
}

/**
 * Writes seeded database bytes into iOS sandbox storage.
 * iOS: uses pinned memory and `memcpy` for NSData interop.
 */
@OptIn(ExperimentalForeignApi::class)
actual fun writeDatabaseFile(fileName: String, bytes: ByteArray) {
    val data = NSMutableData.create(length = bytes.size.toULong()) ?: return
    bytes.usePinned { pinned ->
        memcpy(data.mutableBytes, pinned.addressOf(0), bytes.size.toULong())
    }
    data.writeToFile(getIosDbPath(fileName), atomically = true)
}
