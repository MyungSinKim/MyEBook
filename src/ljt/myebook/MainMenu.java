package ljt.myebook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import ljt.myebook.scansd.ScanSD;
import ljt.myebook.sqlite.DatabaseHelper;
import ljt.myebook.sqlite.DatabaseHelper1;
import ljt.myebook.sqlite.DbHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends Activity {

	public final static int OPENMARK = 0;
	ScanSD scanSD;
	Intent intent;
	Context mContext;
	RadioButton nextPage;
	RadioButton bmRadioButton;
	RadioButton myTF;
	ListView ListView;
	TextView textView1;
	TextView textView2;
	ImageView storm;
	private DbHelper db;
	private int pos;
	final String[] items =  { "书签1 未使用", "书签2 未使用", "书签3 未使用", "书签4 未使用","书签5 未使用","书签6未使用","书签7 未使用",
			"书签8 未使用","书签9 未使用","书签10未使用", "自动书签 未使用" };
	Cursor mCursor, c,c1;
	private final String isfirst="isfirst";
	private final String bgpic="bgpic";
	private final String table="configdb";
	private final String _id="_id";
	private SQLiteDatabase db1;
	private final String DBName="eebookdb";
	private final int[] picbg=new int[]{R.drawable.bg1,R.drawable.bg2,
			R.drawable.bg3,R.drawable.bg4,R.drawable.bg5,R.drawable.bg6};
	private LinearLayout llayout1;
	private RelativeLayout rlayout;
	
	public final String name="filelistDB";	
	public final String filepath="filepath";
	public final String table1="filelisttable";
	public final String filename="filename";
	private DatabaseHelper1 dbHelper1;	
	public int longId;
	public SimpleAdapter adapter=null ;
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.mainmenu);
		intent = new Intent();
		mContext = MainMenu.this;

		scanSD = new ScanSD();
		findView();
		
		initBookMark();
		//initListView();
		textView1.setOnClickListener(radButtonClicked);		
		nextPage.setOnClickListener(radButtonClicked);
		bmRadioButton.setOnClickListener(radButtonClicked);
		myTF.setOnClickListener(radButtonClicked);
		//getDialogIntent();
	}

	private void findView() {
		textView1 = (TextView) findViewById(R.id.textView1);		
		bmRadioButton = (RadioButton) findViewById(R.id.bookmark);		
		nextPage = (RadioButton) findViewById(R.id.radio_next);
		ListView = (ListView) findViewById(R.id.listfile);
		myTF=(RadioButton)findViewById(R.id.myTextFile);
		llayout1=(LinearLayout)findViewById(R.id.main_tab_container);
		rlayout=(RelativeLayout)findViewById(R.id.mainRelatout);
		//System.out.println("findview==========");
	}

	private OnClickListener radButtonClicked = new OnClickListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.bookmark:
				initBookMark();
				showDialog(0);System.out.println("R.id.bookmark:");				
				break;

			case R.id.radio_next:{
				MyDialog myDialog=new MyDialog(MainMenu.this);
				myDialog.show();
				break;
			}				
			case R.id.textView1:
				Toast.makeText(MainMenu.this, "thanks", 500).show();
				finish();
				break;
			
			case R.id.myTextFile:{
				intent.setClass(mContext, DialogActivity.class);				
				startActivity(intent);
				//finish();
				break;
			}
			
			}
		}
	};

	private void initBookMark() {
		try{
			db = new DbHelper(mContext);
			try {
				mCursor = db.select();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (mCursor.getCount() > 0) {
				for (int i = 0; i < mCursor.getCount(); i++) {
					mCursor.moveToPosition(mCursor.getCount() - (i + 1));
					String str = mCursor.getString(1);
					str = str.substring(str.lastIndexOf('/') + 1, str.length());
					items[i] = str + ": " + mCursor.getString(2)+":"+mCursor.getString(3);
				}
			}
			mCursor.close();
			db.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(!mCursor.isClosed()){
				mCursor.close();
			}
			if(db!=null){
				db.close();
			}
		}
		
	}
	/**
	 * 获取并处理各种Intent 对象所携带的信息
	 */
	public void getDialogIntent(){
		Intent intent1=this.getIntent();
		if(intent1.getStringExtra("itemId")!=null){
			int position=Integer.parseInt(intent1.getStringExtra("itemId"));
			switch(position){
			case 0:{
				break;
			}
			case 1:{
				dbControl();
				break;
			}
			case 2:{
				break;
			}
			case 3: case 4:case 5:case 6:case 7:case 8:
			{
				int bgid=position-3;
				if(bgid<6){
					System.out.println("========44==="+bgid);
					setBackgroud1(picbg[bgid]);
					setBackgroundDb(picbg[bgid]);
					dbControl();
					System.out.println("==========="+picbg[bgid]);
				break;
			}
				
			}
			/*if(position<=2){//当position的值<=2,表示是来自DialogActivity的Intent
				dbControl();
			}
			else{//否则是来自BackgroudSet的Intent
				int bgid=position-3;
				if(bgid<6){
					System.out.println("========44==="+bgid);
					setBackgroud1(picbg[bgid]);
					setBackgroundDb(picbg[bgid]);
					dbControl();
					System.out.println("==========="+picbg[bgid]);
				}else{
					dbControl();
				}
			}*/
		 }
		}else{
			dbControl();
		}
		
	}
	/**
	 * 检测数据库是否为空，为空则是第一次使用本程序，采用默认设置初始化
	 * 如果不为空则，则读取数据库的相关数据，并设置本程序
	 */
	public void dbControl(){
		try{
			closeDB(c, db1)	;	
			DatabaseHelper dbHelper=new DatabaseHelper(this,DBName ,_id, isfirst, bgpic);
			db1=dbHelper.getWritableDatabase();
			c=db1.query(table, new String[]{_id,isfirst,bgpic}, null, null, null, null, null);
			System.out.println("getCount==="+c.getCount());
			if(!(c.getCount()>0)){
				ContentValues value1=new ContentValues();
				value1.put(isfirst, "false");
				value1.put(bgpic, R.drawable.bg1+"");
				db1.insert(table, null, value1);
				db1.close();
				initFolder();
				
			}else{
				while(c.moveToNext()){					
					String str1=c.getString(c.getColumnIndex(bgpic));
					if(str1!=null){
						int bg=Integer.parseInt(str1);
						System.out.println("bg==="+bg);
						setBackgroud1(bg);					
					}					
				}
				c.close();
				db1.close();
				readFileListDB();
			}			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{			
			closeDB(c, db1)	;	
			
		}
	}
	/**
	 * 第一次运行程序时执行
	 */
	private void initFolder(){
		final String sdroot=Environment.getExternalStorageDirectory().getAbsolutePath();
		String filePath=sdroot+File.separator+"魔幻Ebook";
		
		
		String fileName="唐诗三百首.txt";
		try {
			InputStream assetsFile = this.getAssets().open("tangshisanbaishou.txt");		
					
			if(isExist(filePath)){				
				File file=new File(filePath,fileName);
				OutputStream newFile= new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = assetsFile.read(buffer)) > 0) {
					newFile.write(buffer, 0, length);
				}
				newFile.flush();
				newFile.close();					
			}
			
			assetsFile.close();
		
		}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
				
		
		try {
			DatabaseHelper1 dbHelper2 = new DatabaseHelper1(MainMenu.this,
					name, _id, filename, filepath);
			SQLiteDatabase db2 = dbHelper2.getWritableDatabase();
			ContentValues values1 = new ContentValues();
			values1.put(filename,fileName);
			values1.put(filepath, filePath+File.separator+fileName);
			db2.insert(table1, null, values1);
			db2.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			

		}
		readFileListDB();	
	}
	
	/**
	 * 检查文件或目录是否存在，不存在则创建
	 * @param path 文件路径
	 * @return true 文件存在，否则不存在
	 */
	public boolean isExist(String path){
		File file=new File(path);
		if(!file.exists()){
			if(file.mkdirs()){
				return true;
			}else{
				return false;
			}
		}
		return true;
		
	}




	/**
	 * 检测并关闭游标和数据库
	 * @param c 游标
	 * @param db 数据库
	 */
	public void closeDB(Cursor c,SQLiteDatabase db){
		if(c!=null){
			if(!c.isClosed()){
				c.close();
			}
		}
		if(db!=null){
			if(db.isOpen()){
				db.close();
			}
		}
	}
	/**
	 * 根据用户的设置，跟换壁纸
	 * @param bg 资源id
	 */
	public void setBackgroud1(int bg){
		llayout1.setBackgroundResource(bg);
		rlayout.setBackgroundResource(bg);
		ListView.setBackgroundResource(bg);
	}
	/**
	 * 设置背景根据所给的图片id,并跟新数据库，保存用户设置
	 * @param bgid 图片ID
	 */
	public void setBackgroundDb(int bgid){
		try{
			DatabaseHelper dbHelper=new DatabaseHelper(this,DBName ,_id, isfirst, bgpic);
			db1=dbHelper.getWritableDatabase();
			c=db1.query(table, new String[]{isfirst, bgpic}, null, 
					null, null, null, null);
			String str=null;
			while(c.moveToNext()){
				str=c.getString(c.getColumnIndex(isfirst));
			}			
			ContentValues value1=new ContentValues();
			value1.put(bgpic, bgid+"");
			value1.put(isfirst,str);
			int num=db1.update(table, value1, null, null);
			System.out.println("dadsdas---"+num+"==="+c.getCount());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			c.close();
			db1.close();
		}		
	}
	/**
	 * 启动程序或者刷新列表时从数据库中读取文件列表
	 */
	public void readFileListDB(){
		try{
			ArrayList<HashMap<String,String>> myList=new ArrayList<HashMap<String,String>>();
			dbHelper1=new DatabaseHelper1(MainMenu.this, name, _id,filename, filepath);
			db1=dbHelper1.getReadableDatabase();
			c1=db1.query(table1, new String[]{filename,filepath}, null, null, null, null, null);
			while(c1.moveToNext()){
				HashMap<String, String> map=new HashMap<String, String>();
				String strName=c1.getString(c1.getColumnIndex(filename));
				String strPath=c1.getString(c1.getColumnIndex(filepath));
				map.put("ItemName", strName);
				map.put("ItemPath", strPath);
				myList.add(map);
			}
			adapter = new SimpleAdapter(MainMenu.this,
					myList, R.layout.relative,
					new String[] { "ItemName" }, new int[] { R.id.ItemText });
			ListView.setAdapter(adapter);
			ListView.setSelector(R.drawable.item_selector);
			addLisenerForListView(adapter);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			c1.close();
			db1.close();
		}
		
	}
	public void addLisenerForListView(final SimpleAdapter adapter){
		ListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int pos,
					long id) {
				String path;
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) ListView
						.getItemAtPosition(pos);
				// String name = map.get("ItemText");
				path = map.get("ItemPath");
				System.out.println("initListView======ddddd===");
				intent.setClass(mContext, EbookActivity.class);
				intent.putExtra("pathes", path);
				startActivity(intent);
				adapter.notifyDataSetChanged(); // 通知adapter刷新数据
			}
		});
		ListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				buildCancelDialog(MainMenu.this,arg2).show();					
				return false;
			}
		});
	}
	
	public Dialog buildCancelDialog(Context context,int arg){
		longId=arg;
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("提示");
		builder.setMessage("您确定要删除该记录吗！");
		builder.setPositiveButton("确 定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteDB(longId);
				dbControl();
			}
		});
		builder.setNegativeButton("取 消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		return builder.create();
	}
	/**
	 * 删除列表中对应的文件
	 * @param pos
	 */
	public void deleteDB(int pos){
		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>) ListView
		.getItemAtPosition(pos);
		// String name = map.get("ItemText");
		String fName = map.get("ItemName");
		dbHelper1=new DatabaseHelper1(MainMenu.this, name, _id,filename, filepath);
		db1=dbHelper1.getReadableDatabase();
		c1=db1.query(table1, new String[]{filename,filepath}, filename+"=?", new String[]{fName}, null, null, null);
		while(c1.moveToNext()){
			//String fId=c1.getString(c1.getColumnIndex(_id));
			db1.delete(table1, filename+"=?", new String[]{fName});
		}
		c1.close();
		db1.close();
	}
	/**
	 * 扫描整个内存卡，并将内存卡里的所有txt文件加载到主界面上
	 */
	/*public void initListView() {
		
		System.out.println("initListView11=========");
		ArrayList<File> list = new ArrayList<File>();
		list = scanSD.getFileList();

		final SimpleAdapter adapter = new SimpleAdapter(MainMenu.this,
				scanSD.getMapData(list), R.layout.relative,
				new String[] { "ItemName" }, new int[] { R.id.ItemText });

		ListView.setAdapter(adapter);
		ListView.setSelector(R.drawable.item_selector);
		ListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int pos,
					long id) {
				String path;
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) ListView
						.getItemAtPosition(pos);
				// String name = map.get("ItemText");
				path = map.get("ItemPath");
				System.out.println("initListView=========");
				intent.setClass(mContext, EbookActivity.class);
				intent.putExtra("pathes", path);
				startActivity(intent);
				adapter.notifyDataSetChanged(); // 通知adapter刷新数据
			}
		});
	}*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
				
		menu.add(0, 1, 1, "背景设置").setIcon(R.drawable.menuset);
		menu.add(0, 2, 2, "帮助").setIcon(R.drawable.about1);
		menu.add(0, 3, 3, "退出").setIcon(R.drawable.back);
		System.out.println("Mainmenu======");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {		
			
		case 1:{
			Intent intent1=new Intent(MainMenu.this,BackgroundSet.class);
			startActivity(intent1);	
			finish();
			return true;		
		}
		case 2:{
			Intent intent1=new Intent(MainMenu.this,AboutActivity.class);
			startActivity(intent1);
			return true;
		}
		case 3:
			finish();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		getDialogIntent();		
		super.onResume();
	}
	

	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(mContext)
				.setTitle(R.string.bookmark)

				.setSingleChoiceItems(items, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								pos = which;
							}
						})
				.setPositiveButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.setNegativeButton(R.string.load_bookmark,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Message msg = new Message();
								msg.what = 0;
								msg.arg1 = pos;
								mhHandler.sendMessage(msg);
							}
						}).create();
	}

	Handler mhHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OPENMARK:
				try {
					mCursor = db.select();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (mCursor.getCount() > 0) {
					mCursor.moveToPosition(mCursor.getCount() - (msg.arg1 + 1));
					Log.i("string", items[mCursor.getCount() - (msg.arg1 + 1)]);
					intent.setClass(mContext, EbookActivity.class);
					intent.putExtra("pathes", mCursor.getString(1));
					intent.putExtra("pos", mCursor.getString(2));
					startActivity(intent);
				}
				db.close();// 打开之后记得关闭数据库
				break;
				
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
}