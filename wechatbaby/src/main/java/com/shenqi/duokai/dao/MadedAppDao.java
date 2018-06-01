package com.shenqi.duokai.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shenqi.duokai.db.MadedAppDBHelper;
import com.shenqi.duokai.interf.MadedAppDBContants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixiang on 2016/10/6.
 */
public class MadedAppDao {
    private MadedAppDBHelper helper;

    public MadedAppDao(Context context) {
        helper = new MadedAppDBHelper(context);
    }

    public void add(String packageName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(MadedAppDBContants.COLUMN_PACKAGENAME, packageName);
            db.insert(MadedAppDBContants.TABLE_NAME, null, values);
            db.close();
        }
    }

    public void delete(String packageName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(MadedAppDBContants.TABLE_NAME, MadedAppDBContants.COLUMN_PACKAGENAME + " = ?", new String[]{packageName});
            db.close();
        }
    }

    public List<String> queryAll() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.query(MadedAppDBContants.TABLE_NAME, new String[]{MadedAppDBContants.COLUMN_PACKAGENAME}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                String packageName = cursor.getString(0);
                list.add(packageName);
            }
            cursor.close();
            db.close();
        }
        return list;
    }
}
