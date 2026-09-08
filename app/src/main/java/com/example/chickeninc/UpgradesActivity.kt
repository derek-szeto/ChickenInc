package com.example.chickeninc

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton


class UpgradesActivity : AppCompatActivity() {

    // =========================
    // GENERAL
    // =========================

    private lateinit var eggCountText: TextView
    private lateinit var backButton: Button


    // =========================
    // TABS
    // =========================

    private lateinit var chickenUpgradeTab: Button
    private lateinit var feederUpgradeTab: Button
    private lateinit var slotUpgradeTab: Button

    private lateinit var chickenUpgradeContent: ScrollView
    private lateinit var feederUpgradeContent: ScrollView
    private lateinit var slotUpgradeContent: ScrollView


    // =========================
    // CHICKEN UPGRADES
    // =========================

    private lateinit var ownedChickenSelector: LinearLayout

    private lateinit var upgradeChickenImage: ImageView

    private lateinit var chickenNameText: TextView
    private lateinit var rarityText: TextView
    private lateinit var levelText: TextView
    private lateinit var multiplierText: TextView
    private lateinit var productionText: TextView
    private lateinit var nextUpgradeText: TextView
    private lateinit var upgradeCostText: TextView

    private lateinit var upgradeButton: Button

    private var selectedChickenIndex = 0


    // =========================
    // FEEDER
    // =========================

    private lateinit var feederLevelText: TextView
    private lateinit var feederLevelDots: TextView
    private lateinit var feederStatsText: TextView
    private lateinit var upgradeFeederButton: Button


    // =========================
    // FARM SLOTS
    // =========================

    private lateinit var slotCountText: TextView

    private lateinit var slot1CardText: TextView
    private lateinit var slot2CardText: TextView
    private lateinit var slot3CardText: TextView

    private lateinit var nextSlotText: TextView
    private lateinit var slotUpgradeCostText: TextView

    private lateinit var upgradeSlotButton: Button


    // =========================
    // CURRENT TAB
    // =========================

    private enum class UpgradeTab {
        CHICKENS,
        FEEDER,
        SLOTS
    }

    private var currentTab =
        UpgradeTab.CHICKENS


    // =====================================================
    // ON CREATE
    // =====================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_upgrades
        )


        // =========================
        // GENERAL
        // =========================

        eggCountText =
            findViewById(R.id.eggCountText)

        backButton =
            findViewById(R.id.backButton)


        // =========================
        // TABS
        // =========================

        chickenUpgradeTab =
            findViewById(R.id.chickenUpgradeTab)

        feederUpgradeTab =
            findViewById(R.id.feederUpgradeTab)

        slotUpgradeTab =
            findViewById(R.id.slotUpgradeTab)


        chickenUpgradeContent =
            findViewById(R.id.chickenUpgradeContent)

        feederUpgradeContent =
            findViewById(R.id.feederUpgradeContent)

        slotUpgradeContent =
            findViewById(R.id.slotUpgradeContent)


        // =========================
        // CHICKEN
        // =========================

        ownedChickenSelector =
            findViewById(R.id.ownedChickenSelector)

        upgradeChickenImage =
            findViewById(R.id.upgradeChickenImage)

        chickenNameText =
            findViewById(R.id.chickenNameText)

        rarityText =
            findViewById(R.id.rarityText)

        levelText =
            findViewById(R.id.levelText)

        multiplierText =
            findViewById(R.id.multiplierText)

        productionText =
            findViewById(R.id.productionText)

        nextUpgradeText =
            findViewById(R.id.nextUpgradeText)

        upgradeCostText =
            findViewById(R.id.upgradeCostText)

        upgradeButton =
            findViewById(R.id.upgradeButton)


        // =========================
        // FEEDER
        // =========================

        feederLevelText =
            findViewById(R.id.feederLevelText)

        feederLevelDots =
            findViewById(R.id.feederLevelDots)

        feederStatsText =
            findViewById(R.id.feederStatsText)

        upgradeFeederButton =
            findViewById(R.id.upgradeFeederButton)


        // =========================
        // SLOTS
        // =========================

        slotCountText =
            findViewById(R.id.slotCountText)

        slot1CardText =
            findViewById(R.id.slot1CardText)

        slot2CardText =
            findViewById(R.id.slot2CardText)

        slot3CardText =
            findViewById(R.id.slot3CardText)

        nextSlotText =
            findViewById(R.id.nextSlotText)

        slotUpgradeCostText =
            findViewById(R.id.slotUpgradeCostText)

        upgradeSlotButton =
            findViewById(R.id.upgradeSlotButton)


        // =========================
        // BACK
        // =========================

        backButton.setOnClickListener {

            finish()
        }


        // =========================
        // TAB BUTTONS
        // =========================

        chickenUpgradeTab.setOnClickListener {

            showTab(
                UpgradeTab.CHICKENS
            )
        }


        feederUpgradeTab.setOnClickListener {

            showTab(
                UpgradeTab.FEEDER
            )
        }


        slotUpgradeTab.setOnClickListener {

            showTab(
                UpgradeTab.SLOTS
            )
        }


        // =========================
        // UPGRADE BUTTONS
        // =========================

        upgradeButton.setOnClickListener {

            buyChickenUpgrade()
        }


        upgradeFeederButton.setOnClickListener {

            buyFeederUpgrade()
        }


        upgradeSlotButton.setOnClickListener {

            buySlotUpgrade()
        }


        // =========================
        // BUTTON ANIMATIONS
        // =========================

        addButtonPressAnimation(
            backButton
        )

        addButtonPressAnimation(
            chickenUpgradeTab
        )

        addButtonPressAnimation(
            feederUpgradeTab
        )

        addButtonPressAnimation(
            slotUpgradeTab
        )

        addButtonPressAnimation(
            upgradeButton
        )

        addButtonPressAnimation(
            upgradeFeederButton
        )

        addButtonPressAnimation(
            upgradeSlotButton
        )


        showTab(
            UpgradeTab.CHICKENS
        )


        updateScreen()
    }


    // =====================================================
    // SHOW TAB
    // =====================================================

    private fun showTab(
        tab: UpgradeTab
    ) {

        currentTab =
            tab


        chickenUpgradeContent.visibility =
            if (
                tab ==
                UpgradeTab.CHICKENS
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }


        feederUpgradeContent.visibility =
            if (
                tab ==
                UpgradeTab.FEEDER
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }


        slotUpgradeContent.visibility =
            if (
                tab ==
                UpgradeTab.SLOTS
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }


        updateTabGraphics()
    }


    // =====================================================
    // TAB COLORS
    // =====================================================

    private fun updateTabGraphics() {

        // CHICKENS
        if (
            currentTab ==
            UpgradeTab.CHICKENS
        ) {

            chickenUpgradeTab.setBackgroundResource(
                R.drawable.shop_tab_selected_bg
            )

        } else {

            chickenUpgradeTab.setBackgroundResource(
                R.drawable.shop_tab_unselected_bg
            )
        }


        // FEEDER
        if (
            currentTab ==
            UpgradeTab.FEEDER
        ) {

            feederUpgradeTab.setBackgroundResource(
                R.drawable.shop_tab_selected_bg
            )

        } else {

            feederUpgradeTab.setBackgroundResource(
                R.drawable.shop_tab_unselected_bg
            )
        }


        // SLOTS
        if (
            currentTab ==
            UpgradeTab.SLOTS
        ) {

            slotUpgradeTab.setBackgroundResource(
                R.drawable.shop_tab_selected_bg
            )

            slotUpgradeTab.setTextColor(
                Color.parseColor("#57351F")
            )

        } else {

            slotUpgradeTab.setBackgroundResource(
                R.drawable.game_buy_button_bg
            )

            slotUpgradeTab.setTextColor(
                Color.WHITE
            )
        }
    }


    // =====================================================
    // UPDATE EVERYTHING
    // =====================================================

    private fun updateScreen() {

        eggCountText.text =
            "🥚 ${formatNumber(GameManager.eggs)}"


        updateChickenSection()

        updateFeederSection()

        updateSlotSection()
    }


    // =====================================================
    // CHICKEN SELECTOR
    // =====================================================

    private fun rebuildChickenSelector() {

        ownedChickenSelector.removeAllViews()


        for (
        index in
        GameManager.ownedChickens.indices
        ) {

            val chicken =
                GameManager.ownedChickens[index]


            val button =
                AppCompatButton(this)
            button.maxLines =
                2

            button.gravity =
                Gravity.CENTER

            button.setPadding(
                dpToPx(8),
                0,
                dpToPx(8),
                0
            )

            button.text =
                chicken.name.replace(
                    " Chicken",
                    ""
                )


            button.textSize =
                16f


            button.setTextColor(
                Color.parseColor(
                    "#57351F"
                )
            )


            button.typeface =
                resources.getFont(
                    R.font.fredoka_bold
                )


            button.isAllCaps =
                true


            button.backgroundTintList =
                null


            if (
                index ==
                selectedChickenIndex
            ) {

                button.setBackgroundResource(
                    R.drawable.shop_tab_selected_bg
                )

            } else {

                button.setBackgroundResource(
                    R.drawable.shop_tab_unselected_bg
                )
            }


            val params =
                LinearLayout.LayoutParams(
                    dpToPx(140),
                    dpToPx(64)
                )


            params.setMargins(
                dpToPx(4),
                0,
                dpToPx(4),
                0
            )


            button.layoutParams =
                params


            addButtonPressAnimation(
                button
            )


            button.setOnClickListener {

                selectedChickenIndex =
                    index


                updateChickenSection()
            }


            ownedChickenSelector.addView(
                button
            )
        }
    }


    // =====================================================
    // CHICKEN SECTION
    // =====================================================

    private fun updateChickenSection() {

        if (
            GameManager
                .ownedChickens
                .isEmpty()
        ) {

            return
        }


        // Safety if list changes
        if (
            selectedChickenIndex >=
            GameManager
                .ownedChickens
                .size
        ) {

            selectedChickenIndex =
                0
        }


        rebuildChickenSelector()


        val chicken =
            GameManager
                .ownedChickens[
                selectedChickenIndex
            ]


        // IMAGE
        upgradeChickenImage.setImageResource(
            chicken.imageResId
        )


        // NAME
        chickenNameText.text =
            chicken.name


        // RARITY
        rarityText.text =
            chicken.rarity.displayName


        // Apply rarity glow to both
        RarityTextStyler.apply(
            chickenNameText,
            chicken.rarity
        )


        RarityTextStyler.apply(
            rarityText,
            chicken.rarity
        )


        // LEVEL
        levelText.text =
            "Level ${chicken.upgradeLevel} / " +
                    "${chicken.maxUpgradeLevel}"


        // CURRENT MULTIPLIER
        multiplierText.text =
            "Multiplier     x%.1f"
                .format(
                    chicken.multiplier
                )


        // CURRENT PRODUCTION
        productionText.text =
            "Production     " +
                    "${GameManager.getChickenProduction(chicken)} eggs"


        // =========================
        // NEXT LEVEL
        // =========================

        if (
            chicken.canUpgrade()
        ) {

            val increase =
                (
                        chicken.maxMultiplier -
                                1.0
                        ) /
                        chicken.maxUpgradeLevel


            val nextMultiplier =
                minOf(
                    chicken.maxMultiplier,
                    chicken.multiplier +
                            increase
                )


            val nextProduction =
                (
                        chicken.baseProduction *
                                nextMultiplier
                        ).toInt()


            nextUpgradeText.text =
                "Next: x%.1f → x%.1f   •   %d → %d eggs"
                    .format(
                        chicken.multiplier,
                        nextMultiplier,
                        chicken.getProduction(),
                        nextProduction
                    )


            val cost =
                chicken.getUpgradeCost()


            upgradeCostText.text =
                "Upgrade Cost: 🥚 " +
                        formatNumber(cost)


            upgradeButton.text =
                "UPGRADE"


            setButtonEnabled(
                upgradeButton,
                GameManager.eggs >= cost
            )

        } else {

            nextUpgradeText.text =
                "✨ MAX POWER!"


            upgradeCostText.text =
                "MAX LEVEL"


            upgradeButton.text =
                "MAXED"


            setButtonEnabled(
                upgradeButton,
                false
            )
        }
    }


    // =====================================================
    // BUY CHICKEN UPGRADE
    // =====================================================

    private fun buyChickenUpgrade() {

        if (
            GameManager
                .ownedChickens
                .isEmpty()
        ) {

            return
        }


        val chicken =
            GameManager
                .ownedChickens[
                selectedChickenIndex
            ]


        if (
            !chicken.canUpgrade()
        ) {

            return
        }


        val cost =
            chicken.getUpgradeCost()


        if (
            GameManager.eggs <
            cost
        ) {

            Toast.makeText(
                this,
                "Not enough eggs!",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        GameManager.eggs -=
            cost


        chicken.upgrade()


        animateViewPop(
            upgradeChickenImage
        )


        updateScreen()
    }


    // =====================================================
    // FEEDER SECTION
    // =====================================================

    private fun updateFeederSection() {

        val feeder =
            GameManager.feeder


        feederLevelText.text =
            "Feeder Level: " +
                    "${feeder.level} / " +
                    "${feeder.maxLevel}"


        feederLevelDots.text =
            makeLevelDots(
                feeder.level,
                feeder.maxLevel
            )


        feederStatsText.text =
            "Capacity: ${feeder.getCapacity()}\n" +
                    "Serving Size: ${feeder.getServingSize()}\n" +
                    "Mood Per Feed: +${feeder.getMoodIncrease()}"


        if (
            feeder.canUpgrade()
        ) {

            val cost =
                feeder.getUpgradeCost()


            upgradeFeederButton.text =
                "UPGRADE • 🥚 " +
                        formatNumber(cost)


            setButtonEnabled(
                upgradeFeederButton,
                GameManager.eggs >= cost
            )

        } else {

            upgradeFeederButton.text =
                "MAX FEEDER"


            setButtonEnabled(
                upgradeFeederButton,
                false
            )
        }
    }


    // =====================================================
    // BUY FEEDER UPGRADE
    // =====================================================

    private fun buyFeederUpgrade() {

        val feeder =
            GameManager.feeder


        if (
            !feeder.canUpgrade()
        ) {

            return
        }


        val cost =
            feeder.getUpgradeCost()


        if (
            GameManager.eggs <
            cost
        ) {

            Toast.makeText(
                this,
                "Not enough eggs!",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        GameManager.eggs -=
            cost


        feeder.upgrade()


        animateViewPop(
            feederLevelDots
        )


        updateScreen()
    }


    // =====================================================
    // SLOT SECTION
    // =====================================================

    private fun updateSlotSection() {

        val unlocked =
            GameManager.maxChickenSlots


        slotCountText.text =
            "$unlocked / 3 Slots Unlocked"


        updateSlotCard(
            slot1CardText,
            1,
            unlocked >= 1
        )


        updateSlotCard(
            slot2CardText,
            2,
            unlocked >= 2
        )


        updateSlotCard(
            slot3CardText,
            3,
            unlocked >= 3
        )


        val nextPrice =
            GameManager.getNextSlotPrice()


        // ALL SLOTS OWNED
        if (
            nextPrice == null
        ) {

            nextSlotText.text =
                "All Farm Slots Unlocked!"


            slotUpgradeCostText.text =
                "MAX SLOTS"


            upgradeSlotButton.text =
                "MAXED"


            setButtonEnabled(
                upgradeSlotButton,
                false
            )

            return
        }


        val nextSlot =
            unlocked + 1


        nextSlotText.text =
            "Unlock Slot $nextSlot"


        slotUpgradeCostText.text =
            "Cost: 🥚 " +
                    formatNumber(nextPrice)


        upgradeSlotButton.text =
            "🔓 UNLOCK SLOT $nextSlot"


        setButtonEnabled(
            upgradeSlotButton,
            GameManager.eggs >= nextPrice
        )
    }


    // =====================================================
    // SLOT CARD
    // =====================================================

    private fun updateSlotCard(
        textView: TextView,
        slotNumber: Int,
        unlocked: Boolean
    ) {

        if (unlocked) {

            textView.text =
                "🐔\nSLOT $slotNumber\nUNLOCKED"


            textView.setBackgroundResource(
                R.drawable.slot_unlocked_bg
            )

        } else {

            textView.text =
                "🔒\nSLOT $slotNumber\nLOCKED"


            textView.setBackgroundResource(
                R.drawable.slot_locked_bg
            )
        }
    }


    // =====================================================
    // BUY SLOT
    // =====================================================

    private fun buySlotUpgrade() {

        val price =
            GameManager.getNextSlotPrice()
                ?: return


        if (
            GameManager.eggs <
            price
        ) {

            Toast.makeText(
                this,
                "Not enough eggs!",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        if (
            GameManager.buyNextSlot()
        ) {

            when (
                GameManager.maxChickenSlots
            ) {

                2 ->
                    animateViewPop(
                        slot2CardText
                    )

                3 ->
                    animateViewPop(
                        slot3CardText
                    )
            }


            Toast.makeText(
                this,
                "New farm slot unlocked!",
                Toast.LENGTH_SHORT
            ).show()


            updateScreen()
        }
    }


    // =====================================================
    // FEEDER LEVEL DOTS
    // =====================================================

    private fun makeLevelDots(
        level: Int,
        maxLevel: Int
    ): String {

        var text =
            ""


        for (
        index in
        1..maxLevel
        ) {

            if (
                index <=
                level
            ) {

                text +=
                    "●"

            } else {

                text +=
                    "○"
            }


            if (
                index <
                maxLevel
            ) {

                text +=
                    "  "
            }
        }


        return text
    }


    // =====================================================
    // ENABLE / DISABLE BUTTON
    // =====================================================

    private fun setButtonEnabled(
        button: Button,
        enabled: Boolean
    ) {

        button.isEnabled =
            enabled


        button.alpha =
            if (enabled) {

                1f

            } else {

                0.55f
            }
    }


    // =====================================================
    // POP ANIMATION
    // =====================================================

    private fun animateViewPop(
        view: View
    ) {

        view.animate()
            .scaleX(1.18f)
            .scaleY(1.18f)
            .setDuration(120)
            .withEndAction {

                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(160)
                    .start()
            }
            .start()
    }


    // =====================================================
    // BUTTON SQUASH
    // =====================================================

    private fun addButtonPressAnimation(
        button: View
    ) {

        button.setOnTouchListener {
                view,
                event ->


            when (
                event.action
            ) {

                MotionEvent.ACTION_DOWN -> {

                    view.animate()
                        .scaleX(0.92f)
                        .scaleY(0.92f)
                        .setDuration(80)
                        .start()
                }


                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {

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


    // =====================================================
    // NUMBER FORMAT
    // =====================================================

    private fun formatNumber(
        number: Int
    ): String {

        return when {

            number >=
                    1_000_000 -> {

                String.format(
                    "%.1fM",
                    number /
                            1_000_000.0
                )
            }


            number >=
                    1_000 -> {

                String.format(
                    "%.1fK",
                    number /
                            1_000.0
                )
            }


            else -> {

                number.toString()
            }
        }
    }


    // =====================================================
    // DP
    // =====================================================

    private fun dpToPx(
        dp: Int
    ): Int {

        return (
                dp *
                        resources
                            .displayMetrics
                            .density
                ).toInt()
    }


    // =====================================================
    // RETURN TO SCREEN
    // =====================================================

    override fun onResume() {

        super.onResume()


        if (
            ::eggCountText
                .isInitialized
        ) {

            updateScreen()
        }
    }
}