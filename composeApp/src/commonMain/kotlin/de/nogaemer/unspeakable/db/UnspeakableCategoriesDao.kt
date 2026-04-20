@file:Suppress("unused")

package de.nogaemer.unspeakable.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UnspeakableCategoriesDao {

    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAllCategories(): List<UnspeakableCategoryDto>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): UnspeakableCategoryDto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCategory(category: UnspeakableCategoryDto)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: String)
}


