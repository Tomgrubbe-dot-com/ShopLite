package com.tomgrubbe.shoplite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;


public class StringListAdapter extends RecyclerView.Adapter<StringListAdapter.ViewHolder> {

    private static final int STRING_LIST_ADD = 4;
    private static final int STRING_LIST_DELETE = 5;
    private StringListListener mListener;
    private Context mContext;
    private List<String> mItems;

    public StringListAdapter(Context context, List<String> items)
    {
        this.mContext = context;
        this.mItems = items;
        this.mListener = (StringListListener) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.string_list_item, parent, false);
        return new StringListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String item = mItems.get(position);
        final int pos = position;

        holder.itemText.setText(item);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)  {
                    mListener.OnProductSelected(item);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)  {
                    mListener.OnDeleteItem(item, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView itemText;
        public final Button btnDelete;
        public final View view;

        public ViewHolder(View itemView) {
            super(itemView);

            itemText = (TextView) itemView.findViewById(R.id.textListItem);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
            view = itemView;
        }
    }

    // Callbacks for anyone using this generic list
    public interface StringListListener {
        void OnDeleteItem(String itemName, int position);
        void OnProductSelected(String itemName);
    }

}
