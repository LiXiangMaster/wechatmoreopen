package com.shenqi.duokai.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shenqi.duokai.interf.MadedAppDBContants;

/**
 * Created by lixiang on 2016/10/6.
 */
public class MadedAppDBHelper extends SQLiteOpenHelper {

    public MadedAppDBHelper(Context context) {
        super(context, MadedAppDBContants.DB_NAME, null, MadedAppDBContants.START_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MadedAppDBContants.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
