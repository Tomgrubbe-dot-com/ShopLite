package com.tomgrubbe.shoplite;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tomgrubbe.shoplite.database.CategoriesTable;
import com.tomgrubbe.shoplite.database.ProductsTable;
import com.tomgrubbe.shoplite.database.SelectedItemsTable;
import com.tomgrubbe.shoplite.model.Category;
import com.tomgrubbe.shoplite.model.Product;
import com.tomgrubbe.shoplite.model.SelectedItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductListActivity extends AppCompatActivity implements StringListAdapter.StringListListener  {

    public static final String PRODUCT_LIST_TITLE = "ProductListTitle";
    public static final String PRODUCT_IS_SELECTABLE = "IsItemSelectable";
    public static final int PRODUCT_LIST_RESULT = 7;
    private EditText editItem;
    private Button buttonAdd;
    private RecyclerView viewItemList;
    private ProductsTable productsTable;
    private CategoriesTable categoriesTable;
    private List<String> productList = new ArrayList<>();
    private StringListAdapter mAdapter;
    private boolean setSelectable = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.string_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getStringExtra(PRODUCT_LIST_TITLE);
        title = (title != null) ? title : "Products";
        setTitle(title);

        setSelectable = getIntent().getBooleanExtra(PRODUCT_IS_SELECTABLE, setSelectable);

        editItem = (EditText) findViewById(R.id.editNewItem);
        buttonAdd = (Button) findViewById(R.id.buttonAddNew);
        viewItemList = (RecyclerView) findViewById(R.id.recyclerViewStringList);

        productsTable = new ProductsTable(this);
        categoriesTable = new CategoriesTable(this);
        productList = getProductNames();

        mAdapter = new StringListAdapter(this, productList);
        viewItemList.setAdapter(mAdapter);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add new product
                String text = editItem.getText().toString();

                if (TextUtils.isEmpty(text))    {
                    Toast.makeText(ProductListActivity.this, "Type a new product name and click +Add New.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!productsTable.exists(text))   {
                    hideKeyboard();
                    editItem.setText("");

                    Product newProd = new Product(null, text, categoriesTable.findId(Category.UNCATEGORIZED));
                    productsTable.addProduct(newProd);
                    productList.add(text);
                    Collections.sort(productList);
                    mAdapter.notifyDataSetChanged();
                }
                else    {
                    Toast.makeText(ProductListActivity.this, "Product " + text + " already exists.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private List<String> getProductNames() {
        productList.clear();
        List<String> productNames = new ArrayList<>();
        List<Product> prods = productsTable.getAllProducts();

        for (Product prod : prods)   {
            productNames.add(prod.getName());
        }
        return productNames;
    }


    @Override
    public void OnDeleteItem(String itemName, int position) {
        final String productName = itemName;
        final int pos = position;

        if (itemName.equalsIgnoreCase(Category.UNCATEGORIZED))
            return;

        if (!showDeletePrompt())    {
            Product product = productsTable.getProductFromName(productName);
            deleteProduct(product, pos);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        builder.setTitle("Delete Product")
                .setMessage("Delete product " + itemName + ".  " + "Are you sure?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Product product = productsTable.getProductFromName(productName);
                        deleteProduct(product, pos);
                    }
                })
                .setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideLeft;
        dialog.show();
    }

    private boolean showDeletePrompt()  {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean(getString(R.string.prefs_delete_product), true);
    }

    private void deleteProduct(Product product, int position)   {
        if (product != null)   {
            productsTable.deleteProduct(product);
            productList.remove(position);
            deleteProductFromSelectedItems(product);
            mAdapter.notifyItemRemoved(position);

            Toast.makeText(this, "Product " + product.getName() + " removed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProductFromSelectedItems(Product product)    {
        SelectedItemsTable sit = new SelectedItemsTable(this);
        SelectedItem item = sit.fromProductId(product.getProductId());
        if (item != null)   {
            sit.deleteItem(item);
        }
    }

    @Override
    public void OnProductSelected(String itemName) {

        if (!setSelectable)  {
            return;
        }

        Product product = productsTable.getProductFromName(itemName);
        if (product != null) {
            setResult(MainActivity.PRODUCT_ADD, getIntent().putExtra(MainActivity.PRODUCT_ADD_DATA, product));
            finish();

            //Toast.makeText(this, "Product " + itemName + " selected.", Toast.LENGTH_SHORT).show();
        }
    }
}
