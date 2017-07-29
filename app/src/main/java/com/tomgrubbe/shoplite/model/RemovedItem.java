package com.tomgrubbe.shoplite.model;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.tomgrubbe.shoplite.database.ProductsTable;
import com.tomgrubbe.shoplite.database.RemovedItemsTable;

import java.util.UUID;


public class RemovedItem implements Parcelable {

    private String removedItemId;
    private String productId;
    private boolean isChecked = false;
    private String date;

    public RemovedItem(String itemId, String productId, boolean is_checked, String date) {
        this.removedItemId = (itemId == null) ? UUID.randomUUID().toString() : itemId;
        this.productId = productId;
        this.isChecked = is_checked;
        this.date      = date;
    }

    private RemovedItem(Parcel in) {
        this.removedItemId = in.readString();
        this.productId = in.readString();
    }


    public String getRemovedItemId() {
        return removedItemId;
    }
    public void setRemovedItemId(String recentlyRemovedId) { this.removedItemId = recentlyRemovedId; }


    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }


    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }


    public String getRemovedItemName(Context context)  {
        String productName = null;
        if (context != null) {
            ProductsTable table = new ProductsTable(context);
            Product prod = table.getProductFromId(this.productId);
            if (prod != null) {
                productName = prod.getName();
            }
        }
        return productName;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(RemovedItemsTable.COLUMN_ID, removedItemId);
        values.put(RemovedItemsTable.COLUMN_PRODUCT_ID, productId);
        values.put(RemovedItemsTable.COLUMN_IS_CHECKED, isChecked);
        values.put(RemovedItemsTable.COLUMN_DATE_TIME, date);

        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.removedItemId);
        dest.writeString(this.productId);
        dest.writeInt((this.isChecked) ? 1 : 0);
        dest.writeString(this.date);
    }


    public static final Parcelable.Creator<RemovedItem> CREATOR = new Parcelable.Creator<RemovedItem>() {
        @Override
        public RemovedItem createFromParcel(Parcel source) {
            return new RemovedItem(source);
        }

        @Override
        public RemovedItem[] newArray(int size) {
            return new RemovedItem[size];
        }
    };
}
