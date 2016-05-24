package ljt.myebook;

import java.io.IOException;
import java.text.SimpleDateFormat;

import ljt.myebook.bookpage.BookPageFactory;
import ljt.myebook.bookpage.PageWidget;
import ljt.myebook.sqlite.DatabaseHelper;
import ljt.myebook.sqlite.DbHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class EbookActivity extends Activity {

	public final static int OPENMARK = 0;
	public final static int SAVEMARK = 1;
	public final static int TEXTSET = 2;

	private PageWidget mPageWidget;
	private BookPageFactory pagefactory;
	DbHelper db;
	Bitmap mCurPageBitmap, mNextPageBitmap;
	Canvas mCurPageCanvas, mNextPageCanvas;
	Context mContext;
	Cursor mCursor;
	final String[] items = { "书签1 未使用", "书签2 未使用", "书签3 未使用", "书签4 未使用","书签5 未使用","书签6未使用","书签7 未使用",
							"书签8 未使用","书签9 未使用","书签10未使用", "自动书签 未使用" };
	

	String fileName;
	String path;
	int curPostion;
	int screenWidth;
	int screenHeight;
	int bookMarkPos;
	private final String isfirst="isfirst";
	private final String bgpic="bgpic";
	private final String table="configdb";
	private final String _id="_id";
	private SQLiteDatabase db1;
	private final String DBName="eebookdb";
	private final int[] picbg=new int[]{R.drawable.bg1,R.drawable.bg2,
			R.drawable.bg3,R.drawable.bg4,R.drawable.bg5,R.drawable.bg6};
	private int bgid=R.drawable.bg1;
	
	private PowerManager pm ;
	private PowerManager.WakeLock mWakeLock ;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		initScreedWH();
		mPageWidget = new PageWidget(this);
		mPageWidget.setWidth(screenWidth);
		mPageWidget.setHeight(screenHeight);
		mContext = this;

		initBookMark();
		dbControl();
		setContentView(mPageWidget);

		initBookPage();

		String pos = this.getIntent().getStringExtra("pos");

		if (!isViewIntent()) {
			path = this.getIntent().getStringExtra("pathes");

		} else {
			path = this.getIntent().getData().getPath();
		}

		try {
			//Log.i("path", path);
			System.out.println("path="+path);
			pagefactory.openBook(path);
			fileName = pagefactory.getFileName();
			if (pos != null) {
				pagefactory.setBeginPos(Integer.valueOf(pos));
				pagefactory.pageUp();
			}
			pagefactory.onDraw(mCurPageCanvas);
		} catch (IOException e1) {
			Toast.makeText(this, fileName + "不存在，请将文件放在SD卡根目录下,可以超过100M容量",
					Toast.LENGTH_LONG).show();
		}

		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);

		mPageWidget.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent e) {
				// TODO Auto-generated method stub
				boolean ret = false;
				if (v == mPageWidget) {
					if (e.getAction() == MotionEvent.ACTION_DOWN) {
						mPageWidget.abortAnimation();
						mPageWidget.calcCornerXY(e.getX(), e.getY());
						pagefactory.onDraw(mCurPageCanvas);
						if (mPageWidget.DragToRight()) {
							try {
								pagefactory.prePage();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							if (pagefactory.isFirstPage())
								return false;
							pagefactory.onDraw(mNextPageCanvas);
						} else {
							try {
								pagefactory.nextPage();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							if (pagefactory.isLastPage()) {
								return false;
							}
							pagefactory.onDraw(mNextPageCanvas);
						}
						mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
					}
					ret = mPageWidget.doTouchEvent(e);

					return ret;
				}
				return false;
			}
		});
	}

	private void initScreedWH() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenHeight = dm.heightPixels;
		screenWidth = dm.widthPixels;
	}

	private boolean isViewIntent() {
		String action = getIntent().getAction();
		return Intent.ACTION_VIEW.equals(action);
	}

	private void initBookPage() {
		mCurPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		pagefactory = new BookPageFactory(screenWidth, screenHeight);
//***************************************************************************
		pagefactory.setBgBitmap(BitmapFactory.decodeResource(
				mContext.getResources(), bgid));
	}
	
	public void dbControl(){
		Cursor c=null;
		try{
			DatabaseHelper dbHelper=new DatabaseHelper(this,DBName ,_id, isfirst, bgpic);
			db1=dbHelper.getWritableDatabase();
			c=db1.query(table, new String[]{_id,isfirst,bgpic}, null, null, null, null, null);
			System.out.println("getCount==="+c.getCount());
			while(c.moveToNext()){				
				String str1=c.getString(c.getColumnIndex(bgpic));
				if(str1!=null){
					bgid=Integer.parseInt(str1);
					System.out.println("bg==="+bgid);
										
				}
			}	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			c.close();
			db1.close();
		}
	}

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
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			mCursor.close();
			db.close();
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		
		menu.add(0, 1, 1, "书签").setIcon(R.drawable.menushuqian);
		menu.add(0, 2, 2, "设置").setIcon(R.drawable.menuset);
		menu.add(0, 3, 3, "帮助").setIcon(R.drawable.about1);
		menu.add(0, 4, 4, "退出").setIcon(R.drawable.back);
		System.out.println("menu======");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {		
		case 1:
			showDialog(OPENMARK);
			return true;

		case 2:
			showDialog(TEXTSET);
			break;
		case 3:{
			Intent intent1=new Intent(EbookActivity.this,AboutActivity.class);
			startActivity(intent1);	
			break;
		}
		case 4:
			finish();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		//关闭屏幕常亮功能	
		if (mWakeLock != null) {

			mWakeLock.release();

			}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag"); 
		// in onResume() call		
		mWakeLock.acquire(); 
		// in onPause() call 
			
		super.onResume();
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case OPENMARK:
			return new AlertDialog.Builder(mContext)
					.setTitle(R.string.bookmark)

					.setSingleChoiceItems(items, 0,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									bookMarkPos = which;
								}
							})
					.setPositiveButton(R.string.load_bookmark,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Message msg = new Message();
									msg.what = OPENMARK;
									msg.arg1 = bookMarkPos;
									mhHandler.sendMessage(msg);
								}
							})
					.setNegativeButton(R.string.save_bookmark,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Message msg = new Message();
									msg.what = SAVEMARK;
									msg.arg1 = bookMarkPos;
									curPostion = pagefactory.getCurPostion();
									msg.arg2 = curPostion;
									mhHandler.sendMessage(msg);
								}
							}).create();
		case TEXTSET:
			String color[] = new String[] { "红色", "深灰色", "黄色", "蓝色", "黑色" };
			return new AlertDialog.Builder(mContext)
					.setTitle("字体颜色设置")

					.setSingleChoiceItems(color, 0,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									bookMarkPos = which;
								}
							})
					.setPositiveButton("ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Message msg = new Message();
									msg.what = TEXTSET;
									switch (bookMarkPos) {
									case 0:
										msg.arg1 = Color.RED;
										break;
									case 1:
										msg.arg1 = Color.DKGRAY;
										break;
									case 2:
										msg.arg1 = Color.YELLOW;
										break;
									case 3:
										msg.arg1 = Color.BLUE;
										break;
									case 4:
										msg.arg1 = Color.BLACK;
										break;

									default:
										break;
									}
									mhHandler.sendMessage(msg);
								}
							})
					.setNegativeButton("cancle",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).create();
		default:
			break;
		}
		return null;

	}

	Handler mhHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case TEXTSET:
				pagefactory.changBackGround(msg.arg1);
				pagefactory.onDraw(mCurPageCanvas);
				mPageWidget.postInvalidate();
				break;

			case OPENMARK:
				try {
					mCursor = db.select();

				} catch (Exception e) {
					e.printStackTrace();
				}
				if (mCursor.getCount() > 0) {
					mCursor.moveToPosition(mCursor.getCount() - (msg.arg1 + 1));
					String pos = mCursor.getString(2);
					String tmp = mCursor.getString(1);
					
					if (fileName.equals(tmp.substring(tmp.lastIndexOf('/') + 1,
							tmp.length()))) {

						pagefactory.setBeginPos(Integer.valueOf(pos));
						try {
							pagefactory.prePage();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						pagefactory.onDraw(mNextPageCanvas);
						mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
						mPageWidget.invalidate();
						db.close();
					} else {
						Intent intent = new Intent(EbookActivity.this,
								EbookActivity.class);
						intent.putExtra("pathes", mCursor.getString(1));
						intent.putExtra("pos", mCursor.getString(2));
						db.close();
						startActivity(intent);
						finish();
					}
				}
				break;
//保存书签到数据库 
			case SAVEMARK:
				try {
					mCursor = db.select();
					SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String currentTime = tempDate.format(new java.util.Date());

					if (mCursor.getCount() > 0 && mCursor.getCount() > msg.arg1) {
						mCursor.moveToPosition(mCursor.getCount() - (msg.arg1 + 1));
						db.update(mCursor.getInt(0), path, String.valueOf(msg.arg2),currentTime);
					} else {
						db.insert(path, String.valueOf(msg.arg2),currentTime);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					db.close();
				}				
				
				items[msg.arg1] = path.substring(path.lastIndexOf('/') + 1,
						path.length()) + ": " + String.valueOf(msg.arg2);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	/**
	 * 退出时自动保存书签
	 */
	public void saveMark(){
		bookMarkPos=0;
		for (int i = 0; i < items.length; i++) {
			if(items[i].contains("未使用")){
				bookMarkPos=i;
				break;
			}
		}
		System.out.println("bookMarkPos=="+bookMarkPos);
		Message msg = new Message();
		msg.what = SAVEMARK;
		msg.arg1 = bookMarkPos;
		curPostion = pagefactory.getCurPostion();
		msg.arg2 = curPostion;
		mhHandler.sendMessage(msg);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		saveMark();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		saveMark();
		super.onDestroy();
	}
	
	
}