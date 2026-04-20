package de.nogaemer.unspeakable.features.words.words.word

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import de.nogaemer.unspeakable.core.components.menu.AbstractMenuPage
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.db.Graph
import de.nogaemer.unspeakable.db.UnspeakableCard
import de.nogaemer.unspeakable.db.UnspeakableCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class CardComponent(
    val ctx: ComponentContext,
    onBack: () -> Unit,
    protected open val category: UnspeakableCategory? = null,
) : AbstractMenuPage(ctx, onBack){

    open fun saveCard(){
        ctx.coroutineScope().launch {
            Graph.dao.saveCard(card.toCardDto())
        }.invokeOnCompletion {
            onBack()
        }
    }

    open fun deleteCard(goToOverview: () -> Unit = {},){
        ctx.coroutineScope().launch {
            Graph.dao.deleteById(card.id)
        }.invokeOnCompletion {
            goToOverview()
        }
    }

    abstract var card: UnspeakableCard

    val scope = ctx.coroutineScope()
    val categories: StateFlow<List<UnspeakableCategory>> = flow {
        emit(Graph.categoriesDao.getAllCategories().map { it.toUnspeakableCategory() })
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )
}

class NewCardComponent(
    ctx: ComponentContext,
    onBack: () -> Unit,
    override val category: UnspeakableCategory? = null,
) :  CardComponent(ctx, onBack) {
    override val titleKey = { s: Strings -> "New Card" }
    override val icon = Lucide.Plus

    override var card: UnspeakableCard = UnspeakableCard(
        id = 0,
        word = "",
        forbiddenWords = listOf("", "", "", "", ""),
        category = category?.id ?: "",
        language = Graph.settings.appSettings.locales.lang,
    )
}

class EditCardComponent(
    ctx: ComponentContext,
    onBack: () -> Unit,
    override var card: UnspeakableCard
) : CardComponent(ctx, onBack) {
    override val titleKey = { s: Strings -> "Edit Card" }
    override val icon = Lucide.Plus
}