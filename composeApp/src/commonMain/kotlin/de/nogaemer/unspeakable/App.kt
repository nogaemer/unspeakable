package de.nogaemer.unspeakable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatableV2
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.materialkolor.rememberDynamicColorScheme
import de.nogaemer.unspeakable.db.Graph
import de.nogaemer.unspeakable.db.getDatabaseBuilder
import de.nogaemer.unspeakable.db.getRoomDatabase
import de.nogaemer.unspeakable.db.isDatabaseFileCopied
import de.nogaemer.unspeakable.db.writeDatabaseFile
import de.nogaemer.unspeakable.features.game.GameScreen
import de.nogaemer.unspeakable.features.game_settings.SetupScreen
import de.nogaemer.unspeakable.features.main.MainScreen
import de.nogaemer.unspeakable.navigation.RootComponent
import unspeakable.composeapp.generated.resources.Res


@OptIn(
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class, ExperimentalDecomposeApi::class
)
@Composable
fun App(root: RootComponent) {

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
    }

    // ######################## Ui ##########################

    AppTheme(
        Color.Blue,
        true
    ) {
        AppContent(root)
    }
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun AppContent(root: RootComponent) {
    AppTheme {
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
                    fallbackAnimation = stackAnimation(slide()),
                )
            ) { child ->
                when (val instance = child.instance) {
                    is RootComponent.Child.Main -> MainScreen(instance.component)
                    is RootComponent.Child.Setup -> SetupScreen(instance.component)
                    is RootComponent.Child.Game -> GameScreen(instance.component)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTheme(
    seedColor: Color = Color(0xFF55C8E5),
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // This magical function generates all 30+ Material 3 color roles!
    val colorScheme = rememberDynamicColorScheme(
        seedColor = seedColor,
        isDark = darkTheme,
        isAmoled = false
    )

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        content = content
    )
}
