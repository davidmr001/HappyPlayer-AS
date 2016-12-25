package com.zhangliangming.hp.ui.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.zhangliangming.hp.ui.R;


public class BaseSeekBar extends SeekBar {

    private Context context;

    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint secondProgressPaint;

    private boolean isLoadColor = false;

    public BaseSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public BaseSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseSeekBar(Context context) {
        super(context);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        this.context = context;
        initPaint();

    }

    private void initPaint() {

        backgroundPaint = new Paint();
        backgroundPaint.setDither(true);
        backgroundPaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setDither(true);
        progressPaint.setAntiAlias(true);

        secondProgressPaint = new Paint();
        secondProgressPaint.setDither(true);
        secondProgressPaint.setAntiAlias(true);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (!isLoadColor) {

            backgroundPaint.setColor(Color.TRANSPARENT);
            progressPaint.setColor(getResources().getColor(R.color.def_bar_title_color));
            secondProgressPaint.setColor(Color.TRANSPARENT);
            isLoadColor = true;
        }

        Rect backgroundRect = new Rect(0, 0, getWidth(), getHeight());
        canvas.drawRect(backgroundRect, backgroundPaint);
        if (getMax() != 0) {
            if (getSecondaryProgress() != 0) {
                Rect secondProgressRect = new Rect(0, 0, getSecondaryProgress()
                        * getWidth() / getMax(), getHeight());
                canvas.drawRect(secondProgressRect, secondProgressPaint);
            }
            Rect progressRect = new Rect(0, 0, getProgress() * getWidth()
                    / getMax(), getHeight());
            canvas.drawRect(progressRect, progressPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

}
