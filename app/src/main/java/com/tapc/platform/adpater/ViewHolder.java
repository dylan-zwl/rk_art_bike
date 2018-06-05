package com.tapc.platform.adpater;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tapc.platform.R;

/**
 * Adapter ViewHolder
 * 
 * @author allen@tronsis.com
 */
public class ViewHolder {
	private final SparseArray<View> views;
	private int position;
	private View convertView;

	private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
		this.position = position;
		this.views = new SparseArray<View>();
		convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		// setTag
		convertView.setTag(this);
	}

	/**
	 * 拿到�?��ViewHolder对象
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId, position);
		}
		return (ViewHolder) convertView.getTag();
	}

	public View getConvertView() {
		return convertView;
	}

	/**
	 * 通过控件的Id获取对应的控件，如果没有则加入views
	 * 
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId) {
		View view = views.get(viewId);
		if (view == null) {
			view = convertView.findViewById(viewId);
			views.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 为TextView设置字符�?
	 * 
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageResource(int viewId, int drawableId) {
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param bitmap
	 * @return
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param url
	 * @return
	 */
	// public ViewHolder setImageByUrl(int viewId, String url) {
	// ImageView view = getView(viewId);
	// SysApplication.imageLoader.displayImage(url, view);
	// return this;
	// }

	public static int getUserIcId(String name) {
		int id = R.drawable.iv_head_portrait_normal;
		int index = 0;
		for (int i = 0; i < name.length(); i++) {
			char fir0 = name.charAt(i);
			index = index + fir0;
		}
		index = index % 53;
		switch (index) {
		case 0:
			id = R.drawable.user_ic_0;
			break;
		case 1:
			id = R.drawable.user_ic_1;
			break;
		case 2:
			id = R.drawable.user_ic_2;
			break;
		case 3:
			id = R.drawable.user_ic_3;
			break;
		case 4:
			id = R.drawable.user_ic_4;
			break;
		case 5:
			id = R.drawable.user_ic_5;
			break;
		case 6:
			id = R.drawable.user_ic_6;
			break;
		case 7:
			id = R.drawable.user_ic_7;
			break;
		case 8:
			id = R.drawable.user_ic_8;
			break;
		case 9:
			id = R.drawable.user_ic_9;
			break;
		case 10:
			id = R.drawable.user_ic_10;
			break;
		case 11:
			id = R.drawable.user_ic_11;
			break;
		case 12:
			id = R.drawable.user_ic_12;
			break;
		case 13:
			id = R.drawable.user_ic_13;
			break;
		case 14:
			id = R.drawable.user_ic_14;
			break;
		case 15:
			id = R.drawable.user_ic_15;
			break;
		case 16:
			id = R.drawable.user_ic_16;
			break;
		case 17:
			id = R.drawable.user_ic_17;
			break;
		case 18:
			id = R.drawable.user_ic_18;
			break;
		case 19:
			id = R.drawable.user_ic_19;
			break;
		case 20:
			id = R.drawable.user_ic_20;
			break;
		case 21:
			id = R.drawable.user_ic_21;
			break;
		case 22:
			id = R.drawable.user_ic_22;
			break;
		case 23:
			id = R.drawable.user_ic_23;
			break;
		case 24:
			id = R.drawable.user_ic_24;
			break;
		case 25:
			id = R.drawable.user_ic_25;
			break;
		case 26:
			id = R.drawable.user_ic_26;
			break;
		case 27:
			id = R.drawable.user_ic_27;
			break;
		case 28:
			id = R.drawable.user_ic_28;
			break;
		case 29:
			id = R.drawable.user_ic_29;
			break;
		case 30:
			id = R.drawable.user_ic_30;
			break;
		case 31:
			id = R.drawable.user_ic_31;
			break;
		case 32:
			id = R.drawable.user_ic_32;
			break;
		case 33:
			id = R.drawable.user_ic_33;
			break;
		case 34:
			id = R.drawable.user_ic_34;
			break;
		case 35:
			id = R.drawable.user_ic_35;
			break;
		case 36:
			id = R.drawable.user_ic_36;
			break;
		case 37:
			id = R.drawable.user_ic_37;
			break;
		case 38:
			id = R.drawable.user_ic_38;
			break;
		case 39:
			id = R.drawable.user_ic_39;
			break;
		case 40:
			id = R.drawable.user_ic_40;
			break;
		case 41:
			id = R.drawable.user_ic_41;
			break;
		case 42:
			id = R.drawable.user_ic_42;
			break;
		case 43:
			id = R.drawable.user_ic_43;
			break;
		case 44:
			id = R.drawable.user_ic_44;
			break;
		case 45:
			id = R.drawable.user_ic_45;
			break;
		case 46:
			id = R.drawable.user_ic_46;
			break;
		case 47:
			id = R.drawable.user_ic_47;
			break;
		case 48:
			id = R.drawable.user_ic_48;
			break;
		case 49:
			id = R.drawable.user_ic_49;
			break;
		case 50:
			id = R.drawable.user_ic_50;
			break;
		case 51:
			id = R.drawable.user_ic_51;
			break;
		case 52:
			id = R.drawable.user_ic_52;
			break;
		case 53:
			id = R.drawable.user_ic_53;
			break;

		default:
			id = R.drawable.iv_head_portrait_normal;
			break;
		}
		return id;
	}
}