package com.example.chickeninc

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {
    private lateinit var eggCountText: TextView
    private lateinit var eggProduction: TextView

    private val handler = Handler(Looper.getMainLooper())

    //handles mood stuff
    private lateinit var moodText: TextView
    private lateinit var moodBar: ProgressBar
    private lateinit var feedButton: Button
    private lateinit var feederText: TextView

    private lateinit var chickenImage1: ImageView
    private lateinit var chickenImage2: ImageView
    private lateinit var chickenImage3: ImageView

    private lateinit var chickenSlotText1: TextView
    private lateinit var chickenSlotText2: TextView
    private lateinit var chickenSlotText3: TextView
    private val eggRunnable = object : Runnable {

        override fun run() {

            // Chicken only produces if mood is above 10
            if (GameManager.mood > 10) {
                GameManager.eggs += GameManager.getTotalProduction()
            }

            updateScreen()

            // Production speed depends on mood
            handler.postDelayed(this, getProductionDelay())
        }
    }
    private val moodRunnable = object : Runnable {

        override fun run() {

            if (!GameManager.hasUnlimitedMood()) {

                if (GameManager.mood > 0) {
                    GameManager.mood -= 1
                }
            }

            updateScreen()

            handler.postDelayed(this, 5000)
        }
    }
    private fun getProductionDelay(): Long {

        return when (GameManager.mood) {

            in 90..100 -> 500
            in 75..89 -> 1000
            in 60..74 -> 2000
            in 45..59 -> 3000
            in 30..44 -> 4000
            in 11..29 -> 5000

            else -> 1000
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eggCountText = findViewById(R.id.eggCountText)
        eggProduction = findViewById(R.id.eggsPerSecondText)

        moodText = findViewById(R.id.moodText)
        moodBar = findViewById(R.id.moodBar)
        feedButton = findViewById(R.id.feedButton)
        feederText = findViewById(R.id.feederText)

        chickenImage1 = findViewById(R.id.chickenImage1)
        chickenImage2 = findViewById(R.id.chickenImage2)
        chickenImage3 = findViewById(R.id.chickenImage3)

        chickenSlotText1 = findViewById(R.id.chickenSlotText1)
        chickenSlotText2 = findViewById(R.id.chickenSlotText2)
        chickenSlotText3 = findViewById(R.id.chickenSlotText3)

        val upgradesButton = findViewById<Button>(R.id.upgradesButton)
        val shopButton = findViewById<Button>(R.id.shopButton)
        val spinButton = findViewById<Button>(R.id.spinButton)
        val rebirthButton = findViewById<Button>(R.id.rebirthButton)

        updateScreen()

        upgradesButton.setOnClickListener {
            startActivity(Intent(this, UpgradesActivity::class.java))
        }

        shopButton.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }

        spinButton.setOnClickListener {
            startActivity(Intent(this, SpinActivity::class.java))
        }
        feedButton.setOnClickListener {

            val feeder = GameManager.feeder

            val canFeed =
                if (GameManager.hasUnlimitedFeeder()) {

                    true

                } else {

                    feeder.feed()
                }


            if (canFeed) {

                GameManager.mood +=
                    feeder.getMoodIncrease()


                if (GameManager.mood > 100) {

                    GameManager.mood =
                        100
                }


                updateScreen()
            }
        }
    }
    private fun updateScreen() {

        eggCountText.text =
            "Eggs: ${GameManager.eggs}"

        moodText.text =
            "Mood: ${GameManager.mood}%"

        moodBar.progress =
            GameManager.mood


        val production =
            GameManager.getTotalProduction()


        eggProduction.text = when (GameManager.mood) {

            in 90..100 ->
                "$production eggs every 0.5 seconds"

            in 75..89 ->
                "$production eggs every 1 second"

            in 60..74 ->
                "$production eggs every 2 seconds"

            in 45..59 ->
                "$production eggs every 3 seconds"

            in 30..44 ->
                "$production eggs every 4 seconds"

            in 11..29 ->
                "$production eggs every 5 seconds"

            else ->
                "No egg production"
        }
        feederText.text =
            "Feeder: ${GameManager.feeder.currentFood} / ${GameManager.feeder.getCapacity()}"

        updateChickenDisplay()
    }
    override fun onResume() {
        super.onResume()

        updateScreen()

        handler.removeCallbacks(eggRunnable)
        handler.removeCallbacks(moodRunnable)

        handler.post(eggRunnable)
        handler.post(moodRunnable)
    }

    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(eggRunnable)
        handler.removeCallbacks(moodRunnable)
    }
    private fun updateChickenDisplay() {

        updateChickenSlot(
            0,
            chickenImage1,
            chickenSlotText1
        )

        updateChickenSlot(
            1,
            chickenImage2,
            chickenSlotText2
        )

        updateChickenSlot(
            2,
            chickenImage3,
            chickenSlotText3
        )
    }
    private fun updateChickenSlot(
        slotIndex: Int,
        imageView: ImageView,
        textView: TextView
    ) {

        // There is an EQUIPPED chicken in this slot
        if (slotIndex < GameManager.equippedChickens.size) {

            val chicken =
                GameManager.equippedChickens[slotIndex]

            imageView.setImageResource(
                chicken.imageResId
            )

            imageView.visibility =
                View.VISIBLE

            textView.text =
                chicken.name
        }

        // Slot is unlocked, but no chicken is equipped
        else if (slotIndex < GameManager.maxChickenSlots) {

            imageView.setImageDrawable(null)

            imageView.visibility =
                View.INVISIBLE

            textView.text =
                "Empty Slot"
        }

        // Slot is still locked
        else {

            imageView.setImageDrawable(null)

            imageView.visibility =
                View.INVISIBLE

            textView.text =
                "🔒 Locked"
        }
    }
}