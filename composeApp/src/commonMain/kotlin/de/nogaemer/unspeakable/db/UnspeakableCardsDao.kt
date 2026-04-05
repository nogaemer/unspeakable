package de.nogaemer.unspeakable.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UnspeakableCardsDao {

    @Query("SELECT * FROM cards WHERE language = :lang ORDER BY id ASC")
    suspend fun getAllCards(lang: String): List<UnspeakableCardDto>

    @Query("SELECT * FROM cards WHERE language = :lang AND id = :id")
    suspend fun getCardById(lang: String, id: Int): UnspeakableCardDto?

    @Query("SELECT * FROM cards WHERE language = :lang AND word = :word")
    suspend fun getCardByWord(lang: String, word: String): UnspeakableCardDto?

    @Query("SELECT * FROM cards WHERE language = :lang AND category IN(:categories)")
    suspend fun getCardsByCategory(lang: String, categories: List<String>): List<UnspeakableCardDto>

    @Query("SELECT * FROM cards WHERE language = :lang AND category = :category AND word = :word")
    suspend fun getCardsByCategoryAndWord(lang: String, category: String, word: String): UnspeakableCardDto?

    @Query("SELECT * FROM cards WHERE language = :lang ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomCards(lang: String, limit: Int): List<UnspeakableCardDto>

    @Query("SELECT * FROM cards WHERE language = :lang ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomCard(lang: String): UnspeakableCardDto?

    @Query("SELECT * FROM cards WHERE language = :lang AND category IN(:categories) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomCardByCategories(lang: String, categories: List<String>): UnspeakableCardDto?

    @Query("SELECT COUNT(*) FROM cards WHERE language = :lang")
    suspend fun getCardCount(lang: String): Int

    @Query("DELETE FROM cards")
    suspend fun deleteAll()

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM cards WHERE language = :lang")
    suspend fun deleteByLanguage(lang: String)
}