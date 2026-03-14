package de.nogaemer.unspeakable.features.settings.pages

import com.arkivanov.decompose.ComponentContext
import de.nogaemer.unspeakable.features.settings.SettingsConfig
import de.nogaemer.unspeakable.features.settings.SettingsPage


interface SettingsOverviewComponent {
    val pages: List<List<SettingsPageEntry>>
    fun onNavigate(config: SettingsConfig)
}


data class SettingsPageEntry(
    val config: SettingsConfig,
    val page: SettingsPage
)

class DefaultSettingsOverviewComponent(
    ctx: ComponentContext,
    private val language: LanguageComponent,
    private val about: AboutComponent,
    private val _onNavigate: (SettingsConfig) -> Unit
): SettingsOverviewComponent, ComponentContext by ctx {

    override val pages = listOf(
        listOf(
            SettingsPageEntry(SettingsConfig.Language, language),
            SettingsPageEntry(SettingsConfig.Language, language),
            SettingsPageEntry(SettingsConfig.Language, language),
            SettingsPageEntry(SettingsConfig.Language, language),
            SettingsPageEntry(SettingsConfig.Language, language)
        ),
        listOf(
            SettingsPageEntry(SettingsConfig.Language, language)
        ),
        listOf(
            SettingsPageEntry(SettingsConfig.Language, language),
            SettingsPageEntry(SettingsConfig.About, about)
        )
    )
    override fun onNavigate(config: SettingsConfig) = _onNavigate(config)

}