package com.xjj.myweibo.model;

import weibo4j.model.Status;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xjj.myweibo.R;
import com.xjj.myweibo.ui.WeiboFragment;

public class WeiboInfoFragment extends WeiboFragment  implements OnClickListener{
	private static final int BACK= -1;
	private static final int REFRESH = 0;
	private static final int COMMENT = 1;//评论
	private static final int FORWARD = 2;
	private static final int FAV = 3;
	private static final int MORE = 4;
	private static final int USER = 5;
	private static final int COMMENTLIST = 6;//评论列表
	private Status status;

	@Override
	public void init() {
		//获取传递过来的微博信息
		status= (Status) getActivity().getIntent().getSerializableExtra("status");
	}
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.detailweibo, container, false);

		//用户描述：
		RelativeLayout tweet_profile = (RelativeLayout) rootView.findViewById(R.id.tweet_profile);
		tweet_profile.setTag(USER);
		tweet_profile.setOnClickListener(this);
		
		View title = rootView.findViewById(R.id.detailweibo_title);
		
		//返回按钮
		Button back = (Button) title.findViewById(R.id.title_bt_left);
	    back.setOnClickListener(this);
	    back.setTag(BACK);
		back.setText(R.string.imageviewer_back);
		
		//
		Button home = (Button) title.findViewById(R.id.title_bt_right);
		home.setBackgroundResource(R.drawable.title_home);
		home.setOnClickListener(this);
		
		
		return rootView;
	}

	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void refresh(Object... param) {
		// TODO Auto-generated method stub

	}




}
