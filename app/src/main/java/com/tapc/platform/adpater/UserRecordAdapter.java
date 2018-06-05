package com.tapc.platform.adpater;

import java.util.List;

import com.tapc.platform.Config;
import com.tapc.platform.R;
import com.tapc.platform.sql.SportRecordItem;

import android.content.Context;
import android.renderscript.ProgramFragmentFixedFunction.Builder.Format;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UserRecordAdapter extends BaseAdapter {
	private Context mContext;
	private List<SportRecordItem> mList;

	public UserRecordAdapter(Context context, List<SportRecordItem> list) {
		this.mContext = context;
		this.mList = list;
	}

	@Override
	public int getCount() {
		if (mList.size() > 0) {
			return mList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {

		return position;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.record_item, null);

			holder.datetime = (TextView) convertView
					.findViewById(R.id.datetime_record);
			holder.runtime = (TextView) convertView
					.findViewById(R.id.runtime_record);
			holder.distance = (TextView) convertView
					.findViewById(R.id.distance_record);
			holder.calories = (TextView) convertView
					.findViewById(R.id.calories_record);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.datetime.setText(mList.get(position).datetime);
		if (Config.SERVICE_ID == 0) {
			int runtime = Integer.valueOf(
					mList.get(position).getSportsData().getRuntime())
					.intValue();
			double distance = Double.valueOf(mList.get(position)
					.getSportsData().getDistance());
			double calories = Double.valueOf(mList.get(position)
					.getSportsData().getCalories());
			if (runtime < 60) {
				holder.runtime.setText(Integer.toString(runtime) + " Sec");
			} else {
				holder.runtime.setText(Integer.toString(runtime / 60) + " Min");
			}
			holder.distance.setText(String.format("%.2f", distance) + " Km");
			holder.calories.setText(String.format("%.2f", calories) + " Kcal");
		} else if (Config.SERVICE_ID == 1) {
			String runtime = mList.get(position).getSportsData().getRuntime();
			String distance = mList.get(position).getSportsData().getDistance();
			String calories = mList.get(position).getSportsData().getCalories();
			holder.runtime.setText(runtime + " Min");
			holder.distance.setText(distance + " Km");
			holder.calories.setText(calories + " Kcal");
		}
		return convertView;
	}

	class ViewHolder {
		TextView datetime;
		TextView runtime;
		TextView distance;
		TextView calories;
	}
}
