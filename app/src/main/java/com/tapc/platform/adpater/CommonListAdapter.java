package com.tapc.platform.adpater;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;


/**
 * 通用ListView Adapter
 * 
 * @author allen@tronsis.com
 */
public abstract class CommonListAdapter<T> extends BaseAdapter implements SectionIndexer {
	protected LayoutInflater inflater;
	protected Context context;
	protected List<T> datas;
	protected final int itemLayoutId;

	public CommonListAdapter(Context context, List<T> mDatas, int itemLayoutId) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.datas = mDatas;
		this.itemLayoutId = itemLayoutId;
	}
	
	public List<T> getData(){
		return datas;
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public T getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder = getViewHolder(position, convertView, parent);
		convert(viewHolder, getItem(position), position, parent);
		return viewHolder.getConvertView();

	}

	public abstract void convert(ViewHolder helper, T item, int position, ViewGroup parent);

	private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
		return ViewHolder.get(context, convertView, parent, itemLayoutId, position);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@Override
	public int getPositionForSection(int section) {
//		for (int i = 0; i < getCount(); i++) {
//			SortModel sortModel = (SortModel) datas.get(i);
//			String sortStr = sortModel.getPinyinCategory();
//			char firstChar = sortStr.toUpperCase().charAt(0);
//			if (firstChar == section) {
//				return i;
//			}
//		}

		return -1;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	@Override
	public int getSectionForPosition(int position) {
		return 0;
//		SortModel sortModel = (SortModel) datas.get(position);
//		return sortModel.getPinyinCategory().charAt(0);
	}

	@Override
	public Object[] getSections() {
		return null;
	}
	
	public void notifyDataSetChanged(List<T> data){
		this.datas = data;
		this.notifyDataSetChanged();
	}
}
