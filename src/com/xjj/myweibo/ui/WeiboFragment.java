package com.xjj.myweibo.ui;

import com.xjj.myweibo.controller.MainService;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 被其他Fragment继承的类
 * @author XJJ
 *
 */
public abstract class WeiboFragment extends Fragment {
	
    public abstract void init();
    public abstract void refresh(Object ... param);
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainService.addFragment(this);
		init();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		//init();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MainService.removeFragment(this);
	}
    
    
}
