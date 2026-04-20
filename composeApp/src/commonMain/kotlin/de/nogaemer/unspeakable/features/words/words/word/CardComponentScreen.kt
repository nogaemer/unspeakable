package de.nogaemer.unspeakable.features.words.words.word

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.util.icon.byString
import de.nogaemer.unspeakable.db.UnspeakableCard
import de.nogaemer.unspeakable.db.UnspeakableCategory
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun CardComponentScreen(
    component: CardComponent,
) {
    val categories by component.categories.collectAsState()


    CardEditorContent(
        modifier = Modifier.fillMaxSize(),
        card = component.card,
        categories,
        updateCard = { component.card = it },
        onBack = component.onBack,
        onSave = { component.saveCard() },
    )

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CardEditorContent(
    modifier: Modifier = Modifier,
    card: UnspeakableCard,
    categories: List<UnspeakableCategory> = emptyList(),
    updateCard: (UnspeakableCard) -> Unit = {},
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
) {
    val s = strings
    var term by remember { mutableStateOf(card.word) }
    var forbiddenWords by remember { mutableStateOf(card.forbiddenWords) }

    var categoryInput by remember(categories, card.category) {
        mutableStateOf(
            categories.find { it.id == card.category } ?: categories.firstOrNull()
        )
    }

    LaunchedEffect(term, forbiddenWords, categoryInput) {
        updateCard(
            card.copy(
                word = term,
                forbiddenWords = forbiddenWords,
                category = categoryInput?.id ?: card.category
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .padding(horizontal = 16.dp).padding(bottom = 16.dp),
    ) {
        Column(
            modifier = modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(24.dp)
                ).verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        ) {


            CardInputField(
                value = term,
                onValueChange = { term = it },
                placeholder = s.cardEditor.term,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))


            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                forbiddenWords.forEachIndexed { index, word ->
                    CardInputField(
                        value = word,
                        onValueChange = { newValue ->
                            val newList = forbiddenWords.toMutableList()
                            newList[index] = newValue
                            forbiddenWords = newList
                        },
                        placeholder = s.cardEditor.prohibitedTerm(index + 1),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (categories.isNotEmpty()) {
                categoryInput?.let { selectedCategory ->
                    CategoryPicker(
                        options = categories,
                        selected = selectedCategory,
                        onSelect = { categoryInput = it }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.End),
        ) {
            FilledTonalButton(
                onClick = onBack,
                modifier = Modifier.height(ButtonDefaults.MediumContainerHeight).weight(1f),
                contentPadding = ButtonDefaults.MediumContentPadding,
                shapes = ButtonDefaults.shapesFor(40.dp),
            ) {
                Text(s.common.cancel)
            }

            Button(
                onClick = onSave,
                modifier = Modifier.height(ButtonDefaults.MediumContainerHeight).weight(1f),
                contentPadding = ButtonDefaults.MediumContentPadding,
                shapes = ButtonDefaults.shapesFor(40.dp),
            ) {
                Text(s.common.save)
            }
        }
    }
}

@Composable
fun CardInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val state = rememberTextFieldState(
        initialText = value,
    )

    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }
            .distinctUntilChanged()
            .collect { onValueChange(it) }
    }


    OutlinedTextField(
        state = state,
        label = { Text(placeholder) },
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPicker(
    options: List<UnspeakableCategory>,
    selected: UnspeakableCategory,
    onSelect: (UnspeakableCategory) -> Unit
) {
    val s = strings
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            leadingIcon = {
                if (selected.iconName.isNotEmpty()) {
                    Icon(
                        imageVector = Lucide.byString(selected.iconName),
                        contentDescription = null,
                    )
                }
            },
            value = selected.getTranslatedName(strings),
            onValueChange = {},
            readOnly = true,
            label = { Text(s.cardEditor.category) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable) // ← required
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                val textColor =
                    if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                val backgroundColor =
                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer

                DropdownMenuItem(
                    colors = MenuDefaults.itemColors().copy(
                        textColor = textColor,
                        leadingIconColor = textColor,
                    ),
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor),
                    text = { Text(option.getTranslatedName(strings)) },
                    onClick = { onSelect(option); expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    leadingIcon = {
                        Icon(
                            imageVector = Lucide.byString(option.iconName),
                            contentDescription = null,
                        )
                    },
                    trailingIcon = if (option == selected) {
                        { Icon(Lucide.Check, contentDescription = null) }
                    } else null
                )
            }
        }
    }
}

@Preview
@Composable
fun CardComponentScreenPreview() {
    AppTheme(darkTheme = true) {
        CardEditorContent(
            modifier = Modifier.fillMaxSize(), card = UnspeakableCard(
                id = 0,
                word = "Example",
                forbiddenWords = listOf("Forbidden1", "Forbidden2", "Forbidden3", "Forbidden4"),
                category = "1",
                language = "en",
            ), categories = listOf(
                UnspeakableCategory(
                    id = "1",
                    name = "Category 1",
                    iconName = "Star"
                ),
                UnspeakableCategory(
                    id = "2",
                    name = "Category 2",
                    iconName = "Check"
                ),
            )
        )
    }
}
