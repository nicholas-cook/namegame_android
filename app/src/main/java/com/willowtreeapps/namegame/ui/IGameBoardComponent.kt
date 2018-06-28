package com.willowtreeapps.namegame.ui

import com.willowtreeapps.namegame.core.INameGameComponent
import com.willowtreeapps.namegame.util.FragmentScoped
import dagger.Component

@FragmentScoped
@Component(dependencies = [(INameGameComponent::class)],
    modules = [(GameBoardPresenterModule::class)])
interface IGameBoardComponent {

    fun inject(gameBoardFragment: GameBoardFragment)
}