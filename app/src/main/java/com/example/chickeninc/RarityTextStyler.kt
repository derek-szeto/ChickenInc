package com.example.chickeninc

import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import kotlin.random.Random


object RarityTextStyler {


    private data class StyleState(

        val rarity: Rarity,

        val animator: ValueAnimator? = null,

        val particleHandler: Handler? = null,

        val particleRunnable: Runnable? = null
    )


    fun apply(
        textView: TextView,
        rarity: Rarity
    ) {

        val oldState =
            textView.tag as? StyleState


        // IMPORTANT:
        // MainActivity updates very frequently.
        // Don't restart rarity animations
        // every production tick.
        if (
            oldState != null &&
            oldState.rarity == rarity
        ) {

            return
        }


        // =========================
        // STOP OLD EFFECTS
        // =========================

        oldState
            ?.animator
            ?.cancel()


        if (
            oldState?.particleHandler != null &&
            oldState.particleRunnable != null
        ) {

            oldState
                .particleHandler
                .removeCallbacks(
                    oldState.particleRunnable
                )
        }


        textView.tag =
            null


        textView.alpha =
            1f


        textView.setShadowLayer(
            0f,
            0f,
            0f,
            Color.TRANSPARENT
        )


        // =========================
        // RARITIES
        // =========================

        when (rarity) {


            // -------------------------
            // COMMON
            // -------------------------

            Rarity.COMMON -> {

                textView.setTextColor(
                    Color.parseColor(
                        "#5F5A52"
                    )
                )


                // Small light outline-like effect
                // makes it readable over sky/grass.
                textView.setShadowLayer(
                    2.5f,
                    0f,
                    1f,
                    Color.parseColor(
                        "#FFF7E8"
                    )
                )


                textView.tag =
                    StyleState(
                        rarity
                    )
            }


            // -------------------------
            // UNCOMMON
            // -------------------------

            Rarity.UNCOMMON -> {

                textView.setTextColor(
                    Color.parseColor(
                        "#2F8C3D"
                    )
                )


                textView.setShadowLayer(
                    3f,
                    0f,
                    1f,
                    Color.parseColor(
                        "#DDF3D8"
                    )
                )


                textView.tag =
                    StyleState(
                        rarity
                    )
            }


            // -------------------------
            // RARE
            // -------------------------

            Rarity.RARE -> {

                textView.setTextColor(
                    Color.parseColor(
                        "#236FA5"
                    )
                )


                textView.setShadowLayer(
                    3.5f,
                    0f,
                    1f,
                    Color.parseColor(
                        "#D8F0FF"
                    )
                )


                textView.tag =
                    StyleState(
                        rarity
                    )
            }


            // -------------------------
            // EPIC
            // -------------------------

            Rarity.EPIC -> {

                textView.setTextColor(
                    Color.parseColor(
                        "#7A469D"
                    )
                )


                textView.setShadowLayer(
                    4f,
                    0f,
                    1f,
                    Color.parseColor(
                        "#E7D9F0"
                    )
                )


                textView.tag =
                    StyleState(
                        rarity
                    )
            }


            // -------------------------
            // LEGENDARY
            // -------------------------

            Rarity.LEGENDARY -> {

                startLegendaryAnimation(
                    textView
                )
            }


            // -------------------------
            // SECRET
            // -------------------------

            Rarity.SECRET -> {

                startSecretAnimation(
                    textView
                )
            }
        }
    }


    // =====================================================
    // LEGENDARY
    // =====================================================

    private fun startLegendaryAnimation(
        textView: TextView
    ) {

        /*
         * Darker gold than before.
         *
         * The old very bright yellow glow
         * could become uncomfortable against
         * cream panels.
         */

        val animator =
            ValueAnimator.ofObject(

                ArgbEvaluator(),

                Color.parseColor(
                    "#B87900"
                ),

                Color.parseColor(
                    "#E8B832"
                ),

                Color.parseColor(
                    "#C98D08"
                )
            )


        animator.duration =
            1400


        animator.repeatCount =
            ValueAnimator.INFINITE


        animator.repeatMode =
            ValueAnimator.REVERSE


        animator.addUpdateListener {

            val color =
                it.animatedValue as Int


            textView.setTextColor(
                color
            )


            // Soft glow instead of giant neon glow
            textView.setShadowLayer(
                4.5f,
                0f,
                1f,
                Color.parseColor(
                    "#FFF0B5"
                )
            )
        }


        animator.start()


        textView.tag =
            StyleState(
                rarity =
                    Rarity.LEGENDARY,

                animator =
                    animator
            )
    }


    // =====================================================
    // SECRET
    // =====================================================

    private fun startSecretAnimation(
        textView: TextView
    ) {

        // =========================
        // BLACK TEXT
        // =========================

        textView.setTextColor(
            Color.BLACK
        )


        /*
         * Gray aura around black text.
         * We never reduce alpha because
         * Secret should always remain readable.
         */

        val auraAnimator =
            ValueAnimator.ofFloat(
                3.5f,
                7f
            )


        auraAnimator.duration =
            1200


        auraAnimator.repeatCount =
            ValueAnimator.INFINITE


        auraAnimator.repeatMode =
            ValueAnimator.REVERSE


        auraAnimator.addUpdateListener {

            val glow =
                it.animatedValue as Float


            textView.setShadowLayer(
                glow,
                0f,
                1f,
                Color.parseColor(
                    "#9A9A9A"
                )
            )
        }


        auraAnimator.start()


        // =========================
        // SECRET PARTICLES
        // =========================

        val handler =
            Handler(
                Looper.getMainLooper()
            )


        lateinit var particleRunnable:
                Runnable


        particleRunnable =
            object : Runnable {

                override fun run() {

                    /*
                     * If this screen is gone,
                     * stop automatically.
                     */
                    if (
                        !textView
                            .isAttachedToWindow
                    ) {

                        return
                    }


                    // Emit a couple particles
                    // each cycle.
                    spawnSecretParticle(
                        textView
                    )

                    spawnSecretParticle(
                        textView
                    )


                    handler.postDelayed(
                        this,
                        320
                    )
                }
            }


        handler.post(
            particleRunnable
        )


        textView.tag =
            StyleState(

                rarity =
                    Rarity.SECRET,

                animator =
                    auraAnimator,

                particleHandler =
                    handler,

                particleRunnable =
                    particleRunnable
            )
    }


    // =====================================================
    // SECRET PARTICLE
    // =====================================================

    private fun spawnSecretParticle(
        textView: TextView
    ) {

        val parent =
            textView.parent
                    as? ViewGroup
                ?: return


        if (
            textView.width <= 0 ||
            textView.height <= 0
        ) {

            return
        }


        val particle =
            View(
                textView.context
            )


        // Random 3–6dp particle
        val size =
            dpToPx(
                textView,
                Random.nextInt(
                    3,
                    7
                )
            )


        // Black / charcoal / gray
        val particleColors =
            intArrayOf(

                Color.parseColor(
                    "#101010"
                ),

                Color.parseColor(
                    "#303030"
                ),

                Color.parseColor(
                    "#565656"
                ),

                Color.parseColor(
                    "#808080"
                )
            )


        val shape =
            GradientDrawable()


        shape.shape =
            GradientDrawable.OVAL


        shape.setColor(
            particleColors.random()
        )


        particle.background =
            shape


        particle.alpha =
            0.8f


        /*
         * Overlay means the particles
         * do NOT affect layout spacing.
         */
        particle.layout(
            0,
            0,
            size,
            size
        )


        parent.overlay.add(
            particle
        )


        // Start somewhere around the letters
        particle.x =
            textView.x +
                    Random.nextFloat() *
                    textView.width


        particle.y =
            textView.y +
                    textView.height *
                    (
                            0.25f +
                                    Random.nextFloat() *
                                    0.55f
                            )


        val sideMovement =
            dpToPx(
                textView,
                Random.nextInt(
                    -14,
                    15
                )
            )


        val rise =
            dpToPx(
                textView,
                Random.nextInt(
                    18,
                    38
                )
            )


        particle.animate()

            .translationXBy(
                sideMovement.toFloat()
            )

            .translationYBy(
                -rise.toFloat()
            )

            .alpha(
                0f
            )

            .scaleX(
                0.35f
            )

            .scaleY(
                0.35f
            )

            .setDuration(
                Random.nextLong(
                    650,
                    1000
                )
            )

            .setInterpolator(
                DecelerateInterpolator()
            )

            .withEndAction {

                parent.overlay.remove(
                    particle
                )
            }

            .start()
    }


    // =====================================================
    // DP HELPER
    // =====================================================

    private fun dpToPx(
        view: View,
        dp: Int
    ): Int {

        return (
                dp *
                        view.resources
                            .displayMetrics
                            .density
                ).toInt()
    }
}