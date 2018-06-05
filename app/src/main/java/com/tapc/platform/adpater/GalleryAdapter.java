/**
 * GalleryAdapter.java[v 1.0.0]
 * classes:com.jht.tapc.platform.adpater.GalleryAdapter
 * fch Create of at 2015��3��17�� ����6:05:13
 */
package com.tapc.platform.adpater;

import java.util.ArrayList;

import com.tapc.platform.Config;
import com.tapc.platform.R;
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
public class GalleryAdapter extends
		RecyclerView.Adapter<GalleryAdapter.ViewHolder> implements
		OnClickListener {

	private ArrayList<PlayEntity> mList;

	private Context mContext;

	private OnItemClickListener<PlayEntity> mOnItemClickListener;

	public GalleryAdapter(Context context, ArrayList<PlayEntity> list) {
		mContext = context;
		mList = list;
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
		return (mList != null) ? mList.size() : 0;
	}

	@Override
	public void onBindViewHolder(ViewHolder viewholder, int index) {
		viewholder.itemView.setTag(mList.get(index));
		if (mList.get(index).getPath().equals(Config.VA_FILE_PATH_NAND)) {
			viewholder.mImg.setImageBitmap(BitmapFactory
					.decodeFile(Config.VA_FILE_PATH_NAND + "/"
							+ mList.get(index).still));
		} else if (mList.get(index).getPath().equals(Config.VA_FILE_PATH_SD)) {
			viewholder.mImg.setImageBitmap(BitmapFactory
					.decodeFile(Config.VA_FILE_PATH_SD + "/"
							+ mList.get(index).still));
		}
		viewholder.mTxt.setText(mList.get(index).name);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.scene_gallery_item, arg0, false);
		view.setOnClickListener(this);
		ViewHolder holder = new ViewHolder(view);
		holder.mImg = (ImageView) view.findViewById(R.id.play_gallery_img);
		holder.mTxt = (TextView) view.findViewById(R.id.play_gallery_name);
		return holder;
	}

	public void setOnItemClickListener(OnItemClickListener<PlayEntity> listhener) {
		mOnItemClickListener = listhener;
	}

	@Override
	public void onClick(View v) {
		if (mOnItemClickListener != null) {
			PlayEntity entity = (PlayEntity) v.getTag();
			mOnItemClickListener.onItemClick(v, entity);
		}
	}
}
