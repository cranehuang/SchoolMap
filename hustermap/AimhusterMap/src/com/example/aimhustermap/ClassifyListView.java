package com.example.aimhustermap;


import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.aimhustermap.adapter.ClassifyLvAdapter;
import com.example.aimhustermap.db.DatabaseHust;
import com.example.aimhustermap.db.DatabaseSearcher;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ClassifyListView extends Activity {
	
	private String name;
	private String officePhone;
	private String address;
	LinearLayout hintLayout;
	List<DatabaseHust> databaseHusts;
	DatabaseSearcher mySearcher=new DatabaseSearcher(this);
	 ClassifyLvAdapter adapter=null;
	 ListView list=null;
    ArrayList<OverlayItem>  items=new ArrayList<OverlayItem>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.classifylistview);
		
		TextView textView=(TextView)findViewById(R.id.show_placename);
		hintLayout=(LinearLayout)findViewById(R.id.hint_layout);
		Button backButton=(Button)findViewById(R.id.back);
		 Intent intent=getIntent();
		Bundle dataBundle=intent.getExtras();
		String pString=(String)dataBundle.getSerializable("pString");
		System.out.println("-------------->"+pString);
		if(pString.equals("食堂"))
		{
			hintLayout.setVisibility(View.VISIBLE);
			System.out.println("----------->jinlaimeiaaa");
		}
		Button clearButton=(Button)findViewById(R.id.clear_btn);
		clearButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(hintLayout.isShown())
				{
					hintLayout.setVisibility(View.GONE);
				}
			}
		});
		databaseHusts=mySearcher.sortData(pString);
		textView.setText("分类"+"("+pString+")");
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 Intent intent=getIntent();
	        		Bundle dataBundle=new Bundle();
	        		dataBundle.putString("key",null);
	        		intent.putExtras(dataBundle);
	        		ClassifyListView.this.setResult(1,intent);
	        		ClassifyListView.this.finish();
			}
		});
		 list=(ListView)findViewById(R.id.listview);
		adapter=new ClassifyLvAdapter(ClassifyListView.this, databaseHusts);
		list.setAdapter(adapter);
	    list.setOnItemClickListener(new OnItemClickListener() {
	    	@Override  
	        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
	                long arg3) {
	    			    		
//	    		Intent intent=new Intent(ClassifyListView.this, HusterMain.class);
//	    		intent.putExtra("x", databaseHusts.get(arg2).geoPoint.getLongitudeE6());	    		
//	    		intent.putExtra("y",databaseHusts.get(arg2).geoPoint.getLatitudeE6());	
//	    		startActivity(intent);
	    	    // Toast.makeText(ClassifyListView.this, databaseHusts.get(arg2).officeNanme, Toast.LENGTH_SHORT).show();
	    		Intent intent=getIntent();
	    		Bundle dataBundle=new Bundle();
	    		dataBundle.putString("key", databaseHusts.get(arg2).officeNanme);
	    		intent.putExtras(dataBundle);
	    		ClassifyListView.this.setResult(0,intent);
	    		ClassifyListView.this.finish();
	    	    return; 
	    	}
		});
	    list.setOnItemLongClickListener(new OnItemLongClickListener() {
	    	 @Override
	            public boolean onItemLongClick(AdapterView<?> parent, View view,  
	                    int position, long id) {
           
	    		 TextView textView=(TextView)view.findViewById(R.id.phonenum);
	    		 officePhone=(String)textView.getText();
	    		 TextView textView2=(TextView)view.findViewById(R.id.address);
	    		 name=textView2.getText().toString();
	    		 
	    		 showPopup_ask(ClassifyListView.this, view);
                       return  true;

                   }
		});
	}
	  
	public  void copyText(Context context, String text) {
	    ClipboardManager cm = (ClipboardManager) context
	            .getSystemService(Context.CLIPBOARD_SERVICE);
	    cm.setText(text);
	}
	
	/*ListView中的按钮点击事件，直接绑定到标签
	 * 
	 * */
	
	public void OnItemBtnClick(View v)
	{
		RelativeLayout layout=(RelativeLayout)v.getParent();
		String phoneNum=((TextView)layout.findViewById(R.id.phonenum)).getText().toString();
		
	
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"  
		              + phoneNum)); 
		     	ClassifyListView.this.startActivity(intent);
		     	ClassifyListView.this.finish();
		
     	
	}
	
	
	  private void showPopup_ask(Context context,View anchor)
	    {        
	       
	// 【Ⅰ】 获取自定义popupWindow布局文件
	
	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
	final View vPopupWindow = inflater.inflate(R.layout.popup_contacts, null, false);
	// 【Ⅱ】 创建PopupWindow实例    
	        vPopupWindow.setBackgroundColor(Color.WHITE);
	     // /////////////////////////////////////////////////////
	     // 【Ⅳ】自定义布局中的事件响应
	    	// OK按钮及其处理事件
	        final Button addContacts = (Button) vPopupWindow.findViewById(R.id.add_contacts);	
  	        final Button copyButton=(Button)vPopupWindow.findViewById(R.id.copy_phonenum);
	    	// 获取屏幕和对话框各自高宽
	        int screenWidth, screenHeight, dialgoWidth, dialgoheight;
//	        screenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
//	        screenHeight = context.getWindowManager().getDefaultDisplay().getHeight();
	       final PopupWindow   pw = new PopupWindow(vPopupWindow, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, true);// 声明一个弹出框 ，最后一个参数和setFocusable对应
	        pw.setContentView(vPopupWindow);   // 为弹出框设定自定义的布局 
	      //pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corners_pop));//设置整个popupwindow的样式。
	        pw.setAnimationStyle(R.style.PopupAnimation);
	        pw.setOutsideTouchable(true);
	        pw.setBackgroundDrawable(new BitmapDrawable());      
	        dialgoWidth = pw.getWidth();
	        dialgoheight = pw.getHeight();	       
//	        pw.showAsDropDown(anchor, 0, 0);
				pw.showAtLocation(anchor, Gravity.CENTER, 0, 0);
	         addContacts.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pw.dismiss();
					Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
			           intent.setType(People.CONTENT_ITEM_TYPE);
			           intent.putExtra(Contacts.Intents.Insert.NAME, name);
			           intent.putExtra(Contacts.Intents.Insert.PHONE, officePhone);
			           intent.putExtra(Contacts.Intents.Insert.NOTES, address);
			           intent.putExtra(Contacts.Intents.Insert.PHONE_TYPE,Contacts.PhonesColumns.TYPE_MOBILE);
			          
			           startActivity(intent);
				
				}
			});
	         copyButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pw.dismiss();
					copyText(ClassifyListView.this, officePhone);
					Toast.makeText(ClassifyListView.this, "亲，电话号码已复制哦！", Toast.LENGTH_SHORT).show();
				}
			});

	    }

	  @Override
	    protected void onPause() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
	    	 */
		
	        super.onPause();
	    }
	    
	    @Override
	    protected void onResume() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
	    	 */
	      
	        super.onResume();
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
	    	 */
	      
	        super.onDestroy();
	    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_huster_main, menu);
		return true;
	}
	

}
