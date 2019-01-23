package com.camp.bit.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {
//CREATE TABLE note(_id INTEGER PRIMARY KEY AUTOINCREMENT, date INTEGER, state INTEGER, content TEXT);
    // TODO 定义表结构和 SQL 语句常量
    public static  final String SQL_CREATE_ENTRIES="CREATE TABLE "+TodoEntry.TABLE_NAME+" ("+TodoEntry._ID+" INTEGER PRIMARY KEY,"+
        TodoEntry.COLUMN1_NAME+" INTEGER,"+TodoEntry.COLUMN2_NAME+" INTEGER,"+TodoEntry.COLUMN3_NAME+" TEXT)";
    public static final String SQL_DELETE_ENTRYIES="DROP TABLE IF EXISTS "+TodoEntry.TABLE_NAME;
    public static class TodoEntry implements BaseColumns{
        public static final String TABLE_NAME ="note";
        public static final String COLUMN1_NAME="date";
        public static final String COLUMN2_NAME="state";
        public static final String COLUMN3_NAME ="content";
}
    private TodoContract() {
    }

}
