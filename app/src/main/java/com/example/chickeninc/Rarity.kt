package com.example.chickeninc

enum class Rarity(
    val displayName: String,
    val maxUpgradeLevel: Int,
    val maxMultiplier: Double
) {

    COMMON(
        "Common",
        5,
        2.0
    ),

    UNCOMMON(
        "Uncommon",
        7,
        3.0
    ),

    RARE(
        "Rare",
        9,
        5.0
    ),

    EPIC(
        "Epic",
        12,
        8.0
    ),

    LEGENDARY(
        "Legendary",
        15,
        12.0
    ),

    SECRET(
        "Secret",
        20,
        20.0
    )
}