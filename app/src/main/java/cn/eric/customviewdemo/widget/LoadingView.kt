package cn.eric.customviewdemo.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View

/**
 * Created by eric on 2018/6/30
 */
class LoadingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pathMeasure = PathMeasure()
    private var animatorValue = 0f
    private val dst = Path()
    private var length: Float = 0f

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f

        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addUpdateListener {
            animatorValue = it.animatedValue as Float
            invalidate()
        }
        valueAnimator.duration = 2000
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val path = Path()
        path.reset()
        path.addCircle(w / 2f, h / 2f, 200f, Path.Direction.CW)
        pathMeasure.setPath(path, true)
        length = pathMeasure.length
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        dst.reset()
        // 硬件加速的BUG
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            dst.rLineTo(0f, 0f)
        }

        val stop = animatorValue * length
        val start = stop - (0.5f - Math.abs(animatorValue - 0.5f)) * length

        pathMeasure.getSegment(start, stop, dst, true)
        canvas.drawPath(dst, paint)
    }
}