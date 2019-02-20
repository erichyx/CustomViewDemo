package cn.eric.customviewdemo.widget.plus

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cn.eric.customviewdemo.utils.dp2px
import cn.eric.customviewdemo.utils.getAvatar

/**
 * Created by eric on 2019/2/11
 */
class AvatarView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmap = getAvatar(resources, WIDTH)
    private val xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val savedArea = RectF(PADDING, PADDING, PADDING + WIDTH, PADDING + WIDTH)
    private val ovalRect = RectF(PADDING + EDGE_WIDTH, PADDING + EDGE_WIDTH, PADDING + WIDTH - EDGE_WIDTH, PADDING + WIDTH - EDGE_WIDTH)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawOval(savedArea, paint)
        val saveLayer = canvas.saveLayer(savedArea, paint, Canvas.ALL_SAVE_FLAG)
        canvas.drawOval(ovalRect, paint)
        paint.xfermode = xfermode
        canvas.drawBitmap(bitmap, PADDING, PADDING, paint)
        paint.xfermode = null
        canvas.restoreToCount(saveLayer)
    }

    companion object {
        private val WIDTH = dp2px(300f).toInt()
        private val PADDING = dp2px(50f)
        private val EDGE_WIDTH = dp2px(10f)
    }


}