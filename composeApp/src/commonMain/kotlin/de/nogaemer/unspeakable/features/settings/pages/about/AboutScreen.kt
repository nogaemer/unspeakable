package de.nogaemer.unspeakable.features.settings.pages.about

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.strings
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.ChevronUp
import com.composables.icons.lucide.Code
import com.composables.icons.lucide.Github
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Tag
import com.composables.icons.lucide.User
import de.nogaemer.unspeakable.AppTheme
import de.nogaemer.unspeakable.core.components.AppIcon
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedColumn
import de.nogaemer.unspeakable.core.components.segmentedlist.SegmentedListItem


@Composable
fun AboutScreen(component: AboutComponent) {
    AboutContent(
        appVersion = component.versionName,
        appBuild = component.versionCode,
        libraries = component.libraries
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutContent(
    appVersion: String,
    appBuild: Int,
    libraries: List<OpenSourceLib>,
    modifier: Modifier = Modifier
) {
    val s = strings
    val about = s.settings.aboutStrings
    var licensesExpanded by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(top = 64.dp, bottom = 48.dp, start = 24.dp, end = 24.dp),
                ) {
                    // App icon in expressive asymmetric shape with tonal shadow
                    Box(
                        modifier = Modifier
                            .size(176.dp)
                            .clip(MaterialShapes.Cookie7Sided.toShape())
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        AppIcon(
                            modifier = Modifier.size(144.dp),
                        )
                    }

                    // App name
                    Text(
                        text = s.common.appName,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                    )

                    // Tagline
                    Text(
                        text = about.heroTagline,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f),
                        textAlign = TextAlign.Center,
                    )

                    // Version pill
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        ) {
                            Icon(
                                imageVector = Lucide.Tag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(13.dp),
                            )
                            Text(
                                text = about.versionLabel(appVersion, appBuild),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
        }

        // ── Developer ─────────────────────────────────────────────────────────
        item {
            SegmentedColumn(
                segmentTitle = about.developerSectionTitle,
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 24.dp, bottom = 4.dp),
            ) {
                SegmentedListItem(
                    modifier = Modifier.clickable { uriHandler.openUri("https://github.com/nogaemer") },
                    headlineContent   = { Text(about.developerName) },
                    supportingContent = { Text(about.developerRole) },
                    leadingContent    = { Icon(Lucide.User, contentDescription = null) },
                )
                SegmentedListItem(
                    modifier = Modifier.clickable { uriHandler.openUri("https://github.com/nogaemer/unspeakable") },
                    headlineContent   = { Text(about.sourceCodeTitle) },
                    supportingContent = { Text(about.sourceCodeSubtitle) },
                    leadingContent    = { Icon(Lucide.Github, contentDescription = null) },
                )
            }
        }

        // ── Open Source ───────────────────────────────────────────────────────
        item {
            SegmentedColumn(
                segmentTitle = about.openSourceSectionTitle,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            ) {
                // Expandable header row
                SegmentedListItem(
                    modifier = Modifier.clickable { licensesExpanded = !licensesExpanded },
                    headlineContent   = { Text(about.librariesTitle) },
                    supportingContent = {
                        Text(if (licensesExpanded) about.librariesCollapseHint else about.librariesCount(libraries.size))
                    },
                    leadingContent  = { Icon(Lucide.Code, contentDescription = null) },
                    trailingContent = {
                        Icon(
                            imageVector = if (licensesExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                            contentDescription = null,
                        )
                    },
                )

                AnimatedVisibility(
                    visible = licensesExpanded,
                    enter   = expandVertically(),
                    exit    = shrinkVertically(),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        libraries.forEach { lib ->
                            SegmentedListItem(
                                modifier = Modifier.clickable{
                                    if (lib.url.isNotBlank()) {
                                        uriHandler.openUri(lib.url)
                                    }
                                },
                                headlineContent   = { Text(lib.name) },
                                supportingContent = { Text(lib.author) },
                                leadingContent    = {
                                    // Indent to align under the list title
                                    Spacer(Modifier.width(4.dp))
                                },
                                trailingContent = {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                    ) {
                                        Text(
                                            text = lib.license,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }

        // ── Footer ────────────────────────────────────────────────────────────
        item {
            Text(
                text = about.footer,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
            )
        }
    }
}

@Preview
@Composable
private fun AboutScreenPreview() {
    AppTheme {
        AboutContent(
            appVersion = "1.2.3",
            appBuild = 42,
            libraries = listOf(
                OpenSourceLib("Compose Multiplatform", "JetBrains", "Apache 2.0", ""),
                OpenSourceLib("Material 3", "Google", "Apache 2.0", ""),
                OpenSourceLib("Decompose", "Arkadii Ivanov", "Apache 2.0", ""),
                OpenSourceLib("Lucide Icons", "Lucide Contributors", "ISC", "")
            )
        )
    }
}
