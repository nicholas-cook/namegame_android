package com.willowtreeapps.namegame.ui.gameboard

import android.os.Bundle
import com.willowtreeapps.namegame.R
import com.willowtreeapps.namegame.core.ListRandomizer
import com.willowtreeapps.namegame.network.api.PeopleRepository
import com.willowtreeapps.namegame.network.api.model.Person
import com.willowtreeapps.namegame.util.IBaseView
import javax.inject.Inject

class GameBoardPresenter
@Inject constructor(
    private val peopleRepository: PeopleRepository,
    private val listRandomizer: ListRandomizer
) : IGameBoardContract.Presenter {

    private var view: IGameBoardContract.View? = null

    private val allPeople = ArrayList<Person>()
    private val currentPeople = ArrayList<Person>(MAX_PEOPLE)
    private var correctPerson: Person? = null

    private var correctTotal = 0
    private var incorrectTotal = 0

    private var stateRestored = false

    private val peopleListener: PeopleRepository.Listener = object : PeopleRepository.Listener {
        override fun onLoadFinished(people: MutableList<Person>) {
            allPeople.apply {
                clear()
                addAll(people)
            }
            if (!stateRestored) {
                stateRestored = true
                resetPeopleValues()
                prepAndDisplayBoard()
            }
        }

        override fun onError(error: Throwable) {
            view?.showPeopleLoadError(R.string.people_load_error)
        }
    }

    override fun attachView(view: IBaseView) {
        this.view = view as IGameBoardContract.View
    }

    override fun detachView() {
        view = null
    }

    override fun start() {
        peopleRepository.register(peopleListener)
    }

    override fun stop() {
        peopleRepository.unregister(peopleListener)
    }

    override fun saveState(outState: Bundle) {
        outState.apply {
            putParcelableArrayList(EXTRA_CURRENT_PEOPLE, currentPeople)
            putParcelable(EXTRA_CORRECT_PERSON, correctPerson)
            putInt(EXTRA_CORRECT_TOTAL, correctTotal)
            putInt(EXTRA_INCORRECT_TOTAL, incorrectTotal)
        }
    }

    override fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            currentPeople.apply {
                clear()
                addAll(it.getParcelableArrayList(EXTRA_CURRENT_PEOPLE))
            }
            correctPerson = it.getParcelable(EXTRA_CORRECT_PERSON)
            correctTotal = it.getInt(EXTRA_CORRECT_TOTAL)
            incorrectTotal = it.getInt(EXTRA_INCORRECT_TOTAL)
            if (!currentPeople.isEmpty()) {
                stateRestored = true
                prepAndDisplayBoard()
            }
        }
    }

    override fun onPictureClicked(person: Person) {
        correctPerson?.let {
            val correctPersonIndex = currentPeople.indexOf(it)
            if (person.id == correctPerson?.id) {
                correctTotal++
                view?.onCorrectAnswer(correctPersonIndex)
            } else {
                incorrectTotal++
                view?.onIncorrectAnswer(correctPersonIndex)
            }
            resetPeopleValues()
        }
    }

    private fun resetPeopleValues() {
        currentPeople.apply {
            clear()
            addAll(listRandomizer.pickN(allPeople, MAX_PEOPLE))
        }
        correctPerson = listRandomizer.pickOne(currentPeople)
    }

    override fun prepAndDisplayBoard() {
        view?.let {
            it.showPeople(currentPeople, getFullName())
            it.updateCorrectTotal(correctTotal)
            it.updateIncorrectTotal(incorrectTotal)
        }
    }

    private fun getFullName(): String {
        var fullName: String? = ""
        if (!correctPerson?.firstName.isNullOrBlank() && !correctPerson?.lastName.isNullOrBlank()) {
            fullName = correctPerson?.firstName?.plus(" ")?.plus(correctPerson?.lastName)
        } else if (!correctPerson?.firstName.isNullOrBlank()) {
            fullName = correctPerson?.firstName
        } else if (!correctPerson?.lastName.isNullOrBlank()) {
            fullName = correctPerson?.lastName
        }
        return fullName ?: ""
    }

    companion object {
        private const val EXTRA_CURRENT_PEOPLE = "com.willowtreeapps.namegame.ui.gameboard.EXTRA_CURRENT_PEOPLE"
        private const val EXTRA_CORRECT_PERSON = "com.willowtreeapps.namegame.ui.gameboard.EXTRA_CORRECT_PERSON"
        private const val EXTRA_CORRECT_TOTAL = "com.willowtreeapps.namegame.ui.gameboard.EXTRA_CORRECT_TOTAL"
        private const val EXTRA_INCORRECT_TOTAL = "com.willowtreeapps.namegame.ui.gameboard.EXTRA_INCORRECT_TOTAL"

        private const val MAX_PEOPLE = 5
    }
}