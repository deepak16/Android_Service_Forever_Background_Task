package com.example.hp.backgrounduploading;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HP on 05-07-2017.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Photos1.db";
    public static final String TABLE_NAME = "Photos_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "IMAGE_PATH";
   // public static final String COL_3 = "IMAGE_NAME";//i dont need name
    //public static final String COL_4 = "MARKS";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+ TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,IMAGE_PATH TEXT) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    public boolean insertdata(String path){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,path);
        //contentValues.put(COL_3,name);

        long result = sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        if(result == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getalldata()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("select * from "+ TABLE_NAME,null);
        return res;

    }

   public Cursor getonedata(String id)
    {
        // Cursor cursor = null;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //boolean result = false;
        //  try {
        String[] args = { "" + id };
        StringBuffer sbQuery = new StringBuffer("SELECT * from ").append(
                TABLE_NAME).append(" where ID=?");
        Cursor res = sqLiteDatabase.rawQuery(sbQuery.toString(), args);
            /*if (res != null && res.moveToFirst()) {
                //result = true;
                return  res;
            }
        } catch (Exception e) {
            Log.e("Requestdbhelper", e.toString());
        }*/
        //return result;
        return  res;

       /* String uniqueid = id;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("select * from "+ TABLE_NAME + " where id = ?",null);
        return res;*/

    }

    public int getpicturepathcount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }





    public boolean updatedata(String id , String path , String name ){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,path);
       // contentValues.put(COL_3,name);
        sqLiteDatabase.update(TABLE_NAME,contentValues, "id = ?",new String[]{id});
        return true;


    }

    public  int deletedata(String id)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME,"ID = ?",new String[]{id});

    }

}
