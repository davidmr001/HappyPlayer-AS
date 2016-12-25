package com.zhangliangming.hp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zhangliangming.hp.ui.R;
import com.zhangliangming.hp.ui.util.ColorUtil;
import com.zhangliangming.hp.ui.util.MediaUtils;


public class LrcSeekBar extends SeekBar {

    private Context context;
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint secondProgressPaint;

    private Paint thumbPaint;

    private boolean isLoadColor = false;

    /**
     * 弹出提示信息窗口
     */
    private PopupWindow mPopupWindow;

    /**
     * 弹出窗口显示文本
     */
    private TextView tipTextView = null;

    private class SeekBarMessage {
        String timeTip;
        String timeLrc;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            SeekBarMessage sm = (SeekBarMessage) msg.obj;
            tipTextView.setText(sm.timeTip + sm.timeLrc);
        }
    };

    public LrcSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public LrcSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LrcSeekBar(Context context) {
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

        thumbPaint = new Paint();
        thumbPaint.setDither(true);
        thumbPaint.setAntiAlias(true);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (!isLoadColor) {
            backgroundPaint.setColor(ColorUtil.parserColorValue("#767f83,255"));
            progressPaint.setColor(ColorUtil.parserColorValue("#767f83,255"));
            secondProgressPaint.setColor(ColorUtil.parserColorValue("#767f83,150"));
            thumbPaint.setColor(ColorUtil.parserColorValue("#767f83,255"));
            isLoadColor = true;
        }

        int rSize = 10;
        int height = 3;
        int cRSize = 10;
        int leftPadding = cRSize;

        if (getProgress() > 0) {
            leftPadding = 0;
        }

        RectF backgroundRect = new RectF(leftPadding, getHeight() / 2 - height, getWidth(),
                getHeight() / 2 + height);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(backgroundRect, rSize, rSize, backgroundPaint);


        if (getMax() != 0) {
            RectF secondProgressRect = new RectF(leftPadding, getHeight() / 2 - height,
                    getSecondaryProgress() * getWidth() / getMax(), getHeight()
                    / 2 + height);
            canvas.drawRoundRect(secondProgressRect, rSize, rSize, secondProgressPaint);

            RectF progressRect = new RectF(leftPadding, getHeight() / 2 - height,
                    getProgress() * getWidth() / getMax(), getHeight() / 2
                    + height);
            canvas.drawRoundRect(progressRect, rSize, rSize, progressPaint);


            int cx = getProgress() * getWidth() / getMax();
            if ((cx + cRSize) > getWidth()) {
                cx = getWidth() - cRSize;
            } else {
                cx = Math.max(cx, cRSize);
            }
            int cy = getHeight() / 2;
            canvas.drawCircle(cx, cy, cRSize, thumbPaint);
        }
    }

    /**
     * 创建PopupWindow
     */
    private void initPopuptWindow(String timeStr, View v, String lrc) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View popupWindow = layoutInflater.inflate(
                R.layout.seekbar_progress_dialog, null);
        tipTextView = (TextView) popupWindow.findViewById(R.id.tip);

        tipTextView.setText(timeStr + lrc);

        int padding = 25;

        mPopupWindow = new PopupWindow(popupWindow, screenWidth - padding * 2,
                80, true);
        // mPopupWindow = new PopupWindow(popupWindow, LayoutParams.FILL_PARENT,
        // LayoutParams.FILL_PARENT, true);
        // int[] location = new int[2];
        // this.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
        // this.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
        // mPopupWindow.showAsDropDown(v, 0, v.getHeight() - 80);

        int[] location = new int[2];
        v.getLocationOnScreen(location);

        mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, padding, location[1]
                - mPopupWindow.getHeight());
    }

    /**
     * 获取PopupWindow实例
     *
     * @param lrc
     */
    public void popupWindowShow(int timeLongStr, View v, String lrc) {
        String timeStr = MediaUtils.formatTime(timeLongStr);
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            Message msg = new Message();
            SeekBarMessage sm = new SeekBarMessage();
            sm.timeTip = timeStr;
            sm.timeLrc = lrc;
            msg.obj = sm;
            handler.sendMessage(msg);
        } else {
            initPopuptWindow(timeStr, v, lrc);
        }
    }

    /**
     * 关闭窗口
     */
    public void popupWindowDismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }
}
