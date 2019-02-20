package cn.eric.customviewdemo.widget.plus

import android.content.Context
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import cn.eric.customviewdemo.utils.getAvatar
import cn.eric.customviewdemo.utils.getZForCamera

/**
 * Created by eric on 2019/2/13
 */
class CameraView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val camera = Camera()

    init {
        camera.rotateX(30f)
        camera.setLocation(0f, 0f, getZForCamera())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制上半部分
        canvas.save()
        canvas.translate(100 + 600 / 2f, 100 + 600 / 2f)
        canvas.rotate(-20f)
        canvas.clipRect(-600, -600, 600, 0)
        canvas.rotate(20f)
        canvas.translate(-(100 + 600 / 2f), -(100 + 600 / 2f))
        canvas.drawBitmap(getAvatar(resources, 600), 100f, 100f, paint)
        canvas.restore()

        // 绘制下半部分
        canvas.save()
        canvas.translate(100 + 600 / 2f, 100 + 600 / 2f)
        canvas.rotate(-20f)
        camera.applyToCanvas(canvas)
        canvas.clipRect(-600, 0, 600, 600)
        canvas.rotate(20f)
        canvas.translate(-(100 + 600 / 2f), -(100 + 600 / 2f))
        canvas.drawBitmap(getAvatar(resources, 600), 100f, 100f, paint)
        canvas.restore()
    }

}