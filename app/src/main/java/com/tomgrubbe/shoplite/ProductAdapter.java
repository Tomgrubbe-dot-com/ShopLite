package com.tomgrubbe.shoplite;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tomgrubbe.shoplite.database.ProductsTable;
import com.tomgrubbe.shoplite.database.SelectedItemsTable;
import com.tomgrubbe.shoplite.model.Product;
import com.tomgrubbe.shoplite.model.SelectedItem;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private static final String TAG = "ProductAdapter";
    public static final String ITEM_KEY = "item_key";
    private List<SelectedItem> mItems;
    private Context mContext;
    private int selectedPos = -1;

    public ProductAdapter(Context context, List<SelectedItem> items)
    {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SelectedItem selectedItem = mItems.get(position);
        final ProductsTable pt = new ProductsTable(mContext);
        final SelectedItemsTable selectedItemsTable = new SelectedItemsTable(mContext);
        final Product prod = pt.getProductFromId(selectedItem.getProductId());
        final ViewHolder viewHolder = holder;
        final int pos = position;

        String itemName = selectedItem.getSelectedItemName(mContext);
        holder.itemText.setText(itemName);

        //highlightItem(holder, position);

        holder.itemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPos = pos;
                notifyDataSetChanged();

                Intent intent = new Intent(mContext, com.tomgrubbe.shoplite.DetailActivity.class);
                intent.putExtra(ITEM_KEY, prod);
                ((Activity)mContext).startActivityForResult(intent, 3);
            }
        });

        holder.itemCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedItem.setChecked(isChecked);
                selectedItemsTable.updateSelectedItem(selectedItem);
                mItems.get(pos).setChecked(isChecked);
            }
        });

        holder.btnLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q = selectedItem.getQuantity();
                q = (q <= 1) ? 1 : q - 1;
                updateQuantity(selectedItem, selectedItemsTable, viewHolder, q);
            }
        });

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q = selectedItem.getQuantity();
                q = (q >= SelectedItem.MAX_QUANTITY) ? SelectedItem.MAX_QUANTITY : q + 1;
                updateQuantity(selectedItem, selectedItemsTable, viewHolder, q);
            }
        });

        holder.itemCheck.setChecked(selectedItem.isChecked());
        holder.textQuantity.setText(Integer.toString(selectedItem.getQuantity()));

        // TODO: FIX THIS
        //sort();
    }



    private void updateQuantity(SelectedItem item, SelectedItemsTable table, ViewHolder holder, int quantity)   {
        item.setQuantity(quantity);
        table.updateSelectedItem(item);
        holder.textQuantity.setText(Integer.toString(quantity));
        holder.view.playSoundEffect(SoundEffectConstants.CLICK);
        //Toast.makeText(mContext, "Quantity changed: " + Integer.toString(quantity), Toast.LENGTH_SHORT).show();
    }


    void highlightItem(ViewHolder holder, int position)   {
        if (selectedPos == position)
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorListItemSelectedLight));
        else
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorListItemLight));
    }

    public List<SelectedItem> getItemList()    {
        return this.mItems;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final CheckBox itemCheck;
        public final TextView itemText;
//        public final ImageView btnLess;
//        public final ImageView btnMore;
        public final Button btnLess;
        public final Button btnMore;
        public final TextView textQuantity;
        public final View view;

        public ViewHolder(View itemView) {
            super(itemView);

            itemCheck = (CheckBox) itemView.findViewById(R.id.itemCheck);
            itemText  = (TextView) itemView.findViewById(R.id.itemText);
            btnLess  = (Button) itemView.findViewById(R.id.quantityLess);
            btnMore  = (Button) itemView.findViewById(R.id.quantityMore);
            textQuantity  = (TextView) itemView.findViewById(R.id.textQuantity);
            view      = itemView;

            itemView.setClickable(true);
        }
    }
}
