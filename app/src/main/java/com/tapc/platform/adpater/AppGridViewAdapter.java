package com.tapc.platform.adpater;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tapc.platform.R;
import com.tapc.platform.entity.AppInfoEntity;

/**
 * @author Jason.liu
 * @Email 1946711081@qq.com TODO
 */
public class AppGridViewAdapter extends BaseAdapter {

	private List<AppInfoEntity> mListAppInfo = null;
	private Context mContext;

	public AppGridViewAdapter(Context context, List<AppInfoEntity> mListAppInfo) {
		this.mListAppInfo = mListAppInfo;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return mListAppInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return mListAppInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.app_grid_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}

		AppInfoEntity appInfo = (AppInfoEntity) getItem(position);
		viewHolder.app_iconz_img.setImageDrawable(appInfo.getAppIcon());
		viewHolder.app_name_tx.setText(appInfo.getAppLabel());

		return convertView;
	}

	class ViewHolder {
		@ViewInject(R.id.app_iconz_img)
		private ImageView app_iconz_img;// 图像
		@ViewInject(R.id.app_name_tx)
		private TextView app_name_tx;// 名称

		public ViewHolder(View view) {
			ViewUtils.inject(this, view);
		}
	}

}
