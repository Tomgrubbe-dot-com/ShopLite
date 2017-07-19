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
import com.tomgrubbe.shoplite.model.Category;
import com.tomgrubbe.shoplite.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CategoryListActivity extends AppCompatActivity implements StringListAdapter.StringListListener {

    public static final String CATEGORY_LIST_TITLE = "ProductListTitle";
    public static final int CATEGORY_LIST_RESULT = 6;
    private EditText editItem;
    private Button buttonAdd;
    private RecyclerView viewItemList;
    private CategoriesTable categoriesTable;
    private List<String> categoryList = new ArrayList<>();
    private StringListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.string_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getStringExtra(CATEGORY_LIST_TITLE);
        title = (title != null) ? title : "Categories";
        setTitle(title);

        editItem = (EditText) findViewById(R.id.editNewItem);
        buttonAdd = (Button) findViewById(R.id.buttonAddNew);
        viewItemList = (RecyclerView) findViewById(R.id.recyclerViewStringList);

        categoriesTable = new CategoriesTable(this);
        categoryList = getCategoryNames();

        mAdapter = new StringListAdapter(this, categoryList);
        viewItemList.setAdapter(mAdapter);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add new category
                String text = editItem.getText().toString();

                if (TextUtils.isEmpty(text))    {
                    Toast.makeText(CategoryListActivity.this, "Type a new category name and click +Add New.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!categoriesTable.exists(text))   {
                    hideKeyboard();
                    editItem.setText("");
                    Category newCat = new Category(null, text);
                    categoriesTable.addCategory(newCat);
                    categoryList.add(text);
                    Collections.sort(categoryList);
                    mAdapter.notifyDataSetChanged();
                }
                else    {
                    Toast.makeText(CategoryListActivity.this, "Category " + text + " already exists.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    private List<String> getCategoryNames() {
        categoryList.clear();
        List<String> categoryNames = new ArrayList<>();
        List<Category> cats = categoriesTable.getAllCategories();

        for (Category cat : cats)   {
            categoryNames.add(cat.getCategoryName());
        }
        return categoryNames;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void OnDeleteItem(String itemName, int position) {
        final String categoryName = itemName;
        final int pos = position;

        if (itemName.equalsIgnoreCase(Category.UNCATEGORIZED))
            return;

        if (!showDeletePrompt())    {
            Category category = categoriesTable.getCategoryFromName(categoryName);
            deleteCategory(category, pos);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        builder.setTitle("Delete Category")
                .setMessage("Delete category " + itemName + ".  " + "Are you sure?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Category category = categoriesTable.getCategoryFromName(categoryName);
                        deleteCategory(category, pos);
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
        return prefs.getBoolean(getString(R.string.prefs_delete_category), true);
    }

    private void deleteCategory(Category category, int position)   {
        if (category != null)   {
            resetProductsToUncategorized(category);  // Don't forget this

            categoriesTable.deleteCategory(category);
            categoryList.remove(position);
            mAdapter.notifyItemRemoved(position);

            Toast.makeText(this, "Category " + category.getCategoryName() + " removed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnProductSelected(String itemName) {

    }

    // We deleted this category so reset all items in this category to "Uncategorized"
    private void resetProductsToUncategorized(Category droppedCategory) {
        ProductsTable pt = new ProductsTable(this);
        List<Product> products = pt.getProductFromCategory(droppedCategory);
        String catIdUncategorized = categoriesTable.findId(Category.UNCATEGORIZED);

        for (Product product : products)    {
            product.setCategoryId(catIdUncategorized);
            pt.updateProduct(product);
        }
    }
}

