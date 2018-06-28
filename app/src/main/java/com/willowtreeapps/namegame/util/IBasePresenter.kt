package com.willowtreeapps.namegame.util

import android.os.Bundle

interface IBasePresenter {

    fun start()

    fun stop()

    fun attachView(view: IBaseView)

    fun detachView()

    fun saveState(outState: Bundle)

    fun restoreState(savedInstanceState: Bundle?)
}