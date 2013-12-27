package com.xjj.myweibo.controller;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import weibo4j.Account;
import weibo4j.Comments;
import weibo4j.Favorite;
import weibo4j.Timeline;
import weibo4j.Users;
import weibo4j.http.ImageItem;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.User;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.xjj.myweibo.model.AccessTokenKeeper;
import com.xjj.myweibo.ui.WeiboFragment;
import com.xjj.myweibo.util.GetPicFromURL;
import com.xjj.myweibo.util.MyLog;


public class MainService extends IntentService {
	//向本Service注册的Fragment，以便被回调
	private static ArrayList<WeiboFragment> allFragments=new ArrayList<WeiboFragment>();
	
    //                   用户id    头像
    public static HashMap<String, BitmapDrawable> allicon
                           =new HashMap<String,BitmapDrawable>();
    
    public static Timeline weibo; //读微博、发微博
    public static Comments cmt;   //发评论
    public static Favorite favorite; //收藏、取消收藏
    
	public MainService() throws WeiboException, JSONException {
		super("XJJ");
		
		//读取用户资料
		 Account am = new Account();
	     am.client.setToken(AccessTokenKeeper.token);
		 Users um = new Users();
		 um.client.setToken(AccessTokenKeeper.token);
		 //JSONObject uid =am.getUid();
		//User user = um.showUserById(uid.get("uid").toString());
		 
		//初始化微博处理类 
		weibo = new Timeline();
		weibo.setToken(AccessTokenKeeper.token);
		//weibo.client.setToken(AccessTokenKeeper.token);
		
		//初始化评论类
		cmt = new Comments();
		cmt.setToken(AccessTokenKeeper.token);
		
		//初始化收藏类
		favorite = new Favorite();
		favorite.setToken(AccessTokenKeeper.token);

	}
	
	public MainService(String name) {
		super(name);
	}
	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		int task = intent.getIntExtra("Task", 0);
		Message message=handler.obtainMessage();
		message.what=task;
		
		MyLog.t("onHandleIntent Entry: "+task);

		switch (task) {
		//获取最新关注的微博
		case TaskType.TS_GET_FRIENDS_HOMETIMELINE:
			//MyLog.t("HOMETIMELINE Entry: "+task);

			try {
				//Paging p=new Paging(1,5);
				StatusWapper astatus = weibo.getFriendsTimeline();
				 List<Status> alls=astatus.getStatuses();
				message.obj = alls;
			} catch (WeiboException e) {
				Toast.makeText(this, "刷新失败", Toast.LENGTH_SHORT).show();
				//e.printStackTrace();
			}

			break;
			
		//获取更多微博	
		case TaskType.TS_GET_FRIENDS_HOMETIMELINE_MORE:
			//MyLog.t("HOMETIMELINE_MORE Entry: "+task);
			
	    	try {
				int currentPage = intent.getIntExtra("currentPage", 0);
				int pageSize = intent.getIntExtra("pageSize", 0);
				List<Status> allsmore=weibo.getFriendsTimeline(0,0,new Paging(currentPage, pageSize)).getStatuses();
				message.obj=allsmore;
			} catch (WeiboException e) {
				Toast.makeText(this, "获取失败", Toast.LENGTH_SHORT).show();
				//e.printStackTrace();
			}
	    	
			break;
			
		case TaskType.TS_GET_USER_ICON:// 下载头像
			User u = (User) intent.getSerializableExtra("User");
			// 获取该用户的头像
			BitmapDrawable bd = GetPicFromURL.getPic(u.getProfileImageURL());
			if (bd != null) {
				//Bitmap.createBitmap(bd.getBitmap());
				allicon.put(u.getId(), bd);
			}
			break;
		
		case TaskType.TS_NEW_WEIBO:// 发表微博
			try {
				String msg = intent.getStringExtra("msg");
				weibo.UpdateStatus(msg);
				message.obj = 1;// 1表示发表成功
			} catch (WeiboException e) {
				Toast.makeText(this, "发表失败", Toast.LENGTH_SHORT).show();
				//e.printStackTrace();
			}

			break;

		case TaskType.TS_NEW_WEIBO_PIC:// 发表图片微博

			try {
				String msg1 = intent.getStringExtra("msg");
				byte picdat[] = intent.getByteArrayExtra("picdata");
				Log.d("MyTest", "picdat------------------" + picdat.length);
				ImageItem item = new ImageItem("pic", picdat);
				weibo.UploadStatus(URLEncoder.encode(msg1, "UTF-8"), item);
				
				//MyLog.t("Pic Msg:" + msg1);
				message.obj = 1;// 1表示发表成功
			} catch (WeiboException e) {
				Toast.makeText(this, "发表失败", Toast.LENGTH_SHORT).show();
				//e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				Toast.makeText(this, "发表失败", Toast.LENGTH_SHORT).show();
				//e.printStackTrace();
			}

			break;
			
	    case TaskType.TS_COMMENT_WEIBO://发表评论
			String id = intent.getStringExtra("id");
			String comment = intent.getStringExtra("comment");
			Boolean commentAndForward = intent.getBooleanExtra("commentAndForward", false);
			Boolean commentToOrig = intent.getBooleanExtra("commentToOrig", false);

			try {
				//comment = URLEncoder.encode(comment, "UTF-8");
				// 是否转发到我的微博
				if (commentAndForward) {
					if(commentToOrig){
						MyLog.t("评论当前微博、转发、并评论给原文作者");
						weibo.Repost(id, comment, 3); // 3：评论当前微博、转发、并评论给原文作者
					}else{
						MyLog.t("评论当前微博、并转发");
						weibo.Repost(id, comment, 1); // 1：评论当前微博、并转发
					}
				}else{//只评论
					if(commentToOrig){
						MyLog.t("评论当前微博、并评论给原作者");
						cmt.createComment(comment, id, 1); // 1：评论当前微博、并评论给原作者
					}else{
						cmt.createComment(comment, id, 0); // 0: 只评论当前微博
					}
				}
				message.obj = 1;// 1表示发表成功
			} catch (Exception e) {
				Toast.makeText(this, "发表评论失败", Toast.LENGTH_SHORT).show();
				//e.printStackTrace();
			}
			
			break;
			
	    case TaskType.TS_FORWARD_WEIBO://转发微博
			id = intent.getStringExtra("id");
			comment = intent.getStringExtra("comment");
			commentAndForward = intent.getBooleanExtra("commentAndForward", false);
			commentToOrig = intent.getBooleanExtra("commentToOrig", false);

			try {
				//comment = URLEncoder.encode(comment, "UTF-8");
				// 是否同时评论给原作者
				if (commentAndForward) {
					if(commentToOrig){
						weibo.Repost(id, comment, 3); // 3：转发、评论、并评论给原作者
					}else{
						weibo.Repost(id, comment, 1); // 1：转发、评论、不评论给原作者
					}
					
				}else{
					if(commentToOrig){
						weibo.Repost(id, comment, 2); // 2：转发、并评论给原作者
					}else{
						weibo.Repost(id, comment, 0); // 0：只转发不评论
					}
				}
				message.obj = 1;// 1表示成功
			} catch (Exception e) {
				Toast.makeText(this, "转发失败", Toast.LENGTH_SHORT).show();
				//e.printStackTrace();
			}
			
			break;
			
	    case TaskType.TS_CREATE_FAVORITE:
	    	id = intent.getStringExtra("id");
			MyLog.t("MainService --- Create Favorite ---- ID: " + id);

	    	try {
				favorite.createFavorites(id);
				message.obj = 1;// 1表示成功
			} catch (WeiboException e) {
				Toast.makeText(this, "收藏失败", Toast.LENGTH_SHORT).show();
			}
	    	break;
	    	
	    case TaskType.TS_DESTROY_FAVORITE:
	    	id = intent.getStringExtra("id");
	    	try {
				favorite.destroyFavorites(id);
				message.obj = 1;// 1表示成功
			} catch (WeiboException e) {
				Toast.makeText(this, "取消收藏失败", Toast.LENGTH_SHORT).show();
			}
	    	break;
	    	
	    case TaskType.TS_GET_STATUS_PIC_MID: //获取中型图片
	    	String url = intent.getStringExtra("url");
	    	MyLog.t("Pic URL:" + url);
			try {
				BitmapDrawable bd02=GetPicFromURL.getPic(new URL(url));
				message.obj=bd02;
			} catch (MalformedURLException e) {
				Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
			}
	    	
	    	break;

		default:
			// do nothing
		}
		
		MyLog.t("handler.sendMessage");

		handler.sendMessage(message);

	}

	// 添加Fragment到集合中
	public static void addFragment(WeiboFragment wa) {
		allFragments.add(wa);
	}

	public static void removeFragment(WeiboFragment wa) {
		allFragments.remove(wa);
	}

	public static WeiboFragment getFragmentByName(String aname) {
		for (WeiboFragment wa : allFragments) {
			if (wa.getClass().getName().indexOf(aname) >= 0) {
				return wa;
			}
		}
		return null;
	}
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			MyLog.t("handleMessage Entry: "+msg.what);
			
           switch(msg.what)
           {
          	   
           case TaskType.TS_GET_HUATI://话题
        	    MainService.getFragmentByName("HuatiActivity")
        	    .refresh(msg.what,msg.obj);
        	    break;
        	    
           case TaskType.TS_USER_LOGIN://登录结果
                MainService.getFragmentByName("LoginActivity")
                 .refresh(msg.what,msg.obj);
                break;
                
           case TaskType.TS_CREATE_FAVORITE:				//收藏
           case TaskType.TS_DESTROY_FAVORITE:				//取消收藏
           case TaskType.TS_GET_FRIENDS_HOMETIMELINE_MORE:	//获取更多微博
           case TaskType.TS_GET_FRIENDS_HOMETIMELINE:		//获取首页结果
            	MainService.getFragmentByName("HomeFragment").refresh(msg.what, msg.obj);
				//MyLog.t("getFragmentByName(HomeFragment)");
            	break;
            	
            case TaskType.TS_GET_USER_ICON://有新的头像下载成功了
            	MainService.getFragmentByName("HomeFragment").refresh(msg.what);
            	break;
            	
            case TaskType.TS_COMMENT_WEIBO://评论微博
            case TaskType.TS_FORWARD_WEIBO://转发微博
            	MainService.getFragmentByName("CommentFragment").refresh(msg.what, msg.obj);
            	break;
            	
            case TaskType.TS_GET_STATUS_PIC_MID://下载中型图片
            	WeiboFragment f = MainService.getFragmentByName("WeiboInfoFragment");
            	if(f != null)
            		f.refresh(msg.what, msg.obj);
              	//MyLog.t("MainService---Handler Pic Mid: " + msg.obj.toString());
            	break;
            	
            case TaskType.TS_GET_STATUS_PIC_ORI:
            	MainService.getFragmentByName("ShowStatusBitmap")
            	 .refresh(msg.what,msg.obj);
            	break;
            	
            case TaskType.TS_NEW_WEIBO:
            case TaskType.TS_NEW_WEIBO_PIC:
            case TaskType.TS_NEW_WEIBO_GPS:
            	MainService.getFragmentByName("NewWeiboFragment").refresh(msg.what,msg.obj);
            	break;
            	
           }
		}
	};
}
