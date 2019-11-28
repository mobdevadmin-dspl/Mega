package com.datamation.megaheaters.view.presale;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.control.print_preview.SalesPrintPreviewAlertBox;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.data.OrdFreeIssueDS;
import com.datamation.megaheaters.data.OrderDiscDS;
import com.datamation.megaheaters.data.PreProductDS;
import com.datamation.megaheaters.data.PreSaleTaxDTDS;
import com.datamation.megaheaters.data.PreSaleTaxRGDS;
import com.datamation.megaheaters.data.TranSODetDS;
import com.datamation.megaheaters.data.TranSOHedDS;
import com.datamation.megaheaters.model.TranSODet;
import com.datamation.megaheaters.model.TranSOHed;
import com.datamation.megaheaters.view.IconPallet_mega;
import com.datamation.megaheaters.view.MainActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

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

public class PreSalesSummary extends Fragment {

    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    View view;
    TextView lblNetVal, lblDisc, lblSchDisc, lblLineDisc, lblGross, lblQty;
    SharedPref mSharedPref;
    String RefNo = null;
    TranSOHed Header;
    ArrayList<TranSODet> OrdDetList;
    Activity activity;
    int freeQty;
    FloatingActionButton fabPause, fabDiscard, fabSave;
    FloatingActionMenu fam;
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

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sales_management_pre_sales_summary, container, false);
        localSP = getActivity().getSharedPreferences(SETTINGS, 0);
        RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.NumVal));
        mSharedPref = new SharedPref(getActivity());

        fam = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        fabPause = (FloatingActionButton) view.findViewById(R.id.fab2);
        fabDiscard = (FloatingActionButton) view.findViewById(R.id.fab3);
        fabSave = (FloatingActionButton) view.findViewById(R.id.fab1);

        lblNetVal = (TextView) view.findViewById(R.id.lblNetVal);
        lblDisc = (TextView) view.findViewById(R.id.lblDisc);
        lblSchDisc = (TextView) view.findViewById(R.id.lblSchDisc);
        lblLineDisc = (TextView) view.findViewById(R.id.lblLineDisc);
        lblGross = (TextView) view.findViewById(R.id.lblGross);
        lblQty = (TextView) view.findViewById(R.id.lblQty);

        fam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fam.isOpened()) {
                    fam.close(true);
                }
            }
        });
        fam.setClosedOnTouchOutside(true);
        fabPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPauseOrder();
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



	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*Undo order header-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void refreshData() {

        double ftotAmt = 0, fTotLineDisc = 0, fTotSchDisc = 0;
        int ftotQty = 0, fTotFree = 0;

        OrdDetList = new TranSODetDS(getActivity()).getAllOrderDetails(RefNo);
        Header = new TranSOHedDS(getActivity()).getActiveSOHed();

        for (TranSODet ordDet : OrdDetList) {
            ftotAmt += Double.parseDouble(ordDet.getFTRANSODET_AMT());

            if (ordDet.getFTRANSODET_TYPE().equals("SA"))
                ftotQty += Integer.parseInt(ordDet.getFTRANSODET_QTY());
            else
                fTotFree += Integer.parseInt(ordDet.getFTRANSODET_QTY());

            fTotLineDisc += Double.parseDouble(ordDet.getFTRANSODET_DISAMT());
            fTotSchDisc += Double.parseDouble(ordDet.getFTRANSODET_SCHDISC());
        }

        freeQty = fTotFree;
        lblQty.setText(String.valueOf(ftotQty + fTotFree));
        lblGross.setText(String.format("%.2f", ftotAmt + fTotSchDisc + fTotLineDisc));
        lblDisc.setText(String.format("%.2f", fTotSchDisc + fTotLineDisc));
        lblNetVal.setText(String.format("%.2f", ftotAmt));
        lblSchDisc.setText(String.format("%.2f", fTotSchDisc));
        lblLineDisc.setText(String.format("%.2f", fTotLineDisc));

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*- Save final Sales order to database-*-*-*-**-*-*-*-*-*-*-*-*-*/

    public void undoEditingData() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Do you want to discard the order?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                MainActivity activity = (MainActivity) getActivity();
                boolean result = new TranSOHedDS(getActivity()).restData(RefNo);

                if (result) {
                    new TranSODetDS(getActivity()).restData(RefNo);
                    new PreProductDS(getActivity()).mClearTables();
                    new OrderDiscDS(getActivity()).clearData(RefNo);
                    new OrdFreeIssueDS(getActivity()).ClearFreeIssues(RefNo);
                    new PreSaleTaxDTDS(getActivity()).ClearTable(RefNo);
                    new PreSaleTaxRGDS(getActivity()).ClearTable(RefNo);
                }
                activity.cusPosition = 0;
                activity.selectedDebtor = null;
                activity.selectedSOHed = null;
                android.widget.Toast.makeText(getActivity(), "Order discarded successfully..!", android.widget.Toast.LENGTH_LONG).show();
                UtilityContainer.PreClearSharedPref(getActivity());
                UtilityContainer.mLoadFragment(new PreSalesInvoice(), activity);

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

        if (new TranSODetDS(getActivity()).getItemCount(RefNo) > 0) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage("Do you want to save the order ?");
            alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(final DialogInterface dialog, int id) {

                    TranSOHed mainHead = new TranSOHed();
                    ArrayList<TranSOHed> ordHedList = new ArrayList<TranSOHed>();

                    if (Header != null) {

                        mainHead.setFTRANSOHED_REFNO(RefNo);
                        mainHead.setFTRANSOHED_DEBCODE(Header.getFTRANSOHED_DEBCODE());
                        mainHead.setFTRANSOHED_TXNDELDATE(Header.getFTRANSOHED_TXNDELDATE());
                        mainHead.setFTRANSOHED_MANUREF(Header.getFTRANSOHED_MANUREF());
                        mainHead.setFTRANSOHED_REMARKS(Header.getFTRANSOHED_REMARKS());
                        mainHead.setFTRANSOHED_ADDMACH(Header.getFTRANSOHED_ADDMACH());
                        mainHead.setFTRANSOHED_CURCODE(Header.getFTRANSOHED_CURCODE());
                        mainHead.setFTRANSOHED_CURRATE(Header.getFTRANSOHED_CURRATE());
                        mainHead.setFTRANSOHED_REPCODE(Header.getFTRANSOHED_REPCODE());
                        mainHead.setFTRANSOHED_CONTACT(Header.getFTRANSOHED_CONTACT());
                        mainHead.setFTRANSOHED_CUSADD1(Header.getFTRANSOHED_CUSADD1());
                        mainHead.setFTRANSOHED_CUSADD2(Header.getFTRANSOHED_CUSADD2());
                        mainHead.setFTRANSOHED_CUSADD3(Header.getFTRANSOHED_CUSADD3());
                        mainHead.setFTRANSOHED_CUSTELE(Header.getFTRANSOHED_CUSTELE());
                        mainHead.setFTRANSOHED_TXNTYPE(Header.getFTRANSOHED_TXNTYPE());
                        mainHead.setFTRANSOHED_TXNDATE(Header.getFTRANSOHED_TXNDATE());
                        mainHead.setFTRANSOHED_TAXREG(Header.getFTRANSOHED_TAXREG());
                        mainHead.setFTRANSOHED_TOURCODE(Header.getFTRANSOHED_TOURCODE());
                        mainHead.setFTRANSOHED_LOCCODE(Header.getFTRANSOHED_LOCCODE());
                        mainHead.setFTRANSOHED_AREACODE(Header.getFTRANSOHED_AREACODE());
                        mainHead.setFTRANSOHED_ROUTECODE(Header.getFTRANSOHED_ROUTECODE());
                        mainHead.setFTRANSOHED_COSTCODE(Header.getFTRANSOHED_COSTCODE());
                        mainHead.setFTRANSOHED_IS_ACTIVE("0");
                        mainHead.setfTRANSOHED_IS_SYNCED("0");
                    }

                    mainHead.setFTRANSOHED_BPTOTALDIS("0");
                    mainHead.setFTRANSOHED_BTOTALAMT("0");
                    mainHead.setFTRANSOHED_BTOTALDIS("0");
                    mainHead.setFTRANSOHED_BTOTALTAX("0");


                    mainHead.setFTRANSOHED_LATITUDE(mSharedPref.getGlobalVal("Latitude").equals("")?"0.0":mSharedPref.getGlobalVal("Latitude"));
                    mainHead.setFTRANSOHED_LONGITUDE(mSharedPref.getGlobalVal("Longitude").equals("")?"0.0":mSharedPref.getGlobalVal("Longitude"));
                    mainHead.setFTRANSOHED_TOTALTAX("");
                    mainHead.setFTRANSOHED_TOTALDIS(lblDisc.getText().toString());
                    mainHead.setFTRANSOHED_TOTALAMT(lblNetVal.getText().toString());
                    mainHead.setFTRANSOHED_PTOTALDIS("0");
                    mainHead.setFTRANSOHED_START_TIMESO(localSP.getString("SO_Start_Time", "").toString());
                    mainHead.setFTRANSOHED_END_TIMESO(currentTime());
                    mainHead.setFTRANSOHED_TOTQTY(lblQty.getText() + "");
                    mainHead.setFTRANSOHED_TOTFREE(freeQty + "");
                    ordHedList.add(mainHead);

                    if (new TranSOHedDS(getActivity()).createOrUpdateTranSOHedDS(ordHedList) > 0) {

                        new TranSODetDS(getActivity()).InactiveStatusUpdate(RefNo);
                        new TranSOHedDS(getActivity()).InactiveStatusUpdate(RefNo);
                        new PreProductDS(getActivity()).mClearTables();
                        MainActivity activity = (MainActivity) getActivity();
                        activity.cusPosition = 0;
                        activity.selectedDebtor = null;
                        activity.selectedSOHed = null;

                        UpdateTaxDetails(RefNo);
                        new ReferenceNum(getActivity()).NumValueUpdate(getResources().getString(R.string.NumVal));
                        android.widget.Toast.makeText(getActivity(), "Order saved successfully !", android.widget.Toast.LENGTH_LONG).show();
                        new SalesPrintPreviewAlertBox(getActivity()).PrintDetailsDialogbox(getActivity(), "Sales Order", RefNo);
                        UtilityContainer.PreClearSharedPref(getActivity());
                        UtilityContainer.mLoadFragment(new PreSalesInvoice(), activity);
                    } else {
                        android.widget.Toast.makeText(getActivity(), "Order failed !", android.widget.Toast.LENGTH_LONG).show();
                    }
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alertD = alertDialogBuilder.create();
            alertD.show();
        }{
            android.widget.Toast.makeText(getActivity(), "Order det failed !", android.widget.Toast.LENGTH_LONG).show();
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void UpdateTaxDetails(String refNo) {

        ArrayList<TranSODet> list = new TranSODetDS(activity).getEveryItem(refNo);
        //commented by rashmi 2018-08-22 because tax forward calculationn in order detail.not in summary
        new TranSODetDS(activity).UpdateItemTaxInfo(list);
        new PreSaleTaxRGDS(activity).UpdateSalesTaxRG(list);
        new PreSaleTaxDTDS(activity).UpdateSalesTaxDT(list);
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private String currentTime() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mPauseOrder() {

        if (new TranSODetDS(getActivity()).getItemCount(RefNo) > 0)
            UtilityContainer.mLoadFragment(new IconPallet_mega(), activity);
        else
            Toast.makeText(activity, "Add items before pause ...!", Toast.LENGTH_SHORT).show();
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

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            PreSalesSummary.this.refreshData();
        }
    }


}
