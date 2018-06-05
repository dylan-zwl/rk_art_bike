/**
 * GalleryAdapter.java[v 1.0.0]
 * classes:com.jht.tapc.platform.adpater.GalleryAdapter
 * fch Create of at 2015��3��17�� ����6:05:13
 */
package com.tapc.platform.adpater;

import java.util.ArrayList;
import java.util.List;

import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.entity.AppInfoEntity;
import com.tapc.platform.listener.OnItemClickListener;
import com.tapc.platform.media.PlayEntity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * classes:com.jht.tapc.platform.adpater.GalleryAdapter
 * 
 * @author fch <br/>
 *         Create of at 2015��3��17�� ����6:05:13
 */
@SuppressLint("NewApi")
public class AppGalleryAdapter
		extends
			RecyclerView.Adapter<AppGalleryAdapter.ViewHolder>
		implements
			OnClickListener {

	private List<AppInfoEntity> mListAppInfo = null;
	private OnItemClickLitener mOnItemClickLitener;
	private Context mContext;

	public AppGalleryAdapter(Context context, List<AppInfoEntity> list) {
		mContext = context;
		mListAppInfo = list;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView mImg;
		TextView mTxt;
		public ViewHolder(View arg0) {
			super(arg0);
		}
	}

	@Override
	public int getItemCount() {
		return (mListAppInfo != null) ? mListAppInfo.size() : 0;
	}

	/**
	 * ItemClick的回调接口
	 * 
	 * @author zhy
	 * 
	 */
	public interface OnItemClickLitener {
		void onItemClick(View view, int position);
	}

	public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
		this.mOnItemClickLitener = mOnItemClickLitener;
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
		viewHolder.itemView.setTag(mListAppInfo.get(position));
		viewHolder.mImg.setImageDrawable(mListAppInfo.get(position)
				.getAppIcon());
		viewHolder.mTxt.setText(mListAppInfo.get(position).getAppLabel());
		// 如果设置了回调，则设置点击事件
		if (mOnItemClickLitener != null) {
			viewHolder.itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnItemClickLitener.onItemClick(viewHolder.itemView,
							position);
				}
			});
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.app_grid_item, arg0, false);
		view.setOnClickListener(this);
		ViewHolder holder = new ViewHolder(view);
		holder.mImg = (ImageView) view.findViewById(R.id.app_iconz_img);
		holder.mTxt = (TextView) view.findViewById(R.id.app_name_tx);
		return holder;
	}

	@Override
	public void onClick(View arg0) {

	}
}
