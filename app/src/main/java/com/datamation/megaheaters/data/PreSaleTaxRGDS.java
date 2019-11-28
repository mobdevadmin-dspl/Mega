package com.datamation.megaheaters.data;

import java.util.ArrayList;

import com.datamation.megaheaters.model.TaxDet;
import com.datamation.megaheaters.model.TaxRG;
import com.datamation.megaheaters.model.TranSODet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PreSaleTaxRGDS {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "PreSaleTaxRGDS ";

    public PreSaleTaxRGDS(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int UpdateSalesTaxRG(ArrayList<TranSODet> list) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            Cursor cursor = null;
            for (TranSODet ordDet : list) {

                if (ordDet.getFTRANSODET_TYPE().equals("SA")) {

                    ArrayList<TaxDet> taxcodelist = new TaxDetDS(context).getTaxInfoByComCode(ordDet.getFTRANSODET_TAXCOMCODE());

                    for (TaxDet taxDet : taxcodelist) {

                        String s = "SELECT * FROM " + DatabaseHelper.TABLE_PRETAXRG + " WHERE " + DatabaseHelper.PRETAXRG_REFNO + "='" + ordDet.getFTRANSODET_REFNO() + "' AND " + DatabaseHelper.PRETAXRG_TAXCODE + "='" + taxDet.getTAXCODE() + "'";

                        cursor = dB.rawQuery(s, null);

                        ContentValues values = new ContentValues();
                        values.put(DatabaseHelper.PRETAXRG_REFNO, ordDet.getFTRANSODET_REFNO());
                        values.put(DatabaseHelper.PRETAXRG_RGNO, new TaxDS(context).getTaxRGNo(taxDet.getTAXCODE()));
                        values.put(DatabaseHelper.PRETAXRG_TAXCODE, taxDet.getTAXCODE());

                        if (cursor.getCount() <= 0)
                            count = (int) dB.insert(DatabaseHelper.TABLE_PRETAXRG, null, values);

                    }
                }
            }
            cursor.close();
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            dB.close();
        }
        return count;

    }

    public ArrayList<TaxRG> getAllTaxRG(String RefNo) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<TaxRG> list = new ArrayList<TaxRG>();
        try {
            String selectQuery = "select * from " + DatabaseHelper.TABLE_PRETAXRG + " WHERE RefNo='" + RefNo + "'";

            Cursor cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {
                TaxRG tax = new TaxRG();

                tax.setREFNO(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRETAXRG_REFNO)));
                tax.setTAXCODE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRETAXRG_TAXCODE)));
                tax.setRGNO(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PRETAXRG_RGNO)));
                list.add(tax);
            }
            cursor.close();

        } catch (Exception e) {
            Log.v("Erorr ", e.toString());

        } finally {
            dB.close();
        }

        return list;

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void ClearTable(String RefNo) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {

            dB.delete(DatabaseHelper.TABLE_PRETAXRG, DatabaseHelper.PRETAXRG_REFNO + "='" + RefNo + "'", null);
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            dB.close();
        }

    }

}
