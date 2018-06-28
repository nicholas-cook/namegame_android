package com.willowtreeapps.namegame.core;

import android.app.Application;

import com.willowtreeapps.namegame.network.NetworkModule;

public class NameGameApplication extends Application {

    private INameGameComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = buildComponent();
    }

    public INameGameComponent component() {
        return component;
    }

    protected INameGameComponent buildComponent() {
        return DaggerINameGameComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule("https://willowtreeapps.com/"))
                .build();
    }
}
