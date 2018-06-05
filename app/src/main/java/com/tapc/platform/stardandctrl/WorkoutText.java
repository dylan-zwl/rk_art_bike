package com.tapc.platform.stardandctrl;
import com.tapc.platform.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class WorkoutText extends View {

	private String mStringTitle;
	private String mStringUnit;
	private String mStringValue;
	private float mTitleSize;
	private float mUnitSize;
	private float mValueSize;
	private float mScale;
	private float mOffset_x;
	private Paint mPaint;

	public WorkoutText(Context context) {
		this(context, null);
	}

	public WorkoutText(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Workout, 0, 0);
		mStringTitle = a.getString(R.styleable.Workout_titleName);
		mTitleSize = a.getDimension(R.styleable.Workout_titleSize, 15);
		mStringUnit = a.getString(R.styleable.Workout_unitName);
		mUnitSize = a.getDimension(R.styleable.Workout_unitSize, 15);
		mStringValue = a.getString(R.styleable.Workout_valueName);
		mValueSize = a.getDimension(R.styleable.Workout_valueSize, 15);
		mScale = a.getDimension(R.styleable.Workout_scale, 2);
		mOffset_x = a.getFloat(R.styleable.Workout_offset_x, 0);
		int color = a.getColor(R.styleable.Workout_textColor, 0xFFFFFFFF);
		mPaint = new Paint();
		mPaint.setColor(color);
		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		mPaint.setStyle(Style.FILL);
		mPaint.setTextSize(mTitleSize);
		FontMetrics fontMetrics = mPaint.getFontMetrics();
		float fontHeight = fontMetrics.bottom - fontMetrics.top;
		float fontLen = mPaint.measureText(mStringTitle);
		float height = getHeight() / mScale;
		float width = getWidth();
		float textBaseY = (height + fontHeight) / 2 - fontMetrics.bottom;
		float textBaseX = (width - fontLen) / 2;
		canvas.drawText(mStringTitle, textBaseX, textBaseY, mPaint);

		mPaint.setTextSize(mUnitSize);
		fontMetrics = mPaint.getFontMetrics();
		fontHeight = fontMetrics.bottom - fontMetrics.top;
		fontLen = mPaint.measureText(mStringUnit);
		textBaseY = height + fontHeight;
		textBaseX = (width - fontLen) / 2;
		canvas.drawText(mStringUnit, textBaseX, textBaseY, mPaint);

		mPaint.setTextSize(mValueSize);
		fontMetrics = mPaint.getFontMetrics();
		fontHeight = fontMetrics.bottom - fontMetrics.top;
		if (mStringValue != null) {
			fontLen = mPaint.measureText(mStringValue);
			textBaseY = 2 * height + (2 * height + fontHeight) / 2
					- fontMetrics.bottom;
			textBaseX = (width - fontLen) / 2;
			canvas.drawText(mStringValue, textBaseX + mOffset_x, textBaseY,
					mPaint);
		}
	}

	public void setValue(String value) {
		mStringValue = value;
		invalidate();
	}
	
	public void setUnit(String uint){
		mStringUnit = uint;
		invalidate();
	}
	
	public void setTitle(String title){
		mStringTitle = title;
		invalidate();
	}
}
