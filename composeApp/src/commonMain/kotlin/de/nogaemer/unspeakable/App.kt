package de.nogaemer.unspeakable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.materialkolor.rememberDynamicColorScheme
import de.nogaemer.unspeakable.db.Graph
import de.nogaemer.unspeakable.db.UnspeakableCard
import de.nogaemer.unspeakable.db.getDatabaseBuilder
import de.nogaemer.unspeakable.db.getRoomDatabase
import de.nogaemer.unspeakable.db.isDatabaseFileCopied
import de.nogaemer.unspeakable.db.writeDatabaseFile
import de.nogaemer.unspeakable.screens.GameScreen
import kotlinx.coroutines.launch
import unspeakable.composeapp.generated.resources.Res


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {

    //Database setup
    val scope = rememberCoroutineScope()

    //DB init
    LaunchedEffect(Unit) {
        scope.launch {
            val builder = getDatabaseBuilder()
            if (!isDatabaseFileCopied("taboo.db")) {
                val dbBytes = Res.readBytes("files/taboo.db")
                writeDatabaseFile("taboo.db", dbBytes)
            }

            // Build the DB and save it to our global Graph!
            val db = getRoomDatabase(builder)
            Graph.database = db
        }
    }

    AppTheme(
        Color.Blue,
        true
    ) {
        GameScreen()
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTheme(
    seedColor: Color = Color(0xFFE28743),
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // This magical function generates all 30+ Material 3 color roles!
    val colorScheme = rememberDynamicColorScheme(
        seedColor = seedColor,
        isDark = darkTheme,
        isAmoled = false // Set to true if you want pitch-black dark mode
    )

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        content = content
    )
}
