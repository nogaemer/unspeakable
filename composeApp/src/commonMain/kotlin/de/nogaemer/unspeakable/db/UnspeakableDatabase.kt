@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package de.nogaemer.unspeakable.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [
        UnspeakableCard::class,
               ],
    version = 1
)
@ConstructedBy(UnspeakableDatabaseConstructor::class)
abstract class UnspeakableDatabase : RoomDatabase() {
    abstract fun unspeakableCardsDao(): UnspeakableCardsDao
}

@Suppress("KotlinNoActualForExpect")
expect object UnspeakableDatabaseConstructor : RoomDatabaseConstructor<UnspeakableDatabase> {
    override fun initialize(): UnspeakableDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<UnspeakableDatabase>
): UnspeakableDatabase {
    return builder
        .addMigrations()
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
}


fun getUnspeakableCardsDao(appDatabase: UnspeakableDatabase) = appDatabase.unspeakableCardsDao()