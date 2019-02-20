package cn.eric.customviewdemo.widget.plus

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import cn.eric.customviewdemo.utils.dp2px
import cn.eric.customviewdemo.utils.getAvatar

/**
 * Created by eric on 2019/2/19
 */
class ScalableImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmap: Bitmap
    private val detector: GestureDetectorCompat
    private val scroller = OverScroller(context)

    private var smallScale: Float = 0.0f
    private var bigScale: Float = 0.0f
    private var big = false

    private var originalOffsetX: Float = 0.0f
    private var originalOffsetY: Float = 0.0f
    private var offsetX: Float = 0.0f
    private var offsetY: Float = 0.0f

    private var scaleFraction = 0.0f
        set(value) {
            field = value
            invalidate()
        }
    private var _scaleAnimator: ObjectAnimator? = null
    private val scaleAnimator: ObjectAnimator
        get() {
            if (_scaleAnimator == null) {
                _scaleAnimator = ObjectAnimator.ofFloat(this, "scaleFraction", 0f, 1f)
            }

            return _scaleAnimator!!
        }

    init {
        bitmap = getAvatar(context.resources, IMAGE_WIDTH.toInt())
        detector = GestureDetectorCompat(context, this)
//        detector.setOnDoubleTapListener(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        originalOffsetX = (w - bitmap.width) / 2f
        originalOffsetY = (h - bitmap.height) / 2f

        if (bitmap.width.toFloat() / bitmap.height > w.toFloat() / h) {
            smallScale = w.toFloat() / bitmap.width
            bigScale = h.toFloat() / bitmap.height * OVER_SCALE_FACTOR
        } else {
            smallScale = h.toFloat() / bitmap.height
            bigScale = w.toFloat() / bitmap.width * OVER_SCALE_FACTOR
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.translate(offsetX, offsetY)
        val scale = smallScale + (bigScale - smallScale) * scaleFraction
        canvas.scale(scale, scale, width / 2f, height / 2f)
        canvas.drawBitmap(bitmap, originalOffsetX, originalOffsetY, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return detector.onTouchEvent(event)
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        if (big) {
            scroller.fling(offsetX.toInt(), offsetY.toInt(), velocityX.toInt(), velocityY.toInt(),
                    -((bitmap.width * bigScale - width) / 2).toInt(),
                    ((bitmap.width * bigScale - width) / 2).toInt(),
                    -((bitmap.height * bigScale - height)).toInt() / 2,
                    ((bitmap.height * bigScale - height) / 2).toInt())
        }
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        if (big) {
            offsetX -= distanceX
            offsetX = Math.min(offsetX, (bitmap.width * bigScale - width) / 2)
            offsetX = Math.max(offsetX, -(bitmap.width * bigScale - width) / 2)

            offsetY -= distanceY
            offsetY = Math.min(offsetY, (bitmap.height * bigScale - height) / 2)
            offsetY = Math.max(offsetY, -(bitmap.height * bigScale - height) / 2)
            invalidate()
        }
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        big = !big
        if (big) scaleAnimator.start() else scaleAnimator.reverse()
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return false
    }

    companion object {
        private val IMAGE_WIDTH = dp2px(300f)
        private const val OVER_SCALE_FACTOR = 1.5f
    }


}
