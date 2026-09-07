package com.example.chickeninc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class PrizeWheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var prizes: List<WheelPrize> =
        emptyList()


    private val segmentPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private val borderPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            style = Paint.Style.STROKE

            strokeWidth = dp(6f)

            color = Color.WHITE
        }


    private val dividerPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            style = Paint.Style.STROKE

            strokeWidth = dp(3f)

            color = Color.WHITE
        }


    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            textAlign = Paint.Align.CENTER

            typeface =
                Typeface.DEFAULT_BOLD

            textSize = sp(10f)

            color = Color.WHITE

            setShadowLayer(
                dp(3f),
                0f,
                dp(1f),
                Color.BLACK
            )
        }


    private val centerPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color =
                Color.parseColor("#FF4D6D")
        }


    init {

        setLayerType(
            LAYER_TYPE_SOFTWARE,
            null
        )
    }


    fun setPrizes(
        newPrizes: List<WheelPrize>
    ) {

        prizes = newPrizes

        invalidate()
    }


    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)


        if (prizes.size != 8) {
            return
        }


        val size =
            min(width, height).toFloat()

        val centerX =
            width / 2f

        val centerY =
            height / 2f

        val radius =
            size * 0.46f


        val wheelRect =
            RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
            )


        val segmentAngle =
            360f / 8f


        // Makes slot 0 centered directly
        // underneath the top pointer
        val firstStartAngle =
            -90f - segmentAngle / 2f


        // -------------------------
        // DRAW SEGMENTS
        // -------------------------

        for (index in prizes.indices) {

            val prize =
                prizes[index]

            val startAngle =
                firstStartAngle +
                        index * segmentAngle


            segmentPaint.color =
                getPrizeColor(prize)


            canvas.drawArc(
                wheelRect,
                startAngle,
                segmentAngle,
                true,
                segmentPaint
            )


            drawPrize(
                canvas,
                prize,
                startAngle +
                        segmentAngle / 2f,
                centerX,
                centerY,
                radius
            )
        }


        // -------------------------
        // DIVIDING LINES
        // -------------------------

        for (index in 0 until 8) {

            val angle =
                Math.toRadians(
                    (
                            firstStartAngle +
                                    index *
                                    segmentAngle
                            ).toDouble()
                )


            val endX =
                centerX +
                        cos(angle).toFloat() *
                        radius

            val endY =
                centerY +
                        sin(angle).toFloat() *
                        radius


            canvas.drawLine(
                centerX,
                centerY,
                endX,
                endY,
                dividerPaint
            )
        }


        // Outer white ring
        canvas.drawCircle(
            centerX,
            centerY,
            radius,
            borderPaint
        )


        // Center hub
        canvas.drawCircle(
            centerX,
            centerY,
            radius * 0.18f,
            centerPaint
        )


        canvas.drawCircle(
            centerX,
            centerY,
            radius * 0.18f,
            borderPaint
        )
    }


    private fun drawPrize(
        canvas: Canvas,
        prize: WheelPrize,
        angleDegrees: Float,
        centerX: Float,
        centerY: Float,
        radius: Float
    ) {

        val angle =
            Math.toRadians(
                angleDegrees.toDouble()
            )


        // Position of chicken image
        val iconDistance =
            radius * 0.58f


        val iconX =
            centerX +
                    cos(angle).toFloat() *
                    iconDistance

        val iconY =
            centerY +
                    sin(angle).toFloat() *
                    iconDistance


        if (prize is WheelPrize.ChickenPrize) {

            val drawable =
                ContextCompat.getDrawable(
                    context,
                    prize.chicken.imageResId
                )


            if (drawable != null) {

                val iconSize =
                    dp(52f).toInt()


                drawable.setBounds(
                    (iconX - iconSize / 2).toInt(),
                    (iconY - iconSize / 2).toInt(),
                    (iconX + iconSize / 2).toInt(),
                    (iconY + iconSize / 2).toInt()
                )


                drawable.draw(canvas)
            }

        } else {

            textPaint.textSize =
                sp(9f)


            canvas.drawText(
                "GADGET",
                iconX,
                iconY,
                textPaint
            )
        }


        // Prize name closer to edge
        val textDistance =
            radius * 0.82f


        val textX =
            centerX +
                    cos(angle).toFloat() *
                    textDistance

        val textY =
            centerY +
                    sin(angle).toFloat() *
                    textDistance


        textPaint.textSize =
            sp(9f)


        canvas.drawText(
            getShortName(prize),
            textX,
            textY,
            textPaint
        )
    }


    private fun getShortName(
        prize: WheelPrize
    ): String {

        return when (prize) {

            is WheelPrize.ChickenPrize -> {

                val name =
                    prize.chicken.name
                        .replace(
                            " Chicken",
                            ""
                        )

                if (name.length > 10) {
                    name.take(9) + "."
                } else {
                    name
                }
            }


            is WheelPrize.GadgetPrize -> {

                when (prize.gadget) {

                    WheelGadget.UNLIMITED_FEEDER ->
                        "FEED ∞"

                    WheelGadget.UNLIMITED_MOOD ->
                        "MOOD ∞"

                    WheelGadget.SPIN_ROLL ->
                        "+1 ROLL"
                }
            }
        }
    }


    private fun getPrizeColor(
        prize: WheelPrize
    ): Int {

        if (prize is WheelPrize.GadgetPrize) {

            return Color.parseColor(
                "#FF6D00"
            )
        }


        val chicken =
            (prize as WheelPrize.ChickenPrize)
                .chicken


        return when (chicken.rarity) {

            Rarity.COMMON ->
                Color.parseColor("#9E9E9E")

            Rarity.UNCOMMON ->
                Color.parseColor("#64DD17")

            Rarity.RARE ->
                Color.parseColor("#29B6F6")

            Rarity.EPIC ->
                Color.parseColor("#AB47BC")

            Rarity.LEGENDARY ->
                Color.parseColor("#FFD54F")

            Rarity.SECRET ->
                Color.parseColor("#151515")
        }
    }


    private fun dp(
        value: Float
    ): Float {

        return value *
                resources.displayMetrics.density
    }


    private fun sp(
        value: Float
    ): Float {

        return value *
                resources.displayMetrics.scaledDensity
    }
}