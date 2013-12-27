package com.xjj.myweibo.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import weibo4j.model.Status;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xjj.myweibo.R;
import com.xjj.myweibo.controller.MainService;
import com.xjj.myweibo.controller.TaskType;
import com.xjj.myweibo.ui.WeiboFragment;
import com.xjj.myweibo.ui.WriteWeiboActivity;
import com.xjj.myweibo.util.MyLog;

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
	private ImageView tweet_upload_pic;
	private boolean hasPic = false;

	@Override
	public void init() {
		//获取传递过来的微博信息
		status= (Status) getActivity().getIntent().getSerializableExtra("status");
		
		String midPicUrl = status.getBmiddlePic();
		hasPic = (midPicUrl!=null && midPicUrl.length()>0);
		if(hasPic)
		{//发送任务下载图片
			Intent it = new Intent(getActivity(), MainService.class);
			it.putExtra("Task", TaskType.TS_GET_STATUS_PIC_MID);
			it.putExtra("from", "WeiboInfo");
			it.putExtra("url", midPicUrl);
			getActivity().startService(it);
			
			MyLog.t("WeiboInfofragment---getMidPic");
		}
		
		//MyLog.t("WeiboInfofragment---init() 1");

	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		//MyLog.t("WeiboInfoFragment---onCreateView 1");

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
		
		//回到主界面
		Button home = (Button) title.findViewById(R.id.title_bt_right);
		home.setBackgroundResource(R.drawable.title_home);
		home.setOnClickListener(this);
		
		((TextView) title.findViewById(R.id.textView)).setText(R.string.title_mblog_content);
		
		//转发（原微博）布局：
		if(status.getRetweetedStatus()!=null){
			View src_text_block = rootView.findViewById(R.id.src_text_block);
			src_text_block.setVisibility(View.VISIBLE);
			TextView tweet_oriTxt = (TextView)src_text_block.findViewById(R.id.tweet_oriTxt);
			
			tweet_oriTxt.setText(status.getRetweetedStatus().getText());
			//TODO:
			//TextViewLink.addURLSpan(status.getRetweetedStatus().getText(), tweet_oriTxt);
			ImageView tweet_upload_pic2 = (ImageView)src_text_block.findViewById(R.id.tweet_upload_pic2);
		}
		
		//用户名
		TextView tweet_profile_name = (TextView) rootView.findViewById(R.id.tweet_profile_name);
		tweet_profile_name.setText(status.getUser().getName());
		
		//微博正文
		TextView tweet_message = (TextView) rootView.findViewById(R.id.tweet_message);
		tweet_message.setText(status.getText());
		//TODO 定义话题的局部连接
	    //TextViewLink.addURLSpan(status.getText(), tweet_message);
		
		//头像
		ImageView tweet_profile_preview = (ImageView) rootView.findViewById(R.id.tweet_profile_preview);
	    if(MainService.allicon.get(status.getUser().getId())!=null)
        {	
	    	tweet_profile_preview.setImageDrawable(MainService.allicon.get(status.getUser().getId()));
        }else
        {// 如果之前没有下载头像，设定缺省的图片
        	tweet_profile_preview.setImageResource(R.drawable.portrait);	
        }
		
	    //图片（大小？）
		tweet_upload_pic = (ImageView) rootView.findViewById(R.id.tweet_upload_pic);
		if(hasPic)
			tweet_upload_pic.setVisibility(View.VISIBLE);
		tweet_upload_pic.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View v) {
        		//TODO 打开新窗口显示原始图片
//		      Intent it = new Intent(getActivity(), ShowStatusBitmap.class);
//		      it.putExtra("url", status.getOriginalPic());
//		      startActivity(it);
			}
		}
		);
		
		//发表日期
		TextView tweet_updated =(TextView) rootView.findViewById(R.id.tweet_updated);
		SimpleDateFormat f = new SimpleDateFormat("yyyy年MM月dd日 E kk点mm分"); 
		String s = f.format(status.getCreatedAt());
		tweet_updated.setText(s);
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(status.getCreatedAt());
//		int month = cal.get(Calendar.MONTH) + 1;
//		int day = cal.get(Calendar.DAY_OF_MONTH);
//		int hours = cal.get(Calendar.HOUR_OF_DAY);
//		int minute = cal.get(Calendar.MINUTE);;
//		tweet_updated.setText(month+"-"+day+"  "+hours+"："+minute);
		
		//是否“大V"
		ImageView tweet_profile_vip = (ImageView) rootView.findViewById(R.id.tweet_profile_vip);
		if(status.getUser().isVerified()){//如果是VIP
			tweet_profile_vip.setVisibility(View.VISIBLE);
		}
		//如果头像已经下载
		
		//评论
		TextView tweet_comment =(TextView) rootView.findViewById(R.id.tweet_comment);
		tweet_comment.setText("评论[" + status.getCommentsCount() + "]");//
		tweet_comment.setTag(COMMENTLIST);
		tweet_comment.setOnClickListener(this);
		
		//转发
		TextView tweet_redirect =(TextView) rootView.findViewById(R.id.tweet_redirect);
		tweet_redirect.setText("转发[" + status.getRepostsCount() + "]");//
		
		//来源
		TextView tweet_via =(TextView) rootView.findViewById(R.id.tweet_via);
		tweet_via.setText("来自："+Html.fromHtml(status.getSource().getName()));
		
		//刷新
		TextView tvRefresh = (TextView) rootView.findViewById(R.id.tvReload);
		tvRefresh.setTag(REFRESH);
		tvRefresh.setOnClickListener(this);
		
		//评论
		TextView tvComment = (TextView) rootView.findViewById(R.id.tvComment);
		tvComment.setTag(COMMENT);
		tvComment.setOnClickListener(this);
		
		//转发
		TextView tvForward = (TextView) rootView.findViewById(R.id.tvForward);
		tvForward.setTag(FORWARD);
		tvForward.setOnClickListener(this);
		
		//收藏
		TextView tvFav = (TextView) rootView.findViewById(R.id.tvFav);
		tvFav.setTag(FAV);
		tvFav.setOnClickListener(this);
		
		//短信分享
		TextView tvMore = (TextView) rootView.findViewById(R.id.tvMore);
		tvMore.setTag(MORE);
		tvMore.setOnClickListener(this);
		
		
		return rootView;
	}

	
	public void onClick(View v) {
		int tag = (Integer) v.getTag();
		Intent it = null;
		
		switch (tag) {
		case BACK:
			getActivity().finish();
			break;
			
		case REFRESH:
			Toast.makeText(getActivity(), "REFRESH", 3000).show();
			break;
			
		case COMMENT:
			it = new Intent(getActivity(), WriteWeiboActivity.class);
			it.putExtra("fragment", "CommentFragment");
			it.putExtra("id", status.getId());
			it.putExtra("Task", TaskType.TS_COMMENT_WEIBO);
			it.putExtra("currentContent", "");
			startActivity(it);
			break;
			
		case FORWARD:
			it = new Intent(getActivity(), WriteWeiboActivity.class);
			it.putExtra("fragment", "CommentFragment");
			it.putExtra("id", status.getId());
			it.putExtra("Task", TaskType.TS_FORWARD_WEIBO);

			// 有原微博的时候，把当前微博正文当成转发的内容
			Status re_st = status.getRetweetedStatus();
			if (re_st != null) {
				it.putExtra("currentContent", "//@" + status.getUser().getName()
						+ "：" + status.getText());
			} else {
				it.putExtra("currentContent", "");
			}
			startActivity(it);
			break;
			
		case FAV:
			Toast.makeText(getActivity(), "FAV", 3000).show();
			break;
			
		case MORE:
			// Toast.makeText(this, "MORE", 3000).show();
			// 通过短信方式分享这条微博内容
			Uri uri = Uri.parse("smsto://");
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			if (this.status.getText().length() > 70) {
				intent.putExtra("sms_body",
						this.status.getText().substring(0, 66) + "...");
			} else {
				intent.putExtra("sms_body", this.status.getText());
			}
			startActivity(intent);
			break;
			
		case USER:
			Toast.makeText(getActivity(), "USER", 3000).show();
			//TODO 通知MainActivity切换到用户tab
//			MainService.showus = status.getUser();
//			MainActivity.mainUI.th.setCurrentTabByTag("TS_USER");
//			MainActivity.mainUI.bt02.setChecked(true);
//			getActivity().finish();
			break;
			
		case COMMENTLIST:
			Toast.makeText(getActivity(), "COMMENTLIST", 3000).show();
			break;
		}
	}

	public void refresh(Object... param) {
		int type = (Integer) param[0];
		
		if (type == TaskType.TS_COMMENT_WEIBO) {
			Toast.makeText(getActivity(), "评论发表成功", 500).show();
			
		} else if (type == TaskType.TS_FORWARD_WEIBO) {
			Toast.makeText(getActivity(), "转发微博成功", 500).show();
			
		} else if (type == TaskType.TS_GET_STATUS_PIC_MID) {
			if(param[1] == null){
				Toast.makeText(getActivity(), "获取中型图片失败", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Bitmap contextBt = ((BitmapDrawable) param[1]).getBitmap();
			
			tweet_upload_pic.setImageBitmap(contextBt);
			//tweet_upload_pic.setVisibility(View.VISIBLE);
			
			Toast t = Toast.makeText(getActivity(), "获取中型图片成功", 500);
			//ImageView iv = new ImageView(getActivity());
			//iv.setImageBitmap(contextBt);
			//t.setView(iv);
			t.show();
		}
	}



}
