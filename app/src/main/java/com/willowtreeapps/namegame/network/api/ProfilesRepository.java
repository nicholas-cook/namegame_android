package com.willowtreeapps.namegame.network.api;

import android.accounts.NetworkErrorException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.willowtreeapps.namegame.network.api.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ProfilesRepository {

    @NonNull
    private final NameGameApi api;
    @NonNull
    private List<Listener> listeners = new ArrayList<>(1);
    @Nullable
    private List<Person> people;
    @Nullable
    private Throwable error;

    public ProfilesRepository(@NonNull NameGameApi api, Listener... listeners) {
        this.api = api;
        if (listeners != null) {
            this.listeners = new ArrayList<>(Arrays.asList(listeners));
        }
        load();
    }

    private void load() {
        this.api.getPeople().enqueue(new Callback<List<Person>>() {
            @Override
            public void onResponse(Call<List<Person>> call, Response<List<Person>> response) {
                if (response.isSuccessful()) {
                    people = response.body();
                    if (people != null) {
                        for (Listener listener : listeners) {
                            listener.onLoadFinished(people);
                        }
                    }
                } else {
                    onFailure(call, new HttpException(response));
                }
            }

            @Override
            public void onFailure(Call<List<Person>> call, Throwable t) {
                error = t;
                for (Listener listener : listeners) {
                    listener.onError(t);
                }
            }
        });
    }

    public void register(@NonNull Listener listener) {
        if (listeners.contains(listener)) throw new IllegalStateException("Listener is already registered.");
        listeners.add(listener);
        if (people != null) {
            listener.onLoadFinished(people);
        } else if (error != null) {
            listener.onError(error);
        }
    }

    public void unregister(@NonNull Listener listener) {
        listeners.remove(listener);
    }

    public interface Listener {
        void onLoadFinished(@NonNull List<Person> people);

        void onError(@NonNull Throwable error);
    }

}
