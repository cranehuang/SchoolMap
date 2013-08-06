package com.example.aimhustermap.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.StaticLayout;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class DatabaseHust {
	
	

	//楼栋
	public String buildingName = null;
	
	//办公室位置
	public String officeRoom = null;
	
	//办公室名字
	public  String officeNanme = null;
	
	//办公室电话
	public  String officePhone = null;
	
	//办公室网址
	public  String officeUrl = null;	
	
	//Geopoint对应的经纬度*1E6 
	public GeoPoint geoPoint = null;
	
	//类别
	public String sort = null;
	
	//显示是否有detail
	public int detail = 1;
	
	
	//当前距离到该点的距离
	public double distance = 0;
	
	
	
	//各数据列在数据库表中的名称
	public static final String ID = "_id";
	public static final String BUILDINGNAME = "BuildingName";
	public static final String OFFICEROOM = "OfficeRoom";
	public static final String OFFICENAME = "OfficeNanme";
	public static final String OFFICEPHONE = "OfficePhone";
	public static final String OFFICEURL = "OfficeUrl";
	public static final String LON = "Lon";
	public static final String LAT = "Lat"; 
	public static final String SORT = "Sort";
	public static final String RANK = "Rank";
	public static final String DETAIL = "Detail";
	public static final String RANKDEEP = "RankDeep";
	
	//各数据列在数据库中的索引
	public static final int IDINDEX = 0;
	public static final int BUILDDINGINDEX = 1;
	public static final int ROOMINDEX = 2;
	public static final int NAMEINDEX = 3;
	public static final int PHONEINDEX = 4;
	public static final int URLINDEX = 5;
	public static final int LONINDEX = 6;
	public static final int LATINDEX = 7;
	public static final int SORTINDEX = 8;
	public static final int RANKINDEX = 9;
	public static final int DETAILINDEX = 10;
	public static final int RANKDEEPINDEX = 11;
	
	
	//cursor 表中的索引
	public static final int CURSORNAMEINDEX = 0;

	//数据库名称
	public static final String DATABASENAME = "hustmap";
	
	//数据库工作路径
	public static final String DB_PATH = "/data/data/com.example.aimhustermap/databases";
	//数据库名称
	public static final String DB_NAME = "hustmap.db";
	
	
	
	
	
	
	

}
