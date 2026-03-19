package de.nogaemer.unspeakable.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import de.nogaemer.unspeakable.features.game.DefaultGameComponent
import de.nogaemer.unspeakable.features.game_settings.DefaultSetupComponent
import de.nogaemer.unspeakable.features.game_settings.SetupComponent
import de.nogaemer.unspeakable.features.main.DefaultMainComponent
import de.nogaemer.unspeakable.features.main.MainComponent
import de.nogaemer.unspeakable.navigation.RootComponent.Child.Game
import de.nogaemer.unspeakable.navigation.RootComponent.Child.Main

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {


    private val navigation = StackNavigation<ScreenConfig>()


    val stack: Value<ChildStack<ScreenConfig, Child>> = childStack(
        source = navigation,
        serializer = ScreenConfig.serializer(),
        initialConfiguration = ScreenConfig.Main,
        handleBackButton = true,
        childFactory = ::createChild
    )

    @OptIn(DelicateDecomposeApi::class)
    private fun createChild(config: ScreenConfig, context: ComponentContext): Child {
        return when (config) {
            ScreenConfig.Main -> Main(
                DefaultMainComponent(
                    context,
                    onSelect = { networkMode ->
                        navigation.push(ScreenConfig.Setup(networkMode))
                    }
                )
            )

            is ScreenConfig.Setup -> Child.Setup(
                DefaultSetupComponent(context, config.networkMode) { gameConfig ->
                    navigation.push(ScreenConfig.Game(gameConfig))
                }
            )

            is ScreenConfig.Game -> Game(
                DefaultGameComponent(context, config.gameConfig)
            )
        }
    }

    @OptIn(DelicateDecomposeApi::class)
    fun navigateTo(config: ScreenConfig) {
        navigation.push(config)
    }

    fun goBack() {
        navigation.pop()
    }

    sealed class Child {
        data class Main(val component: MainComponent) : Child()
        data class Setup(val component: SetupComponent) : Child()
        data class Game(val component: DefaultGameComponent) : Child()
    }
}
