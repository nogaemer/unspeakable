package de.nogaemer.unspeakable.features.home

import com.arkivanov.decompose.ComponentContext
import de.nogaemer.unspeakable.features.game_setup.NetworkMode

interface HomeComponent {
    fun onHostClicked()
    fun onJoinClicked()
    fun onLocalClicked()
}

class DefaultHomeComponent(
    componentContext: ComponentContext,
    private val onSelect: (networkMode: NetworkMode) -> Unit,
) : HomeComponent, ComponentContext by componentContext {

    override fun onHostClicked() = onSelect(NetworkMode.HOST)
    override fun onJoinClicked() = onSelect(NetworkMode.CLIENT)
    override fun onLocalClicked() = onSelect(NetworkMode.LOCAL)
}