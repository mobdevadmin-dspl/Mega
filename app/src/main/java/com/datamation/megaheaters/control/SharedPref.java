package com.datamation.megaheaters.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPref {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public SharedPref(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setGlobalVal(String mKey, String mValue) {
        editor = prefs.edit();
        editor.putString(mKey, mValue);
        editor.commit();
    }

    public String getGlobalVal(String mKey) {
        return prefs.getString(mKey, "");
    }
  //-----------dhanushika -----------------------------------------------------------------------------------------------------------
    public String getGlobalValue(String mKey) {
        return prefs.getString(mKey, "");
    }
}
