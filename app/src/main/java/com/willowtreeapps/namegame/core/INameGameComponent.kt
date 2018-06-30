package com.willowtreeapps.namegame.core

import com.squareup.picasso.Picasso
import com.willowtreeapps.namegame.network.NetworkModule
import com.willowtreeapps.namegame.network.api.PeopleRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(ApplicationModule::class), (NetworkModule::class)])
interface INameGameComponent {

    fun getListRandomizer(): ListRandomizer

    fun getPicasso(): Picasso

    fun getProfilesRepository(): PeopleRepository
}