package com.datamation.megaheaters.control;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.util.Log;

import com.datamation.megaheaters.data.ReferenceDS;
import com.datamation.megaheaters.model.Reference;

public class ReferenceNum {
    Context context;

    public ReferenceNum(Context context) {
        this.context = context;
    }

    public String getCurrentRefNo(String cSettingsCode) {

        String preFix = "";
        ReferenceDS referenceDS = new ReferenceDS(context);
        DecimalFormat dFormat = new DecimalFormat("0000");

        Calendar c = Calendar.getInstance();
        /* Format year and month 2016 01 => 1601 */
        String sDate = String.valueOf(c.get(Calendar.YEAR)).substring(2) + String.format("%02d", c.get(Calendar.MONTH) + 1);

        String nextNumVal = referenceDS.getNextNumVal(cSettingsCode);
        ArrayList<Reference> list = referenceDS.getCurrentPreFix(cSettingsCode);

        if (!nextNumVal.equals("")) {

            for (Reference reference : list) {
                preFix = reference.getCharVal() + reference.getRepPrefix() + sDate;
            }
            Log.v("next num val", "NEXT :" + preFix + "/" + dFormat.format(Integer.valueOf(nextNumVal)));

        } else {
            for (Reference reference : list) {
                preFix = reference.getCharVal() + reference.getRepPrefix() + sDate;
            }

            Log.v("next num val", "NEXT :" + preFix + "/" + dFormat.format(Integer.valueOf(nextNumVal)));
            Log.v("next num val", "NEXT :" + preFix);
        }

        return preFix + "/" + dFormat.format(Integer.valueOf(nextNumVal));
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public int nNumValueInsertOrUpdate(String cSettingsCode) {

        ReferenceDS referenceDS = new ReferenceDS(context);
        int nextNumVal = 0;

        nextNumVal = Integer.parseInt(referenceDS.getNextNumVal(cSettingsCode)) + 1;

        int count = referenceDS.InsetOrUpdate(cSettingsCode, nextNumVal);

        if (count > 0) {
            Log.v("InsertOrUpdate", "success");
        } else {
            Log.v("InsertOrUpdate", "Failed");
        }

        return 0;

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-Item or value based ref no update-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public int NumValueUpdate(String cSettingsCode) {

        ReferenceDS referenceDS = new ReferenceDS(context);
        int nextNumVal = 0;

        nextNumVal = Integer.parseInt(referenceDS.getNextNumVal(cSettingsCode)) + 1;

        int count = referenceDS.InsetOrUpdate(cSettingsCode, nextNumVal);

        if (count > 0) {
            Log.v("InsertOrUpdate", "success");
        } else {
            Log.v("InsertOrUpdate", "Failed");
        }

        return 0;

    }

}
