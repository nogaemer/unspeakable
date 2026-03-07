package de.nogaemer.unspeakable.db

import androidx.room.RoomDatabase


expect fun getDatabaseBuilder(): RoomDatabase.Builder<UnspeakableDatabase>
expect fun isDatabaseFileCopied(fileName: String): Boolean
expect fun writeDatabaseFile(fileName: String, bytes: ByteArray)
