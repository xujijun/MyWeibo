package com.xjj.myweibo.ui;

import java.io.ByteArrayOutputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xjj.myweibo.R;
import com.xjj.myweibo.controller.MainService;
import com.xjj.myweibo.controller.TaskType;
import com.xjj.myweibo.util.MyLog;

public class NewWeiboFragment extends WeiboFragment {

    private EditText etBlog;
    private Button  btBack;
    private Button  btSend;
    private Button btPic;
    private Button btGPS;
    private ImageView ivpic;
    private TextView tv;
    private byte picdat[];//图片字节流
    private static final int BT_TEXT=1;//文字微博
    private static final int BT_PIC=2;//图片微博
    private static final int BT_GPS=3;//GPS微博
    private static final int BT_PIC_GPS=4;//图片和GPS微博
    private double gpspoint[];
    private int blogType=BT_TEXT;//微博类型
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.newblog, container, false);
		

		//图片微博：
		ivpic = (ImageView) rootView.findViewById(R.id.ivCameraPic);
		btPic = (Button) rootView.findViewById(R.id.btGallery);
		btPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
				startActivityForResult(it, 0);
				// Intent it=new
				// Intent(NewWeiboActivity.this,CamerActivity.class);
				// NewWeiboActivity.this.startActivityForResult(it,22);
			}
		});
		
		// 位置微博：
		btGPS = (Button) rootView.findViewById(R.id.btGPS);
		btGPS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "此功能正在开发中……", Toast.LENGTH_SHORT)
						.show();

				// TODO 获取当前位置
				// gpspoint = GPSPoint.getGPSPoint(NewWeiboActivity.this);
				// Toast.makeText(NewWeiboActivity.this,
				// "获取当前位置\n精度" + gpspoint[0] + "\n纬度" + gpspoint[1], 500)
				// .show();
				// blogType = BT_GPS;
			}

		});
		
		// 字数提醒
		final TextView tvlabel = (TextView) rootView.findViewById(R.id.tvLabel);
		tvlabel.setText("剩余文字140");
		tvlabel.setVisibility(View.VISIBLE);

		etBlog = (EditText) rootView.findViewById(R.id.etBlog);
		etBlog.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				int charsRemaining = 140 - etBlog.length();
				String msg = null;
				
				if(charsRemaining < 0){
					msg = "<font color=\"red\">剩余字数：" + charsRemaining + "</font>";
				}
				else
					msg = "<font color=\"#008B00\">剩余字数：" + charsRemaining + "</font>";
				
				tvlabel.setText(Html.fromHtml(msg));
			}
		});
		
		View title = rootView.findViewById(R.id.title);
		
		tv = (TextView) title.findViewById(R.id.textView);
		tv.setText("写微博");

		// 发表微博：
		btSend = (Button) title.findViewById(R.id.title_bt_right);
		//btSend.setBackgroundResource(R.drawable.title_new);
		btSend.setText("发表");
		btSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (etBlog.length() > 140) {
					Toast.makeText(getActivity(), "字数超过140，无法发送",
							Toast.LENGTH_SHORT).show();
					return;
				}

				Intent it = new Intent(getActivity(), MainService.class);
				it.putExtra("msg", etBlog.getText().toString());

				switch (blogType) {
				case BT_TEXT:
					it.putExtra("Task", TaskType.TS_NEW_WEIBO);
					break;
				case BT_PIC:
					it.putExtra("Task", TaskType.TS_NEW_WEIBO_PIC);
					it.putExtra("picdata", picdat);
					break;
				case BT_GPS:
					it.putExtra("Task", TaskType.TS_NEW_WEIBO_GPS);
					it.putExtra("lat", gpspoint[0]);
					it.putExtra("lon", gpspoint[1]);
					break;
				case BT_PIC_GPS:
					// TODO 发表图片和位置微博
					// it.putExtra("Task", TaskType.TS_NEW_WEIBO_PIC_GPS);
					// it.putExtra("picdata", picdat);
					// it.putExtra("lat", 0);
					// it.putExtra("lon", 0);
					break;
				}

				getActivity().startService(it);
				tv.setText("努力发表中……");
			}

		});
		
		
		//退出当前窗口：
		btBack = (Button) title.findViewById(R.id.title_bt_left);
		btBack.setBackgroundResource(R.drawable.title_back);
		btBack.setText("返回");
		btBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});

		return rootView;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	

	@Override
	public void init() {
		// Do Nothing

	}

	@Override
	public void refresh(Object... param) {
		//int type = (Integer) param[0];
		int result = (Integer) param[1];
		if (result == 1) {
			Toast.makeText(getActivity(), "微博发表成功！", Toast.LENGTH_LONG).show();
			getActivity().finish();
		}

	}
	
	/**
	 * 获得拍照结果
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		MyLog.t("NewWeiboFragment: onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap bmp=(Bitmap)data.getExtras().get("data");

		ivpic.setVisibility(View.VISIBLE);
		ivpic.setImageBitmap(bmp);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 50, bos);
		this.picdat = bos.toByteArray();// 将图片转化为字节数组
		this.blogType = BT_PIC;
	}


}
