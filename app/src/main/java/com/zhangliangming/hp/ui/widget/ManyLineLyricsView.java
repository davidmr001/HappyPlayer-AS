package com.zhangliangming.hp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;

import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.utils.TimeUtils;
import com.nineoldandroids.animation.ValueAnimator;
import com.zhangliangming.hp.ui.R;
import com.zhangliangming.hp.ui.common.Constants;
import com.zhangliangming.hp.ui.logger.LoggerManage;
import com.zhangliangming.hp.ui.util.ColorUtil;
import com.zhangliangming.hp.ui.util.FontUtil;
import com.zhangliangming.hp.ui.util.LyricsParserUtil;

import java.util.Calendar;
import java.util.TreeMap;

/**
 * Created by zhangliangming on 2016/12/19.
 * 多行歌词
 */

public class ManyLineLyricsView extends View {

    /**
     * 默认画笔
     */
    private Paint paint;
    /**
     * 默认高亮未读画笔
     */
    private Paint paintHLDEF;
    /**
     * 画时间线的画时间线
     ***/
    private Paint paintTimeLine;
    /**
     * 绘画播放按钮
     */
    private Paint paintPlay;

    /**
     * 高亮已读画笔
     */
    private Paint paintHLED;

    /**
     * 歌词解析
     */
    private LyricsParserUtil lyricsParser;

    /**
     * 歌词列表
     */
    private TreeMap<Integer, LyricsLineInfo> lyricsLineTreeMap;

    /**
     * 当前歌词的所在行数
     */
    private int lyricsLineNum = -1;

    /**
     * 当行歌词的第几个字
     */
    private int lyricsWordIndex = -1;

    /**
     *
     */
    private Context context;
    /**
     * 是否有歌词
     */
    private boolean hasLrc;
    /***
     * 字体大小
     */
    private int fontSize;
    /**
     * 默认提示语
     */
    private String defTipText;
    /**
     * 日志
     */
    private LoggerManage logger;

    /**
     * 空行高度
     */
    private int spaceLineHeight = 35;

    /**
     * Y轴移动的时间
     */
    private int duration = 350;

    /**
     * 歌词在Y轴上的偏移量
     */
    private float offsetY = 0;

    /**
     *
     * 以下为动感歌词参数
     */
    /**
     * 当行歌词已播放的高亮长度
     */
    private float lineLyricsHLEDWidth = 0;

    /**
     * 当行歌词第几个字 已经播放的时间长度
     */
    private int lyricsWordHLEDTime = 0;
    /**
     * 字体的高度进行微调
     */
    private int adjustmentNum = 5;

    //以下为歌词滚动的参数
    /**
     * 是否正在滑动
     */
    private boolean isScroll = false;
    /**
     * 是否正在触屏
     */
    private boolean isActionDown = false;
    /**
     * 播放按钮图标
     */
    private String playText;
    /**
     * 播放按钮区域
     */
    private Rect playRect;

    /**
     * 获得能够进行手势滑动的距离
     */
    private int mTouchSlop;
    /**
     * 获得允许执行一个fling手势动作的最小速度值
     */
    private int mMinimumVelocity;
    /**
     * 获得允许执行一个fling手势动作的最大速度值
     */
    private int mMaximumVelocity;
    /**
     * 速度跟踪器
     */
    private VelocityTracker mVelocityTracker;
    /**
     * 纵轴上的滑动速度
     */
    private float mVelocity;

    public ManyLineLyricsView(Context context) {
        super(context);
        init(context);
    }

    public ManyLineLyricsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ManyLineLyricsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        this.context = context;

        Typeface typeFace = Typeface.createFromAsset(context.getAssets(),
                "fonts/weiruanyahei14M.ttf");

        logger = LoggerManage.getZhangLogger(context);
        defTipText = "乐乐" + getYear() + ",传播好的音乐";

        paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setTypeface(typeFace);

        paintHLDEF = new Paint();
        paintHLDEF.setDither(true);
        paintHLDEF.setAntiAlias(true);
        paintHLDEF.setColor(Color.WHITE);
        paintHLDEF.setTypeface(typeFace);

        paintHLED = new Paint();
        paintHLED.setDither(true);
        paintHLED.setAntiAlias(true);
        paintHLED.setTypeface(typeFace);


        paintTimeLine = new Paint();
        paintTimeLine.setDither(true);
        paintTimeLine.setAntiAlias(true);
        paintTimeLine.setTypeface(typeFace);


        // 设置字体图片
        Typeface iconfont = FontUtil.getInstance(context).getTypeFace();
        paintPlay = new Paint();
        paintPlay.setDither(true);
        paintPlay.setAntiAlias(true);
        paintPlay.setTypeface(iconfont);

        playText = context.getResources().getString(R.string.play_def_icon);

        initColor();
        initFontSize();
        //滑动
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    /**
     * 获取年份
     *
     * @return
     */
    private String getYear() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return year + "";
    }


    /**
     * 初始化字体大小
     */
    private void initFontSize() {
        fontSize = Constants.lrcFontSize;
        paint.setTextSize(fontSize);
        //
        paintTimeLine.setTextSize(fontSize / 3 * 2);
        paintHLDEF.setTextSize(fontSize);
        paintHLED.setTextSize(fontSize);
        paintPlay.setTextSize(fontSize);
    }

    /***
     * 被始化颜色
     */
    private void initColor() {
        paintHLED
                .setColor(ColorUtil.parserColor(Constants.lrcColorStr[Constants.lrcColorIndex]));
        paintTimeLine
                .setColor(ColorUtil.parserColor(Constants.lrcColorStr[Constants.lrcColorIndex]));

        paintHLDEF.setColor(ColorUtil.parserColor("#767f83"));
        paint.setColor(ColorUtil.parserColor("#767f83"));
        paintPlay.setColor(ColorUtil.parserColor(Constants.lrcColorStr[Constants.lrcColorIndex]));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (!hasLrc || lyricsLineTreeMap == null || lyricsLineTreeMap.size() == 0) {
            //没有歌词，绘画默认的提示文字
            drawDefText(canvas);
        } else {
            drawLrcText(canvas);
        }
        // 画时间线和时间线
        if (isScroll) {
            drawIndicator(canvas);
        }

    }

    /**
     * 绘画时间线提示器
     *
     * @param canvas
     */
    private void drawIndicator(Canvas canvas) {
        //获取当前滑动到的歌词播放行
        int scrollLrcLineNum = getScrollLrcLineNum();
        int startTime = lyricsLineTreeMap.get(scrollLrcLineNum).getStartTime();
        String startTimeStr = TimeUtils.parseString(startTime);
        int textHeight = (int) getTextHeight(paintTimeLine);
        float textWidth = paintTimeLine.measureText(startTimeStr);// 用画笔测量歌词的宽度
        float textX = 0;
        float textY = (getMeasuredHeight() - textHeight) / 2;
        //画当前时间
        canvas.drawText(startTimeStr, textX, textY, paintTimeLine);

        //画播放按钮

        float playW = paintPlay.measureText(playText);
        int playH = (int) getTextHeight(paintPlay);
        int playX = (int) (getMeasuredWidth() - 5 - playW);
        int playY = (getMeasuredHeight() - playH) / 2;

        //按钮微调，增加击中的概率
        int rectAdjustmentNum = 20;
        playRect = new Rect();
        playRect.left = playX - rectAdjustmentNum;
        playRect.right = (int) (playX + playW) + rectAdjustmentNum;
        playRect.top = playY - rectAdjustmentNum;
        playRect.bottom = (playY + Math.abs(playH)) + rectAdjustmentNum;

        //绘画播放按钮
        canvas.drawText(playText, playX, playY, paintPlay);

        //
        float y = getMeasuredHeight() / 2;
        float x = textX + textWidth + 5;
        //画时间线
        canvas.drawLine(x, y, getWidth() - playW - textWidth - 2, y, paintTimeLine);
    }

    /**
     * 绘画默认的提示文字
     *
     * @param canvas
     */
    private void drawDefText(Canvas canvas) {
        Paint.FontMetrics fm = paintHLDEF.getFontMetrics();
        float textWidth = paintHLDEF.measureText(defTipText);// 用画笔测量歌词的宽度
        int textHeight = (int) getTextHeight(paintHLDEF);
        // save和restore是为了剪切操作不影响画布的其它元素
        canvas.save();

        float textX = (getWidth() - textWidth) / 2;
        float textY = (getMeasuredHeight() - textHeight) / 2;

        canvas.drawText(defTipText, textX, textY, paintHLDEF);

        // 设置过渡的颜色和进度，过滤的同时，对字体的高度进行微调
        canvas.clipRect(textX, textY - Math.abs(textHeight) - adjustmentNum, textX + textWidth / 2,
                textY + Math.abs(textHeight) + adjustmentNum);

        canvas.drawText(defTipText, textX, textY, paintHLED);
        canvas.restore();
    }

    /**
     * 绘画歌词
     *
     * @param canvas
     */
    private void drawLrcText(Canvas canvas) {
        //因为offsetY = 当前行 * getLineHeight(paint) ；所以为了让当前行可居中。。。。。
        //获取中间位置
        float centY = (getMeasuredHeight() - (int) getTextHeight(paintHLED)) / 2
                + lyricsLineNum * getLineHeight(paint) - offsetY;

        // 画当前歌词之前的歌词
        for (int i = lyricsLineNum - 1; i >= 0; i--) {
            String text = lyricsLineTreeMap.get(i).getLineLyrics();
            float textWidth = paint.measureText(text);
            float textX = (getWidth() - textWidth) / 2;
            float textY = centY
                    - (lyricsLineNum - i) * getLineHeight(paint);

            //超出视图的不再绘画
            if (textY < getLineHeight(paint)) {
                break;
            }
            canvas.drawText(text, textX, textY, paint);
        }

        //画当前的行歌词
        // drawCurLineLrc(canvas, centY);

        //绘画当前行的动感歌词
        drawDGLineLrc(canvas, centY);


        // 画当前歌词之后的歌词
        for (int i = lyricsLineNum + 1; i < lyricsLineTreeMap.size(); i++) {
            String text = lyricsLineTreeMap.get(i).getLineLyrics();
            float textWidth = paint.measureText(text);
            float textX = (getWidth() - textWidth) / 2;
            float textY = centY
                    + (i - lyricsLineNum) * getLineHeight(paint);

            //超出视图的不再绘画
            if (textY + getLineHeight(paint) > getHeight()) {
                break;
            }

            canvas.drawText(text, textX, textY, paint);

        }
    }

    /**
     * 绘画动感歌词
     *
     * @param canvas
     * @param curTextY
     */
    private void drawDGLineLrc(Canvas canvas, float curTextY) {
        LyricsLineInfo lyricsLineInfo = lyricsLineTreeMap
                .get(lyricsLineNum);
        // 整行歌词
        String lineLyrics = lyricsLineInfo.getLineLyrics();
        float lineLyricsWidth = paintHLED.measureText(lineLyrics);

        // 歌词
        if (lyricsWordIndex == -1) {
            //设置等于当行歌词的大小，防止跳转下一行歌词后，该行歌词不为高亮状态
            lineLyricsHLEDWidth = lineLyricsWidth;
        } else {
            String lyricsWords[] = lyricsLineInfo.getLyricsWords();
            int wordsDisInterval[] = lyricsLineInfo
                    .getWordsDisInterval();
            // 当前歌词之前的歌词
            String lyricsBeforeWord = "";
            for (int i = 0; i < lyricsWordIndex; i++) {
                lyricsBeforeWord += lyricsWords[i];
            }
            // 当前歌词
            String lyricsNowWord = lyricsWords[lyricsWordIndex].trim();// 去掉空格

            // 当前歌词之前的歌词长度
            float lyricsBeforeWordWidth = paintHLED
                    .measureText(lyricsBeforeWord);

            // 当前歌词长度
            float lyricsNowWordWidth = paintHLED.measureText(lyricsNowWord);

            float len = lyricsNowWordWidth
                    / wordsDisInterval[lyricsWordIndex]
                    * lyricsWordHLEDTime;
            lineLyricsHLEDWidth = lyricsBeforeWordWidth + len;
        }

        //文本大小
        int textHeight = (int) getTextHeight(paintHLDEF);
        float curTextX = (getWidth() - lineLyricsWidth) / 2;

        // save和restore是为了剪切操作不影响画布的其它元素
        canvas.save();

        // 画当前歌词
        canvas.drawText(lineLyrics, curTextX, curTextY, paintHLDEF);


        // 设置过渡的颜色和进度，过滤的同时，对字体的高度进行微调
        canvas.clipRect(curTextX, curTextY - Math.abs(textHeight) - adjustmentNum, curTextX + lineLyricsHLEDWidth,
                curTextY + Math.abs(textHeight) + adjustmentNum);

        // 画当前歌词
        canvas.drawText(lineLyrics, curTextX, curTextY, paintHLED);
        canvas.restore();

    }

    /**
     * 画当前行歌词
     *
     * @param canvas
     * @param curTextY
     */
    private void drawCurLineLrc(Canvas canvas, float curTextY) {
        //画当前的行歌词
        String curText = lyricsLineTreeMap.get(lyricsLineNum).getLineLyrics();
        float curTextWidth = paintHLED.measureText(curText);
        float curTextX = (getWidth() - curTextWidth) / 2;
        canvas.drawText(curText, curTextX, curTextY, paintHLED);
    }

    //----------------------------------------------------//
    /**
     * 隐藏指示器
     */
    private int HIDEINDICATOR = 0;
    /**
     *
     */
    private int hideDuration = 1500;
    /**
     * 还原歌词视图
     */
    private int RESETLRCVIEW = 1;
    /**
     *
     */
    private int resetDuration = 1000;

    /**
     * Handler处理滑动指示器隐藏和歌词滚动到当前播放的位置
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    //发送还原当前的播放
                    if (!isActionDown)
                        handler.sendEmptyMessageDelayed(RESETLRCVIEW, resetDuration);
                    break;
                case 1:
                    isScroll = false;
                    //移动到当前播放的位置
                    smoothScrollTo(lyricsLineNum * getLineHeight(paint));
                    invalidateView();
                    break;
            }
        }
    };

    /**
     * 记录上一次的数据
     */
    private float downX, downY, lastScrollY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!hasLrc) {
            return false;
        }
        obtainVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
                actionCancel(event);
                break;
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                actionUp(event);
                break;
            default:
                break;
        }
        invalidateView();
        return true;
    }

    /**
     * 速度跟踪器初始化
     *
     * @param event
     */
    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 释放
     */
    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 手势按下执行事件
     *
     * @param event
     */
    private void actionDown(MotionEvent event) {

        handler.removeMessages(RESETLRCVIEW);
        handler.removeMessages(HIDEINDICATOR);

        isScroll = true;
        isActionDown = true;

        lastScrollY = offsetY;
        downX = event.getX();
        downY = event.getY();

    }

    /**
     * 手势移动执行事件
     *
     * @param event
     */
    private void actionMove(MotionEvent event) {

        int lineCount = lyricsLineTreeMap.size();
        int lineHeight = getLineHeight(paint);

        float scrollY = lastScrollY + downY - event.getY();
        //滚动到第一行歌词和最后一行歌词
        if (scrollY < 0 || scrollY > lineHeight * lineCount) {
            //除以3使产生阻尼效果
            offsetY = lastScrollY + (downY - event.getY()) / 3;
        } else {
            offsetY = scrollY;
        }
        final VelocityTracker velocityTracker = mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        mVelocity = (int) velocityTracker.getYVelocity();
    }

    /**
     * 歌词事件
     */
    private OnLrcClickListener onLrcClickListener;

    /**
     * 手势抬起执行事件
     */
    private void actionUp(MotionEvent event) {
        //
        releaseVelocityTracker();
        //发送隐藏指示器
        handler.sendEmptyMessageDelayed(HIDEINDICATOR, hideDuration);

        isActionDown = false;
        int lineCount = lyricsLineTreeMap.size();
        int lineHeight = getLineHeight(paint);

        if (offsetY < 0) {
            //阻尼回弹
            flingAnimator(0);
            return;
        }
        if (offsetY > lineHeight * (lineCount - 1)) {
            //阻尼回弹
            flingAnimator(lineHeight * (lineCount - 1));
            return;
        }

        //滑动速度大于最少的启动速度
        if (Math.abs(mVelocity) > mMinimumVelocity) {


            //获取距离
            double totalDistance = getSplineFlingDistance((int) mVelocity);
            //缩小距离
            int mDistance = (int) (totalDistance * Math.signum(mVelocity)) / 3;
            int deltaY = (int) offsetY - mDistance;

            if (deltaY < 0) {
                deltaY = 0;
            } else if (deltaY > lineHeight * (lineCount - 1)) {
                deltaY = lineHeight * (lineCount - 1);
            }
            //
            flingAnimator(deltaY);

        }

        //判断是否在滑动和是否点击了播放按钮
        if (isScroll && playClick(event)) {

            isScroll = false;
            handler.removeMessages(RESETLRCVIEW);
            handler.removeMessages(HIDEINDICATOR);


            if (onLrcClickListener != null) {
                //获取当前滑动到的歌词播放行
                int scrollLrcLineNum = getScrollLrcLineNum();
                int startTime = lyricsLineTreeMap.get(scrollLrcLineNum).getStartTime();
                onLrcClickListener.onLrcPlayClicked(startTime);
            }

        }
    }

    ///--------------------Scroller源码代码---------------------------------------------//

    public double getSplineFlingDistance(int velocityY) {

        final double l = getG(velocityY);
        final double decelMinusOne = (float) (Math.log(0.78) / Math.log(0.9)) - 1.0;
        return ViewConfiguration.getScrollFriction() * (SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.37f // inch/meter
                * (context.getResources().getDisplayMetrics().density * 160.0f)
                * 0.84f) * Math.exp((float) (Math.log(0.78) / Math.log(0.9)) / decelMinusOne * l);
    }


    public double getG(int velocityY) {
        return Math.log(0.35f * Math.abs(velocityY) / (ViewConfiguration.getScrollFriction() * SensorManager.GRAVITY_EARTH
                * 39.37f // inch/meter
                * (context.getResources().getDisplayMetrics().density * 160.0f)
                * 0.84f));

    }
///-----------------------------------------------------------------//

    /**
     * 滑动动画
     *
     * @param deltaY
     */
    private void flingAnimator(int deltaY) {

        //使用开源动画库nineoldandroids来兼容api11之前的版本
        ValueAnimator animator = ValueAnimator.ofFloat(offsetY, deltaY);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                offsetY = (float) valueAnimator.getAnimatedValue();
                invalidateView();

            }
        });
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    /**
     * 判断是否是播放按钮点击
     *
     * @param event
     * @return
     */
    private boolean playClick(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();
        if (playRect.contains(x, y)) {
            return true;
        }

        return false;
    }

    /**
     * 获取滑动的当前行
     *
     * @return
     */
    private int getScrollLrcLineNum() {
        float scrollY = offsetY + getLineHeight(paint) / 2;
        int scrollLrcLineNum = (int) (scrollY / getLineHeight(paint));
        if (scrollLrcLineNum >= lyricsLineTreeMap.size()) {
            scrollLrcLineNum = lyricsLineTreeMap.size() - 1;
        } else if (scrollLrcLineNum < 0) {
            scrollLrcLineNum = 0;
        }
        return scrollLrcLineNum;
    }

    /**
     * 手势取消执行事件
     *
     * @param event
     */
    private void actionCancel(MotionEvent event) {

    }

    /**
     * 歌词事件
     */
    public interface OnLrcClickListener {
        /**
         * 歌词快进播放
         *
         * @param progress
         */
        void onLrcPlayClicked(int progress);
    }

    public void setOnLrcClickListener(OnLrcClickListener onLrcClickListener) {
        this.onLrcClickListener = onLrcClickListener;
    }

    //----------------------------------------------------//

    /**
     * 数据初始化
     *
     * @param lyricsParser
     */
    public void init(LyricsParserUtil lyricsParser) {

        this.lyricsParser = lyricsParser;
        lyricsLineTreeMap = lyricsParser.getLyricsLineTreeMap();

        //初始化数据
        lyricsLineNum = -1;
        lyricsWordIndex = -1;
        lineLyricsHLEDWidth = 0;
        offsetY = 0;
        isActionDown = false;
        isScroll = false;

        invalidateView();
    }

    /**
     * 根据播放进度绘画歌词
     *
     * @param curPlayingTime
     */
    public void showLrc(int curPlayingTime) {
        if (lyricsParser == null)
            return;
        //添加歌词时间补偿值
        curPlayingTime += lyricsParser.getPlayOffset();
        int newLyricsLineNum = lyricsParser
                .getLineNumber(curPlayingTime);
        if (newLyricsLineNum != lyricsLineNum) {

            //动态歌词参数重置
            lineLyricsHLEDWidth = 0;
            lyricsWordIndex = 0;

            //不是同一行的歌词，则对歌词进行上一行或者下一行移动，计算Y轴的移动距离 = 当前歌词的行No *  行高
            int deltaY = newLyricsLineNum * getLineHeight(paint);
            lyricsLineNum = newLyricsLineNum;

            //歌词移动到当前行
            smoothScrollTo(deltaY);
        }

        //判断当前播放到该行的第几个歌词
        lyricsWordIndex = lyricsParser.getDisWordsIndex(
                lyricsLineNum, curPlayingTime);
        //获取当行歌词第几个字，已播放的时间长度
        lyricsWordHLEDTime = lyricsParser.getDisWordsIndexLen(
                lyricsLineNum, curPlayingTime);

        invalidateView();
    }

    /**
     * 缓慢滚动到指定的位置
     *
     * @param deltaY 要滚动的距离
     */
    private void smoothScrollTo(int deltaY) {

        //使用开源动画库nineoldandroids来兼容api11之前的版本
        ValueAnimator animator = ValueAnimator.ofFloat(offsetY, deltaY);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {


                if (isScroll) {
                    valueAnimator.cancel();
                    return;
                }

                offsetY = (float) valueAnimator.getAnimatedValue();
                invalidateView();

            }
        });
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }


    /**
     * 获取字符串的文本高度
     *
     * @param paint
     * @return
     */
    public double getTextHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return Math.ceil(fm.descent + fm.ascent);
    }

    /**
     * 行高度 = 歌词文本高度 + 空行高度
     *
     * @return
     */
    public int getLineHeight(Paint paint) {
        return (int) Math.abs(getTextHeight(paint)) + spaceLineHeight;
    }

    /**
     * 获取当前时间对应的行歌词
     *
     * @param progress
     * @return
     */
    public String getLineLrc(int progress) {
        String lrc = "";
        if (!hasLrc)
            return lrc;
        if (lyricsParser == null)
            return lrc;
        int index = lyricsParser
                .getLineNumber(progress);
        if (lyricsLineTreeMap == null || index >= lyricsLineTreeMap.size())
            return lrc;
        LyricsLineInfo kscLyricsLineInfo = lyricsLineTreeMap.get(index);
        if (kscLyricsLineInfo == null)
            return lrc;
        lrc = kscLyricsLineInfo.getLineLyrics();
        return " " + lrc;
    }

    /**
     * 刷新View
     */
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //  当前线程是主UI线程，直接刷新。
            invalidate();
        } else {
            //  当前线程是非UI线程，post刷新。
            postInvalidate();
        }
    }

    /**
     * @param hasLrc
     */
    public void setHasLrc(boolean hasLrc) {
        this.hasLrc = hasLrc;
        //重绘
        invalidateView();
    }

    public boolean getHasLrc() {
        return hasLrc;
    }

    /**
     * 设置字体颜色
     */
    public void setFontColor() {
        initColor();
        //重绘
        invalidateView();
    }

    /***
     * 设置字体大小
     */
    public void setFontSize() {
        initFontSize();
        //因为字体大小发生了变化 ，所以这里面要设置当前的滑动的位置为当前行
        offsetY = lyricsLineNum * getLineHeight(paint);
        //重绘
        invalidateView();
    }
}
