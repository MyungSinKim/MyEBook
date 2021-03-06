package ljt.myebook;
import java.util.ArrayList;
import java.util.HashMap;

import ljt.myebook.scansd.SDFileExplorer;
import ljt.myebook.sqlite.DatabaseHelper;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

public class DialogActivity extends ListActivity{
	private String item="item";	
	private final String isfirst="isfirst";
	private final String bgpic="bgpic";
	private final String table="configdb";
	private final String _id="_id";
	private SQLiteDatabase db1;
	private final String DBName="eebookdb";
	private LinearLayout layout;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogframexml);       
        setTitle("我的文件");
        layout=(LinearLayout)findViewById(R.id.listLinearLayout);
        getBackground();
        String [] menulist=new String[]{"自定义添加","返回"};  
        
       
        ArrayList<HashMap<String,String>> list=new 
        		ArrayList<HashMap<String, String>>();
        
        for(int i=0;i<2;i++){
        	HashMap<String, String> map=new HashMap<String, String>();
        	map.put(item, menulist[i]);
        	list.add(map);        	
        }
        MyDialogAdapter listAdapter = new MyDialogAdapter(this, list,
				R.layout.dialogitemlist, new String[] { item },
				new int[] { R.id.dialoglistTV});
		setListAdapter(listAdapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		switch(position){		
		case 0:{
			Intent intent1=new Intent(DialogActivity.this,SDFileExplorer.class);
			startActivity(intent1);
			finish();
			break;
		}
		case 1:{
			/*Intent intent=new Intent(DialogActivity.this,MainMenu.class);
			intent.putExtra("itemId", position+"");
			startActivity(intent);*/
			finish();
			break;
		}
		}
		super.onListItemClick(l, v, position, id);		
	}	
	
	public void getBackground(){
		DatabaseHelper dbHelper=new DatabaseHelper(this,DBName ,_id, isfirst, bgpic);
		db1=dbHelper.getWritableDatabase();
		Cursor c=db1.query(table, new String[]{_id,isfirst,bgpic}, null, null, null, null, null);
		while(c.moveToNext()){					
			String str1=c.getString(c.getColumnIndex(bgpic));
			if(str1!=null){
				int bg=Integer.parseInt(str1);
				System.out.println("bg==="+bg);
				layout.setBackgroundResource(bg);					
			}
		}
		c.close();
		db1.close();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		return super.onCreateOptionsMenu(menu);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		/*Intent intent=new Intent(DialogActivity.this,MainMenu.class);		
		startActivity(intent);*/
		finish();
		super.onBackPressed();
	}
	

}
