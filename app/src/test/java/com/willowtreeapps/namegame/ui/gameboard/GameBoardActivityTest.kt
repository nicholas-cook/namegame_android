package com.willowtreeapps.namegame.ui.gameboard

import android.view.ViewGroup
import com.willowtreeapps.namegame.BuildConfig
import com.willowtreeapps.namegame.R
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class GameBoardActivityTest {

    @Test
    fun testFaceContainer() {
        val gbActivity = Robolectric.setupActivity(GameBoardActivity::class.java)
        val faceContainer = gbActivity.findViewById<ViewGroup>(R.id.face_container)
        assertThat("Face container contains five views", faceContainer.childCount, equalTo(5))
    }
}