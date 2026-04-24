package de.nogaemer.unspeakable.core.components.menu

import androidx.compose.runtime.Composable
import cafe.adriel.lyricist.strings
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatableV2
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation

/**
 * Renders a menu stack and switches between overview/pages with back-aware animations.
 */
@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun <O> NestedMenuScreen(
    component: DefaultMenuComponent<*, O>,
    rootTitle: String? = null,
    showBackOnOverview: Boolean = false,
    onRootBack: (() -> Unit)? = null,
    overviewContent: @Composable ((O) -> Unit)? = null,
) {
    val s = strings
    Children(
        stack = component.stack,
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            onBack = component::goBack,
            selector = { e, _, _ -> androidPredictiveBackAnimatableV2(e) },
            fallbackAnimation = stackAnimation(slide() + fade()),
        )
    ) { child ->
        when (val instance = child.instance) {
            is MenuChild.Overview -> DefaultTopAppBar(
                title = rootTitle ?: (component as? MenuPage)?.titleKey?.invoke(s) ?: "",
                onBack = onRootBack ?: component::goBack,
                navigationIcon = rootTitle == null || showBackOnOverview,
                isTransparent = instance.isTitleBarTransparent,
                actions = instance.actions,
            ) {
                overviewContent?.invoke(instance.component)
                    ?: OverviewScreen(instance.component as SimpleOverviewComponent<*>)
            }

            is MenuChild.Page -> DefaultTopAppBar(
                title = instance.component.titleKey(s),
                onBack = instance.component.onBack,
                isTransparent = instance.isTitleBarTransparent,
                actions = instance.actions,
            ) {
                instance.content(instance.component)
            }

            is MenuChild.SubMenu -> {
                instance.content(instance.component)
            }
        }
    }
}