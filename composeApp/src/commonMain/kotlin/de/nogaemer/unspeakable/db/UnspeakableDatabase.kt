@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package de.nogaemer.unspeakable.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [
        UnspeakableCardDto::class,
        UnspeakableCategoryDto::class,
    ],
    version = 2
)
@ConstructedBy(UnspeakableDatabaseConstructor::class)
abstract class UnspeakableDatabase : RoomDatabase() {
    abstract fun unspeakableCardsDao(): UnspeakableCardsDao

    abstract fun unspeakableCategoriesDao(): UnspeakableCategoriesDao
}

/**
 * Migrates schema from version 1 to 2 by creating the `categories` table
 * and backfilling it with distinct existing category values from `cards`.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS categories (
                id TEXT NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
        connection.execSQL("INSERT OR IGNORE INTO categories(id) SELECT DISTINCT category FROM cards")
    }
}

@Suppress("KotlinNoActualForExpect")
expect object UnspeakableDatabaseConstructor : RoomDatabaseConstructor<UnspeakableDatabase> {
    override fun initialize(): UnspeakableDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<UnspeakableDatabase>
): UnspeakableDatabase {
    return builder
        .addMigrations(MIGRATION_1_2)
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
}
