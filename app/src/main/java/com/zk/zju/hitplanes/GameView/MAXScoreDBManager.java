package com.zk.zju.hitplanes.GameView;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Date;

/**
 * Created by Administrator on 2017/11/12.
 */

public class MAXScoreDBManager  extends SQLiteOpenHelper {
    private static MAXScoreDBManager mInstance;
    public static final int FIRST_VERSION = 1;
    public static final String TABLE_NAME = "MAXScore_info";
    private static SQLiteDatabase MAXScore_db;
//
//  public static String getTableName() {
//       return TABLE_NAME;
//    }
//
//   public static SQLiteDatabase getUsers_db() {
//        return users_db;
//    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not EXISTS " + TABLE_NAME +"(ID integer primary key autoincrement, Score integer)");
    }

    public static MAXScoreDBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (MAXScoreDBManager.class) {
                if (mInstance == null) {
                    mInstance = new MAXScoreDBManager(context);
                    MAXScore_db = mInstance.getWritableDatabase();
                    mInstance.insert(0L);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public MAXScoreDBManager(Context context) {
        super(context, TABLE_NAME, null, FIRST_VERSION);
    }

//    public UsersDBManager(String dir) {
//        //  db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/my.db3", null);
//        //如果数据库不存在，就创建它
//        users_db = SQLiteDatabase.openOrCreateDatabase(dir + "/users.db3", null);
//        Log.d("zk", "CreateSuccessful ! ");
//    }

    public boolean insert(Long score) {
        ContentValues temp = new ContentValues();
        temp.put("Score",score);
        try {
            MAXScore_db.execSQL("create table if not EXISTS " + TABLE_NAME +"(ID integer primary key autoincrement, Score integer)");
            Log.d("zkk", "CreateTableSuccessful ! ");
        } catch (SQLException e) {
            Log.d("zkk", "CreateTableFailed ! ");
            return false;
        }
        long tag = -1;
        tag = MAXScore_db.insert(TABLE_NAME, null, temp);
        Log.d("zk", "insertSuccessful ! tag =  " + tag);
        if (tag == -1) {
            return false;
        } else {
            return true;
        }
    }
    private MAXScore covertToMAXScore(Cursor cursor) {
        int length = cursor.getCount();
        if (length == 0 || !cursor.moveToFirst()) {
            return null;
        } else {
            MAXScore resultMAXScore = new MAXScore();
            resultMAXScore.setId(cursor.getInt(0));
            resultMAXScore.setMAXScore(cursor.getLong(1));
            cursor.close();
            return resultMAXScore;
        }
    }

     MAXScore query(int id) {
        if (id <= -1) {
            return null;
        } else {
            Cursor result = MAXScore_db.query(TABLE_NAME, new String[]{"ID", "Score"}, "ID = " + id, null, null, null, null);
            return covertToMAXScore(result);
        }
    }
    public boolean update(int id, long score) {
        if (id < 0) {
            return false;
        } else {
            if (query(id)!= null) {
                MAXScore_db.execSQL("update " + TABLE_NAME + " set Score =" + "'" + score + "'" + " where ID = " + id + ";");
                return true;
            } else{
                return false;
            }

        }
    }
    public boolean deleteAll() {
        try {
            MAXScore_db.execSQL("delete from " + TABLE_NAME + ";");
            MAXScore_db.execSQL("DELETE FROM MAXScore_info;");
            int de;
            de = MAXScore_db.delete(TABLE_NAME, null, null);
            Log.d("zkk", "已执行删除" + de);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

}


