package com.willowtreeapps.namegame.ui.gameboard

import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.willowtreeapps.namegame.R
import com.willowtreeapps.namegame.core.NameGameApplication
import com.willowtreeapps.namegame.network.api.model.Person
import com.willowtreeapps.namegame.util.CircleBorderTransform
import com.willowtreeapps.namegame.util.Ui
import kotlinx.android.synthetic.main.name_game_fragment.*
import java.util.*
import javax.inject.Inject

class GameBoardFragment : Fragment(), IGameBoardContract.View {

    @Inject
    lateinit var picasso: Picasso
    @Inject
    lateinit var presenter: GameBoardPresenter

    private val faces = ArrayList<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectNameGamePresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.name_game_fragment, container, false)
        presenter.attachView(this)
        return rootView
    }

    private fun injectNameGamePresenter() {
        DaggerIGameBoardComponent.builder()
            .iNameGameComponent((activity?.application as NameGameApplication).component())
            .build()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //Hide the views until data loads
        title.alpha = 0f
        correct_incorrect.alpha = 0f

        val n = face_container.childCount
        for (i in 0 until n) {
            val face = face_container.getChildAt(i) as ImageView
            faces.add(face)

            //Hide the views until data loads
            face.apply {
                scaleX = 0f
                scaleY = 0f
            }
        }
        presenter.restoreState(savedInstanceState)
    }

    /**
     * A method for setting the images from people into the imageviews
     */
    private fun setImages(faces: List<ImageView>, people: List<Person>) {
        val imageSize = Ui.convertDpToPixel(100f, activity!!).toInt()
        val n = faces.size

        for (i in 0 until n) {
            val face = faces[i]
            val person = people[i]
            face.setOnClickListener(getFaceClickListener(person))
            face.tag = person
            picasso.load(PREFIX_HTTPS + person.headshot.url).apply {
                placeholder(R.drawable.ic_face_white_48dp)
                resize(imageSize, imageSize)
                transform(CircleBorderTransform())
                into(face)
            }
        }
    }

    private fun getFaceClickListener(person: Person): View.OnClickListener {
        return View.OnClickListener { presenter.onPictureClicked(person) }
    }

    /**
     * A method to animate the faces into view
     */
    private fun animateFacesIn() {
        title.animate().alpha(1f).start()
        correct_incorrect.animate().alpha(0f).start()
        for (i in faces.indices) {
            val face = faces[i]
            face.animate().apply {
                scaleX(1f)
                scaleY(1f)
                startDelay = 800L + 120L * i
                interpolator = OVERSHOOT
                start()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        presenter.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        presenter.stop()
        super.onStop()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun showPeople(people: ArrayList<Person>, fullName: String) {
        if (fullName.isEmpty()) {
            title.text = getString(R.string.question_no_name)
        } else {
            title.text = getString(R.string.question, fullName)
        }
        setImages(faces, people)
        animateFacesIn()
    }

    override fun showIncorrectAnswer(correctPerson: Person) {
        correct_incorrect.apply {
            animate().alpha(1f).start()
            setText(R.string.incorrect)
            setTextColor(getColor(R.color.alphaRed))
        }
        hideIncorrectAnswers(correctPerson)
    }

    override fun showCorrectAnswer(correctPerson: Person) {
        correct_incorrect.apply {
            animate().alpha(1f).start()
            setText(R.string.correct)
            setTextColor(getColor(R.color.alphaGreen))
        }
        hideIncorrectAnswers(correctPerson)
    }

    @ColorInt
    private fun getColor(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(activity!!, colorRes)
    }

    private fun hideIncorrectAnswers(correctPerson: Person) {
        for (i in faces.indices) {
            val face = faces[i]
            face.setOnClickListener(null)
            if ((face.tag as Person).id != correctPerson.id) {
                face.animate().apply {
                    scaleX(0f)
                    scaleY(0f)
                    startDelay = (800 + 120 * i).toLong()
                    interpolator = OVERSHOOT
                    start()
                }
            } else {
                face.animate().apply {
                    scaleX(0f)
                    scaleY(0f)
                    startDelay = 2500
                    interpolator = OVERSHOOT
                    start()
                }
                Handler().postDelayed({ presenter.setUpNewBoard() }, 3000)
            }
        }
    }

    override fun showPeopleLoadError(@StringRes message: Int) {
        Snackbar.make(face_container, message, Snackbar.LENGTH_LONG).show()
    }

    companion object {

        private val OVERSHOOT = OvershootInterpolator()
        private const val PREFIX_HTTPS = "https:"
    }
}
