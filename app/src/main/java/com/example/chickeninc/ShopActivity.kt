package com.example.chickeninc

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ShopActivity : AppCompatActivity() {

    private lateinit var chickenGrid: GridLayout

    private lateinit var chickensContent: LinearLayout
    private lateinit var gadgetsContent: LinearLayout

    private lateinit var shopTitleText: TextView
    private lateinit var shopEggText: TextView

    private lateinit var previewImage: ImageView
    private lateinit var previewName: TextView
    private lateinit var previewRarity: TextView
    private lateinit var previewStats: TextView
    private lateinit var previewPrice: TextView

    private lateinit var feederStockText: TextView
    private lateinit var feederRestockCostText: TextView

    private lateinit var backButton: Button
    private lateinit var chickensTab: Button
    private lateinit var gadgetsTab: Button
    private lateinit var buyButton: Button
    private lateinit var deployButton: Button
    private lateinit var restockFeederButton: Button

    private var selectedChicken = GameManager.basicChicken
    private var showingGadgets = false

    private var previewBobAnimator: ObjectAnimator? = null
    private var buyPulseAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        bindViews()
        setListeners()
        setButtonAnimations()

        showChickenTab(false)
    }

    override fun onResume() {
        super.onResume()

        updateEggDisplay()

        if (showingGadgets) {
            updateFeederRestock()
        } else {
            populateChickenGrid(false)
            updatePreview()
            startPreviewBob()
        }
    }

    override fun onPause() {
        super.onPause()

        stopPreviewBob()
        stopBuyPulse()
    }

    override fun onDestroy() {
        stopPreviewBob()
        stopBuyPulse()

        super.onDestroy()
    }

    private fun bindViews() {
        chickenGrid = findViewById(R.id.chickenGrid)

        chickensContent = findViewById(R.id.chickensContent)
        gadgetsContent = findViewById(R.id.gadgetsContent)

        shopTitleText = findViewById(R.id.shopTitleText)
        shopEggText = findViewById(R.id.shopEggText)

        previewImage = findViewById(R.id.previewImage)
        previewName = findViewById(R.id.previewName)
        previewRarity = findViewById(R.id.previewRarity)
        previewStats = findViewById(R.id.previewStats)
        previewPrice = findViewById(R.id.previewPrice)

        feederStockText = findViewById(R.id.feederStockText)
        feederRestockCostText = findViewById(R.id.feederRestockCostText)

        backButton = findViewById(R.id.backButton)
        chickensTab = findViewById(R.id.chickensTab)
        gadgetsTab = findViewById(R.id.gadgetsTab)
        buyButton = findViewById(R.id.buyButton)
        deployButton = findViewById(R.id.deployButton)
        restockFeederButton = findViewById(R.id.restockFeederButton)
    }

    private fun setListeners() {
        backButton.setOnClickListener {
            finish()
        }

        chickensTab.setOnClickListener {
            showChickenTab()
        }

        gadgetsTab.setOnClickListener {
            showGadgetsTab()
        }

        buyButton.setOnClickListener {
            buySelectedChicken()
        }

        deployButton.setOnClickListener {
            handleDeployButton()
        }

        restockFeederButton.setOnClickListener {
            restockFeeder()
        }
    }

    private fun setButtonAnimations() {
        listOf(
            backButton,
            chickensTab,
            gadgetsTab,
            buyButton,
            deployButton,
            restockFeederButton
        ).forEach {
            addButtonPressAnimation(it)
        }
    }

    private fun showChickenTab(animate: Boolean = true) {
        showingGadgets = false

        shopTitleText.text = "🐔 CHICKEN SHOP!"

        chickensTab.setBackgroundResource(
            R.drawable.shop_tab_selected_bg
        )

        gadgetsTab.setBackgroundResource(
            R.drawable.shop_tab_unselected_bg
        )

        if (animate) {
            showContentAnimated(
                chickensContent,
                gadgetsContent,
                false
            )
        } else {
            gadgetsContent.visibility = View.GONE
            chickensContent.visibility = View.VISIBLE
            chickensContent.alpha = 1f
            chickensContent.translationX = 0f
        }

        updateEggDisplay()
        populateChickenGrid()
        updatePreview()
        startPreviewBob()
    }

    private fun showGadgetsTab() {
        showingGadgets = true

        shopTitleText.text = "⚡ GADGET SHOP!"

        chickensTab.setBackgroundResource(
            R.drawable.shop_tab_unselected_bg
        )

        gadgetsTab.setBackgroundResource(
            R.drawable.shop_tab_selected_bg
        )

        showContentAnimated(
            gadgetsContent,
            chickensContent,
            true
        )

        stopPreviewBob()
        stopBuyPulse()

        updateEggDisplay()
        updateFeederRestock()
    }

    private fun showContentAnimated(
        showView: View,
        hideView: View,
        moveFromRight: Boolean
    ) {
        showView.animate().cancel()
        hideView.animate().cancel()

        hideView.visibility = View.GONE
        showView.visibility = View.VISIBLE

        showView.alpha = 0f

        showView.translationX =
            if (moveFromRight) {
                dpToPx(30).toFloat()
            } else {
                -dpToPx(30).toFloat()
            }

        showView.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(220)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun populateChickenGrid(
        animateCards: Boolean = true
    ) {
        chickenGrid.removeAllViews()

        for (index in GameManager.shopChickens.indices) {
            val chicken = GameManager.shopChickens[index]

            val card = layoutInflater.inflate(
                R.layout.item_chicken_shop,
                chickenGrid,
                false
            )

            val image =
                card.findViewById<ImageView>(
                    R.id.cardChickenImage
                )

            val name =
                card.findViewById<TextView>(
                    R.id.cardNameText
                )

            val rarity =
                card.findViewById<TextView>(
                    R.id.cardRarityText
                )

            val price =
                card.findViewById<TextView>(
                    R.id.cardPriceText
                )

            image.setImageResource(chicken.imageResId)

            name.text = chicken.name
            rarity.text = chicken.rarity.displayName

            RarityTextStyler.apply(
                name,
                chicken.rarity
            )

            RarityTextStyler.apply(
                rarity,
                chicken.rarity
            )

            when {
                GameManager.isEquipped(chicken) -> {
                    price.text = "ACTIVE"

                    price.setTextColor(
                        Color.parseColor("#3F8A47")
                    )
                }

                GameManager.isOwned(chicken) -> {
                    price.text = "OWNED"

                    price.setTextColor(
                        Color.parseColor("#3F8A47")
                    )
                }

                else -> {
                    price.text =
                        "🥚 ${formatNumber(chicken.shopPrice)}"

                    price.setTextColor(
                        Color.parseColor("#57351F")
                    )
                }
            }

            card.setOnClickListener {
                selectedChicken = chicken

                animateCardSelection(card)
                updatePreview()
            }

            chickenGrid.addView(card)

            if (animateCards) {
                card.alpha = 0f
                card.translationY =
                    dpToPx(25).toFloat()

                card.postDelayed(
                    {
                        card.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(220)
                            .setInterpolator(
                                DecelerateInterpolator()
                            )
                            .start()
                    },
                    index * 45L
                )
            }
        }
    }

    private fun updatePreview() {
        val chicken = selectedChicken

        previewImage.setImageResource(
            chicken.imageResId
        )

        previewName.text =
            chicken.name

        previewRarity.text =
            chicken.rarity.displayName

        RarityTextStyler.apply(
            previewName,
            chicken.rarity
        )

        RarityTextStyler.apply(
            previewRarity,
            chicken.rarity
        )

        previewStats.text =
            "Base Production: ${chicken.baseProduction}\n" +
                    "Max Level: ${chicken.maxUpgradeLevel}\n" +
                    "Max Multiplier: x${chicken.maxMultiplier}"

        when {
            !GameManager.isOwned(chicken) -> {
                previewPrice.text =
                    "🥚 ${formatNumber(chicken.shopPrice)} Eggs"

                buyButton.text = "🛒 BUY"
                buyButton.isEnabled = true
                buyButton.alpha = 1f

                deployButton.text = "DEPLOY"
                deployButton.isEnabled = false
                deployButton.alpha = 0.5f
            }

            GameManager.isEquipped(chicken) -> {
                previewPrice.text =
                    "Owned • Active"

                buyButton.text = "OWNED"
                buyButton.isEnabled = false
                buyButton.alpha = 0.5f

                deployButton.text = "ACTIVE"
                deployButton.isEnabled = false
                deployButton.alpha = 0.55f
            }

            else -> {
                previewPrice.text =
                    "Owned"

                buyButton.text = "OWNED"
                buyButton.isEnabled = false
                buyButton.alpha = 0.5f

                deployButton.text = "🐔 DEPLOY"
                deployButton.isEnabled = true
                deployButton.alpha = 1f
            }
        }

        updateBuyPulse()
        animatePreviewChange()
        updateEggDisplay()
    }

    private fun buySelectedChicken() {
        val chicken = selectedChicken

        if (GameManager.isOwned(chicken)) {
            showShopPopup(
                "ALREADY OWNED!",
                "${chicken.name} is already in your collection.",
                chicken,
                false
            )

            return
        }

        if (GameManager.eggs < chicken.shopPrice) {
            val missing =
                chicken.shopPrice - GameManager.eggs

            showShopPopup(
                "NOT ENOUGH EGGS!",
                "You need 🥚 ${formatNumber(missing)} more eggs.",
                chicken,
                false
            )

            return
        }

        if (!GameManager.buyChicken(chicken)) {
            return
        }

        animateEggCounter()

        showShopPopup(
            "🎉 PURCHASED! 🎉",
            "${chicken.name} was added to your collection!",
            chicken,
            true
        )

        refreshShop()
    }

    private fun handleDeployButton() {
        val chicken = selectedChicken

        if (!GameManager.isOwned(chicken)) {
            showShopPopup(
                "LOCKED!",
                "Buy ${chicken.name} before deploying it.",
                chicken,
                false
            )

            return
        }

        if (GameManager.isEquipped(chicken)) {
            showShopPopup(
                "ALREADY ACTIVE!",
                "${chicken.name} is already working on your farm.",
                chicken,
                false
            )

            return
        }

        if (
            GameManager.equippedChickens.size <
            GameManager.maxChickenSlots
        ) {
            if (GameManager.equipChicken(chicken)) {
                showShopPopup(
                    "🐔 DEPLOYED!",
                    "${chicken.name} is now working on your farm!",
                    chicken,
                    true
                )

                refreshShop()
            }

            return
        }

        showReplaceChickenPopup(chicken)
    }

    private fun updateFeederRestock() {
        val feeder = GameManager.feeder
        val capacity = feeder.getCapacity()

        val missingFood =
            (capacity - feeder.currentFood)
                .coerceAtLeast(0)

        feederStockText.text =
            "Food: ${feeder.currentFood} / $capacity"

        if (missingFood == 0) {
            feederRestockCostText.text =
                "Feeder is full!"

            feederRestockCostText.setTextColor(
                Color.parseColor("#3F8A47")
            )

            restockFeederButton.text =
                "✓ FULL"

            restockFeederButton.isEnabled =
                false

            restockFeederButton.alpha =
                0.55f

            return
        }

        val cost =
            missingFood *
                    RESTOCK_COST_PER_FOOD

        feederRestockCostText.text =
            "Restock Cost: 🥚 ${formatNumber(cost)}"

        if (GameManager.eggs >= cost) {
            feederRestockCostText.setTextColor(
                Color.parseColor("#57351F")
            )
        } else {
            feederRestockCostText.setTextColor(
                Color.parseColor("#A24A3A")
            )
        }

        restockFeederButton.text =
            "🌽 RESTOCK"

        restockFeederButton.isEnabled =
            true

        restockFeederButton.alpha =
            1f
    }

    private fun restockFeeder() {
        val feeder = GameManager.feeder
        val capacity = feeder.getCapacity()

        val missingFood =
            (capacity - feeder.currentFood)
                .coerceAtLeast(0)

        if (missingFood == 0) {
            showShopPopup(
                "FEEDER FULL!",
                "Your feeder is already completely stocked.",
                null,
                false
            )

            return
        }

        val cost =
            missingFood *
                    RESTOCK_COST_PER_FOOD

        if (GameManager.eggs < cost) {
            val missingEggs =
                cost - GameManager.eggs

            showShopPopup(
                "NOT ENOUGH EGGS!",
                "You need 🥚 ${formatNumber(missingEggs)} more eggs.",
                null,
                false
            )

            return
        }

        GameManager.eggs -= cost

        feeder.addFood(missingFood)

        animateEggCounter()
        updateEggDisplay()
        updateFeederRestock()

        showShopPopup(
            "🌽 RESTOCKED!",
            "Your feeder is now ${feeder.currentFood} / ${feeder.getCapacity()} full.",
            null,
            true
        )
    }

    private fun showReplaceChickenPopup(
        newChicken: Chicken
    ) {
        val dialog = Dialog(this)

        dialog.setContentView(
            R.layout.dialog_replace_chicken
        )

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        val newChickenImage =
            dialog.findViewById<ImageView>(
                R.id.newChickenImage
            )

        val newChickenName =
            dialog.findViewById<TextView>(
                R.id.newChickenName
            )

        val newChickenRarity =
            dialog.findViewById<TextView>(
                R.id.newChickenRarity
            )

        val replacementContainer =
            dialog.findViewById<LinearLayout>(
                R.id.replacementContainer
            )

        val cancelButton =
            dialog.findViewById<Button>(
                R.id.cancelDeployButton
            )

        newChickenImage.setImageResource(
            newChicken.imageResId
        )

        newChickenName.text =
            newChicken.name

        newChickenRarity.text =
            newChicken.rarity.displayName

        RarityTextStyler.apply(
            newChickenName,
            newChicken.rarity
        )

        RarityTextStyler.apply(
            newChickenRarity,
            newChicken.rarity
        )

        for (index in GameManager.equippedChickens.indices) {
            val currentChicken =
                GameManager.equippedChickens[index]

            val row = layoutInflater.inflate(
                R.layout.item_replace_chicken,
                replacementContainer,
                false
            )

            val image =
                row.findViewById<ImageView>(
                    R.id.replaceChickenImage
                )

            val name =
                row.findViewById<TextView>(
                    R.id.replaceChickenName
                )

            val stats =
                row.findViewById<TextView>(
                    R.id.replaceChickenStats
                )

            val replaceButton =
                row.findViewById<Button>(
                    R.id.replaceChickenButton
                )

            image.setImageResource(
                currentChicken.imageResId
            )

            name.text =
                currentChicken.name

            RarityTextStyler.apply(
                name,
                currentChicken.rarity
            )

            stats.text =
                "Level ${currentChicken.upgradeLevel} • " +
                        "${formatNumber(GameManager.getChickenProduction(currentChicken))} Eggs"

            replaceButton.text =
                "REPLACE"

            addButtonPressAnimation(
                replaceButton
            )

            replaceButton.setOnClickListener {
                val replaced =
                    GameManager.replaceEquippedChicken(
                        index,
                        newChicken
                    )

                if (replaced) {
                    dialog.dismiss()
                    refreshShop()

                    showShopPopup(
                        "🐔 DEPLOYED!",
                        "${newChicken.name} replaced ${currentChicken.name}.",
                        newChicken,
                        true
                    )
                }
            }

            replacementContainer.addView(row)
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        addButtonPressAnimation(
            cancelButton
        )

        dialog.show()

        dialog.window?.setLayout(
            (
                    resources.displayMetrics.widthPixels *
                            0.90
                    ).toInt(),

            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun showShopPopup(
        title: String,
        message: String,
        chicken: Chicken?,
        success: Boolean
    ) {
        val dialog = Dialog(this)

        dialog.setContentView(
            R.layout.dialog_shop_message
        )

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        val root =
            dialog.findViewById<View>(
                R.id.shopPopupRoot
            )

        val popupTitle =
            dialog.findViewById<TextView>(
                R.id.shopPopupTitle
            )

        val popupImage =
            dialog.findViewById<ImageView>(
                R.id.shopPopupImage
            )

        val popupChickenName =
            dialog.findViewById<TextView>(
                R.id.shopPopupChickenName
            )

        val popupMessage =
            dialog.findViewById<TextView>(
                R.id.shopPopupMessage
            )

        val popupButton =
            dialog.findViewById<Button>(
                R.id.shopPopupButton
            )

        popupTitle.text =
            title

        popupMessage.text =
            message

        if (chicken != null) {
            popupImage.visibility =
                View.VISIBLE

            popupChickenName.visibility =
                View.VISIBLE

            popupImage.setImageResource(
                chicken.imageResId
            )

            popupChickenName.text =
                chicken.name

            RarityTextStyler.apply(
                popupChickenName,
                chicken.rarity
            )
        } else {
            popupImage.visibility =
                View.GONE

            popupChickenName.visibility =
                View.GONE
        }

        if (success) {
            popupButton.text =
                "AWESOME!"

            popupButton.setBackgroundResource(
                R.drawable.game_buy_button_bg
            )
        } else {
            popupButton.text =
                "OK"

            popupButton.setBackgroundResource(
                R.drawable.game_back_button_bg
            )
        }

        popupButton.backgroundTintList =
            null

        popupButton.setOnClickListener {
            dialog.dismiss()
        }

        addButtonPressAnimation(
            popupButton
        )

        dialog.show()

        dialog.window?.setLayout(
            (
                    resources.displayMetrics.widthPixels *
                            0.86
                    ).toInt(),

            WindowManager.LayoutParams.WRAP_CONTENT
        )

        root.scaleX = 0.68f
        root.scaleY = 0.68f
        root.alpha = 0f

        root.animate()
            .scaleX(1.06f)
            .scaleY(1.06f)
            .alpha(1f)
            .setDuration(220)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                root.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(130)
                    .start()
            }
            .start()

        if (chicken != null) {
            popupImage.translationY =
                -dpToPx(20).toFloat()

            popupImage.alpha =
                0f

            popupImage.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(300)
                .setStartDelay(100)
                .setInterpolator(
                    DecelerateInterpolator()
                )
                .start()
        }
    }

    private fun animateCardSelection(card: View) {
        card.animate().cancel()

        card.animate()
            .scaleX(1.07f)
            .scaleY(1.07f)
            .setDuration(100)
            .withEndAction {
                card.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun animatePreviewChange() {
        previewImage.animate().cancel()

        previewImage.scaleX = 0.78f
        previewImage.scaleY = 0.78f
        previewImage.alpha = 0.4f

        previewImage.animate()
            .scaleX(1.08f)
            .scaleY(1.08f)
            .alpha(1f)
            .setDuration(180)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                previewImage.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .start()
            }
            .start()
    }

    private fun updateBuyPulse() {
        val shouldPulse =
            !GameManager.isOwned(selectedChicken) &&
                    GameManager.eggs >= selectedChicken.shopPrice &&
                    buyButton.isEnabled

        if (shouldPulse) {
            startBuyPulse()
        } else {
            stopBuyPulse()
        }
    }

    private fun startBuyPulse() {
        if (buyPulseAnimator?.isRunning == true) {
            return
        }

        buyPulseAnimator =
            ValueAnimator.ofFloat(
                1f,
                1.05f
            ).apply {
                duration = 550
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator =
                    AccelerateDecelerateInterpolator()

                addUpdateListener {
                    val scale =
                        it.animatedValue as Float

                    buyButton.scaleX =
                        scale

                    buyButton.scaleY =
                        scale
                }

                start()
            }
    }

    private fun stopBuyPulse() {
        buyPulseAnimator?.cancel()
        buyPulseAnimator = null

        if (::buyButton.isInitialized) {
            buyButton.scaleX = 1f
            buyButton.scaleY = 1f
        }
    }

    private fun startPreviewBob() {
        if (previewBobAnimator?.isRunning == true) {
            return
        }

        previewBobAnimator =
            ObjectAnimator.ofFloat(
                previewImage,
                View.TRANSLATION_Y,
                0f,
                -dpToPx(5).toFloat(),
                0f
            ).apply {
                duration = 1800
                repeatCount =
                    ValueAnimator.INFINITE

                interpolator =
                    AccelerateDecelerateInterpolator()

                start()
            }
    }

    private fun stopPreviewBob() {
        previewBobAnimator?.cancel()
        previewBobAnimator = null

        if (::previewImage.isInitialized) {
            previewImage.translationY = 0f
        }
    }

    private fun animateEggCounter() {
        shopEggText.animate().cancel()

        shopEggText.scaleX = 1f
        shopEggText.scaleY = 1f

        shopEggText.animate()
            .scaleX(1.12f)
            .scaleY(1.12f)
            .setDuration(100)
            .withEndAction {
                shopEggText.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
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

                    if (view === buyButton) {
                        stopBuyPulse()
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
                            if (view === buyButton) {
                                updateBuyPulse()
                            }
                        }
                        .start()
                }
            }

            false
        }
    }

    private fun refreshShop() {
        updateEggDisplay()
        populateChickenGrid(false)
        updatePreview()
    }

    private fun updateEggDisplay() {
        shopEggText.text =
            "🥚 ${formatNumber(GameManager.eggs)}"
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

    private companion object {
        const val RESTOCK_COST_PER_FOOD = 25
    }
}