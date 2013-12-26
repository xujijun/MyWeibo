package com.xjj.myweibo.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.xjj.myweibo.R;
import com.xjj.myweibo.model.WeiboInfoFragment;
import com.xjj.myweibo.util.MyLog;

/**
 * 显示微博详情
 * @author XJJ
 *
 */
public class WeiboInfoActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_weibo);

		MyLog.t("WeiboInfoActivity---onCreate 1");

		if(findViewById(R.id.fragment_container) != null){
			MyLog.t("WeiboInfoActivity---onCreate 2");

			FragmentTransaction transation = getSupportFragmentManager().beginTransaction();

			WeiboInfoFragment wif = new WeiboInfoFragment();
			transation.add(R.id.fragment_container, wif);
			transation.commit();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	   if(keyCode==KeyEvent.KEYCODE_BACK)
	   {
		   finish();
		   return true;
	   }
		return super.onKeyDown(keyCode, event);
	}
}
