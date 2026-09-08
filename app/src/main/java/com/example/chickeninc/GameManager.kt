package com.example.chickeninc

object GameManager {

    var eggs = 0
    var mood = 100

    val feeder = Feeder()

    var spinRolls = 0
    var nextFreeSpinTime = 0L
    var unlimitedFeederUntil = 0L
    var unlimitedMoodUntil = 0L

    const val SPIN_COOLDOWN =
        6L * 60L * 60L * 1000L

    const val GADGET_DURATION =
        5L * 60L * 1000L

    const val MAX_REBIRTHS = 10

    var rebirthLevel = 0
    var maxChickenSlots = 1

    private val rebirthMultipliers = intArrayOf(
        1,
        2,
        4,
        8,
        12,
        18,
        25,
        35,
        50,
        70,
        100
    )

    private val rebirthCosts = intArrayOf(
        100_000,
        500_000,
        2_000_000,
        10_000_000,
        30_000_000,
        75_000_000,
        150_000_000,
        300_000_000,
        600_000_000,
        1_200_000_000
    )

    val basicChicken = Chicken(
        "Basic Chicken",
        Rarity.COMMON,
        1,
        0,
        100,
        R.drawable.basic_chicken
    )

    val spottedChicken = Chicken(
        "Spotted Chicken",
        Rarity.UNCOMMON,
        2,
        300,
        150,
        R.drawable.spotted_chicken
    )

    val strawberryChicken = Chicken(
        "Strawberry Chicken",
        Rarity.UNCOMMON,
        3,
        500,
        200,
        R.drawable.best_strawberry_chicken
    )

    val blueberryChicken = Chicken(
        "Blueberry Chicken",
        Rarity.UNCOMMON,
        4,
        800,
        250,
        R.drawable.blueberry_chicken
    )

    val fancyChicken = Chicken(
        "Fancy Chicken",
        Rarity.UNCOMMON,
        5,
        1200,
        300,
        R.drawable.fancy_chicken
    )

    val mexicanChicken = Chicken(
        "Mexican Chicken",
        Rarity.UNCOMMON,
        6,
        1800,
        400,
        R.drawable.mexican_chicken
    )

    val silverChicken = Chicken(
        "Silver Chicken",
        Rarity.RARE,
        8,
        3000,
        600,
        R.drawable.silver_chicken
    )

    val goldChicken = Chicken(
        "Gold Chicken",
        Rarity.RARE,
        10,
        5000,
        800,
        R.drawable.gold_chicken
    )

    val emeraldChicken = Chicken(
        "Emerald Chicken",
        Rarity.RARE,
        12,
        8000,
        1000,
        R.drawable.emerald_chicken
    )

    val rubyChicken = Chicken(
        "Ruby Chicken",
        Rarity.RARE,
        15,
        12000,
        1500,
        R.drawable.ruby_chicken
    )

    val candyChicken = Chicken(
        "Candy Chicken",
        Rarity.RARE,
        18,
        18000,
        2000,
        R.drawable.candy_chicken
    )

    val ninjaChicken = Chicken(
        "Ninja Chicken",
        Rarity.RARE,
        22,
        25000,
        2500,
        R.drawable.ninja_chicken
    )

    val diamondChicken = Chicken(
        "Diamond Chicken",
        Rarity.EPIC,
        28,
        40000,
        4000,
        R.drawable.best_diamond_chicken
    )

    val fireChicken = Chicken(
        "Fire Chicken",
        Rarity.EPIC,
        35,
        60000,
        5500,
        R.drawable.fire_chicken
    )

    val iceChicken = Chicken(
        "Ice Chicken",
        Rarity.EPIC,
        42,
        85000,
        7000,
        R.drawable.ice_chicken
    )

    val lightningChicken = Chicken(
        "Lightning Chicken",
        Rarity.EPIC,
        50,
        120000,
        9000,
        R.drawable.lightning_chicken
    )

    val toxicChicken = Chicken(
        "Toxic Chicken",
        Rarity.EPIC,
        60,
        175000,
        12000,
        R.drawable.toxic_chicken
    )

    val robotChicken = Chicken(
        "Robot Chicken",
        Rarity.EPIC,
        72,
        250000,
        15000,
        R.drawable.robot_chicken
    )

    val superheroChicken = Chicken(
        "Superhero Chicken",
        Rarity.EPIC,
        85,
        350000,
        20000,
        R.drawable.superhero_chicken
    )

    val spaceChicken = Chicken(
        "Space Chicken",
        Rarity.LEGENDARY,
        120,
        600000,
        30000,
        R.drawable.space_chicken
    )

    val glitchChicken = Chicken(
        "Glitch Chicken",
        Rarity.LEGENDARY,
        160,
        900000,
        45000,
        R.drawable.glitch_chicken
    )

    val shinyChicken = Chicken(
        "Shiny Chicken",
        Rarity.SECRET,
        250,
        2_000_000,
        100_000,
        R.drawable.shiny_chicken
    )

    val chromaticChicken = Chicken(
        "Chromatic Chicken",
        Rarity.SECRET,
        400,
        5_000_000,
        250_000,
        R.drawable.chromatic_chicken
    )

    val kingChicken = Chicken(
        "King Chicken",
        Rarity.LEGENDARY,
        500,
        0,
        100_000,
        R.drawable.king_chicken,
        false
    )

    val corruptedChicken = Chicken(
        "Corrupted Chicken",
        Rarity.SECRET,
        700,
        0,
        200_000,
        R.drawable.corrupted_chicken,
        false
    )

    val angelChicken = Chicken(
        "Angel Chicken",
        Rarity.SECRET,
        800,
        0,
        250_000,
        R.drawable.angel_chicken,
        false
    )

    val demonChicken = Chicken(
        "Demon Chicken",
        Rarity.SECRET,
        850,
        0,
        250_000,
        R.drawable.demon_chicken,
        false
    )

    val bestStrawberryChicken = Chicken(
        "Perfect Strawberry Chicken",
        Rarity.SECRET,
        1000,
        0,
        300_000,
        R.drawable.best_strawberry_chicken,
        false
    )

    val bestDiamondChicken = Chicken(
        "Perfect Diamond Chicken",
        Rarity.SECRET,
        1500,
        0,
        400_000,
        R.drawable.best_diamond_chicken,
        false
    )

    val shopChickens = listOf(
        spottedChicken,
        strawberryChicken,
        blueberryChicken,
        fancyChicken,
        mexicanChicken,
        silverChicken,
        goldChicken,
        emeraldChicken,
        rubyChicken,
        candyChicken,
        ninjaChicken,
        diamondChicken,
        fireChicken,
        iceChicken,
        lightningChicken,
        toxicChicken,
        robotChicken,
        superheroChicken,
        spaceChicken,
        glitchChicken,
        shinyChicken,
        chromaticChicken
    )

    val ownedChickens = ArrayList<Chicken>()
    val equippedChickens = ArrayList<Chicken>()

    init {
        ownedChickens.add(basicChicken)
        equippedChickens.add(basicChicken)
    }

    fun getChickenProduction(chicken: Chicken): Int {
        return chicken.getProduction() * getRebirthMultiplier()
    }

    fun getTotalProduction(): Int {
        var total = 0

        for (chicken in equippedChickens) {
            total += getChickenProduction(chicken)
        }

        return total
    }

    fun isOwned(chicken: Chicken): Boolean {
        return ownedChickens.contains(chicken)
    }

    fun isEquipped(chicken: Chicken): Boolean {
        return equippedChickens.contains(chicken)
    }

    fun buyChicken(chicken: Chicken): Boolean {
        if (!chicken.availableInShop) {
            return false
        }

        if (isOwned(chicken)) {
            return false
        }

        if (eggs < chicken.shopPrice) {
            return false
        }

        eggs -= chicken.shopPrice
        ownedChickens.add(chicken)

        if (equippedChickens.size < maxChickenSlots) {
            equippedChickens.add(chicken)
        }

        return true
    }

    fun equipChicken(chicken: Chicken): Boolean {
        if (!isOwned(chicken)) {
            return false
        }

        if (isEquipped(chicken)) {
            return false
        }

        if (equippedChickens.size >= maxChickenSlots) {
            return false
        }

        equippedChickens.add(chicken)
        return true
    }

    fun unequipChicken(chicken: Chicken): Boolean {
        if (!isEquipped(chicken)) {
            return false
        }

        if (equippedChickens.size <= 1) {
            return false
        }

        equippedChickens.remove(chicken)
        return true
    }

    fun replaceEquippedChicken(
        slotIndex: Int,
        newChicken: Chicken
    ): Boolean {
        if (!isOwned(newChicken)) {
            return false
        }

        if (isEquipped(newChicken)) {
            return false
        }

        if (slotIndex !in equippedChickens.indices) {
            return false
        }

        equippedChickens[slotIndex] = newChicken
        return true
    }

    fun getNextSlotPrice(): Int? {
        return when (maxChickenSlots) {
            1 -> 5_000
            2 -> 100_000
            else -> null
        }
    }

    fun buyNextSlot(): Boolean {
        val price = getNextSlotPrice() ?: return false

        if (eggs < price) {
            return false
        }

        eggs -= price
        maxChickenSlots++

        return true
    }

    fun isFreeSpinReady(): Boolean {
        return System.currentTimeMillis() >= nextFreeSpinTime
    }

    fun canSpin(): Boolean {
        return isFreeSpinReady() || spinRolls > 0
    }

    fun consumeSpin(): Boolean {
        if (isFreeSpinReady()) {
            nextFreeSpinTime =
                System.currentTimeMillis() + SPIN_COOLDOWN

            return true
        }

        if (spinRolls > 0) {
            spinRolls--
            return true
        }

        return false
    }

    fun activateUnlimitedFeeder() {
        val currentTime = System.currentTimeMillis()
        val startTime = maxOf(currentTime, unlimitedFeederUntil)

        unlimitedFeederUntil =
            startTime + GADGET_DURATION
    }

    fun activateUnlimitedMood() {
        val currentTime = System.currentTimeMillis()
        val startTime = maxOf(currentTime, unlimitedMoodUntil)

        unlimitedMoodUntil =
            startTime + GADGET_DURATION
    }

    fun hasUnlimitedFeeder(): Boolean {
        return System.currentTimeMillis() < unlimitedFeederUntil
    }

    fun hasUnlimitedMood(): Boolean {
        return System.currentTimeMillis() < unlimitedMoodUntil
    }

    fun getRebirthMultiplier(): Int {
        val safeLevel =
            rebirthLevel.coerceIn(0, MAX_REBIRTHS)

        return rebirthMultipliers[safeLevel]
    }

    fun getNextRebirthMultiplier(): Int? {
        if (rebirthLevel >= MAX_REBIRTHS) {
            return null
        }

        return rebirthMultipliers[rebirthLevel + 1]
    }

    fun getNextRebirthCost(): Int? {
        if (rebirthLevel >= MAX_REBIRTHS) {
            return null
        }

        return rebirthCosts[rebirthLevel]
    }

    fun canRebirth(): Boolean {
        val cost = getNextRebirthCost() ?: return false
        return eggs >= cost
    }

    fun performRebirth(): Boolean {
        if (!canRebirth()) {
            return false
        }

        rebirthLevel++
        eggs = 0
        mood = 100

        for (chicken in ownedChickens) {
            chicken.upgradeLevel = 0
            chicken.multiplier = 1.0
        }

        return true
    }
}