package de.nogaemer.unspeakable.features.settings

import com.arkivanov.decompose.ComponentContext

interface SettingsComponent {

}

class DefaultSettingsComponent(
    componentContext: ComponentContext,
) : SettingsComponent, ComponentContext by componentContext {

}