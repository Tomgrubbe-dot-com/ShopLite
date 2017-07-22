package com.tomgrubbe.shoplite.database;


import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tomgrubbe.shoplite.R;

public abstract class TableBase {
    public static final String TAG = "TableBase";
    public static final String DB_FILE_NAME = "shoplite.db";
    public static final int DB_VERSION = 2;

    protected Context mContext;
    protected static DBHelper mDBHelper;

    public TableBase(Context context)   {

        mContext = context;
    }

    public SQLiteDatabase openDB()  {
        if (mDBHelper == null)  {
            mDBHelper = new DBHelper(mContext);
        }
        return mDBHelper.getWritableDatabase();
    }

    public void closeDB()   {
        mDBHelper.close();
    }


    protected static class DBHelper extends SQLiteOpenHelper {


        public DBHelper(Context context)    {
            super(context, DB_FILE_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CategoriesTable.SQL_CREATE);
            db.execSQL(ProductsTable.SQL_CREATE);
            db.execSQL(SelectedItemsTable.SQL_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion == 1 && newVersion == 2) {
                db.execSQL(SelectedItemsTable.SQL_UPGRADE_TO_V2);
            } else  {
                db.execSQL(CategoriesTable.SQL_DELETE);
                db.execSQL(ProductsTable.SQL_DELETE);
                db.execSQL(SelectedItemsTable.SQL_DELETE);
                onCreate(db);
            }
        }


    }
}
