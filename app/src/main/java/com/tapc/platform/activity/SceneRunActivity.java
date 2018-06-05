package com.tapc.platform.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.R;
import com.tapc.android.data.Enum.ProgramType;
import com.tapc.platform.Config;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.adpater.GalleryAdapter;
import com.tapc.platform.listener.OnItemClickListener;
import com.tapc.platform.media.PlayEntity;
import com.tapc.platform.media.ValUtil;
import com.tapc.platform.sportsrunctrl.SportsEngin;

/**
 * classes:com.jht.tapc.platform.activity.SceneRunActivity
 * 
 * @author fch <br/>
 *         Create of at 2015�?3�?17�? 上午10:16:33
 */
@SuppressLint("NewApi")
public class SceneRunActivity extends BaseActivity implements
		OnItemClickListener<PlayEntity> {
	@ViewInject(R.id.recycler_view)
	private RecyclerView mRecyclerView;

	@ViewInject(R.id.surface_view)
	private SurfaceView mSurfaceView;

	@ViewInject(R.id.videoMessage)
	private TextView mVideoText;

	public static void launch(Context c) {
		Intent i = new Intent(c, SceneRunActivity.class);
		c.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_scenerun);
		init();
	}

	private void init() {
		ViewUtils.inject(this);
		initPlayer();
	}

	private void initPlayer() {
		ArrayList<PlayEntity> playList1 = ValUtil
				.getValList(Config.VA_FILE_PATH_SD);
		ArrayList<PlayEntity> playList2 = ValUtil
				.getValList(Config.VA_FILE_PATH_NAND);
		ArrayList<PlayEntity> playList = new ArrayList<PlayEntity>();
		playList.addAll(playList1);
		playList.addAll(playList2);
		LinearLayoutManager manager = new LinearLayoutManager(this);
		manager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mRecyclerView.setLayoutManager(manager);
		GalleryAdapter galleryAdapter = new GalleryAdapter(this, playList);
		galleryAdapter.setOnItemClickListener(this);
		mRecyclerView.setAdapter(galleryAdapter);
	}

	private SportsEngin getSportsEngin() {
		return TapcApp.getInstance().getSportsEngin();
	}

	@Override
	public void onItemClick(View view, PlayEntity entity) {
		TapcApp.getInstance().vaRecordPosition.setVaRecordPosition(false, 0, -1);
		getSportsEngin().openScene(entity);
		ScenePlayActivity.launch(this, entity);
		this.finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}
