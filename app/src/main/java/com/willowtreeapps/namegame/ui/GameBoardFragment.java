package com.willowtreeapps.namegame.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
    private TextView result;
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
        result = view.findViewById(R.id.correct_incorrect);
        container = view.findViewById(R.id.face_container);

        //Hide the views until data loads
        title.setAlpha(0);
        result.setAlpha(0);

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
        int imageSize = (int) Ui.convertDpToPixel(100, getActivity());
        int n = faces.size();

        for (int i = 0; i < n; i++) {
            ImageView face = faces.get(i);
            Person person = people.get(i);
            face.setOnClickListener(getFaceClickListener(person));
            face.setTag(person);
            picasso.load(PREFIX_HTTPS + person.getHeadshot().getUrl())
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
        result.animate().alpha(0).start();
        for (int i = 0; i < faces.size(); i++) {
            ImageView face = faces.get(i);
            face.animate().scaleX(1).scaleY(1).setStartDelay(800 + 120 * i).setInterpolator(OVERSHOOT).start();
        }
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

    private View.OnClickListener getFaceClickListener(final Person person) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPictureClicked(person);
            }
        };
    }

    @Override
    public void showIncorrectAnswer(@NotNull Person correctPerson) {
        result.animate().alpha(1).start();
        result.setText(R.string.incorrect);
        result.setTextColor(getColor(R.color.alphaRed));
        hideIncorrectAnswers(correctPerson);
    }

    @Override
    public void showCorrectAnswer(@NotNull Person correctPerson) {
        result.animate().alpha(1).start();
        result.setText(R.string.correct);
        result.setTextColor(getColor(R.color.alphaGreen));
        hideIncorrectAnswers(correctPerson);
    }

    @ColorInt
    private int getColor(@ColorRes int colorRes) {
        return ContextCompat.getColor(getActivity(), colorRes);
    }

    private void hideIncorrectAnswers(Person correctPerson) {
        for (int i = 0; i < faces.size(); i++) {
            ImageView face = faces.get(i);
            face.setOnClickListener(null);
            if (!((Person) face.getTag()).getId().equals(correctPerson.getId())) {
                face.animate()
                        .scaleX(0)
                        .scaleY(0)
                        .setStartDelay(800 + 120 * i)
                        .setInterpolator(OVERSHOOT)
                        .start();
            } else {
                face.animate()
                        .scaleX(0)
                        .scaleY(0)
                        .setStartDelay(2500)
                        .setInterpolator(OVERSHOOT)
                        .start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        presenter.setUpNewBoard();
                    }
                }, 3000);
            }
        }
    }

    @Override
    public void showPeopleLoadError(@StringRes int message) {
        Snackbar.make(container, message, Snackbar.LENGTH_LONG).show();
    }
}
