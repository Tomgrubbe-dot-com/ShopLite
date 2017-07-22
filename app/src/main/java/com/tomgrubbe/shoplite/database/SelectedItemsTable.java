package com.tomgrubbe.shoplite.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.tomgrubbe.shoplite.model.SelectedItem;

import java.util.ArrayList;
import java.util.List;

public class SelectedItemsTable extends TableBase {

    public static final String TABLE_NAME = "SelectedItems";
    public static final String COLUMN_ID = "SelectedItemId";
    public static final String COLUMN_PRODUCT_ID = "ProductId";
    public static final String COLUMN_IS_CHECKED = "IsChecked";
    public static final String COLUMN_QUANTITY = "Quantity";

    public static final String[] ALL_COLUMNS =
            {COLUMN_ID, COLUMN_PRODUCT_ID, COLUMN_IS_CHECKED, COLUMN_QUANTITY };

    public static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    COLUMN_ID + " TEXT PRIMARY KEY," +
                    COLUMN_PRODUCT_ID + " TEXT," +
                    COLUMN_IS_CHECKED + " TEXT" +
                    COLUMN_QUANTITY + " TEXT" + ");";

    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_NAME;

    public static final String SQL_UPGRADE_TO_V2 =
            "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_QUANTITY;

    public SelectedItemsTable(Context context) {
        super(context);
        openDB().execSQL(SQL_CREATE);
    }

    public long getSelectedItemsCount()    {
        return DatabaseUtils.queryNumEntries(openDB(), TABLE_NAME);
    }

    public void addItem(SelectedItem item)  {
        ContentValues values = item.toValues();
        openDB().insert(TABLE_NAME, null, values);
    }

    public void deleteItem(SelectedItem item)  {
        openDB().delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { item.getSelectedItemId() } );
    }

    public void deleteAll() {
        openDB().delete(TABLE_NAME, null, null);
    }

    public void updateSelectedItem(SelectedItem selectedItem)   {
        int checked = (selectedItem.isChecked()) ?  1 : 0;
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, selectedItem.getSelectedItemId());
        values.put(COLUMN_PRODUCT_ID, selectedItem.getProductId());
        values.put(COLUMN_IS_CHECKED, checked);
        values.put(COLUMN_QUANTITY, selectedItem.getQuantity());
        openDB().update(TABLE_NAME, values, COLUMN_ID + "= ?", new String[] { selectedItem.getSelectedItemId() });
    }

    public SelectedItem fromProductId(String id) {
        Cursor cursor = openDB().query(TABLE_NAME, null, COLUMN_PRODUCT_ID + "= ?", new String[] { id }, null, null, null);

        if (cursor.moveToNext())    {
            String itemId      = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            String productId   = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID));
            boolean isChecked  = (cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CHECKED)) != 0);
            int quantity       = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));

            cursor.close();
            return new SelectedItem(itemId, productId, isChecked, quantity);
        }
        cursor.close();
        return null;
    }

    public List<SelectedItem> getAllSelectedItems()   {
        List<SelectedItem> items = new ArrayList<>();

        Cursor cursor = openDB().query(TABLE_NAME, ALL_COLUMNS, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String selectedItemId   = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            String productId        = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID));
            boolean isChecked       = (cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CHECKED)) != 0);
            int quantity            = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));

            SelectedItem selectedItem = new SelectedItem(selectedItemId, productId, isChecked, quantity);
            items.add(selectedItem);
        }
        cursor.close();
        return items;
    }
}
