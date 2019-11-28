package com.datamation.megaheaters.view.sales_return;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.data.DebtorDS;
import com.datamation.megaheaters.data.FInvRHedDS;
import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.data.SharedPreferencesClass;
import com.datamation.megaheaters.model.FInvRHed;
import com.datamation.megaheaters.view.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SalesReturnHeader extends Fragment {

    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    View view;
    TextView lblCusName, txtRoute, lblCostCode;
    EditText lblRefno, lblDate, txtManual, txtRemarks;
    SharedPref mSharedPref;
    String RefNo;
    MyReceiver r;
    MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sales_management_return_header, container, false);
        activity = (MainActivity) getActivity();

        mSharedPref = new SharedPref(getActivity());
        txtManual = (EditText) view.findViewById(R.id.return_header_manual_no);
        txtRemarks = (EditText) view.findViewById(R.id.return_header_remark);
        txtRoute = (TextView) view.findViewById(R.id.txtRoute);
        lblCusName = (TextView) view.findViewById(R.id.customerName);
        lblRefno = (EditText) view.findViewById(R.id.return_header_return_no);
        lblDate = (EditText) view.findViewById(R.id.return_header_date);
        lblCostCode = (TextView) view.findViewById(R.id.txtCost);

        RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanReturnNumVal));

        localSP = activity.getSharedPreferences(SETTINGS, 0);

        lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        lblRefno.setText(RefNo);
        txtRoute.setText(new SharedPref(getActivity()).getGlobalVal("returnKeyRoute"));
        lblCostCode.setText(new SharedPref(getActivity()).getGlobalVal("returnKeyCostCode"));

        ArrayList<FInvRHed> getReturnHed = new FInvRHedDS(getActivity()).getAllActiveInvrhed();

        mSharedPref.setGlobalVal("returnkeyRefNo", lblRefno.getText().toString());

        if (!getReturnHed.isEmpty()) {
            txtManual.setText(getReturnHed.get(0).getFINVRHED_MANUREF());
            txtRemarks.setText(getReturnHed.get(0).getFINVRHED_REMARKS());
            activity.selectedReturnHed = getReturnHed.get(0);
            activity.selectedRetDebtor = new DebtorDS(getActivity()).getSelectedCustomerByCode(getReturnHed.get(0).getFINVRHED_DEBCODE());
        }

       /* txtRemarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputDialogbox(txtRemarks.getText().toString(), "R");
            }
        });

        *//*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*//*

        txtManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputDialogbox(txtManual.getText().toString(), "M");
            }
        });*/

        return view;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private String currentTime() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void SaveReturnHeader() {

        if (lblRefno.getText().length() > 0) {

            MainActivity activity = (MainActivity) getActivity();
            FInvRHed hed = new FInvRHed();

            hed.setFINVRHED_REFNO(RefNo);
            hed.setFINVRHED_MANUREF(txtManual.getText().toString());
            hed.setFINVRHED_REMARKS(txtRemarks.getText().toString());
            hed.setFINVRHED_ADD_USER(new SalRepDS(getActivity()).getCurrentRepCode());
            hed.setFINVRHED_ADD_DATE(currentTime());
            hed.setFINVRHED_ADD_MACH(localSP.getString("MAC_Address", "No MAC Address").toString());
            hed.setFINVRHED_TXNTYPE("42");
            hed.setFINVRHED_TXN_DATE(lblDate.getText().toString());
            hed.setFINVRHED_IS_ACTIVE("1");
            hed.setFINVRHED_IS_SYNCED("0");
            hed.setFINVRHED_INV_REFNO("NON");

            if (activity.selectedRetDebtor != null) {
                hed.setFINVRHED_DEBCODE(activity.selectedRetDebtor.getFDEBTOR_CODE());
                hed.setFINVRHED_TAX_REG(activity.selectedRetDebtor.getFDEBTOR_TAX_REG());
            }

            hed.setFINVRHED_LOCCODE(new SalRepDS(getActivity()).getCurrentLocCode());
            hed.setFINVRHED_ROUTE_CODE(new SharedPref(getActivity()).getGlobalVal("returnKeyRouteCode"));
            hed.setFINVRHED_COSTCODE(lblCostCode.getText().toString());

            activity.selectedReturnHed = hed;
            SharedPreferencesClass.setLocalSharedPreference(activity, "Return_Start_Time", currentTime());
            ArrayList<FInvRHed> returnHedList = new ArrayList<FInvRHed>();
            returnHedList.add(activity.selectedReturnHed);
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            SalesReturnHeader.this.mRefreshHeader();
        }
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }

   	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void onResume() {
        super.onResume();
        r = new SalesReturnHeader.MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_RET_HEADER"));
    }

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mRefreshHeader() {

        if (mSharedPref.getGlobalVal("returnkeyCustomer").equals("1")) {

            if (activity.selectedRetDebtor != null)
                lblCusName.setText(activity.selectedRetDebtor.getFDEBTOR_NAME());

            lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
           /* txtRemarks.setEnabled(true);
            txtManual.setEnabled(true);
*/
            /*already a header exist*/
            if (activity.selectedReturnHed != null) {
                txtManual.setText(activity.selectedReturnHed.getFINVRHED_MANUREF());
                txtRemarks.setText(activity.selectedReturnHed.getFINVRHED_REMARKS());
                lblRefno.setText(activity.selectedReturnHed.getFINVRHED_REFNO());
            } else { /*No header*/
                lblRefno.setText(new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanReturnNumVal)));
                SaveReturnHeader();
            }

        } else {
            Toast.makeText(getActivity(), "Select a customer to continue...", Toast.LENGTH_SHORT).show();
            txtRemarks.setEnabled(false);
            txtManual.setEnabled(false);
        }
    }

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mInputDialogbox(String s, final String sType) {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.input_dialogbox, null);
        final EditText enteredText = (EditText) promptView.findViewById(R.id.txtTextbox);
        enteredText.setText(s);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        alertDialogBuilder.setCancelable(false).setPositiveButton("Save", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                if (sType.equals("R"))
                    txtRemarks.setText(enteredText.getText().toString());
                else
                    txtManual.setText(enteredText.getText().toString());

                SaveReturnHeader();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }
}
