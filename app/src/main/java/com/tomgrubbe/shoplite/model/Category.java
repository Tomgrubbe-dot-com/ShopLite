package com.tomgrubbe.shoplite.model;


import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.tomgrubbe.shoplite.database.CategoriesTable;

import java.util.UUID;

public class Category implements Parcelable {

    private String categoryId;
    private String categoryName;
    public static final String UNCATEGORIZED = "Uncategorized";

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category(String categoryId, String categoryName) {
        this.categoryId = (categoryId == null) ? UUID.randomUUID().toString() : categoryId;
        this.categoryName = categoryName;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(CategoriesTable.COLUMN_ID, categoryId);
        values.put(CategoriesTable.COLUMN_NAME, categoryName);

        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.categoryId);
        dest.writeString(this.categoryName);
    }

    private Category(Parcel in)   {
        this.categoryId = in.readString();
        this.categoryName = in.readString();
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public String toString() {
        return "Category{" +
                "categoryName='" + categoryName + '\'' +
                '}';
    }
}
