package cn.eric.customviewdemo.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.TypedValue
import cn.eric.customviewdemo.R

/**
 * Created by eric on 2018/10/4
 */

fun getScreenWidth(context: Context): Int {
    return context.resources.displayMetrics.widthPixels
}

fun getScreenHeight(context: Context): Int {
    return context.resources.displayMetrics.heightPixels
}

fun dp2px(dp: Float) : Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics)
}

fun px2dp(px: Int) : Float {
    return px / Resources.getSystem().displayMetrics.density
}

fun getAvatar(resources : Resources, width: Int): Bitmap {
    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeResource(resources, R.drawable.avatar, options)
    options.apply {
        inJustDecodeBounds = false
        inDensity = outWidth
        inTargetDensity = width
    }
    return BitmapFactory.decodeResource(resources, R.drawable.avatar, options)
}

fun getZForCamera() : Float {
    return Resources.getSystem().displayMetrics.density * -4
}