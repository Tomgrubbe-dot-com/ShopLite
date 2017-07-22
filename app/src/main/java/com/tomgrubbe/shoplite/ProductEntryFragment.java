package com.tomgrubbe.shoplite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tomgrubbe.shoplite.database.ProductsTable;
import com.tomgrubbe.shoplite.database.SelectedItemsTable;
import com.tomgrubbe.shoplite.model.Product;
import com.tomgrubbe.shoplite.model.SelectedItem;

import java.util.ArrayList;
import java.util.List;

//import android.app.Fragment;

/**
 * Allows the user to select/add a product to the list
 */
public class ProductEntryFragment extends Fragment {

    private ProductEntryListener mListener;
    private List<String> allProducts = new ArrayList<>();

    private AutoCompleteTextView productText;
    private Button productAddFromList;
    private Button productAdd;

    public ProductEntryFragment() {
    }


    public static ProductEntryFragment newInstance() {
        return new ProductEntryFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_entry, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        productText = (AutoCompleteTextView) getView().findViewById(R.id.productText);
        productAddFromList = (Button) getView().findViewById(R.id.productAddFromList);
        productAdd = (Button) getView().findViewById(R.id.productAdd);

        getAllProducts();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, allProducts);
        productText.setThreshold(1);
        productText.setAdapter(adapter);

        productText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String productName = parent.getItemAtPosition(position).toString();
                if (!isAlreadySelected(productName)) {
                    selectProduct(productName);
                }
            }
        });

        productText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Enter pressed
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        productText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    // Done pressed

                    String productName = productText.getText().toString();
                    if (!productName.isEmpty()) {
                        if (!isAlreadySelected(productName)) {
                            selectProduct(productName);
                        }
                    }
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        productAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = productText.getText().toString();
                if (!productName.isEmpty()) {
                    if (!isAlreadySelected(productName)) {
                        selectProduct(productName);
                    }
                } else {
                    Toast.makeText((Context) mListener, "Nothing to add.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        productAddFromList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent((Context)mListener, com.tomgrubbe.shoplite.ProductListActivity.class);
                intent.putExtra(ProductListActivity.PRODUCT_LIST_TITLE, "Select Product from List");
                startActivityForResult(intent, ProductListActivity.PRODUCT_LIST_RESULT);
            }
        });
    }

    private boolean isAlreadySelected(String name) {
        Context context = getActivity();
        SelectedItemsTable sit = new SelectedItemsTable(context);
        List<SelectedItem> selectedList = sit.getAllSelectedItems();

        for (SelectedItem item : selectedList) {
            String productName = item.getSelectedItemName(context);
            if (productName != null) {
                if (productName.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void selectProduct(String productName) {
        productText.setText("");

        hideKeyboard();

        if (mListener != null) {
            mListener.OnProductAdded(productName);
            ProductsTable pt = ((MainActivity) getActivity()).getProductTable();
            Product prod = pt.getProductFromName(productName);
            // Add new product to master list
            if (prod == null) {
                allProducts.add(productName);
            }
        }
        showKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(productText, InputMethodManager.SHOW_IMPLICIT);
        productText.requestFocus();
    }

    private void getAllProducts() {
        ProductsTable productsTable = ((MainActivity) getActivity()).getProductTable();
        List<Product> products = productsTable.getAllProducts();

        for (Product product : products) {
            allProducts.add(product.getName());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        productText = null;
        productAdd = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProductEntryListener) {
            mListener = (ProductEntryListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ProductEntryListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface ProductEntryListener {

        void OnProductAdded(String product);
    }
}
