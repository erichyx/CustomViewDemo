package cn.eric.customviewdemo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.support.v4.graphics.PathParser
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import cn.eric.customviewdemo.R
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory


/**
 * Created by eric on 2018/8/3
 */
class SvgMapView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var scale = 1.0f

    private var totalRect: RectF? = null
    private val colorArray = intArrayOf(Color.parseColor("#5EE63E"), Color.parseColor("#EA3DD4"),
            Color.parseColor("#3FDDE8"), Color.parseColor("#E7DD40"))
    private val itemList: MutableList<RegionItem> = mutableListOf()
    private var selectItem: RegionItem? = null

    private val loadThread = object : Thread() {
        @SuppressLint("RestrictedApi")
        override fun run() {
            val inputStream = resources.openRawResource(R.raw.china)
            try {

                val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                val doc = builder.parse(inputStream)   //解析输入流 得到Document实例
                val rootElement = doc.documentElement
                val items = rootElement.getElementsByTagName("path")

                var left = Float.MAX_VALUE
                var right = Float.MIN_VALUE
                var top = Float.MAX_VALUE
                var bottom = Float.MIN_VALUE
                for (i in 0 until items.length) {
                    val element = items.item(i) as Element
                    val pathData = element.getAttribute("d")
                    val name = element.getAttribute("title")
                    val path = PathParser.createPathFromPathData(pathData)
                    itemList.add(RegionItem(name, path))

                    // 获取宽高
                    val rect = RectF()
                    path.computeBounds(rect, true)
                    left = Math.min(left, rect.left)
                    right = Math.max(right, rect.right)
                    top = Math.min(top, rect.top)
                    bottom = Math.max(bottom, rect.bottom)
                }

                totalRect = RectF(left, top, right, bottom)
                handler.sendEmptyMessage(1)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    init {
        loadThread.start()
    }

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            itemList.forEachIndexed { i: Int, regionItem: RegionItem ->
                val flag = i % 4
                regionItem.drawColor = colorArray[flag]
            }
//            postInvalidate()
            requestLayout()
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        totalRect?.let {
            val mapWidth = it.width()
            scale = width / mapWidth
        }

        setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.scale(scale, scale)
        for (item in itemList) {
            if (item != selectItem) {
                item.drawItem(canvas, paint)
            }
        }
        selectItem?.drawItem(canvas, paint, true)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var tempItem: RegionItem? = null
        for (item in itemList) {
            if (item.contains(event.x / scale, event.y / scale)) {
                tempItem = item
            }
        }

        selectItem = tempItem?.apply {
            postInvalidate()
        }

        return super.onTouchEvent(event)
    }
}

data class RegionItem(val regionName: String, val path: Path, var drawColor: Int = 0) {
    fun contains(x: Float, y: Float): Boolean {
        val rectF = RectF()
        path.computeBounds(rectF, true)

        val region = Region()
        region.setPath(path, Region(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()))
        return region.contains(x.toInt(), y.toInt())
    }

    fun drawItem(canvas: Canvas, paint: Paint, isSelected: Boolean = false) {
        if (isSelected) {

            paint.clearShadowLayer()
            paint.color = drawColor
            paint.style = Paint.Style.FILL
            canvas.drawPath(path, paint)

            paint.strokeWidth = 2f
            paint.style = Paint.Style.STROKE
            paint.color = 0xFFD0E8F4.toInt()
            canvas.drawPath(path, paint)
        } else {
            paint.clearShadowLayer()
            paint.color = drawColor
            paint.style = Paint.Style.FILL
            canvas.drawPath(path, paint)

            paint.strokeWidth = 1f
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            paint.setShadowLayer(8f, 0f, 0f, 0xffffff)
            canvas.drawPath(path, paint)
        }
    }
}
