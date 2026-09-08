package com.example.chickeninc

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var playButton: Button
    private lateinit var tutorialButton: Button
    private lateinit var closeTutorialButton: Button
    private lateinit var tutorialOverlay: FrameLayout

    private var playButtonAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        playButton = findViewById(R.id.playButton)
        tutorialButton = findViewById(R.id.tutorialButton)
        closeTutorialButton = findViewById(R.id.closeTutorialButton)
        tutorialOverlay = findViewById(R.id.tutorialOverlay)

        playButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
        }

        tutorialButton.setOnClickListener {
            tutorialOverlay.visibility = View.VISIBLE
        }

        closeTutorialButton.setOnClickListener {
            tutorialOverlay.visibility = View.GONE
        }

        addButtonPressAnimation(tutorialButton)
        addButtonPressAnimation(closeTutorialButton)
    }

    override fun onResume() {
        super.onResume()
        startPlayButtonAnimation()
    }

    override fun onPause() {
        super.onPause()
        stopPlayButtonAnimation()
    }

    private fun startPlayButtonAnimation() {
        stopPlayButtonAnimation()

        playButtonAnimator = ValueAnimator.ofFloat(
            1f,
            1.07f
        ).apply {
            duration = 850
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener {
                val scale = it.animatedValue as Float

                playButton.scaleX = scale
                playButton.scaleY = scale
            }

            start()
        }
    }

    private fun stopPlayButtonAnimation() {
        playButtonAnimator?.cancel()
        playButtonAnimator = null

        if (::playButton.isInitialized) {
            playButton.scaleX = 1f
            playButton.scaleY = 1f
        }
    }

    private fun addButtonPressAnimation(button: View) {
        button.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate()
                        .scaleX(0.94f)
                        .scaleY(0.94f)
                        .setDuration(70)
                        .start()
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(110)
                        .start()
                }
            }

            false
        }
    }
}