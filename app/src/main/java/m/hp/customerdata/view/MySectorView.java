package m.hp.customerdata.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.List;

import m.hp.customerdata.R;

/**
 * @author huangping
 */
public class MySectorView extends View {
    private final Paint mPaint;
    private int mViewHeight;
    private int mViewWidth;
    /**
     * 扇形图半径
     */
    private int mSectorRadius;
    private List<SectorParams> mSectorParams;
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSectorRadius(int sectorRadius) {
        mSectorRadius = sectorRadius;
    }

    public void addSectorParams(List<SectorParams> sectorParams) {
        mSectorParams = sectorParams;
    }

    public MySectorView(Context context) {
        this(context, null);
    }

    public MySectorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MySectorView);
        String gravity = typedArray.getString(R.styleable.MySectorView_android_gravity);
        typedArray.recycle();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSector(canvas);
    }

    private void drawSector(Canvas canvas) {

        int scaleN = mSectorRadius;

        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        //圆的半径
        float r = 540;
        //空心圆
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.GRAY);
        canvas.drawCircle(r, mViewHeight / 2, r - scaleN, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        //实心扇形
        float left = 0 + scaleN;
        float top = mViewHeight / 2 - r + scaleN;
        float right = 2 * r - scaleN;
        float bottom = mViewHeight / 2 + r - scaleN;
        //扇形矩阵
        RectF rectF = new RectF(left, top, right, bottom);
        int inCreateH = 0;
        if (mSectorParams == null) {
            return;
        }
        for (int i = 0; i < mSectorParams.size(); i++) {
            inCreateH += mViewWidth / 10;
            mPaint.setColor(mSectorParams.get(i).color);
            canvas.drawArc(rectF, mSectorParams.get(i).startAngle, mSectorParams.get(i).sweepAngle, true, mPaint);
            //扇形区域说明矩阵
            canvas.drawRect(left, bottom + inCreateH, mViewWidth / 10 + left, (mViewWidth / 10) + bottom + inCreateH, mPaint);
            mPaint.setTextSize(mViewWidth / 20);
            canvas.drawText(mSectorParams.get(i).text, left + mViewWidth / 9, (mViewWidth / 11) + bottom + inCreateH, mPaint);
        }
        canvas.drawText(title, (mViewWidth - getTextWidth(mPaint, title)) / 2, mViewHeight / 4, mPaint);

        Log.d("rectF", "rectF is " + rectF.centerX() + "|" + rectF.centerY());
        Log.d("left", "left is " + left);
        Log.d("top", "top is " + top);
        Log.d("right", "right is " + right);
        Log.d("bottom", "bottom is " + bottom);
    }

    private int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();

    }

    public static class SectorParams {
        private float radius;//半径
        private int startAngle;//扇形起始角度
        private int sweepAngle;//扇形面积角度
        private int color;
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public int getStartAngle() {
            return startAngle;
        }

        public void setStartAngle(int startAngle) {
            this.startAngle = startAngle;
        }

        public int getSweepAngle() {
            return sweepAngle;
        }

        public void setSweepAngle(int sweepAngle) {
            this.sweepAngle = sweepAngle;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("onTouchEvent", "event x == " + event.getX() + "|event y == " + event.getY());
        return super.onTouchEvent(event);
    }
}
