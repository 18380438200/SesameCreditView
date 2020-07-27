package com.example.sesamecreditview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

/**
 * create by libo
 * create on 2020/7/27
 * description 支付宝芝麻信用自定义view
 */
public class SesameCreditView extends View {
    /** 圆心坐标 */
    private int centerX;
    private int centerY;
    /** 圆环paint */
    private Paint ringPaint;
    /** 信用分文字paint */
    private Paint scoreTextPaint;
    /** 外圆环宽度 */
    private int outerRingWidth;
    /** 内圆环宽度 */
    private int innerRingWidth;
    /** 表盘圆环总度数 */
    private final int totalAngle = 210;
    /** 圆环透明度 0-255 */
    private final int ringAlpha = 80;
    private final int lineAlpha = 100;
    /** 圆弧上总共刻度数 */
    private final int totalDegreeScale = 30;

    public SesameCreditView(Context context) {
        super(context);
        init();
    }

    public SesameCreditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        outerRingWidth = dp2px(getContext(), 3);
        innerRingWidth = dp2px(getContext(), 10);

        ringPaint = new Paint();
        ringPaint.setColor(getResources().getColor(R.color.white));
        ringPaint.setAntiAlias(true);
        ringPaint.setStyle(Paint.Style.STROKE);

        scoreTextPaint = new Paint();
        scoreTextPaint.setColor(getResources().getColor(R.color.white));
        scoreTextPaint.setAntiAlias(true);
        scoreTextPaint.setTextSize(30);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawRing(canvas);
        drawDegreeScale(canvas);
    }

    /**
     * 绘制圆弧
     * @param canvas
     */
    private void drawRing (Canvas canvas) {
        ringPaint.setAlpha(ringAlpha);
        int startAngle = -90-totalAngle/2;  //圆环起始角度，12点钟位置为-90°

        //绘制外圆环
        ringPaint.setStrokeWidth(outerRingWidth);
        RectF rectF = new RectF(outerRingWidth, outerRingWidth, getWidth()-outerRingWidth, getHeight()-outerRingWidth);
        canvas.drawArc(rectF, startAngle, totalAngle, false, ringPaint);

        //绘制内圆环
        ringPaint.setStrokeWidth(innerRingWidth);
        int padding = outerRingWidth + dp2px(getContext(), 12);
        RectF innerRectF = new RectF(padding, padding, getWidth()-padding, getHeight()-padding);
        canvas.drawArc(innerRectF, startAngle, totalAngle, false, ringPaint);
    }

    /**
     * 绘制圆环刻度，分数文字
     */
    private void drawDegreeScale(Canvas canvas) {
        canvas.save();


        int padding = dp2px(getContext(), 10);

        canvas.rotate(-totalAngle/2, getWidth()/2, getHeight()/2); //将画布逆时间旋转一半弧度，使以左端点为刻度起点

        for (int i=0;i<=totalDegreeScale;i++) {
            ringPaint.setAlpha(80);
            ringPaint.setStrokeWidth(2);
            //每一格绘制一个浅色刻度
            canvas.drawLine(getWidth()/2, padding, getWidth()/2, padding+innerRingWidth, ringPaint);


            ringPaint.setAlpha(100);
            ringPaint.setStrokeWidth(3);
            //每6格刻度绘制一个深色刻度，即大刻度
            if (i%6==0) {
                canvas.drawLine(getWidth()/2, padding, getWidth()/2, padding+innerRingWidth+5, ringPaint);
            }

            //每三格刻度绘制一个文字
            if (i%3==0) {
                scoreTextPaint.setAlpha(180);
                float textWidth = scoreTextPaint.measureText("350");  //测量该文本宽度，需向左移动半个文本宽度以对齐
                canvas.drawText("350", getWidth()/2-textWidth/2, padding+innerRingWidth+dp2px(getContext(), 12), scoreTextPaint);
            }

            canvas.rotate(totalAngle/totalDegreeScale, getWidth()/2, getHeight()/2); //每次画完从中心开始旋转画布单位刻度的弧度
        }

    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
