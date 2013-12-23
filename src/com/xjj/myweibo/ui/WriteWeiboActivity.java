/**
 * 
 */
package com.xjj.myweibo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.xjj.myweibo.R;

/**
 * @author XJJ
 * 写微博，转发、评论微博
 */
public class WriteWeiboActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_weibo);

		if(findViewById(R.id.fragment_container) != null){
			Intent it = getIntent();
		    FragmentTransaction transation = getSupportFragmentManager().beginTransaction();
			
			if(it.getStringExtra("fragment").equals("NewWeiboFragment")){ //写微博
				WeiboFragment nwf = new NewWeiboFragment();
				transation.add(R.id.fragment_container, nwf);
				transation.commit();
			}
			else if(it.getStringExtra("fragment").equals("ForwardFragment")){ //转发微博
				//ForwardWeiboFragment fwf = new ForwardWeiboFragment();
				//fwf.setArguments(it.getExtras());
				
			}else if(it.getStringExtra("fragment").equals("CommentFragment")){ //转发微博
				CommentFragment cf = new CommentFragment();
				//cf.setArguments(it.getExtras());
				transation.add(R.id.fragment_container, cf);
				transation.commit();
			}
		}
	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
	}	
}
