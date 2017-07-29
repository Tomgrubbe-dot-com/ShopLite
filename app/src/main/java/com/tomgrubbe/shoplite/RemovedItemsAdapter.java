package com.tomgrubbe.shoplite;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tomgrubbe.shoplite.database.ProductsTable;
import com.tomgrubbe.shoplite.database.RemovedItemsTable;
import com.tomgrubbe.shoplite.model.Product;
import com.tomgrubbe.shoplite.model.RemovedItem;

import java.util.ArrayList;
import java.util.List;

public class RemovedItemsAdapter extends RecyclerView.Adapter<RemovedItemsAdapter.ViewHolder> {

    private List<RemovedItem> mItems = new ArrayList<>();
    private Context mContext;
    RemovedListListener mListener;

    public RemovedItemsAdapter(Context context, List<RemovedItem> list) {
        mContext = context;
        mItems = list;
        mListener = (RemovedListListener) context;
    }

    @Override
    public RemovedItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.removed_list_item, parent, false);
        return new RemovedItemsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RemovedItemsAdapter.ViewHolder holder, int position) {

        final RemovedItem item = mItems.get(position);
        final RemovedItemsTable rit = new RemovedItemsTable(mContext);
        final int pos = position;

        holder.itemText.setText(item.getRemovedItemName(mContext));

        holder.itemCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
                rit.updateRemovedItem(item);
                mItems.get(pos).setChecked(isChecked);
            }
        });

        holder.itemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)  {
                    ProductsTable pt = new ProductsTable(mContext);
                    Product product =  pt.getProductFromId(item.getProductId());
                    mListener.OnAddRemovedItemBack(product);
                }
            }
        });

        holder.itemCheck.setChecked(item.isChecked());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public final CheckBox itemCheck;
        public final TextView itemText;
        public final View view;

        public ViewHolder(View itemView) {
            super(itemView);

            itemCheck = (CheckBox) itemView.findViewById(R.id.removedItemCheck);
            itemText = (TextView) itemView.findViewById(R.id.removedItemText);
            view = itemView;
        }
    }

    // Callbacks for anyone using this generic list
    public interface RemovedListListener {
        void OnAddRemovedItemBack(Product product);
    }
}
