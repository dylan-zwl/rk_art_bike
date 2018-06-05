package com.tapc.platform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tapc.platform.R;
import com.tapc.platform.TapcApp;
import com.tapc.platform.activity.base.BaseActivity;
import com.tapc.platform.dto.GroupUserDTO;
import com.tapc.platform.dto.ResponseDTO;
import com.tapc.platform.dto.net.Callback;
import com.tapc.platform.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class GroupUserDetailActivity extends BaseActivity {
    @ViewInject(R.id.tv_time)
    private TextView tvTime;

    @ViewInject(R.id.tv_speed)
    private TextView tvSpeed;

    @ViewInject(R.id.tv_distance)
    private TextView tvDistance;

    @ViewInject(R.id.tv_calorie)
    private TextView tvCalorie;

    @ViewInject(R.id.tv_member_name)
    private TextView tvName;

    @ViewInject(R.id.tv_member_like)
    private TextView tvLikeNumber;

    @ViewInject(R.id.rb_user_detail_like)
    private RadioButton RbLike;

    private GroupUserDTO userDTO;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_group_user_detail);
        ViewUtils.inject(this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            userDTO = (GroupUserDTO) intent.getExtras().get("groupUserDTO");
            System.out.println(userDTO.toString());
            tvName.setText(userDTO.getRunner());
            tvTime.setText(userDTO.getTimeTampStr());
            tvDistance.setText(userDTO.getDistanceStr() + " Km");
            tvSpeed.setText(userDTO.getSpeedStr() + " Km/h");
            tvCalorie.setText(userDTO.getCaloriesStr() + " kcal");
            // tvLikeNumber.setText(userDTO.getMachine());
        }
        isHasLiked();
        likeCountPost();
    }

    @SuppressWarnings("deprecation")
    @OnClick(R.id.rb_user_detail_like)
    public void RbLikeOnClick(View v) {
        if (!isHasLiked()) {
            likePost();
        } else {
            Toast.makeText(GroupUserDetailActivity.this, R.string.has_liked, Toast.LENGTH_SHORT).show();
        }
    }

    private void likePost() {
        TapcApp.getInstance().getHttpClient().like(userDTO.getRunner(), new Callback() {

            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onSuccess(Object o) {
                ResponseDTO dto = (ResponseDTO) o;
                String str = dto.getResponse().toString();
                String count = str.substring(0, str.indexOf("."));
                tvLikeNumber.setText(count);
                String newNameStr = "";
                String read = PreferenceHelper.readString(GroupUserDetailActivity.this, "likeRunner", "runners");
                newNameStr = read + userDTO.getRunner() + ",";
                PreferenceHelper.write(GroupUserDetailActivity.this, "likeRunner", "runners", newNameStr);
                System.out.println("new--->" + newNameStr + "old--->" + read);
                RbLike.setChecked(true);
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onFailure(Object o) {
            }
        });
    }

    private void likeCountPost() {

        TapcApp.getInstance().getHttpClient().likeCount(userDTO.getRunner(), new Callback() {

            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onSuccess(Object o) {
                ResponseDTO dto = (ResponseDTO) o;
                String str = dto.getResponse().toString();
                String count = str.substring(0, str.indexOf("."));
                tvLikeNumber.setText(count);
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onFailure(Object o) {
            }
        });

    }

    private boolean isHasLiked() {
        String oldNameStr = PreferenceHelper.readString(this, "likeRunner", "runners");
        if (oldNameStr != null && oldNameStr.length() > 0) {
            List<String> list = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer(oldNameStr, ",");
            while (st.hasMoreTokens()) {
                list.add(st.nextToken());
            }

            for (String s : list) {
                System.out.println(s);
                if (s.equals(userDTO.getRunner())) {
                    RbLike.setChecked(true);
                    return true;
                }
            }
            return false;
        } else {
            PreferenceHelper.write(GroupUserDetailActivity.this, "likeRunner", "runners", "");
            RbLike.setChecked(false);
            return false;
        }

    }

}
