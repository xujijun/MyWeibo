package com.xjj.myweibo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xjj.myweibo.R;
import com.xjj.myweibo.controller.MainService;
import com.xjj.myweibo.controller.TaskType;
import com.xjj.myweibo.util.MyLog;

public class CommentFragment extends WeiboFragment {

	private View title;
	private Button back,send;
	private TextView titleTv;
	private EditText editText;
	private TextView tvCmtLabel;
	private CheckBox rb_forward;
	private CheckBox comment_to_orig;
	
	private Intent itIn;
	private String id;
	private Boolean writeComment;
	String currentContent = "";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		

		
		View rootView = inflater.inflate(R.layout.comment, container, false);
		
		//标题文字
		title = rootView.findViewById(R.id.title);
		titleTv = (TextView)title.findViewById(R.id.textView);
		
		if(writeComment)
			titleTv.setText("微博评论");
		else
			titleTv.setText("转发微博");
		
		editText = (EditText)rootView.findViewById(R.id.etCmtReason);
		if(!writeComment){ //如果是转发，则初始化当前微博内容
			editText.setText(currentContent);
			MyLog.t(currentContent);
		}
		editText.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				int charsRemaining = 140 - s.length();
				String msg = null;
				
				if(charsRemaining < 0){
					msg = "<font color=\"red\">剩余字数：" + charsRemaining + "</font>";
				}
				else
					msg = "<font color=\"#008B00\">剩余字数：" + charsRemaining + "</font>";
				
				tvCmtLabel.setText(Html.fromHtml(msg));
				
			}
			
		});
		
		
		tvCmtLabel =(TextView)rootView.findViewById(R.id.tvCmtLabel);
		tvCmtLabel.setText("少于140");
		
		
		rb_forward = (CheckBox) rootView.findViewById(R.id.rb_forward);
		if(writeComment)
			rb_forward.setText("同时转发到我的微博");
		else
			rb_forward.setText("同时评论给作者");
		
		comment_to_orig = (CheckBox) rootView.findViewById(R.id.comment_to_original_author);
		
		//返回按钮
		back = (Button) title.findViewById(R.id.title_bt_left);
		back.setText("返回");
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		
		//发送按钮
		send = (Button)title.findViewById(R.id.title_bt_right);
		send.setText("发送");
		send.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
		        String comment = editText.getText().toString();
		        
				Intent it = new Intent(getActivity(), MainService.class);
				it.putExtra("comment", comment);
				it.putExtra("id", id);

				//转发或评论
				if(writeComment){
					it.putExtra("Task", TaskType.TS_COMMENT_WEIBO);
				}
				else{
					it.putExtra("Task", TaskType.TS_FORWARD_WEIBO);
				}
				
				//同时评论给原文作者
				if(comment_to_orig.isChecked()){
					it.putExtra("commentToOrig", Boolean.valueOf(true));
				}else{
					it.putExtra("commentToOrig", Boolean.valueOf(false));
				}
					
		        
				// 是否“同时转发到我的微博”或“同时发表评论”
				if (rb_forward.isChecked()) {
					it.putExtra("commentAndForward", Boolean.valueOf(true));
				} else {
					it.putExtra("commentAndForward", Boolean.valueOf(false));
				}
				
				getActivity().startService(it);
				titleTv.setText("发送中……");
			}
			
		});
		return rootView;
	}

	@Override
	public void init() {
		try{//获取要评论微博的ID，是否写评价或转发，转发的内容
			itIn = getActivity().getIntent();
			id = itIn.getStringExtra("id");
			if(id==null||id==""){
				Toast.makeText(getActivity(), "微博ID为空", Toast.LENGTH_LONG).show();
				getActivity().finish();
			}else{
				writeComment = itIn.getBooleanExtra("writeComment", true);
			}
			
			currentContent = itIn.getStringExtra("currentContent");
			MyLog.t(currentContent);
			
		}catch(Exception e){
			Log.e("CommentFragment", "get ID from Intent Error!");
			getActivity().finish();
		}
		
	}

	@Override
	public void refresh(Object... param) {
		
		int result =0;
		try {
			result = (Integer) param[1];
		} catch (Exception e) {
			getActivity().finish();
		}
		
		if (result == 1) {
			String msg = null;
			if(writeComment)
				msg = "微博评论成功！";
			else
				msg = "微博转发成功！";
				
			if(msg != null)
				Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
		}
		getActivity().finish();
	}

}
