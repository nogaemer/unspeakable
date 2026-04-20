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
    version = 3
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

/**
 * Migrates schema from version 2 to 3 by adding category display metadata
 * and backfilling it for the bundled category ids.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE categories ADD COLUMN name TEXT NOT NULL DEFAULT ''")
        connection.execSQL("ALTER TABLE categories ADD COLUMN icon TEXT NOT NULL DEFAULT ''")
        connection.execSQL("BEGIN TRANSACTION")
        try {
            CATEGORY_SEEDS.forEach { seed ->
                val statement = connection.prepare("UPDATE categories SET name = ?, icon = ? WHERE id = ?")
                try {
                    statement.bindText(1, seed.name)
                    statement.bindText(2, seed.iconName)
                    statement.bindText(3, seed.id)
                    statement.step()
                } finally {
                    statement.close()
                }
            }
            connection.execSQL("COMMIT")
        } catch (throwable: Throwable) {
            connection.execSQL("ROLLBACK")
            throw throwable
        }
    }
}

private data class CategorySeed(
    val id: String,
    val name: String,
    val iconName: String,
)

private val CATEGORY_SEEDS = listOf(
    CategorySeed("adjectives", "Adjectives", "Sparkles"),
    CategorySeed("animation", "Animations", "Film"),
    CategorySeed("art", "Art", "Palette"),
    CategorySeed("body", "Body", "PersonStanding"),
    CategorySeed("clothing", "Clothing", "Shirt"),
    CategorySeed("entertainment", "Entertainment", "PartyPopper"),
    CategorySeed("environment", "Environment", "Trees"),
    CategorySeed("events", "Events", "CalendarDays"),
    CategorySeed("food", "Food", "UtensilsCrossed"),
    CategorySeed("games", "Games", "Gamepad2"),
    CategorySeed("geography", "Geography", "Globe"),
    CategorySeed("health", "Health", "HeartPulse"),
    CategorySeed("history", "History", "Landmark"),
    CategorySeed("home", "Home", "House"),
    CategorySeed("media", "Media", "Clapperboard"),
    CategorySeed("music", "Music", "Music2"),
    CategorySeed("nature", "Nature", "Leaf"),
    CategorySeed("objects", "Objects", "Package"),
    CategorySeed("people_and_feelings", "People & Feelings", "Smile"),
    CategorySeed("people_and_jobs", "People & Jobs", "BriefcaseBusiness"),
    CategorySeed("places", "Places", "MapPinned"),
    CategorySeed("politics", "Politics", "Scale"),
    CategorySeed("religion", "Religion", "Cross"),
    CategorySeed("school_and_work", "School & Work", "GraduationCap"),
    CategorySeed("sports_and_games", "Sports & Games", "Trophy"),
    CategorySeed("technology", "Technology", "Cpu"),
    CategorySeed("travel", "Travel", "Plane"),
    CategorySeed("verbs", "Verbs", "SpellCheck"),
    CategorySeed("weather", "Weather", "CloudSun"),
)

@Suppress("KotlinNoActualForExpect")
expect object UnspeakableDatabaseConstructor : RoomDatabaseConstructor<UnspeakableDatabase> {
    override fun initialize(): UnspeakableDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<UnspeakableDatabase>
): UnspeakableDatabase {
    return builder
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
}
