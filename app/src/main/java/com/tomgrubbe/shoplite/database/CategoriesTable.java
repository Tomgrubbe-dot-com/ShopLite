package com.tomgrubbe.shoplite.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.tomgrubbe.shoplite.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoriesTable extends TableBase {
    public static final String TABLE_NAME = "Categories";
    public static final String COLUMN_ID = "CategoryId";
    public static final String COLUMN_NAME = "CategoryName";

    public static final String[] ALL_COLUMNS =
            {COLUMN_ID, COLUMN_NAME};

    public static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    COLUMN_ID + " TEXT PRIMARY KEY," +
                    COLUMN_NAME + " TEXT" + ");";

    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_NAME;

    public CategoriesTable(Context context) {
        super(context);
        openDB().execSQL(SQL_CREATE);
    }

    public long getCategoryCount()    {
        return DatabaseUtils.queryNumEntries(openDB(), TABLE_NAME);
    }

    public void addCategory(Category category)  {
        ContentValues values = category.toValues();
        openDB().insert(TABLE_NAME, null, values);
    }

    public void deleteCategory(Category category)  {
        openDB().delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { category.getCategoryId() } );
    }

    public String findId(String categoryName)   {
        Cursor cursor = openDB().query(TABLE_NAME, null, COLUMN_NAME + "= ?", new String[] { categoryName }, null, null, null);

        if (cursor.moveToNext())    {
            String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
            return id;
        }
        cursor.close();
        return null;
    }

    public Category getCategoryFromId(String id)  {
        Cursor cursor = openDB().query(TABLE_NAME, null, COLUMN_ID + "= ?", new String[] { id }, null, null, null);

        if (cursor.moveToNext())    {
            String categoryId          = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            String name        = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));

            cursor.close();
            return new Category(categoryId, name);
        }
        cursor.close();
        return null;
    }

    public Category getCategoryFromName(String name)  {
        Cursor cursor = openDB().query(TABLE_NAME, null, COLUMN_NAME + "= ?", new String[] { name }, null, null, null);

        if (cursor.moveToNext())    {
            String categoryId          = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            String catName        = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));

            cursor.close();
            return new Category(categoryId, catName);
        }
        cursor.close();
        return null;
    }

    public boolean exists(String categoryNAme)   {
        List<Category> allItems = getAllCategories();
        for (Category category : allItems)    {
            if (category.getCategoryName().equalsIgnoreCase(categoryNAme))    {
                return true;
            }
        }
        return false;
    }

    public List<Category> getAllCategories()   {
        List<Category> categories = new ArrayList<>();

        Cursor cursor = openDB().query(TABLE_NAME, ALL_COLUMNS, null, null, null, null, COLUMN_NAME);

        while (cursor.moveToNext()) {
            String categoryId   = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            String categoryName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));

            Category category = new Category(categoryId, categoryName);
            categories.add(category);
        }
        cursor.close();
        return categories;
    }
}
