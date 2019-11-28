package com.datamation.megaheaters.view.vansale;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.InvDetAdapter;
import com.datamation.megaheaters.adapter.NewProduct_Adapter;
import com.datamation.megaheaters.adapter.ReturnDetailsAdapter;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.control.print_preview.VanSalePrintPreviewAlertBox;
import com.datamation.megaheaters.data.ControlDS;
import com.datamation.megaheaters.data.DebItemPriDS;
import com.datamation.megaheaters.data.DispDetDS;
import com.datamation.megaheaters.data.DispHedDS;
import com.datamation.megaheaters.data.DispIssDS;
import com.datamation.megaheaters.data.FInvRDetDS;
import com.datamation.megaheaters.data.FInvRHedDS;
import com.datamation.megaheaters.data.InvDetDS;
import com.datamation.megaheaters.data.InvHedDS;
import com.datamation.megaheaters.data.InvTaxDTDS;
import com.datamation.megaheaters.data.InvTaxRGDS;
import com.datamation.megaheaters.data.ItemLocDS;
import com.datamation.megaheaters.data.ItemsDS;
import com.datamation.megaheaters.data.OrdFreeIssueDS;
import com.datamation.megaheaters.data.OrderDiscDS;
import com.datamation.megaheaters.data.ProductDS;
import com.datamation.megaheaters.data.STKInDS;
import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.data.StkIssDS;
import com.datamation.megaheaters.data.TaxDetDS;
import com.datamation.megaheaters.model.FInvRDet;
import com.datamation.megaheaters.model.FInvRHed;
import com.datamation.megaheaters.model.InvDet;
import com.datamation.megaheaters.model.InvHed;
import com.datamation.megaheaters.model.Items;
import com.datamation.megaheaters.model.Product;
import com.datamation.megaheaters.model.StkIn;
import com.datamation.megaheaters.view.IconPallet_mega;
import com.datamation.megaheaters.view.MainActivity;
import com.datamation.megaheaters.view.sales_return.SalesReturnHistory;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class VanSalesSummary extends Fragment {

    public static final String SETTINGS = "VanSalesSummary";
    public static SharedPreferences localSP;
    View view;
    TextView lblGross, lblReturnQty, lblReturn, lblNetVal, lblReplacements, lblQty;
    SharedPref mSharedPref;
    String RefNo = null,ReturnRefNo = null;
    ArrayList<InvDet> list;
    ArrayList<FInvRDet> returnList;
    Activity activity;
    String locCode;
    FloatingActionButton fabPause, fabDiscard, fabSave;
    FloatingActionMenu fam;
    MyReceiver r;
    int iTotFreeQty = 0;
    private  SweetAlertDialog pDialog;

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

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*Cancel order*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sales_management_van_sales_summary, container, false);

        mSharedPref = new SharedPref(getActivity());
        final MainActivity activity = (MainActivity) getActivity();
        localSP = getActivity().getSharedPreferences(SETTINGS, 0);
        if(activity.selectedDebtor != null)
        {
            if(!TextUtils.isEmpty(activity.selectedDebtor.getFDEBTOR_TAX_REG()))
            {
                if(activity.selectedDebtor.getFDEBTOR_TAX_REG().equals("Y")){
                    RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanNumValTax));

                }else{
                    RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanNumValNonTax));

                }
            }
        }
        else
        {
            Toast.makeText(activity,"Select Customer",Toast.LENGTH_SHORT);
        }

        ReturnRefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanReturnNumVal));
        fabPause = (FloatingActionButton) view.findViewById(R.id.fab2);
        fabDiscard = (FloatingActionButton) view.findViewById(R.id.fab3);
        fabSave = (FloatingActionButton) view.findViewById(R.id.fab1);
        fam = (FloatingActionMenu) view.findViewById(R.id.fab_menu);

        lblNetVal = (TextView) view.findViewById(R.id.lblNetVal_Inv);
        lblReturn = (TextView) view.findViewById(R.id.lbl_return_tot);
        lblReturnQty = (TextView) view.findViewById(R.id.lblReturnQty);
        lblReplacements = (TextView) view.findViewById(R.id.lblReplacement);
        lblGross = (TextView) view.findViewById(R.id.lblGross_Inv);
        lblQty = (TextView) view.findViewById(R.id.lblQty_Inv);

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

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*Clear Shared preference-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public void undoEditingData() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Do you want to discard ?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                MainActivity activity = (MainActivity) getActivity();
                String result = new InvHedDS(getActivity()).restData(RefNo);
                int resultReturn = new FInvRHedDS(getActivity()).restData(ReturnRefNo);

                if (!result.equals("")) {
                    new InvDetDS(getActivity()).restData(RefNo);
                    new ProductDS(getActivity()).mClearTables();
                }
                if(resultReturn != 0){
                    new FInvRDetDS(getActivity()).restData(ReturnRefNo);
                }

                activity.cusPosition = 0;
                activity.selectedDebtor = null;
                activity.selectedRetDebtor = null;
                activity.selectedInvHed = null;
                activity.selectedReturnHed = null;
                android.widget.Toast.makeText(getActivity(), "Invoice and return details discarded successfully..!", android.widget.Toast.LENGTH_SHORT).show();
                UtilityContainer.ClearVanSharedPref(getActivity());
                UtilityContainer.ClearReturnSharedPref(getActivity());

                UtilityContainer.mLoadFragment(new VanSaleInvoice(),getActivity());

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-Save primary & secondary invoice-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*/

    public void mRefreshData() {

        int ftotQty = 0, fTotFree = 0, returnQty = 0, replacements = 0;
        double ftotAmt = 0, fTotLineDisc = 0, fTotSchDisc = 0, totalReturn = 0;

        locCode = new SharedPref(getActivity()).getGlobalVal("KeyLocCode");

        list = new InvDetDS(getActivity()).getAllInvDet(RefNo);
        returnList = new FInvRDetDS(getActivity()).getAllInvRDet(ReturnRefNo);

        for (InvDet ordDet : list) {
            ftotAmt += Double.parseDouble(ordDet.getFINVDET_AMT());

            if (ordDet.getFINVDET_TYPE().equals("SA"))
                ftotQty += Integer.parseInt(ordDet.getFINVDET_QTY());
            else
                fTotFree += Integer.parseInt(ordDet.getFINVDET_QTY());

            fTotLineDisc += Double.parseDouble(ordDet.getFINVDET_DIS_AMT());
            fTotSchDisc += Double.parseDouble(ordDet.getFINVDET_DISVALAMT());
        }
        for (FInvRDet returnDet : returnList){
            if(!returnDet.getFINVRDET_RETURN_TYPE().equals("RP")) {
                totalReturn += Double.parseDouble(returnDet.getFINVRDET_AMT());
                returnQty += Double.parseDouble(returnDet.getFINVRDET_QTY());
            }else{
                replacements += Double.parseDouble(returnDet.getFINVRDET_QTY());
            }
        }

        iTotFreeQty = fTotFree;
        lblQty.setText(String.valueOf(ftotQty + fTotFree));
        lblGross.setText(String.format("%.2f", ftotAmt + fTotSchDisc + fTotLineDisc));
        lblReturn.setText(String.format("%.2f", totalReturn));
        lblNetVal.setText(String.format("%.2f", ftotAmt-totalReturn));
        lblReturnQty.setText(String.valueOf(returnQty));
        lblReplacements.setText(String.valueOf(replacements));


    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public void saveSummaryDialog() {

        if (new InvDetDS(getActivity()).getItemCount(RefNo) > 0)
        {

            if (new FInvRDetDS(getActivity()).getItemCount(ReturnRefNo) > 0) {
                //save both invoice and return
                //Changed By Yasith - 2019-01-29
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptView = layoutInflater.inflate(R.layout.sales_management_van_sales_summary_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Do you want to save the invoice with return details ?");
                alertDialogBuilder.setView(promptView);

                final ListView lvProducts_Invoice = (ListView) promptView.findViewById(R.id.lvProducts_Summary_Dialog_Inv);
                final ListView lvProducts_Return = (ListView) promptView.findViewById(R.id.lvProducts_Summary_Dialog_Ret);

                ArrayList<InvDet> invoiceItemList = null;
                ArrayList<FInvRDet> returnItemList = null;

                invoiceItemList = new InvDetDS(getActivity()).getAllItemsAddedInCurrentSale(RefNo);
                returnItemList = new FInvRDetDS(getActivity()).getAllItemsAddedInCurrentReturn(ReturnRefNo);
                lvProducts_Invoice.setAdapter(new InvDetAdapter(getActivity(), invoiceItemList));
                lvProducts_Return.setAdapter(new ReturnDetailsAdapter(getActivity(), returnItemList));

                alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, int id) {

                        InvHed sHed = new InvHed();
                        ArrayList<InvHed> invHedList = new ArrayList<InvHed>();

                        InvHed invHed = new InvHedDS(getActivity()).getActiveInvhed();

                        sHed.setFINVHED_REFNO(RefNo);
                        sHed.setFINVHED_DEBCODE(invHed.getFINVHED_DEBCODE());
                        sHed.setFINVHED_ADDDATE(invHed.getFINVHED_ADDDATE());
                        sHed.setFINVHED_MANUREF(invHed.getFINVHED_MANUREF());
                        sHed.setFINVHED_REMARKS(invHed.getFINVHED_REMARKS());
                        sHed.setFINVHED_ADDMACH(invHed.getFINVHED_ADDMACH());
                        sHed.setFINVHED_ADDUSER(invHed.getFINVHED_ADDUSER());
                        sHed.setFINVHED_CURCODE(invHed.getFINVHED_CURCODE());
                        sHed.setFINVHED_CURRATE(invHed.getFINVHED_CURRATE());
                        sHed.setFINVHED_LOCCODE(invHed.getFINVHED_LOCCODE());

                        sHed.setFINVHED_CUSTELE(invHed.getFINVHED_CUSTELE());
                        sHed.setFINVHED_CONTACT(invHed.getFINVHED_CONTACT());
                        sHed.setFINVHED_CUSADD1(invHed.getFINVHED_CUSADD1());
                        sHed.setFINVHED_CUSADD2(invHed.getFINVHED_CUSADD2());
                        sHed.setFINVHED_CUSADD3(invHed.getFINVHED_CUSADD3());
                        sHed.setFINVHED_TXNTYPE(invHed.getFINVHED_TXNTYPE());
                        sHed.setFINVHED_IS_ACTIVE(invHed.getFINVHED_IS_ACTIVE());
                        sHed.setFINVHED_IS_SYNCED(invHed.getFINVHED_IS_SYNCED());
                        sHed.setFINVHED_LOCCODE(invHed.getFINVHED_LOCCODE());
                        sHed.setFINVHED_AREACODE(invHed.getFINVHED_AREACODE());
                        sHed.setFINVHED_ROUTECODE(invHed.getFINVHED_ROUTECODE());
                        sHed.setFINVHED_COSTCODE(invHed.getFINVHED_COSTCODE());
                        sHed.setFINVHED_TAXREG(invHed.getFINVHED_TAXREG());
                        sHed.setFINVHED_TOURCODE(invHed.getFINVHED_TOURCODE());
                        sHed.setFINVHED_TOURCODE(invHed.getFINVHED_START_TIME_SO());

                        sHed.setFINVHED_BPTOTALDIS("0");
                        sHed.setFINVHED_BTOTALAMT("0");
                        sHed.setFINVHED_BTOTALDIS("0");
                        sHed.setFINVHED_BTOTALTAX("0");
                        sHed.setFINVHED_END_TIME_SO(currentTime());
                        sHed.setFINVHED_START_TIME_SO(invHed.getFINVHED_START_TIME_SO());
                        sHed.setFINVHED_LATITUDE(mSharedPref.getGlobalVal("Latitude").equals("") ? "0.00" : mSharedPref.getGlobalVal("Latitude"));
                        sHed.setFINVHED_LONGITUDE(mSharedPref.getGlobalVal("Longitude").equals("") ? "0.00" : mSharedPref.getGlobalVal("Longitude"));
                        sHed.setFINVHED_ADDRESS(localSP.getString("GPS_Address", "").toString());
                        sHed.setFINVHED_TOTALTAX("0");
                        sHed.setFINVHED_TOTALDIS("0.0");
                        sHed.setFINVHED_TOTALAMT(lblNetVal.getText().toString());
                        sHed.setFINVHED_TXNDATE(invHed.getFINVHED_TXNDATE());
                        sHed.setFINVHED_REPCODE(new SalRepDS(getActivity()).getCurrentRepCode());
                        sHed.setFINVHED_REFNO1("");
                        sHed.setFINVHED_TOTQTY(lblQty.getText().toString());
                        sHed.setFINVHED_TOTFREEQTY(iTotFreeQty + "");
                        sHed.setFINVHED_SETTING_CODE(invHed.getFINVHED_SETTING_CODE());

                        invHedList.add(sHed);

                        if (new InvHedDS(getActivity()).createOrUpdateInvHed(invHedList) > 0) {
                            new ProductDS(getActivity()).mClearTables();
                            new InvHedDS(getActivity()).InactiveStatusUpdate(RefNo);
                            new InvDetDS(getActivity()).InactiveStatusUpdate(RefNo);

                            final MainActivity activity = (MainActivity) getActivity();
                            if (activity.selectedDebtor.getFDEBTOR_TAX_REG().equals("Y")) {
                                new ReferenceNum(getActivity()).nNumValueInsertOrUpdate(getResources().getString(R.string.VanNumValTax));
                            } else {
                                new ReferenceNum(getActivity()).nNumValueInsertOrUpdate(getResources().getString(R.string.VanNumValNonTax));
                            }
                            FInvRHed mainHead = new FInvRHed();
                            ArrayList<FInvRHed> returnHedList = new ArrayList<FInvRHed>();
                            ArrayList<FInvRHed> HedList = new FInvRHedDS(getActivity()).getAllActiveInvrhed();
                            if (!HedList.isEmpty()) {

                                mainHead.setFINVRHED_REFNO(ReturnRefNo);
                                mainHead.setFINVRHED_DEBCODE(invHed.getFINVHED_DEBCODE());
                                mainHead.setFINVRHED_ADD_DATE(invHed.getFINVHED_ADDDATE());
                                mainHead.setFINVRHED_MANUREF(invHed.getFINVHED_MANUREF());
                                mainHead.setFINVRHED_REMARKS(invHed.getFINVHED_REMARKS());
                                mainHead.setFINVRHED_ADD_MACH(invHed.getFINVHED_ADDMACH());
                                mainHead.setFINVRHED_ADD_USER(invHed.getFINVHED_ADDUSER());
                                mainHead.setFINVRHED_TXN_DATE(HedList.get(0).getFINVRHED_TXN_DATE());
                                mainHead.setFINVRHED_ROUTE_CODE(invHed.getFINVHED_ROUTECODE());
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
                                mainHead.setFINVRHED_INV_REFNO(RefNo);//HedList.get(0).getFINVRHED_INV_REFNO()
                                mainHead.setFINVRHED_IS_ACTIVE("0");
                                mainHead.setFINVRHED_IS_SYNCED("0");
                            }

                            returnHedList.add(mainHead);
                            //Log.v("Ref No :",mainHead.getFINVRHED_INV_REFNO().toString());

                            if (new FInvRHedDS(getActivity()).createOrUpdateInvRHed(returnHedList) > 0) {
                                UpdateReturnTotal(ReturnRefNo);
                                new FInvRDetDS(getActivity()).InactiveStatusUpdate(ReturnRefNo);
                                new FInvRHedDS(getActivity()).InactiveStatusUpdate(ReturnRefNo);

                                activity.selectedReturnHed = null;
                                new ReferenceNum(getActivity()).NumValueUpdate(getResources().getString(R.string.VanReturnNumVal));
                                Toast.makeText(getActivity(), "Return saved successfully !", android.widget.Toast.LENGTH_LONG).show();
                                UtilityContainer.ClearReturnSharedPref(getActivity());
                               // UtilityContainer.mLoadFragment(new SalesReturnHistory(), activity);

//						new SalesPrintPreviewAlertBox(getActivity()).PrintDetailsDialogbox(getActivity(), "Sales return",
//								RefNo);
                            } else {
                                android.widget.Toast.makeText(getActivity(), "Return failed !", android.widget.Toast.LENGTH_LONG)
                                        .show();
                            }


                            /*-*-*-*-*-*-*-*-*-*-QOH update-*-*-*-*-*-*-*-*-*/

                            UpdateTaxDetails(RefNo);
                            UpdateQOH_FIFO();
                            new ItemLocDS(getActivity()).UpdateInvoiceQOH(RefNo, "-", locCode);
                            new ItemLocDS(getActivity()).UpdateInvoiceQOHInReturn(RefNo, "+", locCode);
                            updateDispTables(sHed);
                            int a = new VanSalePrintPreviewAlertBox(getActivity()).PrintDetailsDialogbox(getActivity(), "Print preview", RefNo);

                            android.widget.Toast.makeText(getActivity(), "Invoice saved successfully..!", android.widget.Toast.LENGTH_SHORT).show();
                            UtilityContainer.ClearVanSharedPref(getActivity());
                            activity.cusPosition = 0;
                            activity.selectedDebtor = null;
                            activity.selectedRetDebtor = null;
                            activity.selectedReturnHed = null;
                            activity.selectedInvHed = null;
                            loadFragment(new VanSaleInvoice());


                        } else {
                            android.widget.Toast.makeText(getActivity(), "Failed..", android.widget.Toast.LENGTH_SHORT).show();
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
            else
            {
                // save only invoice
                //Changed By Yasith - 2019-01-29
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptView = layoutInflater.inflate(R.layout.sales_management_van_sales_summary_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Do you want to save the invoice ?");
                alertDialogBuilder.setView(promptView);

                final ListView lvProducts_Invoice = (ListView) promptView.findViewById(R.id.lvProducts_Summary_Dialog_Inv);
                ArrayList<InvDet> invoiceItemList = null;
                invoiceItemList = new InvDetDS(getActivity()).getAllItemsAddedInCurrentSale(RefNo);
                lvProducts_Invoice.setAdapter(new InvDetAdapter(getActivity(), invoiceItemList));

            alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(final DialogInterface dialog, int id) {

                    InvHed sHed = new InvHed();
                    ArrayList<InvHed> invHedList = new ArrayList<InvHed>();

                    InvHed invHed = new InvHedDS(getActivity()).getActiveInvhed();

                    sHed.setFINVHED_REFNO(RefNo);
                    sHed.setFINVHED_DEBCODE(invHed.getFINVHED_DEBCODE());
                    sHed.setFINVHED_ADDDATE(invHed.getFINVHED_ADDDATE());
                    sHed.setFINVHED_MANUREF(invHed.getFINVHED_MANUREF());
                    sHed.setFINVHED_REMARKS(invHed.getFINVHED_REMARKS());
                    sHed.setFINVHED_ADDMACH(invHed.getFINVHED_ADDMACH());
                    sHed.setFINVHED_ADDUSER(invHed.getFINVHED_ADDUSER());
                    sHed.setFINVHED_CURCODE(invHed.getFINVHED_CURCODE());
                    sHed.setFINVHED_CURRATE(invHed.getFINVHED_CURRATE());
                    sHed.setFINVHED_LOCCODE(invHed.getFINVHED_LOCCODE());

                    sHed.setFINVHED_CUSTELE(invHed.getFINVHED_CUSTELE());
                    sHed.setFINVHED_CONTACT(invHed.getFINVHED_CONTACT());
                    sHed.setFINVHED_CUSADD1(invHed.getFINVHED_CUSADD1());
                    sHed.setFINVHED_CUSADD2(invHed.getFINVHED_CUSADD2());
                    sHed.setFINVHED_CUSADD3(invHed.getFINVHED_CUSADD3());
                    sHed.setFINVHED_TXNTYPE(invHed.getFINVHED_TXNTYPE());
                    sHed.setFINVHED_IS_ACTIVE(invHed.getFINVHED_IS_ACTIVE());
                    sHed.setFINVHED_IS_SYNCED(invHed.getFINVHED_IS_SYNCED());
                    sHed.setFINVHED_LOCCODE(invHed.getFINVHED_LOCCODE());
                    sHed.setFINVHED_AREACODE(invHed.getFINVHED_AREACODE());
                    sHed.setFINVHED_ROUTECODE(invHed.getFINVHED_ROUTECODE());
                    sHed.setFINVHED_COSTCODE(invHed.getFINVHED_COSTCODE());
                    sHed.setFINVHED_TAXREG(invHed.getFINVHED_TAXREG());
                    sHed.setFINVHED_TOURCODE(invHed.getFINVHED_TOURCODE());
                    sHed.setFINVHED_START_TIME_SO(invHed.getFINVHED_START_TIME_SO());

                    sHed.setFINVHED_BPTOTALDIS("0");
                    sHed.setFINVHED_BTOTALAMT("0");
                    sHed.setFINVHED_BTOTALDIS("0");
                    sHed.setFINVHED_BTOTALTAX("0");
                    sHed.setFINVHED_END_TIME_SO(currentTime());
                  //  sHed.setFINVHED_START_TIME_SO(localSP.getString("Van_Start_Time", "").toString());
                    sHed.setFINVHED_LATITUDE(mSharedPref.getGlobalVal("Latitude").equals("") ? "0.00" : mSharedPref.getGlobalVal("Latitude"));
                    sHed.setFINVHED_LONGITUDE(mSharedPref.getGlobalVal("Longitude").equals("") ? "0.00" : mSharedPref.getGlobalVal("Longitude"));
                    sHed.setFINVHED_ADDRESS(localSP.getString("GPS_Address", "").toString());
                    sHed.setFINVHED_TOTALTAX("0");
                    sHed.setFINVHED_TOTALDIS("0.0");
                    sHed.setFINVHED_TOTALAMT(lblNetVal.getText().toString());
                    sHed.setFINVHED_TXNDATE(invHed.getFINVHED_TXNDATE());
                    sHed.setFINVHED_REPCODE(new SalRepDS(getActivity()).getCurrentRepCode());
                    sHed.setFINVHED_REFNO1("");
                    sHed.setFINVHED_TOTQTY(lblQty.getText().toString());
                    sHed.setFINVHED_TOTFREEQTY(iTotFreeQty + "");
                    sHed.setFINVHED_SETTING_CODE(invHed.getFINVHED_SETTING_CODE());

                    invHedList.add(sHed);

                    if (new InvHedDS(getActivity()).createOrUpdateInvHed(invHedList) > 0) {
                        new ProductDS(getActivity()).mClearTables();
                        new InvHedDS(getActivity()).InactiveStatusUpdate(RefNo);
                        new InvDetDS(getActivity()).InactiveStatusUpdate(RefNo);

                        final MainActivity activity = (MainActivity) getActivity();
                        if (activity.selectedDebtor.getFDEBTOR_TAX_REG().equals("Y")) {
                            new ReferenceNum(getActivity()).nNumValueInsertOrUpdate(getResources().getString(R.string.VanNumValTax));
                        } else {
                            new ReferenceNum(getActivity()).nNumValueInsertOrUpdate(getResources().getString(R.string.VanNumValNonTax));
                        }



                        /*-*-*-*-*-*-*-*-*-*-QOH update-*-*-*-*-*-*-*-*-*/

                        UpdateTaxDetails(RefNo);
                        UpdateQOH_FIFO();
                        new ItemLocDS(getActivity()).UpdateInvoiceQOH(RefNo, "-", locCode);
                        updateDispTables(sHed);
                        int a = new VanSalePrintPreviewAlertBox(getActivity()).PrintDetailsDialogbox(getActivity(), "Print preview", RefNo);

                        //if(a == 1)
                        //{
                            android.widget.Toast.makeText(getActivity(), "Invoice saved successfully..!", android.widget.Toast.LENGTH_SHORT).show();
                            UtilityContainer.ClearVanSharedPref(getActivity());
                            activity.cusPosition = 0;
                            activity.selectedDebtor = null;
                            activity.selectedRetDebtor = null;
                            activity.selectedRecHed = null;
                            activity.selectedInvHed = null;
                            loadFragment(new VanSaleInvoice());
                        //}

                    } else {
                        android.widget.Toast.makeText(getActivity(), "Failed..", android.widget.Toast.LENGTH_SHORT).show();
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

        } else
            Toast.makeText(activity, "Add items before save ...!", Toast.LENGTH_SHORT).show();


    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public void UpdateTaxDetails(String refNo) {
        ArrayList<InvDet> list = new InvDetDS(activity).getAllInvDet(refNo);
        new InvDetDS(activity).UpdateItemTaxInfo(list);
        new InvTaxRGDS(activity).UpdateInvTaxRG(list);
        new InvTaxDTDS(activity).UpdateInvTaxDT(list);
    }
    public void UpdateReturnTotal(String refNo) {
        ArrayList<FInvRDet> list = new FInvRDetDS(activity).getAllInvRDet(refNo);
        new FInvRDetDS(activity).UpdateReturnTot(list);

    }
	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    private String currentTime() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    private void UpdateQOH_FIFO() {

        ArrayList<InvDet> list = new InvDetDS(getActivity()).getAllInvDet(RefNo);

		/*-*-*-*-*-*-*-*-*-*-*-*-each itemcode has multiple sizecodes*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*/
        for (InvDet item : list) {

            int Qty = (int) Double.parseDouble(item.getFINVDET_QTY());

            ArrayList<StkIn> GRNList = new STKInDS(activity).getAscendingGRNList(item.getFINVDET_ITEM_CODE(), locCode);

			/*-*-*-*-*-*-*-*-*-*-multiple GRN for each sizecode-*-*-*-*-*-*-*-*-*-*-*/
            for (StkIn size : GRNList) {
                int balQty = (int) Double.parseDouble(size.getBALQTY());

                if (balQty > 0) {
                    if (Qty > balQty) {
                        Qty = Qty - balQty;
                        size.setBALQTY("0");
                        new StkIssDS(activity).InsertSTKIssData(size, RefNo, String.valueOf(balQty), locCode);

                    } else {
                        size.setBALQTY(String.valueOf(balQty - Qty));
                        new StkIssDS(activity).InsertSTKIssData(size, RefNo, String.valueOf(Qty), locCode);
                        break;
                    }
                }
            }
            new STKInDS(activity).UpdateBalQtyByFIFO(GRNList);
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public void updateDispTables(InvHed invHed) {

        String dispREfno = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.DispVal));

        int res = new DispHedDS(getActivity()).updateHeader(invHed, dispREfno);

        if (res > 0) {
            ArrayList<InvDet> list = new InvDetDS(getActivity()).getAllInvDet(invHed.getFINVHED_REFNO());
            new DispDetDS(getActivity()).updateDispDet(list, dispREfno);
            new DispIssDS(getActivity()).updateDispIss(new StkIssDS(getActivity()).getUploadData(invHed.getFINVHED_REFNO()), dispREfno);
            new ReferenceNum(getActivity()).nNumValueInsertOrUpdate(getResources().getString(R.string.DispVal));
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public void mPauseinvoice() {

        if (new InvDetDS(getActivity()).getItemCount(RefNo) > 0) {
            loadFragment(new IconPallet_mega());
        } else
            Toast.makeText(activity, "Add items before pause ...!", Toast.LENGTH_SHORT).show();
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_SUMMARY"));
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public void loadFragment(Fragment fragment) {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
        ft.replace(R.id.main_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public void updateInvoice() {

        ArrayList<Product> list = new ProductDS(getActivity()).getSelectedItems();

        int i = 0;

        for (Product product : list) {

            MainActivity activity = (MainActivity) getActivity();
            double totAmt = Double.parseDouble(product.getFPRODUCT_PRICE()) * Double.parseDouble(product.getFPRODUCT_QTY());
            String TaxedAmt = new TaxDetDS(getActivity()).calculateTax(product.getFPRODUCT_ITEMCODE(), new BigDecimal(totAmt));

            double brandDiscPer = new DebItemPriDS(getActivity()).getBrandDiscount(new ItemsDS(getActivity()).getBrandCode(product.getFPRODUCT_ITEMCODE()), activity.selectedDebtor.getFDEBTOR_CODE());
            double compDiscPer = new ControlDS(getActivity()).getCompanyDisc();

            double Disc = (totAmt / 100) * compDiscPer;
            double compDisc = Disc;
            totAmt -= Disc;

            Disc = (totAmt / 100) * brandDiscPer;
            double brandDisc = Disc;
            totAmt -= Disc;

            InvDet invDet = new InvDet();

            invDet.setFINVDET_ID(String.valueOf(i++));
            invDet.setFINVDET_AMT(String.format("%.2f", totAmt - Double.parseDouble(TaxedAmt)));
            invDet.setFINVDET_BAL_QTY(product.getFPRODUCT_QTY());
            invDet.setFINVDET_B_AMT(invDet.getFINVDET_AMT());
            invDet.setFINVDET_B_SELL_PRICE(product.getFPRODUCT_PRICE());
            invDet.setFINVDET_BT_SELL_PRICE(product.getFPRODUCT_PRICE());
            invDet.setFINVDET_DIS_AMT(String.format("%.2f", compDisc + brandDisc));
            invDet.setFINVDET_DIS_PER("0");
            invDet.setFINVDET_ITEM_CODE(product.getFPRODUCT_ITEMCODE());
            invDet.setFINVDET_PRIL_CODE(activity.selectedDebtor.getFDEBTOR_PRILLCODE());
            invDet.setFINVDET_QTY(product.getFPRODUCT_QTY());
            invDet.setFINVDET_PICE_QTY(product.getFPRODUCT_QTY());
            invDet.setFINVDET_TYPE("SA");
            invDet.setFINVDET_BT_TAX_AMT("");
            invDet.setFINVDET_RECORD_ID("");


        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            VanSalesSummary.this.mRefreshData();
        }
    }


    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/
    public void updateQtyInItemLocTblItemWise()
    {
        try
        {
            ArrayList<InvDet> list = new InvDetDS(getActivity()).getAllInvDet(RefNo);

		/*-*-*-*-*-*-*-*-*-*-*-*-each itemcode has multiple sizecodes*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*/
            for (InvDet item : list)
            {
                int Qty = (int) Double.parseDouble(item.getFINVDET_QTY());
                ArrayList<StkIn> GRNList = new STKInDS(activity).getAscendingGRNList(item.getFINVDET_ITEM_CODE(), locCode);

            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    //------------------------------------------------------------------------------------------------------------------------------------
    public class LoardingPrintView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            new VanSalePrintPreviewAlertBox(getActivity()).PrintDetailsDialogbox(getActivity(), "Print preview", RefNo);
            return null;

        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(pDialog.isShowing()){
                pDialog.dismiss();
            }
            final MainActivity activity = (MainActivity) getActivity();
            android.widget.Toast.makeText(getActivity(), "Invoice saved successfully..!", android.widget.Toast.LENGTH_SHORT).show();
            UtilityContainer.ClearVanSharedPref(getActivity());
            activity.cusPosition = 0;
            activity.selectedDebtor = null;
            activity.selectedRetDebtor = null;
            activity.selectedRecHed = null;
            activity.selectedInvHed = null;
            loadFragment(new VanSaleInvoice());

        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/


}
