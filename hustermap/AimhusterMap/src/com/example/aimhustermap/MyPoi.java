package com.example.aimhustermap;


import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MyPoi {
	
	  public double poiLat; 	  
	  public double poiLon;
	  public String poiName;
	  public double distance;
//	  public GeoPoint p=new GeoPoint((int)(poiLat* 1E6), (int)(poiLon * 1E6));
	  public GeoPoint p;
	  
	    public  MyPoi(String PoiName,double PoiLon,double PoiLat) {
	    	
			// TODO Auto-generated constructor stub
	    	super();
	    	this.poiName=PoiName;
	    	this.poiLon=PoiLon;
	    	this.poiLat=PoiLat;
	    	p=new GeoPoint((int)(poiLat* 1E6), (int)(poiLon * 1E6));
		}
	    public String getPoiName() {  
	        return poiName;  
	    }  
	   public double getLon() {
		   return poiLon;
		
	}
	   public double getLat()
	   {
		   return poiLat;
	   }
	   public double getDistance()
	   {
		   return distance;
	   }
	   public void setDistance(double distance)
	   {
		   this.distance=distance;
	   }
	   
	   public MyPoi(String poiName,double distance,double poiLon,double poiLat)
	   {
		   this.poiName=poiName;
		   this.distance=distance;
		   this.poiLon=poiLon;
		   this.poiLat=poiLat;
	   }

}
