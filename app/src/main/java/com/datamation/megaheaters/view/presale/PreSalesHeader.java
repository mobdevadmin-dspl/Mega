package com.datamation.megaheaters.view.presale;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.data.DebtorDS;
import com.datamation.megaheaters.data.FDDbNoteDS;
import com.datamation.megaheaters.data.LocationsDS;
import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.data.SharedPreferencesClass;
import com.datamation.megaheaters.data.TranSOHedDS;
import com.datamation.megaheaters.model.Locations;
import com.datamation.megaheaters.model.TranSOHed;
import com.datamation.megaheaters.view.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PreSalesHeader extends Fragment {

    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    View view;
    TextView customerName, outStandingAmt, lastBillAmt, txtDeliDate, txtRoute, lblCostCode, txtManual, txtRemarks;
    Spinner spnLocations,spnPayMethod;
    EditText orderNo, currnentDate;
    ImageButton btnDeliDate;
    SharedPref mSharedPref;
    MainActivity activity;
    MyReceiver r;
    ArrayList<Locations> locations;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sales_management_pre_sales_header, container, false);

        activity = (MainActivity) getActivity();
        localSP = getActivity().getSharedPreferences(SETTINGS, 0);
        mSharedPref = new SharedPref(getActivity());
        txtManual = (EditText) view.findViewById(R.id.sa_header_manual_no);
        txtRemarks = (EditText) view.findViewById(R.id.sa_header_remark);
        btnDeliDate = (ImageButton) view.findViewById(R.id.iBtnDeliDate);

        txtRoute = (TextView) view.findViewById(R.id.txtRoute);
        customerName = (TextView) view.findViewById(R.id.customerName);
        outStandingAmt = (TextView) view.findViewById(R.id.sa_header_outstanding_amt);
        lastBillAmt = (TextView) view.findViewById(R.id.sa_header_last_bill_amt);
        orderNo = (EditText) view.findViewById(R.id.sa_header_order_no);
        currnentDate = (EditText) view.findViewById(R.id.sa_header_date);
        txtDeliDate = (EditText) view.findViewById(R.id.sa_header_del_date);
        lblCostCode = (EditText) view.findViewById(R.id.txtCost);
        spnLocations = (Spinner) view.findViewById(R.id.spnLoc);
        spnPayMethod = (Spinner) view.findViewById(R.id.spnnerPayment);
//-------------------------------------------------------------------------------------------------------------------------------
        locations = new LocationsDS(getActivity()).getlocDetails();

        List<String> locNames = new ArrayList<String>();
		/* Merge type code with type name to the list */
        for (Locations loc : locations) {
            locNames.add(loc.getFLOCATIONS_LOC_CODE() + " - " + loc.getFLOCATIONS_LOC_NAME());
        }

        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, locNames);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLocations.setAdapter(dataAdapter3);
        btnDeliDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DateTimePicker();
            }
        });


        if (activity.selectedSOHed == null) {
            TranSOHed SOHed = new TranSOHedDS(getActivity()).getActiveSOHed();
            if (SOHed != null) {
                if (activity.selectedDebtor == null)
                    activity.selectedDebtor = new DebtorDS(getActivity()).getSelectedCustomerByCode(SOHed.getFTRANSOHED_DEBCODE());
            }




        }

        spnLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                SaveSalesHeader();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        List<String> listPayType = new ArrayList<String>();
        // listPayType.add("-SELECT PAYMENT TYPE-");
        listPayType.add("CASH");
        listPayType.add("CREDIT");

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, listPayType);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPayMethod.setAdapter(dataAdapter1);

        spnPayMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                new SharedPref(getActivity()).setGlobalVal("PrekeyPayType", spnPayMethod.getSelectedItem().toString());
                SaveSalesHeader();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        return view;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void DateTimePicker() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.date_picker);
        final DatePicker dtp = (DatePicker) dialog.findViewById(R.id.dpResult);
        dialog.setCancelable(true);
        Button button = (Button) dialog.findViewById(R.id.btnok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int month = dtp.getMonth() + 1;
                int year  = dtp.getYear();
                int date  = dtp.getDayOfMonth();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                Date dateToday = null;
                Date date2 = null;

                try {
                    dateToday = sdf.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    date2 = sdf.parse(year + "-" + month + "-" + date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (date2.equals(dateToday) || date2.after(dateToday)) {
                    dialog.dismiss();
                    txtDeliDate.setText(year + "-" + String.format("%02d", month) + "-" + String.format("%02d", date));
                    SaveSalesHeader();
                }

            }
        });

        dialog.show();
    }
	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private String currentTime() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void SaveSalesHeader() {

        if (orderNo.getText().length() > 0) {

           TranSOHed hed = new TranSOHed();

            hed.setFTRANSOHED_REFNO(orderNo.getText().toString());
            hed.setFTRANSOHED_TXNDELDATE(txtDeliDate.getText().toString());
            hed.setFTRANSOHED_MANUREF(txtManual.getText().toString());
            hed.setFTRANSOHED_REMARKS(txtRemarks.getText().toString());
            hed.setFTRANSOHED_ADDMACH(localSP.getString("MAC_Address", "No MAC Address").toString());
            hed.setFTRANSOHED_CURCODE("LKR");
            hed.setFTRANSOHED_CURRATE("1.00");

            hed.setFTRANSOHED_REPCODE(new SalRepDS(getActivity()).getCurrentRepCode());


            hed.setFTRANSOHED_DEBCODE(activity.selectedDebtor.getFDEBTOR_CODE());
            hed.setFTRANSOHED_CONTACT(activity.selectedDebtor.getFDEBTOR_MOB());
            hed.setFTRANSOHED_CUSADD1(activity.selectedDebtor.getFDEBTOR_ADD1());
            hed.setFTRANSOHED_CUSADD2(activity.selectedDebtor.getFDEBTOR_ADD2());
            hed.setFTRANSOHED_CUSADD3(activity.selectedDebtor.getFDEBTOR_ADD3());
            hed.setFTRANSOHED_CUSTELE(activity.selectedDebtor.getFDEBTOR_TELE());
            hed.setFTRANSOHED_TAXREG(activity.selectedDebtor.getFDEBTOR_TAX_REG());


            hed.setFTRANSOHED_TXNTYPE("42");
            hed.setFTRANSOHED_TXNDATE(currnentDate.getText().toString());
            hed.setFTRANSOHED_IS_ACTIVE("1");
            hed.setfTRANSOHED_IS_SYNCED("0");
            hed.setFTRANSOHED_TOURCODE(new SharedPref(getActivity()).getGlobalVal("preKeyTouRef"));

            //hed.setFTRANSOHED_AREACODE(new SharedPref(getActivity()).getGlobalVal("preKeyAreaCode"));
            hed.setFTRANSOHED_AREACODE(activity.selectedDebtor.getFDEBTOR_AREA_CODE());
           // hed.setFTRANSOHED_LOCCODE(new SharedPref(getActivity()).getGlobalVal("preKeyLocCode"));
            hed.setFTRANSOHED_LOCCODE(spnLocations.getSelectedItem().toString().trim().split("-")[0]);
            hed.setFTRANSOHED_ROUTECODE(new SharedPref(getActivity()).getGlobalVal("preKeyRouteCode"));
            hed.setFTRANSOHED_COSTCODE(lblCostCode.getText().toString());
            hed.setFTRANSOHED_PAYMENT_TYPE(spnPayMethod.getSelectedItem().toString());
            activity.selectedSOHed = hed;
            SharedPreferencesClass.setLocalSharedPreference(activity, "SO_Start_Time", currentTime());
            ArrayList<TranSOHed> ordHedList = new ArrayList<>();
            ordHedList.add(activity.selectedSOHed);
            new TranSOHedDS(getActivity()).createOrUpdateTranSOHedDS(ordHedList);
        }
    }

   /* public void mInputDialogbox(String s, final String sType) {

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

                SaveSalesHeader();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }*/

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }

   	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void onResume() {
        super.onResume();
        r = new PreSalesHeader.MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_HEADER"));
    }

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mRefreshHeader() {

        if (mSharedPref.getGlobalVal("PrekeyCustomer").equals("Y")) {

            currnentDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            txtRoute.setText(new SharedPref(getActivity()).getGlobalVal("preKeyRoute"));
            btnDeliDate.setEnabled(true);
            txtRemarks.setEnabled(true);
            txtManual.setEnabled(true);
            customerName.setText(activity.selectedDebtor.getFDEBTOR_NAME());
            String debCode= new SharedPref(getActivity()).getGlobalVal("PrekeyCusCode");
            outStandingAmt.setText(String.format("%,.2f", new FDDbNoteDS(getActivity()).getDebtorBalance(debCode)));
            lastBillAmt.setText("14532.50");

            if (activity.selectedSOHed != null) {
                if (activity.selectedDebtor == null)
                    activity.selectedDebtor = new DebtorDS(getActivity()).getSelectedCustomerByCode(activity.selectedSOHed.getFTRANSOHED_DEBCODE());

                customerName.setText(activity.selectedDebtor.getFDEBTOR_NAME());
                orderNo.setText(activity.selectedSOHed.getFTRANSOHED_REFNO());
                txtDeliDate.setText(activity.selectedSOHed.getFTRANSOHED_TXNDELDATE());
                txtManual.setText(activity.selectedSOHed.getFTRANSOHED_MANUREF());
                txtRemarks.setText(activity.selectedSOHed.getFTRANSOHED_REMARKS());
                lblCostCode.setText(activity.selectedSOHed.getFTRANSOHED_COSTCODE());
                outStandingAmt.setText(String.format("%,.2f", new FDDbNoteDS(getActivity()).getDebtorBalance(activity.selectedDebtor.getFDEBTOR_CODE())));
            } else {

                orderNo.setText(new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.NumVal)));
                lblCostCode.setText(new SharedPref(getActivity()).getGlobalVal("preKeyCostCode"));
                txtDeliDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                SaveSalesHeader();
            }

        } else {
            Toast.makeText(getActivity(), "Select a customer to continue...", Toast.LENGTH_SHORT).show();
            txtRemarks.setEnabled(false);
            txtManual.setEnabled(false);
            btnDeliDate.setEnabled(false);
        }

    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            PreSalesHeader.this.mRefreshHeader();
        }
    }


}


    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

