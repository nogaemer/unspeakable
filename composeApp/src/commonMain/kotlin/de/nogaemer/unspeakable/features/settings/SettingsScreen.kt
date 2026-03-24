package de.nogaemer.unspeakable.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.lyricist.strings
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatableV2
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.core.util.settings.LocalAppSettings
import de.nogaemer.unspeakable.core.util.settings.isDark
import de.nogaemer.unspeakable.features.settings.pages.AboutScreen
import de.nogaemer.unspeakable.features.settings.pages.LanguageScreen
import de.nogaemer.unspeakable.features.settings.pages.PersonalizationScreen
import de.nogaemer.unspeakable.features.settings.pages.SettingsOverviewScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalDecomposeApi::class)
@Composable
fun SettingsScreen(component: SettingsComponent) {
    Children(
        stack = component.stack,
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            onBack = component::goBack,
            selector = { backEvent, _, _ ->
                androidPredictiveBackAnimatableV2(
                    backEvent
                )
            },
            fallbackAnimation = stackAnimation(slide() + fade()),
        )
    ) { child ->
        when (val instance = child.instance) {
            is DefaultSettingsComponent.SettingsChild.Overview -> DefaultTopAppBar(
                title = "Settings",
                onBack = component::goBack,
                navigationIcon = false,
            ) { SettingsOverviewScreen(instance.component) }

            is DefaultSettingsComponent.SettingsChild.Personalization -> DefaultTopAppBar(
                instance.component,
                component::goBack
            ) { PersonalizationScreen(it) }

            is DefaultSettingsComponent.SettingsChild.Language -> DefaultTopAppBar(
                instance.component,
                component::goBack
            ) { LanguageScreen(it) }

            is DefaultSettingsComponent.SettingsChild.About -> DefaultTopAppBar(
                instance.component,
                component::goBack
            ) { AboutScreen(it) }
        }
    }
}

@Composable
fun <C : SettingsPage> DefaultTopAppBar(
    component: C,
    onBack: () -> Unit,
    navigationIcon: Boolean = true,
    content: @Composable ColumnScope.(C) -> Unit,
) {
    val s = strings
    DefaultTopAppBar(
        title = component.titleKey(s),
        onBack = onBack
    ) {
        content(component)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
    title: String,
    onBack: () -> Unit,
    navigationIcon: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val settings = LocalAppSettings.current.appSettings
    Scaffold(
        topBar = {
            key(settings.isDark.toString() + settings.isAmoled.toString()){
                TopAppBar(
                    title = {
                        Text(
                            title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        if (navigationIcon) TooltipBox(
                            positionProvider =
                                TooltipDefaults.rememberTooltipPositionProvider(
                                    TooltipAnchorPosition.Above
                                ),
                            tooltip = { PlainTooltip { Text("Back") } },
                            state = rememberTooltipState(),
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)).zIndex(1f),
                            ) {
                                Icon(
                                    imageVector = Lucide.ArrowLeft,
                                    contentDescription = strings.common.back,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor        = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        titleContentColor      = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    )

                )

            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}
