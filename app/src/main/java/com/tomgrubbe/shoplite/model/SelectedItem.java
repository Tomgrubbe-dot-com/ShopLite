package com.tomgrubbe.shoplite.model;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.tomgrubbe.shoplite.MainActivity;
import com.tomgrubbe.shoplite.database.ProductsTable;
import com.tomgrubbe.shoplite.database.SelectedItemsTable;

import java.util.UUID;

public class SelectedItem implements Parcelable {
    private String selectedItemId;
    private String productId;
    private boolean isChecked = false;


    public SelectedItem(String itemId, String productId) {
        this(itemId, productId, false);
    }

    public SelectedItem(String itemId, String productId, boolean isChecked) {
        this.selectedItemId = (itemId == null) ? UUID.randomUUID().toString() : itemId;
        this.productId = productId;
        this.isChecked = isChecked;
    }


    private SelectedItem(Parcel in) {
        this.selectedItemId = in.readString();
        this.productId = in.readString();
        this.isChecked = (in.readInt() != 0);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(String selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSelectedItemName (Context context)  {
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

        values.put(SelectedItemsTable.COLUMN_ID, selectedItemId);
        values.put(SelectedItemsTable.COLUMN_PRODUCT_ID, productId);
        values.put(SelectedItemsTable.COLUMN_IS_CHECKED, isChecked);

        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.selectedItemId);
        dest.writeString(this.productId);
        dest.writeInt((this.isChecked) ? 1 : 0);
    }

    public static final Parcelable.Creator<SelectedItem> CREATOR = new Parcelable.Creator<SelectedItem>() {
        @Override
        public SelectedItem createFromParcel(Parcel source) {
            return new SelectedItem(source);
        }

        @Override
        public SelectedItem[] newArray(int size) {
            return new SelectedItem[size];
        }
    };
}
