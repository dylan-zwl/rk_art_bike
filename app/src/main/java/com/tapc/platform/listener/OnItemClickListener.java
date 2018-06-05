/**
 * OnItemClick.java[v 1.0.0]
 * classes:com.jht.tapc.platform.listener.OnItemClick
 * fch Create of at 2015�?3�?18�? 下午6:31:43
 */
package com.tapc.platform.listener;

import android.view.View;

public interface OnItemClickListener<Entity> {
	
	void onItemClick(View view, Entity entity);
}
