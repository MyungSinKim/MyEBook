package ljt.myebook.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper1 extends SQLiteOpenHelper{
	private final static int VERSION=1;	
	private String filepath=null;	
	private String _id;
	private String filename=null;

	public DatabaseHelper1(Context context, String name, CursorFactory factory,
			int version,String _id,String filename,String filepath) {
		super(context, name, factory, version);		
		this.filepath=filepath;		
		this._id=_id;
		this.filename=filename;
		
	}
	public DatabaseHelper1(Context context,String name,int version,String _id,String filename,String filepath){
		this(context,name,null,version,_id,filename,filepath);
	}
	public DatabaseHelper1(Context context,String name,String _id,String filename,String filepath){
		this(context,name,VERSION,_id,filename,filepath);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		
		//execSQL函数用于执行SQL语句
		db.execSQL("create table filelisttable( " + _id + " integer PRIMARY KEY autoincrement, " +filename+" varchar(50), "+ filepath + " varchar(100) )");
		 
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	

}