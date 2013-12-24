package com.xjj.myweibo.ui.adapter;

import java.util.List;

import weibo4j.model.Status;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xjj.myweibo.R;
import com.xjj.myweibo.controller.MainService;
import com.xjj.myweibo.controller.TaskType;
import com.xjj.myweibo.util.DateUtil;
import com.xjj.myweibo.util.MyLog;

public class WeiboAdapter extends BaseAdapter{
	
	public List<Status> allStatus;
	public Context context;

	public WeiboAdapter(List<Status> ls, Context con) {
		this.allStatus = ls;
		this.context = con;
	}

	@Override //返回当前数据适配器中记录的个数
	public int getCount() {
		return allStatus.size()+2;
	} 
	
    public void addNewData(List<Status> news)
    {
    	allStatus.addAll(news);
    	this.notifyDataSetChanged();
    }
    
	@Override //返回当前列表对应的条目信息
	public Object getItem(int position) {
		if(position>0 && position<this.getCount()-1)
		{
			return allStatus.get(position-1);
		}
		return null;
	}
   
    @Override//返回当前列表条目的编号
	public long getItemId(int position) {
		//返回当前条的微博编号
		if(position==0)
			 return 0;//刷新
		if(position==this.getCount()-1)//选中最后一条
			 return -1;//更多

		return allStatus.get(position-1).getIdstr();
	}

	
	private  ViewHolder vh=new ViewHolder();
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0){ // 刷新
			View weiboItem = LayoutInflater.from(context).inflate(R.layout.moreitemsview, null);
			TextView tv = (TextView) weiboItem.findViewById(R.id.tvitemtitle);
			tv.setText("刷新");
			//MyLog.t("geView-----------position=0");
			return weiboItem;
		} else if (position == this.getCount() - 1){// 更多
			View weiboitem = LayoutInflater.from(context).inflate(
					R.layout.moreitemsview, null);
			TextView tv = (TextView) weiboitem.findViewById(R.id.tvitemtitle);
			tv.setText("更多");
			//MyLog.t("geView-----------position=max, " + String.valueOf(this.getCount()-1));
			return weiboitem;
		} else {
			View weiboItem = null;

			if (convertView != null	&& convertView.findViewById(R.id.tvitemtitle) == null) {
				//Log.d("getview", "doGetView-------get TextView-----------" + position);
				weiboItem = convertView;
			} else {
				//Log.d("getview", "doGetView-------new TextView-----------" + position);
				// 把xml布局文件变成View对象
				weiboItem = LayoutInflater.from(context).inflate(R.layout.itemview, null);
			}
			//MyLog.t("geView----0-------position=" + position);

			vh.ivItemPortrait = (ImageView) weiboItem.findViewById(R.id.ivItemPortrait);//头像
			vh.tvItemName = (TextView) weiboItem.findViewById(R.id.tvItemName);//昵称
			vh.ivItemV = (ImageView) weiboItem.findViewById(R.id.ivItemV);//加V标志
			vh.tvItemDate = (TextView) weiboItem.findViewById(R.id.tvItemDate);//发表时间
			vh.ivItemPic = (ImageView) weiboItem.findViewById(R.id.ivItemPic);//是否有图片的标志
			vh.tvItemContent = (TextView) weiboItem.findViewById(R.id.tvItemContent);//微博正文
			vh.contentPic = (ImageView) weiboItem.findViewById(R.id.contentPic);//？？？//TODO
			vh.subLayout = weiboItem.findViewById(R.id.subLayout);//原微博
			vh.tvItemSubContent = (TextView) vh.subLayout.findViewById(R.id.tvItemSubContent);//原微博正文
			vh.subContentPic = (ImageView) vh.subLayout.findViewById(R.id.subContentPic);//元微博是否有图片 //TODO

			Status status = allStatus.get(position - 1);
			
			vh.tvItemName.setText(status.getUser().getName());
			
			String content = status.getText();
			
			//来源
			if(status.getSource() != null)
				content = content + "\n\n来自：" + status.getSource().getName();
			//转发数和评论数
			content = content + "\n转发(" + status.getRepostsCount()
					+ ") | 评论(" + status.getCommentsCount() + ")";
			//是否已收藏
			if(status.isFavorited())
				content += " | 已收藏";
			else
				content += " | 未收藏";
				
			vh.tvItemContent.setText(content);
			
			Status retweetedStatus = status.getRetweetedStatus();
			if (retweetedStatus != null) {
				vh.subLayout.setVisibility(View.VISIBLE);

				String txt = retweetedStatus.getText();

				if(retweetedStatus.getUser() != null)
					txt = "@" + retweetedStatus.getUser().getName() + "：" + txt;
				txt = txt + "\n\n发表时间：" + DateUtil.getCreateAt(retweetedStatus.getCreatedAt());

				if(retweetedStatus.getSource() != null)
					txt = txt + " 来自：" + retweetedStatus.getSource().getName();

				txt = txt + "\n转发(" + retweetedStatus.getRepostsCount()
						+ ") | 评论(" + retweetedStatus.getCommentsCount() + ")";
				
				vh.tvItemSubContent.setText(txt);
				
				// TextViewLink.addURLSpan(" "
				// +allStatus.get(position-1)
				// .getRetweeted_status().getText(), vh.tvItemSubContent);
				// vh.tvItemSubContent.setFocusable(false);
				//
			} else {
				vh.subLayout.setVisibility(View.GONE);
			}

			//发表时间
			vh.tvItemDate.setText(DateUtil.getCreateAt(status.getCreatedAt()));
			
			// 是否实名认证
			if (status.getUser().isVerified()) {
				//Log.d("ok", "ok isVerified");
				vh.ivItemV.setVisibility(View.VISIBLE);
			} else {
				vh.ivItemV.setVisibility(View.GONE);
			}
			
			// 判断有没有图片
			if (status.getThumbnailPic() != null) {
				if (!status.getThumbnailPic().equals("")) {
					vh.ivItemPic.setVisibility(View.VISIBLE);
				} else {
					vh.ivItemPic.setVisibility(View.GONE);
				}
			}else{
				vh.ivItemPic.setVisibility(View.GONE);
			}
			
			// 头像
			// 如果头像已经下载
			if (MainService.allicon.get(status.getUser().getId()) != null) {
				vh.ivItemPortrait.setImageDrawable(MainService.allicon.get(status.getUser().getId()));
				
			} else {// 设定缺省的图片
				vh.ivItemPortrait.setImageResource(R.drawable.portrait);
				// 获取头像
//				HashMap hm = new HashMap();
//				hm.put("us", allStatus.get(position - 1).getUser());
//				Task ts = new Task(TaskType.TS_GET_USER_ICON, hm);
//				MainService.newTask(ts);
				
				if (true) {//TODO 判断设置，是否要显示用户头像
					Intent it = new Intent(context, MainService.class);
					it.putExtra("Task", TaskType.TS_GET_USER_ICON);
					it.putExtra("User", allStatus.get(position - 1).getUser());
					context.startService(it);
				}
			}
			
			weiboItem.setTag(status);
			return weiboItem;
		}
	}

}
