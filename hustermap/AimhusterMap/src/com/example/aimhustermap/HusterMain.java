package com.example.aimhustermap;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



import android.R.anim;
import android.R.integer;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TextItem;
import com.baidu.mapapi.map.TextOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.aimhustermap.adapter.ClassifyLvAdapter;
import com.example.aimhustermap.db.DatabaseHust;
import com.example.aimhustermap.db.DatabaseSearcher;

public class HusterMain extends Activity {
	
	boolean isLocked=true;
	String[] myPoies=new String[100];
	List<String> recievetextViewList=new ArrayList<String>();
	ArrayAdapter<String> adapter=null;
	PopupWindow pw;
	public ListView listView=null;
	public EditText editView=null;
	List<DatabaseHust> poiSearch=new ArrayList<DatabaseHust>();
	List<DatabaseHust> datailList=new ArrayList<DatabaseHust>();
	List<GeoPoint> placeList=new ArrayList<GeoPoint>();
	
	private MyOverlay lableOverlay=null;
	private PopupOverlay   pop  = null;
	private ArrayList<OverlayItem>  mItems =  new ArrayList<OverlayItem>(); 
	
	static double DEF_PI = 3.14159265359; // PI
	static double DEF_2PI= 6.28318530712; // 2*PI
	static double DEF_PI180= 0.01745329252; // PI/180.0
	static double DEF_R =6370693.5; // radius of earth
	
	boolean hasAdd=false;//是否添加了文字标注
	public TextOverlay mTextOverlay=null;
    private ArrayList<MyPoi>  myPois=new ArrayList<MyPoi>();
	public MyPoi myPoi1;
	public MyPoi myPoi2;
	public MyPoi myPoi3;
	public MyPoi myPoi4;
	public MyPoi myPoi5;
	// 定位相关
			LocationClient mLocClient;
			LocationData locData = null;
			EditText editSearch;
			ArrayAdapter<String> adapter1;
			ListView list1;
			public MyLocationListenner myListener = new MyLocationListenner();
			Button requestLocButton = null;
			boolean isRequest = false;//是否手动触发请求定位
			boolean isFirstLoc = true;//是否首次定位
			boolean locationFinish=false;
			boolean isLocationClientStop = false;
			//定位图层
			MyLocationOverlay myLocationOverlay = null;
            private Button searchButton=null;
            private Button classifyButton=null;
            private Button locButton=null;
            private Button settingButton=null;
            private Button showButton=null;
            private LinearLayout layout;
            private Handler handler  =new Handler()
            {
         	   @Override
         	   public void handleMessage(Message msg)
         	   {
         		   if(msg.what==0)
         		   {
         			   showButton.setBackgroundResource(R.drawable.left_arrow);
         		   }
         		   else if(msg.what==1)
         		   {
         			   showButton.setBackgroundResource(R.drawable.right_arrow);				
     			   }
         		   else if (msg.what==2) {
         			   Toast.makeText(HusterMain.this, "定位不成功或不在武汉市内", Toast.LENGTH_SHORT).show();
					
				}
         	   }
            };
            private Timer timer =new Timer();
            DatabaseSearcher mySearcher ;
	private MapView mMapView = null;
	private MyOverlay  mOverlay = null;
	MKOfflineMap mOffline = null;//离线地图相关
	Drawable[] drawable =new Drawable[10] ;//覆盖物的图标
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;
	/**
	 *  MKMapViewListener 用于处理地图事件回调
	 */
	MKMapViewListener mMapListener = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 /**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        ManagerApp app = (ManagerApp)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(ManagerApp.strKey,new ManagerApp.MyGeneralListener());
        }
        /**
          * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
          */
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        
		setContentView(R.layout.activity_huster_main);
		
		mMapView=(MapView)findViewById(R.id.bmapView);
		
		 /**
         * 获取地图控制器
         */
        mMapController = mMapView.getController();
        /**
         *  设置地图是否响应点击事件  .
         */
        mMapController.enableClick(true);
        
        mMapController.setOverlookingGesturesEnabled(false);
        /**
         * 设置地图缩放级别
         */
        mMapController.setZoom(15);
        
    	
//        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。  
//        t.setToNow(); // 取得系统时间。
//         year = t.year;  
//         month = t.month+1;
//         System.out.println("-----year="+String.valueOf(year)+" "+"month="+String.valueOf(month));
//         if(year>2013||month>7)
//	       {
//	    	   isLocked=false;
//	    	   System.out.println("----------->01  isLocked="+String.valueOf(isLocked));
//	       }
         
      
      //写在onCreate函数里  
        mOffline = new MKOfflineMap();  
        //offline 实始化方法用更改。  
        
        mOffline.init(mMapController, new MKOfflineMapListener() {  
            @Override  
            public void onGetOfflineMapState(int type, int state) {  
                switch (type) {  
                case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:  
                    {  
                        MKOLUpdateElement update = mOffline.getUpdateInfo(state);  
                        //mText.setText(String.format("%s : %d%%", update.cityName, update.ratio));  
                    }  
                    break;  
                case MKOfflineMap.TYPE_NEW_OFFLINE:  
                    Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));  
                    break;  
                case MKOfflineMap.TYPE_VER_UPDATE:  
                    Log.d("OfflineDemo", String.format("new offlinemap ver"));  
                    break;  
                }      
                  }  
        }  
        ); 
     
        includeMap();//将离线地图读入SD卡根目录
        int num = mOffline.scan();//导入离线地图     
        mySearcher = new DatabaseSearcher(HusterMain.this);
        
       
        
        myPoi1=new MyPoi("华中科技大学", 114.419896, 30.51344);
		myPoi2=new MyPoi("瑜伽山派出所", 114.443478, 30.519826);
		myPoi3=new MyPoi("湖北省博士后公寓", 114.433273, 30.521214);
		myPoi4=new MyPoi("二十三栋110寝室", 114.44059, 30.52149);
		myPoi5=new MyPoi("华中科技大学图书馆", 114.418491, 30.518469);
		
		myPois.add(myPoi1);
		myPois.add(myPoi2);
		myPois.add(myPoi3);
		myPois.add(myPoi4);
		myPois.add(myPoi5);
		mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_marka),mMapView);
		mLocClient = new LocationClient( this );
        mTextOverlay=new TextOverlay(mMapView);

       autoLocation();//初始化时自动定位，定位不成功，则设置中心点为华科南大门
       GeoPoint point1=new GeoPoint(30513441,114419896); 
       mMapController.setCenter(point1);

			for(int i=0;i<myPois.size();i++)
				
				
			{
				 mTextOverlay.addText(DrawText(myPois.get(i).p,myPois.get(i).getPoiName()));
			}
		    
        /*地图监听事件
         * */
        mMapListener = new MKMapViewListener() {
			@Override
			public void onMapMoveFinish() {
				/**
				 * 在此处理地图移动完成回调
				 * 缩放，平移等操作完成后，此回调被触发
				 */
				
				 double zoomLever=mMapView.getZoomLevel();
			     if(!hasAdd&&zoomLever>18)
			     {
			        mMapView.getOverlays().add(mTextOverlay);
			        mMapView.refresh();
			        hasAdd=true;
			     }
			     if(hasAdd&&zoomLever<=18)
			     {
			    	 mMapView.getOverlays().remove(mTextOverlay);
			    	 mMapView.refresh();
			    	 hasAdd=false;
			     }
			   
			     if(layout.isShown())
					{
						layout_dismiss();
					}
			}
			
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * 在此处理底图poi点击事件
				 * 显示底图poi名称并移动至该点
				 * 设置过： mMapController.enableClick(true); 时，此回调才能被触发
				 * 
				 */
				if(layout.isShown())
				{
					 layout_dismiss();
					
				}
			    GeoPoint p=null; 
				String title = "";
				if (mapPoiInfo != null){
					title = mapPoiInfo.strText;
					p=mapPoiInfo.geoPt;
					System.out.println("------------->ClickItem:"+"lat="+String.valueOf(p.getLatitudeE6())+"  "+"lon="+
				    		String.valueOf(p.getLongitudeE6()));
					mySearcher=new DatabaseSearcher(HusterMain.this);
		    		List<DatabaseHust>  databaseHusts=new ArrayList<DatabaseHust>();
		    		databaseHusts=mySearcher.search(p);
		    		System.out.println("------------------>dataSize:"+databaseHusts.size());
		    		if(!databaseHusts.isEmpty()){
		    			Intent intent=new Intent(HusterMain.this,ShowDetail_Activity.class);
		    			intent.putExtra("x", databaseHusts.get(0).geoPoint.getLongitudeE6());
		    			intent.putExtra("y", databaseHusts.get(0).geoPoint.getLatitudeE6());
		    			startActivity(intent);
		    			
		    		
		    		}
		    		else{
		    			Toast.makeText(HusterMain.this, "亲，抱歉无详情！", Toast.LENGTH_SHORT).show();
		    			
		    		}
					Toast.makeText(HusterMain.this,title,Toast.LENGTH_SHORT).show();
					mMapController.animateTo(p);
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				/**
				 *  当调用过 mMapView.getCurrentMap()后，此回调会被触发
				 *  可在此保存截图至存储设备
				 */
			}

			@Override
			public void onMapAnimationFinish() {
				/**
				 *  地图完成带动画的操作（如: animationTo()）后，此回调被触发
				 */
				if(!mItems.isEmpty())
				{
					OverlayItem item=mItems.get(0);
					item.setMarker(getResources().getDrawable(R.drawable.icon_markj));
					clearOverlay();
					mOverlay.addItem(item);
					mMapView.getOverlays().add(mOverlay);
					mMapView.refresh();
					mItems.clear();
				}
			}
		};
		mMapView.regMapViewListener(ManagerApp.getInstance().mBMapManager, mMapListener);
		Resources res = getResources();
		drawable[0]=res.getDrawable(R.drawable.icon_marka);
		drawable[1]=res.getDrawable(R.drawable.icon_markb);
		drawable[2]=res.getDrawable(R.drawable.icon_markc);
		drawable[3]=res.getDrawable(R.drawable.icon_markd);
		drawable[4]=res.getDrawable(R.drawable.icon_marke);
		drawable[5]=res.getDrawable(R.drawable.icon_markf);
		drawable[6]=res.getDrawable(R.drawable.icon_markg);
		drawable[7]=res.getDrawable(R.drawable.icon_markh);
		drawable[8]=res.getDrawable(R.drawable.icon_marki);
		drawable[9]=res.getDrawable(R.drawable.icon_markj);
		
        list1=(ListView)findViewById(R.id.listview1);
        editSearch = (EditText) findViewById(R.id.searchkey); 
	
		 adapter1=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myPoies);
		 list1.setAdapter(adapter1);
	     classifyButton=(Button)findViewById(R.id.fenlei);
		 locButton=(Button)findViewById(R.id.loc);
		 settingButton=(Button)findViewById(R.id.setting);
		 searchButton=(Button)findViewById(R.id.search);
		 showButton=(Button)findViewById(R.id.showbtn);
		 layout =(LinearLayout)findViewById(R.id.layout1);
		 searchButton.setEnabled(false);
		 searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				list1.setVisibility(View.GONE);
				closeKeyboard();
				if(layout.isShown())
				{
					 
					  layout_dismiss();
					
				}
				ArrayList<OverlayItem> items=new ArrayList<OverlayItem>();
			    clearOverlay();
				 String place=null;
				 place =editSearch.getText().toString();
				 boolean isMatch=false;
				 mySearcher = new DatabaseSearcher(HusterMain.this);
				 placeList=mySearcher.searchGeo(place);
				System.out.println("------------->placeList.Size"+placeList.size());
				 if(!placeList.isEmpty())
				 {
                   int i;
				   for( i=0;i<placeList.size();i++)
				    {
					 if(placeList.get(i).getLatitudeE6()!=0||placeList.get(i).getLongitudeE6()!=0)
					 {
					 OverlayItem item=new OverlayItem(placeList.get(i),"","");
					
					    if(items.isEmpty())
					      {
						     items.add(item);
					       }
					   else {
						  
						   boolean hasExist=false;
						    for(int j=0;j<items.size();j++)
						      {
							     if((item.getPoint().getLatitudeE6()!=items.get(j).getPoint().getLatitudeE6())||(item.getPoint().getLongitudeE6()!=items.get(j).getPoint().getLongitudeE6()))
							    {
								 continue; 
							    }
							     else 
							     {
							    	 hasExist=true;
							    	 break;
								 }
						      }
						   // System.out.println("------------------>内层循环次数："+String.valueOf(j));
						     if(!hasExist)
						    	 items.add(item);
					        }
					
					
					 }					
					 System.out.println("----------->RESULT"+String.valueOf(i)+":Lat="+String.valueOf(placeList.get(i).getLatitudeE6())+"   "+"Lon="+String.valueOf(placeList.get(i).getLongitudeE6()));
				    }
				   System.out.println("---------->Size:"+String.valueOf(items.size()));
				   
				 }				 
				 if(!items.isEmpty())
				 {
					 isMatch=true;
				 }
				
				if(!isMatch)
				{
				String iString="抱歉，没有您要的位置数据";
				Toast.makeText(HusterMain.this, iString, Toast.LENGTH_SHORT).show();
				
			     }
				else
				{
					for(int i=0;i<items.size();i++)
					{
						 System.out.println("----------->最终item"+String.valueOf(i)+":Lat="+String.valueOf(items.get(i).getPoint().getLatitudeE6())+"   "+"Lon="+String.valueOf(items.get(i).getPoint().getLongitudeE6()));
					}
					
					mySearcher = new DatabaseSearcher(HusterMain.this);
			   //  List<DatabaseHust>  daHusts=mySearcher.search(items.get(0).getPoint());
//			     List<DatabaseHust> ds = mySearcher.search(items.get(3).getPoint());
//			     if (daHusts.isEmpty()) {
//					System.out.println("wu ni mei");
//				}
//			     else {
//			    	 System.out.println("---------------->"+daHusts.get(0).buildingName);
////				     System.out.println("3333---------->"+ds.get(0).buildingName);
//				}
			      
					initOverlay(items);
			        items.clear();			        
		         }	
			}
		});
		 classifyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				list1.setVisibility(View.GONE);
				  showfenlei_PopupWindow(v);
				  classifyButton.setBackgroundResource(R.drawable.fenlei1);
						               
			}
		});
		 locButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				list1.setVisibility(View.GONE);
				classifyButton.setBackgroundResource(R.drawable.fenlei2);
				 double zoomLever=mMapView.getZoomLevel();
				 if(hasAdd&&zoomLever<=18.9)
			     {
			    	 mMapView.getOverlays().remove(mTextOverlay);
			    	 mMapView.refresh();
			    	 hasAdd=false;
			     }
				 if(isLocked)
				 {
					 Toast.makeText(HusterMain.this, "亲，你不在武汉市内吧！", Toast.LENGTH_SHORT).show();
					 
				 }
				 else{
					   isRequest=true;
						locationFinish=true;
					    mLocClient.requestLocation();
					    mLocClient.requestOfflineLocation();
					    Toast.makeText(HusterMain.this, "正在定位…", Toast.LENGTH_SHORT).show();
					    mMapController.setZoom(15);
				 }
				
			}
		});
		
       settingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			 classifyButton.setBackgroundResource(R.drawable.fenlei2);
			  Intent intent=new Intent(HusterMain.this,BaseMap.class);
			  startActivity(intent);
			}
		})	;	
       
       
    
       
       
		 showButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 
				if(!layout.isShown()){
				layout.setVisibility(View.VISIBLE);				
				Animation animation = AnimationUtils.loadAnimation(HusterMain.this, R.anim.translate_display);
				layout.startAnimation(animation);
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Message msg=new Message();
						msg.what=1;
						handler.sendMessage(msg);
					}
				}, 500);
				}
				else {
					layout_dismiss();
				}
			}
		});
		 list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override  
		        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
		                long arg3) {  
		              
					/**
			    	 * 创建自定义overlay
			    	 * 
			    	 */
					mySearcher = new DatabaseSearcher(HusterMain.this);
					
					 
					 closeKeyboard();
					 
					 ArrayList<OverlayItem> items=new ArrayList<OverlayItem>();
					
			         //mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_marka),mMapView);	
					System.out.println("----------->list1Size:"+list1.getCount());
					String place = list1.getItemAtPosition(arg2).toString();
					System.out.println("---------->1"+place);
			     DatabaseHust    p=mySearcher.clickSearch(place);
			      //  System.out.println("-------------->2"+"Lat="+String.valueOf(p.getLatitudeE6())+"   "+"Lon="+String.valueOf(p.getLongitudeE6()));
					if(p.geoPoint.getLatitudeE6()!=0||p.geoPoint.getLongitudeE6()!=0)
					{
						OverlayItem item=new OverlayItem(p.geoPoint,"",place);
		                  items.add(item);
					}
					if(items.isEmpty())
					{
						clearOverlay();
						ArrayList<DatabaseHust> databaseHusts=new ArrayList<DatabaseHust>();
					
						databaseHusts.add(p);
						if(databaseHusts.isEmpty())
						{
							System.out.println("------------------->卧槽啊啊啊啊啊啊啊");
						}
						showpopupListView(HusterMain.this,editSearch, databaseHusts);
						InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
						imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
						Toast.makeText(HusterMain.this, "抱歉，无该地的地址信息", Toast.LENGTH_SHORT).show();
					}
					else{
						System.out.println("------------->Size:"+items.size());
						int distance;
						if(locationFinish)
						{
							distance=(int)GetShortDistance(items.get(0).getPoint().getLongitudeE6()*1e-6, items.get(0).getPoint().getLatitudeE6()*1e-6, locData.longitude, locData.latitude);
							Toast.makeText(HusterMain.this,"与你所在位置相距"+ String.valueOf(distance)+"米", Toast.LENGTH_LONG).show();
						}
						
						initOverlay(items);	
						
					}
					editSearch.setText(list1.getItemAtPosition(arg2).toString());
					editSearch.selectAll();
					 list1.setVisibility(View.GONE);
		        }  
			});

		 
		 editSearch.addTextChangedListener(new TextWatcher(){

				@Override
				public void afterTextChanged(Editable s) {
					
					 String flagString=s.toString().trim();
					if(flagString==null||flagString.length()<=0)
					{
						searchButton.setEnabled(false);
						 adapter1=new ArrayAdapter<String>(HusterMain.this,android.R.layout.simple_list_item_1,new String[]{});	
			               list1.setAdapter(adapter1);
			               mMapView.getOverlays().remove(mOverlay);
			               mMapView.refresh();
			               list1.setVisibility(View.GONE);
			             
			    
					}
					else 
					{
						searchButton.setEnabled(true);
						
						 /**
						  * 更新提示数据
                           */
						mySearcher = new DatabaseSearcher(HusterMain.this);
						recievetextViewList=mySearcher.search(editSearch.getText().toString());
						myPoies=recievetextViewList.toArray(new String[recievetextViewList.size()]);
					   
		                if(myPoies.length>0)
		                {
		                	 adapter1=new ArrayAdapter<String>(HusterMain.this,android.R.layout.simple_list_item_1,myPoies);
				               list1.setAdapter(adapter1);
				               list1.setVisibility(View.VISIBLE);
				               if(myPoies.length>6)
				               {
				            	   LayoutParams params=new LayoutParams(LayoutParams.FILL_PARENT, 700);
				            	   list1.setLayoutParams(params);

				               }
				               else{
				            	  
				            	   LayoutParams params2=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				            	   list1.setLayoutParams(params2);
				               }
		                	   list1.bringToFront();
				              
				             
		                }
		                else {
							list1.setEmptyView(list1.getEmptyView());
                            list1.setVisibility(View.GONE);
						}
					}
					
				}

				@Override
				public void beforeTextChanged(CharSequence s, int arg1,
						int arg2, int arg3) {
				
					
				}
				@Override
				public void onTextChanged(CharSequence cs, int arg1, int arg2,
						int arg3) {
					
				}
	        });
		 editSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(layout.isShown())
				{
					 classifyButton.setBackgroundResource(R.drawable.fenlei2);
					   layout.setVisibility(View.GONE);
					   Animation animation=AnimationUtils.loadAnimation(HusterMain.this,R.anim.translate_dismiss);
						layout.startAnimation(animation);
					timer.schedule(new TimerTask() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Message msg=new Message();
							msg.what=0;
							handler.sendMessage(msg);
						}
					}, 500);
					
				}
			}
		});
      
     
//        btnDistance.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				
//				String pString=null;
//				if(locationFinish)
//				{
//					for (int i = 0; i < myPois.size(); i++) {
//						myPois.get(i).setDistance(GetShortDistance(myPois.get(i).getLon(), myPois.get(i).getLat(), locData.longitude, locData.latitude));
//					   pString=myPois.get(i).getPoiName();
//					   
//					}
//					Collections.sort(myPois, new SortByDistance());
//					pString=myPois.get(0).getPoiName();
//					Toast.makeText(HusterMain.this, pString, Toast.LENGTH_SHORT).show();
//					GeoPoint point=new GeoPoint((int)(myPois.get(0).getLat() * 1E6), (int)(myPois.get(0).getLon() * 1E6));
//					 mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_markb),mMapView);
//					 OverlayItem item=new OverlayItem(point,pString,"");
//					
//					
//						 mOverlay.addItem(item);
//						 mMapView.getOverlays().add(mOverlay);
//						 mMapView.refresh();
//						 mMapController.animateTo(point);
//					
//				}
//				else{
//					Toast.makeText(HusterMain.this, "请先定位", Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
	}   
	
	
	
	/*
	 * 响应触摸屏事件
	 * 
	 * */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(layout.isShown())
		{
			
			 layout_dismiss();
			return true;
		}
		else if(list1.isShown())
		{
			list1.setVisibility(View.GONE);
			return true;
		}
		else return false;
	}
	
	/*
	 * 
	 * 初始化时自动定位
	 * */
	public void autoLocation()
	{
	        locData=new LocationData();
	        mLocClient.registerLocationListener( myListener );
	        LocationClientOption option = new LocationClientOption();
	        option.setOpenGps(true);//打开gps
	        option.setCoorType("bd09ll");     //设置坐标类型
	        option.setScanSpan(5000);//设置定时定位的时间间隔。单位ms
	        option.disableCache(false);//是否启用缓存定位
	        mLocClient.setLocOption(option);
	        mLocClient.start();	        
	        myLocationOverlay=new MyLocationOverlay(mMapView);	        
		      //设置定位数据
			    myLocationOverlay.setData(locData);			   
			    myLocationOverlay.setMarker(getResources().getDrawable(R.drawable.icon_geo));
			    //添加定位图层
				mMapView.getOverlays().add(myLocationOverlay);
				myLocationOverlay.enableCompass();
				//修改定位数据后刷新图层生效
				mMapView.refresh();
				locationFinish=true;
	}
	
	 /**
     * 手动触发一次定位请求
     * 
     */
    public void requestLocClick(){
    	isRequest = true;
        mLocClient.requestLocation();
        Toast.makeText(HusterMain.this, "正在定位…", Toast.LENGTH_SHORT).show();
    }
	  /**
     * 清除所有Overlay
     * @param view
     */
    public void clearOverlay(){
    	if(mOverlay!=null)
    	{
    	mOverlay.removeAll();    	
    	mMapView.getOverlays().remove(mOverlay);
    	mMapView.refresh();
    	}
    	else return;
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
     	HusterMain.this.startActivity(intent);
	}

   /* 
    自定义覆盖物图层
    */
	
    public class MyOverlay extends ItemizedOverlay{

    	public MyOverlay(Drawable defaultMarker, MapView mapView) {
    		super(defaultMarker, mapView);
    	}
    	

    	@Override
    	public boolean onTap(int index){
    		OverlayItem item = getItem(index);
    		//Toast.makeText(HusterMain.this, "item: "+String.valueOf(index)+" "+"has been touched", Toast.LENGTH_SHORT).show();
    		System.out.println("------------->ClickItem:"+String.valueOf(index)+"lat="+String.valueOf(item.getPoint().getLatitudeE6())+"  "+"lon="+
    		String.valueOf(item.getPoint().getLongitudeE6()));
    		mySearcher=new DatabaseSearcher(HusterMain.this);
    		List<DatabaseHust>  databaseHusts=new ArrayList<DatabaseHust>();
    		databaseHusts=mySearcher.search(item.getPoint());
    		System.out.println("------------------>dataSize:"+databaseHusts.size());
    		if(!databaseHusts.isEmpty()){
//    		showDetailListView(HusterMain.this, editSearch, databaseHusts,databaseHusts.get(0).buildingName);
    			Intent intent=new Intent(HusterMain.this,ShowDetail_Activity.class);
    			intent.putExtra("x", databaseHusts.get(0).geoPoint.getLongitudeE6());
    			intent.putExtra("y", databaseHusts.get(0).geoPoint.getLatitudeE6());
    			startActivity(intent);
    			
    		return true;
    		}
    		else{
    			Toast.makeText(HusterMain.this, "亲，抱歉无详情！", Toast.LENGTH_SHORT).show();
    			return true;
    		}
    	}
    	
    	@Override
    	public boolean onTap(GeoPoint pt , MapView mMapView){
//    		if (pop != null){
//                pop.hidePop();
//                mMapView.refresh();
//    		}
    		return false;
    	}
    	
    }
	  
	  
	
	  /*关于弹出悬浮框的方法
	   * 
	   * */
	
	  
	
	  private void showfenlei_PopupWindow(View anchor)
	    {        
	       
	// 【Ⅰ】 获取自定义popupWindow布局文件
	
	LayoutInflater inflater = (LayoutInflater) HusterMain.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
	final View vPopupWindow = inflater.inflate(R.layout.popupwindow, null, false);
	// 【Ⅱ】 创建PopupWindow实例    
	        vPopupWindow.setBackgroundColor(Color.rgb(240, 255, 240));
	     // /////////////////////////////////////////////////////
	     // 【Ⅳ】自定义布局中的事件响应
	    	// OK按钮及其处理事件
	    	        final Button btnmess = (Button) vPopupWindow.findViewById(R.id.mess);	    	      
                    final Button btnhotel=(Button) vPopupWindow.findViewById(R.id.hotel);
                    final Button btnbank=(Button)vPopupWindow.findViewById(R.id.bank);
                    final Button btnmarket=(Button)vPopupWindow.findViewById(R.id.market);
                    final Button btnatm=(Button)vPopupWindow.findViewById(R.id.atm);
                    
                    ViewUtil myUtil1=new ViewUtil(btnmess);
                    ViewUtil myUtil2=new ViewUtil(btnhotel);
                    ViewUtil myUtil3=new ViewUtil(btnbank);
                    ViewUtil myUtil4=new ViewUtil(btnmarket);
                    ViewUtil myUtil5=new ViewUtil(btnatm);
                    int height=myUtil1.getHeight()+myUtil2.getHeight()+myUtil3.getHeight()+myUtil4.getHeight()+myUtil5.getHeight();
	    	  // 【Ⅲ】 显示popupWindow对话框
	    	// 获取屏幕和对话框各自高宽
	        int screenWidth, screenHeight, dialgoWidth, dialgoheight;
	        screenWidth = HusterMain.this.getWindowManager().getDefaultDisplay().getWidth();
	        screenHeight = HusterMain.this.getWindowManager().getDefaultDisplay().getHeight();
	       final PopupWindow   pw = new PopupWindow(vPopupWindow, screenWidth/3-5,height, false);// 声明一个弹出框 ，最后一个参数和setFocusable对应
	        pw.setContentView(vPopupWindow);   // 为弹出框设定自定义的布局 
	      //pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corners_pop));//设置整个popupwindow的样式。
	        pw.setAnimationStyle(R.style.PopupAnimation);
	        pw.setOutsideTouchable(true);
	        pw.setBackgroundDrawable(new BitmapDrawable());      
	        dialgoWidth = pw.getWidth();
	        dialgoheight = pw.getHeight();	       
	        pw.showAsDropDown(anchor, 0, 0);
				
	        btnmess.setOnClickListener(new OnClickListener()
	        {
	            @Override
	            public void onClick(View v)
	            {
	            	
	            	if(layout.isShown())
	            	{
	            		layout_dismiss();
	            	}
	                pw.dismiss();// 关闭
	                String pString=btnmess.getText().toString();
	                Bundle data=new Bundle();
	                data.putSerializable("pString", pString);	                
	                Intent intent=new Intent(HusterMain.this,ClassifyListView.class);
	                intent.putExtras(data);
	               startActivityForResult(intent, 0);
	            }
	        }); 
	
	    
	    }

	/*
	 *  接收另一个Activity的Intent 
	 * */
	  
   @Override	  
   public void onActivityResult(int requestCode,int resultCode,Intent intent)
{
	if(requestCode==0&&resultCode==0)
	{
		
		try {
			Bundle dataBundle=intent.getExtras();
			String officename=dataBundle.getString("key");
			mySearcher=new DatabaseSearcher(HusterMain.this);
			editSearch.setText(officename);
			editSearch.selectAll();
			list1.setVisibility(View.GONE);	
		    List<GeoPoint>  geoPoints=mySearcher.searchGeo(officename);
		    OverlayItem item=new OverlayItem(geoPoints.get(0), "", officename);
			 mItems.add(item);
		    mMapController.animateTo(geoPoints.get(0));
		  System.out.println("------------->item:"+" "+"lat="+String.valueOf(geoPoints.get(0).getLatitudeE6())+" "+"lon="+String.valueOf(geoPoints.get(0).getLongitudeE6()));
			Toast.makeText(HusterMain.this, officename, Toast.LENGTH_SHORT).show();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
	}
	
}
   
	/*
	 *  无地址信息时弹出的悬浮框
	 * 
	 * */
  public void showpopupListView(Context context,View anchor,List<DatabaseHust>  databaseHusts)
	  {
		  
		
	        ///////////////////////////////////////////////////////
			// 【Ⅰ】 获取自定义popupWindow布局文件
			
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
		final View vPopupWindow = inflater.inflate(R.layout.popuplistview, null, false);
	    ClassifyLvAdapter	adapter=new ClassifyLvAdapter(context, databaseHusts);
		listView = (ListView) vPopupWindow.findViewById(R.id.listView);
		listView.setAdapter(adapter);
		
			        vPopupWindow.setBackgroundColor(Color.WHITE);			     
			        int screenWidth, screenHeight, dialgoWidth, dialgoheight;
			        screenHeight = getWindowManager().getDefaultDisplay().getHeight();// 获取屏幕和对话框各自高宽
			        screenWidth =getWindowManager().getDefaultDisplay().getWidth();
			//pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corners_pop));//设置整个popupwindow的样式。
			     
			        pw = new PopupWindow(vPopupWindow,screenWidth,LayoutParams.WRAP_CONTENT, true); // 声明一个弹出框 ，最后一个参数和setFocusable对应
			        pw.setContentView(vPopupWindow);   // 为弹出框设定自定义的布局 
			        dialgoWidth = pw.getWidth();
			        dialgoheight = pw.getHeight();
			        pw.setOutsideTouchable(true);
			        pw.setBackgroundDrawable(new BitmapDrawable());
			// 【Ⅲ】 显示popupWindow对话框
			

		           pw.update();
		          pw.showAsDropDown(anchor);
		         // pw.showAtLocation(anchor, Gravity.TOP, 0, 0);
		  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override  
		        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
		                long arg3) {  
		              
					/**
			    	 * 创建自定义overlay
			    	 * 
			    	 */
					Toast.makeText(HusterMain.this, "抱歉，无该地的地址信息！！", Toast.LENGTH_SHORT).show();

		        }  
			});
	
		       
	  }
	
     /*
      * 收回菜单栏
      * 
      * */
   
  public void layout_dismiss()
  {
	  layout.setVisibility(View.GONE);
	  classifyButton.setBackgroundResource(R.drawable.fenlei2);
		Animation animation=AnimationUtils.loadAnimation(HusterMain.this,R.anim.translate_dismiss);
		layout.startAnimation(animation);
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg=new Message();
				msg.what=0;
				handler.sendMessage(msg);
			}
		}, 500);
  }
 

  
    /**
     * 定位SDK监听函数
     */
	   public class MyLocationListenner implements BDLocationListener {
	    	
	        @Override
	        public void onReceiveLocation(BDLocation location) {
	            if (location == null || isLocationClientStop)
	                return ;
	  
	            double left_edge=113.997122;
	            double right_edge=114.609982;
	            double top_edge=30.683752;
	            double bottom_edge=30.439953;

//	            double left_edge=114.313325;
//	            double right_edge=114.330573;
//	            double top_edge=30.553471;
//	            double bottom_edge=30.503201;
	            if(location.getLongitude()>=left_edge&&location.getLongitude()<=right_edge&&location.getLatitude()>=bottom_edge&&location.getLatitude()<=top_edge)
	            {
	            	isLocked=false;
	            	locData.latitude = location.getLatitude();
	 	            locData.longitude = location.getLongitude();
	 	            //如果不显示定位精度圈，将accuracy赋值为0即可
	 	            if(location.getRadius()>100)
	 	            {
	 	            	locData.accuracy=100;
	 	            }
	 	            else {
	 	            	locData.accuracy = location.getRadius();
	 				}
	 	     
	 	            locData.direction = location.getDerect();
	 	            //更新定位数据
	 	            myLocationOverlay.setData(locData);
	 	            //更新图层数据执行刷新后生效
	 	            mMapView.refresh();
	 	            //是手动触发请求或首次定位时，移动到定位点
	 	            if (isRequest || isFirstLoc){
	 	            	//移动地图到定位点
	 	                mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
	 	                isRequest = false;
	 	            }
	 	            //首次定位完成
	 	            isFirstLoc = false;
	            }
	            else {
	            	isLocked=true;
	            	timer.schedule(new TimerTask() {
	        			
	        			@Override
	        			public void run() {
	        				// TODO Auto-generated method stub
	        				Message msg=new Message();
	        				msg.what=2;
	        				handler.sendMessage(msg);
	        			}
	        		}, 0);
				}
	   
	        }
	        
	        public void onReceivePoi(BDLocation poiLocation) {
	            if (poiLocation == null){
	                return ;
	            }
	        }
	    }
 

	 @Override
	    protected void onPause() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
	    	 */
		   isLocationClientStop = true;
		   int  cityID=Integer.parseInt("218");
		   mOffline.pause(cityID);
	        mMapView.onPause();
	        super.onPause();
	    }
	    
	    @Override
	    protected void onResume() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
	    	 */

	    	isLocationClientStop=false;
	        mMapView.onResume();
	        super.onResume();
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
	    	 */
	    	//退出时销毁定位
	    	ManagerApp app=(ManagerApp)this.getApplication();
	    	
	        if (mLocClient != null)
	            mLocClient.stop();
	        isLocationClientStop = true;
	        if(mOffline!=null)
	        {
	        	 mOffline.destroy();
	        }	       
	        mMapView.destroy();
	        if (app.mBMapManager != null) {
				app.mBMapManager.destroy();
				app.mBMapManager = null;
			}
	        super.onDestroy();
	        System.exit(0);
	    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_huster_main, menu);
		return true;
	}
	
	
	/*初始化Overlay
	 * 
	 * */	
	public void initOverlay(ArrayList<OverlayItem> items)
	{
		int lonFlag;
	
		int start;
		int end;
		int flag=items.size()>10?10:items.size();
		if(flag!=0){
		for(int i=0;i<flag;i++)
		{
			items.get(i).setMarker(drawable[i]);
		}
		
		clearOverlay();
		mOverlay.addItem(items);
		mMapView.getOverlays().add(mOverlay);
		if(locData!=null)
		{
			int locLon=(int)(locData.longitude *  1e6);
			int temp1=findthelargestLon(items);
			int temp2=findthesmallestLon(items);
			int temp3=findthelargestLat(items);
			int temp4=findthesmallestLat(items);
			start=temp3-temp4;
			lonFlag=temp1-items.get(0).getPoint().getLongitudeE6();
			
			if(temp2>locLon)
			{
				
				if((items.get(0).getPoint().getLongitudeE6()-locLon)>lonFlag)
				{
					
					end=2*(items.get(0).getPoint().getLongitudeE6()-locLon);
					mMapController.zoomToSpan(start, end);
					System.out.println("---------->Start="+String.valueOf(start)+"  "+"end="+String.valueOf(end));
				}
				else {
					end=2*lonFlag;
					mMapController.zoomToSpan(start, end);
				}
			}
			else if(locLon>temp1)
			{
				if((locLon-items.get(0).getPoint().getLongitudeE6())>(items.get(0).getPoint().getLongitudeE6()-temp2))
				{ end=2*(locLon-items.get(0).getPoint().getLongitudeE6());
				mMapController.zoomToSpan(start, end);}
				else  {
					end=2*(items.get(0).getPoint().getLongitudeE6()-temp2);
					mMapController.zoomToSpan(start, end);
				}
				System.out.println("---------->Start="+String.valueOf(start)+"  "+"end="+String.valueOf(end));
			}
		}
		mMapView.refresh();
		mMapController.animateTo(items.get(0).getPoint());
		}
		else			
			return;		
	}
	
	/*找出搜到覆盖物的最大最小经纬度
	 * 
	 * */
	public int findthelargestLon(ArrayList<OverlayItem> items)
	{
		int  largestLon;
		largestLon=items.get(0).getPoint().getLongitudeE6();
		for(int i=0;i<items.size();i++)
		{
			if(items.get(i).getPoint().getLongitudeE6()>largestLon)
			{
				largestLon=items.get(i).getPoint().getLongitudeE6();
			}
		}
		return largestLon;
	}
	public int findthesmallestLon(ArrayList<OverlayItem> items)
	{
		int  smallestLon;
		smallestLon=items.get(0).getPoint().getLongitudeE6();
		for(int i=0;i<items.size();i++)
		{
			if(items.get(i).getPoint().getLongitudeE6()<smallestLon)
			{
				smallestLon=items.get(i).getPoint().getLongitudeE6();
			}
		}
		return smallestLon;
	}
	public int findthesmallestLat(ArrayList<OverlayItem> items)
	{
		int  smallestLat;
		smallestLat=items.get(0).getPoint().getLatitudeE6();
		for(int i=0;i<items.size();i++)
		{
			if(items.get(i).getPoint().getLatitudeE6()<smallestLat)
			{
				smallestLat=items.get(i).getPoint().getLatitudeE6();
			}
		}
		return smallestLat;
	}
	public int findthelargestLat(ArrayList<OverlayItem> items)
	{
		int  largestLat;
		largestLat=items.get(0).getPoint().getLatitudeE6();
		for(int i=0;i<items.size();i++)
		{
			if(items.get(i).getPoint().getLatitudeE6()>largestLat)
			{
				largestLat=items.get(i).getPoint().getLatitudeE6();
			}
		}
		return largestLat;
	}
	/*
	 *  在地图上标注地名
	 * 
	 * */	
	
	
 	public TextItem DrawText(GeoPoint p,String poiName)
	{
		TextItem item=new TextItem();
		item.pt=p;
		item.text=poiName;
		//设文字大小
    	item.fontSize = 30;
    	Symbol symbol = new Symbol();
    	Symbol.Color bgColor = symbol.new Color();
    	//设置文字背景色
    	bgColor.red = 255;
    	bgColor.blue = 255;
    	bgColor.green = 255;
    	bgColor.alpha = 0;
    	
    	Symbol.Color fontColor = symbol.new Color();
    	//设置文字着色
    	fontColor.alpha = 255;
    	fontColor.red = 0;
    	fontColor.green = 0;
    	fontColor.blue  = 0;
    	//设置对齐方式
    	item.align = TextItem.ALIGN_CENTER;
    	//设置文字颜色和背景颜色
    	item.fontColor = fontColor;
    	item.bgColor  = bgColor ; 
    	return item;
	}
	
	
	
	/*
	获取地图上两点之间距离的方法
	*/
	
	public double GetShortDistance(double lon1, double lat1, double lon2, double lat2)
		{
			double ew1, ns1, ew2, ns2;
			double dx, dy, dew;
			double distance;
			// 角度转换为弧度
			ew1 = lon1 * DEF_PI180;
			ns1 = lat1 * DEF_PI180;
			ew2 = lon2 * DEF_PI180;
			ns2 = lat2 * DEF_PI180;
			// 经度差
			dew = ew1 - ew2;
			// 若跨东经和西经180 度，进行调整
			if (dew > DEF_PI)
			dew = DEF_2PI - dew;
			else if (dew < -DEF_PI)
			dew = DEF_2PI + dew;
			dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
			dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
			// 勾股定理求斜边长
			distance = Math.sqrt(dx * dx + dy * dy);
			return distance;
		}
	
	/*
	 * 控制软键盘消失
	 * 
	 * */
	public  void closeKeyboard()
	{
		InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0); 
	}
	
	/*
	 * 弹出软键盘
	 * 
	 * */
	 private void openKeyboard() {

         Timer timer = new Timer();
         timer.schedule(new TimerTask() {
                 @Override
                 public void run() {
                         InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                         imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                 }
         }, 1);
 } 

	 
	 
	/* 从asset中将离线地图包放入SD卡根目录
	 * 
	 * */
	 
	 public  void includeMap(){
		  boolean b = false;
		  
		  String path = Environment.getExternalStorageDirectory().toString();
		  String mapPath = path+"/BaiduMapSdk/vmp/h";
		  String mapName = "Wu_Han_Shi_218.dat_svc";
			// 检查 SQLite 数据库文件是否存在
					if ((new File(mapPath+"/"+mapName)).exists() == false) {
						// 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
						
						// 如 database 目录不存在，新建该目录
						if (!(new File(path+"/BaiduMapSdk")).exists()) {
							b = (new File(path)).mkdir();
						}
						if (!(new File(path+"BaiduMapSdk/vmp")).exists()) {
							b = (new File(path+"BaiduMapSdk/vmp")).mkdir();
						}
						if (!(new File(mapPath).exists())) {
							b = (new File(mapPath)).mkdir();
						}

						
						try {
							// 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
							
							InputStream is = this.getBaseContext().getAssets().open(mapName);
							// 输出流
							OutputStream os = new FileOutputStream(mapPath + "/" + mapName);

							// 文件写入
							byte[] buffer = new byte[1024];
							int length;
							while ((length = is.read(buffer)) > 0) {
								os.write(buffer, 0, length);
							}

							// 关闭文件流
							os.flush();
							os.close();
							is.close();
							b = true;
						} catch (Exception e) {
							e.printStackTrace();
							b = false;
						}
					}
					
	  }

	 
	 
}
