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
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.OverScroller
import cn.eric.customviewdemo.utils.dp2px
import cn.eric.customviewdemo.utils.getAvatar

/**
 * Created by eric on 2019/2/19
 */
class ScalableImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmap: Bitmap
    private val detector: GestureDetectorCompat
    private val scaleDetector: ScaleGestureDetector
    private val scroller = OverScroller(context)

    private var smallScale: Float = 0.0f
    private var bigScale: Float = 0.0f
    private var big = false

    private var originalOffsetX: Float = 0.0f
    private var originalOffsetY: Float = 0.0f
    private var offsetX: Float = 0.0f
    private var offsetY: Float = 0.0f

    private var currentScale = 0.0f
        set(value) {
            field = value
            invalidate()
        }
    private var _scaleAnimator: ObjectAnimator? = null
    private val scaleAnimator: ObjectAnimator
        get() {
            if (_scaleAnimator == null) {
                _scaleAnimator = ObjectAnimator.ofFloat(this, "currentScale", 0f)
            }

            return _scaleAnimator!!.apply { setFloatValues(smallScale, bigScale) }
        }

    private val gestureListener = MyGestureListener()
    private val flingRunnable = MyFlingRunnable()

    private val scaleGestureListener = MyScaleGestureListener()

    init {
        bitmap = getAvatar(context.resources, IMAGE_WIDTH.toInt())
        detector = GestureDetectorCompat(context, gestureListener)
        scaleDetector = ScaleGestureDetector(context, scaleGestureListener)
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
        currentScale = smallScale
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val scaleFraction = (currentScale - smallScale) / (bigScale - smallScale)
        canvas.translate(offsetX * scaleFraction, offsetY * scaleFraction)
        canvas.scale(currentScale, currentScale, width / 2f, height / 2f)
        canvas.drawBitmap(bitmap, originalOffsetX, originalOffsetY, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var result = scaleDetector.onTouchEvent(event)
        if (!scaleDetector.isInProgress) {
            result = detector.onTouchEvent(event)
        }
        return result
    }

    private fun fixOffsets() {
        offsetX = Math.min(offsetX, (bitmap.width * bigScale - width) / 2)
        offsetX = Math.max(offsetX, -(bitmap.width * bigScale - width) / 2)
        offsetY = Math.min(offsetY, (bitmap.height * bigScale - height) / 2)
        offsetY = Math.max(offsetY, -(bitmap.height * bigScale - height) / 2)
    }

    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
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

                postOnAnimation(flingRunnable)
            }
            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (big) {
                offsetX -= distanceX
                offsetY -= distanceY
                fixOffsets()
                invalidate()
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            big = !big
            if (big) {
                offsetX = e.x - width / 2 - (e.x - width / 2) * bigScale / smallScale
                offsetY = e.y - height / 2 - (e.y - height / 2) * bigScale / smallScale
                fixOffsets()
                scaleAnimator.start()
            } else {
                scaleAnimator.reverse()
            }
            return false
        }
    }

    private inner class MyFlingRunnable : Runnable {
        override fun run() {
            scroller.run {
                if (computeScrollOffset()) {
                    offsetX = currX.toFloat()
                    offsetY = currY.toFloat()
                    invalidate()
                    postOnAnimation(flingRunnable)
                }
            }
        }
    }

    private inner class MyScaleGestureListener : ScaleGestureDetector.OnScaleGestureListener {
        private var initialScale : Float = 0.0f
        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            initialScale = currentScale
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            currentScale = initialScale * detector.scaleFactor
            invalidate()
            return false
        }

    }

    companion object {
        private val IMAGE_WIDTH = dp2px(300f)
        private const val OVER_SCALE_FACTOR = 1.5f
    }
}
