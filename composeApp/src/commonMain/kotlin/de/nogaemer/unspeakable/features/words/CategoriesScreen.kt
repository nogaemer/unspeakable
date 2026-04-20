package de.nogaemer.unspeakable.features.words

import androidx.compose.runtime.Composable
import cafe.adriel.lyricist.strings
import de.nogaemer.unspeakable.core.components.menu.NestedMenuScreen

@Composable
fun CategoriesScreen(component: CategoryComponent) {
    val s = strings
    NestedMenuScreen(
        component = component,
        rootTitle = s.categories.title,
        overviewContent = { CategoryOverviewScreen(component) }
    )
}