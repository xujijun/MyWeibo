package com.xjj.myweibo.ui;

import com.xjj.myweibo.R;
import com.xjj.myweibo.R.id;
import com.xjj.myweibo.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;


public class LogoActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_logo);
		
		ImageView iv = (ImageView)findViewById(R.id.logo_bg);
		
		//define a 3 seconds animation
		AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
		aa.setDuration(300);
		
		iv.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				// launch the logon activity
				Intent it=new Intent(LogoActivity.this, LoginActivity.class);
			    startActivity(it);
			    finish();
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// Do Nothing
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// Do Nothing
				
			}
			
		});
	}
}
