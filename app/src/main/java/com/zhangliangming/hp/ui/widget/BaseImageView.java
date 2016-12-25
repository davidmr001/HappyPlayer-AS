package com.zhangliangming.hp.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zhangliangming.hp.ui.util.FontUtil;


/**
 * 图标
 * 
 * @author zhangliangming
 * 
 */
public class BaseImageView extends TextView {

	public BaseImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public BaseImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BaseImageView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		// 设置字体图片
		Typeface iconfont = FontUtil.getInstance(context).getTypeFace();
		setTypeface(iconfont);
	}

}
