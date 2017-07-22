package com.tomgrubbe.shoplite;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tomgrubbe.shoplite.database.CategoriesTable;
import com.tomgrubbe.shoplite.database.ProductsTable;
import com.tomgrubbe.shoplite.database.SelectedItemsTable;
import com.tomgrubbe.shoplite.model.Category;
import com.tomgrubbe.shoplite.model.Product;
import com.tomgrubbe.shoplite.model.SelectedItem;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private TextView textName;
    private Spinner spinCategories;
    private Button btnAddCategory;
    private Spinner spinQuantities;
    private List<String> mCategoriesList = new ArrayList<>();
    private Product mProduct;
    private final Context mContext = this;
    private List<Integer> mQuantitiesList = new ArrayList<>();
    private int quantity = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        textName = (TextView) findViewById(R.id.textName);
        spinCategories = (Spinner) findViewById(R.id.textCategory);
        btnAddCategory = (Button) findViewById(R.id.buttonCreateCategory);
        spinQuantities = (Spinner) findViewById(R.id.spinQuantity);

        mProduct = getIntent().getExtras().getParcelable(ProductAdapter.ITEM_KEY);
        if (mProduct == null) {
            throw new AssertionError("Null data item received!");
        }

        CategoriesTable categories = new CategoriesTable(this);
        Category category = categories.getCategoryFromId(mProduct.getCategoryId());
        textName.setText(mProduct.getName());

        mCategoriesList = getCategoriesList();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, mCategoriesList);

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
        spinCategories.setAdapter(adapter);
        selectCategory(category.getCategoryName());

        mQuantitiesList =  getQuantitiesList();
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_spinner_item, mQuantitiesList);

        spinQuantities.setAdapter(adapter2);
        SelectedItemsTable sit = new SelectedItemsTable(this);
        SelectedItem item = sit.fromProductId(mProduct.getProductId());
        spinQuantities.setSelection(item.getQuantity() - 1);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

//        mContext = context;
//        mListener = (ProductUpdateListener)context;   // WRONG.  GET THIS ANOTHER WAY

        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuProductSave:
                saveProduct();
                break;
            case R.id.menuProductDelete:
                deleteProduct();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {
        String cat = spinCategories.getSelectedItem().toString();
        final String name = textName.getText().toString();

        CategoriesTable categories = new CategoriesTable(this);
        final ProductsTable pt = new ProductsTable(this);
        final String catId = categories.findId(cat);

        // If user edited the original name, ask to overwrite existing item if it exists
        if (!mProduct.getName().equalsIgnoreCase(name) && pt.exists(name)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Product Exists");

            dialog.setMessage("The product " + name + " already exists overwite?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User clicked "Yes" so delete original item and replace it with this one
                            pt.deleteProduct(pt.getProductFromName(name));

                            mProduct.setCategoryId(catId);
                            mProduct.setName(name);
                            pt.addProduct(mProduct);
                            setResult(MainActivity.PRODUCT_UPDATE, getIntent().putExtra(MainActivity.PRODUCT_UPDATE_DATA, mProduct));
                            DetailActivity.this.finish();
                        }
                    });

            // create Alert dialog
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();

        } else {
            // Update existing item
            mProduct.setCategoryId(catId);
            mProduct.setName(name);

            pt.updateProduct(mProduct);

            SelectedItemsTable sit = new SelectedItemsTable(this);
            SelectedItem item = sit.fromProductId(mProduct.getProductId());
            int quantity = spinQuantities.getSelectedItemPosition() + 1;
            item.setQuantity(quantity);
            sit.updateSelectedItem(item);
            setResult(MainActivity.PRODUCT_UPDATE, getIntent().putExtra(MainActivity.PRODUCT_UPDATE_DATA, mProduct));
            DetailActivity.this.finish();
        }


    }

    private void deleteProduct()    {
        final String productName = mProduct.getName();
        String productId = mProduct.getProductId();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        builder.setTitle("Delete Product")
                .setMessage("Delete " + productName + ".  " + "Are you sure?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        setResult(MainActivity.PRODUCT_DELETE, getIntent().putExtra(MainActivity.PRODUCT_DELETE_DATA, mProduct));
                        Toast.makeText(mContext, "Product " + productName + " deleted.", Toast.LENGTH_SHORT).show();
                        DetailActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideUp;
        dialog.show();
    }

    private List<String> getCategoriesList() {
        List<String> categoriesStringList = new ArrayList<>();
        CategoriesTable categoriesTable = new CategoriesTable(this);
        List<Category> categoriesList = categoriesTable.getAllCategories();

        for (Category cat : categoriesList) {
            categoriesStringList.add(cat.getCategoryName());
        }
        return categoriesStringList;
    }
    private List<Integer> getQuantitiesList() {
        List<Integer> list = new ArrayList<>();
        for (int i=1; i<=SelectedItem.MAX_QUANTITY; i++) {
            list.add(i);
        }
        return list;
    }

    private void selectCategory(String category) {
        for (int i = 0; i < mCategoriesList.size(); i++) {
            String cat = mCategoriesList.get(i);
            if (cat.equals(category)) {
                spinCategories.setSelection(i);
                return;
            }
        }
    }

    public void OnNewCategory(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View viewPrompt = layoutInflater.inflate(R.layout.text_prompt, null);
        TextView tv = (TextView)viewPrompt.findViewById(R.id.textCategoryPrompt);

        tv.setText("Enter new category name:");

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);

        builder.setView(viewPrompt);

        final EditText categoryName = (EditText)viewPrompt.findViewById(R.id.editNewCategoryName);

        builder.setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category = categoryName.getText().toString();

                        if (!TextUtils.isEmpty(category)) {
                            addNewCategory(category);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dlg = builder.create();
        dlg.getWindow().getAttributes().windowAnimations = R.style.DialogFadeIn;

        dlg.show();

    }

    private void addNewCategory(String categoryName)    {
        CategoriesTable table = new CategoriesTable(this);
        if (!table.exists(categoryName))    {
            mCategoriesList.add(categoryName);

            Category category = new Category(null, categoryName);
            table.addCategory(category);
            selectCategory(categoryName);
        } else  {
            Toast.makeText(this, "Category " + categoryName + " already exists.", Toast.LENGTH_SHORT).show();
        }
    }
}
