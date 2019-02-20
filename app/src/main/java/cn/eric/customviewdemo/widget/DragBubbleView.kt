package cn.eric.customviewdemo.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PointFEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import cn.eric.customviewdemo.R

/**
 * Created by eric on 2018/6/28
 */
class DragBubbleView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 气泡半径
    private val bubbleRadius: Float
    // 气泡颜色
    private val bubbleColor: Int
    // 气泡消息文本
    private val textStr: String
    // 气泡消息文本大小
    private val textSize: Float
    // 气泡消息文本颜色
    private val textColor: Int

    // 不动气泡半径
    private var bubStillRadius: Float
    // 不动气泡的圆心
    private val bubStillCenter = PointF()
    // 可动气泡半径
    private val bubMoveableRadius: Float
    // 可动气泡的圆心
    private var bubMoveableCenter = PointF()

    // 气泡相连状态最大圆心距离
    private val maxDist: Float
    // 两气泡圆心距离
    private var dist = 0f
    // 手指触摸偏移量
    private val moveOffset: Float

    // 气泡画笔
    private val bubblePaint: Paint
    // 文本画笔
    private val textPaint: Paint
    // 爆炸画笔
    private val burstPaint: Paint

    //文本绘制区域
    private val textRect = Rect()
    // 贝塞尔曲线路径
    private val bezierPath = Path()
    // 爆炸绘制区域
    private val burstRect = Rect()

    // 气泡爆炸的图片id数组
    private val burstDrawablesArray = intArrayOf(R.drawable.burst_1, R.drawable.burst_2,
            R.drawable.burst_3, R.drawable.burst_4, R.drawable.burst_5)
    // 气泡爆炸的bitmap数组
    private val burstBitmapsArray: Array<Bitmap>
    // 当前爆炸图片索引
    private var curDrawableIndex = 0
    // 爆炸动画是否启动
    private var burstAnimStart = false

    // 气泡状态
    private var bubbleState = BubbleState.DEFAULT

    private enum class BubbleState {
        DEFAULT, CONNECT, APART, DISMISS
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DragBubbleView, defStyleAttr, 0)

        bubbleRadius = typedArray.getDimension(R.styleable.DragBubbleView_bubble_radius, 8.0f)
        bubbleColor = typedArray.getColor(R.styleable.DragBubbleView_bubble_color, Color.RED)
        textStr = typedArray.getString(R.styleable.DragBubbleView_bubble_text)
        textSize = typedArray.getDimension(R.styleable.DragBubbleView_bubble_textSize, 12.0f)
        textColor = typedArray.getColor(R.styleable.DragBubbleView_bubble_textColor, Color.WHITE)

        typedArray.recycle()

        bubStillRadius = bubbleRadius
        bubMoveableRadius = bubbleRadius
        maxDist = bubbleRadius * 8
        moveOffset = maxDist / 4

        // 气泡画笔
        bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bubblePaint.color = bubbleColor
        bubblePaint.style = Paint.Style.FILL

        // 文本画笔
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.color = textColor
        textPaint.textSize = textSize

        // 爆炸画笔
        burstPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        burstPaint.isFilterBitmap = true

        // 将气泡爆炸的drawable转为bitmap
        burstBitmapsArray = Array(burstDrawablesArray.size) {
            BitmapFactory.decodeResource(resources, burstDrawablesArray[it])
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initView(w, h)
    }

    private fun initView(w: Int, h: Int) {
        //设置两气泡圆心初始坐标
        bubStillCenter.set((w / 2).toFloat(), (h / 2).toFloat())
        bubMoveableCenter.set((w / 2).toFloat(), (h / 2).toFloat())
        bubbleState = BubbleState.DEFAULT
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1、画静止状态
        // 2、画相连状态
        // 3、画分离状态
        // 4、画消失状态---爆炸动画

        // 画拖拽的气泡和文字
        if (bubbleState != BubbleState.DISMISS) {
            canvas.drawCircle(bubMoveableCenter.x, bubMoveableCenter.y, bubMoveableRadius, bubblePaint)

            textPaint.getTextBounds(textStr, 0, textStr.length, textRect)

            canvas.drawText(textStr, bubMoveableCenter.x - textRect.width() / 2,
                    bubMoveableCenter.y + textRect.height() / 2, textPaint)
        }

        // 画相连的气泡状态
        if (bubbleState == BubbleState.CONNECT) {
            // 1、画静止气泡
            canvas.drawCircle(bubStillCenter.x, bubStillCenter.y, bubStillRadius, bubblePaint)
            // 2、画相连曲线
            // 计算控制点坐标，两个圆心的中点
            val anchorX = ((bubStillCenter.x + bubMoveableCenter.x) / 2).toInt()
            val anchorY = ((bubStillCenter.y + bubMoveableCenter.y) / 2).toInt()

            val cosTheta = (bubMoveableCenter.x - bubStillCenter.x) / dist
            val sinTheta = (bubMoveableCenter.y - bubStillCenter.y) / dist

            val bubStillStartX = bubStillCenter.x - bubStillRadius * sinTheta
            val bubStillStartY = bubStillCenter.y + bubStillRadius * cosTheta
            val bubMoveableEndX = bubMoveableCenter.x - bubMoveableRadius * sinTheta
            val bubMoveableEndY = bubMoveableCenter.y + bubMoveableRadius * cosTheta
            val bubMoveableStartX = bubMoveableCenter.x + bubMoveableRadius * sinTheta
            val bubMoveableStartY = bubMoveableCenter.y - bubMoveableRadius * cosTheta
            val bubStillEndX = bubStillCenter.x + bubStillRadius * sinTheta
            val bubStillEndY = bubStillCenter.y - bubStillRadius * cosTheta

            bezierPath.reset()
            // 画上半弧
            bezierPath.moveTo(bubStillStartX, bubStillStartY)
            bezierPath.quadTo(anchorX.toFloat(), anchorY.toFloat(), bubMoveableEndX, bubMoveableEndY)
            // 画下半弧
            bezierPath.lineTo(bubMoveableStartX, bubMoveableStartY)
            bezierPath.quadTo(anchorX.toFloat(), anchorY.toFloat(), bubStillEndX, bubStillEndY)
            bezierPath.close()
            canvas.drawPath(bezierPath, bubblePaint)
        }

        if (burstAnimStart) {
            burstRect.set((bubMoveableCenter.x - bubMoveableRadius).toInt(),
                    (bubMoveableCenter.y - bubMoveableRadius).toInt(),
                    (bubMoveableCenter.x + bubMoveableRadius).toInt(),
                    (bubMoveableCenter.y + bubMoveableRadius).toInt())

            canvas.drawBitmap(burstBitmapsArray[curDrawableIndex], null, burstRect, bubblePaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (bubbleState != BubbleState.DISMISS) {
                    dist = Math.hypot((event.x - bubStillCenter.x).toDouble(), (event.y - bubStillCenter.y).toDouble()).toFloat()
                    bubbleState = if (dist < bubbleRadius + moveOffset) BubbleState.CONNECT else BubbleState.DEFAULT
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (bubbleState != BubbleState.DEFAULT) {
                    bubMoveableCenter.x = event.x
                    bubMoveableCenter.y = event.y
                    dist = Math.hypot((event.x - bubStillCenter.x).toDouble(), (event.y - bubStillCenter.y).toDouble()).toFloat()
                    if (bubbleState == BubbleState.CONNECT) {
                        // 减去moveOffset是为了让不动气泡半径到一个较小值时就直接消失
                        // 或者说是进入分离状态
                        if (dist < maxDist - moveOffset) {
                            bubStillRadius = bubbleRadius - dist / 8
                        } else {
                            bubbleState = BubbleState.APART
                        }
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (bubbleState == BubbleState.CONNECT) {
                    // 弹回的动画
                    startBubbleRestAnim()

                } else if (bubbleState == BubbleState.APART) {
                    if (dist < 2 * bubbleRadius) {
                        // 弹回的动画
                        startBubbleRestAnim()
                    } else {
                        // 爆裂的动画
                        startBubbleBurstAnim()
                    }
                }
            }
            else -> {
                return super.onTouchEvent(event)
            }
        }
        return true
    }

    private fun startBubbleBurstAnim() {
        //气泡改为消失状态
        bubbleState = BubbleState.DISMISS
        burstAnimStart = true
        //做一个int型属性动画，从0~mBurstDrawablesArray.length结束
        val anim = ValueAnimator.ofInt(0, burstDrawablesArray.size)
        anim.interpolator = LinearInterpolator()
        anim.duration = 500
        anim.addUpdateListener { animation ->
            //设置当前绘制的爆炸图片index
            curDrawableIndex = animation.animatedValue as Int
            invalidate()
        }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                //修改动画执行标志
                burstAnimStart = false
            }
        })
        anim.start()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startBubbleRestAnim() {
        val anim = ValueAnimator.ofObject(PointFEvaluator(),
                PointF(bubMoveableCenter.x, bubMoveableCenter.y),
                PointF(bubStillCenter.x, bubStillCenter.y))

        anim.duration = 200
        anim.interpolator = OvershootInterpolator(5f)
        anim.addUpdateListener { animation ->
            bubMoveableCenter = animation.animatedValue as PointF
            invalidate()
        }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                bubbleState = BubbleState.DEFAULT
            }
        })
        anim.start()
    }

    fun reset() {
        initView(width, height)
        invalidate()
    }
}