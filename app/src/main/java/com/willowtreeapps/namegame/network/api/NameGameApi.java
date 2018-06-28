package com.willowtreeapps.namegame.network.api;

import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.network.api.model.Profiles;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NameGameApi {
    //This call is broken
    @GET("/api/v1.0/profiles")
    Call<Profiles> getProfiles();

    @GET("/api/v1.0/profiles")
    Call<List<Person>> getPeople();
}
