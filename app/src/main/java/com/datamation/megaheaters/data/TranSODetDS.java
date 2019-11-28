package com.datamation.megaheaters.data;

import java.util.ArrayList;

import com.datamation.megaheaters.model.OrderDisc;
import com.datamation.megaheaters.model.TranSODet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TranSODetDS {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = " TranSODet";

    public TranSODetDS(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int createOrUpdateSODet(ArrayList<TranSODet> list) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;

        try {

            for (TranSODet ordDet : list) {

                ContentValues values = new ContentValues();

                String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_FTRANSODET + " WHERE " + DatabaseHelper.FTRANSODET_ID + " = '" + ordDet.getFTRANSODET_ID() + "'";

                cursor = dB.rawQuery(selectQuery, null);

                values.put(DatabaseHelper.FTRANSODET_AMT, ordDet.getFTRANSODET_AMT());
                values.put(DatabaseHelper.FTRANSODET_BAMT, ordDet.getFTRANSODET_AMT());
                values.put(DatabaseHelper.FTRANSODET_BSELLPRICE, ordDet.getFTRANSODET_BSELLPRICE());
                values.put(DatabaseHelper.FTRANSODET_DISAMT, ordDet.getFTRANSODET_DISAMT());
                values.put(DatabaseHelper.FTRANSODET_ITEMCODE, ordDet.getFTRANSODET_ITEMCODE());
                values.put(DatabaseHelper.FTRANSODET_QTY, ordDet.getFTRANSODET_QTY());
                values.put(DatabaseHelper.FTRANSODET_PICE_QTY, ordDet.getFTRANSODET_PICE_QTY());
                values.put(DatabaseHelper.FTRANSODET_REFNO, ordDet.getFTRANSODET_REFNO());
                values.put(DatabaseHelper.FTRANSODET_SELLPRICE, ordDet.getFTRANSODET_SELLPRICE());
                values.put(DatabaseHelper.FTRANSODET_SEQNO, ordDet.getFTRANSODET_SEQNO());
                values.put(DatabaseHelper.FTRANSODET_TAXAMT, ordDet.getFTRANSODET_TAXAMT());
                values.put(DatabaseHelper.FTRANSODET_TAXCOMCODE, ordDet.getFTRANSODET_TAXCOMCODE());
                values.put(DatabaseHelper.FTRANSODET_TSELLPRICE, ordDet.getFTRANSODET_TSELLPRICE());
                values.put(DatabaseHelper.FTRANSODET_TXNDATE, ordDet.getFTRANSODET_TXNDATE());
                values.put(DatabaseHelper.FTRANSODET_TXNTYPE, ordDet.getFTRANSODET_TXNTYPE());
                values.put(DatabaseHelper.FTRANSODET_COSTPRICE, ordDet.getFTRANSODET_COSTPRICE());
                values.put(DatabaseHelper.FTRANSODET_BALQTY, ordDet.getFTRANSODET_QTY());
                values.put(DatabaseHelper.FTRANSODET_BTSELLPRICE, ordDet.getFTRANSODET_BTSELLPRICE());
                values.put(DatabaseHelper.FTRANSODET_PRILCODE, ordDet.getFTRANSODET_PRILCODE());
                values.put(DatabaseHelper.FTRANSODET_BDISAMT, ordDet.getFTRANSODET_BDISAMT());
                values.put(DatabaseHelper.FTRANSODET_PDISAMT, ordDet.getFTRANSODET_PDISAMT());
                values.put(DatabaseHelper.FTRANSODET_BPDISAMT, ordDet.getFTRANSODET_BPDISAMT());
                values.put(DatabaseHelper.FTRANSODET_BTAXAMT, ordDet.getFTRANSODET_BTAXAMT());
                values.put(DatabaseHelper.FTRANSODET_IS_SYNCED, ordDet.getFTRANSODET_IS_SYNCED());
                values.put(DatabaseHelper.FTRANSODET_IS_ACTIVE, ordDet.getFTRANSODET_IS_ACTIVE());
                values.put(DatabaseHelper.FTRANSODET_LOCCODE, ordDet.getFTRANSODET_LOCCODE());
                values.put(DatabaseHelper.FTRANSODET_QOH, ordDet.getFTRANSODET_QOH());
                values.put(DatabaseHelper.FTRANSODET_TYPE, ordDet.getFTRANSODET_TYPE());

                values.put(DatabaseHelper.FTRANSODET_COMP_DIS, ordDet.getFTRANSODET_COMP_DISC());
                values.put(DatabaseHelper.FTRANSODET_BRAND_DIS, ordDet.getFTRANSODET_BRAND_DISC());
                values.put(DatabaseHelper.FTRANSODET_COMP_DISPER, ordDet.getFTRANSODET_COMP_DISPER());
                values.put(DatabaseHelper.FTRANSODET_BRAND_DISPER, ordDet.getFTRANSODET_BRAND_DISPER());
                values.put(DatabaseHelper.FTRANSODET_DISVALAMT, ordDet.getFTRANSODET_SCHDISC());
                values.put(DatabaseHelper.FTRANSODET_SCH_DISPER, ordDet.getFTRANSODET_SCHDISPER());
                values.put(DatabaseHelper.FTRANSODET_PRICE, ordDet.getFTRANSODET_PRICE());
                values.put(DatabaseHelper.FTRANSODET_DISCTYPE, ordDet.getFTRANSODET_DISCTYPE());
                values.put(DatabaseHelper.FTRANSODET_CHANGED_PRICE, ordDet.getFTRANSODET_CHANGED_PRICE());
                values.put(DatabaseHelper.FTRANSODET_DISPER, ordDet.getFTRANSODET_DISPER());

                int cn = cursor.getCount();
                if (cn > 0) {

                    count = dB.update(DatabaseHelper.TABLE_FTRANSODET, values, DatabaseHelper.FTRANSODET_ID + " =?", new String[]{String.valueOf(ordDet.getFTRANSODET_ID())});

                } else {
                    count = (int) dB.insert(DatabaseHelper.TABLE_FTRANSODET, null, values);
                }

            }
        } catch (Exception e) {

           e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return count;

    }

	/*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-**-*-*-*/

    public ArrayList<TranSODet> getAllOrderDetails(String refno) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Cursor cursor = null;
        ArrayList<TranSODet> list = new ArrayList<TranSODet>();

        String selectQuery = "select * from " + DatabaseHelper.TABLE_FTRANSODET + " WHERE " + DatabaseHelper.FTRANSODET_REFNO + "='" + refno + "' AND Type='SA'";

        try {

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                TranSODet ordDet = new TranSODet();

                ordDet.setFTRANSODET_AMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_AMT)));
                ordDet.setFTRANSODET_BALQTY(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BALQTY)));
                ordDet.setFTRANSODET_BAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BAMT)));
                ordDet.setFTRANSODET_BDISAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BDISAMT)));
                ordDet.setFTRANSODET_BPDISAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BPDISAMT)));
                ordDet.setFTRANSODET_BSELLPRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BSELLPRICE)));
                ordDet.setFTRANSODET_BTAXAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BTAXAMT)));
                ordDet.setFTRANSODET_BTSELLPRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BTSELLPRICE)));
                ordDet.setFTRANSODET_COSTPRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_COSTPRICE)));
                ordDet.setFTRANSODET_DISAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_DISAMT)));
                ordDet.setFTRANSODET_ID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_ID)));
                ordDet.setFTRANSODET_IS_ACTIVE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_IS_ACTIVE)));
                ordDet.setFTRANSODET_IS_SYNCED(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_IS_SYNCED)));
                ordDet.setFTRANSODET_ITEMCODE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_ITEMCODE)));
                ordDet.setFTRANSODET_LOCCODE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_LOCCODE)));
                ordDet.setFTRANSODET_PDISAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_PDISAMT)));
                ordDet.setFTRANSODET_PICE_QTY(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_PICE_QTY)));
                ordDet.setFTRANSODET_PRILCODE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_PRILCODE)));
                ordDet.setFTRANSODET_QTY(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_QTY)));
                ordDet.setFTRANSODET_REFNO(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_REFNO)));
                ordDet.setFTRANSODET_SELLPRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_SELLPRICE)));
                ordDet.setFTRANSODET_SEQNO(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_SEQNO)));
                ordDet.setFTRANSODET_TAXAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TAXAMT)));
                ordDet.setFTRANSODET_TAXCOMCODE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TAXCOMCODE)));
                ordDet.setFTRANSODET_TSELLPRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TSELLPRICE)));
                ordDet.setFTRANSODET_TXNDATE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TXNDATE)));
                ordDet.setFTRANSODET_TXNTYPE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TXNTYPE)));
                ordDet.setFTRANSODET_QOH(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_QOH)));
                ordDet.setFTRANSODET_TYPE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TYPE)));
                ordDet.setFTRANSODET_SCHDISC(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_DISVALAMT)));
                ordDet.setFTRANSODET_PRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_PRICE)));
                ordDet.setFTRANSODET_CHANGED_PRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_CHANGED_PRICE)));
                ordDet.setFTRANSODET_DISPER(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_DISPER)));

                list.add(ordDet);

            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return list;
    }

	/*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*set RESERVE location records*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-**-*-*-*/

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*--*-*-*-*/

    public int deleteOrdDetItemCode(String itemCode, String type) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {

            cursor = dB.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_FTRANSODET + " WHERE " + DatabaseHelper.FTRANSODET_ITEMCODE + "='" + itemCode + "' AND type='" + type + "'", null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(DatabaseHelper.TABLE_FTRANSODET, DatabaseHelper.FTRANSODET_ITEMCODE + "='" + itemCode + "' AND type='" + type + "'", null);
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

	/*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-**-*-*-*/

    public int InactiveStatusUpdate(String refno) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;

        try {

            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_FTRANSODET + " WHERE " + DatabaseHelper.FTRANSODET_REFNO + " = '" + refno + "'";

            cursor = dB.rawQuery(selectQuery, null);

            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.FTRANSODET_IS_ACTIVE, "0");

            int cn = cursor.getCount();

            if (cn > 0) {
                count = dB.update(DatabaseHelper.TABLE_FTRANSODET, values, DatabaseHelper.FTRANSODET_REFNO + " =?", new String[]{String.valueOf(refno)});
            } else {
                count = (int) dB.insert(DatabaseHelper.TABLE_FTRANSODET, null, values);
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

	/*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-**-*-*-*/

    public ArrayList<TranSODet> getAllItemsforPrint(String refno) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Cursor cursor = null;
        ArrayList<TranSODet> list = new ArrayList<TranSODet>();

        String selectQuery = "select itemcode, amt, qty, TxnType, disamt, disvalamt, Tsellprice, type, DisPer, ChangedPrice, Price from " + DatabaseHelper.TABLE_FTRANSODET + " WHERE " + DatabaseHelper.FTRANSODET_REFNO + "='" + refno + "' group by type,itemcode order by type DESC";

        try {

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                TranSODet ordDet = new TranSODet();

                ordDet.setFTRANSODET_AMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_AMT)));
                ordDet.setFTRANSODET_ITEMCODE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_ITEMCODE)));
                ordDet.setFTRANSODET_QTY(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_QTY)));
                ordDet.setFTRANSODET_TXNTYPE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TXNTYPE)));
                ordDet.setFTRANSODET_REFNO(refno);
                ordDet.setFTRANSODET_DISAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_DISAMT)));
                ordDet.setFTRANSODET_SCHDISC(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_DISVALAMT)));
                ordDet.setFTRANSODET_TSELLPRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TSELLPRICE)));
                ordDet.setFTRANSODET_DISPER(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_DISPER)));
                ordDet.setFTRANSODET_CHANGED_PRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_CHANGED_PRICE)));
                ordDet.setFTRANSODET_PRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_PRICE)));
                list.add(ordDet);
            }

        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return list;
    }

	/*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-**-*-*-*/

    public int restData(String refno) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;

        try {

            String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_FTRANSODET + " WHERE " + DatabaseHelper.FTRANSODET_REFNO + " = '" + refno + "'";
            cursor = dB.rawQuery(selectQuery, null);
            int cn = cursor.getCount();

            if (cn > 0) {
                count = dB.delete(DatabaseHelper.TABLE_FTRANSODET, DatabaseHelper.FTRANSODET_REFNO + " ='" + refno + "'", null);
                Log.v("Success Stauts", count + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return count;

    }

	/*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-**-*-*-*/

    public ArrayList<TranSODet> getAllUnSync(String refno) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<TranSODet> list = new ArrayList<TranSODet>();

        String selectQuery = "select * from " + DatabaseHelper.TABLE_FTRANSODET + " WHERE " + DatabaseHelper.FTRANSODET_REFNO + "='" + refno + "'";

        Cursor cursor = dB.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {

            TranSODet ordDet = new TranSODet();

            ordDet.setFTRANSODET_AMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_AMT)));
            ordDet.setFTRANSODET_BALQTY(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BALQTY)));
            ordDet.setFTRANSODET_BAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BAMT)));
            ordDet.setFTRANSODET_BDISAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BDISAMT)));
            ordDet.setFTRANSODET_BPDISAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BPDISAMT)));
            ordDet.setFTRANSODET_BSELLPRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BSELLPRICE)));
            ordDet.setFTRANSODET_BTAXAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BTAXAMT)));
            ordDet.setFTRANSODET_BTSELLPRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BTSELLPRICE)));
            ordDet.setFTRANSODET_COSTPRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_COSTPRICE)));
            ordDet.setFTRANSODET_ID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_ID)));
            ordDet.setFTRANSODET_IS_ACTIVE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_IS_ACTIVE)));
            ordDet.setFTRANSODET_IS_SYNCED(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_IS_SYNCED)));
            ordDet.setFTRANSODET_ITEMCODE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_ITEMCODE)));
            ordDet.setFTRANSODET_LOCCODE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_LOCCODE)));
            ordDet.setFTRANSODET_PDISAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_PDISAMT)));
            ordDet.setFTRANSODET_PICE_QTY(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_PICE_QTY)));
            ordDet.setFTRANSODET_PRILCODE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_PRILCODE)));
            ordDet.setFTRANSODET_QTY(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_QTY)));
            ordDet.setFTRANSODET_REFNO(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_REFNO)));
            ordDet.setFTRANSODET_SELLPRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_SELLPRICE)));
            ordDet.setFTRANSODET_SEQNO(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_SEQNO)));
            ordDet.setFTRANSODET_TAXAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TAXAMT)));
            ordDet.setFTRANSODET_TAXCOMCODE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TAXCOMCODE)));
            ordDet.setFTRANSODET_TSELLPRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TSELLPRICE)));
            ordDet.setFTRANSODET_TXNDATE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TXNDATE)));
            ordDet.setFTRANSODET_TXNTYPE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TXNTYPE)));

            ordDet.setFTRANSODET_TYPE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TYPE)));
            ordDet.setFTRANSODET_BRAND_DISC(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BRAND_DIS)));
            ordDet.setFTRANSODET_BRAND_DISPER(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BRAND_DISPER)));
            ordDet.setFTRANSODET_COMP_DISC(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_COMP_DIS)));
            ordDet.setFTRANSODET_COMP_DISPER(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_COMP_DISPER)));
            ordDet.setFTRANSODET_SCHDISPER(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_SCH_DISPER)));
            ordDet.setFTRANSODET_PRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_PRICE)));
            ordDet.setFTRANSODET_CHANGED_PRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_CHANGED_PRICE)));
//            if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_DISCTYPE)).equals("P")) {
//                ordDet.setFTRANSODET_DISAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_DISVALAMT)));
//                ordDet.setFTRANSODET_SCHDISC("0");
//            } else {
//                ordDet.setFTRANSODET_DISAMT("0");
//                ordDet.setFTRANSODET_SCHDISC(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_DISVALAMT)));
//            }
            ordDet.setFTRANSODET_DISAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_DISAMT)));
            ordDet.setFTRANSODET_DISPER(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_DISPER)));
            list.add(ordDet);

        }

        cursor.close();
        dB.close();
        return list;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public ArrayList<TranSODet> getEveryItem(String refno) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<TranSODet> list = new ArrayList<TranSODet>();

        String selectQuery = "select Itemcode,RefNo,TaxComCode,Price,Qty,Amt,BAmt,type,disvalamt,TaxAmt,DisPer from " + DatabaseHelper.TABLE_FTRANSODET + " WHERE RefNo='" + refno + "'";

        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            while (cursor.moveToNext()) {

                TranSODet SODet = new TranSODet();

                SODet.setFTRANSODET_ITEMCODE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_ITEMCODE)));
                SODet.setFTRANSODET_REFNO(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_REFNO)));
                SODet.setFTRANSODET_TAXCOMCODE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TAXCOMCODE)));
                SODet.setFTRANSODET_BTSELLPRICE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_PRICE)));
                SODet.setFTRANSODET_QTY(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_QTY)));
                SODet.setFTRANSODET_AMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_AMT)));
                SODet.setFTRANSODET_TYPE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TYPE)));
                SODet.setFTRANSODET_SCHDISC(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_DISVALAMT)));
                SODet.setFTRANSODET_DISPER(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_DISPER)));
                SODet.setFTRANSODET_BAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_BAMT)));
                SODet.setFTRANSODET_TAXAMT(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TAXAMT)));
                list.add(SODet);

            }

        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return list;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public double getTaxedSellprice(String refno, String itemCode) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "SELECT SellPrice,TaxAmt,Qty  FROM " + DatabaseHelper.TABLE_FTRANSODET + " WHERE Itemcode='" + itemCode + "' AND RefNo='" + refno + "' AND LocCode='" + new LocationsDS(context).GetReserveCode("LT4") + "'";

        try {
            Cursor cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {
                double sellprice = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_SELLPRICE)));
                double qty = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_QTY)));
                double taxamt = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_TAXAMT)));

                return sellprice - (taxamt / qty);

            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            dB.close();
        }

        return 0.00;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public int getItemCount(String refNo) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
            String selectQuery = "SELECT count(RefNo) as RefNo FROM " + DatabaseHelper.TABLE_FTRANSODET + " WHERE  " + DatabaseHelper.FTRANSODET_REFNO + "='" + refNo + "'";
            Cursor cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {
                return Integer.parseInt(cursor.getString(cursor.getColumnIndex("RefNo")));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            dB.close();
        }
        return 0;

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void updateDiscount(TranSODet ordDet, double discount, String discType) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;

        try {

            OrderDisc orderDisc = new OrderDisc();
            orderDisc.setRefNo(ordDet.getFTRANSODET_REFNO());
            orderDisc.setTxnDate(ordDet.getFTRANSODET_TXNDATE());
            orderDisc.setItemCode(ordDet.getFTRANSODET_ITEMCODE());
            orderDisc.setDisAmt(String.format("%.2f", discount));

            new OrderDiscDS(context).UpdateOrderDiscount(orderDisc, ordDet.getFTRANSODET_DISC_REF(), ordDet.getFTRANSODET_SCHDISPER());
            String amt = String.format(String.format("%.2f", (Double.parseDouble(ordDet.getFTRANSODET_AMT()) + Double.parseDouble(ordDet.getFTRANSODET_SCHDISC())) - discount));
            String updateQuery = "UPDATE FTranSODet SET " + DatabaseHelper.FTRANSODET_SCH_DISPER + "='" + ordDet.getFTRANSODET_SCHDISPER() + "'," + DatabaseHelper.FTRANSODET_DISVALAMT + " ='" + String.format("%.2f", discount) + "'," + DatabaseHelper.FTRANSODET_AMT + "='" + amt + "'," + DatabaseHelper.FTRANSODET_BAMT + "='" + amt + "'," + DatabaseHelper.FTRANSODET_DISCTYPE + "='" + discType + "' WHERE Itemcode ='" + ordDet.getFTRANSODET_ITEMCODE() + "' AND type='SA'";
            dB.execSQL(updateQuery);

        } catch (Exception e) {
            Log.v(TAG + " Exception", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public int restFreeIssueData(String refno) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;

        try {

            String selectQuery = "SELECT * FROM FTranSODet WHERE " + DatabaseHelper.FTRANSODET_REFNO + " = '" + refno + "' AND " + DatabaseHelper.FTRANSODET_TYPE + " = 'FI'";
            cursor = dB.rawQuery(selectQuery, null);
            int cn = cursor.getCount();

            if (cn > 0) {
                count = dB.delete(DatabaseHelper.TABLE_FTRANSODET, DatabaseHelper.FTRANSODET_REFNO + " = '" + refno + "' AND " + DatabaseHelper.FTRANSODET_TYPE + " = 'FI'", null);
                Log.v("Success Stauts", count + "");
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
    //-------------------------------------------------------------------------------------------------------------------------------------------------

    public ArrayList<TranSODet> getAllFreeIssue(String refno) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<TranSODet> list = new ArrayList<TranSODet>();

        String selectQuery = "select * from " + DatabaseHelper.TABLE_FTRANSODET + " WHERE " + DatabaseHelper.FTRANSODET_REFNO + "='" + refno + "' AND " + DatabaseHelper.FTRANSODET_TYPE  + "='FI'" ;

        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {

            while (cursor.moveToNext()) {

                TranSODet tranSODet=new TranSODet();
                tranSODet.setFTRANSODET_ITEMCODE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_ITEMCODE)));
                tranSODet.setFTRANSODET_QTY(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FTRANSODET_QTY)));
                list.add(tranSODet);
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return list;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public ArrayList<TranSODet> getSAForFreeIssueCalc(String refno) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<TranSODet> list = new ArrayList<TranSODet>();

        String selectQuery = "select * from " + dbHelper.TABLE_FTRANSODET + " WHERE " + dbHelper.FTRANSODET_TXNTYPE + "='21' AND " + dbHelper.FTRANSODET_REFNO + "='" + refno + "'";

        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            while (cursor.moveToNext()) {

                TranSODet ordDet = new TranSODet();

                ordDet.setFTRANSODET_ID(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_ID)));
                ordDet.setFTRANSODET_AMT(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_AMT)));
                ordDet.setFTRANSODET_BAMT(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_BAMT)));
                ordDet.setFTRANSODET_BSELLPRICE(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_BSELLPRICE)));
                ordDet.setFTRANSODET_BTSELLPRICE(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_BTSELLPRICE)));
                ordDet.setFTRANSODET_DISAMT(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_DISAMT)));
                ordDet.setFTRANSODET_ITEMCODE(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_ITEMCODE)));
                ordDet.setFTRANSODET_QTY(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_QTY)));
                ordDet.setFTRANSODET_PICE_QTY(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_PICE_QTY)));
                ordDet.setFTRANSODET_TYPE(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_TYPE)));
                ordDet.setFTRANSODET_REFNO(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_REFNO)));
                ordDet.setFTRANSODET_SELLPRICE(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_SELLPRICE)));
                ordDet.setFTRANSODET_SEQNO(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_SEQNO)));
                ordDet.setFTRANSODET_TAXAMT(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_TAXAMT)));
                ordDet.setFTRANSODET_TAXCOMCODE(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_TAXCOMCODE)));
                ordDet.setFTRANSODET_TSELLPRICE(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_TSELLPRICE)));
                ordDet.setFTRANSODET_TXNDATE(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_TXNDATE)));
                ordDet.setFTRANSODET_IS_ACTIVE(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_IS_ACTIVE)));
                ordDet.setFTRANSODET_TXNTYPE(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_TXNTYPE)));
                ordDet.setFTRANSODET_PRILCODE(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_PRILCODE)));
                ordDet.setFTRANSODET_SCHDISC(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_DISVALAMT)));
                ordDet.setFTRANSODET_PRICE(cursor.getString(cursor.getColumnIndex(dbHelper.FTRANSODET_PRICE)));

                list.add(ordDet);

            }
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return list;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void UpdateArrayDiscount(ArrayList<TranSODet> orderList) {

        String DiscRef = orderList.get(0).getFTRANSODET_DISC_REF();
        String DiscPer = orderList.get(0).getFTRANSODET_SCHDISPER();

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {

            for (TranSODet ordDet : orderList) {

                OrderDisc orderDisc = new OrderDisc();
                orderDisc.setRefNo(ordDet.getFTRANSODET_REFNO());
                orderDisc.setTxnDate(ordDet.getFTRANSODET_TXNDATE());
                orderDisc.setRefNo1(ordDet.getFTRANSODET_DISC_REF());
                orderDisc.setItemCode(ordDet.getFTRANSODET_ITEMCODE());
                orderDisc.setDisAmt(ordDet.getFTRANSODET_DISAMT());
                orderDisc.setDisPer(ordDet.getFTRANSODET_SCHDISPER());

                new OrderDiscDS(context).UpdateOrderDiscount(orderDisc, DiscRef, DiscPer);
                String updateQuery = "UPDATE ftransodet SET SchDisPer='" + ordDet.getFTRANSODET_SCHDISPER() + "', DisValAmt='" + ordDet.getFTRANSODET_DISAMT() + "' where Itemcode ='" + ordDet.getFTRANSODET_ITEMCODE() + "'";
                dB.execSQL(updateQuery);

            }

        } catch (Exception e) {
            Log.v(TAG + " Exception", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
    }

    public void UpdateItemTaxInfo(ArrayList<TranSODet> list) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        double totTax = 0, totalAmt = 0, tsellPrice = 0;

        try {

            for (TranSODet ordDet : list) {

				/* Calculate only for SA */
                if (ordDet.getFTRANSODET_TYPE().equals("SA")) {

                    String sArray[] = new TaxDetDS(context).calculateTaxForward(ordDet.getFTRANSODET_ITEMCODE(), Double.parseDouble(ordDet.getFTRANSODET_BAMT()));
                    //comment by rashmi..because upload issue
//                    totTax += Double.parseDouble(sArray[1]);
//                    totalAmt += Double.parseDouble(sArray[0]);

                   totTax += Double.parseDouble(ordDet.getFTRANSODET_TAXAMT());
                   totalAmt += Double.parseDouble(ordDet.getFTRANSODET_AMT());
                    //comment by rashmi..because upload issue
                    // String updateQuery = "UPDATE ftransodet SET taxamt='" + sArray[1] + "', amt='" + sArray[0] + "' where Itemcode ='" + ordDet.getFTRANSODET_ITEMCODE() + "' AND refno='" + ordDet.getFTRANSODET_REFNO() + "' AND type='SA'";

                     String updateQuery = "UPDATE ftransodet SET taxamt='" + ordDet.getFTRANSODET_TAXAMT() + "', amt='" + ordDet.getFTRANSODET_AMT() + "' where Itemcode ='" + ordDet.getFTRANSODET_ITEMCODE() + "' AND refno='" + ordDet.getFTRANSODET_REFNO() + "' AND type='SA'";
                    dB.execSQL(updateQuery);
                }
            }
            /* Update Sales order Header TotalTax */
            //dB.execSQL("UPDATE ftransohed SET totaltax='" + totTax + "',totalamt='" + totalAmt + "' WHERE refno='" + list.get(0).getFTRANSODET_REFNO() + "'");
            dB.execSQL("UPDATE ftransohed SET totaltax='" + totTax + "',totalamt='" + totalAmt + "' WHERE refno='" + list.get(0).getFTRANSODET_REFNO() + "'");

        } catch (Exception e) {
            Log.v(TAG + " Exception", e.toString());
        } finally {
            dB.close();
        }

    }


    public String getLastSequnenceNo(String RefNo) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {
            String selectQuery = "SELECT Max(seqno) as seqno FROM " + dbHelper.TABLE_FTRANSODET + " WHERE " + dbHelper.FTRANSODET_REFNO + "='" + RefNo + "'";
            Cursor cursor = dB.rawQuery(selectQuery, null);
            cursor.moveToFirst();

            return (cursor.getInt(cursor.getColumnIndex("seqno")) + 1) + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "1";
        } finally {
            dB.close();
        }
    }


}
