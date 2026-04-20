package de.nogaemer.unspeakable.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.core.i18n.categoryName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "categories")
data class UnspeakableCategoryDto(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "icon") val iconName: String,
) {
    fun toUnspeakableCategory() = UnspeakableCategory(id = id, name = name, iconName = iconName)
}

@Serializable
data class UnspeakableCategory(
    val id: String,
    val name: String = id,
    val iconName: String,
) {
    fun getTranslatedName(strings: Strings): String {
        return strings.categoryName(id) ?: name
    }

    fun toCategoryDto() = UnspeakableCategoryDto(id, name, iconName)
}

