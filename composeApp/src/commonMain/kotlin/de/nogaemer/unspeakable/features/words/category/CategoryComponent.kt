package de.nogaemer.unspeakable.features.words.category

import androidx.compose.ui.graphics.vector.ImageVector
import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import de.nogaemer.unspeakable.core.components.menu.AbstractMenuPage
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.core.util.icon.byString
import de.nogaemer.unspeakable.core.util.icon.iconCategoryMap
import de.nogaemer.unspeakable.db.Graph
import de.nogaemer.unspeakable.db.UnspeakableCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

abstract class CategoryComponent(
    val ctx: ComponentContext,
    onBack: () -> Unit,
): AbstractMenuPage(ctx, onBack) {
    override val titleKey = { s: Strings -> "New Category" }
    override val icon = Lucide.Plus

    abstract var category: UnspeakableCategory

    val allIcons: StateFlow<List<Pair<String, ImageVector>>> = flow {
        emit(iconCategoryMap.keys.map { name -> name to Lucide.byString(name) })
    }
        .flowOn(Dispatchers.Default)
        .stateIn(ctx.coroutineScope(), SharingStarted.Eagerly, emptyList())


    open fun saveCategory(){
        ctx.coroutineScope().launch {
            Graph.categoriesDao.saveCategory(category.toCategoryDto())
        }.invokeOnCompletion {
            onBack()
        }
    }

    open fun deleteCategory(
        goToOverview: () -> Unit = {},
    ){
        ctx.coroutineScope().launch {
            Logger.d("Deleting category ${category.id}")
            Graph.dao.deleteByCategory(category.id)
            Graph.categoriesDao.deleteById(category.id)
            Logger.d("Deleted category ${category.id}")
        }.invokeOnCompletion {
            goToOverview()
        }
    }
}

class NewCategoryComponent(
    ctx: ComponentContext,
    onBack: () -> Unit,
) :  CategoryComponent(ctx, onBack) {
    override val titleKey = { s: Strings -> "New Category" }
    override val icon = Lucide.Plus

    override var category: UnspeakableCategory = UnspeakableCategory(
        "",
        "",
        "",
    )

    @OptIn(ExperimentalUuidApi::class)
    override fun saveCategory() {
        category = category.copy(id = Uuid.random().toString())
        super.saveCategory()
    }
}

class EditCategoryComponent(
    ctx: ComponentContext,
    onBack: () -> Unit,
    override var category: UnspeakableCategory,
) : CategoryComponent(ctx, onBack) {
    override val titleKey = { s: Strings -> "Edit Category" }
    override val icon = Lucide.Plus
}