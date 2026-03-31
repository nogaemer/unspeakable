package de.nogaemer.unspeakable.core.components.menu

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.KSerializer

/**
 * Generic navigation host for any stacked-menu feature.
 *
 * [Config]    — your @Serializable sealed class of destinations
 * [Overview]  — the component type for the top-level overview screen
 *
 * Usage:
 *   class DefaultSettingsComponent(ctx: ComponentContext) :
 *       DefaultMenuComponent<SettingsConfig, SettingsOverviewComponent>(
 *           ctx            = ctx,
 *           serializer     = SettingsConfig.serializer(),
 *           initialConfig  = SettingsConfig.Overview,
 *           childFactory   = { config, childCtx, nav -> ... }
 *       )
 */
abstract class DefaultMenuComponent<Config : Any, Overview>(
    ctx: ComponentContext,
    serializer: KSerializer<Config>,
    private val initialConfig: Config,
    private val childFactory: (
        config: Config,
        ctx: ComponentContext,
        push: (Config) -> Unit,
        pop: () -> Unit,
    ) -> MenuChild<Overview>,
) : ComponentContext by ctx {

    protected val navigation = StackNavigation<Config>()

    @OptIn(DelicateDecomposeApi::class)
    val stack: Value<ChildStack<Config, MenuChild<Overview>>> = childStack(
        source = navigation,
        serializer = serializer,
        initialConfiguration = initialConfig,
        handleBackButton = true,
        childFactory = { config, childCtx ->
            childFactory(config, childCtx, { navigation.push(it) }, { navigation.pop() })
        }
    )

    fun goBack() = navigation.pop()
    fun resetToRoot() = navigation.replaceAll(initialConfig)
}