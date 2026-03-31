package de.nogaemer.unspeakable.db

import androidx.room.RoomDatabase

/**
 * Provides a Room builder pointed at writable app storage.
 * Android/iOS/JVM: each target resolves database path and builder style differently.
 */
expect fun getDatabaseBuilder(): RoomDatabase.Builder<UnspeakableDatabase>

/**
 * Checks whether the seeded database file already exists in app storage.
 */
expect fun isDatabaseFileCopied(fileName: String): Boolean

/**
 * Writes seed database bytes to app storage before first open.
 */
expect fun writeDatabaseFile(fileName: String, bytes: ByteArray)
