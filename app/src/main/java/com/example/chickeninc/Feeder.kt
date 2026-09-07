package com.example.chickeninc

class Feeder {

    var level: Int = 1
    var currentFood: Int = 10

    val maxLevel: Int = 5


    fun getCapacity(): Int {

        return when (level) {

            1 -> 10
            2 -> 18
            3 -> 30
            4 -> 45
            5 -> 65

            else -> 65
        }
    }


    fun getServingSize(): Int {

        return when (level) {

            1 -> 1
            2 -> 2
            3 -> 3
            4 -> 4
            5 -> 5

            else -> 5
        }
    }


    fun getMoodIncrease(): Int {

        return when (level) {

            1 -> 15
            2 -> 20
            3 -> 25
            4 -> 32
            5 -> 40

            else -> 40
        }
    }


    fun getUpgradeCost(): Int {

        return when (level) {

            1 -> 1000
            2 -> 5000
            3 -> 20000
            4 -> 75000

            else -> 0
        }
    }


    fun canUpgrade(): Boolean {

        return level < maxLevel
    }


    fun upgrade(): Boolean {

        if (!canUpgrade()) {
            return false
        }

        level++

        return true
    }


    fun canFeed(): Boolean {

        return currentFood >= getServingSize()
    }


    fun feed(): Boolean {

        if (!canFeed()) {
            return false
        }

        currentFood -= getServingSize()

        return true
    }


    fun addFood(amount: Int) {

        currentFood += amount

        if (currentFood > getCapacity()) {
            currentFood = getCapacity()
        }
    }
}