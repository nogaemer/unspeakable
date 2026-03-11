package de.nogaemer.unspeakable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.retainedComponent
import de.nogaemer.unspeakable.db.AndroidAppContext
import de.nogaemer.unspeakable.navigation.RootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

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