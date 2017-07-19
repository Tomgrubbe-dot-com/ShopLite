package com.tomgrubbe.shoplite.model;


import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.tomgrubbe.shoplite.database.ProductsTable;

import java.util.UUID;

public class Product implements Parcelable {

    private java.lang.String productId;
    private java.lang.String name;
    private java.lang.String categoryId;


    public Product(String id, String name, String categoryId)    {

        this.productId = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.categoryId = categoryId;
    }

    public java.lang.String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(ProductsTable.COLUMN_ID, productId);
        values.put(ProductsTable.COLUMN_NAME, name);
        values.put(ProductsTable.COLUMN_CATEGORY_ID, categoryId);

        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productId);
        dest.writeString(this.name);
        dest.writeString(this.categoryId);
    }

    private Product(Parcel in)   {
        this.productId = in.readString();
        this.name = in.readString();
        this.categoryId = in.readString();
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                '}';
    }
}
