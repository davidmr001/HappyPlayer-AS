package com.zhangliangming.hp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zhangliangming.hp.ui.util.ColorUtil;
import com.zhangliangming.hp.ui.util.FontUtil;


/**
 * 按钮颜色图标
 *
 * @author Administrator
 */
public class BaseColorImageButton extends TextView {

    private int defColor;
    private int pressColor;
    private boolean isPressed = false;
    private boolean isLoadColor = false;

    public BaseColorImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public BaseColorImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseColorImageButton(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // 设置字体图片
        Typeface iconfont = FontUtil.getInstance(context).getTypeFace();
        setTypeface(iconfont);

        //
        defColor = ColorUtil.parserColor("#ffffff", 255);
        pressColor = ColorUtil.parserColor("#e1e1e1", 200);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!isLoadColor) {

            if (isPressed) {
                setTextColor(pressColor);
            } else {
                setTextColor(defColor);
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
