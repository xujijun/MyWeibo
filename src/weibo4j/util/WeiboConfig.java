package weibo4j.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.xjj.myweibo.util.Constants;

public class WeiboConfig {
	public WeiboConfig(){}
	public static Properties props = new Properties(); 
	
	public static String getValue(String key){
		//Changed by XJJ
		//return props.getProperty(key);
		return Constants.BaseURL;
	}

    public static void updateProperties(String key,String value) {    
            props.setProperty(key, value); 
    } 
}
