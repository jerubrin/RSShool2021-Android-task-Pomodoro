package com.jerubrin.pomodoro.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import com.jerubrin.pomodoro.R

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var periodMs = 0L
    private var currentMs = 0L
    private var fromColor = 0
    private var toColor = 0xFF0000
    private var style = FILL
    private val paint = Paint()

    private var fromR = 0
    private var fromG = 0
    private var fromB = 0
    private var fromA = 0

    private var toR = 0
    private var toG = 0
    private var toB = 0
    private var toA = 0

    val isSet: Boolean get() = (periodMs != 0L)

    init {
        if (attrs != null) {
            val styledAttrs = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CustomView,
                defStyleAttr,
                0
            )
            fromColor = styledAttrs.getColor(R.styleable.CustomView_from_color, Color.BLUE)
            toColor = styledAttrs.getColor(R.styleable.CustomView_to_color, Color.RED)
            style = styledAttrs.getInt(R.styleable.CustomView_custom_style, FILL)
            styledAttrs.recycle()
        }
        fromR = Color.red(fromColor)
        fromG = Color.green(fromColor)
        fromB = Color.blue(fromColor)
        fromA = Color.alpha(fromColor)

        toR = Color.red(toColor)
        toG = Color.green(toColor)
        toB = Color.blue(toColor)
        toA = Color.alpha(toColor)

        paint.style = if (style == FILL) Paint.Style.FILL else Paint.Style.STROKE
        paint.strokeWidth = 5F
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (periodMs == 0L || currentMs == 0L) return
        if (periodMs < currentMs) currentMs = periodMs

        val progress = width.toFloat() * (1.0f - currentMs.toFloat() / periodMs.toFloat() )
        if (currentMs < periodMs) {
            paint.color = getRGB(1.0 - currentMs.toDouble() / periodMs.toDouble())
        } else {
            paint.color = fromColor
        }

        canvas.drawRect(
            0f,
            0f,
            progress,
            height.toFloat(),
            paint
        )
    }

    private inline fun getRGB(progress: Double) : Int{
        val r = if (fromR > toR)
            fromR.toDouble() - (fromR - toR).toDouble() * progress
        else
            fromR.toDouble() + (toR - fromR).toDouble() * progress

        val g = if (fromG > toG)
            fromG.toDouble() - (fromG - toG).toDouble() * progress
        else
            fromG.toFloat() + (toG - fromG).toDouble() * progress

        val b = if (fromB > toB)
            fromB.toDouble() - (fromB - toB).toDouble() * progress
        else
            fromB.toDouble() + (toB - fromB).toDouble() * progress

        val a = if (fromA > toA)
            fromA.toDouble() - (fromA - toA).toDouble() * progress
        else
            fromA.toDouble() + (toA - fromA).toDouble() * progress

        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }

    /**
     * Set lasted milliseconds
     */
    fun setCurrent(current: Long) {
        currentMs = current
        invalidate()
    }

    /**
     * Set time period
     */
    fun setPeriod(period: Long?) {
        periodMs = period ?: 0
    }

    private companion object {
        private const val FILL = 0
    }
}