package com.example.chickeninc

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UpgradesActivity : AppCompatActivity() {

    private lateinit var eggCountText: TextView
    private lateinit var chickenNameText: TextView
    private lateinit var rarityText: TextView
    private lateinit var levelText: TextView
    private lateinit var multiplierText: TextView
    private lateinit var productionText: TextView
    private lateinit var upgradeCostText: TextView

    private lateinit var upgradeButton: Button


    //this is all chicken selection
    private var selectedChickenIndex = 0

    private lateinit var chicken1Button: Button
    private lateinit var chicken2Button: Button
    private lateinit var chicken3Button: Button

    private lateinit var feederLevelText: TextView
    private lateinit var feederStatsText: TextView
    private lateinit var upgradeFeederButton: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_upgrades)

        chicken1Button = findViewById(R.id.chicken1Button)
        chicken2Button = findViewById(R.id.chicken2Button)
        chicken3Button = findViewById(R.id.chicken3Button)

        feederLevelText =
            findViewById(R.id.feederLevelText)

        feederStatsText =
            findViewById(R.id.feederStatsText)

        upgradeFeederButton =
            findViewById(R.id.upgradeFeederButton)

        upgradeFeederButton.setOnClickListener {

            buyFeederUpgrade()
        }

        eggCountText = findViewById(R.id.eggCountText)
        chickenNameText = findViewById(R.id.chickenNameText)
        rarityText = findViewById(R.id.rarityText)
        levelText = findViewById(R.id.levelText)
        multiplierText = findViewById(R.id.multiplierText)
        productionText = findViewById(R.id.productionText)
        upgradeCostText = findViewById(R.id.upgradeCostText)

        upgradeButton = findViewById(R.id.upgradeButton)

        val backButton =
            findViewById<Button>(R.id.backButton)


        backButton.setOnClickListener {
            finish()
        }


        upgradeButton.setOnClickListener {
            buyUpgrade()
        }

        chicken1Button.setOnClickListener {

            if (GameManager.ownedChickens.size >= 1) {
                selectedChickenIndex = 0
                updateScreen()
            }
        }
        chicken2Button.setOnClickListener {

            if (GameManager.ownedChickens.size >= 2) {
                selectedChickenIndex = 1
                updateScreen()
            }
        }
        chicken3Button.setOnClickListener {

            if (GameManager.ownedChickens.size >= 3) {
                selectedChickenIndex = 2
                updateScreen()
            }
        }
        updateScreen()
    }


    private fun buyUpgrade() {

        val chicken =
            GameManager.ownedChickens[selectedChickenIndex]


        if (!chicken.canUpgrade()) {
            return
        }

        val cost = chicken.getUpgradeCost()


        if (GameManager.eggs >= cost) {

            GameManager.eggs -= cost

            chicken.upgrade()

            updateScreen()
        }
    }


    private fun updateScreen() {

        updateChickenButtons()

        val chicken =
            GameManager.ownedChickens[selectedChickenIndex]



        eggCountText.text =
            "Eggs: ${GameManager.eggs}"

        chickenNameText.text =
            chicken.name

        rarityText.text =
            chicken.rarity.displayName

        RarityTextStyler.apply(
            rarityText,
            chicken.rarity
        )

        levelText.text =
            "Level: ${chicken.upgradeLevel} / ${chicken.maxUpgradeLevel}"


        multiplierText.text =
            "Multiplier: x%.1f".format(chicken.multiplier)


        productionText.text =
            "Production: ${chicken.getProduction()} eggs"


        if (chicken.canUpgrade()) {

            upgradeCostText.text =
                "Upgrade Cost: ${chicken.getUpgradeCost()} Eggs"

            upgradeButton.text =
                "Upgrade"

            upgradeButton.isEnabled =
                GameManager.eggs >= chicken.getUpgradeCost()

        } else {

            upgradeCostText.text =
                "MAX LEVEL"

            upgradeButton.text =
                "MAXED"

            upgradeButton.isEnabled =
                false
        }
        val feeder =
            GameManager.feeder


        feederLevelText.text =
            "Feeder Level: ${feeder.level} / ${feeder.maxLevel}"


        feederStatsText.text =
            "Capacity: ${feeder.getCapacity()}\n" +
                    "Serving: ${feeder.getServingSize()}\n" +
                    "Mood Per Feed: +${feeder.getMoodIncrease()}"

        if (feeder.canUpgrade()) {

            upgradeFeederButton.text =
                "Upgrade Feeder - ${feeder.getUpgradeCost()} Eggs"

            upgradeFeederButton.isEnabled =
                GameManager.eggs >= feeder.getUpgradeCost()

        } else {

            upgradeFeederButton.text =
                "MAX FEEDER"

            upgradeFeederButton.isEnabled =
                false
        }
    }
    private fun updateChickenButtons() {

        if (GameManager.ownedChickens.size >= 1) {

            chicken1Button.text =
                GameManager.ownedChickens[0].name

            chicken1Button.isEnabled = true

        } else {

            chicken1Button.text = "Locked"
            chicken1Button.isEnabled = false
        }


        if (GameManager.ownedChickens.size >= 2) {

            chicken2Button.text =
                GameManager.ownedChickens[1].name

            chicken2Button.isEnabled = true

        } else {

            chicken2Button.text = "Locked"
            chicken2Button.isEnabled = false
        }


        if (GameManager.ownedChickens.size >= 3) {

            chicken3Button.text =
                GameManager.ownedChickens[2].name

            chicken3Button.isEnabled = true

        } else {

            chicken3Button.text = "Locked"
            chicken3Button.isEnabled = false
        }
    }
    private fun buyFeederUpgrade() {

        val feeder =
            GameManager.feeder

        if (!feeder.canUpgrade()) {
            return
        }

        val cost =
            feeder.getUpgradeCost()

        if (GameManager.eggs < cost) {
            return
        }

        GameManager.eggs -= cost

        feeder.upgrade()

        updateScreen()
    }
}