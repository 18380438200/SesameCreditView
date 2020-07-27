package com.example.sesamecreditview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.annotation.Nullable;

/**
 * create by libo
 * create on 2020/7/27
 * description 支付宝芝麻信用自定义view
 */
public class SesameCreditView extends View {
    /** 圆环paint */
    private Paint ringPaint;
    /** 信用分文字paint */
    private Paint scoreTextPaint;
    /** 指示器paint */
    private Paint dotPaint;
    /** 外圆环宽度 */
    private int outerRingWidth;
    /** 内圆环宽度 */
    private int innerRingWidth;
    /** 表盘圆环总度数 */
    private final int totalAngle = 210;
    /** 圆环透明度 0-255 */
    private final int ringAlpha = 80;
    /** 圆弧上总共刻度数 */
    private final int totalDegreeScale = 30;
    /** 各分数刻度文本 */
    private String[] scores = new String[] {"350", "较差", "550", "中等", "600", "良好", "650", "优秀", "700", "极好", "950"};
    /** 信用分数 */
    private int score = 700;
    /** 当前扫过的数值角度 */
    private float curProgressAngle;

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
        scoreTextPaint.setTextSize(32);

        dotPaint = new Paint();
        dotPaint.setColor(getResources().getColor(R.color.white));
        dotPaint.setAntiAlias(true);
        dotPaint.setMaskFilter(new BlurMaskFilter(outerRingWidth, BlurMaskFilter.Blur.SOLID));  //设置指示器发光

        startAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawRing(canvas);
        drawDegreeScale(canvas);
        drawCenterText(canvas);
        drawDot(canvas);


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
                float textWidth = scoreTextPaint.measureText(scores[i/3]);  //测量该文本宽度，需向左移动半个文本宽度以对齐
                canvas.drawText(scores[i/3], getWidth()/2-textWidth/2, padding+innerRingWidth+dp2px(getContext(), 12), scoreTextPaint);
            }

            canvas.rotate(totalAngle/totalDegreeScale, getWidth()/2, getHeight()/2); //每次画完从中心开始旋转画布单位刻度的弧度
        }

        canvas.restore(); //恢复角度
    }

    /**
     * 绘制中心文本
     */
    private void drawCenterText(Canvas canvas) {
        //绘制当前分数
        scoreTextPaint.setAlpha(255);
        scoreTextPaint.setTextSize(170);

        String curScore = String.valueOf(score);
        Rect scoreRect = new Rect();
        scoreTextPaint.getTextBounds(curScore, 0, curScore.length(), scoreRect);  //需左移文字宽度以居中
        canvas.drawText(curScore, getWidth()/2-scoreRect.width()/2, getHeight()/2, scoreTextPaint);

        //绘制BETA文字
        scoreTextPaint.setAlpha(150);
        scoreTextPaint.setTextSize(35);
        Rect betaRect= new Rect();
        String betaStr = "BETA";
        scoreTextPaint.getTextBounds(betaStr, 0, betaStr.length(), betaRect);  //beta需向坐上移动
        canvas.drawText(betaStr, getWidth()/2-betaRect.width()/2, getHeight()/2-scoreRect.height()-betaRect.height()/2, scoreTextPaint);

        //绘制信用等级文本
        scoreTextPaint.setAlpha(200);
        scoreTextPaint.setTextSize(75);
        Rect creditRect = new Rect();
        String creditStr = "信用极好";
        scoreTextPaint.getTextBounds(creditStr, 0, creditStr.length(), creditRect);
        canvas.drawText(creditStr, getWidth()/2-creditRect.width()/2, getHeight()/2+scoreRect.height()/2+20, scoreTextPaint);

        //绘制评估时间
        scoreTextPaint.setAlpha(150);
        scoreTextPaint.setTextSize(35);
        float timeStrWidth = scoreTextPaint.measureText("评估时间：2020.07.27");
        canvas.drawText("评估时间:2020.07.27", getWidth()/2-timeStrWidth/2, getHeight()/2+scoreRect.height()+10, scoreTextPaint);
    }

    /**
     * 绘制进度动画小圆点
     */
    private void drawDot(Canvas canvas) {
        scoreTextPaint.setAlpha(230);
        float x = (float) (getWidth()/2 + (getWidth()/2-outerRingWidth)*Math.sin(Math.toRadians(curProgressAngle)));
        float y = (float) (getHeight()/2 - (getWidth()/2-outerRingWidth)*Math.cos(Math.toRadians(curProgressAngle)));
        canvas.drawCircle(x, y, outerRingWidth, dotPaint);
    }

    /**
     * 启动指示器加载动画
     */
    private void startAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(-105, 73);
        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curProgressAngle = (float) animation.getAnimatedValue();
                Log.i("minfo", "角度 " + curProgressAngle);
                postInvalidate();  //数值改变实时更新绘制
            }
        });
        valueAnimator.start();
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
