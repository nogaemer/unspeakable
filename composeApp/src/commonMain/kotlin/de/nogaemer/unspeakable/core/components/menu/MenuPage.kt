package de.nogaemer.unspeakable.core.components.menu

import androidx.compose.ui.graphics.vector.ImageVector
import de.nogaemer.unspeakable.core.i18n.Strings

/**
 * Defines page metadata and back behavior for nested menu flows.
 */
interface MenuPage {
    val titleKey: (Strings) -> String
    val descriptionKey: ((Strings) -> String)? get() = null
    val icon: ImageVector
    val onBack: () -> Unit
}