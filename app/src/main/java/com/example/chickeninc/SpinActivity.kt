package com.example.chickeninc

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class SpinActivity : AppCompatActivity() {

    private lateinit var wheelView: PrizeWheelView
    private lateinit var wheelPointer: TextView

    private lateinit var spinButton: Button
    private lateinit var backButton: Button

    private lateinit var resultText: TextView
    private lateinit var spinTimerText: TextView

    private val wheelPrizes = ArrayList<WheelPrize>()

    private var isSpinning = false
    private var wheelNeedsRefresh = false

    private var wheelAnimator: ObjectAnimator? = null
    private var pointerAnimator: ObjectAnimator? = null
    private var spinPulseAnimator: ObjectAnimator? = null

    private val timerHandler = Handler(Looper.getMainLooper())

    private val timerRunnable = object : Runnable {
        override fun run() {
            updateSpinStatus()
            timerHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin)

        bindViews()
        setupBackHandling()
        setListeners()
        setButtonAnimations()

        generateWheel()
        updateSpinStatus()
    }

    override fun onResume() {
        super.onResume()

        timerHandler.removeCallbacks(timerRunnable)
        timerHandler.post(timerRunnable)

        startPointerAnimation()
        updateSpinStatus()
    }

    override fun onPause() {
        super.onPause()

        timerHandler.removeCallbacks(timerRunnable)

        stopPointerAnimation()
        stopSpinReadyPulse()
    }

    override fun onDestroy() {
        timerHandler.removeCallbacks(timerRunnable)

        wheelAnimator?.cancel()
        pointerAnimator?.cancel()
        spinPulseAnimator?.cancel()

        wheelAnimator = null
        pointerAnimator = null
        spinPulseAnimator = null

        super.onDestroy()
    }

    private fun bindViews() {
        wheelView = findViewById(R.id.prizeWheelView)
        wheelPointer = findViewById(R.id.wheelPointer)

        spinButton = findViewById(R.id.spinButton)
        backButton = findViewById(R.id.backButton)

        resultText = findViewById(R.id.resultText)
        spinTimerText = findViewById(R.id.spinTimerText)
    }

    private fun setupBackHandling() {
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
    }

    private fun setListeners() {
        backButton.setOnClickListener {
            if (!isSpinning) {
                finish()
            }
        }

        spinButton.setOnClickListener {
            if (isSpinning) {
                return@setOnClickListener
            }

            if (wheelNeedsRefresh) {
                wheelView.rotation = 0f
                generateWheel()
                wheelNeedsRefresh = false
            }

            spinWheel()
        }
    }

    private fun setButtonAnimations() {
        addButtonPressAnimation(backButton)
        addButtonPressAnimation(spinButton)
    }

    private fun generateWheel() {
        wheelPrizes.clear()

        repeat(7) {
            wheelPrizes.add(
                WheelPrize.ChickenPrize(
                    getWeightedChicken()
                )
            )
        }

        val gadget =
            WheelGadget.entries.random()

        wheelPrizes.add(
            WheelPrize.GadgetPrize(gadget)
        )

        wheelPrizes.shuffle()

        wheelView.setPrizes(wheelPrizes)

        resetResultStyle()
        resultText.text = "Tap SPIN to win!"
    }

    private fun getWeightedChicken(): Chicken {
        val roll = Random.nextInt(1, 101)

        val rarity = when (roll) {
            in 1..42 -> Rarity.UNCOMMON
            in 43..70 -> Rarity.RARE
            in 71..88 -> Rarity.EPIC
            in 89..97 -> Rarity.LEGENDARY
            else -> Rarity.SECRET
        }

        val matchingChickens =
            GameManager.shopChickens.filter {
                it.rarity == rarity &&
                        it.availableInShop
            }

        if (matchingChickens.isNotEmpty()) {
            return matchingChickens.random()
        }

        return GameManager.shopChickens
            .filter {
                it.rarity != Rarity.COMMON &&
                        it.availableInShop
            }
            .random()
    }

    private fun spinWheel() {
        if (!GameManager.consumeSpin()) {
            resetResultStyle()
            resultText.text = "No spin available!"

            animateResultReveal()
            updateSpinStatus()

            return
        }

        if (wheelPrizes.isEmpty()) {
            generateWheel()
        }

        isSpinning = true

        stopSpinReadyPulse()

        backButton.isEnabled = false
        backButton.alpha = 0.5f

        spinButton.isEnabled = false
        spinButton.alpha = 0.65f

        resetResultStyle()
        resultText.text = "Spinning..."

        updateSpinStatus()

        val winningIndex =
            Random.nextInt(wheelPrizes.size)

        val segmentAngle =
            360f / wheelPrizes.size

        val currentRotation =
            ((wheelView.rotation % 360f) + 360f) % 360f

        val desiredRotation =
            (360f - winningIndex * segmentAngle) % 360f

        val extraRotation =
            (
                    desiredRotation -
                            currentRotation +
                            360f
                    ) % 360f

        val fullSpins =
            Random.nextInt(7, 11)

        val startRotation =
            wheelView.rotation

        val endRotation =
            startRotation +
                    fullSpins * 360f +
                    extraRotation

        wheelAnimator?.cancel()

        wheelAnimator = ObjectAnimator.ofFloat(
            wheelView,
            View.ROTATION,
            startRotation,
            endRotation
        ).apply {
            duration = 4500
            interpolator = DecelerateInterpolator(2f)

            addListener(
                object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(
                        animation: Animator
                    ) {
                        wheelView.rotation =
                            ((wheelView.rotation % 360f) + 360f) % 360f

                        val winningPrize =
                            wheelPrizes[winningIndex]

                        givePrize(winningPrize)

                        animateWheelLanding()
                        animateResultReveal()

                        isSpinning = false
                        wheelNeedsRefresh = true

                        backButton.isEnabled = true
                        backButton.alpha = 1f

                        wheelAnimator = null

                        updateSpinStatus()
                    }

                    override fun onAnimationCancel(
                        animation: Animator
                    ) {
                        isSpinning = false

                        backButton.isEnabled = true
                        backButton.alpha = 1f

                        wheelAnimator = null

                        updateSpinStatus()
                    }
                }
            )

            start()
        }
    }

    private fun givePrize(prize: WheelPrize) {
        when (prize) {
            is WheelPrize.ChickenPrize ->
                giveChickenPrize(prize.chicken)

            is WheelPrize.GadgetPrize ->
                giveGadgetPrize(prize.gadget)
        }
    }

    private fun giveChickenPrize(chicken: Chicken) {
        applyChickenResultStyle(chicken.rarity)

        if (!GameManager.isOwned(chicken)) {
            GameManager.ownedChickens.add(chicken)

            if (
                GameManager.equippedChickens.size <
                GameManager.maxChickenSlots
            ) {
                GameManager.equipChicken(chicken)
            }

            resultText.text =
                "🎉 NEW CHICKEN! 🎉\n" +
                        "${chicken.name}\n" +
                        chicken.rarity.displayName

            return
        }

        if (chicken.canUpgrade()) {
            chicken.upgrade()

            resultText.text =
                "✨ DUPLICATE! ✨\n" +
                        "${chicken.name}\n" +
                        "FREE UPGRADE → Level ${chicken.upgradeLevel}"

            return
        }

        val bonusEggs =
            maxOf(
                chicken.shopPrice / 2,
                100
            )

        GameManager.eggs += bonusEggs

        resultText.text =
            "🥚 MAXED DUPLICATE! 🥚\n" +
                    "${chicken.name}\n" +
                    "+${formatNumber(bonusEggs)} Eggs"
    }

    private fun giveGadgetPrize(gadget: WheelGadget) {
        applyGadgetResultStyle()

        when (gadget) {
            WheelGadget.SPIN_ROLL -> {
                GameManager.spinRolls++

                resultText.text =
                    "⚡ GADGET! ⚡\n" +
                            "+1 SPIN ROLL"
            }

            WheelGadget.UNLIMITED_FEEDER -> {
                GameManager.activateUnlimitedFeeder()

                resultText.text =
                    "🌽 GADGET! 🌽\n" +
                            "UNLIMITED FEEDER\n" +
                            "5 MINUTES"
            }

            WheelGadget.UNLIMITED_MOOD -> {
                GameManager.activateUnlimitedMood()

                resultText.text =
                    "😊 GADGET! 😊\n" +
                            "UNLIMITED MOOD\n" +
                            "5 MINUTES"
            }
        }
    }

    private fun updateSpinStatus() {
        val currentTime =
            System.currentTimeMillis()

        if (GameManager.isFreeSpinReady()) {
            spinTimerText.text =
                "FREE SPIN READY!  •  Rolls: ${GameManager.spinRolls}"
        } else {
            val remaining =
                GameManager.nextFreeSpinTime -
                        currentTime

            spinTimerText.text =
                "Next Free: ${formatTime(remaining)}  •  " +
                        "Rolls: ${GameManager.spinRolls}"
        }

        if (isSpinning) {
            spinButton.text = "SPINNING..."
            spinButton.isEnabled = false
            spinButton.alpha = 0.65f

            stopSpinReadyPulse()

            return
        }

        spinButton.text = when {
            GameManager.isFreeSpinReady() ->
                "🎡 FREE SPIN!"

            GameManager.spinRolls > 0 ->
                "🎟 USE ROLL (${GameManager.spinRolls})"

            else ->
                "⏳ WAIT FOR SPIN"
        }

        val canSpin =
            GameManager.canSpin()

        spinButton.isEnabled =
            canSpin

        spinButton.alpha =
            if (canSpin) 1f else 0.55f

        if (canSpin) {
            startSpinReadyPulse()
        } else {
            stopSpinReadyPulse()
        }
    }

    private fun applyChickenResultStyle(rarity: Rarity) {
        when (rarity) {
            Rarity.COMMON -> {
                resultText.setTextColor(
                    Color.parseColor("#5F5A52")
                )

                resultText.setShadowLayer(
                    2.5f,
                    0f,
                    0f,
                    Color.parseColor("#FFF7E8")
                )
            }

            Rarity.UNCOMMON -> {
                resultText.setTextColor(
                    Color.parseColor("#2F8C3D")
                )

                resultText.setShadowLayer(
                    4f,
                    0f,
                    0f,
                    Color.parseColor("#DDF3D8")
                )
            }

            Rarity.RARE -> {
                resultText.setTextColor(
                    Color.parseColor("#236FA5")
                )

                resultText.setShadowLayer(
                    4.5f,
                    0f,
                    0f,
                    Color.parseColor("#D8F0FF")
                )
            }

            Rarity.EPIC -> {
                resultText.setTextColor(
                    Color.parseColor("#7A469D")
                )

                resultText.setShadowLayer(
                    5f,
                    0f,
                    0f,
                    Color.parseColor("#E7D9F0")
                )
            }

            Rarity.LEGENDARY -> {
                resultText.setTextColor(
                    Color.parseColor("#C98D08")
                )

                resultText.setShadowLayer(
                    6f,
                    0f,
                    0f,
                    Color.parseColor("#FFF0B5")
                )
            }

            Rarity.SECRET -> {
                resultText.setTextColor(
                    Color.BLACK
                )

                resultText.setShadowLayer(
                    6f,
                    0f,
                    0f,
                    Color.parseColor("#9A9A9A")
                )
            }
        }
    }

    private fun applyGadgetResultStyle() {
        resultText.setTextColor(
            Color.parseColor("#238F89")
        )

        resultText.setShadowLayer(
            5f,
            0f,
            0f,
            Color.parseColor("#C7F5F0")
        )
    }

    private fun resetResultStyle() {
        resultText.animate().cancel()

        resultText.setTextColor(
            Color.parseColor("#57351F")
        )

        resultText.setShadowLayer(
            0f,
            0f,
            0f,
            Color.TRANSPARENT
        )

        resultText.alpha = 1f
        resultText.scaleX = 1f
        resultText.scaleY = 1f
        resultText.translationY = 0f
    }

    private fun animateResultReveal() {
        resultText.animate().cancel()

        resultText.alpha = 0f
        resultText.scaleX = 0.72f
        resultText.scaleY = 0.72f
        resultText.translationY =
            dpToPx(18).toFloat()

        resultText.animate()
            .alpha(1f)
            .scaleX(1.12f)
            .scaleY(1.12f)
            .translationY(0f)
            .setDuration(220)
            .setInterpolator(
                DecelerateInterpolator()
            )
            .withEndAction {
                resultText.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(160)
                    .start()
            }
            .start()
    }

    private fun animateWheelLanding() {
        wheelView.animate().cancel()

        wheelView.animate()
            .scaleX(1.05f)
            .scaleY(1.05f)
            .setDuration(100)
            .withEndAction {
                wheelView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(160)
                    .start()
            }
            .start()
    }

    private fun startPointerAnimation() {
        if (pointerAnimator?.isRunning == true) {
            return
        }

        pointerAnimator = ObjectAnimator.ofFloat(
            wheelPointer,
            View.TRANSLATION_Y,
            -dpToPx(2).toFloat(),
            dpToPx(5).toFloat(),
            -dpToPx(2).toFloat()
        ).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            interpolator =
                AccelerateDecelerateInterpolator()

            start()
        }
    }

    private fun stopPointerAnimation() {
        pointerAnimator?.cancel()
        pointerAnimator = null

        if (::wheelPointer.isInitialized) {
            wheelPointer.translationY =
                -dpToPx(3).toFloat()
        }
    }

    private fun startSpinReadyPulse() {
        if (spinPulseAnimator?.isRunning == true) {
            return
        }

        val scaleX =
            PropertyValuesHolder.ofFloat(
                View.SCALE_X,
                1f,
                1.06f,
                1f
            )

        val scaleY =
            PropertyValuesHolder.ofFloat(
                View.SCALE_Y,
                1f,
                1.06f,
                1f
            )

        spinPulseAnimator =
            ObjectAnimator.ofPropertyValuesHolder(
                spinButton,
                scaleX,
                scaleY
            ).apply {
                duration = 1100
                repeatCount = ValueAnimator.INFINITE
                interpolator =
                    AccelerateDecelerateInterpolator()

                start()
            }
    }

    private fun stopSpinReadyPulse() {
        spinPulseAnimator?.cancel()
        spinPulseAnimator = null

        if (::spinButton.isInitialized) {
            spinButton.scaleX = 1f
            spinButton.scaleY = 1f
        }
    }

    private fun addButtonPressAnimation(button: View) {
        button.setOnTouchListener { view, event ->
            if (!view.isEnabled) {
                return@setOnTouchListener false
            }

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate().cancel()

                    if (view === spinButton) {
                        stopSpinReadyPulse()
                    }

                    view.animate()
                        .scaleX(0.92f)
                        .scaleY(0.92f)
                        .setDuration(75)
                        .start()
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    view.animate().cancel()

                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                        .withEndAction {
                            if (
                                view === spinButton &&
                                GameManager.canSpin() &&
                                !isSpinning
                            ) {
                                startSpinReadyPulse()
                            }
                        }
                        .start()
                }
            }

            false
        }
    }

    private fun formatTime(milliseconds: Long): String {
        if (milliseconds <= 0) {
            return "READY"
        }

        val totalSeconds =
            milliseconds / 1000

        val hours =
            totalSeconds / 3600

        val minutes =
            (totalSeconds % 3600) / 60

        val seconds =
            totalSeconds % 60

        return String.format(
            "%02d:%02d:%02d",
            hours,
            minutes,
            seconds
        )
    }

    private fun formatNumber(number: Int): String {
        return when {
            number >= 1_000_000_000 ->
                String.format(
                    "%.1fB",
                    number / 1_000_000_000.0
                )

            number >= 1_000_000 ->
                String.format(
                    "%.1fM",
                    number / 1_000_000.0
                )

            number >= 1_000 ->
                String.format(
                    "%.1fK",
                    number / 1_000.0
                )

            else ->
                number.toString()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (
                dp *
                        resources.displayMetrics.density
                ).toInt()
    }
}