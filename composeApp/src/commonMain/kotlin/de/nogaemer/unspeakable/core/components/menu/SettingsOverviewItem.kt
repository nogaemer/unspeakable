package de.nogaemer.unspeakable.core.components.menu

import androidx.compose.ui.graphics.vector.ImageVector
import de.nogaemer.unspeakable.core.i18n.Strings

/**
 * Describes one navigable row in an overview menu list.
 */
data class OverviewItem<C>(
    val config: C,
    val icon: ImageVector,
    val titleKey: (Strings) -> String,
    val descriptionKey: ((Strings) -> String)? = null,
)