package de.nogaemer.unspeakable.core.components.menu

import com.arkivanov.decompose.ComponentContext

/**
 * A generic overview component for any menu level that just needs
 * a grouped list of tappable rows. No feature-specific logic.
 *
 * If your overview needs custom logic (e.g. SettingsOverviewComponent
 * that shows the current language), extend this or write your own.
 */
class SimpleOverviewComponent<Config : Any>(
    ctx: ComponentContext,
    private val onNavigate: (Config) -> Unit,
    val goBack: () -> Unit,
    val items: List<List<OverviewItem<Config>>>,
) : ComponentContext by ctx {

    fun navigateTo(config: Config) = onNavigate(config)
}