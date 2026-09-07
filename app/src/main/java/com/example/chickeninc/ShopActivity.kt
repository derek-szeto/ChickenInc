package com.example.chickeninc

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
class ShopActivity : AppCompatActivity() {

    // =========================
    // MAIN SHOP VIEWS
    // =========================

    private lateinit var chickenGrid: GridLayout

    private lateinit var chickensContent: LinearLayout
    private lateinit var gadgetsContent: LinearLayout

    private lateinit var shopTitleText: TextView
    private lateinit var shopEggText: TextView


    // =========================
    // CHICKEN PREVIEW
    // =========================

    private lateinit var previewImage: ImageView
    private lateinit var previewName: TextView
    private lateinit var previewRarity: TextView
    private lateinit var previewStats: TextView
    private lateinit var previewPrice: TextView

    private lateinit var buyButton: Button
    private lateinit var deployButton: Button


    // =========================
    // TABS
    // =========================

    private lateinit var chickensTab: Button
    private lateinit var gadgetsTab: Button


    // Chicken currently selected
    private var selectedChicken: Chicken =
        GameManager.basicChicken


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_shop)


        // =========================
        // FIND VIEWS
        // =========================

        chickenGrid =
            findViewById(R.id.chickenGrid)

        chickensContent =
            findViewById(R.id.chickensContent)

        gadgetsContent =
            findViewById(R.id.gadgetsContent)


        shopTitleText =
            findViewById(R.id.shopTitleText)

        shopEggText =
            findViewById(R.id.shopEggText)


        previewImage =
            findViewById(R.id.previewImage)

        previewName =
            findViewById(R.id.previewName)

        previewRarity =
            findViewById(R.id.previewRarity)

        previewStats =
            findViewById(R.id.previewStats)

        previewPrice =
            findViewById(R.id.previewPrice)


        buyButton =
            findViewById(R.id.buyButton)

        deployButton =
            findViewById(R.id.deployButton)


        chickensTab =
            findViewById(R.id.chickensTab)

        gadgetsTab =
            findViewById(R.id.gadgetsTab)


        val backButton =
            findViewById<Button>(R.id.backButton)


        // =========================
        // BUTTON LISTENERS
        // =========================

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


        // Start on chicken shop
        showChickenTab()
    }


    // =====================================================
    // CHICKEN TAB
    // =====================================================

    private fun showChickenTab() {

        shopTitleText.text =
            "🐔 Chicken Shop"

        chickensContent.visibility =
            View.VISIBLE

        gadgetsContent.visibility =
            View.GONE


        // Selected tab graphics
        chickensTab.setBackgroundResource(
            R.drawable.shop_tab_selected_bg
        )

        gadgetsTab.setBackgroundResource(
            R.drawable.shop_tab_unselected_bg
        )


        updateEggDisplay()

        populateChickenGrid()

        updatePreview()
    }


    // =====================================================
    // GADGET TAB
    // =====================================================

    private fun showGadgetsTab() {

        shopTitleText.text =
            "⚡ Gadget Shop"

        chickensContent.visibility =
            View.GONE

        gadgetsContent.visibility =
            View.VISIBLE


        chickensTab.setBackgroundResource(
            R.drawable.shop_tab_unselected_bg
        )

        gadgetsTab.setBackgroundResource(
            R.drawable.shop_tab_selected_bg
        )


        updateEggDisplay()
    }


    // =====================================================
    // CHICKEN GRID
    // =====================================================

    private fun populateChickenGrid() {

        chickenGrid.removeAllViews()


        for (chicken in GameManager.shopChickens) {

            val card =
                layoutInflater.inflate(
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


            // =========================
            // CARD INFORMATION
            // =========================

            image.setImageResource(
                chicken.imageResId
            )


            name.text =
                chicken.name


            rarity.text =
                chicken.rarity.displayName


            // Rarity colors / animations
            RarityTextStyler.apply(
                name,
                chicken.rarity
            )

            RarityTextStyler.apply(
                rarity,
                chicken.rarity
            )


            // =========================
            // OWNERSHIP STATUS
            // =========================

            if (GameManager.isOwned(chicken)) {

                if (GameManager.isEquipped(chicken)) {

                    price.text =
                        "ACTIVE"

                    price.setTextColor(
                        getColor(
                            android.R.color.holo_green_dark
                        )
                    )

                } else {

                    price.text =
                        "OWNED"

                    price.setTextColor(
                        getColor(
                            android.R.color.holo_green_dark
                        )
                    )
                }

            } else {

                price.text =
                    "🥚 ${formatNumber(chicken.shopPrice)}"

                price.setTextColor(
                    getColor(
                        android.R.color.black
                    )
                )
            }


            // =========================
            // SELECT CHICKEN
            // =========================

            card.setOnClickListener {

                selectedChicken =
                    chicken

                updatePreview()
            }


            chickenGrid.addView(card)
        }
    }


    // =====================================================
    // SELECTED CHICKEN PREVIEW
    // =====================================================

    private fun updatePreview() {

        val chicken =
            selectedChicken


        // Image
        previewImage.setImageResource(
            chicken.imageResId
        )


        // Name
        previewName.text =
            chicken.name


        // Rarity
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


        // =========================
        // STATS
        // =========================

        previewStats.text =
            "Base Production: ${chicken.baseProduction}\n" +
                    "Max Level: ${chicken.maxUpgradeLevel}\n" +
                    "Max Multiplier: x${chicken.maxMultiplier}"


        // =========================
        // NOT OWNED
        // =========================

        if (!GameManager.isOwned(chicken)) {

            previewPrice.text =
                "🥚 ${formatNumber(chicken.shopPrice)} Eggs"

            buyButton.text =
                "BUY"

            buyButton.isEnabled =
                true

            buyButton.alpha =
                1f


            deployButton.text =
                "DEPLOY"

            deployButton.isEnabled =
                false

            deployButton.alpha =
                0.5f
        }


        // =========================
        // OWNED + ACTIVE
        // =========================

        else if (GameManager.isEquipped(chicken)) {

            previewPrice.text =
                "Owned • Active"

            buyButton.text =
                "OWNED"

            buyButton.isEnabled =
                false

            buyButton.alpha =
                0.5f


            deployButton.text =
                "ACTIVE"

            deployButton.isEnabled =
                true

            deployButton.alpha =
                1f
        }


        // =========================
        // OWNED BUT NOT ACTIVE
        // =========================

        else {

            previewPrice.text =
                "Owned"

            buyButton.text =
                "OWNED"

            buyButton.isEnabled =
                false

            buyButton.alpha =
                0.5f


            deployButton.text =
                "DEPLOY"

            deployButton.isEnabled =
                true

            deployButton.alpha =
                1f
        }


        updateEggDisplay()
    }


    // =====================================================
    // BUY CHICKEN
    // =====================================================

    private fun buySelectedChicken() {

        val chicken =
            selectedChicken


        // Already owned
        if (GameManager.isOwned(chicken)) {

            previewPrice.text =
                "You already own this chicken!"

            return
        }


        // Not enough eggs
        if (GameManager.eggs < chicken.shopPrice) {

            previewPrice.text =
                "Not enough eggs!"

            return
        }


        // Attempt purchase
        if (GameManager.buyChicken(chicken)) {

            previewPrice.text =
                "Purchased!"

            refreshShop()
        }
    }


    // =====================================================
    // DEPLOY CHICKEN
    // =====================================================

    private fun handleDeployButton() {

        val chicken =
            selectedChicken


        // Must own chicken
        if (!GameManager.isOwned(chicken)) {

            previewPrice.text =
                "Buy this chicken first!"

            return
        }


        // Already deployed
        if (GameManager.isEquipped(chicken)) {

            previewPrice.text =
                "This chicken is already active!"

            return
        }


        // Free farm slot
        if (
            GameManager.equippedChickens.size <
            GameManager.maxChickenSlots
        ) {

            if (
                GameManager.equipChicken(
                    chicken
                )
            ) {

                previewPrice.text =
                    "${chicken.name} deployed!"

                refreshShop()
            }

            return
        }


        // Farm full:
        // choose chicken to replace
        showReplaceChickenPopup(
            chicken
        )
    }


    // =====================================================
    // REFRESH SHOP
    // =====================================================

    private fun refreshShop() {

        updateEggDisplay()

        populateChickenGrid()

        updatePreview()
    }


    // =====================================================
    // EGG DISPLAY
    // =====================================================

    private fun updateEggDisplay() {

        shopEggText.text =
            "🥚 ${formatNumber(GameManager.eggs)}"
    }


    // =====================================================
    // NUMBER FORMATTING
    // =====================================================

    private fun formatNumber(
        number: Int
    ): String {

        return when {

            number >= 1_000_000 -> {

                String.format(
                    "%.1fM",
                    number / 1_000_000.0
                )
            }


            number >= 1_000 -> {

                String.format(
                    "%.1fK",
                    number / 1_000.0
                )
            }


            else -> {

                number.toString()
            }
        }
    }


    // =====================================================
    // RETURNING TO SHOP
    // =====================================================

    override fun onResume() {
        super.onResume()

        updateEggDisplay()

        if (::chickenGrid.isInitialized) {

            populateChickenGrid()

            updatePreview()
        }
    }
    private fun showReplaceChickenPopup(
        newChicken: Chicken
    ) {

        val dialog =
            Dialog(this)

        dialog.setContentView(
            R.layout.dialog_replace_chicken
        )


        // Transparent outside the rounded popup
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
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


        // =========================
        // NEW CHICKEN INFO
        // =========================

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


        // =========================
        // CURRENT ACTIVE CHICKENS
        // =========================

        for (
        index in
        GameManager.equippedChickens.indices
        ) {

            val currentChicken =
                GameManager.equippedChickens[index]


            val row =
                layoutInflater.inflate(
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
                "Level ${currentChicken.upgradeLevel}  •  " +
                        "${currentChicken.getProduction()} Eggs"


            replaceButton.setOnClickListener {

                val replaced =
                    GameManager
                        .replaceEquippedChicken(
                            index,
                            newChicken
                        )


                if (replaced) {

                    dialog.dismiss()

                    previewPrice.text =
                        "${newChicken.name} deployed!"

                    refreshShop()
                }
            }


            replacementContainer.addView(
                row
            )
        }


        cancelButton.setOnClickListener {

            dialog.dismiss()
        }


        dialog.show()


        // Make popup wide enough
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90)
                .toInt(),

            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
}