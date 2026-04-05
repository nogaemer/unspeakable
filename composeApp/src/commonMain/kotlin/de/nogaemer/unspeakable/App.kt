package de.nogaemer.unspeakable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.rememberStrings
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatableV2
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import de.nogaemer.unspeakable.core.util.SystemBarAppearance
import de.nogaemer.unspeakable.core.util.settings.AppSettingsController
import de.nogaemer.unspeakable.core.util.settings.LocalAppSettings
import de.nogaemer.unspeakable.core.util.settings.isDark
import de.nogaemer.unspeakable.db.Graph
import de.nogaemer.unspeakable.db.getDatabaseBuilder
import de.nogaemer.unspeakable.db.getRoomDatabase
import de.nogaemer.unspeakable.db.isDatabaseFileCopied
import de.nogaemer.unspeakable.db.writeDatabaseFile
import de.nogaemer.unspeakable.features.game.GameScreen
import de.nogaemer.unspeakable.features.game_setup.SetupScreen
import de.nogaemer.unspeakable.features.main.MainScreen
import de.nogaemer.unspeakable.navigation.RootComponent
import de.nogaemer.unspeakable.theme.rememberWallpaperSeedColor
import unspeakable.composeapp.generated.resources.Res


@OptIn(
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class, ExperimentalDecomposeApi::class
)
@Composable
fun App(root: RootComponent) {
    Logger.setMinSeverity(Severity.Verbose)
    Logger.setTag("Unspeakable")

    val controller = remember { AppSettingsController() }


    //DB init
    LaunchedEffect(Unit) {
        val builder = getDatabaseBuilder()
        if (!isDatabaseFileCopied("taboo.db")) {
            val dbBytes = Res.readBytes("files/taboo.db")
            writeDatabaseFile("taboo.db", dbBytes)
        }

        // Build the DB and save it to our global Graph!
        val db = getRoomDatabase(builder)
        Graph.database = db
        Graph.settings = controller
    }

    //Locals
    val lyricist = rememberStrings(
        defaultLanguageTag = "en",
        currentLanguageTag = controller.appSettings.locales.lang
    )

    val wallpaperSeedColor =
        rememberWallpaperSeedColor(controller.appSettings.isDark) ?: Color(0xFF558FE5)
    val seedColor =
        if (controller.appSettings.useDynamicColor) wallpaperSeedColor else controller.appSettings.seedColor

    // ######################## Ui ##########################

    CompositionLocalProvider(LocalAppSettings provides controller) {
        ProvideStrings(lyricist) {
            AppTheme(
                seedColor = seedColor,
                darkTheme = controller.appSettings.isDark,
                isAmoled = controller.appSettings.isAmoled,
                paletteStyle = controller.appSettings.paletteStyle,
            ) {
                AppContent(root)
            }
        }
    }

}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun AppContent(root: RootComponent) {
    Surface {
        Children(
            stack = root.stack,
            animation = predictiveBackAnimation(
                backHandler = root.backHandler,
                onBack = root::goBack,
                selector = { backEvent, _, _ ->
                    androidPredictiveBackAnimatableV2(
                        backEvent
                    )
                },
                fallbackAnimation = stackAnimation(slide() + fade()),
            )
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.Main -> MainScreen(instance.component)
                is RootComponent.Child.Setup -> SetupScreen(instance.component, onBack = root::goBack)
                is RootComponent.Child.Game -> GameScreen(
                    component = instance.component,
                    onBack = root::goBack,
                    onGoHome = root::goHome,
                )
            }
        }
    }

}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTheme(
    seedColor: Color = Color(0xFFA9E555),
    darkTheme: Boolean = isSystemInDarkTheme(),
    isAmoled: Boolean = false,
    paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
    content: @Composable () -> Unit
) {
    SystemBarAppearance(darkTheme)

    // This magical function generates all 30+ Material 3 color roles!
    val colorScheme = rememberDynamicColorScheme(
        seedColor = seedColor,
        isDark = darkTheme,
        isAmoled = isAmoled,
        style = paletteStyle
    )


    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}
