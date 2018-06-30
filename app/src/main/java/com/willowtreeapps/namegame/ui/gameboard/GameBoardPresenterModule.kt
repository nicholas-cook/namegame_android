package com.willowtreeapps.namegame.ui.gameboard

import dagger.Module
import dagger.Provides

@Module
class GameBoardPresenterModule(private val view: IGameBoardContract.View) {

    @Provides
    fun provideGameBoardContractView(): IGameBoardContract.View? = view
}