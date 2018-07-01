package com.willowtreeapps.namegame.ui.gameboard

import com.willowtreeapps.namegame.core.ListRandomizer
import com.willowtreeapps.namegame.network.api.PeopleRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GameBoardPresenterTest {

    @Mock
    private lateinit var peopleRepository: PeopleRepository
    @Mock
    private lateinit var listRandomizer: ListRandomizer
    @Mock
    private lateinit var gameBoardView: IGameBoardContract.View

    lateinit var presenter: GameBoardPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = GameBoardPresenter(peopleRepository, listRandomizer)
        presenter.attachView(gameBoardView)
    }

    @Test
    fun testPrepAndDisplayBoard() {
        presenter.prepAndDisplayBoard()
        verify(gameBoardView).showPeople(listOf(), "")
    }
}