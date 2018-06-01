package com.shenqi.duokai.constants;

public interface AppMadeDBConstants {

	String DB_NAME = "applock.db";
	int START_VERSION = 1;
	String SQL_CREATE_TABLE = "create table applocked(_id integer primary key autoincrement, packagename text)";
	String TABLE_NAME = "applocked";
	String COLUMN_PACKAGENAME = "packagename";

}
