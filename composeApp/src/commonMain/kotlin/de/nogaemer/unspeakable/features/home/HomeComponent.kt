package de.nogaemer.unspeakable.features.home

import com.arkivanov.decompose.ComponentContext

interface HomeComponent {
    fun onHostClicked()
    fun onJoinClicked()
    fun onLocalClicked()
}

class DefaultHomeComponent(
    componentContext: ComponentContext,
    private val onHost:  () -> Unit,
    private val onJoin:  () -> Unit,
    private val onLocal: () -> Unit
) : HomeComponent, ComponentContext by componentContext {

    override fun onHostClicked()  = onHost()
    override fun onJoinClicked()  = onJoin()
    override fun onLocalClicked() = onLocal()
}