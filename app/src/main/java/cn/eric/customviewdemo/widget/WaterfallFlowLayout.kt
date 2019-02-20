package cn.eric.customviewdemo.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.View.MeasureSpec.*
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT

/**
 * Created by eric on 2018/6/16
 */
class WaterfallFlowLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.d("eric", "onLayout......")

        var startX = paddingLeft
        var startY = paddingTop
        val selfWidth = measuredWidth
        val selfHeight = measuredHeight

        var childWidthSpace: Int
        var curLineMaxHeight = 0
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (childView.visibility == View.GONE) {
                continue
            }

            val layoutParams = childView.layoutParams as MarginLayoutParams
            val childMeasuredWidth = childView.measuredWidth
            val childMeasuredHeight = childView.measuredHeight

            childWidthSpace = childMeasuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
            // 是否需要换行
            if (startX + childWidthSpace > selfWidth - paddingRight) {
                startX = paddingLeft
                startY += curLineMaxHeight
                curLineMaxHeight = 0
            }

            val left = startX + layoutParams.leftMargin
            val top = startY + layoutParams.topMargin
            val right = left + childMeasuredWidth
            val bottom = top + childMeasuredHeight
            childView.layout(left, top, right, bottom)

            startX += childWidthSpace
            curLineMaxHeight = Math.max(curLineMaxHeight, childMeasuredHeight
                    + layoutParams.topMargin + layoutParams.bottomMargin)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        Log.d("eric", "onMeasure......")

        val selfWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val selfWidthSize = MeasureSpec.getSize(widthMeasureSpec)

        val selfHeightMode = MeasureSpec.getMode(heightMeasureSpec)
        val selfHeightSize = MeasureSpec.getSize(heightMeasureSpec)

        // 自身测量后的宽高
        var selfMeasureWidth = 0
        var selfMeasureHeight = 0

        // 当前View的宽高
        var childWidth: Int
        var childHeight: Int

        // 当前行的宽高
        var curLineWidth = 0
        var curLineMaxHeight = 0

        for (i in 0 until childCount) {
            // 测量子View
            val child = getChildAt(i)
            val layoutParams = child.layoutParams as MarginLayoutParams

            val childWidthSpec = when (layoutParams.width) {
                MATCH_PARENT -> {
                    if (selfWidthMode == EXACTLY || selfWidthMode == AT_MOST)
                        MeasureSpec.makeMeasureSpec(selfWidthSize, EXACTLY)
                    else
                        MeasureSpec.makeMeasureSpec(0, UNSPECIFIED)
                }
                WRAP_CONTENT -> {
                    if (selfWidthMode == EXACTLY || selfWidthMode == AT_MOST)
                        MeasureSpec.makeMeasureSpec(selfWidthSize, AT_MOST)
                    else
                        MeasureSpec.makeMeasureSpec(0, UNSPECIFIED)
                }
                else -> MeasureSpec.makeMeasureSpec(layoutParams.width, EXACTLY)
            }

            val childHeightSpec = when (layoutParams.height) {
                MATCH_PARENT -> {
                    if (selfHeightMode == EXACTLY || selfHeightMode == AT_MOST)
                        MeasureSpec.makeMeasureSpec(selfHeightSize, EXACTLY)
                    else
                        MeasureSpec.makeMeasureSpec(0, UNSPECIFIED)
                }
                WRAP_CONTENT -> {
                    if (selfHeightMode == EXACTLY || selfHeightMode == AT_MOST)
                        MeasureSpec.makeMeasureSpec(selfHeightSize - paddingTop - paddingBottom - selfMeasureHeight, AT_MOST)
                    else
                        MeasureSpec.makeMeasureSpec(0, UNSPECIFIED)
                }
                else -> MeasureSpec.makeMeasureSpec(layoutParams.height, EXACTLY)
            }

            measureChild(child, childWidthSpec, childHeightSpec)


            // 子View占用的宽高
            childWidth = child.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
            childHeight = child.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin

            // 是否需要换行
            if (childWidth + curLineWidth > selfWidthSize) {

                // 记录最大宽度
                selfMeasureWidth = Math.max(selfMeasureWidth, curLineWidth)
                // 累加高度
                selfMeasureHeight += curLineMaxHeight

                Log.d("eric", "line height:$curLineMaxHeight")

                // 更新新行的信息
                curLineWidth = childWidth
                curLineMaxHeight = childHeight

            } else {
                // 累加宽度
                curLineWidth += childWidth
                // 高度以最大为准
                curLineMaxHeight = Math.max(curLineMaxHeight, childHeight)
            }

            // 最后一行
            if (i == childCount - 1) {
                selfMeasureWidth = Math.max(selfMeasureWidth, curLineWidth)
                selfMeasureHeight += curLineMaxHeight
            }
        }

        selfMeasureWidth += paddingLeft + paddingRight
        selfMeasureHeight += paddingTop + paddingBottom

        Log.d("eric", "before adjust width:$selfMeasureWidth," +
                "before adjust height:$selfMeasureHeight")

        // 修正尺寸
        selfMeasureWidth = View.resolveSize(selfMeasureWidth, widthMeasureSpec)
        selfMeasureHeight = View.resolveSize(selfMeasureHeight, heightMeasureSpec)

        Log.d("eric", "after adjust width:$selfMeasureWidth," +
                "after adjust height:$selfMeasureHeight")

        setMeasuredDimension(selfMeasureWidth, selfMeasureHeight)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }
}