package cn.eric.customviewdemo.widget.plus

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cn.eric.customviewdemo.utils.dp2px


/**
 * Created by eric on 2019/2/12
 */
class SportsView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = Rect()
    private val arcRect = RectF()
    private val metrics = Paint.FontMetrics()

    init {
        paint.apply {
            textSize = dp2px(100f)
            typeface = Typeface.createFromAsset(getContext().assets, "Quicksand-Regular.ttf")
            textAlign = Paint.Align.CENTER
            getFontMetrics(metrics)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        arcRect.set(width / 2 - RADIUS, height / 2 - RADIUS, width / 2 + RADIUS, height / 2 + RADIUS)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制环
        paint.style = Paint.Style.STROKE
        paint.color = CIRCLE_COLOR
        paint.strokeWidth = RING_WIDTH
        canvas.drawCircle(width / 2f, height / 2f, RADIUS, paint)

        // 绘制进度条
        paint.color = HIGHLIGHT_COLOR
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawArc(arcRect, -90f, 225f, false, paint)

        // 绘制文字
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
//        paint.getTextBounds("abab",0,"abab".length, rect)
//        val offset = (rect.top + rect.bottom) / 2
        val offset = (metrics.ascent + metrics.descent) / 2
        canvas.drawText("abab", width / 2f, height / 2f - offset, paint)

        // 绘制文字2
        paint.textAlign = Paint.Align.LEFT
        paint.getTextBounds("aaaa",0,"aaaa".length, rect)
        canvas.drawText("aaaa", -rect.left.toFloat(), -offset*2, paint)

    }

    companion object {
        private val RING_WIDTH = dp2px(20f)
        private val RADIUS = dp2px(150f)
        private val CIRCLE_COLOR = Color.parseColor("#90A4AE")
        private val HIGHLIGHT_COLOR = Color.parseColor("#FF4081")
    }
}