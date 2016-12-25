package com.zhangliangming.hp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhangliangming.hp.ui.util.ColorUtil;

/**
 * 颜色面板
 */
public class ColorRelativeLayout extends RelativeLayout {

    private int panelColor;
    private int maskColor;

    private boolean selected = false;

    public ColorRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ColorRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorRelativeLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        panelColor = ColorUtil.parserColor("#000000", 0);
        maskColor = ColorUtil.parserColor("#000000", 120);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(panelColor);
        paint.setStyle(Paint.Style.FILL);

        RectF ref = new RectF();
        ref.left = 0;
        ref.right = getWidth();
        ref.top = 0;
        ref.bottom = getHeight();

        canvas.drawRoundRect(ref, 15, 15, paint);

        if (!selected) {
            paint.setColor(maskColor);
            canvas.drawRoundRect(ref, 15, 15, paint);
        }
    }

    public void setPanelColor(int panelColor) {
        this.panelColor = panelColor;
        invalidate();
    }

    /**
     * 设置标签被选中
     *
     * @param selected
     */
    public void setSelect(boolean selected) {
        this.selected = selected;
        invalidate();
    }

}
