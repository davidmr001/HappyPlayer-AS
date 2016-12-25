package com.zhangliangming.hp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zhangliangming.hp.ui.util.ColorUtil;
import com.zhangliangming.hp.ui.util.FontUtil;


/**
 * 按钮图标切换
 *
 * @author Administrator
 */
public class BaseImageButton extends TextView {

    private boolean isPressed = false;
    private boolean isLoadColor = false;

    private String defIcon = "";
    private String pressedIcon = "";

    public BaseImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public BaseImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseImageButton(Context context) {
        super(context);
        init(context);
    }

    public void setIcon(String defIcon,String pressedIcon){
        this.defIcon = defIcon;
        this.pressedIcon = pressedIcon;
        setText(defIcon);
    }

    private void init(Context context) {
        // 设置字体图片
        Typeface iconfont = FontUtil.getInstance(context).getTypeFace();
        setTypeface(iconfont);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!isLoadColor) {
            if (isPressed) {
                setText(pressedIcon);
            } else {
                setText(defIcon);
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
