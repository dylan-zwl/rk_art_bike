package com.tapc.platform.stardandctrl;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import com.tapc.platform.R;

public class WorkoutValue extends View {

	private float mMin = 0;
	private float mMax = 0;
	private float mUnitSize;
	private Paint mPaint;

	public WorkoutValue(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public WorkoutValue(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Workout, 0, 0);
		mUnitSize = a.getDimension(R.styleable.Workout_unitSize, 15);
		int color = a.getColor(R.styleable.Workout_textColor, 0xFFFFFFFF);
		mPaint = new Paint();
		mPaint.setColor(color);
		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		String strValue;
		strValue = String.format("%.1f", mMin);
		mPaint.setStyle(Style.FILL);
		mPaint.setTextSize(mUnitSize);
		FontMetrics fontMetrics = mPaint.getFontMetrics();

		float fontHeight = fontMetrics.bottom - fontMetrics.top;

		float fontLen = mPaint.measureText(strValue);

		float height = getHeight();
		float width = getWidth();
		float textBaseY = (height + fontHeight) / 2 - fontMetrics.bottom;
		float textBaseX = 0;
		canvas.drawText(strValue, textBaseX, textBaseY, mPaint);

		strValue = String.format("%.1f", mMax);
		fontLen = mPaint.measureText(strValue);
		textBaseX = width - fontLen;
		canvas.drawText(strValue, textBaseX, textBaseY, mPaint);

	}

	public void SetRange(float min, float max) {
		mMin = min;
		mMax = max;
		invalidate();
	}

	public float getMin() {
		return mMin;
	}

	public float getMax() {
		return mMax;
	}
}
