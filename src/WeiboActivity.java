import com.xjj.myweibo.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

/**
 * 为其他activity所继承的类
 * @author XJJ
 *
 */
public abstract class WeiboActivity extends Activity {

    public abstract void init();
    public abstract void refresh(Object ... param);
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	
	}
	
	

	@Override
	protected void onStart() {
		super.onStart();
		init();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.weibo, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_account:
			
			break;
			
		case R.id.action_settings:
			
			break;
			
		case R.id.action_about:
			
			break;
			
		case R.id.action_exit:
			exitPromote();
			
			break;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){ //用户按退出键的时候
			exitPromote();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 提示用户是否要退出
	 */
	private void exitPromote(){
		AlertDialog.Builder  ab=new AlertDialog.Builder(this);
		ab.setTitle("退出提示");
		ab.setMessage("您真的要退出吗?");
		ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			   finish();
		       android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		ab.setNegativeButton("取消",null);
		ab.create().show();
	}

}
