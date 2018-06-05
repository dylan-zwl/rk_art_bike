package com.tapc.platform.witget;
/**
 * AdjustView.java[v 1.0.0]
 * classes:com.jht.tapc.platform.widget.AdjustView
 * fch Create of at 2015�?�?0�?下午5:50:23
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnTouch;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;

public class AdjustView extends FrameLayout implements OnTouchListener {
	/**
	 * 设置速度
	 */
	public static final int MODEL_SPEED = 0;

	/**
	 * 设置坡度
	 */
	public static final int MODEL_GRADIENT = 1;

	/**
	 * 可以设为设置速度、设置坡�?
	 */
	private int model = MODEL_SPEED;

	@ViewInject(R.id.valueText)
	private TextView mValueText;

	public AdjustView(Context context) {
		this(context, null);
	}

	public AdjustView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public AdjustView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		View view = LayoutInflater.from(context).inflate(R.layout.adjust_view,
				this);
		ViewUtils.inject(view);
	}

	public void setTitle(String title) {
		mValueText.setText(title);
	}

	public void setModel(int model) {
		this.model = model;
	}

	@OnClick(R.id.addBtn)
	private void addClick(View v) {
		switch (model) {
			case MODEL_SPEED :
				TapcApp.getInstance().mainActivity.getSpeedCtrl()
						.addClick(null);
				break;
			case MODEL_GRADIENT :
				TapcApp.getInstance().mainActivity.getInclineCtrl().addClick(
						null);
				break;
		}
	}

	@OnClick(R.id.minusBtn)
	private void minutClick(View v) {
		switch (model) {
			case MODEL_SPEED :
				TapcApp.getInstance().mainActivity.getSpeedCtrl()
						.subClick(null);
				break;
			case MODEL_GRADIENT :
				TapcApp.getInstance().mainActivity.getInclineCtrl().subClick(
						null);
				break;
		}
	}

	@OnTouch(R.id.addBtn)
	private boolean addTouch(View v, MotionEvent event) {
		return false;
	}

	@OnTouch(R.id.minusBtn)
	private boolean minitTouch(View v, MotionEvent envent) {
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
