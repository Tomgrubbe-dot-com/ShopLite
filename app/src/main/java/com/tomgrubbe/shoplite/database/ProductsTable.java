package com.tomgrubbe.shoplite.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.tomgrubbe.shoplite.model.Category;
import com.tomgrubbe.shoplite.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductsTable extends TableBase {
    public static final String TABLE_NAME = "Products";
    public static final String COLUMN_ID = "ProductId";
    public static final String COLUMN_NAME = "ProductName";
    public static final String COLUMN_CATEGORY_ID = "CategoryId";

    public static final String[] ALL_COLUMNS =
            {COLUMN_ID, COLUMN_NAME, COLUMN_CATEGORY_ID};

    public static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    COLUMN_ID + " TEXT PRIMARY KEY," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_CATEGORY_ID + " TEXT" + ");";

    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_NAME;

    public ProductsTable(Context context) {
        super(context);
        openDB().execSQL(SQL_CREATE);
    }

    public long getProductCount()    {
        return DatabaseUtils.queryNumEntries(openDB(), TABLE_NAME);
    }

    public void addProduct(Product product)  {
        ContentValues values = product.toValues();
        openDB().insert(TABLE_NAME, null, values);
    }

    public void deleteProduct(Product product)  {
        openDB().delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { product.getProductId() } );
    }

    public String findId(String productName)   {
        Cursor cursor = openDB().query(TABLE_NAME, null, COLUMN_NAME + "= ?", new String[] { productName }, null, null, null);

        if (cursor.moveToNext())    {
            String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
            return id;
        }
        cursor.close();
        return null;
    }

    public boolean exists(String productName)   {
        List<Product> allItems = getAllProducts();
        for (Product product : allItems)    {
            if (product.getName().equalsIgnoreCase(productName))    {
                return true;
            }
        }
        return false;
    }

    public void updateProduct(Product product)   {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, product.getProductId());
        values.put(COLUMN_NAME, product.getName());
        values.put(COLUMN_CATEGORY_ID, product.getCategoryId());
        openDB().update(TABLE_NAME, values, COLUMN_ID + "= ?", new String[] {product.getProductId() });
    }

    public Product getProductFromName(String productName)  {
        Cursor cursor = openDB().query(TABLE_NAME, null, COLUMN_NAME + "= ?", new String[] { productName }, null, null, null);

        if (cursor.moveToNext())    {
            String id          = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            String name        = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String catID       = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_ID));

            cursor.close();
            return new Product(id, name, catID);
        }
        cursor.close();
        return null;
    }

    public Product getProductFromId(String productId)  {
        Cursor cursor = openDB().query(TABLE_NAME, null, COLUMN_ID + "= ?", new String[] { productId }, null, null, null);

        if (cursor.moveToNext())    {
            String id          = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            String name        = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String catID       = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_ID));

            cursor.close();
            return new Product(id, name, catID);
        }
        cursor.close();
        return null;
    }

    public List<Product> getProductFromCategory(Category category)  {
        List<Product> products = new ArrayList<>();

        Cursor cursor = openDB().query(TABLE_NAME, null, COLUMN_CATEGORY_ID + "= ?", new String[] { category.getCategoryId() }, null, null, null);

        while (cursor.moveToNext()) {
            String prodId      = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            String prodName    = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String catId       = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_ID));

            Product product = new Product(prodId, prodName, catId);
            products.add(product);
        }
        cursor.close();
        return products;
    }

    public List<Product> getAllProducts()   {
        List<Product> products = new ArrayList<>();

        Cursor cursor = openDB().query(TABLE_NAME, ALL_COLUMNS, null, null, null, null, COLUMN_NAME);

        while (cursor.moveToNext()) {
            String prodId      = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            String prodName    = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String catId       = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_ID));

            Product product = new Product(prodId, prodName, catId);
            products.add(product);
        }
        cursor.close();
        return products;
    }
}
