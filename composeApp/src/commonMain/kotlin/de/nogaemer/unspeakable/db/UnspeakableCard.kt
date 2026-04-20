package de.nogaemer.unspeakable.db


import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "cards")
data class UnspeakableCardDto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val category: String,
    val language: String,
    val forbidden1: String,
    val forbidden2: String,
    val forbidden3: String,
    val forbidden4: String,
    val forbidden5: String
) {
    fun toUnspeakableCard() = UnspeakableCard(id, word, category, language, listOf(forbidden1, forbidden2, forbidden3, forbidden4, forbidden5))
}

@Serializable
data class UnspeakableCard(
    val id: Int,
    val word: String,
    val category: String,
    val language: String,
    val forbiddenWords: List<String> = emptyList(),
) {
    fun toCardDto() = UnspeakableCardDto(
        id,
        word,
        category,
        language,
        forbiddenWords.getOrElse(0) { "" },
        forbiddenWords.getOrElse(1) { "" },
        forbiddenWords.getOrElse(2) { "" },
        forbiddenWords.getOrElse(3) { "" },
        forbiddenWords.getOrElse(4) { "" }
    )
}