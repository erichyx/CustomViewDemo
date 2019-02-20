package cn.eric.customviewdemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.eric.customviewdemo.R;

/**
 * Created by eric on 2018/7/20
 */
public class ArcProgressView extends View {

    // 颜色相关
    private int mStartColor = Color.parseColor("#ff6526");
    private int mEndColor = Color.parseColor("#da2043");
    private int mProgressColor = Color.YELLOW;

    // 画笔相关
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBmpPaint = new Paint();
    private Paint mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mProgressWidth = 20f;

    // logo相关
    private PointF mLogoPoint;
    private Bitmap mLogoBitmap;

    // 圆弧路径相关
    private Path mPath = new Path();
    private Path mDstPath = new Path();
    private PathMeasure mPathMeasure = new PathMeasure();
    private float mArcLen;
    private RectF mClipRectF;

    // 其他
    private float mRadius = 10f;
    private int mOrientation = 0;
    private int mGradientType = GRADIENT_TYPE_CENTER;
    private int mProgress = 0;
    private int mMax = 100;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GRADIENT_TYPE_NONE, GRADIENT_TYPE_CENTER, GRADIENT_TYPE_SWEEP, GRADIENT_TYPE_LTR, GRADIENT_TYPE_TTB})
    @interface GradientType {
    }

    public static final int GRADIENT_TYPE_NONE = 0;
    public static final int GRADIENT_TYPE_CENTER = 1;
    public static final int GRADIENT_TYPE_SWEEP = 2;
    public static final int GRADIENT_TYPE_LTR = 3;
    public static final int GRADIENT_TYPE_TTB = 4;

    public ArcProgressView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ArcProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ArcProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcProgressView, defStyleAttr, 0);
            mStartColor = typedArray.getColor(R.styleable.ArcProgressView_startColor, Color.parseColor("#ff6526"));
            mEndColor = typedArray.getColor(R.styleable.ArcProgressView_endColor, Color.parseColor("#da2043"));
            mProgressColor = typedArray.getColor(R.styleable.ArcProgressView_progressColor, Color.YELLOW);
            mRadius = typedArray.getDimension(R.styleable.ArcProgressView_radius, 10f);
            mMax = typedArray.getInt(R.styleable.ArcProgressView_max, 100);
            mOrientation = typedArray.getInt(R.styleable.ArcProgressView_orientation, 0);
            mGradientType = typedArray.getInt(R.styleable.ArcProgressView_gradientType, 0);
            mProgressWidth = typedArray.getDimension(R.styleable.ArcProgressView_progressWidth, 20f);
            int logoSampleSize = typedArray.getInt(R.styleable.ArcProgressView_logoSampleSize, 1);
            int resId = typedArray.getResourceId(R.styleable.ArcProgressView_logo, 0);
            if (resId > 0) {
                mLogoBitmap = decodeBmp(resId, logoSampleSize);
            }
            typedArray.recycle();
        }

        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);

        updateForRadiusChanged();
    }

    private Bitmap decodeBmp(int resId, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeResource(getResources(), resId, options);
    }

    private void updateForRadiusChanged() {
        initShader();
        updateLogo();
        updateForProgressWidthChanged();
    }

    private void updateLogo() {
        if (mLogoBitmap != null) {
            float length = (float) (mRadius * Math.cos(Math.PI / 4));
            float top = mRadius - length + getPaddingTop();
            mLogoPoint = new PointF(getPaddingLeft() + (length - mLogoBitmap.getWidth()) / 2, top + (length - mLogoBitmap.getHeight()) / 2);
        }
    }

    private void updateForProgressWidthChanged() {
        mProgressPaint.setStrokeWidth(mProgressWidth);
        mClipRectF = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + mRadius + mProgressWidth / 2,
                getPaddingTop() + mRadius + mProgressWidth / 2);
        if (mGradientType == GRADIENT_TYPE_SWEEP) {
            initShader();
        }
        updatePath();
    }

    private void updatePath() {
        mPath.reset();
        mPath.addCircle(getPaddingLeft(), getPaddingTop() + mRadius + mProgressWidth / 2,
                mRadius, mOrientation == 0 ? Path.Direction.CW : Path.Direction.CCW);
        mPathMeasure.setPath(mPath, true);
        mArcLen = mPathMeasure.getLength() / 4;
    }

    private void initShader() {
        Shader shader = null;
        switch (mGradientType) {
            case GRADIENT_TYPE_CENTER:
                shader = new RadialGradient(getPaddingLeft(), getPaddingTop() + mRadius,
                        mRadius, mStartColor, mEndColor, Shader.TileMode.CLAMP);
                break;
            case GRADIENT_TYPE_SWEEP:
                shader = new SweepGradient(getPaddingLeft(), getPaddingTop() + mRadius + mProgressWidth / 2, mStartColor, mEndColor);
                break;
            case GRADIENT_TYPE_LTR:
                shader = new LinearGradient(getPaddingLeft(), getPaddingTop(),
                        getPaddingLeft() + mRadius, getPaddingTop(), mStartColor, mEndColor, Shader.TileMode.CLAMP);
                break;
            case GRADIENT_TYPE_TTB:
                shader = new LinearGradient(getPaddingLeft(), getPaddingTop(),
                        getPaddingLeft(), getPaddingTop() + mRadius, mStartColor, mEndColor, Shader.TileMode.CLAMP);
                break;
            default:
                // 无渐变
                mPaint.setColor(mStartColor);
                break;
        }
        mPaint.setShader(shader);
    }

    public int getStartColor() {
        return mStartColor;
    }

    public void setStartColor(@ColorInt int startColor) {
        if (mStartColor != startColor) {
            mStartColor = startColor;
            initShader();
            invalidate();
        }
    }

    public int getEndColor() {
        return mEndColor;
    }

    public void setEndColor(@ColorInt int endColor) {
        if (mEndColor != endColor) {
            mEndColor = endColor;
            initShader();
            invalidate();
        }
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        if (mProgressColor != progressColor) {
            mProgressColor = progressColor;
            mProgressPaint.setColor(mProgressColor);
            invalidate();
        }
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        if (mRadius != radius) {
            mRadius = radius;
            updateForRadiusChanged();
            invalidate();
        }
    }

    public int getGradientType() {
        return mGradientType;
    }

    public void setGradientType(@GradientType int gradientType) {
        if (mGradientType != gradientType) {
            mGradientType = gradientType;
            initShader();
            invalidate();
        }
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        if (mMax != max) {
            mMax = max;
            invalidate();
        }
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(int orientation) {
        if (mOrientation != orientation) {
            mOrientation = orientation;
            updatePath();
            invalidate();
        }
    }

    public void setLogoResource(@DrawableRes int logoId, int sampleSize) {
        if (sampleSize < 1) {
            sampleSize = 1;
        }

        mLogoBitmap = decodeBmp(logoId, sampleSize);
        updateLogo();
        invalidate();
    }

    public float getProgressWidth() {
        return mProgressWidth;
    }

    public void setProgressWidth(float progressWidth) {
        if (mProgressWidth != progressWidth) {
            mProgressWidth = progressWidth;
            updateForProgressWidthChanged();
            invalidate();
        }
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        if (mProgress != progress) {
            mProgress = progress;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int bmpWidth = 0;
        int bmpHeight = 0;
        if (mLogoBitmap != null) {
            bmpWidth = mLogoBitmap.getWidth();
            bmpHeight = mLogoBitmap.getHeight();
        }

        // 计算期望的宽高
        int expectWidth = Math.max(getMinimumWidth(), Math.max(bmpWidth, (int) mRadius) + getPaddingLeft() + getPaddingRight() + (int) mProgressWidth / 2);
        int expectHeight = Math.max(getMinimumHeight(), Math.max(bmpHeight, (int) mRadius) + getPaddingTop() + getPaddingBottom() + (int) mProgressWidth / 2);

        // 根据spec修正宽高
        int measureWidth = View.resolveSize(expectWidth, widthMeasureSpec);
        int measureHeight = View.resolveSize(expectHeight, heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.clipRect(mClipRectF);
        canvas.drawPath(mPath, mPaint);
        if (mLogoBitmap != null) {
            canvas.drawBitmap(mLogoBitmap, mLogoPoint.x, mLogoPoint.y, mBmpPaint);
        }

        float ratio = 0f;
        if (mMax > 0) {
            ratio = (float) mProgress / mMax;
        }

        float startD = 0f;
        if (mOrientation == 0) {// 顺时针
            startD = mArcLen * 3;
        }
        float stopD = startD + ratio * mArcLen;

        mDstPath.reset();
        // 硬件加速的bug
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            mDstPath.rLineTo(0f, 0f);
        }
        mPathMeasure.getSegment(startD, stopD, mDstPath, true);
        canvas.drawPath(mDstPath, mProgressPaint);
    }
}
