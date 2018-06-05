package com.tapc.platform.adpater;

import java.util.List;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * ViewPager的自定义Adapter
 * @author allen@tronsis.com
 * @date 2016-1-15 上午11:00:10
 */
public class SimpleViewPagerAdapter extends FragmentPagerAdapter {

	/** ViewPager管理的所有页**/
	private List<Fragment> fragments;

	public SimpleViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

}
