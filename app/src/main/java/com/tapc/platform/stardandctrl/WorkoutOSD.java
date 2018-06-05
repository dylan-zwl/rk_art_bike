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

public class WorkoutOSD extends View{

	private String mStringTop="";
	private String mStringBottom="";
	private float mTopSize;
	private float mBottomSize;
	private float mOffset_y;
	private Paint mPaint;
	
	public WorkoutOSD(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public WorkoutOSD(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Workout, 0, 0);
				mStringTop = a.getString(R.styleable.Workout_titleName);
				mTopSize = a.getDimension(R.styleable.Workout_titleSize, 15);
				mStringBottom = a.getString(R.styleable.Workout_unitName);
				mBottomSize = a.getDimension(R.styleable.Workout_unitSize, 15);
				mOffset_y = a.getFloat(R.styleable.Workout_offset_y, 0);
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
		 mPaint.setTextSize(mTopSize);
		 FontMetrics fontMetrics = mPaint.getFontMetrics();
         //���ָ߶�
         float fontHeight = fontMetrics.bottom-fontMetrics.top;
         //���ֿ��
         float fontLen = mPaint.measureText(mStringTop);
         //����baseline
         float height = getHeight()/2;
         float width = getWidth();
         float textBaseY = (height+fontHeight)/2-fontMetrics.bottom;
         float textBaseX = (width-fontLen)/2;
         canvas.drawText(mStringTop, textBaseX, textBaseY, mPaint);
         
         mPaint.setTextSize(mBottomSize);
         fontMetrics = mPaint.getFontMetrics();
         fontHeight = fontMetrics.bottom-fontMetrics.top;
         fontLen = mPaint.measureText(mStringBottom);
         textBaseY = height+fontMetrics.bottom;
         textBaseX = (width-fontLen)/2;
         canvas.drawText(mStringBottom, textBaseX, textBaseY+mOffset_y, mPaint);
         //�������  
         //��������  
     } 
	 
	 public void setTopString(String strTop) {
		mStringTop = strTop;
		invalidate();
	}
	 
	 public void setBottomString(String strBottom) {
		mStringBottom = strBottom;
		invalidate();
	}

}
