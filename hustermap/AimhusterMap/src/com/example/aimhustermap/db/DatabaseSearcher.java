package com.example.aimhustermap.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.R.array;
import android.R.id;
import android.R.integer;
import android.R.interpolator;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class DatabaseSearcher {

	private SQLiteDatabase database;	
	//数据表名
	private String table = "hust";
	private String tablecursor = "cursor";
	private Activity activity;
	private boolean isInclude = false;
	
	//根据选择类型给rankdeep赋不同值
	private final int MODEGEO = 1;
	private final int MODECLICK = 2;
	private final int RESET = 0;//清零
	
	//数据库版本后
	private final int VERSION = 5;
	
	//把楼栋和办公室的名称放在一个数组中查询
	private	String dataColumns[] = {DatabaseHust.OFFICENAME , DatabaseHust.BUILDINGNAME , DatabaseHust.SORT};
	//把楼栋和办公室的索引放在一个数组中
	private	int dataIndex[] = {DatabaseHust.NAMEINDEX , DatabaseHust.BUILDDINGINDEX , DatabaseHust.SORTINDEX};

	/*
	 * 
	 * */
	public DatabaseSearcher(Context context){
		this.activity = (Activity)context;
		
		DatabaseHelper dbHelper = new DatabaseHelper(context, DatabaseHust.DB_NAME, null, VERSION);
		
		File file = new File(DatabaseHust.DB_PATH + "/" +DatabaseHust.DB_NAME);
//		File file = new File(DatabaseHust.DB_PATH);
//		System.out.println("new ---->");
		//如果不存在数据库，则从asset导入
		if(!file.exists()){
//			isInclude = includeDB();
			isInclude = false;
//			System.out.println("isInclude--->"+isInclude);
		}
		else {
			isInclude = true;
//			System.out.println("isInclude--->"+isInclude);
		}
		
	}
	
	
		
	/*
	 *根据点击地点返回该楼栋的所有信息 ,若无详情，即detail=0；返回null
	 * 
	 * */
	public List<DatabaseHust> search(GeoPoint point){
		List<DatabaseHust> datas = new ArrayList();
		
		//若导入成功,则进行查询
		if (isInclude) {
			//开启数据库
//			database = SQLiteDatabase.openDatabase(DatabaseHust.DB_PATH + "/" + DatabaseHust.DB_NAME, null, activity.MODE_WORLD_WRITEABLE);
			DatabaseHelper dbHelper = new DatabaseHelper(activity, DatabaseHust.DB_NAME, null, VERSION);
			database = dbHelper.getWritableDatabase();
			database.beginTransaction();
			
			//从传入的坐标获得经纬度
			int lon = point.getLongitudeE6();
			int lat = point.getLatitudeE6();
			
			String sql = "select * from "+table+" where Lon='"+lon+"'and Lat='"+lat+"' order by "+DatabaseHust.RANKDEEP+" DESC,"+DatabaseHust.RANK + " DESC";
			Cursor cursor = database.rawQuery(sql, null);
			//讲满足该坐标的信息循环取出
			for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
				DatabaseHust databaseHust = new DatabaseHust();
				
				//从数据库中获得该坐标点的详细信息
				databaseHust.buildingName = cursor.getString(DatabaseHust.BUILDDINGINDEX);
				databaseHust.officeNanme = cursor.getString(DatabaseHust.NAMEINDEX);
				databaseHust.officePhone = cursor.getString(DatabaseHust.PHONEINDEX);
				databaseHust.officeUrl = cursor.getString(DatabaseHust.URLINDEX);
				databaseHust.officeRoom = cursor.getString(DatabaseHust.ROOMINDEX);
				databaseHust.geoPoint = new GeoPoint(cursor.getInt(DatabaseHust.LATINDEX),cursor.getInt(DatabaseHust.LONINDEX));
				databaseHust.sort = cursor.getString(DatabaseHust.SORTINDEX);
				databaseHust.detail = cursor.getInt(DatabaseHust.DETAILINDEX);
				
				//将该实例添加到list中
				datas.add(databaseHust);
				
				//更新rank
				updateRank(cursor, database);
			}
			cursor.close();
			resetRankDeep(database);
			database.endTransaction();
			database.close();
		}	
		
//		if (datas.get(0).detail == 0) {
//			datas = null;
//		}
		return datas;		
	}

	
	
	/*
	 * 
	 * 根据搜索词按rank值返回相关的办公室或楼栋或类别列表,并更新rank
	 * 
	 * */
	public List<String> search(String set){
		
		List<String> results =new ArrayList<String>();
	
		if (isInclude) {
			
//			database = SQLiteDatabase.openDatabase(DatabaseHust.DB_PATH+ "/" + DatabaseHust.DB_NAME, null, activity.MODE_WORLD_WRITEABLE);
//			database = activity.openOrCreateDatabase(DatabaseHust.DATABASENAME, activity.MODE_PRIVATE, null);
			DatabaseHelper dbHelper = new DatabaseHelper(activity, DatabaseHust.DB_NAME, null, VERSION);
			database = dbHelper.getWritableDatabase();
			database.beginTransaction();
			
			//做一次循环，分别拿出满足搜索要求的楼栋和办公室名称
			for(int i=0;i<dataColumns.length;i++){
																			
					//获得查询语句的sql
					String sql = setSql(set, dataColumns[i]);
					Cursor cursor = database.rawQuery(sql, null);
					for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){					
						String data = cursor.getString(dataIndex[i]);
						results.add(data);
						//更新rank
						updateRank(cursor, database);
					}							
					cursor.close();
			}
			database.endTransaction();
			database.close();
		}
		
		results = removeDuplicateWithOrder(results);
		
		return results;				
	}

	
	/*
	 * 
	 * 根据搜索词返回cursor
	 * */
    public Cursor searchCursor(String set){
    	Cursor cursor = null;
    	String name = "name";
    	String sqlCreate = "create table if not exists "+tablecursor+" (cursor_id INTERGER PRIMARY KEY,"+name+")";
    	String sqlInsert;
    	String sqlDelete = "delete from "+tablecursor;
    	String sqlSelect = "select "+name+" from "+tablecursor;
    	List<String> sList = new ArrayList<String>();
    	sList = search(set);
    	if (isInclude) {
//    		database = SQLiteDatabase.openDatabase(DatabaseHust.DB_PATH+ "/" + DatabaseHust.DB_NAME, null, activity.MODE_WORLD_WRITEABLE);
    		DatabaseHelper dbHelper = new DatabaseHelper(activity, DatabaseHust.DB_NAME, null, VERSION);
			database = dbHelper.getWritableDatabase();
    		database.execSQL(sqlCreate);
    		database.execSQL(sqlDelete);
    		for(String s : sList){
    			sqlInsert = "insert into "+tablecursor+" ("+name+") values('"+s+"')";
    			database.execSQL(sqlInsert);
    		}
    		
    		cursor = database.rawQuery(sqlSelect, null);
    		cursor.moveToFirst();
    		database.close();
		}
    	
    	return cursor;
    }

    
	/*
	 * 根据搜索词返回满足要求的点的对象，并更新rank和rankDeep；若没有坐标点，则返回的坐标为（0,0）；
	 * 
	 * */
	public List<GeoPoint> searchGeo(String set){
//		boolean isButton = true;
		List<GeoPoint> points = new ArrayList<GeoPoint>();
		List<Integer> idList = new ArrayList<Integer>();
		
		if (isInclude) {
			
//			database = SQLiteDatabase.openDatabase(DatabaseHust.DB_PATH + "/" + DatabaseHust.DB_NAME, null, activity.MODE_WORLD_WRITEABLE);
			DatabaseHelper dbHelper = new DatabaseHelper(activity, DatabaseHust.DB_NAME, null, VERSION);
			database = dbHelper.getWritableDatabase();
            database.beginTransaction();
            System.out.println("dbfile---->"+database.getPath());
//			database = activity.openOrCreateDatabase(DatabaseHust.DATABASENAME, activity.MODE_PRIVATE, null);
			for (int i = 0; i < dataColumns.length; i++) {
				
				String sql = setSql(set, dataColumns[i]);
				Cursor cursor = database.rawQuery(sql, null);
				
				for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
//					GeoPoint point = new GeoPoint(0, 0);
//					point.setLatitudeE6(cursor.getInt(DatabaseHust.LATINDEX));
//					point.setLongitudeE6(cursor.getInt(DatabaseHust.LONINDEX));
//					DatabaseHust data = new DatabaseHust();
					int id = cursor.getInt(DatabaseHust.IDINDEX);
					if(!idExist(id, idList)){
						GeoPoint point ;
//						data.id = cursor.getInt(DatabaseHust.IDINDEX);
//						data.buildingName = cursor.getString(DatabaseHust.BUILDDINGINDEX);
//						data.officeNanme = cursor.getString(DatabaseHust.NAMEINDEX);
//						data.officePhone = cursor.getString(DatabaseHust.PHONEINDEX);
//						data.officeRoom = cursor.getString(DatabaseHust.ROOMINDEX);
//						data.officeUrl = cursor.getString(DatabaseHust.URLINDEX);
//						data.sort = cursor.getString(DatabaseHust.SORTINDEX);
//						data.detail = cursor.getInt(DatabaseHust.DETAILINDEX);
						point = new GeoPoint(cursor.getInt(DatabaseHust.LATINDEX), cursor.getInt(DatabaseHust.LONINDEX));
						
//						
//						datas.add(data);
						points.add(point);
						idList.add(id);
						//更新rank
						updateRank(cursor, database);
						updateRankDeep(id, MODEGEO, database);
					}
					
				}
				cursor.close();
			}
			database.endTransaction();
			database.close();
		}
		
		points = removeDuplicateWithOrder(points);
		
		return points;		
	}

	
	/*
	 * 传入准确的名称，返回相应的对象
	 * 
	 * */
	public DatabaseHust clickSearch(String name){
		
		GeoPoint point = null;
		DatabaseHust databaseHust = new DatabaseHust();
		String sql = "select * from " + table + " where " + DatabaseHust.BUILDINGNAME + " = '" + name + 
				     "' or " + DatabaseHust.OFFICENAME + " = '" + name + "' or " + DatabaseHust.SORT + " = '" + name+"'";
		if(isInclude){
//			database = SQLiteDatabase.openDatabase(DatabaseHust.DB_PATH + "/" + DatabaseHust.DB_NAME, null, activity.MODE_WORLD_WRITEABLE);
			DatabaseHelper dbHelper = new DatabaseHelper(activity, DatabaseHust.DB_NAME, null, VERSION);
			database = dbHelper.getWritableDatabase();
			
			Cursor cursor = database.rawQuery(sql, null);
			cursor.moveToFirst();
			
//			int lon = cursor.getInt(DatabaseHust.LONINDEX);
//			int lat = cursor.getInt(DatabaseHust.LATINDEX);
//			point = new GeoPoint(lat, lon);
			databaseHust.buildingName = cursor.getString(DatabaseHust.BUILDDINGINDEX);
			databaseHust.officeNanme = cursor.getString(DatabaseHust.NAMEINDEX);
			databaseHust.officePhone = cursor.getString(DatabaseHust.PHONEINDEX);
			databaseHust.officeUrl = cursor.getString(DatabaseHust.URLINDEX);
			databaseHust.officeRoom = cursor.getString(DatabaseHust.ROOMINDEX);
			databaseHust.geoPoint = new GeoPoint(cursor.getInt(DatabaseHust.LATINDEX),cursor.getInt(DatabaseHust.LONINDEX));
			databaseHust.sort = cursor.getString(DatabaseHust.SORTINDEX);
			databaseHust.detail = cursor.getInt(DatabaseHust.DETAILINDEX);
			
			int id = cursor.getInt(DatabaseHust.IDINDEX);
			updateRankDeep(id, MODECLICK, database);
			
			cursor.close();
		}
		
		return databaseHust;
	}
	
	
	/*
	 * 根据传入的类别返回该类别的建筑物的坐标点，并更新rank
	 * */
	public List<GeoPoint> sortGeo(String sort){
		List<GeoPoint> points = new ArrayList<GeoPoint>();
		
		
		if (isInclude) {
			
//			database = SQLiteDatabase.openDatabase(DatabaseHust.DB_PATH + "/" + DatabaseHust.DB_NAME, null, activity.MODE_WORLD_WRITEABLE);
//			database = activity.openOrCreateDatabase(DatabaseHust.DATABASENAME, activity.MODE_PRIVATE, null);
			DatabaseHelper dbHelper = new DatabaseHelper(activity, DatabaseHust.DB_NAME, null, VERSION);
			database = dbHelper.getWritableDatabase();
			database.beginTransaction();
			
			String sql = "select * from "+table+" where "+DatabaseHust.SORT+"='"+sort+"' order by " + DatabaseHust.RANK + " DESC";
			Cursor cursor = database.rawQuery(sql, null);
			for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
				//构造该点的坐标
				GeoPoint point = new GeoPoint(0,0);
				point.setLatitudeE6(cursor.getInt(DatabaseHust.LATINDEX));
				point.setLongitudeE6(cursor.getInt(DatabaseHust.LONINDEX));
				
				points.add(point);
				
				//更新rank
				updateRank(cursor, database);
				
			}
			cursor.close();
			database.endTransaction();
			database.close();
		}
		
		points = removeDuplicateWithOrder(points);
		
		return points;
		
	}
	
	
	/*
	 * 根据分类传入的类别返回所有信息
	 * 
	 * */
	public List<DatabaseHust> sortData(String sort){
		List<DatabaseHust> datas = new ArrayList<DatabaseHust>();
				
		if(isInclude){
//			database = SQLiteDatabase.openDatabase(DatabaseHust.DB_PATH + "/" + DatabaseHust.DB_NAME, null, activity.MODE_WORLD_WRITEABLE);
//			database = activity.openOrCreateDatabase(DatabaseHust.DATABASENAME, activity.MODE_PRIVATE, null);
			DatabaseHelper dbHelper = new DatabaseHelper(activity, DatabaseHust.DB_NAME, null, VERSION);
			database = dbHelper.getWritableDatabase();
			database.beginTransaction();
			String sql = "select * from "+table+" where "+DatabaseHust.SORT+"='"+sort+"' order by " + DatabaseHust.RANK + " DESC";
			Cursor cursor = database.rawQuery(sql, null);
			
			for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
				DatabaseHust data = new DatabaseHust();
				data.buildingName = cursor.getString(DatabaseHust.BUILDDINGINDEX);
				data.officeNanme = cursor.getString(DatabaseHust.NAMEINDEX);
				data.officePhone = cursor.getString(DatabaseHust.PHONEINDEX);
				data.officeRoom = cursor.getString(DatabaseHust.ROOMINDEX);
				data.officeUrl = cursor.getString(DatabaseHust.URLINDEX);
				data.geoPoint = new GeoPoint(cursor.getInt(DatabaseHust.LATINDEX), cursor.getInt(DatabaseHust.LONINDEX));
				data.sort = cursor.getString(DatabaseHust.SORTINDEX);
				data.detail = cursor.getInt(DatabaseHust.DETAILINDEX);
				
				datas.add(data);
			}
			
			cursor.close();
			database.endTransaction();
			database.close();			
		}
		
		return datas;
	}
	
	
	/*
	 * 
	 * 更新数据在数据库中的排序值
	 * */
	private void updateRank(Cursor cursor , SQLiteDatabase database){
		
		int rank ;		
		int id ;
		String sqlUpdate = new String();

		id = cursor.getInt(DatabaseHust.IDINDEX);
		rank = cursor.getInt(DatabaseHust.RANKINDEX);
		rank ++ ;
		sqlUpdate = "update " + table + " set " + DatabaseHust.RANK + " = " + rank + " where " +
                   DatabaseHust.ID + " = " + id ;
//		System.out.println("update------->"+sqlUpdate);
		database.execSQL(sqlUpdate);			
				
	}
		
	
	/*
	 * 更新数据库中的RankDeep值
	 * */
	private void updateRankDeep(int id,int mode,SQLiteDatabase database){
		
		String sqlUpdate = "update "+table+" set "+DatabaseHust.RANKDEEP+"="+mode+" where "+DatabaseHust.ID
				           + "=" + id;
		database.execSQL(sqlUpdate);
		
	}
	
	
	/*
	 * 把rankdeep清零
	 * */
	private void resetRankDeep(SQLiteDatabase database){
		String sqlUpdate = "update "+table+" set "+DatabaseHust.RANKDEEP+"="+RESET;
		database.execSQL(sqlUpdate);
	}
	
	/*
	 * List 去重
	 * */
	public static List removeDuplicateWithOrder(List list) {
		
        Set set = new HashSet();

        List newList = new ArrayList();

        for (Iterator iter = list.iterator(); iter.hasNext();) {

            Object element = iter.next();

            if (set.add(element))

                newList.add(element);		
        }

        return newList;

    }

	/*
	 * 根据搜索词构造数据库查询语句
	 * */
	private String setSql(String set,String dataColumn){
//		String sql = "select "+DatabaseHust.ID+","+dataColumn+" from "+table+
//				" where "+dataColumn+" like '";
		String sql = "select * from "+table+
				" where "+dataColumn+" like '";
		char[] values = set.toCharArray();
		
		for(char a:values){
			sql += "%"+a;
		}
//		sql+="%'";
		sql +="%' order by " + DatabaseHust.RANK + " DESC";
		
		return sql;
	}
	
	
	/*
	 * 判断id列表中是否已经有该id
	 * */
	private boolean idExist(int id,List<Integer> idList){
		boolean exist = false;
		for(int _id : idList){
			if (id == _id) {
				exist = true;
				break;
			}
		}
		return exist;
	}

	
  }	
