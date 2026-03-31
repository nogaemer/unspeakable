package de.nogaemer.unspeakable.features.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import cafe.adriel.lyricist.strings
import com.arkivanov.decompose.ExperimentalDecomposeApi
import de.nogaemer.unspeakable.core.components.menu.NestedMenuScreen
import de.nogaemer.unspeakable.features.settings.pages.SettingsOverviewScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalDecomposeApi::class)
@Composable
fun SettingsScreen(component: DefaultSettingsComponent) {

    val s = strings
    NestedMenuScreen(
        component = component,
        rootTitle = s.settings.title,
        overviewContent = { SettingsOverviewScreen(it) }
    )
}