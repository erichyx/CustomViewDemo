package cn.eric.customviewdemo.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import cn.eric.customviewdemo.R

/**
 * Created by eric on 2018/10/16
 */
class HonorAvatarView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var frameBitmap: Bitmap
    private var honorLabelBitmap: Bitmap? = null
    private var placeholderBitmap: Bitmap? = null
    private var avatarBitmap: Bitmap? = null

    private val paint = Paint()
    private var duffXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.HonorAvatarView)

            val frameResId = typedArray.getResourceId(R.styleable.HonorAvatarView_frame, R.drawable.challenge_head_frame)
            frameBitmap = BitmapFactory.decodeResource(resources, frameResId)

            val avatarResId = typedArray.getResourceId(R.styleable.HonorAvatarView_avatar, 0)
            avatarBitmap = BitmapFactory.decodeResource(resources, avatarResId)

            val honorResId = typedArray.getResourceId(R.styleable.HonorAvatarView_honorLabel, 0)
            honorLabelBitmap = BitmapFactory.decodeResource(resources, honorResId)

            val placeholderId = typedArray.getResourceId(R.styleable.HonorAvatarView_frame, R.drawable.challenge_head_bg)
            placeholderBitmap = BitmapFactory.decodeResource(resources, placeholderId)

            typedArray.recycle()
        }
        setLayerType(LAYER_TYPE_SOFTWARE,null)
    }

    public fun setAvatar(bitmap: Bitmap) {
        avatarBitmap = bitmap
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var left : Float
        var top : Float
        placeholderBitmap?.let {
            left = (width - it.width) / 2f
            top = (height - it.height) / 2f
            canvas.drawBitmap(it, left, top, paint)
        }

//        val layerId = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        paint.xfermode = duffXfermode

        avatarBitmap?.let {
            left = (width - it.width) / 2f
            canvas.drawBitmap(it, left, 0f, paint)
        }


        paint.xfermode = null

        left = (width - frameBitmap.width) / 2f
        top = (height - frameBitmap.height) / 2f
        canvas.drawBitmap(frameBitmap, left, top, paint)

        honorLabelBitmap?.let {
            top = (height - it.height).toFloat()
            canvas.drawBitmap(it, 0f, top, paint)
        }
//        canvas.restoreToCount(layerId)
    }
}