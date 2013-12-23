package com.xjj.myweibo.ui;


import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.xjj.myweibo.R;
import com.xjj.myweibo.model.AccessTokenKeeper;
import com.xjj.myweibo.util.Constants;

public class LoginActivity extends Activity {

    /** 显示认证后的信息，如 AccessToken */
    private TextView textViewToken;
    
    private Button buttonLogin;
    private Button buttonNewWeibo;
    private Button buttonCheckWeibo;

    /** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWeiboAuth;
    
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    
    private boolean authorized = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		textViewToken = (TextView)findViewById(R.id.textViewToken);
		
        // 创建鉴权实例
        mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);

        // 通过单点登录 (SSO) 获取 Token
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(authorized){
            		//已经获取授权
    				Intent it=new Intent(LoginActivity.this, MainActivity.class);
    			    startActivity(it);
    			    finish();
            	}else{
            		//尚未获取授权
            		mSsoHandler = new SsoHandler(LoginActivity.this, mWeiboAuth);
            		mSsoHandler.authorize(new AuthListener());
            	}
            }
        });
        
        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()) {
            updateTokenView(true);
        }else{
    		buttonNewWeibo.setVisibility(View.GONE);
    		buttonCheckWeibo.setVisibility(View.GONE);
        }
        
        //发微博
		buttonNewWeibo = (Button) findViewById(R.id.buttonNewWeibo);
		buttonNewWeibo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent it=new Intent(LoginActivity.this, WriteWeiboActivity.class);
				it.putExtra("fragment", "NewWeiboFragment");
			    startActivity(it);
			}
			
		});
		
		//看微博
		buttonCheckWeibo = (Button) findViewById(R.id.buttonCheckWeibo);
		buttonCheckWeibo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent it=new Intent(LoginActivity.this, MainActivity.class);
			    startActivity(it);
			    finish();				
			}
		});
	}

	
    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     */	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
        // SSO 授权回调
        // 发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
	}



	/**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
                updateTokenView(false);
                
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            } else {
                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code = values.getString("code");
                String message = "登录失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "取消登录", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
	
    /**
     * 显示当前 Token 信息。
     * 
     * @param tokenExisted 配置文件中是否已存在 token 信息并且合法
     */
    private void updateTokenView(boolean tokenExisted) {
        AccessTokenKeeper.token = mAccessToken.getToken();
    	
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = getString(R.string.token_to_string_format_1);
        textViewToken.setText(String.format(format, mAccessToken.getToken(), date));
        

        
        String message = String.format(format, mAccessToken.getToken(), date);
        if (tokenExisted) {
            message = getString(R.string.token_already_existed) + "\n" + message;
        }else{
    		buttonNewWeibo.setVisibility(View.GONE);
    		buttonCheckWeibo.setVisibility(View.GONE);
        }
        
        textViewToken.setText(message);
        
        buttonLogin.setText("登录成功!");
        buttonLogin.setEnabled(false);
        authorized = true;
        
		//define a 3 seconds animation
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1.3f, 0.1f, 1.3f);
		sa.setDuration(3000);
		ImageView iv = (ImageView) findViewById(R.id.imageView_login);
		iv.setAnimation(sa);
		sa.start();
    }
}
