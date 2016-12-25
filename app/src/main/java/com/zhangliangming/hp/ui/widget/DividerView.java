package com.zhangliangming.hp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.zhangliangming.hp.ui.util.ColorUtil;

public class DividerView extends View {
    private boolean isLoadColor = false;

    public DividerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DividerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DividerView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        if (!isLoadColor) {
            int color = ColorUtil.parserColor("#000000",15);
            setBackgroundColor(color);
            isLoadColor = true;
        }
        super.dispatchDraw(canvas);
    }
}
