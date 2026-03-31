package de.nogaemer.unspeakable.core.components.menu

import com.arkivanov.decompose.ComponentContext

/**
 * Base class for any page that lives inside a [DefaultMenuComponent] stack.
 * Extend this instead of writing an interface + default impl pair.
 *
 * Usage:
 *   class DefaultMyPage(ctx: ComponentContext, onBack: () -> Unit) :
 *       AbstractMenuPage(ctx, onBack) {
 *   }
 */
/**
 * Supplies shared menu-page wiring while delegating ComponentContext behavior.
 */
abstract class AbstractMenuPage(
    ctx: ComponentContext,
    override val onBack: () -> Unit,
) : MenuPage, ComponentContext by ctx