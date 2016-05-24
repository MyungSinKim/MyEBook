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
	// ��¼��ǰ�ĸ��ļ���
	File currentParent;
	// ��¼��ǰ·���µ������ļ����ļ�����
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
		//��ȡϵͳ��SD����Ŀ¼
		File root = new File("/mnt/sdcard/");
		//��� SD������
		if (root.exists())
		{
			currentParent = root;
			currentFiles = root.listFiles();
			//ʹ�õ�ǰĿ¼�µ�ȫ���ļ����ļ��������ListView
			inflateListView(currentFiles);
		}
		// ΪListView���б���ĵ����¼��󶨼�����
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id)
			{
				// �û��������ļ���ֱ�ӷ��أ������κδ���
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
							Toast.makeText(SDFileExplorer.this, "�ļ��Ѿ��ɹ���ӵ��б�",
									Toast.LENGTH_SHORT).show();
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							db.close();
							
						}
					}else{
						Toast.makeText(SDFileExplorer.this, "�ļ���ʽ���ԣ���ѡ��.txt��ʽ���ļ���",
								20000).show();
						
					}
					return;
				}
					
				// ��ȡ�û�������ļ����µ������ļ�
				File[] tmp = currentFiles[position].listFiles();
				if (tmp == null || tmp.length == 0)
				{
					Toast.makeText(SDFileExplorer.this, "��ǰ·�����ɷ��ʻ��·����û���ļ�",
						Toast.LENGTH_LONG).show();
				}
				else
				{
					//��ȡ�û��������б����Ӧ���ļ��У���Ϊ��ǰ�ĸ��ļ���
					currentParent = currentFiles[position];
					//���浱ǰ�ĸ��ļ����ڵ�ȫ���ļ����ļ���
					currentFiles = tmp;
					// �ٴθ���ListView
					inflateListView(currentFiles);
				}
			}
		});
		// ��ȡ��һ��Ŀ¼�İ�ť
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
						// ��ȡ��һ��Ŀ¼
						currentParent = currentParent.getParentFile();
						// �г���ǰĿ¼�������ļ�
						currentFiles = currentParent.listFiles();
						// �ٴθ���ListView
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
		// ����һ��List���ϣ�List���ϵ�Ԫ����Map
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < files.length; i++)
		{
			Map<String, Object> listItem = new HashMap<String, Object>();
			//�����ǰFile���ļ��У�ʹ��folderͼ�ꣻ����ʹ��fileͼ��
			if (files[i].isDirectory())
			{
				listItem.put("icon", R.drawable.folder);
			}
			else
			{
				listItem.put("icon", R.drawable.file);
			}
			listItem.put("fileName", files[i].getName());
			//���List��
			listItems.add(listItem);
		}
		// ����һ��SimpleAdapter
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
			R.layout.line, new String[] { "icon", "fileName" }, new int[] {
				R.id.icon, R.id.file_name });
		// ΪListView����Adapter
		listView.setAdapter(simpleAdapter);
		listView.setSelector(R.drawable.item_selector);
		try
		{
			textView.setText("��ǰ·��Ϊ��" + currentParent.getCanonicalPath());
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
	