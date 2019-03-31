package cn.eric.customviewdemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import cn.eric.customviewdemo.utils.dp2px


/**
 * Created by eric on 2019/3/31
 */
class LetterIndexView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var hit: Boolean = false
    private var touchingLetterChangedListener: OnTouchingLetterChangedListener? = null
    private val letters = arrayOf("↑", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")

    private val paint: Paint = Paint(ANTI_ALIAS_FLAG)

    init {
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.parseColor("#564F5F")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getWidthSize(widthMeasureSpec), View.getDefaultSize(suggestedMinimumHeight, heightMeasureSpec))
    }

    private fun getWidthSize(widthMeasureSpec: Int): Int {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        return when (widthMode) {
            View.MeasureSpec.AT_MOST -> {
                if (widthSize >= DEFAULT_WIDTH) {
                    DEFAULT_WIDTH
                } else {
                    widthSize
                }
            }
            View.MeasureSpec.EXACTLY -> {
                widthSize
            }
            View.MeasureSpec.UNSPECIFIED -> DEFAULT_WIDTH
            else -> DEFAULT_WIDTH
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                hit = true
                onHit(event.y)
            }
            MotionEvent.ACTION_MOVE -> onHit(event.y)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                hit = false
                touchingLetterChangedListener?.onCancel()
            }
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (hit) {
            //字母索引条背景色
            canvas.drawColor(Color.parseColor("#bababa"))
        }
        val letterHeight = height.toFloat() / letters.size
        val width = width.toFloat()
        val textSize = letterHeight * 5 / 7
        paint.textSize = textSize
        for (i in 0 until letters.size) {
            canvas.drawText(letters[i], width / 2, letterHeight * i + textSize, paint)
        }
    }

    private fun onHit(offset: Float) {
        if (hit) {
            var index = (offset / height * letters.size).toInt()
            index = Math.max(index, 0)
            index = Math.min(index, letters.size - 1)
            touchingLetterChangedListener?.onHit(letters[index])
        }
    }

    fun setOnTouchingLetterChangedListener(listener: OnTouchingLetterChangedListener) {
        touchingLetterChangedListener = listener
    }

    companion object {
        private val DEFAULT_WIDTH = dp2px(24f).toInt()
    }

    interface OnTouchingLetterChangedListener {
        fun onHit(letter: String)
        fun onCancel()
    }
}
