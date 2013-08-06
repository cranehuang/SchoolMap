package com.example.aimhustermap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;

public class Lanucher_Activity extends Activity{

	boolean isFirstIn = false;
    
	boolean isBack=false;
	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	// 延迟3秒
	private static final long SPLASH_DELAY_MILLIS = 2000;

	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	/**
	 * Handler:跳转到不同界面
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.lanucher_activity);	
		init();
	
	}

	
	private void init() {
		// 读取SharedPreferences中需要的数据
		// 使用SharedPreferences来记录程序的使用次数
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, MODE_PRIVATE);

		// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		isFirstIn = preferences.getBoolean("isFirstIn", true);

		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
		if (!isFirstIn) {
			// 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		}

	}

	private void goHome() {
		if(isBack)
		{
			Lanucher_Activity.this.finish();
		}
		else {
			Intent intent = new Intent(Lanucher_Activity.this,HusterMain.class);
			Lanucher_Activity.this.startActivity(intent);
			Lanucher_Activity.this.finish();
		}
		
	}

	private void goGuide() {
		if(isBack)
		{
			Lanucher_Activity.this.finish();
		}
		else {
			Intent intent = new Intent(Lanucher_Activity.this, GuideActivity.class);
			Lanucher_Activity.this.startActivity(intent);
			Lanucher_Activity.this.finish();
		}
		
	}
	
	   @Override 
	    public boolean onKeyDown(int keyCode, KeyEvent event) { 
	        if (keyCode == KeyEvent.KEYCODE_BACK /*&& event.getRepeatCount() == 0*/) { 
	           isBack=true;
	            Lanucher_Activity.this.finish(); 
	        } 
	        return false; 
	    }
	  
}
