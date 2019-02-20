package cn.eric.customviewdemo.widget.plus

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import cn.eric.customviewdemo.utils.dp2px

/**
 * Created by eric on 2019/2/11
 */
class DashboardView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val arcPath = Path()
    private val dashPath = Path()
    private lateinit var rect: RectF
    private lateinit var effect: PathDashPathEffect

    init {
        paint.apply {
            style = Paint.Style.STROKE
            strokeWidth = dp2px(2f)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(TAG, "onSizeChanged:w=$w,h=$h,oldw=$oldw,oldh=$oldh")

        rect = RectF(w / 2 - RADIUS, h / 2 - RADIUS, w / 2 + RADIUS,
                h / 2 + RADIUS)
        arcPath.addArc(rect, 90 + ANGLE / 2f, 360 - ANGLE)
        val pathMeasure = PathMeasure(arcPath, false)

        dashPath.addRect(0f, 0f, dp2px(2f), dp2px(10f), Path.Direction.CW)
        effect = PathDashPathEffect(dashPath, (pathMeasure.length - dp2px(2f)) / 20, 0f, PathDashPathEffect.Style.ROTATE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 画弧线
        canvas.drawArc(rect, 90 + ANGLE / 2f, 360 - ANGLE, false, paint)
        paint.pathEffect = effect
        // 画刻度
        canvas.drawArc(rect, 90 + ANGLE / 2f, 360 - ANGLE, false, paint)
        paint.pathEffect = null

        // 画指针
        canvas.drawLine(width / 2f, height / 2f,
                width / 2f + Math.cos(Math.toRadians(getAngleFromMark(5))).toFloat() * LENGTH,
                height / 2f + Math.sin(Math.toRadians(getAngleFromMark(5))).toFloat() * LENGTH, paint)

    }

    private fun getAngleFromMark(mark: Int): Double {
        return 90 + ANGLE / 2.0 + (360 - ANGLE) / 20 * mark
    }

    companion object {
        private const val ANGLE = 120f
        private val LENGTH = dp2px(120f)
        private const val TAG = "DashboardView"
        private val RADIUS = dp2px(150f)
    }
}