package de.nogaemer.unspeakable.features.settings

import com.arkivanov.decompose.ComponentContext
import de.nogaemer.unspeakable.core.components.menu.OverviewItem

interface SettingsOverviewComponentInterface<Config : Any> : ComponentContext {
    val onNavigate: (Config) -> Unit
    val goBack: () -> Unit
    val items: List<List<OverviewItem<Config>>>
}

class SettingsOverviewComponent<Config : Any>(
    ctx: ComponentContext,
    override val onNavigate: (Config) -> Unit,
    override val goBack: () -> Unit,
    override val items: List<List<OverviewItem<Config>>>,
) : SettingsOverviewComponentInterface<Config>, ComponentContext by ctx {

    fun navigateTo(config: Config) = onNavigate(config)
}