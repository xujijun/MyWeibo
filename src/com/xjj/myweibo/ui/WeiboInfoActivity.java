package com.xjj.myweibo.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.xjj.myweibo.R;
import com.xjj.myweibo.model.WeiboInfoFragment;

/**
 * 显示微博详情
 * @author XJJ
 *
 */
public class WeiboInfoActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(findViewById(R.id.fragment_container) != null){
			FragmentTransaction transation = getSupportFragmentManager().beginTransaction();

			WeiboInfoFragment wif = new WeiboInfoFragment();
			transation.add(R.id.fragment_container, wif);
			transation.commit();
		}
	}
}
