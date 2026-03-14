package de.nogaemer.unspeakable.features.settings.pages

import androidx.compose.ui.graphics.vector.ImageVector
import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Cloud
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Palette
import de.nogaemer.unspeakable.core.i18n.Strings
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

    private val personalization: PersonalizationComponent,
    private val language: LanguageComponent,
    private val about: AboutComponent,

    private val _onNavigate: (SettingsConfig) -> Unit
) : SettingsOverviewComponent, ComponentContext by ctx {

    override val pages = listOf(
        listOf(
            SettingsPageEntry(SettingsConfig.Language, language),
            SettingsPageEntry(SettingsConfig.Personalization, personalization),
            SettingsPageEntry(
                SettingsConfig.Language,
                TestComponent("Test #2", "Test description #2", Lucide.Bell)
            ),
            SettingsPageEntry(
                SettingsConfig.Language,
                TestComponent("Test #3", "Test description #3", Lucide.Cloud)
            ),
            SettingsPageEntry(
                SettingsConfig.Language,
                TestComponent("Test #4", "Test description #4", Lucide.Palette)

            ),
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

class TestComponent(
    val _title: String,
    val _description: String? = null,
    override val icon: ImageVector
) : SettingsPage {
    override val titleKey: ((Strings) -> String) = { _title }
    override val descriptionKey: ((Strings) -> String)? =
        _description?.let { description -> { description } }
}
