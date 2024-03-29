package com.datamation.megaheaters.view.receipt;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.ReceiptAdapter;
import com.datamation.megaheaters.control.GPSTracker;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.data.FDDbNoteDS;
import com.datamation.megaheaters.data.RecDetDS;
import com.datamation.megaheaters.data.RecHedDS;
import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.model.FDDbNote;
import com.datamation.megaheaters.model.RecDet;
import com.datamation.megaheaters.model.RecHed;
import com.datamation.megaheaters.view.IconPallet_mega;
import com.datamation.megaheaters.view.MainActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReceiptSummary extends Fragment {

    View view;
    TextView lblRecAmt, lblPayMode, lblCHQNo, lblBank, textNo, textBank;
    SharedPref mSharedPref;
    public static SharedPreferences localSP;
    public static final String SETTINGS = "SETTINGS";
    String RefNo = null;
    ListView lv_fddbnote;
    MainActivity activity;
    ArrayList<FDDbNote> fddbnoteList;
    GPSTracker gps;
    FloatingActionMenu fam;
    FloatingActionButton fabPause, fabDiscard, fabSave;
    MyReceiver r;

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sales_management_receipt_summery, container, false);
        mSharedPref = new SharedPref(getActivity());
        localSP = getActivity().getSharedPreferences(SETTINGS, 0);
        gps = new GPSTracker(getActivity());
        setHasOptionsMenu(true);
        fam = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        fabPause = (FloatingActionButton) view.findViewById(R.id.fab2);
        fabDiscard = (FloatingActionButton) view.findViewById(R.id.fab3);
        fabSave = (FloatingActionButton) view.findViewById(R.id.fab1);
        lblRecAmt = (TextView) view.findViewById(R.id.lblRecAmt);
        lblPayMode = (TextView) view.findViewById(R.id.lblPayMode);
        lblCHQNo = (TextView) view.findViewById(R.id.lblCHQNo);
        lblBank = (TextView) view.findViewById(R.id.lblBank);
        textNo = (TextView) view.findViewById(R.id.textNo);
        textBank = (TextView) view.findViewById(R.id.textBank);
        lv_fddbnote = (ListView) view.findViewById(R.id.lv_fddbnote);
        RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.RecNumVal));

        fam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fam.isOpened()) {
                    fam.close(true);
                }
            }
        });

        fabPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPauseinvoice();
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fddbnoteList.size()>0)
                {
                    if (Double.parseDouble(mSharedPref.getGlobalVal("ReckeyRemnant")) <= 0)
                    saveSummaryDialog(getActivity());
                    else
                        Toast.makeText(getActivity(), "Please allocate remaining amount..!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(),"No records to Save",Toast.LENGTH_LONG).show();
                }

            }
        });

        fabDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoEditingData(getActivity());
            }
        });

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        return view;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        for (int i = 0; i < menu.size(); ++i) {
            menu.removeItem(menu.getItem(i).getItemId());
        }

        inflater.inflate(R.menu.frag_per_sales_summary, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_pre_sales_save) {

            if (Double.parseDouble(mSharedPref.getGlobalVal("ReckeyRemnant")) <= 0)
                saveSummaryDialog(getActivity());
            else
                Toast.makeText(getActivity(), "Please allocate remaining amount..!", Toast.LENGTH_SHORT).show();

        } else if (item.getItemId() == R.id.action_pre_sale_undo) {
            undoEditingData(getActivity());
        }
        return super.onOptionsItemSelected(item);

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*Cancel order*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    private void undoEditingData(final Context context) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Do you want to discard the receipt ?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                MainActivity activity = (MainActivity) getActivity();
                new FDDbNoteDS(getActivity()).ClearFddbNoteData();
                new RecHedDS(getActivity()).CancelReceiptS(RefNo);

                activity.cusPosition = 0;
                activity.selectedDebtor = null;
                activity.selectedRecHed = null;
                Toast.makeText(getActivity(), "Receipt discarded successfully..!", Toast.LENGTH_SHORT).show();
                UtilityContainer.ClearReceiptSharedPref(getActivity());
                UtilityContainer.mLoadFragment(new ReceiptInvoice(), getActivity());

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**/

    public void mPauseinvoice() {

        if (mSharedPref.getGlobalVal("ReckeyCustomer").equals("1") && mSharedPref.getGlobalVal("ReckeyHeader").equals("1"))
            UtilityContainer.mLoadFragment(new IconPallet_mega(), activity);
        else
            Toast.makeText(activity, "Select Customer/Fill in header details before Pause", Toast.LENGTH_SHORT).show();

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*Clear Shared preference-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    /* Clear shared preference */
    public void ClearSharedPref() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("ReckeyPayModePos");
        editor.remove("ReckeyPayMode");
        editor.remove("ReckeyRemnant");
        editor.remove("ReckeyRecAmt");
        editor.remove("ReckeyCHQNo");
        editor.remove("ReckeyCustomer");
        editor.remove("ReckeyHeader");
        editor.remove("ReckeyCusCode");
        editor.commit();

    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**/

    public void FetchData() {
        if (((MainActivity) getActivity()).selectedDebtor != null) {
            lv_fddbnote.setAdapter(null);
            fddbnoteList = new FDDbNoteDS(getActivity()).getAllRecords(activity.selectedDebtor.getFDEBTOR_CODE(), true);
            lv_fddbnote.setAdapter(new ReceiptAdapter(getActivity(), fddbnoteList, true, RefNo));
        }
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**/

    private String currentTime() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-Save primary & secondary invoice-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*/

    private void saveSummaryDialog(final Context context) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Do you want to save the receipt ?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(final DialogInterface dialog, int id) {

                RecHed recHed = new RecHed();
                recHed.setFPRECHED_LATITUDE(gps.getLatitude() + "");
                recHed.setFPRECHED_LONGITUDE(gps.getLongitude() + "");
                recHed.setFPRECHED_START_TIME(localSP.getString("Van_Start_Time", "").toString());
                recHed.setFPRECHED_END_TIME(currentTime());
                recHed.setFPRECHED_ADDRESS("None");
                recHed.setFPRECHED_COSTCODE(mSharedPref.getGlobalVal("PrekeyCost"));
                new RecHedDS(getActivity()).UpdateRecHed(recHed, RefNo);
                final MainActivity activity = (MainActivity) getActivity();

                ArrayList<RecDet> RecList = new ArrayList<>();

                for (FDDbNote fddb : fddbnoteList) {

                    RecDet recDet = new RecDet();
                    recDet.setFPRECDET_REFNO(RefNo);
                    recDet.setFPRECDET_BAMT(fddb.getFDDBNOTE_ENTER_AMT());
                    recDet.setFPRECDET_AMT(fddb.getFDDBNOTE_ENTER_AMT());
                    recDet.setFPRECDET_ALOAMT(fddb.getFDDBNOTE_ENTER_AMT());
                    recDet.setFPRECDET_SALEREFNO(fddb.getFDDBNOTE_REFNO());
                    recDet.setFPRECDET_REPCODE(new SalRepDS(getActivity()).getCurrentRepCode());
                    recDet.setFPRECDET_DCURCODE("LKR");
                    recDet.setFPRECDET_DCURRATE("1.0");
                    recDet.setFPRECDET_DTXNDATE(fddb.getFDDBNOTE_TXN_DATE());
                    recDet.setFPRECDET_DTXNTYPE(fddb.getFDDBNOTE_TXN_TYPE());
                    recDet.setFPRECDET_TXNDATE(currentDate());
                    recDet.setFPRECDET_TXNTYPE("21");
                    recDet.setFPRECDET_REFNO1(fddb.getFDDBNOTE_REFNO());
                    recDet.setFPRECDET_MANUREF("");
                    recDet.setFPRECDET_OCURRATE("1.00");
                    recDet.setFPRECDET_OVPAYAMT(String.valueOf(Double.parseDouble(fddb.getFDDBNOTE_TOT_BAL()) - Double.parseDouble(fddb.getFDDBNOTE_ENTER_AMT())));
                    recDet.setFPRECDET_OVPAYBAL(String.valueOf(Double.parseDouble(fddb.getFDDBNOTE_TOT_BAL()) - Double.parseDouble(fddb.getFDDBNOTE_ENTER_AMT())));
                    recDet.setFPRECDET_RECORDID("");
                    recDet.setFPRECDET_TIMESTAMP("");
                    recDet.setFPRECDET_ISDELETE("0");
                    recDet.setFPRECDET_REMARK(fddb.getFDDBNOTE_REMARKS());
                    recDet.setFPRECDET_DEBCODE(activity.selectedDebtor.getFDEBTOR_CODE());
                    RecList.add(recDet);
                }

                new RecDetDS(getActivity()).createOrUpdateRecDetS(RecList);
                new FDDbNoteDS(getActivity()).UpdateFddbNoteBalance(fddbnoteList);
                new RecHedDS(getActivity()).InactiveStatusUpdate(RefNo);

                //new ReceiptPreviewAlertBox(getActivity()).PrintDetailsDialogbox(getActivity(), "Print preview", RefNo);

                activity.cusPosition = 0;
                activity.selectedDebtor = null;
                activity.selectedRecHed = null;
                activity.ReceivedAmt = 0.00;
                new ReferenceNum(getActivity()).nNumValueInsertOrUpdate(getResources().getString(R.string.RecNumVal));

				/*-*-*-*-*-*-*-*-*-*-*-Check if deadline passed-*-*-*-*-*-*-*-*-*-*-*/

                Toast.makeText(getActivity(), "Receipt saved successfully..!", Toast.LENGTH_SHORT).show();
                UtilityContainer.mLoadFragment(new ReceiptInvoice(), getActivity());
                dialog.dismiss();
                ClearSharedPref();/* Clear shared preference */

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    private String currentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public static boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        } else if (!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        return true;
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }

   	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_SUMMARY"));
    }

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mRefreshHeader() {

        RecHed RCHed = new RecHedDS(getActivity()).getReceiptByRefno(RefNo);
        lblPayMode.setText(mSharedPref.getGlobalVal("ReckeyPayMode").toString());
        activity = (MainActivity) getActivity();

        if (mSharedPref.getGlobalVal("ReckeyPayModePos").equals("0")) {
            lblBank.setText("N/A");
            lblCHQNo.setText("N/A");
        } else if(mSharedPref.getGlobalVal("ReckeyPayModePos").equals("1")) {
            lblBank.setText("N/A");
            lblCHQNo.setText("N/A");
        }else if(mSharedPref.getGlobalVal("ReckeyPayModePos").equals("2")) {
            textBank.setText("BANK NAME : ");
            if(RCHed.getFPRECHED_CUSBANK() != null)
            {
                lblBank.setText(RCHed.getFPRECHED_CUSBANK().toString());
                lblCHQNo.setText(mSharedPref.getGlobalVal("ReckeyCHQNo"));
            }
        }else if(mSharedPref.getGlobalVal("ReckeyPayModePos").equals("3")) {
            textBank.setText("CARD TYPE : ");
            textNo.setText("CREDIT CARD NO : ");
            if(RCHed.getFPRECHED_CUSBANK() != null)
            {
                lblBank.setText(RCHed.getFPRECHED_CUSBANK().toString());
                lblCHQNo.setText(mSharedPref.getGlobalVal("ReckeyCHQNo"));
            }
        }else if(mSharedPref.getGlobalVal("ReckeyPayModePos").equals("4")) {
            textBank.setVisibility(View.GONE);
            lblBank.setVisibility(View.GONE);

            textNo.setText("DEPOSIT NO : ");
            lblCHQNo.setText(mSharedPref.getGlobalVal("ReckeyCHQNo"));
        }else{
            textBank.setVisibility(View.GONE);
            lblBank.setVisibility(View.GONE);

            textNo.setText("DRAFT NO : ");
            lblCHQNo.setText(mSharedPref.getGlobalVal("ReckeyCHQNo"));
        }

        if (!mSharedPref.getGlobalVal("ReckeyRecAmt").equals(""))
            lblRecAmt.setText(String.format("%,.2f", Double.parseDouble(mSharedPref.getGlobalVal("ReckeyRecAmt").replaceAll(",", ""))));

        FetchData();
    }

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ReceiptSummary.this.mRefreshHeader();
        }
    }
}
