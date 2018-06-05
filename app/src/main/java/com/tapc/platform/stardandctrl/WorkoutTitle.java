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

public class WorkoutTitle extends View {

	private String mStringTitle;
	private String mStringUnit;
	private float mTitleSize;
	private float mUnitSize;
	private Paint mPaint;
	private Context mContext;

	public WorkoutTitle(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public WorkoutTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Workout, 0, 0);
		mStringTitle = a.getString(R.styleable.Workout_titleName);
		mStringUnit = a.getString(R.styleable.Workout_unitName);
		mTitleSize = a.getDimension(R.styleable.Workout_titleSize, 15);
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
		mPaint.setStyle(Style.FILL);
		mPaint.setTextSize(mTitleSize);
		FontMetrics fontMetrics = mPaint.getFontMetrics();

		float fontHeight = fontMetrics.bottom - fontMetrics.top;

		float fontLen = mPaint.measureText(mStringTitle);

		float height = getHeight();
		float width = getWidth();
		float textBaseY = (height + fontHeight) / 2 - fontMetrics.bottom;
		float textBaseX = (width - fontLen) / 2;
		canvas.drawText(mStringTitle, textBaseX, textBaseY, mPaint);

		textBaseX = textBaseX + fontLen;
		mPaint.setTextSize(mUnitSize);
		fontMetrics = mPaint.getFontMetrics();
		fontHeight = fontMetrics.bottom - fontMetrics.top;
		fontLen = mPaint.measureText(mStringUnit);
		textBaseY = (height + fontHeight) / 2 - fontMetrics.bottom;
		canvas.drawText(mStringUnit, textBaseX, textBaseY, mPaint);
		// �������
		// ��������
	}

	public void setTitleName(String titleName, String stringUnit) {
		mStringTitle = titleName;
		mStringUnit = stringUnit;
		postInvalidate();
	}
}
