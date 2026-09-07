package com.example.chickeninc

sealed class WheelPrize {

    data class ChickenPrize(
        val chicken: Chicken
    ) : WheelPrize()

    data class GadgetPrize(
        val gadget: WheelGadget
    ) : WheelPrize()
}


enum class WheelGadget(
    val displayName: String
) {

    UNLIMITED_FEEDER(
        "Unlimited Feeder"
    ),

    UNLIMITED_MOOD(
        "Unlimited Mood"
    ),

    SPIN_ROLL(
        "Spin Roll"
    )
}