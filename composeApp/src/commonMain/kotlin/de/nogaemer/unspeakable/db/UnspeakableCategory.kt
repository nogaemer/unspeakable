package de.nogaemer.unspeakable.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "categories")
data class UnspeakableCategoryDto(
    @PrimaryKey val id: String,
)

