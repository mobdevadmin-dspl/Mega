package com.datamation.megaheaters.view.sales_return;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
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
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.control.GPSTracker;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.data.FInvRDetDS;
import com.datamation.megaheaters.data.FInvRHedDS;
import com.datamation.megaheaters.model.FInvRDet;
import com.datamation.megaheaters.model.FInvRHed;
import com.datamation.megaheaters.view.IconPallet;
import com.datamation.megaheaters.view.IconPallet_mega;
import com.datamation.megaheaters.view.MainActivity;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

public class SalesReturnSummary extends Fragment {

    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    View view;
    TextView lblNetVal, lblDisc, lblGross;
    SharedPref mSharedPref;
    double ftotAmt = 0.00, totReturnDiscount = 0;
    String RefNo = null;
    ArrayList<FInvRHed> HedList;
    ArrayList<FInvRDet> returnDetList;
    Activity activity;
    GPSTracker gpsTracker;
    FloatingActionButton fabPause, fabDiscard, fabSave;
    MyReceiver r;


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

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*Undo order header-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sales_management_sales_return_summary, container, false);
        localSP = getActivity().getSharedPreferences(SETTINGS, 0);
        RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanReturnNumVal));
        mSharedPref = new SharedPref(getActivity());
        gpsTracker = new GPSTracker(getActivity());
        fabPause = (FloatingActionButton) view.findViewById(R.id.fab2);
        fabDiscard = (FloatingActionButton) view.findViewById(R.id.fab3);
        fabSave = (FloatingActionButton) view.findViewById(R.id.fab1);


        fabPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPauseinvoice();
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSummaryDialog();
            }
        });

        fabDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoEditingData();
            }
        });

        return view;
    }

    public void mRefreshData() {


            lblNetVal = (TextView) view.findViewById(R.id.lblNetVal);
            lblDisc = (TextView) view.findViewById(R.id.lblDisc);
            lblGross = (TextView) view.findViewById(R.id.lblGross);
            HedList = new FInvRHedDS(getActivity()).getAllActiveInvrhed();
            returnDetList = new FInvRDetDS(getActivity()).getAllInvRDet(RefNo);

            for (FInvRDet retDet : returnDetList) {
                ftotAmt += Double.parseDouble(retDet.getFINVRDET_AMT());
                totReturnDiscount += Double.parseDouble(retDet.getFINVRDET_DIS_AMT());
            }

            lblGross.setText(String.format("%.2f", ftotAmt + totReturnDiscount));
            lblDisc.setText(String.format("%.2f", totReturnDiscount));
            lblNetVal.setText(String.format("%.2f", ftotAmt));

    }







	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*- Save final Sales order to database-*-*-*-**-*-*-*-*-*-*-*-*-*/

    public void undoEditingData() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Do you want to discard the return?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                MainActivity activity = (MainActivity) getActivity();
                int result = new FInvRHedDS(getActivity()).restData(RefNo);

                if (result > 0) {
                    new FInvRDetDS(getActivity()).restData(RefNo);
                }
                UtilityContainer.ClearReturnSharedPref(getActivity());
                activity.cusPosition = 0;
                activity.selectedRetDebtor = null;
                activity.selectedReturnHed = null;
                Toast.makeText(getActivity(), "Return discarded successfully..!", Toast.LENGTH_LONG).show();
                UtilityContainer.mLoadFragment(new SalesReturnHistory(), activity);


            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void saveSummaryDialog() {

        if (new FInvRDetDS(getActivity()).getItemCount(RefNo) > 0) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage("Do you want to save the return ?");
            alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(final DialogInterface dialog, int id) {

                    FInvRHed mainHead = new FInvRHed();
                    ArrayList<FInvRHed> returnHedList = new ArrayList<FInvRHed>();

                    if (!HedList.isEmpty()) {

                        mainHead.setFINVRHED_REFNO(RefNo);
                        mainHead.setFINVRHED_INV_REFNO("NON");
                        mainHead.setFINVRHED_DEBCODE(HedList.get(0).getFINVRHED_DEBCODE());
                        mainHead.setFINVRHED_ADD_DATE(HedList.get(0).getFINVRHED_ADD_DATE());
                        mainHead.setFINVRHED_MANUREF(HedList.get(0).getFINVRHED_MANUREF());
                        mainHead.setFINVRHED_REMARKS(HedList.get(0).getFINVRHED_REMARKS());
                        mainHead.setFINVRHED_ADD_MACH(HedList.get(0).getFINVRHED_ADD_MACH());
                        mainHead.setFINVRHED_ADD_USER(HedList.get(0).getFINVRHED_ADD_USER());
                        mainHead.setFINVRHED_TXN_DATE(HedList.get(0).getFINVRHED_TXN_DATE());
                        mainHead.setFINVRHED_ROUTE_CODE(HedList.get(0).getFINVRHED_ROUTE_CODE());
                        mainHead.setFINVRHED_TOTAL_AMT(HedList.get(0).getFINVRHED_TOTAL_AMT());
                        mainHead.setFINVRHED_TXNTYPE(HedList.get(0).getFINVRHED_TXNTYPE());
                        mainHead.setFINVRHED_ADDRESS(HedList.get(0).getFINVRHED_ADDRESS());
                        mainHead.setFINVRHED_REASON_CODE(HedList.get(0).getFINVRHED_REASON_CODE());
                        mainHead.setFINVRHED_COSTCODE(HedList.get(0).getFINVRHED_COSTCODE());
                        mainHead.setFINVRHED_LOCCODE(HedList.get(0).getFINVRHED_LOCCODE());
                        mainHead.setFINVRHED_TAX_REG(HedList.get(0).getFINVRHED_TAX_REG());
                        mainHead.setFINVRHED_TOTAL_TAX(HedList.get(0).getFINVRHED_TOTAL_TAX());
                        mainHead.setFINVRHED_TOTAL_DIS(HedList.get(0).getFINVRHED_TOTAL_DIS());
                        mainHead.setFINVRHED_LONGITUDE(HedList.get(0).getFINVRHED_LONGITUDE());
                        mainHead.setFINVRHED_LATITUDE(HedList.get(0).getFINVRHED_LATITUDE());
                        mainHead.setFINVRHED_START_TIME(HedList.get(0).getFINVRHED_START_TIME());
                        mainHead.setFINVRHED_END_TIME(HedList.get(0).getFINVRHED_END_TIME());
                        mainHead.setFINVRHED_IS_ACTIVE("0");
                        mainHead.setFINVRHED_IS_SYNCED("0");
                    }

                    returnHedList.add(mainHead);

                    if (new FInvRHedDS(getActivity()).createOrUpdateInvRHed(returnHedList) > 0) {

                        new FInvRDetDS(getActivity()).InactiveStatusUpdate(RefNo);
                        new FInvRHedDS(getActivity()).InactiveStatusUpdate(RefNo);
                        MainActivity activity = (MainActivity) getActivity();
                        activity.cusPosition = 0;
                        activity.selectedRetDebtor = null;
                        activity.selectedReturnHed = null;
                        new ReferenceNum(getActivity()).NumValueUpdate(getResources().getString(R.string.VanReturnNumVal));
                        Toast.makeText(getActivity(), "Return saved successfully !", android.widget.Toast.LENGTH_LONG).show();
                        UtilityContainer.ClearReturnSharedPref(getActivity());
                        UtilityContainer.mLoadFragment(new SalesReturnHistory(), activity);

//						new SalesPrintPreviewAlertBox(getActivity()).PrintDetailsDialogbox(getActivity(), "Sales return",
//								RefNo);
                    } else {
                        android.widget.Toast.makeText(getActivity(), "Return failed !", android.widget.Toast.LENGTH_LONG)
                                .show();
                    }

                }

            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alertD = alertDialogBuilder.create();
            alertD.show();
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mPauseinvoice() {

        if (new FInvRDetDS(getActivity()).getItemCount(RefNo) > 0)
            UtilityContainer.mLoadFragment(new IconPallet_mega(), activity);
        else
            Toast.makeText(activity, "Add items before pause ...!", Toast.LENGTH_SHORT).show();
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }


    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            SalesReturnSummary.this.mRefreshData();
        }
    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_RET_SUMMARY"));
    }

}