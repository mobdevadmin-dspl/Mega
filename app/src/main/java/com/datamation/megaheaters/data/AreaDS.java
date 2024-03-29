package com.datamation.megaheaters.data;

import java.util.ArrayList;

import com.datamation.megaheaters.model.Area;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AreaDS {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "swadeshi";

    public AreaDS(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    @SuppressWarnings("static-access")
    public int createOrUpdateArea(ArrayList<Area> list) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        Cursor cursor_ini = null;

        try {

            cursor_ini = dB.rawQuery("SELECT * FROM " + dbHelper.TABLE_FAREA, null);

            for (Area area : list) {
                ContentValues values = new ContentValues();

                values.put(dbHelper.FAREA_ADD_MACH, area.getFAREA_ADD_MACH());
                values.put(dbHelper.FAREA_ADD_USER, area.getFAREA_ADD_USER());
                values.put(dbHelper.FAREA_AREA_CODE, area.getFAREA_AREA_CODE());
                values.put(dbHelper.FAREA_AREA_NAME, area.getFAREA_AREA_NAME());
                values.put(dbHelper.FAREA_DEAL_CODE, area.getFAREA_DEAL_CODE());
                values.put(dbHelper.FAREA_REQ_CODE, area.getFAREA_REQ_CODE());


                if (cursor_ini.getCount() > 0) {
                    String selectQuery = "SELECT * FROM " + dbHelper.TABLE_FAREA + " WHERE " + dbHelper.FAREA_AREA_CODE + "='" + area.getFAREA_AREA_CODE() + "'";
                    cursor = dB.rawQuery(selectQuery, null);

                    if (cursor.getCount() > 0) {
                        count = (int) dB.update(dbHelper.TABLE_FAREA, values, dbHelper.FCOMPANYSETTING_SETTINGS_CODE + "='" + area.getFAREA_AREA_CODE() + "'", null);
                    } else {
                        count = (int) dB.insert(dbHelper.TABLE_FAREA, null, values);
                    }

                } else {
                    count = (int) dB.insert(dbHelper.TABLE_FAREA, null, values);
                }


            }
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (cursor_ini != null) {
                cursor_ini.close();
            }
            dB.close();
        }
        return count;

    }

    public int deleteAll() {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {

            cursor = dB.rawQuery("SELECT * FROM " + dbHelper.TABLE_FAREA, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(dbHelper.TABLE_FAREA, null, null);
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
