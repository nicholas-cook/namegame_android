package com.willowtreeapps.namegame.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.ListRandomizer;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.util.CircleBorderTransform;
import com.willowtreeapps.namegame.util.Ui;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GameBoardFragment extends Fragment implements IGameBoardContract.View {

    private static final Interpolator OVERSHOOT = new OvershootInterpolator();
    private static final String PREFIX_HTTPS = "https:";

    @Inject
    ListRandomizer listRandomizer;
    @Inject
    Picasso picasso;
    @Inject
    GameBoardPresenter presenter;

    private TextView title;
    private ViewGroup container;
    private List<ImageView> faces = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectNameGamePresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.name_game_fragment, container, false);
        presenter.attachView(this);
        return rootView;
    }

    private void injectNameGamePresenter() {
        DaggerIGameBoardComponent.builder()
                .iNameGameComponent(((NameGameApplication) getActivity().getApplication()).component())
                .build()
                .inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        title = view.findViewById(R.id.title);
        container = view.findViewById(R.id.face_container);

        //Hide the views until data loads
        title.setAlpha(0);

        int n = container.getChildCount();
        for (int i = 0; i < n; i++) {
            ImageView face = (ImageView) container.getChildAt(i);
            faces.add(face);

            //Hide the views until data loads
            face.setScaleX(0);
            face.setScaleY(0);
        }
        presenter.restoreState(savedInstanceState);
    }

    /**
     * A method for setting the images from people into the imageviews
     */
    private void setImages(List<ImageView> faces, List<Person> people) {
        int imageSize = (int) Ui.convertDpToPixel(100, getContext());
        int n = faces.size();

        for (int i = 0; i < n; i++) {
            ImageView face = faces.get(i);
            picasso.load(PREFIX_HTTPS + people.get(i).getHeadshot().getUrl())
                    .placeholder(R.drawable.ic_face_white_48dp)
                    .resize(imageSize, imageSize)
                    .transform(new CircleBorderTransform())
                    .into(face);
        }
    }

    /**
     * A method to animate the faces into view
     */
    private void animateFacesIn() {
        title.animate().alpha(1).start();
        for (int i = 0; i < faces.size(); i++) {
            ImageView face = faces.get(i);
            face.animate().scaleX(1).scaleY(1).setStartDelay(800 + 120 * i).setInterpolator(OVERSHOOT).start();
        }
    }

    /**
     * A method to handle when a person is selected
     *
     * @param view   The view that was selected
     * @param person The person that was selected
     */
    private void onPersonSelected(@NonNull View view, @NonNull Person person) {
        //TODO evaluate whether it was the right person and make an action based on that
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        presenter.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    public void onStop() {
        presenter.stop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showPeople(@NotNull ArrayList<Person> people, @NonNull String fullName) {
        if (fullName.isEmpty()) {
            title.setText(getString(R.string.question_no_name));
        } else {
            title.setText(getString(R.string.question, fullName));
        }
        setImages(faces, people);
        animateFacesIn();
    }

    @Override
    public void showIncorrectAnswer(@NotNull View view, @NotNull Person person) {

    }

    @Override
    public void showCorrectAnswer(@NotNull View view, @NotNull Person person) {

    }

    @Override
    public void showPeopleLoadError(@StringRes int message) {
        Snackbar.make(container, message, Snackbar.LENGTH_LONG).show();
    }
}
