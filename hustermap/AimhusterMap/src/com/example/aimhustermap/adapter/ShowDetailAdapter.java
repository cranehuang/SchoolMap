package com.example.aimhustermap.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aimhustermap.R;
import com.example.aimhustermap.db.DatabaseHust;

public class ShowDetailAdapter extends BaseAdapter {  
    private List<DatabaseHust> databaseHusts;  
    Context context;

     
    public  ShowDetailAdapter(Context context,List<DatabaseHust> databaseHusts) 
    {
		
    	this.databaseHusts = databaseHusts;  
        this.context = context;  
	}
  
    @Override  
    public int getCount() {  
        return (databaseHusts==null)?0:databaseHusts.size();  
    }  
  
    @Override  
    public Object getItem(int position) {  
        return databaseHusts.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
      
      
    public class ViewHolder{  
        TextView officenameTV;  
        TextView phonenumTV;
        TextView officeRoomTV;
        Button docallBtn;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        final DatabaseHust databaseHust = (DatabaseHust)getItem(position);  
        ViewHolder viewHolder = null;  
        if(convertView==null){  
           // Log.d("MyBaseAdapter", "新建convertView,position="+position);  
            convertView = LayoutInflater.from(context).inflate(  
                    R.layout.detail_listview, null); 
              
            viewHolder = new ViewHolder();  
            viewHolder.officenameTV = (TextView)convertView.findViewById(  
                    R.id.officename1);  
            viewHolder.phonenumTV = (TextView)convertView.findViewById(  
                    R.id.phonenum1);  
            viewHolder.docallBtn = (Button)convertView.findViewById(  
                    R.id.docall1); 
            viewHolder.officeRoomTV=(TextView)convertView.findViewById(R.id.officeroom);
              
            LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(  
                    LinearLayout.LayoutParams.WRAP_CONTENT,  
                    LinearLayout.LayoutParams.WRAP_CONTENT);  
            mParams.gravity = Gravity.CENTER;  
            mParams.width=50;  
              
            convertView.setTag(viewHolder);  
        }else{  
            viewHolder = (ViewHolder)convertView.getTag();  
            Log.d("MyBaseAdapter", "旧的convertView,position="+position);  
        }  
          
        viewHolder.officenameTV.setText(databaseHust.officeNanme);  
        viewHolder.phonenumTV.setText(databaseHust.officePhone);  
        viewHolder.docallBtn.setBackgroundResource(R.drawable.docall);
        viewHolder.officeRoomTV.setText(databaseHust.officeRoom);
        //对ListView中docall按钮添加点击事件  
//        viewHolder.docallBtn.setOnClickListener(new OnClickListener(){  
//            @Override  
//            public void onClick(View v) { 
//            	
//            	Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"  
//                        + databaseHust.officePhone)); 
//            	context.startActivity(intent);
//            }  
//        });  
          
        //对ListView中的每一行信息配置OnClick事件  
//        convertView.setOnClickListener(new OnClickListener(){  
//    
//            @Override  
//            public void onClick(View v) {                          
//                    //  Toast.makeText(context, "一路向北", Toast.LENGTH_LONG).show();
//            }  
//              
//        });  
          
//        //对ListView中的每一行信息配置OnLongClick事件  
//        convertView.setOnLongClickListener(new OnLongClickListener(){  
//            @Override  
//            public boolean onLongClick(View v) {  
//              return false;
//            }  
//        });  
          
        return convertView;  
    }  
  
}  