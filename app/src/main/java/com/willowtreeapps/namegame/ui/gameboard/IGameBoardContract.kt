package com.willowtreeapps.namegame.ui.gameboard

import android.support.annotation.StringRes
import com.willowtreeapps.namegame.network.api.model.Person
import com.willowtreeapps.namegame.util.IBasePresenter
import com.willowtreeapps.namegame.util.IBaseView

interface IGameBoardContract {

    interface View : IBaseView {
        fun showPeople(people: List<Person>, fullName: String)

        fun showIncorrectAnswer(correctPersonIndex: Int)

        fun showCorrectAnswer(correctPersonIndex: Int)

        fun updateCorrectTotal(correctTotal: Int)

        fun updateIncorrectTotal(incorrectTotal: Int)

        fun showPeopleLoadError(@StringRes message: Int)
    }

    interface Presenter : IBasePresenter {
        fun onPictureClicked(person: Person)

        fun prepAndDisplayBoard()
    }
}