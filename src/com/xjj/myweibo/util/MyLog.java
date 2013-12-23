package com.xjj.myweibo.util;

import android.util.Log;

public class MyLog {
	
	private final static boolean showLog = true;
	
	public static void t(String msg){
		if(showLog)
			Log.i("MyTest", msg);
	}
}
