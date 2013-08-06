package com.example.aimhustermap;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.aimhustermap.HusterMain.MyOverlay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class BaseMap extends Activity{
	/**
	 *  MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;
	/**
	 *  MKMapViewListener 用于处理地图事件回调
	 */
	MKMapViewListener mMapListener = null;
	
	 
	// 定位相关
		LocationClient mLocClient;
		LocationData locData = null;
		public MyLocationListenner myListener = new MyLocationListenner();
	    private boolean isLocked=true;
		//定位图层
		MyLocationOverlay myLocationOverlay = null;
		private MyOverlay  mOverlay = null;
		boolean isRequest = false;//是否手动触发请求定位
		boolean isLocationClientStop = false;		
		Button requestLocButton=null;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
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
	        
	        
	        setContentView(R.layout.basemap);
	        
	        mMapView=(MapView)findViewById(R.id.bmapView_1);
	        /**
	         * 获取地图控制器
	         */
	        mMapController = mMapView.getController();
	        /**
	         *  设置地图是否响应点击事件  .
	         */
	        mMapController.enableClick(true);
	        /**
	         * 设置地图缩放级别
	         */
	        mMapController.setZoom(15);
	        
	        Button backbtnButton = (Button)findViewById(R.id.back_btn);
	        requestLocButton=(Button)findViewById(R.id.loc_btn);
	        requestLocButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(isLocked)
					{
						Toast.makeText(BaseMap.this, "亲，你不在武汉市吧！", Toast.LENGTH_SHORT).show();
					}
					else {
						requestLocClick();
					}
					
				}
			});
	        backbtnButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					BaseMap.this.finish();
				}
			});
	        
	        GeoPoint p ;
	        double cLat =  30.51344 ;
	        double cLon = 114.419896 ;
	        Intent  intent = getIntent();
	        if ( intent.hasExtra("x") && intent.hasExtra("y") ){
	        	//当用intent参数时，设置中心点为指定点
	        	Bundle b = intent.getExtras();
	        	p = new GeoPoint(b.getInt("y"), b.getInt("x"));
	        	mOverlay=new MyOverlay(getResources().getDrawable(R.drawable.icon_marki), mMapView);
	        	OverlayItem item=new OverlayItem(p, "", "");
	        	clearOverlay();
	        	mOverlay.addItem(item);
	        	mMapView.getOverlays().add(mOverlay);
	        	mMapView.refresh();
	        }else{
	        	//设置中心点为华科
	        	 p = new GeoPoint((int)(cLat * 1E6), (int)(cLon * 1E6));
	        }
	        
	        mMapController.setCenter(p);
	        autoLocation();
	        
	      
	        /**
	    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
	    	 */
	        mMapListener = new MKMapViewListener() {
				@Override
				public void onMapMoveFinish() {
					/**
					 * 在此处理地图移动完成回调
					 * 缩放，平移等操作完成后，此回调被触发
					 */
				}
				
				@Override
				public void onClickMapPoi(MapPoi mapPoiInfo) {
					/**
					 * 在此处理底图poi点击事件
					 * 显示底图poi名称并移动至该点
					 * 设置过： mMapController.enableClick(true); 时，此回调才能被触发
					 * 
					 */
					String title = "";
					if (mapPoiInfo != null){
						title = mapPoiInfo.strText;
						Toast.makeText(BaseMap.this,title,Toast.LENGTH_SHORT).show();
						mMapController.animateTo(mapPoiInfo.geoPt);
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
				}
			};
			mMapView.regMapViewListener(ManagerApp.getInstance().mBMapManager, mMapListener);
	        
	 }
	 /*
		 * 
		 * 初始化时自动定位
		 * */
		public void autoLocation()
		{
			 mLocClient = new LocationClient( this );
		        locData=new LocationData();
		        mLocClient.registerLocationListener( myListener );
		        LocationClientOption option = new LocationClientOption();
		        option.setOpenGps(true);//打开gps
		        option.setCoorType("bd09ll");     //设置坐标类型
		       // option.setScanSpan(5000);
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
		}
		
		
		 /**
	     * 手动触发一次定位请求
	     */
	    public void requestLocClick(){
	    	isRequest = true;
	        mLocClient.requestLocation();
	        if(locData!=null)
	        {
	        	 mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
	        }	        
	        Toast.makeText(BaseMap.this, "正在定位…", Toast.LENGTH_SHORT).show();
	    }
	 
	 /**
	     * 定位SDK监听函数
	     */
   public class MyLocationListenner implements BDLocationListener {
	    	
	        @Override
	        public void onReceiveLocation(BDLocation location) {
	            if (location == null || isLocationClientStop)
	                return ;
	            int  locType;
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
	 	            System.out.println("--------------->accuracy=="+String.valueOf(locData.accuracy));
	 	            System.out.println("----------------->lat="+String.valueOf(locData.latitude)+"  "+"lon="+String.valueOf(locData.longitude));
	 	            locData.direction = location.getDerect();
	 	            //更新定位数据
	 	            myLocationOverlay.setData(locData);
	 	            //更新图层数据执行刷新后生效
	 	            mMapView.refresh();
	            }
	            else {
	            	isLocked=true;
	            	
				}
	            locType=location.getLocType();
	            System.out.println("------------->locType=="+String.valueOf(locType));
	           
	        }
	        
	        public void onReceivePoi(BDLocation poiLocation) {
	            if (poiLocation == null){
	                return ;
	            }
	        }
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
	    	
	    	
	    			
	    			return true;
	    		
	    	}
	    	
	    	@Override
	    	public boolean onTap(GeoPoint pt , MapView mMapView){
//	    		if (pop != null){
//	                pop.hidePop();
//	                mMapView.refresh();
//	    		}
	    		return false;
	    	}
	    	
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
	    
	  @Override
	    protected void onPause() {
	    	/**
	    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
	    	 */
		 
		  isLocationClientStop=true;
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
	    	isLocationClientStop=true;
	        mMapView.destroy();
	        super.onDestroy();
	    }
	    
	    @Override
	    protected void onSaveInstanceState(Bundle outState) {
	    	super.onSaveInstanceState(outState);
	    	mMapView.onSaveInstanceState(outState);
	    	
	    }
	    
	    @Override
	    protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    	super.onRestoreInstanceState(savedInstanceState);
	    	mMapView.onRestoreInstanceState(savedInstanceState);
	    }

}
