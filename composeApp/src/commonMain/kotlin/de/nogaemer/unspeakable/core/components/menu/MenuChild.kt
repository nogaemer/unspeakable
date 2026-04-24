package de.nogaemer.unspeakable.core.components.menu

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

/**
 * The three child types a menu stack can contain:
 *  - [Overview]: custom top-level screen (component type is defined by the caller)
 *  - [Page]: standard [MenuPage] paired with a content lambda
 *  - [SubMenu]: nested [MenuPage] branch rendered as a submenu container
 */

sealed class MenuChild<O> {
    /**
     * Represents the root overview node of a menu stack.
     */
    data class Overview<O>(
        val component: O,
        val isTitleBarTransparent: Boolean = false,
        val actions: @Composable RowScope.() -> Unit = {}
    ) : MenuChild<O>()

    /**
     * Represents a standard menu page and its content renderer.
     */
    data class Page<O>(
        val component: MenuPage,
        val actions: @Composable RowScope.() -> Unit = {},
        val isTitleBarTransparent: Boolean = false,
        val content: @Composable (MenuPage) -> Unit,
    ) : MenuChild<O>()

    /**
     * Represents a nested submenu branch and its renderer.
     */
    data class SubMenu<O>(
        val component: MenuPage,
        val content: @Composable (MenuPage) -> Unit,
    ) : MenuChild<O>()
}