package cn.eric.customviewdemo.widget.plus

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import cn.eric.customviewdemo.R
import cn.eric.customviewdemo.utils.dp2px

/**
 * Created by eric on 2019/2/15
 */
class MaterialEditText @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var floatingLabelShown = false
    private var floatingLabelFraction = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val animator = ObjectAnimator.ofFloat(this, "floatingLabelFraction", 0f, 1f)
    private val bgPaddingRect = Rect()

    var useFloatingLabel = true
        set(value) {
            if (field != value) {
                field = value
                onUseFloatingLabelChange()
            }
        }

    init {
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.MaterialEditText).apply {
                useFloatingLabel = getBoolean(R.styleable.MaterialEditText_useFloatingLabel, true)
                recycle()
            }
        }

        init()
    }

    private fun init() {
        paint.textSize = TEXT_SIZE
        background.getPadding(bgPaddingRect)
        onUseFloatingLabelChange()
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!useFloatingLabel) return

                if (floatingLabelShown && s.isNullOrEmpty()) {
                    floatingLabelShown = false
                    animator.reverse()
                } else if (!floatingLabelShown && !s.isNullOrEmpty()) {
                    floatingLabelShown = true
                    animator.start()
                }
            }
        })
    }

    private fun onUseFloatingLabelChange() {
        if (useFloatingLabel) {
            setPadding(paddingLeft, (bgPaddingRect.top + TEXT_SIZE + TEXT_MARGIN).toInt(), paddingRight, paddingBottom)
        } else {
            setPadding(paddingLeft, bgPaddingRect.top, paddingRight, paddingBottom)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (useFloatingLabel) {
            paint.alpha = (floatingLabelFraction * 0xff).toInt()
            val extraOffset = TEXT_ANIMATION_OFFSET * (1 - floatingLabelFraction)
            canvas.drawText(hint.toString(), TEXT_HORIZONTAL_OFFSET, TEXT_VERTICAL_OFFSET + extraOffset, paint)
        }
    }

    companion object {
        private val TEXT_SIZE = dp2px(16f)
        private val TEXT_MARGIN = dp2px(16f)
        private val TEXT_VERTICAL_OFFSET = dp2px(22f)
        private val TEXT_HORIZONTAL_OFFSET = dp2px(1f)
        private val TEXT_ANIMATION_OFFSET = dp2px(16f)
    }
}
