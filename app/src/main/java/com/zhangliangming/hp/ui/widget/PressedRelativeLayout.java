package com.zhangliangming.hp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhangliangming.hp.ui.util.ColorUtil;


/**
 * 按钮点击后，背景颜色
 */
public class PressedRelativeLayout extends RelativeLayout {
    private int pressColor;
    private boolean isPressed = false;
    private boolean isLoadColor = false;

    public PressedRelativeLayout(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public PressedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PressedRelativeLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!isLoadColor) {

            pressColor = ColorUtil.parserColor("#000000", 25);
            if (isPressed) {
                setBackgroundColor(pressColor);
            } else {
                setBackgroundColor(Color.TRANSPARENT);
            }
            isLoadColor = true;
        }
        super.dispatchDraw(canvas);
    }

    public void setPressed(boolean pressed) {
        isLoadColor = false;
        isPressed = pressed;
        invalidate();
        super.setPressed(pressed);
    }

}
