package com.willowtreeapps.namegame.ui.gameboard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.willowtreeapps.namegame.R

class GameBoardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.name_game_activity)
        var gameBoardFragment = supportFragmentManager.findFragmentByTag(
            FRAG_TAG) as GameBoardFragment?
        if (gameBoardFragment == null) {
            gameBoardFragment = GameBoardFragment()
            supportFragmentManager.beginTransaction().add(R.id.container, gameBoardFragment,
                FRAG_TAG).commit()
        }
    }

    companion object {
        private const val FRAG_TAG = "NameGameFragmentTag"
    }
}
