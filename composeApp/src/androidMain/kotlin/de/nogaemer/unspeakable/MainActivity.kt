package de.nogaemer.unspeakable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.arkivanov.decompose.retainedComponent
import com.russhwolf.settings.BuildConfig
import de.nogaemer.unspeakable.core.util.WakeLock
import de.nogaemer.unspeakable.db.AndroidAppContext
import de.nogaemer.unspeakable.navigation.RootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT,
            )
        )

        super.onCreate(savedInstanceState)
        WakeLock.init(this)

        // Configure global logger before creating app components.
        Logger.setMinSeverity(if (BuildConfig.DEBUG) Severity.Verbose else Severity.Info)
        Logger.setTag("Unspeakable")

        AndroidAppContext.application = this.application
        val root = retainedComponent { RootComponent(it) }

        setContent {
            App(root)
        }
    }
}

//@Preview
//@Composable
//fun AppAndroidPreview() {
//    val root = RootComponent(defaultComponentContext())
//    App(root)
//}