package com.willowtreeapps.namegame;

import android.test.suitebuilder.annotation.SmallTest;

import com.willowtreeapps.namegame.network.api.NameGameApi;
import com.willowtreeapps.namegame.network.api.PeopleRepository;
import com.willowtreeapps.namegame.network.api.model.Person;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SmallTest
public class ProfilesRepositoryTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private static final Profiles PROFILES;

    static {
        List<Person> people = new ArrayList<>();
        people.add(new Person("1", null, null, null, "Bill", "Smith", null, null));
        people.add(new Person("2", null, null, null, "Pam", "White", null, null));
        people.add(new Person("3", null, null, null, "Fred", "Doe", null, null));
        PROFILES = new Profiles(people, null);
    }

    @Test
    public void should_throw_for_multiple_registration_of_one_listener() throws Exception {
        NameGameApi api = mock(NameGameApi.class);
        when(api.getProfiles()).thenReturn(SynchronousCallAdapter.forSuccess(PROFILES));
        PeopleRepository repo = new PeopleRepository(api);
        PeopleRepository.Listener listener = mock(PeopleRepository.Listener.class);
        repo.register(listener);
        thrown.expect(IllegalStateException.class);
        repo.register(listener);
    }

    @Test
    public void should_allow_registration_of_multiple_listeners() throws Exception {
        NameGameApi api = mock(NameGameApi.class);
        when(api.getProfiles()).thenReturn(SynchronousCallAdapter.forSuccess(PROFILES));
        PeopleRepository repo = new PeopleRepository(api);
        PeopleRepository.Listener one = mock(PeopleRepository.Listener.class);
        PeopleRepository.Listener two = mock(PeopleRepository.Listener.class);
        PeopleRepository.Listener three = mock(PeopleRepository.Listener.class);
        repo.register(one);
        repo.register(two);
        repo.register(three);
    }

    @Test
    public void should_notify_new_registrants_on_success() throws Exception {
        NameGameApi api = mock(NameGameApi.class);
        when(api.getProfiles()).thenReturn(SynchronousCallAdapter.forSuccess(PROFILES));
        PeopleRepository repo = new PeopleRepository(api);
        PeopleRepository.Listener listener = mock(PeopleRepository.Listener.class);
        repo.register(listener);
        verify(listener, times(1)).onLoadFinished(any(Profiles.class));
    }

    @Test
    public void should_not_notify_new_registrants_on_load_failure() throws Exception {
        NameGameApi api = mock(NameGameApi.class);
        when(api.getProfiles()).thenReturn(SynchronousCallAdapter.<Profiles>forError());
        PeopleRepository repo = new PeopleRepository(api);
        PeopleRepository.Listener listener = mock(PeopleRepository.Listener.class);
        repo.register(listener);
        verify(listener, times(0)).onLoadFinished(any(Profiles.class));
    }

    @Test
    public void should_notify_existing_registrants_on_load_failure() throws Exception {
        NameGameApi api = mock(NameGameApi.class);
        when(api.getProfiles()).thenReturn(SynchronousCallAdapter.<Profiles>forError());
        PeopleRepository.Listener listener = mock(PeopleRepository.Listener.class);
        PeopleRepository repo = new PeopleRepository(api, listener);
        verify(listener, times(1)).onError(any(IOException.class));
    }

}
