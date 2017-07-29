package com.tomgrubbe.shoplite.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.tomgrubbe.shoplite.model.RemovedItem;
import com.tomgrubbe.shoplite.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class RemovedItemsTable extends TableBase {
    public static final String TABLE_NAME = "RemovedItem";
    public static final String COLUMN_ID = "RecentlyRemovedId";
    public static final String COLUMN_PRODUCT_ID = "ProductId";
    public static final String COLUMN_IS_CHECKED = "IsChecked";
    public static final String COLUMN_DATE_TIME = "Date";

    public static final String[] ALL_COLUMNS =
            { COLUMN_ID, COLUMN_PRODUCT_ID, COLUMN_IS_CHECKED, COLUMN_DATE_TIME };

    public static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    COLUMN_ID + " TEXT PRIMARY KEY," +
                    COLUMN_PRODUCT_ID + " TEXT," +
                    COLUMN_IS_CHECKED + " TEXT," +
                    COLUMN_DATE_TIME + " TEXT" + ");";

    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_NAME;

    public RemovedItemsTable(Context context) {
        super(context);
        openDB().execSQL(SQL_CREATE);
    }

    public long getRemovedItemsCount()    {
        return DatabaseUtils.queryNumEntries(openDB(), TABLE_NAME);
    }

    public void addItem(RemovedItem item)  {
        ContentValues values = item.toValues();
        openDB().insert(TABLE_NAME, null, values);
    }

    public void deleteItem(RemovedItem item)  {
        openDB().delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { item.getRemovedItemId() } );
    }

    public void deleteAll() {
        openDB().delete(TABLE_NAME, null, null);
    }

    public void updateRemovedItem(RemovedItem item)   {
        int checked = (item.isChecked()) ?  1 : 0;
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, item.getRemovedItemId());
        values.put(COLUMN_PRODUCT_ID, item.getProductId());
        values.put(COLUMN_IS_CHECKED, checked);

        String date = Utils.getDateStringNow();
        values.put(COLUMN_DATE_TIME, date);
        openDB().update(TABLE_NAME, values, COLUMN_ID + "= ?", new String[] { item.getRemovedItemId() });
    }

    public RemovedItem fromProductId(String id) {
        Cursor cursor = openDB().query(TABLE_NAME, null, COLUMN_PRODUCT_ID + "= ?", new String[] { id }, null, null, null);

        if (cursor.moveToNext())    {
            String itemId      = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            String productId   = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID));
            boolean isChecked  = (cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CHECKED)) != 0);
            String date        = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TIME));

            cursor.close();
            return new RemovedItem(itemId, productId, isChecked, date);
        }
        cursor.close();
        return null;
    }



    public List<RemovedItem> getAllRemovedItems()   {
        List<RemovedItem> items = new ArrayList<>();

        Cursor cursor = openDB().query(TABLE_NAME, ALL_COLUMNS, null, null, null, null, COLUMN_DATE_TIME + " DESC");

        while (cursor.moveToNext()) {
            String removedItemId  = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            String productId      = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID));
            boolean isChecked     = (cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CHECKED)) != 0);
            String date           = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TIME));

            RemovedItem removedItem = new RemovedItem(removedItemId, productId, isChecked, date);
            items.add(removedItem);
        }
        cursor.close();
        return items;
    }
}

