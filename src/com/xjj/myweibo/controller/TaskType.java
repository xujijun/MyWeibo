package com.xjj.myweibo.controller;

public class TaskType {
  public static final int TS_USER_LOGIN=1;//用户登录
  
  public static final int TS_GET_FRIENDS_HOMETIMELINE=2;//获取用户首页信息
  public static final int TS_GET_FRIENDS_HOMETIMELINE_MORE=21;//获取下一页信息
  
  public static final int TS_GET_USER_ICON=32;//获取用户头像
  
  public static final int TS_GET_STATUS_PIC=33;//下载微博小图片
  public static final int TS_GET_STATUS_PIC_MID=34;//下载微博中图片
  public static final int TS_GET_STATUS_PIC_ORI=35;//获取原始图片
  
  public static final int TS_GET_HUATI=36;//获取话题
  
  public static final int TS_NEW_WEIBO=4;//发表微博
  public static final int TS_NEW_WEIBO_PIC=41;//发表图片微博
  public static final int TS_NEW_WEIBO_GPS=42;//发表GPS微博
  public static final int TS_NEW_WEIBO_PIC_GPS=43;//发表图片和GPS微博
  
  public static final int TS_COMMENT_WEIBO=51;//评论微博
  public static final int TS_FORWARD_WEIBO=52;//转发微博
  
  public static final int TS_CREATE_FAVORITE=61;//收藏微博
  public static final int TS_DESTROY_FAVORITE=62;//取消收藏
  
}
