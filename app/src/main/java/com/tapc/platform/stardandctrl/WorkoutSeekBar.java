package com.tapc.platform.stardandctrl;

import com.tapc.platform.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class WorkoutSeekBar extends SeekBar {
	static boolean IS_CAN_MOV = false;
	private float mPosSize;
	private Paint mPaint;
	private float mMin = 0;

	private float offsetValue = 0;

	public WorkoutSeekBar(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public WorkoutSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Workout, 0, 0);
		mPosSize = a.getDimension(R.styleable.Workout_valueSize, 15);
		int color = a.getColor(R.styleable.Workout_textColor, 0xFFFFFFFF);
		mPaint = new Paint();
		mPaint.setColor(color);
		a.recycle();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (IS_CAN_MOV) {
			return super.onTouchEvent(event);
		} else {
			if (event.getAction() == MotionEvent.ACTION_DOWN
					|| event.getAction() == MotionEvent.ACTION_UP) {
				return super.onTouchEvent(event);
			} else {
				return false;
			}
		}
	}
	@SuppressLint("NewApi")
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		String strValue = String.format("%.1f", getPos() - offsetValue);
		mPaint.setStyle(Style.FILL);
		mPaint.setTextSize(mPosSize);
		FontMetrics fontMetrics = mPaint.getFontMetrics();
		// ���ָ߶�
		float fontHeight = fontMetrics.bottom - fontMetrics.top;
		// ���ֿ��
		float fontLen = mPaint.measureText(strValue);
		// ����baseline
		float textBaseY = getThumb().getBounds().top
				+ (getThumb().getBounds().height() - fontHeight) / 2
				+ fontHeight;
		float textBaseX = getThumb().getBounds().left
				+ (getThumb().getBounds().width() - fontLen) / 2;
		canvas.drawText(strValue, textBaseX, fontHeight, mPaint);
	}

	public void SetRange(float min, float max, float step) {
		mMin = min;
		setMax((int) (max * 100 - min * 100));
		invalidate();
	}

	public float getPos() {
		float pos = getProgress();
		pos /= 100;
		pos += mMin;
		return pos;
	}

	public void setPos(float pos) {
		pos -= mMin;
		setProgress((int) (pos * 100));
		invalidate();
	}
}
