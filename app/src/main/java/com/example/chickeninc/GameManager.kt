package com.example.chickeninc

object GameManager {

    var eggs: Int = 0

    var mood: Int = 100

    val feeder = Feeder()
    //dynamic spin variables
    var spinRolls: Int = 0

    var nextFreeSpinTime: Long = 0L
    //timer variables for gadgets
    var unlimitedFeederUntil: Long = 0L

    var unlimitedMoodUntil: Long = 0L


    const val SPIN_COOLDOWN: Long =
        6L * 60L * 60L * 1000L

    const val GADGET_DURATION: Long =
        5L * 60L * 1000L


    // =========================
    // COMMON
    // =========================

    val basicChicken = Chicken(
        "Basic Chicken",
        Rarity.COMMON,
        1,
        0,
        100,
        R.drawable.basic_chicken
    )


    // =========================
    // UNCOMMON
    // =========================

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


    // =========================
    // RARE
    // =========================

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


    // =========================
    // EPIC
    // =========================

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


    // =========================
    // LEGENDARY
    // =========================

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


    // =========================
    // SECRET SHOP CHICKENS
    // =========================

    val shinyChicken = Chicken(
        "Shiny Chicken",
        Rarity.SECRET,
        250,
        2000000,
        100000,
        R.drawable.shiny_chicken
    )

    val chromaticChicken = Chicken(
        "Chromatic Chicken",
        Rarity.SECRET,
        400,
        5000000,
        250000,
        R.drawable.chromatic_chicken
    )


    // =========================
    // SPECIAL / NOT IN SHOP
    // =========================

    val kingChicken = Chicken(
        "King Chicken",
        Rarity.LEGENDARY,
        500,
        0,
        100000,
        R.drawable.king_chicken,
        false
    )

    val corruptedChicken = Chicken(
        "Corrupted Chicken",
        Rarity.SECRET,
        700,
        0,
        200000,
        R.drawable.corrupted_chicken,
        false
    )

    val angelChicken = Chicken(
        "Angel Chicken",
        Rarity.SECRET,
        800,
        0,
        250000,
        R.drawable.angel_chicken,
        false
    )

    val demonChicken = Chicken(
        "Demon Chicken",
        Rarity.SECRET,
        850,
        0,
        250000,
        R.drawable.demon_chicken,
        false
    )

    val bestStrawberryChicken = Chicken(
        "Perfect Strawberry Chicken",
        Rarity.SECRET,
        1000,
        0,
        300000,
        R.drawable.best_strawberry_chicken,
        false
    )

    val bestDiamondChicken = Chicken(
        "Perfect Diamond Chicken",
        Rarity.SECRET,
        1500,
        0,
        400000,
        R.drawable.best_diamond_chicken,
        false
    )


    // =========================
    // SHOP CATALOG
    // =========================

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


    // Player starts with Basic Chicken
    // All chickens the player has purchased
    val ownedChickens = ArrayList<Chicken>()

    // Chickens currently active on the farm
    val equippedChickens = ArrayList<Chicken>()

    // Player begins with one active slot
    var maxChickenSlots = 1


    init {

        ownedChickens.add(basicChicken)
        equippedChickens.add(basicChicken)
    }


    // =========================
    // PRODUCTION
    // =========================

    fun getTotalProduction(): Int {

        var total = 0

        for (chicken in equippedChickens) {

            total += chicken.getProduction()
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


        // Automatically place it on the farm if there is room
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

        // Always require at least one active chicken
        if (equippedChickens.size <= 1) {
            return false
        }

        equippedChickens.remove(chicken)

        return true
    }
    fun getNextSlotPrice(): Int? {

        return when (maxChickenSlots) {

            1 -> 5000

            2 -> 100000

            else -> null
        }
    }


    fun buyNextSlot(): Boolean {

        val price = getNextSlotPrice()
            ?: return false


        if (eggs < price) {
            return false
        }


        eggs -= price

        maxChickenSlots += 1

        return true
    }
    // =========================
    // BUY CHICKEN
    // =========================

//    fun buyChicken(chicken: Chicken): Boolean {
//
//        if (!chicken.availableInShop) {
//            return false
//        }
//
//        // Cannot buy same chicken twice
//        if (ownedChickens.contains(chicken)) {
//            return false
//        }
//
//        // No available chicken slot
//        if (ownedChickens.size >= maxChickenSlots) {
//            return false
//        }
//
//        // Not enough eggs
//        if (eggs < chicken.shopPrice) {
//            return false
//        }
//
//        eggs -= chicken.shopPrice
//
//        ownedChickens.add(chicken)
//
//        return true
//    }
    fun isFreeSpinReady(): Boolean {

        return System.currentTimeMillis() >=
                nextFreeSpinTime
    }


    fun canSpin(): Boolean {

        return isFreeSpinReady() ||
                spinRolls > 0
    }


    fun consumeSpin(): Boolean {

        // Use free spin first
        if (isFreeSpinReady()) {

            nextFreeSpinTime =
                System.currentTimeMillis() +
                        SPIN_COOLDOWN

            return true
        }


        // Otherwise use a spin roll
        if (spinRolls > 0) {

            spinRolls--

            return true
        }


        return false
    }


    fun activateUnlimitedFeeder() {

        val currentTime =
            System.currentTimeMillis()

        val startTime =
            maxOf(
                currentTime,
                unlimitedFeederUntil
            )

        unlimitedFeederUntil =
            startTime + GADGET_DURATION
    }


    fun activateUnlimitedMood() {

        val currentTime =
            System.currentTimeMillis()

        val startTime =
            maxOf(
                currentTime,
                unlimitedMoodUntil
            )

        unlimitedMoodUntil =
            startTime + GADGET_DURATION
    }


    fun hasUnlimitedFeeder(): Boolean {

        return System.currentTimeMillis() <
                unlimitedFeederUntil
    }


    fun hasUnlimitedMood(): Boolean {

        return System.currentTimeMillis() <
                unlimitedMoodUntil
    }
    fun replaceEquippedChicken(
        slotIndex: Int,
        newChicken: Chicken
    ): Boolean {

        // Player must own the chicken
        if (!ownedChickens.contains(newChicken)) {
            return false
        }

        // Chicken cannot already be active
        if (equippedChickens.contains(newChicken)) {
            return false
        }

        // Make sure slot actually exists
        if (
            slotIndex < 0 ||
            slotIndex >= equippedChickens.size
        ) {
            return false
        }

        equippedChickens[slotIndex] =
            newChicken

        return true
    }
}