package com.tomgrubbe.shoplite;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.tomgrubbe.shoplite.database.ProductsTable;
import com.tomgrubbe.shoplite.database.RemovedItemsTable;
import com.tomgrubbe.shoplite.model.Product;
import com.tomgrubbe.shoplite.model.RemovedItem;
import com.tomgrubbe.shoplite.model.SelectedItem;
import com.tomgrubbe.shoplite.utils.Common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RemovedItemsActivity extends AppCompatActivity implements RemovedItemsAdapter.RemovedListListener {

    public static final String REMOVED_LIST_TITLE = "RemovedListTitle";
    public static final int REMOVED_ITEMS_LIST_RESULT = 8;
    private List<RemovedItem> mItems = new ArrayList<>();
    private Context mContext;
    private RemovedItemsTable removedItemsTable;
    private RemovedItemsAdapter mAdapter;
    private RecyclerView viewItemList;
    private Button buttonSelectChecked;
    private Button buttonClearAll;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RemovedItemListener mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removed_items);

        // Should always be MainActivity(!)
        mContext = Common.Instance().getBaseActivity();
        mListener = (RemovedItemListener) mContext;


        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getStringExtra(REMOVED_LIST_TITLE);
        title = (title != null) ? title : "Recently Used";
        setTitle(title);


        buttonSelectChecked = (Button) findViewById(R.id.removedButtonSelectChecked);
        buttonClearAll = (Button) findViewById(R.id.removedButtonCLear);

        removedItemsTable = new RemovedItemsTable(mContext);
        mItems = removedItemsTable.getAllRemovedItems();
        viewItemList = (RecyclerView) findViewById(R.id.removedItemsList);

        mAdapter = new RemovedItemsAdapter(this, mItems);
        viewItemList.setAdapter(mAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.removed_swipe_container);


        buttonSelectChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkedItemCount() == 0)    {
                    Toast.makeText(mContext, "Nothing checked.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ProductsTable pt = new ProductsTable(mContext);
                List<Product> checkedProducts = new ArrayList<>();

                // Used Iterator instead if for loop because removing items while iterating them
                // causes a ConcurrentModificationException exception.
                Iterator<RemovedItem> iter = mItems.iterator();
                while (iter.hasNext())   {
                    RemovedItem ri = iter.next();
                    if (ri.isChecked()) {
                        Product product =  pt.getProductFromId(ri.getProductId());
                        checkedProducts.add(product);
                        iter.remove();
                        removeRemovedItem(ri);
                    }
                }

                mListener.OnSelectRemovedItems(checkedProducts);
                finish();
            }
        });



        buttonClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removedItemsTable.deleteAll();
                mItems.clear();
                mAdapter.notifyDataSetChanged();
                finish();
            }
        });

        initSwipe();
    }

    private int checkedItemCount()  {
        int count = 0;

        for (RemovedItem ri : mItems)   {
            if (ri.isChecked())
                count++;
        }
        return count;
    }

    private void removeRemovedItem(RemovedItem item)    {
        removedItemsTable.deleteItem(item);
        mAdapter.notifyDataSetChanged();
    }

    private void selectedRemovedItem(RemovedItem ri)    {
        ProductsTable pt = new ProductsTable(mContext);
        Product p = pt.getProductFromId(ri.getProductId());
        mListener.OnSelectRemovedItem(p);
    }

    @Override
    public void OnAddRemovedItemBack(Product product) {
        RemovedItem item = removedItemsTable.fromProductId(product.getProductId());

        int position = getRemovedItemIndex(item.getRemovedItemId());

        selectedRemovedItem(item);
        removeRemovedItem(item);

        mItems.remove(position);
        mAdapter.notifyItemRemoved(position);

        // TODO: This notification only happens when this activity is closed.  So create a listener/interface
        //       and fire an event instead.
//        setResult(MainActivity.PRODUCT_ADD, getIntent().putExtra(MainActivity.PRODUCT_ADD_DATA, product));
//        finish();

    }

    private int getRemovedItemIndex(String itemID)  {
        int res = -1;
        for (int i=0; i<mItems.size(); i++) {
            RemovedItem item = mItems.get(i);
            if (itemID.equals(item.getRemovedItemId())) {
                return i;
            }
        }
        return res;
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {

                    RemovedItem item = mItems.get(position);
                    selectedRemovedItem(item);
                    removeRemovedItem(item);

                    mItems.remove(position);
                    mAdapter.notifyItemRemoved(position);
                }
            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(viewItemList);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mItems.clear();
                mItems = removedItemsTable.getAllRemovedItems();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }





    interface RemovedItemListener   {
        public void OnSelectRemovedItem(Product product);
        public void OnSelectRemovedItems(List<Product> products);
    }
}
