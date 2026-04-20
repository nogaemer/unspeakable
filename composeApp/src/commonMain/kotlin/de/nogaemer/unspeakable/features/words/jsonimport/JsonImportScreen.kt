package de.nogaemer.unspeakable.features.words.jsonimport

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TriangleAlert
import com.composables.icons.lucide.Upload
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedLazyColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem
import de.nogaemer.unspeakable.core.util.file.rememberJsonFilePicker
import de.nogaemer.unspeakable.core.util.icon.byString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull

@Composable
fun JsonImportScreen(component: JsonImportComponent) {
    val s = strings
    val uiState by component.uiState.collectAsState()
    val openFilePicker = rememberJsonFilePicker(
        onJsonPicked = component::onPickedJson,
        onPickFailed = { throwable ->
            val detail = throwable?.message?.takeIf { it.isNotBlank() }
                ?.let { s.jsonImport.fileReadFailed(it) }
                ?: s.jsonImport.filePickerUnavailable
            component.onImportError(detail)
        },
    )

    JsonImportContent(
        jsonText = uiState.jsonText,
        existingCategoryIcons = uiState.existingCategoryIcons,
        isImporting = uiState.isImporting,
        importedCardsCount = uiState.importedCardsCount,
        importedCategoriesCount = uiState.importedCategoriesCount,
        skippedCardsCount = uiState.skippedCardsCount,
        validationError = uiState.validationError,
        onJsonTextChange = component::onJsonTextChange,
        onCancel = component.onBack,
        onImport = component::importJson,
        onPickFile = openFilePicker,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun JsonImportContent(
    jsonText: String,
    existingCategoryIcons: Map<String, String>,
    isImporting: Boolean,
    importedCardsCount: Int,
    importedCategoriesCount: Int,
    skippedCardsCount: Int,
    validationError: JsonValidationError?,
    onJsonTextChange: (String) -> Unit,
    onCancel: () -> Unit,
    onImport: () -> Unit,
    onPickFile: () -> Unit,
) {
    val s = strings
    val previewSummary = remember(jsonText, existingCategoryIcons) {
        parseImportPreview(
            rawJson = jsonText,
            existingCategoryIcons = existingCategoryIcons,
        )
    }
    val showPreviewPopup = remember { mutableStateOf(false) }

    fun closePreviewPopup() {
        showPreviewPopup.value = false
    }

    val success = validationError == null &&
            (importedCardsCount > 0 || importedCategoriesCount > 0 || skippedCardsCount > 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surface
            )
            .imePadding()
            .padding(horizontal = 16.dp).padding(bottom = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            OutlinedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onPickFile),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp, top = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            Modifier.background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(50)
                            ).padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Upload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Text(
                            text = s.jsonImport.uploadDropzoneTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = s.jsonImport.uploadDropzoneSubtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = s.common.or,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = s.jsonImport.pastePayloadTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )

                    OutlinedTextField(
                        value = jsonText,
                        onValueChange = onJsonTextChange,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 320.dp).background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(16.dp)
                        ),
                        minLines = 8,
                        placeholder = {
                            Text(sampleImportJson)
                        },
                        shape = RoundedCornerShape(16.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    )

                    if (validationError != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.errorContainer,
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Lucide.TriangleAlert,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                            )
                            Text(
                                text = if (validationError.line != null && validationError.column != null) {
                                    s.jsonImport.errorWithLine(
                                        validationError.line,
                                        validationError.column,
                                        validationError.details,
                                    )
                                } else {
                                    s.jsonImport.errorGeneric(validationError.details)
                                },
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }

                        validationError.path?.takeIf { it.isNotBlank() }?.let { path ->
                            Text(
                                text = s.jsonImport.errorPath(path),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }

            if (success) {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = s.jsonImport.successTitle,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = s.jsonImport.categoriesCount(importedCategoriesCount),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = s.jsonImport.cardsCount(importedCardsCount),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = s.jsonImport.skippedCardsCount(skippedCardsCount),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = s.jsonImport.tip,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilledTonalButton(
                onClick = onCancel,
                modifier = Modifier.height(ButtonDefaults.MediumContainerHeight).weight(1f),
                contentPadding = ButtonDefaults.MediumContentPadding,
                shapes = ButtonDefaults.shapesFor(40.dp),
            ) {
                Text(s.common.cancel)
            }

            Button(
                onClick = { showPreviewPopup.value = true },
                enabled = jsonText.isNotBlank() && !isImporting,
                modifier = Modifier.height(ButtonDefaults.MediumContainerHeight).weight(1f),
                contentPadding = ButtonDefaults.MediumContentPadding,
                shapes = ButtonDefaults.shapesFor(40.dp),
            ) {
                Text(if (isImporting) s.jsonImport.importingButton else s.jsonImport.importButton)
            }
        }
    }

    if (showPreviewPopup.value) {
        JsonImportPreviewDialog(
            previewSummary = previewSummary,
            isImporting = isImporting,
            onDismissRequest = { closePreviewPopup() },
            onConfirm = {
                closePreviewPopup()
                onImport()
            },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun JsonImportPreviewDialog(
    previewSummary: ImportPreviewSummary,
    isImporting: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    val s = strings
    val previewCards = previewSummary.cards

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = s.jsonImport.previewPopupTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = s.jsonImport.previewCount(previewSummary.cards.size),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = s.jsonImport.categoriesCount(previewSummary.categoriesCount),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                }

                SegmentedLazyColumn (
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp)
                ) {
                    if (previewCards.isEmpty()) {
                        item { Text(
                            text = s.jsonImport.previewEmpty,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp),
                        )}
                    } else {
                        items(previewCards) { card ->
                            SegmentedListItem(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                leadingContent = {
                                    Icon(
                                        imageVector = Lucide.byString(card.categoryIconName),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                },
                                headlineContent = {
                                    Text(
                                        text = card.word,
                                    )
                                },
                                supportingContent = {
                                    if (card.forbiddenWords.isNotEmpty()) {
                                        Text(
                                            text = card.forbiddenWords.joinToString(", "),
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        dismissButton = {
            FilledTonalButton(
                onClick = onDismissRequest,
                modifier = Modifier.height(40.dp),
            ) {
                Text(s.common.cancel)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isImporting,
                modifier = Modifier.height(40.dp),
            ) {
                Text(if (isImporting) s.jsonImport.importingButton else s.jsonImport.importButton)
            }
        },
        modifier = Modifier.widthIn(max = 560.dp),
    )
}

private data class PreviewCard(
    val word: String,
    val category: String,
    val categoryIconName: String?,
    val forbiddenWords: List<String>,
)

private data class ImportPreviewSummary(
    val cards: List<PreviewCard>,
    val categoriesCount: Int,
)

private fun parseImportPreview(
    rawJson: String,
    existingCategoryIcons: Map<String, String> = emptyMap(),
): ImportPreviewSummary {
    val root = runCatching { previewParser.parseToJsonElement(rawJson) }
        .getOrNull()
        as? JsonObject
        ?: return ImportPreviewSummary(emptyList(), 0)

    val categories = root["categories"] as? JsonArray ?: JsonArray(emptyList())
    val defaultCategory = root.readString("default_category")

    val categoryLookup = buildPreviewCategoryLookup(
        categories = categories,
        existingCategoryIcons = existingCategoryIcons,
    )

    val categoryIconById = categories
        .mapNotNull { category ->
            val json = category as? JsonObject ?: return@mapNotNull null
            val id = json.readString("id")
            val name = json.readString("name")
            val resolvedId = id.ifBlank { previewSlugify(name) }
            if (resolvedId.isBlank()) return@mapNotNull null

            val iconName = json.readString("iconName")
            if (iconName.isBlank()) {
                null
            } else {
                buildList<Pair<String, String>> {
                    add(resolvedId to iconName)
                    add(resolvedId.lowercase() to iconName)
                    if (name.isNotBlank()) {
                        add(name.lowercase() to iconName)
                    }
                }
            }
        }
        .flatten()
        .toMap()

    val resolvedCategoryIcons = buildMap {
        putAll(existingCategoryIcons)
        putAll(categoryIconById)
    }

    val cards = (root["cards"] as? JsonArray)
        ?.mapNotNull {
            it.toPreviewCard(
                categoryLookup = categoryLookup,
                categoryIconById = resolvedCategoryIcons,
                fallbackCategory = defaultCategory,
            )
        }
        .orEmpty()

    val categoriesCount = categories.size

    return ImportPreviewSummary(cards = cards, categoriesCount = categoriesCount)
}

private fun buildPreviewCategoryLookup(
    categories: JsonArray,
    existingCategoryIcons: Map<String, String>,
): Map<String, String> {
    return buildMap {
        existingCategoryIcons.keys.forEach { key ->
            val trimmed = key.trim()
            if (trimmed.isBlank()) return@forEach
            put(trimmed, trimmed)
            put(trimmed.lowercase(), trimmed)
        }

        categories.forEach { category ->
            val json = category as? JsonObject ?: return@forEach
            val id = json.readString("id")
            val name = json.readString("name")
            val resolvedId = id.ifBlank { previewSlugify(name) }
            if (resolvedId.isBlank()) return@forEach

            put(resolvedId, resolvedId)
            put(resolvedId.lowercase(), resolvedId)
            if (name.isNotBlank()) {
                put(name.lowercase(), resolvedId)
            }
        }
    }
}

private fun resolvePreviewCategoryId(
    categoryLookup: Map<String, String>,
    cardCategory: String,
    fallbackCategory: String,
): String {
    if (cardCategory.isNotBlank()) {
        return categoryLookup[cardCategory]
            ?: categoryLookup[cardCategory.lowercase()]
            ?: cardCategory
    }

    if (fallbackCategory.isNotBlank()) {
        return categoryLookup[fallbackCategory]
            ?: categoryLookup[fallbackCategory.lowercase()]
            ?: fallbackCategory
    }

    return ""
}

private fun JsonElement.toPreviewCard(
    categoryLookup: Map<String, String>,
    categoryIconById: Map<String, String>,
    fallbackCategory: String,
): PreviewCard? {
    val card = this as? JsonObject ?: return null
    val word = card.readString("word")
    if (word.isBlank()) return null

    val rawCategory = card.readString("category")
    val resolvedCategory = resolvePreviewCategoryId(
        categoryLookup = categoryLookup,
        cardCategory = rawCategory,
        fallbackCategory = fallbackCategory,
    )

    val categoryIconName = categoryIconById[resolvedCategory]
        ?: categoryIconById[resolvedCategory.lowercase()]
        ?: categoryIconById[rawCategory]
        ?: categoryIconById[rawCategory.lowercase()]

    val forbiddenWords = card["forbiddenWords"]
        ?.asStringArray()
        .orEmpty()
        .map(String::trim)
        .filter(String::isNotBlank)

    return PreviewCard(
        word = word,
        category = resolvedCategory.ifBlank { rawCategory },
        categoryIconName = categoryIconName,
        forbiddenWords = forbiddenWords,
    )
}

private fun previewSlugify(value: String): String {
    return value
        .trim()
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
}

private fun JsonObject.readString(key: String): String {
    return (this[key] as? JsonPrimitive)
        ?.contentOrNull
        ?.trim()
        .orEmpty()
}

private fun JsonElement.asStringArray(): List<String> {
    val array = this as? JsonArray ?: return emptyList()
    return array.mapNotNull { item ->
        val primitive = item as? JsonPrimitive ?: return@mapNotNull null
        if (primitive.isString) {
            primitive.contentOrNull
        } else {
            primitive.booleanOrNull?.toString() ?: primitive.contentOrNull
        }
    }
}

private val previewParser = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

@Preview
@Composable
private fun JsonImportContentPreview() {
    AppTheme(darkTheme = true) {
        JsonImportContent(
            jsonText = sampleImportJson,
            existingCategoryIcons = emptyMap(),
            isImporting = false,
            importedCardsCount = 12,
            importedCategoriesCount = 3,
            skippedCardsCount = 1,
            validationError = null,
            onJsonTextChange = {},
            onCancel = {},
            onImport = {},
            onPickFile = {},
        )
    }
}

@Preview
@Composable
private fun JsonImportPreviewDialogPreview() {
    AppTheme(darkTheme = true) {
        JsonImportPreviewDialog(
            previewSummary = parseImportPreview(sampleImportJsonLarge),
            isImporting = false,
            onDismissRequest = {},
            onConfirm = {},
        )
    }
}
