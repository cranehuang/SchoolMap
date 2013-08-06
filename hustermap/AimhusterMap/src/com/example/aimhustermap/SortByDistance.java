package com.example.aimhustermap;

import java.util.Comparator;



public class SortByDistance implements Comparator<MyPoi>{
	
	public int compare(MyPoi poi1,MyPoi poi2)
	{
		
		if(poi1.distance!=poi2.distance)
			return (int)poi1.distance-(int)poi2.distance;
		else 
			return 0;
	}

}
