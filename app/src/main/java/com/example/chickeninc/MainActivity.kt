package com.example.chickeninc

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var eggCountText: TextView
    private lateinit var eggProduction: TextView
    private lateinit var farmRankText: TextView
    private lateinit var moodText: TextView
    private lateinit var feederText: TextView
    private lateinit var moodBar: ProgressBar

    private lateinit var chickenImage1: ImageView
    private lateinit var chickenImage2: ImageView
    private lateinit var chickenImage3: ImageView

    private lateinit var chickenSlotText1: TextView
    private lateinit var chickenSlotText2: TextView
    private lateinit var chickenSlotText3: TextView

    private lateinit var chickenSlot1: FrameLayout
    private lateinit var chickenSlot2: FrameLayout
    private lateinit var chickenSlot3: FrameLayout

    private lateinit var feedButton: Button
    private lateinit var deployButton: Button
    private lateinit var rebirthButton: Button
    private lateinit var homeButton: Button

    private val handler = Handler(Looper.getMainLooper())
    private val idleAnimators = ArrayList<ObjectAnimator>()

    private var chickenBaseTranslations = listOf(0f, 0f, 0f)
    private var displayedRebirthLevel = -1

    private val eggRunnable = object : Runnable {
        override fun run() {
            if (GameManager.mood > 10) {
                GameManager.eggs += GameManager.getTotalProduction()
                showEggProductionAnimations()
                animateEggCounter()
            }

            updateScreen()
            handler.postDelayed(this, getProductionDelay())
        }
    }

    private val moodRunnable = object : Runnable {
        override fun run() {
            if (!GameManager.hasUnlimitedMood() && GameManager.mood > 0) {
                GameManager.mood--
            }

            updateScreen()
            handler.postDelayed(this, 5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eggCountText = findViewById(R.id.eggCountText)
        eggProduction = findViewById(R.id.eggsPerSecondText)
        farmRankText = findViewById(R.id.farmRankText)

        moodText = findViewById(R.id.moodText)
        feederText = findViewById(R.id.feederText)
        moodBar = findViewById(R.id.moodBar)

        chickenImage1 = findViewById(R.id.chickenImage1)
        chickenImage2 = findViewById(R.id.chickenImage2)
        chickenImage3 = findViewById(R.id.chickenImage3)

        chickenSlotText1 = findViewById(R.id.chickenSlotText1)
        chickenSlotText2 = findViewById(R.id.chickenSlotText2)
        chickenSlotText3 = findViewById(R.id.chickenSlotText3)

        chickenSlot1 = findViewById(R.id.chickenSlot1)
        chickenSlot2 = findViewById(R.id.chickenSlot2)
        chickenSlot3 = findViewById(R.id.chickenSlot3)

        feedButton = findViewById(R.id.feedButton)
        deployButton = findViewById(R.id.deployButton)
        rebirthButton = findViewById(R.id.rebirthButton)
        homeButton = findViewById(R.id.homeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)

            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            )

            startActivity(intent)
            finish()
        }

        val upgradesButton = findViewById<Button>(R.id.upgradesButton)
        val shopButton = findViewById<Button>(R.id.shopButton)
        val spinButton = findViewById<Button>(R.id.spinButton)

        chickenBaseTranslations = listOf(
            chickenImage1.translationY,
            chickenImage2.translationY,
            chickenImage3.translationY
        )

        deployButton.setOnClickListener {
            showDeployManagerPopup()
        }

        upgradesButton.setOnClickListener {
            startActivity(Intent(this, UpgradesActivity::class.java))
        }

        shopButton.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }

        spinButton.setOnClickListener {
            startActivity(Intent(this, SpinActivity::class.java))
        }

        rebirthButton.setOnClickListener {
            showRebirthPopup()
        }

        feedButton.setOnClickListener {
            feedChickens()
        }

        listOf(
            homeButton,
            deployButton,
            feedButton,
            upgradesButton,
            shopButton,
            spinButton,
            rebirthButton
        ).forEach {
            addButtonPressAnimation(it)
        }

        updateScreen()
    }

    override fun onResume() {
        super.onResume()

        updateScreen()

        handler.removeCallbacks(eggRunnable)
        handler.removeCallbacks(moodRunnable)

        handler.post(eggRunnable)
        handler.post(moodRunnable)

        startChickenIdleAnimations()
    }

    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(eggRunnable)
        handler.removeCallbacks(moodRunnable)

        stopChickenIdleAnimations()
    }

    private fun feedChickens() {
        val feeder = GameManager.feeder

        val canFeed = if (GameManager.hasUnlimitedFeeder()) {
            true
        } else {
            feeder.feed()
        }

        if (!canFeed) {
            Toast.makeText(
                this,
                "The feeder is empty!",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        GameManager.mood =
            (GameManager.mood + feeder.getMoodIncrease()).coerceAtMost(100)

        updateScreen()
        animateMoodBar()

        handler.removeCallbacks(eggRunnable)
        handler.post(eggRunnable)
    }

    private fun updateScreen() {
        eggCountText.text = "Eggs: ${formatNumber(GameManager.eggs)}"

        updateRebirthBadge()
        updateRebirthButton()

        moodText.text = "Mood: ${GameManager.mood}%"
        moodBar.progress = GameManager.mood

        val production = formatNumber(GameManager.getTotalProduction())

        eggProduction.text = when (GameManager.mood) {
            in 90..100 -> "$production eggs every 0.5 seconds"
            in 75..89 -> "$production eggs every 1 second"
            in 60..74 -> "$production eggs every 2 seconds"
            in 45..59 -> "$production eggs every 3 seconds"
            in 30..44 -> "$production eggs every 4 seconds"
            in 11..29 -> "$production eggs every 5 seconds"
            else -> "No egg production"
        }

        feederText.text =
            "Feeder: ${GameManager.feeder.currentFood} / ${GameManager.feeder.getCapacity()}"

        updateChickenDisplay()
    }

    private fun getProductionDelay(): Long {
        return when (GameManager.mood) {
            in 90..100 -> 500
            in 75..89 -> 1000
            in 60..74 -> 2000
            in 45..59 -> 3000
            in 30..44 -> 4000
            else -> 5000
        }
    }

    private fun updateChickenDisplay() {
        updateChickenSlot(0, chickenImage1, chickenSlotText1)
        updateChickenSlot(1, chickenImage2, chickenSlotText2)
        updateChickenSlot(2, chickenImage3, chickenSlotText3)
    }

    private fun updateChickenSlot(
        slotIndex: Int,
        imageView: ImageView,
        textView: TextView
    ) {
        if (slotIndex < GameManager.equippedChickens.size) {
            val chicken = GameManager.equippedChickens[slotIndex]

            imageView.setImageResource(chicken.imageResId)
            imageView.visibility = View.VISIBLE

            textView.text = chicken.name
            RarityTextStyler.apply(textView, chicken.rarity)

            return
        }

        imageView.setImageDrawable(null)
        imageView.visibility = View.INVISIBLE

        RarityTextStyler.apply(textView, Rarity.COMMON)

        if (slotIndex < GameManager.maxChickenSlots) {
            textView.text = "Empty Slot"
            textView.setTextColor(Color.parseColor("#57351F"))
        } else {
            textView.text = "🔒 Locked"
            textView.setTextColor(Color.parseColor("#795638"))
        }
    }

    private fun showDeployManagerPopup() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_manage_chickens)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val container =
            dialog.findViewById<LinearLayout>(R.id.ownedChickenContainer)

        val slotStatus =
            dialog.findViewById<TextView>(R.id.deploySlotStatusText)

        val closeButton =
            dialog.findViewById<Button>(R.id.closeDeployManagerButton)

        slotStatus.text =
            "${GameManager.equippedChickens.size} / ${GameManager.maxChickenSlots} Chickens Deployed"

        for (chicken in GameManager.ownedChickens) {
            val row = layoutInflater.inflate(
                R.layout.item_replace_chicken,
                container,
                false
            )

            val image =
                row.findViewById<ImageView>(R.id.replaceChickenImage)

            val name =
                row.findViewById<TextView>(R.id.replaceChickenName)

            val stats =
                row.findViewById<TextView>(R.id.replaceChickenStats)

            val button =
                row.findViewById<Button>(R.id.replaceChickenButton)

            image.setImageResource(chicken.imageResId)

            name.text = chicken.name
            RarityTextStyler.apply(name, chicken.rarity)

            stats.text =
                "Level ${chicken.upgradeLevel} • " +
                        "${formatNumber(GameManager.getChickenProduction(chicken))} Eggs"

            if (GameManager.isEquipped(chicken)) {
                button.text = "ACTIVE"
                button.isEnabled = false
                button.alpha = 0.55f
            } else {
                button.text = "DEPLOY"
                button.isEnabled = true
                button.alpha = 1f

                addButtonPressAnimation(button)

                button.setOnClickListener {
                    if (GameManager.equippedChickens.size < GameManager.maxChickenSlots) {
                        if (GameManager.equipChicken(chicken)) {
                            dialog.dismiss()
                            updateScreen()
                            startChickenIdleAnimations()

                            Toast.makeText(
                                this,
                                "${chicken.name} deployed!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        dialog.dismiss()
                        showReplaceChickenPopup(chicken)
                    }
                }
            }

            container.addView(row)
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        addButtonPressAnimation(closeButton)

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun showReplaceChickenPopup(newChicken: Chicken) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_replace_chicken)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val newChickenImage =
            dialog.findViewById<ImageView>(R.id.newChickenImage)

        val newChickenName =
            dialog.findViewById<TextView>(R.id.newChickenName)

        val newChickenRarity =
            dialog.findViewById<TextView>(R.id.newChickenRarity)

        val replacementContainer =
            dialog.findViewById<LinearLayout>(R.id.replacementContainer)

        val cancelButton =
            dialog.findViewById<Button>(R.id.cancelDeployButton)

        newChickenImage.setImageResource(newChicken.imageResId)

        newChickenName.text = newChicken.name
        newChickenRarity.text = newChicken.rarity.displayName

        RarityTextStyler.apply(newChickenName, newChicken.rarity)
        RarityTextStyler.apply(newChickenRarity, newChicken.rarity)

        for (index in GameManager.equippedChickens.indices) {
            val oldChicken = GameManager.equippedChickens[index]

            val row = layoutInflater.inflate(
                R.layout.item_replace_chicken,
                replacementContainer,
                false
            )

            val image =
                row.findViewById<ImageView>(R.id.replaceChickenImage)

            val name =
                row.findViewById<TextView>(R.id.replaceChickenName)

            val stats =
                row.findViewById<TextView>(R.id.replaceChickenStats)

            val replaceButton =
                row.findViewById<Button>(R.id.replaceChickenButton)

            image.setImageResource(oldChicken.imageResId)

            name.text = oldChicken.name
            RarityTextStyler.apply(name, oldChicken.rarity)

            stats.text =
                "Level ${oldChicken.upgradeLevel} • " +
                        "${formatNumber(GameManager.getChickenProduction(oldChicken))} Eggs"

            replaceButton.text = "REPLACE"

            addButtonPressAnimation(replaceButton)

            replaceButton.setOnClickListener {
                if (GameManager.replaceEquippedChicken(index, newChicken)) {
                    dialog.dismiss()

                    updateScreen()
                    startChickenIdleAnimations()

                    Toast.makeText(
                        this,
                        "${newChicken.name} deployed!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            replacementContainer.addView(row)
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        addButtonPressAnimation(cancelButton)

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun showRebirthPopup() {
        if (!GameManager.canRebirth()) {
            return
        }

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_rebirth)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val root =
            dialog.findViewById<View>(R.id.rebirthPopupRoot)

        val currentMultiplierText =
            dialog.findViewById<TextView>(R.id.currentRebirthMultiplierText)

        val nextMultiplierText =
            dialog.findViewById<TextView>(R.id.nextRebirthMultiplierText)

        val exampleText =
            dialog.findViewById<TextView>(R.id.rebirthExampleText)

        val costText =
            dialog.findViewById<TextView>(R.id.rebirthCostText)

        val cancelButton =
            dialog.findViewById<Button>(R.id.cancelRebirthButton)

        val confirmButton =
            dialog.findViewById<Button>(R.id.confirmRebirthButton)

        val currentMultiplier = GameManager.getRebirthMultiplier()
        val nextMultiplier =
            GameManager.getNextRebirthMultiplier() ?: currentMultiplier
        val cost = GameManager.getNextRebirthCost() ?: 0

        currentMultiplierText.text = "x$currentMultiplier"
        nextMultiplierText.text = "x$nextMultiplier"
        exampleText.text = "1 egg → $nextMultiplier eggs per chicken"
        costText.text = "Requires: 🥚 ${formatNumber(cost)}"

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        confirmButton.isEnabled = GameManager.canRebirth()
        confirmButton.alpha = if (confirmButton.isEnabled) 1f else 0.55f

        confirmButton.setOnClickListener {
            if (GameManager.performRebirth()) {
                dialog.dismiss()

                updateScreen()

                handler.removeCallbacks(eggRunnable)
                handler.post(eggRunnable)

                showRebirthSuccessFeedback()
            }
        }

        addButtonPressAnimation(cancelButton)
        addButtonPressAnimation(confirmButton)

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.88).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        root.scaleX = 0.72f
        root.scaleY = 0.72f
        root.alpha = 0f

        root.animate()
            .scaleX(1.05f)
            .scaleY(1.05f)
            .alpha(1f)
            .setDuration(220)
            .withEndAction {
                root.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(130)
                    .start()
            }
            .start()
    }

    private fun updateRebirthButton() {
        if (GameManager.rebirthLevel >= GameManager.MAX_REBIRTHS) {
            rebirthButton.text = "⭐ MAX REBIRTH"
            rebirthButton.isEnabled = false
            rebirthButton.alpha = 0.6f
            return
        }

        val canRebirth = GameManager.canRebirth()

        rebirthButton.text =
            if (canRebirth) "⭐ REBIRTH" else "🔒 REBIRTH"

        rebirthButton.isEnabled = canRebirth
        rebirthButton.alpha = if (canRebirth) 1f else 0.58f
    }

    private fun updateRebirthBadge() {
        val level = GameManager.rebirthLevel

        farmRankText.text =
            "x${GameManager.getRebirthMultiplier()}"

        if (displayedRebirthLevel == level) {
            return
        }

        val fillColor = when (level) {
            0 -> "#9A5A2B"
            1 -> "#4E8C4A"
            2 -> "#3977A8"
            3 -> "#69519E"
            4 -> "#8E4E7B"
            5 -> "#A85F32"
            6 -> "#9A761E"
            7 -> "#347D76"
            8 -> "#A14545"
            9 -> "#49498D"
            else -> "#252525"
        }

        val strokeColor =
            if (level == GameManager.MAX_REBIRTHS) "#FFD34E" else "#FFF1CF"

        farmRankText.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(17).toFloat()
            setColor(Color.parseColor(fillColor))
            setStroke(
                dpToPx(3),
                Color.parseColor(strokeColor)
            )
        }

        displayedRebirthLevel = level
    }

    private fun showRebirthSuccessFeedback() {
        Toast.makeText(
            this,
            "⭐ Rebirth ${GameManager.rebirthLevel}! " +
                    "Permanent x${GameManager.getRebirthMultiplier()} boost!",
            Toast.LENGTH_LONG
        ).show()

        farmRankText.animate().cancel()

        farmRankText.scaleX = 0.7f
        farmRankText.scaleY = 0.7f

        farmRankText.animate()
            .scaleX(1.25f)
            .scaleY(1.25f)
            .setDuration(200)
            .withEndAction {
                farmRankText.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(180)
                    .start()
            }
            .start()
    }

    private fun showEggProductionAnimations() {
        val slots = listOf(
            chickenSlot1,
            chickenSlot2,
            chickenSlot3
        )

        for (index in GameManager.equippedChickens.indices) {
            if (index >= slots.size) {
                break
            }

            val chicken = GameManager.equippedChickens[index]

            showEggFloat(
                slots[index],
                GameManager.getChickenProduction(chicken)
            )
        }
    }

    private fun showEggFloat(
        slot: FrameLayout,
        amount: Int
    ) {
        val eggText = TextView(this).apply {
            text = "+${formatNumber(amount)} 🥚"
            textSize = 18f

            setTextColor(
                Color.parseColor("#FFD34E")
            )

            typeface = resources.getFont(
                R.font.fredoka_bold
            )

            setShadowLayer(
                5f,
                1f,
                2f,
                Color.parseColor("#57351F")
            )
        }

        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            bottomMargin = dpToPx(65)
        }

        slot.addView(eggText, params)

        val sidewaysMovement =
            dpToPx(Random.nextInt(-12, 13)).toFloat()

        val riseAnimation = ObjectAnimator.ofFloat(
            eggText,
            View.TRANSLATION_Y,
            0f,
            -dpToPx(105).toFloat()
        )

        val sidewaysAnimation = ObjectAnimator.ofFloat(
            eggText,
            View.TRANSLATION_X,
            0f,
            sidewaysMovement
        )

        val fadeAnimation = ObjectAnimator.ofFloat(
            eggText,
            View.ALPHA,
            1f,
            1f,
            0f
        )

        val scaleX = ObjectAnimator.ofFloat(
            eggText,
            View.SCALE_X,
            0.75f,
            1.18f,
            1f
        )

        val scaleY = ObjectAnimator.ofFloat(
            eggText,
            View.SCALE_Y,
            0.75f,
            1.18f,
            1f
        )

        AnimatorSet().apply {
            playTogether(
                riseAnimation,
                sidewaysAnimation,
                fadeAnimation,
                scaleX,
                scaleY
            )

            duration = 1500
            interpolator = DecelerateInterpolator()

            addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        slot.removeView(eggText)
                    }
                }
            )

            start()
        }
    }

    private fun startChickenIdleAnimations() {
        stopChickenIdleAnimations()

        val images = listOf(
            chickenImage1,
            chickenImage2,
            chickenImage3
        )

        for (index in images.indices) {
            if (index >= GameManager.equippedChickens.size) {
                break
            }

            val image = images[index]
            val baseY = chickenBaseTranslations[index]

            val animator = ObjectAnimator.ofFloat(
                image,
                View.TRANSLATION_Y,
                baseY,
                baseY - dpToPx(6).toFloat(),
                baseY
            ).apply {
                duration = 1800
                startDelay = (index * 180L)
                repeatCount = ValueAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }

            animator.start()
            idleAnimators.add(animator)
        }
    }

    private fun stopChickenIdleAnimations() {
        idleAnimators.forEach {
            it.cancel()
        }

        idleAnimators.clear()

        if (::chickenImage1.isInitialized) {
            chickenImage1.translationY = chickenBaseTranslations[0]
            chickenImage2.translationY = chickenBaseTranslations[1]
            chickenImage3.translationY = chickenBaseTranslations[2]
        }
    }

    private fun animateMoodBar() {
        moodBar.animate().cancel()

        moodBar.animate()
            .scaleY(1.35f)
            .setDuration(120)
            .withEndAction {
                moodBar.animate()
                    .scaleY(1f)
                    .setDuration(160)
                    .start()
            }
            .start()
    }

    private fun animateEggCounter() {
        eggCountText.animate().cancel()

        eggCountText.scaleX = 1f
        eggCountText.scaleY = 1f

        eggCountText.animate()
            .scaleX(1.07f)
            .scaleY(1.07f)
            .setDuration(90)
            .withEndAction {
                eggCountText.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .start()
            }
            .start()
    }

    private fun addButtonPressAnimation(button: View) {
        button.setOnTouchListener { view, event ->
            if (!view.isEnabled) {
                return@setOnTouchListener false
            }

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate().cancel()

                    view.animate()
                        .scaleX(0.92f)
                        .scaleY(0.92f)
                        .setDuration(80)
                        .start()
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    view.animate().cancel()

                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                        .start()
                }
            }

            false
        }
    }

    private fun formatNumber(number: Int): String {
        return when {
            number >= 1_000_000_000 ->
                String.format("%.1fB", number / 1_000_000_000.0)

            number >= 1_000_000 ->
                String.format("%.1fM", number / 1_000_000.0)

            number >= 1_000 ->
                String.format("%.1fK", number / 1_000.0)

            else -> number.toString()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}