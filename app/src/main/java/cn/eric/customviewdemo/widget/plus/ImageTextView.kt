package cn.eric.customviewdemo.widget.plus

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cn.eric.customviewdemo.R
import cn.eric.customviewdemo.utils.dp2px
import cn.eric.customviewdemo.utils.getAvatar
import com.bumptech.glide.Glide.init


/**
 * Created by eric on 2019/2/12
 */
class ImageTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmap = getAvatar(resources, dp2px(100f).toInt())
    private val cutWidth = FloatArray(1)

    init {
        paint.textSize = dp2px(12f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(bitmap, width - dp2px(100f), 100f, paint)
        val text = "使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 " +
                "使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 " +
                "使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 " +
                "使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 " +
                "使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 使用Camera做三维旋转 " +
                "使用Camera做三维旋转 "
        var index = paint.breakText(text, true, width.toFloat(), cutWidth)
        canvas.drawText(text, 0, index, 0f, 50f, paint)

        var oldIndex = index
        index = paint.breakText(text, index, text.length, true, width.toFloat(), cutWidth)
        canvas.drawText(text, oldIndex, oldIndex + index, 0f, 50f + paint.fontSpacing, paint)

        oldIndex = index
        index = paint.breakText(text, index, text.length, true, width - dp2px(100f), cutWidth)
        canvas.drawText(text, oldIndex, oldIndex + index, 0f, 50f + paint.fontSpacing * 2, paint)
    }

    companion object {
        private val RING_WIDTH = dp2px(20f)
        private val RADIUS = dp2px(150f)
        private val CIRCLE_COLOR = Color.parseColor("#90A4AE")
        private val HIGHLIGHT_COLOR = Color.parseColor("#FF4081")
    }
}