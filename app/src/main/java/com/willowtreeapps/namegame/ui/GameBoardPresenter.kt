package com.willowtreeapps.namegame.ui

import android.os.Bundle
import android.view.View
import com.willowtreeapps.namegame.R
import com.willowtreeapps.namegame.core.ListRandomizer
import com.willowtreeapps.namegame.network.api.ProfilesRepository
import com.willowtreeapps.namegame.network.api.model.Person
import com.willowtreeapps.namegame.util.IBaseView
import javax.inject.Inject

class GameBoardPresenter
@Inject constructor(
    private val profilesRepository: ProfilesRepository,
    private val listRandomizer: ListRandomizer
) : IGameBoardContract.Presenter {

    private var view: IGameBoardContract.View? = null

    private val allPeople = ArrayList<Person>()
    private val currentPeople = ArrayList<Person>()
    private val chosenPeople = ArrayList<Person>()
    private var correctPerson: Person? = null

    private var stateRestored = false

    private val peopleListener: ProfilesRepository.Listener = object : ProfilesRepository.Listener {
        override fun onLoadFinished(people: MutableList<Person>) {
            allPeople.apply {
                clear()
                addAll(people)
            }
            if (!stateRestored) {
                setUpNewBoard()
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

    override fun onPictureClicked(view: View, person: Person) {
        if (person.id == correctPerson?.id) {
            this.view?.showCorrectAnswer(view, person)
        } else {
            this.view?.showIncorrectAnswer(view, person)
        }
    }

    override fun start() {
        profilesRepository.register(peopleListener)
    }

    override fun stop() {
        profilesRepository.unregister(peopleListener)
    }

    override fun saveState(outState: Bundle) {
        outState.apply {
            putParcelableArrayList(EXTRA_CURRENT_PEOPLE, currentPeople)
            putParcelableArrayList(EXTRA_CHOSEN_PEOPLE, chosenPeople)
            putParcelable(EXTRA_CORRECT_PERSON, correctPerson)
        }
    }

    override fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            currentPeople.apply {
                clear()
                addAll(savedInstanceState.getParcelableArrayList(EXTRA_CURRENT_PEOPLE))
            }
            chosenPeople.apply {
                clear()
                addAll(savedInstanceState.getParcelableArrayList(EXTRA_CHOSEN_PEOPLE))
            }
            correctPerson = savedInstanceState.getParcelable(EXTRA_CORRECT_PERSON)
            if (!currentPeople.isEmpty()) {
                stateRestored = true
                showBoard()
            }
        }
    }

    private fun setUpNewBoard() {
        currentPeople.apply {
            clear()
            addAll(listRandomizer.pickN(allPeople, MAX_PEOPLE))
        }
        chosenPeople.clear()
        correctPerson = listRandomizer.pickOne(allPeople)
        showBoard()
    }

    private fun showBoard() {
        var fullName: String? = ""
        if (!correctPerson?.firstName.isNullOrBlank() && !correctPerson?.lastName.isNullOrBlank()) {
            fullName = correctPerson?.firstName?.plus(" ")?.plus(correctPerson?.lastName)
        } else if (!correctPerson?.firstName.isNullOrBlank()) {
            fullName = correctPerson?.firstName
        } else if (!correctPerson?.lastName.isNullOrBlank()) {
            fullName = correctPerson?.lastName
        }
        view?.showPeople(currentPeople, fullName ?: "")
    }

    companion object {
        private const val EXTRA_CURRENT_PEOPLE = "com.willowtreeapps.namegame.ui.EXTRA_CURRENT_PEOPLE"
        private const val EXTRA_CHOSEN_PEOPLE = "com.willowtreeapps.namegame.ui.EXTRA_CHOSEN_PEOPLE"
        private const val EXTRA_CORRECT_PERSON = "com.willowtreeapps.namegame.ui.EXTRA_CORRECT_PERSON"

        private const val MAX_PEOPLE = 5
    }
}