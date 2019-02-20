package cn.eric.customviewdemo.widget.plus

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import cn.eric.customviewdemo.utils.dp2px

/**
 * Created by eric on 2019/2/11
 */
class PieChartView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bounds = RectF()

    private val angles = floatArrayOf(60f, 100f, 120f, 80f)
    private val colors = intArrayOf(Color.parseColor("#2979FF"), Color.parseColor("#C2185B"),
            Color.parseColor("#009688"), Color.parseColor("#FF8F00"))

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        bounds.set(w / 2 - RADIUS, h / 2 - RADIUS, w / 2 + RADIUS, h / 2 + RADIUS)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var currentAngle = 0f
        angles.forEachIndexed { index, angle ->
            paint.color = colors[index]
            canvas.save()
            if (PULL_OUT_INDEX == index) {
                canvas.translate(Math.cos(Math.toRadians(currentAngle + angle / 2.0)).toFloat() * LENGTH,
                        Math.sin(Math.toRadians(currentAngle + angle / 2.0)).toFloat() * LENGTH)
            }
            canvas.drawArc(bounds, currentAngle, angle, true, paint)
            canvas.restore()
            currentAngle += angle
        }
    }

    companion object {
        private val RADIUS = dp2px(150f)
        private val LENGTH = dp2px(20f)
        private const val PULL_OUT_INDEX = 2
    }
}