package com.tapc.platform.adpater;

import java.util.List;

import com.tapc.platform.R;
import com.tapc.platform.entity.ChallengeTpye;
import com.tapc.platform.entity.RankingInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChallengeListAdapter extends BaseAdapter {
	private Context mContext;
	private List<RankingInfo> mList;
	private int mMode = 0;

	public ChallengeListAdapter(Context context, List<RankingInfo> list) {
		this.mContext = context;
		this.mList = list;
	}

	public void setMode(int mode) {
		mMode = mode;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.challenge_list_item, null);
			holder.rankingBg = (ImageButton) convertView.findViewById(R.id.challenge_rank_bg);
			holder.ranking = (TextView) convertView.findViewById(R.id.challenge_rank);
			holder.user = (TextView) convertView.findViewById(R.id.challenge_datetime);
			holder.data = (TextView) convertView.findViewById(R.id.challenge_item_value);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		switch (position) {
		case 0:
			holder.rankingBg.setImageResource(R.drawable.rank_first);
			break;
		case 1:
			holder.rankingBg.setImageResource(R.drawable.rank_second);
			break;
		case 2:
			holder.rankingBg.setImageResource(R.drawable.rank_third);
			break;
		default:
			holder.ranking.setText(mList.get(position).getRanking());
			break;
		}
		holder.user.setText(mList.get(position).getUser());
		String value = mList.get(position).getData();
		switch (mMode) {
		case ChallengeTpye.DISTANCE_5:
		case ChallengeTpye.DISTANCE_10:
			holder.data.setText(value);
			break;
		case ChallengeTpye.TIME_20:
			holder.data.setText(value + " Km");
			break;
		default:
			break;
		}
		return convertView;
	}

	public void notifyDataSetChanged(List<RankingInfo> data) {
		this.mList = data;
		this.notifyDataSetChanged();
	}

	class ViewHolder {
		ImageButton rankingBg;
		TextView ranking;
		TextView user;
		TextView data;
	}
}
