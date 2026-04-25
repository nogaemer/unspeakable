package de.nogaemer.unspeakable.features.words

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.essenty.lifecycle.doOnStart
import com.composables.icons.lucide.Import
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.core.components.menu.AppBarButton
import de.nogaemer.unspeakable.core.components.menu.DefaultMenuComponent
import de.nogaemer.unspeakable.core.components.menu.MenuChild
import de.nogaemer.unspeakable.core.components.menu.MenuChild.Overview
import de.nogaemer.unspeakable.core.components.menu.MenuChild.SubMenu
import de.nogaemer.unspeakable.core.components.menu.NestedMenuScreen
import de.nogaemer.unspeakable.db.Graph
import de.nogaemer.unspeakable.db.UnspeakableCategory
import de.nogaemer.unspeakable.features.settings.SettingsOverviewComponent
import de.nogaemer.unspeakable.features.words.category.CategoryComponentScreen
import de.nogaemer.unspeakable.features.words.category.NewCategoryComponent
import de.nogaemer.unspeakable.features.words.jsonimport.JsonImportComponent
import de.nogaemer.unspeakable.features.words.jsonimport.JsonImportScreen
import de.nogaemer.unspeakable.features.words.words.WordsComponent
import de.nogaemer.unspeakable.features.words.words.WordsOverviewScreen
import de.nogaemer.unspeakable.features.words.words.word.CardComponentScreen
import de.nogaemer.unspeakable.features.words.words.word.NewCardComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
sealed class CategoryConfig {
    @Serializable
    data object Overview : CategoryConfig()

    @Serializable
    data class Words(val category: UnspeakableCategory) : CategoryConfig()

    @Serializable
    data object NewCategory : CategoryConfig()

    @Serializable
    data object NewWord : CategoryConfig()

    @Serializable
    data object JsonImport : CategoryConfig()
}

class CategoryComponent(
    ctx: ComponentContext
) : DefaultMenuComponent<CategoryConfig, SettingsOverviewComponent<CategoryConfig>>(
    ctx = ctx,
    serializer = CategoryConfig.serializer(),
    initialConfig = CategoryConfig.Overview,
    childFactory = { config, childCtx, push, pop, _ ->
        when (config) {
            is CategoryConfig.Overview -> Overview(
                SettingsOverviewComponent(
                    ctx = childCtx,
                    goBack = pop,
                    onNavigate = push,
                    items = listOf()
                ),
                hasBottomPadding = false
            ) {
                AppBarButton({ push(CategoryConfig.JsonImport) }, Lucide.Import)
            }

            is CategoryConfig.Words -> {
                val category = config.category
                val c = WordsComponent(
                    ctx = childCtx,
                    onBack = pop,
                    category
                )

                SubMenu(c) {
                    NestedMenuScreen(
                        component = c,
                        showBackOnOverview = true,
                        onRootBack = c.onBack,
                        overviewContent = { WordsOverviewScreen(c) }
                    )
                }
            }

            is CategoryConfig.NewCategory -> {
                val c = NewCategoryComponent(childCtx, pop)
                MenuChild.Page(c) {
                    CategoryComponentScreen(c)
                }
            }

            is CategoryConfig.NewWord -> {
                val c = NewCardComponent(childCtx, pop)
                MenuChild.Page(c) {
                    CardComponentScreen(c)
                }
            }

            is CategoryConfig.JsonImport -> {
                val c = JsonImportComponent(childCtx, pop)
                MenuChild.Page(c) {
                    JsonImportScreen(c)
                }
            }
        }
    }
) {

    private val scope = coroutineScope()

    private val _categories = MutableStateFlow<List<UnspeakableCategory>>(emptyList())
    val categories: StateFlow<List<UnspeakableCategory>> = _categories

    init {
        lifecycle.doOnStart {
            refreshCategories()
        }
    }

    fun refreshCategories() {
        scope.launch {
            _categories.value = Graph.categoriesDao.getAllCategories()
                .map { it.toUnspeakableCategory() }
        }
    }

    override fun goBack() {
        refreshCategories()
        super.goBack()
    }

    @OptIn(DelicateDecomposeApi::class)
    fun onCategoryClick(category: UnspeakableCategory) {
        navigation.push(CategoryConfig.Words(category))
    }
}