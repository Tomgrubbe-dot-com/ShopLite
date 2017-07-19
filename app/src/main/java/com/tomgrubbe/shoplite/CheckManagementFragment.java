package com.tomgrubbe.shoplite;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tomgrubbe.shoplite.database.SelectedItemsTable;
import com.tomgrubbe.shoplite.model.SelectedItem;


public class CheckManagementFragment extends Fragment {


    private CheckBox checkUncheckAll;
    private Button buttonRemoveChecked;
    private CheckManagerListener mListener;

    public CheckManagementFragment() {
    }


    public static CheckManagementFragment newInstance() {
        return new CheckManagementFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check_management, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        checkUncheckAll = (CheckBox) getView().findViewById(R.id.checkUncheckAll);
        buttonRemoveChecked = (Button) getView().findViewById(R.id.buttonDeleteChecked);

        checkUncheckAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Toast.makeText(getContext(), "Check/Uncheck All : " + isChecked, Toast.LENGTH_SHORT).show();
                mListener.OnCheckUncheckAll(isChecked);
            }
        });

        buttonRemoveChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Remove Checked", Toast.LENGTH_SHORT).show();
                mListener.OnRemoveChecked();
            }
        });

        checkAllButton();
    }

    public void clickCheckButton(boolean checked)   {
        checkUncheckAll.setChecked(checked);
    }

    public void checkAllButton()   {
        SelectedItemsTable sit = new SelectedItemsTable(getContext());

        int itemCount = (int)sit.getSelectedItemsCount();
        int numSelected = 0;
        for (SelectedItem item : sit.getAllSelectedItems()) {
            if (item.isChecked())   {
                numSelected++;
            }
        }

        checkUncheckAll.setChecked(numSelected == itemCount);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        checkUncheckAll = null;
        buttonRemoveChecked = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CheckManagementFragment.CheckManagerListener) {
            mListener = (CheckManagementFragment.CheckManagerListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement CheckManagerListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface CheckManagerListener {

        void OnCheckUncheckAll(boolean checked);
        void OnRemoveChecked();
    }


}
