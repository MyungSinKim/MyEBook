package ljt.myebook.scansd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ljt.myebook.R;
import ljt.myebook.sqlite.DatabaseHelper;
import ljt.myebook.sqlite.DatabaseHelper1;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SDFileExplorer extends Activity
{
	ListView listView;
	TextView textView;
	// 记录当前的父文件夹
	File currentParent;
	// 记录当前路径下的所有文件的文件数组
	File[] currentFiles;
	
	private Button exitBT;
	private DatabaseHelper1 dbHelper=null;
	private SQLiteDatabase db=null;
	public final String _id="_id";
	public final String name="filelistDB";
	public final String filename="filename";
	public final String filepath="filepath";
	public final String table="filelisttable";
	
	private final String isfirst="isfirst";
	private final String bgpic="bgpic";
	private final String table1="configdb";
	
	private SQLiteDatabase db1;
	private final String DBName="eebookdb";
	private final int[] picbg=new int[]{R.drawable.bg1,R.drawable.bg2,
			R.drawable.bg3,R.drawable.bg4,R.drawable.bg5,R.drawable.bg6};
	private int bgid=R.drawable.bg1;
	
	private LinearLayout llayout1;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);
		listView = (ListView) findViewById(R.id.list);
		textView = (TextView) findViewById(R.id.path);
		exitBT=(Button)findViewById(R.id.exit_bt);
		llayout1=(LinearLayout)findViewById(R.id.fileexploerid);
		dbControl();
		//获取系统的SD卡的目录
		File root = new File("/mnt/sdcard/");
		//如果 SD卡存在
		if (root.exists())
		{
			currentParent = root;
			currentFiles = root.listFiles();
			//使用当前目录下的全部文件、文件夹来填充ListView
			inflateListView(currentFiles);
		}
		// 为ListView的列表项的单击事件绑定监听器
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id)
			{
				// 用户单击了文件，直接返回，不做任何处理
				if (currentFiles[position].isFile()){
					if(currentFiles[position].getName().indexOf(".txt")>0){
						String path=currentFiles[position].getAbsolutePath();
						String fname=currentFiles[position].getName();
						try{
							dbHelper=new DatabaseHelper1(SDFileExplorer.this, name, _id,filename, filepath);
							db=dbHelper.getWritableDatabase();
							ContentValues values1=new ContentValues();
							values1.put(filename, fname);
							values1.put(filepath, path);
							db.insert(table, null, values1);
							Toast.makeText(SDFileExplorer.this, "文件已经成功添加到列表！",
									Toast.LENGTH_SHORT).show();
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							db.close();
							
						}
					}else{
						Toast.makeText(SDFileExplorer.this, "文件格式不对，请选择.txt格式的文件！",
								20000).show();
						
					}
					return;
				}
					
				// 获取用户点击的文件夹下的所有文件
				File[] tmp = currentFiles[position].listFiles();
				if (tmp == null || tmp.length == 0)
				{
					Toast.makeText(SDFileExplorer.this, "当前路径不可访问或该路径下没有文件",
						Toast.LENGTH_LONG).show();
				}
				else
				{
					//获取用户单击的列表项对应的文件夹，设为当前的父文件夹
					currentParent = currentFiles[position];
					//保存当前的父文件夹内的全部文件和文件夹
					currentFiles = tmp;
					// 再次更新ListView
					inflateListView(currentFiles);
				}
			}
		});
		// 获取上一级目录的按钮
		Button parent = (Button) findViewById(R.id.parent);
		parent.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View source)
			{
				try
				{
					if (!currentParent.getCanonicalPath().equals("/mnt/sdcard"))
					{
						// 获取上一级目录
						currentParent = currentParent.getParentFile();
						// 列出当前目录下所有文件
						currentFiles = currentParent.listFiles();
						// 再次更新ListView
						inflateListView(currentFiles);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		exitBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void inflateListView(File[] files)
	{
		// 创建一个List集合，List集合的元素是Map
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < files.length; i++)
		{
			Map<String, Object> listItem = new HashMap<String, Object>();
			//如果当前File是文件夹，使用folder图标；否则使用file图标
			if (files[i].isDirectory())
			{
				listItem.put("icon", R.drawable.folder);
			}
			else
			{
				listItem.put("icon", R.drawable.file);
			}
			listItem.put("fileName", files[i].getName());
			//添加List项
			listItems.add(listItem);
		}
		// 创建一个SimpleAdapter
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
			R.layout.line, new String[] { "icon", "fileName" }, new int[] {
				R.id.icon, R.id.file_name });
		// 为ListView设置Adapter
		listView.setAdapter(simpleAdapter);
		listView.setSelector(R.drawable.item_selector);
		try
		{
			textView.setText("当前路径为：" + currentParent.getCanonicalPath());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		/*Intent intent=new Intent(SDFileExplorer.this,MainMenu.class);
		startActivity(intent);*/
		finish();
		super.onBackPressed();
	}
	
	public void dbControl(){
		Cursor c=null;
		try{
			DatabaseHelper dbHelper=new DatabaseHelper(this,DBName ,_id, isfirst, bgpic);
			db1=dbHelper.getWritableDatabase();
			c=db1.query(table1, new String[]{_id,isfirst,bgpic}, null, null, null, null, null);
			System.out.println("getCount==="+c.getCount());
			while(c.moveToNext()){				
				String str1=c.getString(c.getColumnIndex(bgpic));
				if(str1!=null){
					bgid=Integer.parseInt(str1);
					System.out.println("bg==="+bgid);
					llayout1.setBackgroundResource(bgid);
					
										
				}
			}	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			c.close();
			db1.close();
		}
	}
}
	