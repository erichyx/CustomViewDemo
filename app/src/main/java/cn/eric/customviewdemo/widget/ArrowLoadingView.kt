package cn.eric.customviewdemo.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cn.eric.customviewdemo.R

/**
 * Created by eric on 2018/6/30
 */
class ArrowLoadingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val defaultPaint: Paint = Paint()
    private val paint: Paint = Paint()
    private var viewWidth = 0f
    private var viewHeight = 0f
    private val pos = FloatArray(2)
    private val tan = FloatArray(2)
    private val arrowBmp : Bitmap
    private val customMatrix = Matrix()
    private var lengthPercent = 0f
    private val path = Path()
    private val pathMeasure = PathMeasure()

    init {
        defaultPaint.color = Color.RED
        defaultPaint.style = Paint.Style.STROKE
        defaultPaint.strokeWidth = 5f

        paint.color = Color.DKGRAY
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f

        //Path.Direction.CW顺时针方向
        path.addCircle(0f, 0f, 200f, Path.Direction.CW)
        pathMeasure.setPath(path, false)

        val options = BitmapFactory.Options()
        options.inSampleSize = 4       // 缩放图片
        arrowBmp = BitmapFactory.decodeResource(context.resources, R.drawable.arrow, options)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        viewWidth = w.toFloat()
        viewHeight = h.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        lengthPercent += 0.005f
        if (lengthPercent >= 1) {
            lengthPercent = 0f
        }

        canvas.drawColor(Color.WHITE)
        canvas.translate(viewWidth / 2, viewHeight / 2)

        pathMeasure.getPosTan(pathMeasure.length * lengthPercent, pos, tan)

        val degree = (Math.atan2(tan[1].toDouble(), tan[0].toDouble()) * 180 / Math.PI).toFloat()

        customMatrix.reset()
        customMatrix.postRotate(degree, (arrowBmp.width / 2).toFloat(), (arrowBmp.height / 2).toFloat())
        customMatrix.postTranslate(pos[0] - arrowBmp.width / 2, pos[1] - arrowBmp.height / 2)

        canvas.drawPath(path, defaultPaint)
        canvas.drawBitmap(arrowBmp, customMatrix, paint)

        invalidate()
    }
}