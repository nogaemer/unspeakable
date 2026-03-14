package de.nogaemer.unspeakable.core.util.settings

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set


data class AppSettings(
    val seedColor: Color = Color(0xFFA9E555),
    val isDark: Boolean = false,
    val locales: Locales = Locales.EN
)

class AppSettingsController() {
    private val settings = Settings()

    var appSettings by mutableStateOf(
        AppSettings(
            locales     = Locales.from(settings.getString("language_tag", Locales.EN.lang)) ?: Locales.EN,
            isDark      = settings.getBoolean("is_dark", false),
            seedColor   = Color(settings.getLong("seed_color", 0xFF55C8E5))
        )
    )
        private set


    fun setLanguage(tag: Locales) {
        settings["language_tag"] = tag.lang
        appSettings = appSettings.copy(locales = tag)
    }

    fun setDark(dark: Boolean) {
        settings["is_dark"] = dark
        appSettings = appSettings.copy(isDark = dark)
    }

    fun setSeedColor(color: Color) {
        settings["seed_color"] = color.value.toLong()
        appSettings = appSettings.copy(seedColor = color)
    }
}

val LocalAppSettings = compositionLocalOf<AppSettingsController> {
    error("No AppSettingsController provided")
}
