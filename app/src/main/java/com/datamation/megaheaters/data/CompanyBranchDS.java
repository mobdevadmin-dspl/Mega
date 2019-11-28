package com.datamation.megaheaters.data;

import java.util.ArrayList;
import java.util.Calendar;

import com.datamation.megaheaters.model.CompanyBranch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CompanyBranchDS {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "CompanyBranchDS ";

    public CompanyBranchDS(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int createOrUpdateFCompanyBranch(ArrayList<CompanyBranch> list) {

        int count = 0;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            for (CompanyBranch branch : list) {

                Cursor cursor = dB.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_FCOMPANYBRANCH + " WHERE " + DatabaseHelper.FCOMPANYBRANCH_CSETTINGS_CODE + "='" + branch.getFCOMPANYBRANCH_CSETTINGS_CODE() + "' AND " + DatabaseHelper.FCOMPANYBRANCH_BRANCH_CODE + "='" + branch.getFCOMPANYBRANCH_BRANCH_CODE() + "' AND nYear='" + branch.getNYEAR() + "' AND nMonth='" + branch.getNMONTH() + "'", null);

                ContentValues values = new ContentValues();

                values.put(DatabaseHelper.FCOMPANYBRANCH_BRANCH_CODE, branch.getFCOMPANYBRANCH_BRANCH_CODE());
                values.put(DatabaseHelper.FCOMPANYBRANCH_RECORD_ID, "");
                values.put(DatabaseHelper.FCOMPANYBRANCH_CSETTINGS_CODE, branch.getFCOMPANYBRANCH_CSETTINGS_CODE());
                values.put(DatabaseHelper.FCOMPANYBRANCH_NNUM_VAL, branch.getFCOMPANYBRANCH_NNUM_VAL());
                values.put(DatabaseHelper.FCOMPANYBRANCH_YEAR, branch.getNYEAR());
                values.put(DatabaseHelper.FCOMPANYBRANCH_MONTH, branch.getNMONTH());

                if (cursor.getCount() > 0) {
                    dB.update(DatabaseHelper.TABLE_FCOMPANYBRANCH, values, DatabaseHelper.FCOMPANYBRANCH_CSETTINGS_CODE + "=? AND " + DatabaseHelper.FCOMPANYBRANCH_BRANCH_CODE + "=? AND " + DatabaseHelper.FCOMPANYBRANCH_YEAR + "=? AND " + DatabaseHelper.FCOMPANYBRANCH_MONTH + "=?", new String[]{branch.getFCOMPANYBRANCH_CSETTINGS_CODE().toString(), branch.getFCOMPANYBRANCH_BRANCH_CODE().toString(), branch.getNYEAR().toString(), branch.getNMONTH().toString()});
                    Log.v(TAG, "Updated");
                } else {
                    count = (int) dB.insert(DatabaseHelper.TABLE_FCOMPANYBRANCH, null, values);
                    Log.v(TAG, "Inserted" + count);
                }
                cursor.close();
            }

        } catch (Exception e) {
            Log.v(TAG + " Exception", e.toString());
        } finally {
            dB.close();
        }
        return count;

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public String getCurrentNextNumVal(String cSettingsCode) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Calendar c = Calendar.getInstance();

        String selectQuery = "SELECT * FROM " + dbHelper.TABLE_FCOMPANYBRANCH + " WHERE " + dbHelper.FCOMPANYBRANCH_BRANCH_CODE + "='" + new SalRepDS(context).getCurrentRepCode() + "' AND " + dbHelper.FCOMPANYBRANCH_CSETTINGS_CODE + "='" + cSettingsCode + "' AND nYear='" + String.valueOf(c.get(Calendar.YEAR)) + "' AND nMonth='" + String.valueOf(c.get(Calendar.MONTH) + 1) + "'";

        Cursor cursor = dB.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {

            return cursor.getString(cursor.getColumnIndex(DatabaseHelper.FCOMPANYBRANCH_NNUM_VAL));

        }

        return "0";
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public int deleteAll() {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {

            cursor = dB.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_FCOMPANYBRANCH, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(DatabaseHelper.TABLE_FCOMPANYBRANCH, null, null);
                Log.v("Success", success + "");
            }
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return count;

    }

}
