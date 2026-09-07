package com.example.chickeninc

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import androidx.activity.OnBackPressedCallback

class SpinActivity : AppCompatActivity() {

    private lateinit var wheelView: PrizeWheelView

    private lateinit var spinButton: Button
    private lateinit var backButton: Button

    private lateinit var resultText: TextView
    private lateinit var spinTimerText: TextView


    private val wheelPrizes =
        ArrayList<WheelPrize>()


    private var isSpinning =
        false

    private var wheelNeedsRefresh =
        false


    private val timerHandler =
        Handler(Looper.getMainLooper())


    private val timerRunnable =
        object : Runnable {

            override fun run() {

                updateSpinStatus()

                timerHandler.postDelayed(
                    this,
                    1000
                )
            }
        }
    private var wheelAnimator: ObjectAnimator? = null


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_spin
        )
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    if (!isSpinning) {
                        finish()
                    }
                }
            }
        )

        wheelView =
            findViewById(
                R.id.prizeWheelView
            )


        spinButton =
            findViewById(
                R.id.spinButton
            )

        backButton =
            findViewById(
                R.id.backButton
            )

        resultText =
            findViewById(
                R.id.resultText
            )

        spinTimerText =
            findViewById(
                R.id.spinTimerText
            )


        generateWheel()


        backButton.setOnClickListener {

            if (!isSpinning) {
                finish()
            }
        }


        spinButton.setOnClickListener {

            if (isSpinning) {
                return@setOnClickListener
            }


            // Generate a fresh wheel before
            // the NEXT spin
            if (wheelNeedsRefresh) {

                wheelView.rotation =
                    0f

                generateWheel()

                wheelNeedsRefresh =
                    false
            }


            spinWheel()
        }


        updateSpinStatus()
    }


    // =========================
    // GENERATE WHEEL
    // =========================

    private fun generateWheel() {

        wheelPrizes.clear()


        // 7 chicken slots
        repeat(7) {

            val chicken =
                getWeightedChicken()

            wheelPrizes.add(
                WheelPrize.ChickenPrize(
                    chicken
                )
            )
        }


        // 1 gadget
        val gadget =
            WheelGadget.entries.random()


        wheelPrizes.add(
            WheelPrize.GadgetPrize(
                gadget
            )
        )


        // Gadget can appear anywhere
        wheelPrizes.shuffle()


        wheelView.setPrizes(
            wheelPrizes
        )


        resultText.text =
            "Tap SPIN to win!"
    }


    // =========================
    // RARITY WEIGHTING
    // =========================

    private fun getWeightedChicken(): Chicken {

        val roll =
            Random.nextInt(
                1,
                101
            )


        val rarity =
            when (roll) {

                in 1..42 ->
                    Rarity.UNCOMMON

                in 43..70 ->
                    Rarity.RARE

                in 71..88 ->
                    Rarity.EPIC

                in 89..97 ->
                    Rarity.LEGENDARY

                else ->
                    Rarity.SECRET
            }


        val matchingChickens =
            GameManager.shopChickens.filter {

                it.rarity == rarity &&
                        it.availableInShop
            }


        // Safety fallback
        if (matchingChickens.isEmpty()) {

            return GameManager
                .shopChickens
                .filter {

                    it.rarity !=
                            Rarity.COMMON
                }
                .random()
        }


        return matchingChickens.random()
    }


    // =========================
    // SPIN LOGIC
    // =========================

    private fun spinWheel() {

        if (!GameManager.consumeSpin()) {

            resultText.text =
                "No spin available!"

            updateSpinStatus()

            return
        }


        isSpinning =
            true
        backButton.isEnabled = false
        backButton.alpha = 0.5f

        spinButton.isEnabled =
            false

        resultText.text =
            "Spinning..."


        // Decide winner BEFORE animation
        val winningIndex =
            Random.nextInt(
                wheelPrizes.size
            )


        val segmentAngle =
            360f / 8f


        // Normalize current wheel angle
        val currentRotation =
            (
                    (wheelView.rotation % 360f) +
                            360f
                    ) % 360f


        // Slot 0 is naturally centered
        // under pointer at rotation 0.
        val desiredRotation =
            (
                    360f -
                            winningIndex *
                            segmentAngle
                    ) % 360f


        var extraRotation =
            desiredRotation -
                    currentRotation


        if (extraRotation < 0f) {

            extraRotation +=
                360f
        }


        // 7-10 full rotations
        val fullSpins =
            Random.nextInt(
                7,
                11
            )


        val totalRotation =
            fullSpins * 360f +
                    extraRotation


        val startRotation =
            wheelView.rotation


        val endRotation =
            startRotation +
                    totalRotation


        wheelAnimator =
            ObjectAnimator.ofFloat(
                wheelView,
                "rotation",
                startRotation,
                endRotation
            )


        wheelAnimator?.duration =
            4500


        wheelAnimator?.interpolator =
            DecelerateInterpolator(
                2f
            )


        wheelAnimator?.addListener(
            object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {



                    wheelView.rotation =
                        ((wheelView.rotation % 360f) + 360f) % 360f

                    val winningPrize =
                        wheelPrizes[winningIndex]

                    givePrize(winningPrize)

                    isSpinning = false
                    wheelNeedsRefresh = true
                    backButton.isEnabled = true
                    backButton.alpha = 1f

                    updateSpinStatus()
                }

                override fun onAnimationCancel(animation: Animator) {

                    isSpinning = false
                }
            }
        )


        wheelAnimator?.start()
    }


    // =========================
    // GIVE PRIZE
    // =========================

    private fun givePrize(
        prize: WheelPrize
    ) {

        when (prize) {

            is WheelPrize.ChickenPrize -> {

                giveChickenPrize(
                    prize.chicken
                )
            }


            is WheelPrize.GadgetPrize -> {

                giveGadgetPrize(
                    prize.gadget
                )
            }
        }
    }


    private fun giveChickenPrize(
        chicken: Chicken
    ) {

        // NEW CHICKEN
        if (!GameManager.isOwned(chicken)) {

            GameManager
                .ownedChickens
                .add(chicken)


            // Auto-equip if a farm
            // slot is available
            if (
                GameManager
                    .equippedChickens
                    .size
                <
                GameManager
                    .maxChickenSlots
            ) {

                GameManager
                    .equippedChickens
                    .add(chicken)
            }


            resultText.text =
                "NEW! ${chicken.name}\n" +
                        "${chicken.rarity.displayName}"

            return
        }


        // DUPLICATE:
        // Give a free upgrade
        if (chicken.canUpgrade()) {

            chicken.upgrade()

            resultText.text =
                "Duplicate ${chicken.name}!\n" +
                        "FREE UPGRADE → " +
                        "Level ${chicken.upgradeLevel}"

            return
        }


        // Already owned + maxed
        val bonusEggs =
            maxOf(
                chicken.shopPrice / 2,
                100
            )


        GameManager.eggs +=
            bonusEggs


        resultText.text =
            "Duplicate MAXED chicken!\n" +
                    "+$bonusEggs Eggs"
    }


    private fun giveGadgetPrize(
        gadget: WheelGadget
    ) {

        when (gadget) {

            WheelGadget.SPIN_ROLL -> {

                GameManager.spinRolls++

                resultText.text =
                    "GADGET!\n+1 Spin Roll"
            }


            WheelGadget.UNLIMITED_FEEDER -> {

                GameManager
                    .activateUnlimitedFeeder()

                resultText.text =
                    "GADGET!\n" +
                            "Unlimited Feeder: 5 min"
            }


            WheelGadget.UNLIMITED_MOOD -> {

                GameManager
                    .activateUnlimitedMood()

                resultText.text =
                    "GADGET!\n" +
                            "Unlimited Mood: 5 min"
            }
        }
    }


    // =========================
    // SPIN TIMER
    // =========================

    private fun updateSpinStatus() {

        val currentTime =
            System.currentTimeMillis()


        if (GameManager.isFreeSpinReady()) {

            spinTimerText.text =
                "FREE SPIN READY!  •  " +
                        "Rolls: ${GameManager.spinRolls}"


            spinButton.text =
                "FREE SPIN"

        } else {

            val remaining =
                GameManager
                    .nextFreeSpinTime -
                        currentTime


            spinTimerText.text =
                "Next Free: " +
                        formatTime(remaining) +
                        "  •  Rolls: " +
                        GameManager.spinRolls


            if (GameManager.spinRolls > 0) {

                spinButton.text =
                    "USE ROLL " +
                            "(${GameManager.spinRolls})"

            } else {

                spinButton.text =
                    "WAIT FOR SPIN"
            }
        }


        spinButton.isEnabled =
            !isSpinning &&
                    GameManager.canSpin()
    }


    private fun formatTime(
        milliseconds: Long
    ): String {

        if (milliseconds <= 0) {

            return "READY"
        }


        val totalSeconds =
            milliseconds / 1000


        val hours =
            totalSeconds / 3600


        val minutes =
            (totalSeconds % 3600) /
                    60


        val seconds =
            totalSeconds % 60


        return String.format(
            "%02d:%02d:%02d",
            hours,
            minutes,
            seconds
        )
    }


    // =========================
    // TIMER LIFECYCLE
    // =========================

    override fun onResume() {
        super.onResume()


        timerHandler.removeCallbacks(timerRunnable)
        timerHandler.post(timerRunnable)

        updateSpinStatus()
    }


    override fun onPause() {
        super.onPause()

        timerHandler.removeCallbacks(timerRunnable)

    }
    override fun onDestroy() {
        super.onDestroy()

        timerHandler.removeCallbacks(timerRunnable)

        wheelAnimator?.cancel()
        wheelAnimator = null
    }
}