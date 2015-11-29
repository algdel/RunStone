package com.zph.run;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/11/14.
 */
public class DbHelper extends SQLiteOpenHelper{
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists tb_history" +
                "(_id integer primary key autoincrement," +
                "time varchar(20)," +
                "distance varchar(20)," +
                "speed varchar(20)," +
                "maxspeed varchar(20)," +
                "spend varchar(20))");
        db.execSQL("create table if not exists tb_plan" +
                "(_id integer primary key autoincrement," +
                "time varchar(30)," +
                "distance varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
