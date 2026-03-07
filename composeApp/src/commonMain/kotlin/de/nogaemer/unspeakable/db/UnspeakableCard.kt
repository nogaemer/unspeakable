package de.nogaemer.unspeakable.db


import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "cards")
data class UnspeakableCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val category: String,
    val language: String,
    val forbidden1: String,
    val forbidden2: String,
    val forbidden3: String,
    val forbidden4: String,
    val forbidden5: String
)