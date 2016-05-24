package ljt.myebook.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	private final static int VERSION=1;	
	private String isfirst=null;
	private String bgpic="";
	private String _id;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version,String _id,String isfirst,String bgpic) {
		super(context, name, factory, version);		
		this.isfirst=isfirst;
		this.bgpic=bgpic;
		this._id=_id;
		
	}
	public DatabaseHelper(Context context,String name,int version,String _id,String isfirst,String bgpic){
		this(context,name,null,version,_id,isfirst,bgpic);
	}
	public DatabaseHelper(Context context,String name,String _id,String isfirst,String bgpic){
		this(context,name,VERSION,_id,isfirst,bgpic);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		
		//execSQL函数用于执行SQL语句
		db.execSQL("create table configdb( " + _id + " integer PRIMARY KEY autoincrement, " + isfirst + " varchar(10), " + bgpic + " varchar(50) )");
		 
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	

}