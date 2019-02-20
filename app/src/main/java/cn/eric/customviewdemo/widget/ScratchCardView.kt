package cn.eric.customviewdemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import cn.eric.customviewdemo.R

/**
 * Created by eric on 2018/6/29
 */
class ScratchCardView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val bitPaint: Paint

    private val bmpSrc: Bitmap
    private val bmpText: Bitmap
    private val bmpDst: Bitmap
    private val path = Path()

    private val bmpDstCanvas: Canvas
    private val duffXfermode: PorterDuffXfermode

    private var preX = 0f
    private var preY = 0f

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        bitPaint = Paint()
        bitPaint.color = Color.RED
        bitPaint.style = Paint.Style.STROKE
        bitPaint.strokeWidth = 45f

        bmpSrc = BitmapFactory.decodeResource(resources, R.drawable.scratch_card, null)
        bmpText = BitmapFactory.decodeResource(resources, R.drawable.scratch_card_text, null)
        bmpDst = Bitmap.createBitmap(bmpSrc.width, bmpSrc.height, Bitmap.Config.ARGB_8888)
        bmpDstCanvas = Canvas(bmpDst)
        duffXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制文本图像
        canvas.drawBitmap(bmpText, 0f, 0f, bitPaint)

        val layerId = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        // 先把手指轨迹画到目标Bitmap上
        bmpDstCanvas.drawPath(path, bitPaint)
        // 然后把目标图像画到画布上
        canvas.drawBitmap(bmpDst, 0f, 0f, bitPaint)

        // 计算源图像区域
        bitPaint.xfermode = duffXfermode
        canvas.drawBitmap(bmpSrc, 0f, 0f, bitPaint)

        bitPaint.xfermode = null
        canvas.restoreToCount(layerId)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                preX = event.x
                preY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = (preX + event.x) / 2
                val endY = (preY + event.y) / 2
                path.quadTo(preX, preY, endX, endY)
                preX = event.x
                preY = event.y
            }
        }

        postInvalidate()
        return super.onTouchEvent(event)
    }
}