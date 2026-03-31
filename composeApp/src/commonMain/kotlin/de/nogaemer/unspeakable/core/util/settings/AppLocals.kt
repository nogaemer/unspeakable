package de.nogaemer.unspeakable.core.util.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

/** Defines user-selectable theme behavior modes. */
enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

/** Holds persisted user preferences that drive language and theming. */
data class AppSettings(
    val locales: Locales = Locales.EN,

    val hue: Float = 210f,
    val seedColor: Color = Color(0xFFA9E555),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColor: Boolean = true,
    val isAmoled: Boolean = false,
)

val AppSettings.isDark: Boolean
    @Composable
    @ReadOnlyComposable
    get() = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

/** Manages settings persistence and exposes reactive in-memory app settings. */
class AppSettingsController() {
    private val settings = Settings()

    var appSettings by mutableStateOf(
        AppSettings(
            locales     = Locales.from(settings.getString("language_tag", Locales.EN.lang)) ?: Locales.EN,

            themeMode   = ThemeMode.valueOf(settings.getString("theme_mode", ThemeMode.SYSTEM.name)),
            seedColor   = Color(settings.getLong("seed_color", 0xFF55C8E5).toInt()),
            hue         = settings.getFloat("app_hue", 210f),
            useDynamicColor = settings.getBoolean("dynamic_color", true),
            isAmoled = settings.getBoolean("amoled", false)
        )
    )
        private set


    fun setLanguage(tag: Locales) {
        settings["language_tag"] = tag.lang
        appSettings = appSettings.copy(locales = tag)
    }

    fun setThemeMode(mode: ThemeMode) {
        settings["theme_mode"] = mode.name
        appSettings = appSettings.copy(themeMode = mode)
    }

    fun setHue(hue: Float) {
        settings["app_hue"] = hue
        val seed = Color.hsv(hue, 1f, 1f)
        settings["seed_color"] = seed.toArgb().toLong()
        appSettings = appSettings.copy(hue = hue, seedColor = seed)
    }

    fun setSeedColor(color: Color) {
        settings["seed_color"] = color.toArgb().toLong()
        appSettings = appSettings.copy(seedColor = color)
    }

    fun setDynamicColor(dynamicColor: Boolean) {
        settings["dynamic_color"] = dynamicColor
        appSettings = appSettings.copy(useDynamicColor = dynamicColor)
    }

    fun setAmoled(amoled: Boolean) {
        settings["amoled"] = amoled
        appSettings = appSettings.copy(isAmoled = amoled)
    }
}

/** Provides `AppSettingsController` to composables via CompositionLocal. */
val LocalAppSettings = compositionLocalOf<AppSettingsController> {
    error("No AppSettingsController provided")
}
