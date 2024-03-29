package com.datamation.megaheaters.data;

import java.util.ArrayList;

import com.datamation.megaheaters.model.ItemPri;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class ItemPriDS {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "ItemPriDS";

    public ItemPriDS(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    @SuppressWarnings("static-access")
    public int createOrUpdateItemPri(ArrayList<ItemPri> list) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            for (ItemPri pri : list) {

                Cursor cursor = dB.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_FITEMPRI + " WHERE " + DatabaseHelper.FITEMPRI_ITEM_CODE + "='" + pri.getFITEMPRI_ITEM_CODE() + "' AND " + DatabaseHelper.FITEMPRI_PRIL_CODE + "='" + pri.getFITEMPRI_PRIL_CODE() + "'", null);

                ContentValues values = new ContentValues();

                values.put(dbHelper.FITEMPRI_ADD_MACH, pri.getFITEMPRI_ADD_MACH());
                values.put(dbHelper.FITEMPRI_ADD_USER, pri.getFITEMPRI_ADD_USER());
                values.put(dbHelper.FITEMPRI_ITEM_CODE, pri.getFITEMPRI_ITEM_CODE());
                values.put(dbHelper.FITEMPRI_PRICE, pri.getFITEMPRI_PRICE());
                values.put(dbHelper.FITEMPRI_PRIL_CODE, pri.getFITEMPRI_PRIL_CODE());
                values.put(dbHelper.FITEMPRI_SKU_CODE, pri.getFITEMPRI_SKU_CODE());
                values.put(dbHelper.FITEMPRI_TXN_MACH, pri.getFITEMPRI_TXN_MACH());
                values.put(dbHelper.FITEMPRI_TXN_USER, pri.getFITEMPRI_TXN_USER());

                if (cursor.getCount() > 0) {
                    dB.update(DatabaseHelper.TABLE_FITEMPRI, values, DatabaseHelper.FITEMPRI_ITEM_CODE + "=? AND " + DatabaseHelper.FITEMPRI_PRIL_CODE + "=?", new String[]{pri.getFITEMPRI_ITEM_CODE().toString(), pri.getFITEMPRI_PRIL_CODE().toString()});
                    Log.v("FITEMPRI : ", "Updated");
                } else {
                    count = (int) dB.insert(DatabaseHelper.TABLE_FITEMPRI, null, values);
                    Log.v("FITEMPRI : ", "Inserted " + count);
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

    public void InsertOrReplaceItemPri(ArrayList<ItemPri> list) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
            dB.beginTransactionNonExclusive();
            String sql = "INSERT OR REPLACE INTO " + DatabaseHelper.TABLE_FITEMPRI + " (AddMach,AddUser,ItemCode,Price,PrilCode,TxnMach,Txnuser,MinPrice,MaxPrice) VALUES (?,?,?,?,?,?,?,?,?)";

            SQLiteStatement stmt = dB.compileStatement(sql);

            for (ItemPri itemPri : list) {

                stmt.bindString(1, itemPri.getFITEMPRI_ADD_MACH());
                stmt.bindString(2, itemPri.getFITEMPRI_ADD_USER());
                stmt.bindString(3, itemPri.getFITEMPRI_ITEM_CODE());
                stmt.bindString(4, itemPri.getFITEMPRI_PRICE());
                stmt.bindString(5, itemPri.getFITEMPRI_PRIL_CODE());
                stmt.bindString(6, itemPri.getFITEMPRI_TXN_MACH());
                stmt.bindString(7, itemPri.getFITEMPRI_TXN_USER());
                stmt.bindString(8, itemPri.getFITEMPRI_MIN_PRICE());
                stmt.bindString(9, itemPri.getFITEMPRI_MAX_PRICE());

                stmt.execute();
                stmt.clearBindings();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dB.setTransactionSuccessful();
            dB.endTransaction();
            dB.close();
        }

    }
	
	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public int deleteAllItemPri() {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {

            cursor = dB.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_FITEMPRI, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(DatabaseHelper.TABLE_FITEMPRI, null, null);
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

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
//#^^
    public ItemPri getProductPriceDetailsByCode(String code, String prilcode) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_FITEMPRI + " WHERE " + DatabaseHelper.FITEMPRI_ITEM_CODE + "='" + code + "' AND " + DatabaseHelper.FITEMPRI_PRIL_CODE + "='" + prilcode + "'";


            cursor = dB.rawQuery(selectQuery, null);
            ItemPri priceDetails = new ItemPri();

            while (cursor.moveToNext()) {

                priceDetails.setFITEMPRI_PRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FITEMPRI_PRICE)));
                priceDetails.setFITEMPRI_MIN_PRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FITEMPRI_MIN_PRICE)));
                priceDetails.setFITEMPRI_MAX_PRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FITEMPRI_MAX_PRICE)));
               // return cursor.getString(cursor.getColumnIndex(DatabaseHelper.FITEMPRI_PRICE));
                return  priceDetails;
            }
        }catch (Exception e) {

               e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                dB.close();
            }
        return null;

    }
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public String getProductPriceByCode(String code, String prilcode) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try
        {
            String selectQuery = null,selectQueryItemPrilCode = null;


            if(prilcode.isEmpty())
            {
                selectQueryItemPrilCode = "SELECT PrilCode FROM "+DatabaseHelper.TABLE_FITEM+" WHERE ItemCode = '"+code+"' ";

                Cursor cursorItemPrilCode = dB.rawQuery(selectQueryItemPrilCode,null);

                if(cursorItemPrilCode.getCount() > 0)
                {
                    while (cursorItemPrilCode.moveToNext())
                    {
                        prilcode =  cursorItemPrilCode.getString(cursorItemPrilCode.getColumnIndex(DatabaseHelper.FITEM_PRIL_CODE));
                    }
                }

                selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_FITEMPRI + " WHERE " + DatabaseHelper.FITEMPRI_ITEM_CODE + "='" + code + "' AND " + DatabaseHelper.FITEMPRI_PRIL_CODE + "='" + prilcode + "'";
            }
            else
            {
                selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_FITEMPRI + " WHERE " + DatabaseHelper.FITEMPRI_ITEM_CODE + "='" + code + "' AND " + DatabaseHelper.FITEMPRI_PRIL_CODE + "='" + prilcode + "'";
            }




            cursor = dB.rawQuery(selectQuery, null);
            if(cursor.getCount() > 0)
            {
                while (cursor.moveToNext()) {

                    return cursor.getString(cursor.getColumnIndex(DatabaseHelper.FITEMPRI_PRICE));
                }

            }
            else
            {
                return "0.00";
            }


        }catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return "";

    }
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public String getProductMinPriceByCode(String code, String prilcode) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_FITEMPRI + " WHERE " + DatabaseHelper.FITEMPRI_ITEM_CODE + "='" + code + "' AND " + DatabaseHelper.FITEMPRI_PRIL_CODE + "='" + prilcode + "'";


            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                return cursor.getString(cursor.getColumnIndex(DatabaseHelper.FITEMPRI_MIN_PRICE));

            }
        }catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return "";

    }
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public String getProductMaxPriceByCode(String code, String prilcode) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_FITEMPRI + " WHERE " + DatabaseHelper.FITEMPRI_ITEM_CODE + "='" + code + "' AND " + DatabaseHelper.FITEMPRI_PRIL_CODE + "='" + prilcode + "'";


            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                return cursor.getString(cursor.getColumnIndex(DatabaseHelper.FITEMPRI_MAX_PRICE));

            }
        }catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return "";

    }
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public String getPrilCodeByItemCode(String code) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_FITEMPRI + " WHERE " + DatabaseHelper.FITEMPRI_ITEM_CODE + "='" + code + "'";

        Cursor cursor = null;
        cursor = dB.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {

            return cursor.getString(cursor.getColumnIndex(DatabaseHelper.FITEMPRI_PRIL_CODE));

        }
        return "";

    }

    public String getProductPriceByCode(String code) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "SELECT * FROM " + dbHelper.TABLE_FITEMPRI + " WHERE " + dbHelper.FITEMPRI_ITEM_CODE + "='" + code + "'";

        Cursor cursor = null;
        cursor = dB.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {

            return cursor.getString(cursor.getColumnIndex(dbHelper.FITEMPRI_PRICE));

        }
        return "";

    }

}
