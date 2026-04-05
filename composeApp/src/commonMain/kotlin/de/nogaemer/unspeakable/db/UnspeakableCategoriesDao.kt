@file:Suppress("unused")

package de.nogaemer.unspeakable.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UnspeakableCategoriesDao {

    @Query("SELECT * FROM categories ORDER BY id ASC")
    suspend fun getAllCategories(): List<UnspeakableCategoryDto>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): UnspeakableCategoryDto?
}


