package com.zhangliangming.hp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhangliangming.hp.ui.util.ColorUtil;

/**
 *
 */
public class ListItemRelativeLayout extends RelativeLayout {

    private int defColor;
    private int selectedColor;
    private int pressColor;

    private boolean isPressed = false;
    private boolean isSelected = false;
    private boolean isLoadColor = false;


    public ListItemRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ListItemRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListItemRelativeLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        defColor = ColorUtil.parserColor("#ffffff", 255);
        selectedColor = ColorUtil.parserColor("#e1e1e1", 200);
        pressColor = ColorUtil.parserColor("#e1e1e1", 200);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!isLoadColor) {

            if (isPressed) {
                setBackgroundColor(pressColor);
            } else {
                if (isSelected) {
                    setBackgroundColor(selectedColor);
                } else {
                    setBackgroundColor(defColor);
                }
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

    /**
     * 设置标签被选中
     *
     * @param selected
     */
    public void setSelect(boolean selected) {
        isLoadColor = false;
        isSelected = selected;
        invalidate();
    }

}
