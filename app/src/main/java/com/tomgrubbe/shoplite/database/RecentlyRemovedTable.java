package com.tomgrubbe.shoplite.database;


public class ProductCategoriesTable {
    public static final String TABLE_NAME = "ProductCategories";
    public static final String COLUMN_PRODUCT_CATEGORY_ID = "ProductCategoryId";
    public static final String COLUMN_PRODUCT_ID = "ProductId";
    public static final String COLUMN_CATEGORY_ID = "CategoryId";

    public static final String[] ALL_COLUMNS =
            { COLUMN_PRODUCT_CATEGORY_ID, COLUMN_PRODUCT_ID, COLUMN_CATEGORY_ID };

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_PRODUCT_CATEGORY_ID + " TEXT PRIMARY KEY," +
                    COLUMN_PRODUCT_ID + " TEXT," +
                    COLUMN_CATEGORY_ID + " TEXT" + ");";

    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_NAME;
}

