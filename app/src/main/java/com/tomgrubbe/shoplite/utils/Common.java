package com.tomgrubbe.shoplite.utils;


import android.app.Activity;

// Used to get easy access to the MainActivity
public class Common {
    public static Common mInstance;

    private Activity baseActivity;

    public static synchronized  Common Instance()    {
        if (mInstance == null)  {
            mInstance = new Common();
        }
        return mInstance;
    }

    public void clear() {
        mInstance = null;
    }

    public Activity getBaseActivity()   {
        return baseActivity;
    }

    public void setBaseActivity(Activity activity)  {
        baseActivity = activity;
    }
}
