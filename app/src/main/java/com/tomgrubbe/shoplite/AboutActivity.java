package com.tomgrubbe.shoplite;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        String versionNumber = BuildConfig.VERSION_NAME;

        TextView tv = (TextView) findViewById(R.id.textVersion);

        tv.setText("v" + versionNumber);
        setTitle("About " + getString(R.string.app_name));
    }
}
