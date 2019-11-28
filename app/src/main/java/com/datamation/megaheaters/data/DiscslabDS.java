package com.datamation.megaheaters.data;

import java.util.ArrayList;

import com.datamation.megaheaters.model.Discslab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DiscslabDS {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "swadeshi";

    public DiscslabDS(Context context) {

        this.context = context;
        dbHelper = new DatabaseHelper(context);

    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    @SuppressWarnings("static-access")
    public int createOrUpdateDiscslab(ArrayList<Discslab> list) {
        int count = 0;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;

        try {

            for (Discslab discslab : list) {

                ContentValues values = new ContentValues();

                values.put(dbHelper.FDISCSLAB_REF_NO, discslab.getFDISCSLAB_REF_NO());
                values.put(dbHelper.FDISCSLAB_SEQ_NO, discslab.getFDISCSLAB_SEQ_NO());
                values.put(dbHelper.FDISCSLAB_QTY_F, discslab.getFDISCSLAB_QTY_F());
                values.put(dbHelper.FDISCSLAB_QTY_T, discslab.getFDISCSLAB_QTY_T());
                values.put(dbHelper.FDISCSLAB_DIS_PER, discslab.getFDISCSLAB_DIS_PER());
                values.put(dbHelper.FDISCSLAB_DIS_AMUT, discslab.getFDISCSLAB_DIS_AMUT());
                values.put(dbHelper.FDISCSLAB_RECORD_ID, discslab.getFDISCSLAB_RECORD_ID());
                values.put(dbHelper.FDISCSLAB_TIMESTAMP_COLUMN, discslab.getFDISCSLAB_TIMESTAMP_COLUMN());

                count = (int) dB.insert(dbHelper.TABLE_FDISCSLAB, null, values);

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return count;
    }

    @SuppressWarnings("static-access")
    public int deleteAll() {
        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {

            int success = dB.delete(dbHelper.TABLE_FDISCSLAB, null, null);
            Log.v("Success", success + "");
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

    public Discslab getDiscountSlabInfo(String refno, int tQty) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Discslab discSlab = new Discslab();

        String selectQuery = "select * from fdiscslab where refno='" + refno + "' and " + tQty + " between CAST(Qtyf as double) and CAST(Qtyt as double)";

        Cursor cursor = dB.rawQuery(selectQuery, null);
        try {
            while (cursor.moveToNext()) {

                discSlab.setFDISCSLAB_ID(cursor.getString(cursor.getColumnIndex(dbHelper.FDISCSLAB_ID)));
                discSlab.setFDISCSLAB_REF_NO(cursor.getString(cursor.getColumnIndex(dbHelper.FDISCSLAB_REF_NO)));
                discSlab.setFDISCSLAB_QTY_F(cursor.getString(cursor.getColumnIndex(dbHelper.FDISCSLAB_QTY_F)));
                discSlab.setFDISCSLAB_QTY_T(cursor.getString(cursor.getColumnIndex(dbHelper.FDISCSLAB_QTY_T)));
                discSlab.setFDISCSLAB_SEQ_NO(cursor.getString(cursor.getColumnIndex(dbHelper.FDISCSLAB_SEQ_NO)));
                discSlab.setFDISCSLAB_DIS_PER(cursor.getString(cursor.getColumnIndex(dbHelper.FDISCSLAB_DIS_PER)));
                discSlab.setFDISCSLAB_DIS_AMUT(cursor.getString(cursor.getColumnIndex(dbHelper.FDISCSLAB_DIS_AMUT)));
                discSlab.setFDISCSLAB_RECORD_ID(cursor.getString(cursor.getColumnIndex(dbHelper.FDISCSLAB_RECORD_ID)));
                discSlab.setFDISCSLAB_TIMESTAMP_COLUMN(cursor.getString(cursor.getColumnIndex(dbHelper.FDISCSLAB_TIMESTAMP_COLUMN)));

            }
        } catch (Exception e) {
            Log.v(TAG + " Exception", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return discSlab;
    }

}
