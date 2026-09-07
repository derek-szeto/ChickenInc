package com.example.chickeninc

class Chicken(

    val name: String,

    val rarity: Rarity,

    val baseProduction: Int,

    val shopPrice: Int,

    val upgradeBaseCost: Int,

    val imageResId: Int,

    val availableInShop: Boolean = true

) {

    var upgradeLevel: Int = 0

    var multiplier: Double = 1.0


    val maxUpgradeLevel: Int
        get() = rarity.maxUpgradeLevel


    val maxMultiplier: Double
        get() = rarity.maxMultiplier


    fun getProduction(): Int {

        // Decimal eggs are always truncated
        return (baseProduction * multiplier).toInt()
    }


    fun getUpgradeCost(): Int {

        return upgradeBaseCost * (upgradeLevel + 1)
    }


    fun canUpgrade(): Boolean {

        return upgradeLevel < maxUpgradeLevel
    }


    fun upgrade() {

        if (!canUpgrade()) {
            return
        }

        upgradeLevel++

        val multiplierIncrease =
            (maxMultiplier - 1.0) / maxUpgradeLevel

        multiplier =
            1.0 + (multiplierIncrease * upgradeLevel)
    }
}