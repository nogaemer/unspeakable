package de.nogaemer.unspeakable.features.words.words

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import cafe.adriel.lyricist.strings
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.Timer
import com.composables.icons.lucide.Trash2
import de.nogaemer.unspeakable.core.components.menu.AppBarButton
import de.nogaemer.unspeakable.core.components.menu.DefaultMenuComponent
import de.nogaemer.unspeakable.core.components.menu.MenuChild
import de.nogaemer.unspeakable.core.components.menu.MenuChild.Overview
import de.nogaemer.unspeakable.core.components.menu.MenuPage
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.db.Graph
import de.nogaemer.unspeakable.db.UnspeakableCard
import de.nogaemer.unspeakable.db.UnspeakableCategory
import de.nogaemer.unspeakable.features.settings.SettingsOverviewComponent
import de.nogaemer.unspeakable.features.words.category.CategoryComponentScreen
import de.nogaemer.unspeakable.features.words.category.EditCategoryComponent
import de.nogaemer.unspeakable.features.words.words.word.CardComponentScreen
import de.nogaemer.unspeakable.features.words.words.word.EditCardComponent
import de.nogaemer.unspeakable.features.words.words.word.NewCardComponent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable

@Serializable
sealed class WordsConfig {
    @Serializable
    object Overview : WordsConfig()

    @Serializable
    data class NewWord(val category: UnspeakableCategory) : WordsConfig()

    @Serializable
    data class EditWord(val card: UnspeakableCard) : WordsConfig()

    data class EditCategory(val category: UnspeakableCategory) : WordsConfig()
}

class WordsComponent(
    ctx: ComponentContext,
    override val onBack: () -> Unit,
    val category: UnspeakableCategory,
) : DefaultMenuComponent<WordsConfig, SettingsOverviewComponent<WordsConfig>>(
    ctx = ctx,
    serializer = WordsConfig.serializer(),
    initialConfig = WordsConfig.Overview,
    childFactory = { config, childCtx, push, pop, _ ->
        when (config) {
            is WordsConfig.Overview -> Overview(
                SettingsOverviewComponent(
                    ctx = childCtx,
                    goBack = onBack,
                    onNavigate = push,
                    items = listOf()
                )
            ){
                AppBarButton({ push(WordsConfig.EditCategory(category)) }, Lucide.Pencil, Color.Transparent)
            }

            is WordsConfig.NewWord -> {
                val c = NewCardComponent(childCtx, pop, config.category)
                MenuChild.Page(c){
                    CardComponentScreen(c)
                }
            }

            is WordsConfig.EditWord -> {
                val c = EditCardComponent(childCtx, pop, config.card)
                MenuChild.Page(c, actions = {
                    IconButton(onClick = {
                        c.deleteCard { pop() }
                    }) {
                        Icon(
                            imageVector = Lucide.Trash2,
                            contentDescription = strings.common.deleteCard
                        )
                    }
                }){
                    CardComponentScreen(c)
                }
            }

            is WordsConfig.EditCategory -> {
                val c = EditCategoryComponent(childCtx, pop, config.category)
                MenuChild.Page(c, actions = {
                    IconButton(onClick = {
                        c.deleteCategory { onBack() }
                    }) {
                        Icon(
                            imageVector = Lucide.Trash2,
                            contentDescription = strings.common.deleteCategory
                        )
                    }
                }) {
                    CategoryComponentScreen(c)
                }
            }
        }
    }
), MenuPage {
    override val titleKey = { s: Strings -> s.game.categoryItemStrings[category.id]?.title ?: category.name }
    override val icon = Lucide.Timer

    val scope = coroutineScope()

    val words: StateFlow<List<UnspeakableCard>> = flow {
        val lang = Graph.settings.appSettings.locales.lang
        emit(
            Graph.dao.getCardsByCategory(lang, listOf(category.id))
            .map { it.toUnspeakableCard() }
            .sortedBy { it.word }
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

}