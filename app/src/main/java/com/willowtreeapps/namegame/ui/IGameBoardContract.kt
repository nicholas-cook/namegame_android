package com.willowtreeapps.namegame.ui

import android.support.annotation.StringRes
import com.willowtreeapps.namegame.network.api.model.Person
import com.willowtreeapps.namegame.util.IBasePresenter
import com.willowtreeapps.namegame.util.IBaseView

interface IGameBoardContract {

    interface View : IBaseView {
        fun showPeople(people: ArrayList<Person>)

        fun showIncorrectAnswer(view: android.view.View, person: Person)

        fun showCorrectAnswer(view: android.view.View, person: Person)

        fun showPeopleLoadError(@StringRes message: Int)
    }

    interface Presenter : IBasePresenter {
        fun onPictureClicked(view: android.view.View, person: Person)
    }
}