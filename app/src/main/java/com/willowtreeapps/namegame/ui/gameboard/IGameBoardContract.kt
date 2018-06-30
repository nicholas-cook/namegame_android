package com.willowtreeapps.namegame.ui.gameboard

import android.support.annotation.StringRes
import com.willowtreeapps.namegame.network.api.model.Person
import com.willowtreeapps.namegame.util.IBasePresenter
import com.willowtreeapps.namegame.util.IBaseView

interface IGameBoardContract {

    interface View : IBaseView {
        fun showPeople(people: ArrayList<Person>, fullName: String)

        fun showIncorrectAnswer(correctPerson: Person)

        fun showCorrectAnswer(correctPerson: Person)

        fun showPeopleLoadError(@StringRes message: Int)
    }

    interface Presenter : IBasePresenter {
        fun onPictureClicked(person: Person)

        fun setUpNewBoard()
    }
}