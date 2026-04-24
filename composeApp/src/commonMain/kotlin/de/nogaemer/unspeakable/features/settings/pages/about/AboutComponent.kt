package de.nogaemer.unspeakable.features.settings.pages.about

import com.arkivanov.decompose.ComponentContext
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import de.nogaemer.unspeakable.core.components.menu.AbstractMenuPage
import de.nogaemer.unspeakable.core.i18n.Strings
import de.nogaemer.unspeakable.core.util.AppInfo

data class OpenSourceLib(
    val name: String,
    val author: String,
    val license: String,
    val url: String,
)


class AboutComponent(
    ctx: ComponentContext,
    onBack: () -> Unit,
) : AbstractMenuPage(ctx, onBack) {

    override val titleKey: (Strings) -> String = { it.settings.aboutStrings.title }
    override val descriptionKey: ((Strings) -> String)? = null
    override val icon = Lucide.Info

    val versionName = AppInfo.versionName
    val versionCode = AppInfo.versionCode

    private val openSourceLibraries = listOf(
        // ── JetBrains ──────────────────────────────────────────────────────────
        OpenSourceLib("Compose Multiplatform",      "JetBrains",                  "Apache 2.0", "https://github.com/JetBrains/compose-multiplatform"),
        OpenSourceLib("Kotlinx Serialization",      "JetBrains",                  "Apache 2.0", "https://github.com/Kotlin/kotlinx.serialization"),
        OpenSourceLib("Kotlinx Coroutines",         "JetBrains",                  "Apache 2.0", "https://github.com/Kotlin/kotlinx.coroutines"),
        OpenSourceLib("Ktor",                       "JetBrains",                  "Apache 2.0", "https://github.com/ktorio/ktor"),

        // ── Google / AndroidX ─────────────────────────────────────────────────
        OpenSourceLib("Material 3",                 "Google",                     "Apache 2.0", "https://m3.material.io"),
        OpenSourceLib("AndroidX Room",              "Google",                     "Apache 2.0", "https://developer.android.com/jetpack/androidx/releases/room"),
        OpenSourceLib("AndroidX Lifecycle",         "Google",                     "Apache 2.0", "https://developer.android.com/jetpack/androidx/releases/lifecycle"),

        // ── Arkadii Ivanov ────────────────────────────────────────────────────
        OpenSourceLib("Decompose",                  "Arkadii Ivanov",             "Apache 2.0", "https://github.com/arkivanov/Decompose"),

        // ── Lucide ────────────────────────────────────────────────────────────
        OpenSourceLib("Lucide Icons",               "Lucide Contributors",        "ISC",        "https://lucide.dev"),
        OpenSourceLib("compose-icons (lucide-cmp)", "Composable Horizons",        "ISC",        "https://github.com/composablehorizons/compose-icons"),

        // ── MIT licensed ──────────────────────────────────────────────────────
        OpenSourceLib("MaterialKolor",              "Jordon Boyd",                "MIT",        "https://github.com/jordond/materialkolor"),
        OpenSourceLib("Lyricist",                   "Adriel Café",                "MIT",        "https://github.com/adrielcafe/lyricist"),
        OpenSourceLib("QR Kit",                     "ChainTech Network",          "MIT",        "https://github.com/ChainTechNetwork/QRKitComposeMultiplatform"),
        OpenSourceLib("colormath",                  "AJ Alt",                     "MIT",        "https://github.com/ajalt/colormath"),

        // ── Apache 2.0 ────────────────────────────────────────────────────────
        OpenSourceLib("Multiplatform Settings",     "Russell Wolf",               "Apache 2.0", "https://github.com/russhwolf/multiplatform-settings"),
        OpenSourceLib("Kermit",                     "Touchlab",                   "Apache 2.0", "https://github.com/touchlab/Kermit"),
        OpenSourceLib("SQLDelight",                 "Cash App",                   "Apache 2.0", "https://github.com/cashapp/sqldelight"),
    )

    val libraries: List<OpenSourceLib>
        get() = openSourceLibraries
}


