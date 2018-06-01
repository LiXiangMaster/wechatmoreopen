package com.shenqi.duokai.interf;

/**
 * Created by lixiang on 2016/10/6.
 */
public interface MadedAppDBContants {
    String DB_NAME = "madedapp.db";
    int START_VERSION = 1;
    String SQL_CREATE_TABLE = "create table hadmadedapp(_id integer primary key autoincrement, packagename text)";
    String TABLE_NAME = "hadmadedapp";
    String COLUMN_PACKAGENAME = "packagename";
}
