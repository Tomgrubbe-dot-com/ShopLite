package com.tomgrubbe.shoplite;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.tomgrubbe.shoplite.database.CategoriesTable;
import com.tomgrubbe.shoplite.database.DatabaseSeeder;
import com.tomgrubbe.shoplite.database.ProductsTable;
import com.tomgrubbe.shoplite.database.SelectedItemsTable;
import com.tomgrubbe.shoplite.database.TableBase;
import com.tomgrubbe.shoplite.model.Category;
import com.tomgrubbe.shoplite.model.Product;
import com.tomgrubbe.shoplite.model.SelectedItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ProductEntryFragment.ProductEntryListener,
        CheckManagementFragment.CheckManagerListener {

    private final static java.lang.String DATA_ITEMS = "DataItems";
    public final static String PRODUCT_ADD_DATA = "PRODUCT_ADD_DATA";
    public final static int PRODUCT_ADD = 2;
    public final static String PRODUCT_UPDATE_DATA = "PRODUCT_UPDATE_DATA";
    public final static int PRODUCT_UPDATE = 3;
    public final static String PRODUCT_DELETE_DATA = "PRODUCT_DELETE_DATA";
    public final static int PRODUCT_DELETE = 4;
    public static final String GLOBAL_PREFS_KEY = "shoplite_global_prefs";
    private List<SelectedItem> mDataItems = new ArrayList<>(); //generateFakeData();
    private RecyclerView recyclerView;
    private ProductEntryFragment productEntryFragment;
    private CheckManagementFragment checkManager;
    private ProductAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProductsTable productTable;
    private CategoriesTable categoriesTable;
    private SelectedItemsTable selectedItemsTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DEBUG
        //this.deleteDatabase(TableBase.DB_FILE_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        productEntryFragment = ProductEntryFragment.newInstance();
        checkManager = CheckManagementFragment.newInstance();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        productTable = new ProductsTable(this);
        categoriesTable = new CategoriesTable(this);
        selectedItemsTable = new SelectedItemsTable(this);

        // Load list of selected items (if any)
        if (productTable.getProductCount() == 0) {
            DatabaseSeeder.SeedInitialDatabase(productTable, categoriesTable);
        } else {
            getSelectedItems();
        }

        if (savedInstanceState != null) {
            mDataItems = savedInstanceState.getParcelableArrayList(DATA_ITEMS);
        }

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mAdapter = new ProductAdapter(this, mDataItems);
        recyclerView.setAdapter(mAdapter);

        // Remove initial focus
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Handle swipe-to-delete of items
        initSwipe();

        // Set default settings values
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

    @Override
    public View onCreateView(java.lang.String name, Context context, AttributeSet attrs) {
        View view = null;
        try {
            view = super.onCreateView(name, context, attrs);
        } catch (Exception e) {
            Log.e("MainActivity", "onCreateView", e);
        }
        return view;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public ProductsTable getProductTable() {
        return productTable;
    }

    public CategoriesTable getCategoriesTable() {
        return categoriesTable;
    }

    public SelectedItemsTable getSelectedItemsTable() { return selectedItemsTable; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuRemoveChecked:
                removeChecked();
                break;
            case R.id.editProducts:
                showProductList();
                break;
            case R.id.menuCheckAll:
                menuCheckUncheckAll();
                break;
            case R.id.editCategories:
                showCategoryList();
                break;
            case R.id.menuSettings:
                showSettings();
                break;
            case R.id.menuReset:
                resetDatabase();
                break;
            case R.id.menuAbout:
                aboutDialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void aboutDialog()  {
        Intent intent = new Intent(this, com.tomgrubbe.shoplite.AboutActivity.class);
        startActivity(intent);
    }

    private void showProductList()   {
        Intent intent = new Intent(this, com.tomgrubbe.shoplite.ProductListActivity.class);
        intent.putExtra(ProductListActivity.PRODUCT_LIST_TITLE, "Products");
        intent.putExtra(ProductListActivity.PRODUCT_IS_SELECTABLE, false);
        startActivityForResult(intent, ProductListActivity.PRODUCT_LIST_RESULT);
    }

    private void showCategoryList()   {
        Intent intent = new Intent(this, com.tomgrubbe.shoplite.CategoryListActivity.class);
        startActivityForResult(intent, CategoryListActivity.CATEGORY_LIST_RESULT);
    }

    private void showSettings() {
        Intent intent = new Intent(this, com.tomgrubbe.shoplite.SettingsActivity.class);
        startActivity(intent);
    }

    private void resetDatabase() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Database")
                .setMessage("Do you really want to clear the list and reset the product database to the default values?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteDatabase();
                        Toast.makeText(MainActivity.this, "Database reset", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void deleteDatabase() {
        selectedItemsTable.deleteAll();
        this.deleteDatabase(TableBase.DB_FILE_NAME);
        getSelectedItems();
        mAdapter.notifyDataSetChanged();
    }

    private void getSelectedItems() {
        mDataItems.clear();
        mDataItems.addAll(selectedItemsTable.getAllSelectedItems());
    }

    private boolean showDeletePrompt()  {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean(getString(R.string.prefs_remove_all), false);
    }

    private void removeChecked() {
        long count = 0;

        for (SelectedItem item : selectedItemsTable.getAllSelectedItems()) {
            if (item.isChecked()) {
                selectedItemsTable.deleteItem(item);
                count++;
            }
        }

        getSelectedItems();
        mAdapter.notifyDataSetChanged();

        String msg = "Removed " + Long.toString(count) + " items.";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void menuCheckUncheckAll()  {
        CheckManagementFragment frag = (CheckManagementFragment)getSupportFragmentManager().findFragmentById(R.id.checkManager);
        if (frag != null)   {
            frag.clickCheckButton(!areAllChecked());
        }
    }

    private void CheckUncheckAll(boolean checked)   {
        for (SelectedItem item : selectedItemsTable.getAllSelectedItems()) {
            item.setChecked(checked);
            selectedItemsTable.updateSelectedItem(item);
        }
        getSelectedItems();
        mAdapter.notifyDataSetChanged();
    }

    private boolean areAllChecked()    {
        int itemCount = (int)selectedItemsTable.getSelectedItemsCount();
        int numSelected = 0;
        for (SelectedItem item : selectedItemsTable.getAllSelectedItems())  {
            if (item.isChecked())   {
                numSelected++;
            }
        }
        return (numSelected == itemCount);
    }

    @Override
    public void OnCheckUncheckAll(boolean checked) {
        CheckUncheckAll(checked);
    }

    @Override
    public void OnRemoveChecked() {

        if (!showDeletePrompt())    {
            removeChecked();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        builder.setTitle("Remove Checked Items")
                .setMessage("Remove all checked items.  " + "Are you sure?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        removeChecked();
                    }
                })
                .setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideLeft;
        dialog.show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(DATA_ITEMS, new ArrayList<SelectedItem>(mAdapter.getItemList()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mDataItems = savedInstanceState.getParcelableArrayList(DATA_ITEMS);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    public void OnProductAdded(String productName) {

        // Add product to list and/or to database
        Product product = productTable.getProductFromName(productName);
        if (product == null) {
            product = new Product(null, productName, categoriesTable.findId(Category.UNCATEGORIZED));
            productTable.addProduct(product);
        }

        if (!isAlreadySelected(product.getName())) {
            SelectedItem item = new SelectedItem(null, product.getProductId());
            selectedItemsTable.addItem(item);
            mDataItems.add(item);
            mAdapter.notifyDataSetChanged();
        }
    }

    private boolean isAlreadySelected(String name) {
        List<SelectedItem> selectedList = selectedItemsTable.getAllSelectedItems();

        for (SelectedItem item : selectedList) {
            String productName = item.getSelectedItemName(this);
            if (productName != null) {
                if (productName.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case MainActivity.PRODUCT_ADD:  {
                Product product = data.getExtras().getParcelable(MainActivity.PRODUCT_ADD_DATA);
                OnProductAdded(product.getName());
                break;
            }
            case MainActivity.PRODUCT_UPDATE: {
                Product product = data.getExtras().getParcelable(MainActivity.PRODUCT_UPDATE_DATA);
                getSelectedItems();
                mAdapter.notifyDataSetChanged();
                break;
            }
            case MainActivity.PRODUCT_DELETE: {
                Product product = data.getExtras().getParcelable(MainActivity.PRODUCT_DELETE_DATA);
                if (product != null) {
                    selectedItemsTable.deleteItem(selectedItemsTable.fromProductId(product.getProductId()));
                    productTable.deleteProduct(product);
                    getSelectedItems();
                    mAdapter.notifyDataSetChanged();

                }
                break;
            }
        }
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.RIGHT) {

                    SelectedItem item = mDataItems.get(position);
                    selectedItemsTable.deleteItem(item);

                    mAdapter.notifyItemRemoved(position);
                    mDataItems.remove(position);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getSelectedItems();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
