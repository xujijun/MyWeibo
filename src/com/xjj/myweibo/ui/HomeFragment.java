package com.xjj.myweibo.ui;

import java.text.SimpleDateFormat;
import java.util.List;

import weibo4j.model.Status;
import weibo4j.model.User;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xjj.myweibo.R;
import com.xjj.myweibo.controller.MainService;
import com.xjj.myweibo.controller.TaskType;
import com.xjj.myweibo.ui.adapter.WeiboAdapter;
import com.xjj.myweibo.util.MyLog;

public class HomeFragment extends WeiboFragment {

	public ListView lv;
	public View progress;
	TextView tv;
	public int currentPage = 1;
	public int pagesize = 15;
	private int savedPosition = 0;

	private boolean isRefreshing = true;
	List<Status> alls;
	WeiboAdapter wa = null;

	@Override
	public void init() {
		Intent it = new Intent(getActivity(), MainService.class);
		it.putExtra("Task", TaskType.TS_GET_FRIENDS_HOMETIMELINE);

		getActivity().startService(it);
		MyLog.t("HomeFragment ------------- init()");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);

		//MyLog.t("HomeFragment ----------- onCreateVew() --- 1 ");

		progress = rootView.findViewById(R.id.progress);
		if (isRefreshing)
			progress.setVisibility(View.VISIBLE);
		else
			progress.setVisibility(View.GONE);

		View title = rootView.findViewById(R.id.freelook_title);

		//MyLog.t("HomeFragment ----------- onCreateVew() --- 2 ");

		// 发微博按钮
		Button btleft = (Button) title.findViewById(R.id.title_bt_left);
		btleft.setBackgroundResource(R.drawable.title_new);
		btleft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// New一个写微博activity：
				Intent it = new Intent(getActivity(), WriteWeiboActivity.class);
				it.putExtra("fragment", "NewWeiboFragment");
				startActivity(it);
			}

		});

		//MyLog.t("HomeFragment ----------- onCreateVew() --- 3 ");

		// 刷新按钮
		Button btright = (Button) title.findViewById(R.id.title_bt_right);
		btright.setBackgroundResource(R.drawable.title_reload);
		btright.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(getActivity(), MainService.class);
				it.putExtra("Task", TaskType.TS_GET_FRIENDS_HOMETIMELINE);
				getActivity().startService(it);
				progress.setVisibility(View.VISIBLE);
			}

		});

		//MyLog.t("HomeFragment ----------- onCreateVew() --- 4 ");

		tv = (TextView) title.findViewById(R.id.textView);
		// TODO
		// tv.setText(MainService.nowu.getName());
		tv.setText("欢迎你！");

		// ListView
		lv = (ListView) rootView.findViewById(R.id.freelook_listview);
		if(wa != null)
			lv.setAdapter(wa);
		else
			init();
			
		// 绑定上下文菜单
		registerForContextMenu(lv);
		// 加入对列表条目单击事件的侦听
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent it;

				if (id == 0)// 刷新
				{// 发送刷新任务
					it = new Intent(getActivity(), MainService.class);
					it.putExtra("Task", TaskType.TS_GET_FRIENDS_HOMETIMELINE);
					getActivity().startService(it);
					// progress.setVisibility(View.VISIBLE);
					TextView tv = (TextView) view
							.findViewById(R.id.tvitemtitle);
					tv.setText("刷新中，请稍候……");
					isRefreshing = true;
				} else if (id == -1)// 更多
				{// 发送获取下一页的任务
					currentPage++;

					it = new Intent(getActivity(), MainService.class);
					it.putExtra("Task",
							TaskType.TS_GET_FRIENDS_HOMETIMELINE_MORE);
					it.putExtra("currentPage", currentPage);
					it.putExtra("pageSize", pagesize);
					getActivity().startService(it);

					TextView tv = (TextView) view
							.findViewById(R.id.tvitemtitle);
					tv.setText("读取中，请稍候……");
					savedPosition = position;
					// progress.setVisibility(View.VISIBLE);

				} else {
					it = new Intent(getActivity(), WeiboInfoActivity.class);
					Status st = (Status) parent.getItemAtPosition(position);
					it.putExtra("status", st);
					startActivity(it);
				}
			}

		});

		//MyLog.t("HomeFragment -------------- onCreateVew() --- end");
		return rootView;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		// MyLog.t("-----onCreateContextMenu entry.");

		AdapterContextMenuInfo acm = (AdapterContextMenuInfo) menuInfo;
		if (acm.id == 0 || acm.id == -1) {
			return;
		}

		Status st = null;
		if (acm != null) {
			st = (Status) acm.targetView.getTag();
		}

		if (st == null) {
			return;
		}

		menu.setHeaderTitle("微博功能");
		menu.add(1, 1, 1, "复制");
		menu.add(1, 2, 2, "转发");
		menu.add(1, 3, 3, "评论");

		if (st.isFavorited())
			menu.add(1, 4, 4, "取消微博");
		else
			menu.add(1, 4, 4, "收藏微博");

		menu.add(1, 5, 5, "查看个人资料");

		Status re_st = st.getRetweetedStatus();
		if (re_st != null) {
			menu.add(1, 6, 6, "复制原微博");
		}

		// 判断该条微博是否包含GPS坐标
		// Status st=(Status)acm.targetView.getTag();
		// if(st.getLatitude()>0)
		// {
		// menu.add(1,5,5,"查看地图");
		// }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo acm = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Status st = null;
		if (acm != null) {
			st = (Status) acm.targetView.getTag();
		}

		if (st == null) {
			return super.onContextItemSelected(item);
		}

		savedPosition = acm.position;

		Intent it = null;
		Status re_st = null;

		switch (item.getItemId()) {
		case 1:// 复制
			String content = "@" + st.getUser().getName() + "：" + st.getText();

			ClipboardManager cm = (ClipboardManager) getActivity()
					.getSystemService(getActivity().CLIPBOARD_SERVICE);
			ClipData copiedText = ClipData.newPlainText("content", content);
			cm.setPrimaryClip(copiedText);

			Toast.makeText(getActivity(), "已复制：\n" + content,
					Toast.LENGTH_SHORT).show();
			break;

		case 2:// 转发
			it = new Intent(getActivity(), WriteWeiboActivity.class);
			it.putExtra("fragment", "CommentFragment");
			it.putExtra("id", st.getId());
			it.putExtra("Task", TaskType.TS_FORWARD_WEIBO);
			// it.putExtra("writeComment", Boolean.valueOf(false));

			// 有原微博的时候，把当前微博正文当成转发的内容
			re_st = st.getRetweetedStatus();
			if (re_st != null) {
				it.putExtra("currentContent", "//@" + st.getUser().getName()
						+ "：" + st.getText());
			} else {
				it.putExtra("currentContent", "");
			}

			startActivity(it);
			// Toast.makeText(getActivity(), "您要转发" + acm.id,
			// Toast.LENGTH_SHORT).show();
			break;

		case 3:// 评论
			it = new Intent(getActivity(), WriteWeiboActivity.class);
			it.putExtra("fragment", "CommentFragment");
			it.putExtra("id", st.getId());
			it.putExtra("Task", TaskType.TS_COMMENT_WEIBO);
			// it.putExtra("writeComment", Boolean.valueOf(true));
			it.putExtra("currentContent", "");

			startActivity(it);

			// Toast.makeText(getActivity(), "您要评论" + acm.id,
			// Toast.LENGTH_SHORT).show();
			break;

		case 4:// 收藏或取消收藏
			it = new Intent(getActivity(), MainService.class);
			it.putExtra("id", st.getId());
			if (st.isFavorited()) {
				it.putExtra("Task", TaskType.TS_DESTROY_FAVORITE); // 取消收藏
			} else {
				it.putExtra("Task", TaskType.TS_CREATE_FAVORITE); // 收藏
			}
			getActivity().startService(it);

			// Toast.makeText(getActivity(), "收藏" + acm.id,
			// Toast.LENGTH_SHORT).show();
			break;

		case 5: // 查看个人资料
			User u = st.getUser();

			if (u != null) {
				String ui = "用户ID：" + u.getId();

				String gender = u.getGender();
				if (gender.equals("m"))
					gender = "男";
				else if (gender.equals("f"))
					gender = "女";
				else
					gender = "未知";
				ui += "  性别：" + gender;

				ui += "\n昵称：" + u.getScreenName();
				if (u.isVerified())
					ui += " (大V)";
				ui += "\n友好显示名称：" + u.getName();

				ui += "\n地址：" + u.getLocation();
				ui += "\n粉丝数：" + u.getFollowersCount();
				ui += "\n关注数：" + u.getFriendsCount();
				ui += "\n微博数：" + u.getStatusesCount();

				ui += "\n\n个人描述：" + u.getDescription();

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				String dateString = formatter.format(u.getCreatedAt());
				ui += "\n创建时间：" + dateString;

				AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
				ab.setTitle("用户信息");
				ab.setMessage(ui);
				ab.setPositiveButton("很好！", null);
				// ab.setNegativeButton("取消",null);
				ab.create().show();
			}
			// Toast.makeText(getActivity(), "查看个人资料" + acm.id,
			// Toast.LENGTH_SHORT).show();
			break;

		case 6: // 复制原微博
			re_st = st.getRetweetedStatus();
			if (re_st != null) {
				content = "@" + re_st.getUser().getName() + "："
						+ re_st.getText();
				cm = (ClipboardManager) getActivity().getSystemService(
						getActivity().CLIPBOARD_SERVICE);
				copiedText = ClipData.newPlainText("content", content);
				cm.setPrimaryClip(copiedText);

				Toast.makeText(getActivity(), "已复制：\n" + content,
						Toast.LENGTH_LONG).show();
			}
			break;

		// case 5://查看地图
		// Intent it=new Intent(this,MapViewStatusPoint.class);
		// Status st=(Status)ami.targetView.getTag();
		// it.putExtra("lat", st.getLatitude());
		// it.putExtra("lon", st.getLongitude());
		// it.putExtra("uid", st.getUser().getId());
		// this.startActivity(it);
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void refresh(Object... param) {
		int type = (Integer) param[0];
		int result = 0;

		switch (type) {
		case TaskType.TS_GET_FRIENDS_HOMETIMELINE_MORE:// 新的数据
			// MyLog.t("HomeFragment: TS_GET_FRIENDS_HOMETIMELINE_MORE");

			wa = (WeiboAdapter) lv.getAdapter();
			List<Status> ls = (List<Status>) param[1];
			if (ls != null) {
				wa.addNewData(ls);
				//wa.notifyDataSetChanged();
				lv.setSelection(savedPosition - 1);
			}
			break;

		case TaskType.TS_GET_USER_ICON:
			// 刷新列表
			WeiboAdapter nowwa = (WeiboAdapter) lv.getAdapter();
			nowwa.notifyDataSetChanged();
			// progress.setVisibility(View.GONE);

			break;

		case TaskType.TS_GET_FRIENDS_HOMETIMELINE:// 更新首页微博
			progress.setVisibility(View.GONE);
			isRefreshing = false;
			lv.setVisibility(View.VISIBLE);
			alls = (List<Status>) param[1];
			if (alls != null) {
				// test
				// MyLog.t(alls.get(0).toString());

				// List<Map<String, Object>> items = new ArrayList<Map<String,
				// Object>>();

				wa = new WeiboAdapter(alls, getActivity());
				lv.setAdapter(wa);
			}
			break;

		case TaskType.TS_CREATE_FAVORITE:
			try {
				result = (Integer) param[1];
			} catch (Exception e) {
				Toast.makeText(getActivity(), "收藏时发生错误，你可能已经收藏过了",
						Toast.LENGTH_LONG).show();
			}

			if (result == 1) {
				WeiboAdapter waFavorite = (WeiboAdapter) lv.getAdapter();
				Status st = waFavorite.allStatus.get(savedPosition - 1);
				st.setFavorited(true);
				waFavorite.notifyDataSetChanged();
				Toast.makeText(getActivity(), "收藏成功", Toast.LENGTH_LONG).show();
			}
			break;

		case TaskType.TS_DESTROY_FAVORITE:
			try {
				result = (Integer) param[1];
			} catch (Exception e) {
				Toast.makeText(getActivity(), "读取取消收藏返回值错误", Toast.LENGTH_LONG)
						.show();
			}

			if (result == 1) {
				WeiboAdapter waFavorite = (WeiboAdapter) lv.getAdapter();
				Status st = waFavorite.allStatus.get(savedPosition - 1);
				st.setFavorited(false);
				waFavorite.notifyDataSetChanged();
				Toast.makeText(getActivity(), "取消收藏成功", Toast.LENGTH_LONG)
						.show();
			}
			break;
		}

	}

}
