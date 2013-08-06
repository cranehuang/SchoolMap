package com.example.aimhustermap;


import java.util.ArrayList;
import java.util.List;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.aimhustermap.adapter.ShowDetailAdapter;
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
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class ShowDetail_Activity extends Activity{
	
	public String phonenum;
	public int _id;
    public int image_id;
    public String name;
    public String mobilePhone;
    public String officePhone;
    public String familyPhone;
    public String position;
    public String company;
    public String address;
    public String zipCode;
    public String email;
    public String otherContact;
    public String remark;
	
	ShowDetailAdapter adapter=null;
	ListView listView;
	List<DatabaseHust>  databaseHusts=new ArrayList<DatabaseHust>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.showdetail_activity);
		final TextView  textView=(TextView)findViewById(R.id.building_name);
		Intent intent=getIntent();
		Bundle dataBundle=intent.getExtras();
		GeoPoint p=new GeoPoint(dataBundle.getInt("y"), dataBundle.getInt("x"));
		
		
		DatabaseSearcher mySearcher=new DatabaseSearcher(this);
		
		databaseHusts=mySearcher.search(p);
		textView.setText(databaseHusts.get(0).buildingName);
		adapter=new ShowDetailAdapter(this, databaseHusts);
		listView=(ListView)findViewById(R.id.detail_listview);
		listView.setAdapter(adapter);
		   listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override  
		        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
		                long arg3) {  
		              
					/**
			    	 * 创建自定义overlay
			    	 * 
			    	 */
							

		        }  
			});
		   listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			   @Override
	            public boolean onItemLongClick(AdapterView<?> parent, View view,  
	                    int position, long id) {
				   TextView office_textView=(TextView)view.findViewById(R.id.officename1);
					name=office_textView.getText().toString();
					TextView phone_textView=(TextView)view.findViewById(R.id.phonenum1);
					officePhone=phone_textView.getText().toString();
					address=textView.getText().toString();
					
					showpopup_ask(ShowDetail_Activity.this,view);
					return true;
			   }
		});
		   
		   
		   Button backbtn=(Button)findViewById(R.id.backBtn1);
		   backbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ShowDetail_Activity.this.finish();
			}
		});

		
	}
	
	/*ListView中的按钮点击事件，直接绑定到标签
	 * 
	 * */
	
	public void OnDetailItemBtnClick(View v)
	{
		RelativeLayout layout=(RelativeLayout)v.getParent();
		String phoneNum=((TextView)layout.findViewById(R.id.phonenum1)).getText().toString();
		
	
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"  
		              + phoneNum)); 
		     	ShowDetail_Activity.this.startActivity(intent);		     	
		
     	
	}
	
	 private void showpopup_ask(Context context,View anchor)
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
//	            final Button btnCancel=(Button) vPopupWindow.findViewById(R.id.no);
	  	// 获取屏幕和对话框各自高宽
	      int screenWidth, screenHeight, dialgoWidth, dialgoheight;
//	      screenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
//	      screenHeight = context.getWindowManager().getDefaultDisplay().getHeight();
	     final PopupWindow   pw = new PopupWindow(vPopupWindow, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, true);// 声明一个弹出框 ，最后一个参数和setFocusable对应
	      pw.setContentView(vPopupWindow);   // 为弹出框设定自定义的布局 
	    //pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corners_pop));//设置整个popupwindow的样式。
	      pw.setAnimationStyle(R.style.PopupAnimation);
	      pw.setOutsideTouchable(true);
	      pw.setBackgroundDrawable(new BitmapDrawable());      
	      dialgoWidth = pw.getWidth();
	      dialgoheight = pw.getHeight();	       
//	      pw.showAsDropDown(anchor, 0, 0);
				pw.showAtLocation(anchor, Gravity.CENTER, 0, 0);
	       addContacts.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pw.dismiss();
//					Intent intent = new Intent();   
//		    		 
//		    		 intent.setAction(Intent.ACTION_VIEW);   
//		    		  
//		    		 intent.setData(Contacts.People.CONTENT_URI); 
//		    		 startActivity(intent);
					
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
					
					copyText(ShowDetail_Activity.this, officePhone);
					pw.dismiss();
					Toast.makeText(ShowDetail_Activity.this, "亲，电话号码已复制哦！", Toast.LENGTH_SHORT).show();
					
				}
			});
	       vPopupWindow.setFocusableInTouchMode(true);
			 vPopupWindow.setOnKeyListener(new OnKeyListener() {
					  public boolean onKey(View v, int keyCode, KeyEvent event)
					    {
					        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK)
					            pw.dismiss();
					 
					        return false;
					    }
				});
	  
	  }
	
	  public  void copyText(Context context, String text) {
		    ClipboardManager cm = (ClipboardManager) context
		            .getSystemService(Context.CLIPBOARD_SERVICE);
		    cm.setText(text);
		}
	  

}
