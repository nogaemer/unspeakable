package de.nogaemer.unspeakable.features.words.jsonimport

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import de.nogaemer.unspeakable.core.components.menu.AbstractMenuPage
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.db.Graph
import de.nogaemer.unspeakable.db.UnspeakableCard
import de.nogaemer.unspeakable.db.UnspeakableCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

data class JsonImportUiState(
    val jsonText: String = "",
    val isImporting: Boolean = false,
    val importedCardsCount: Int = 0,
    val importedCategoriesCount: Int = 0,
    val skippedCardsCount: Int = 0,
    val validationError: JsonValidationError? = null,
    val existingCategoryIcons: Map<String, String> = emptyMap(),
)

data class JsonValidationError(
    val line: Int? = null,
    val column: Int? = null,
    val path: String? = null,
    val details: String = "",
)

class JsonImportComponent(
    ctx: ComponentContext,
    onBack: () -> Unit,
) : AbstractMenuPage(ctx, onBack) {
    override val titleKey = { s: Strings -> s.jsonImport.title }
    override val icon = Lucide.Plus

    private val scope = ctx.coroutineScope()

    private val _uiState = MutableStateFlow(JsonImportUiState(jsonText = ""))
    val uiState: StateFlow<JsonImportUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            refreshExistingCategoryIcons()
        }
    }

    fun onJsonTextChange(value: String) {
        _uiState.update { it.copy(jsonText = value, validationError = null) }
    }

    fun onPickedJson(value: String) {
        _uiState.update { it.copy(jsonText = value, validationError = null) }
    }

    fun onImportError(details: String) {
        _uiState.update {
            it.copy(
                validationError = JsonValidationError(details = details),
                isImporting = false,
            )
        }
    }

    fun importJson() {
        val snapshot = _uiState.value
        if (snapshot.isImporting) return

        scope.launch {
            _uiState.update {
                it.copy(
                    isImporting = true,
                    importedCardsCount = 0,
                    importedCategoriesCount = 0,
                    skippedCardsCount = 0,
                    validationError = null,
                )
            }

            val result = runCatching { importJsonInternal(snapshot.jsonText) }
            result.onSuccess { summary ->
                val existingCategoryIcons = buildCategoryIconLookup()
                _uiState.update {
                    it.copy(
                        isImporting = false,
                        importedCardsCount = summary.importedCards,
                        importedCategoriesCount = summary.importedCategories,
                        skippedCardsCount = summary.skippedCards,
                        existingCategoryIcons = existingCategoryIcons,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isImporting = false,
                        validationError = mapValidationError(
                            rawJson = snapshot.jsonText,
                            throwable = throwable,
                        ),
                    )
                }
            }
        }
    }

    private suspend fun refreshExistingCategoryIcons() {
        val existingCategoryIcons = buildCategoryIconLookup()
        _uiState.update { it.copy(existingCategoryIcons = existingCategoryIcons) }
    }

    private suspend fun importJsonInternal(rawJson: String): ImportSummary {
        val payload = parser.decodeFromString<ImportPayload>(rawJson)

        val defaultLanguage = Graph.settings.appSettings.locales.lang
        val categoryLookup = buildCategoryLookup()
        val summary = ImportSummary(importedCards = 0, importedCategories = 0, skippedCards = 0)

        payload.categories.forEach { categoryJson ->
            val normalized = normalizeCategory(categoryJson) ?: return@forEach
            Graph.categoriesDao.saveCategory(normalized.toCategoryDto())
            categoryLookup[normalized.id] = normalized.id
            categoryLookup[normalized.name.lowercase()] = normalized.id
            summary.importedCategories++
        }

        payload.cards.forEach { cardJson ->
            val normalized = normalizeCard(cardJson, defaultLanguage)
            if (normalized.word.isBlank()) {
                summary.skippedCards++
                return@forEach
            }

            val resolvedCategory = resolveCategoryId(
                categoryLookup = categoryLookup,
                cardCategory = normalized.category,
                fallbackCategory = payload.defaultCategory,
            )

            val card = UnspeakableCard(
                id = 0,
                word = normalized.word,
                category = resolvedCategory,
                language = normalized.language,
                forbiddenWords = normalized.forbiddenWords,
            )

            val inserted = runCatching {
                Graph.dao.saveCard(card.toCardDto())
            }.isSuccess

            if (inserted) {
                summary.importedCards++
            } else {
                summary.skippedCards++
            }
        }

        return summary
    }

    private suspend fun buildCategoryLookup(): MutableMap<String, String> {
        val categories = Graph.categoriesDao.getAllCategories().map { it.toUnspeakableCategory() }
        return buildMap {
            categories.forEach { category ->
                put(category.id, category.id)
                put(category.id.lowercase(), category.id)
                put(category.name.lowercase(), category.id)
            }
        }.toMutableMap()
    }

    private suspend fun buildCategoryIconLookup(): Map<String, String> {
        val categories = Graph.categoriesDao.getAllCategories().map { it.toUnspeakableCategory() }
        return buildMap {
            categories.forEach { category ->
                val iconName = category.iconName.trim()
                if (iconName.isBlank()) return@forEach

                val id = category.id.trim()
                val name = category.name.trim()
                if (id.isNotBlank()) {
                    put(id, iconName)
                    put(id.lowercase(), iconName)
                }
                if (name.isNotBlank()) {
                    put(name.lowercase(), iconName)
                }
            }
        }
    }
}

private fun normalizeCategory(categoryJson: ImportCategoryJson): UnspeakableCategory? {
    val name = categoryJson.name.trim()
    val id = categoryJson.id?.trim().orEmpty().ifBlank { slugify(name) }
    if (id.isBlank()) return null
    return UnspeakableCategory(
        id = id,
        name = name.ifBlank { id },
        iconName = categoryJson.iconName?.trim().orEmpty().ifBlank { "Layers2" },
    )
}

private fun normalizeCard(cardJson: ImportCardJson, defaultLanguage: String): NormalizedCard {
    val forbiddenWords = cardJson.forbiddenWords
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .take(5)
        .toMutableList()

    while (forbiddenWords.size < 5) {
        forbiddenWords.add("")
    }

    return NormalizedCard(
        word = cardJson.word.trim(),
        forbiddenWords = forbiddenWords,
        category = cardJson.category?.trim().orEmpty(),
        language = cardJson.language?.trim().orEmpty().ifBlank { defaultLanguage },
    )
}

private fun resolveCategoryId(
    categoryLookup: Map<String, String>,
    cardCategory: String,
    fallbackCategory: String?,
): String {
    if (cardCategory.isNotBlank()) {
        return categoryLookup[cardCategory]
            ?: categoryLookup[cardCategory.lowercase()]
            ?: cardCategory
    }

    val fallback = fallbackCategory?.trim().orEmpty()
    if (fallback.isNotBlank()) {
        return categoryLookup[fallback]
            ?: categoryLookup[fallback.lowercase()]
            ?: fallback
    }

    return ""
}

private fun slugify(value: String): String {
    return value
        .trim()
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
}

private data class NormalizedCard(
    val word: String,
    val forbiddenWords: List<String>,
    val category: String,
    val language: String,
)

private data class ImportSummary(
    var importedCards: Int,
    var importedCategories: Int,
    var skippedCards: Int,
)

@Serializable
private data class ImportPayload(
    val categories: List<ImportCategoryJson> = emptyList(),
    val cards: List<ImportCardJson> = emptyList(),
    @SerialName("default_category")
    val defaultCategory: String? = null,
)

@Serializable
private data class ImportCategoryJson(
    val id: String? = null,
    val name: String = "",
    val iconName: String? = null,
)

@Serializable
private data class ImportCardJson(
    val word: String = "",
    val forbiddenWords: List<String> = emptyList(),
    val category: String? = null,
    val language: String? = null,
)

private val parser = Json {
    ignoreUnknownKeys = false
    isLenient = false
    explicitNulls = false
    coerceInputValues = false
}

private fun mapValidationError(rawJson: String, throwable: Throwable): JsonValidationError {
    val message = throwable.message.orEmpty()
    val offset = OFFSET_REGEX.find(message)
        ?.groupValues
        ?.getOrNull(1)
        ?.toIntOrNull()

    val (line, column) = offset
        ?.let { offsetToLineColumn(rawJson, it) }
        ?: (null to null)

    val path = PATH_REGEX.find(message)
        ?.groupValues
        ?.getOrNull(1)
        ?.trim()

    val details = message
        .lineSequence()
        .firstOrNull()
        .orEmpty()
        .trim()

    return JsonValidationError(
        line = line,
        column = column,
        path = path,
        details = details,
    )
}

private fun offsetToLineColumn(input: String, offset: Int): Pair<Int, Int> {
    val safeOffset = offset.coerceIn(0, input.length)
    val before = input.substring(0, safeOffset)
    val line = before.count { it == '\n' } + 1
    val lastLineBreak = before.lastIndexOf('\n')
    val column = if (lastLineBreak == -1) {
        safeOffset + 1
    } else {
        safeOffset - lastLineBreak
    }
    return line to column
}

private val OFFSET_REGEX = Regex("offset\\s+(\\d+)")
private val PATH_REGEX = Regex("path:\\s*([^\\n]+)")

val sampleImportJson = """
{
  "categories": [
    {
      "id": "animals",
      "name": "Animals",
      "iconName": "PawPrint"
    }
  ],
  "cards": [
    {
      "word": "Tiger",
      "forbiddenWords": ["Cat", "Jungle", "Stripe", "Wild", "Feline"],
      "category": "animals",
      "language": "en"
    }
  ],
  "default_category": "animals"
}
""".trimIndent()

val sampleImportJsonLarge = """
{
"categories": [
{
"id": "general",
"name": "General",
"iconName": "Layers2"
}
],
"cards": [
{
"word": "Sun",
"forbiddenWords": ["Star", "Day", "Sky", "Light", "Hot"],
"category": "general",
"language": "en"
},
{
"word": "Moon",
"forbiddenWords": ["Night", "Sky", "Orbit", "Lunar", "Light"],
"category": "general",
"language": "en"
},
{
"word": "River",
"forbiddenWords": ["Water", "Stream", "Flow", "Bank", "Current"],
"category": "general",
"language": "en"
},
{
"word": "Mountain",
"forbiddenWords": ["Peak", "Hill", "Climb", "Rock", "Height"],
"category": "general",
"language": "en"
},
{
"word": "Forest",
"forbiddenWords": ["Trees", "Woods", "Nature", "Green", "Wild"],
"category": "general",
"language": "en"
},
{
"word": "Ocean",
"forbiddenWords": ["Sea", "Water", "Wave", "Deep", "Blue"],
"category": "general",
"language": "en"
},
{
"word": "Computer",
"forbiddenWords": ["PC", "Laptop", "Screen", "Keyboard", "Code"],
"category": "general",
"language": "en"
},
{
"word": "Book",
"forbiddenWords": ["Read", "Pages", "Author", "Library", "Story"],
"category": "general",
"language": "en"
},
{
"word": "Train",
"forbiddenWords": ["Rail", "Station", "Track", "Engine", "Travel"],
"category": "general",
"language": "en"
},
{
"word": "Pizza",
"forbiddenWords": ["Food", "Cheese", "Slice", "Italian", "Oven"],
"category": "general",
"language": "en"
}
],
"default_category": "general"
}
""".trimIndent()







