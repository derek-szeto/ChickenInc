package com.example.chickeninc

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.widget.TextView

object RarityTextStyler {

    fun apply(
        textView: TextView,
        rarity: Rarity
    ) {

        // Stop any old rarity animation
        val oldAnimator = textView.tag

        if (oldAnimator is ValueAnimator) {
            oldAnimator.cancel()
        }

        textView.tag = null

        textView.alpha = 1f

        textView.setShadowLayer(
            0f,
            0f,
            0f,
            Color.TRANSPARENT
        )


        when (rarity) {

            Rarity.COMMON -> {

                textView.setTextColor(
                    Color.parseColor("#B0BEC5")
                )
            }


            Rarity.UNCOMMON -> {

                textView.setTextColor(
                    Color.parseColor("#76FF03")
                )
            }


            Rarity.RARE -> {

                textView.setTextColor(
                    Color.parseColor("#40C4FF")
                )
            }


            Rarity.EPIC -> {

                textView.setTextColor(
                    Color.parseColor("#D500F9")
                )
            }


            Rarity.LEGENDARY -> {

                startLegendaryAnimation(textView)
            }


            Rarity.SECRET -> {

                startSecretAnimation(textView)
            }
        }
    }


    private fun startLegendaryAnimation(
        textView: TextView
    ) {

        val animator = ValueAnimator.ofObject(
            ArgbEvaluator(),
            Color.parseColor("#FFB300"),
            Color.parseColor("#FFF59D"),
            Color.parseColor("#FFD700")
        )

        animator.duration = 1100

        animator.repeatCount =
            ValueAnimator.INFINITE

        animator.repeatMode =
            ValueAnimator.REVERSE


        animator.addUpdateListener {

            val color =
                it.animatedValue as Int

            textView.setTextColor(color)

            textView.setShadowLayer(
                10f,
                0f,
                0f,
                Color.parseColor("#FFD700")
            )
        }

        animator.start()

        textView.tag = animator
    }


    private fun startSecretAnimation(
        textView: TextView
    ) {

        // Bright text surrounded by a dark aura
        textView.setTextColor(
            Color.parseColor("#F5F5F5")
        )


        val animator =
            ValueAnimator.ofFloat(
                5f,
                16f
            )

        animator.duration = 900

        animator.repeatCount =
            ValueAnimator.INFINITE

        animator.repeatMode =
            ValueAnimator.REVERSE


        animator.addUpdateListener {

            val glow =
                it.animatedValue as Float

            // Black aura surrounding Secret text
            textView.setShadowLayer(
                glow,
                0f,
                0f,
                Color.BLACK
            )

            // Slight shining pulse
            textView.alpha =
                0.8f + (glow / 80f)
        }

        animator.start()

        textView.tag = animator
    }
}