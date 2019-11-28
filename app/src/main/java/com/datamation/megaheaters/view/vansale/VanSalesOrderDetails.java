package com.datamation.megaheaters.view.vansale;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.FreeIssueAdapter;
import com.datamation.megaheaters.adapter.FreeItemsAdapter;
import com.datamation.megaheaters.adapter.InvDetAdapter;
import com.datamation.megaheaters.adapter.InvoiceDiscountAdapter;

import com.datamation.megaheaters.adapter.NewProduct_Adapter;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.data.ControlDS;
import com.datamation.megaheaters.data.InvDetDS;
import com.datamation.megaheaters.data.ItemPriDS;
import com.datamation.megaheaters.data.ItemsDS;
import com.datamation.megaheaters.data.OrdFreeIssueDS;
import com.datamation.megaheaters.data.ProductDS;
import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.data.TaxDetDS;
import com.datamation.megaheaters.model.FreeIssue;
import com.datamation.megaheaters.model.FreeItemDetails;
import com.datamation.megaheaters.model.InvDet;
import com.datamation.megaheaters.model.InvHed;
import com.datamation.megaheaters.model.ItemFreeIssue;
import com.datamation.megaheaters.model.OrdFreeIssue;
import com.datamation.megaheaters.model.Product;
import com.datamation.megaheaters.view.MainActivity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class VanSalesOrderDetails extends Fragment {

    View view;
    int totPieces = 0;
    int seqno = 0;
    ListView lv_order_det, lvFree;
    ArrayList<InvDet> orderList;
    SharedPref mSharedPref;
    String RefNo, locCode;
    MainActivity mainActivity;
    MyReceiver r;
    ArrayList<Product> productList = null, selectedItemList = null;
    ImageButton ibtProduct, ibtDiscount;
    private  SweetAlertDialog pDialog;
    private InvHed tmpinvHed=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        seqno = 0;
        totPieces = 0;
        view = inflater.inflate(R.layout.sales_management_van_sales_new_details, container, false);
        mSharedPref = new SharedPref(getActivity());
        locCode = new SharedPref(getActivity()).getGlobalVal("KeyLocCode");
        mainActivity = (MainActivity) getActivity();
        if(mainActivity.selectedDebtor != null)
        {
            if(mainActivity.selectedDebtor.getFDEBTOR_TAX_REG().equals("Y")){
                RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanNumValTax));
            }else {
                RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanNumValNonTax));
            }
        }
        else
        {
            Toast.makeText(mainActivity,"Select a customer to continue...",Toast.LENGTH_SHORT);
        }

        lv_order_det = (ListView) view.findViewById(R.id.lvProducts_Inv);
        lvFree = (ListView) view.findViewById(R.id.lvFreeIssue_Inv);

        ibtDiscount = (ImageButton) view.findViewById(R.id.ibtDisc);
        ibtProduct = (ImageButton) view.findViewById(R.id.ibtProduct);
        tmpinvHed=new InvHed();
        showData();

        ibtDiscount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ibtDiscount.setBackground(getResources().getDrawable(R.drawable.discount_down));
                    }
                    break;

                    case MotionEvent.ACTION_UP: {
                        ibtDiscount.setBackground(getResources().getDrawable(R.drawable.discount));
                    }
                    break;
                }
                return false;
            }
        });

        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*//

        ibtProduct.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ibtProduct.setBackground(getResources().getDrawable(R.drawable.product_down));
                    }
                    break;

                    case MotionEvent.ACTION_UP: {
                        ibtProduct.setBackground(getResources().getDrawable(R.drawable.product));
                    }
                    break;
                }
                return false;
            }
        });

        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*//

        ibtProduct.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                 new LoardingProductFromDB().execute();
            }
        });



        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*//

        ibtDiscount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateFreeIssue();
            }
        });

        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*//

        lv_order_det.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new InvDetDS(getActivity()).restFreeIssueData(RefNo);
                //new OrdFreeIssueDS(getActivity()).ClearFreeIssues(RefNo);
                newDeleteOrderDialog(position);
                return true;
            }
        });

        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*//*

        lv_order_det.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                /*new InvDetDS(getActivity()).restFreeIssueData(RefNo);
                new OrdFreeIssueDS(getActivity()).ClearFreeIssues(RefNo);
                InvDet invDet = orderList.get(position);

                FreeIssue issue = new FreeIssue(getActivity());
                ArrayList<FreeItemDetails> list = issue.getEligibleFreeItemsByInvoiceItem( orderList.get(position), new SharedPref(getActivity()).getGlobalVal("KeyCostCode"));
                popEditDialogBox(invDet,list);*/

            }
        });

        return view;

    }

    //------------------------------------------------------------------------------------------------------------------------------------
    public class LoardingProductFromDB extends AsyncTask<Object, Object, ArrayList<Product>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Fetch Data Please Wait.");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected ArrayList<Product> doInBackground(Object... objects) {

            if (new ProductDS(getActivity()).tableHasRecords()) {
                //productList = new ProductDS(getActivity()).getAllItems("");
                productList = new ProductDS(getActivity()).getAllItems("","SA");//rashmi 2018-10-26
            } else {
                //rashmi 2018-10-22 - (getAllItemForVanSales) for mega.because debtor has no prillcode, it get from fitems
                //productList = new ItemsDS(getActivity()).getAllItemForVanSales(new SalRepDS(getActivity()).getCurrentLocCode().trim(), mainActivity.selectedDebtor.getFDEBTOR_PRILLCODE());
                //new ProductDS(getActivity()).insertOrUpdateProducts(productList);

                //yasith 2019-01-14
               // new ProductDS(getActivity()).insertIntoProductAsBulk(new SalRepDS(getActivity()).getCurrentLocCode().trim(), mainActivity.selectedDebtor.getFDEBTOR_PRILLCODE());
               //rashmi 2019-03-12 prilcode get from fsalrep
                new ProductDS(getActivity()).insertIntoProductAsBulk(new SalRepDS(getActivity()).getCurrentLocCode().trim(), new SalRepDS(getActivity()).getCurrentPriLCode().trim());

                if(tmpinvHed!=null) {

                   ArrayList<InvDet> invDetArrayList = tmpinvHed.getInvDetArrayList();
                   if (invDetArrayList != null) {
                       for (int i = 0; i < invDetArrayList.size(); i++) {
                           String tmpItemName = invDetArrayList.get(i).getFINVDET_ITEM_CODE();
                           String tmpQty = invDetArrayList.get(i).getFINVDET_QTY();
                           //Update Qty in  fProducts_pre table
                           int count = new ProductDS(getActivity()).updateProductQtyfor(tmpItemName, tmpQty);
                           if (count > 0) {

                               Log.d("InsertOrUpdate", "success");
                           } else {
                               Log.d("InsertOrUpdate", "Failed");
                           }

                       }
                   }
               }
                //----------------------------------------------------------------------------
            }
            //productList = new ProductDS(getActivity()).getAllItems("","SA");//rashmi -2018-10-26
            return productList;
        }


        @Override
        protected void onPostExecute(ArrayList<Product> products) {
            super.onPostExecute(products);

            if(pDialog.isShowing()){
                pDialog.dismiss();
            }
            ProductDialogBox();
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void calculateFreeIssue() {

        //ibtDiscount.setClickable(false);

			/* GET CURRENT ORDER DETAILS FROM TABLE */
        ArrayList<InvDet> dets = new InvDetDS(getActivity()).getSAForFreeIssueCalc(RefNo);
        //ArrayList<InvDet> dets = new ProductDS(getActivity()).getSelectedItemsForInvoice(RefNo);

            /* CLEAR ORDERDET TABLE RECORD IF FREE ITEMS ARE ALREADY ADDED. */
        new InvDetDS(getActivity()).restFreeIssueData(RefNo);
            /* Clear free issues in OrdFreeIss */
        new OrdFreeIssueDS(getActivity()).ClearFreeIssues(RefNo);
            /*
             * JUST LOAD ORDER ITEMS TO LISTVIEW & CLEAR FREE ITEM FROM LISTVIEW
			 * USER ALREADY RETREIVED FREE ISSUES
			 */
        //FetchData();

        FreeIssue issue = new FreeIssue(getActivity());

			/* Get discounts for assorted items */
        ArrayList<ArrayList<InvDet>> metaOrdList = issue.SortInvoiceDiscount(dets);

			/* Iterate through for discounts for items */
        for (ArrayList<InvDet> OrderList : metaOrdList) {
            // DiscountDialogBox(OrderList);

            double totAmt = 0;
            double freeVal = Double.parseDouble(OrderList.get(0).getFINVDET_B_AMT());
            String discPer = OrderList.get(0).getFINVDET_SCHDISPER();
            String discType = OrderList.get(0).getFINVDET_DISCTYPE();
            String discRef = OrderList.get(0).getFINVDET_DISC_REF();
            OrderList.get(0).setFINVDET_B_AMT("0");

            for (InvDet det : OrderList)
                totAmt += Double.parseDouble(det.getFINVDET_PRICE()) * (Double.parseDouble(det.getFINVDET_QTY()));

            for (InvDet det : OrderList) {
                det.setFINVDET_SCHDISPER(discPer);
                det.setFINVDET_DISCTYPE(discType);
                det.setFINVDET_DISC_REF(discRef);

                double disc;

					/*
                     * For value, calculate amount portion & for percentage ,
					 * calculate percentage portion
					 */
                disc = (freeVal / totAmt) * Double.parseDouble(det.getFINVDET_PRICE()) * (Double.parseDouble(det.getFINVDET_QTY()));

					/* Calculate discount amount from disc percentage portion */
                if (discType.equals("P"))
                    disc = (Double.parseDouble(det.getFINVDET_AMT()) / 100) * disc;

                new InvDetDS(getActivity()).updateDiscount(det, disc, det.getFINVDET_DISCTYPE());
            }
        }

        // GET ARRAY OF FREE ITEMS BY PASSING IN ORDER DETAILS
        ArrayList<FreeItemDetails> list = issue.getFreeItemsByInvoiceItem(dets, new SharedPref(getActivity()).getGlobalVal("KeyCostCode"));
        // PASS EACH ITEM IN TO DIALOG BOX FOR USER SELECTION
        for (FreeItemDetails freeItemDetails : list) {
            freeIssueDialogBox(freeItemDetails);

        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    /*@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch_Inv:

                break;

            case R.id.btn_add_Inv: {

                if (!lblProduct.getText().toString().equals("")) {

                    if (values >= 0 && !(txtQty.getText().toString().equals("")) && Integer.parseInt(txtQty.getText().toString()) > 0) {

                        InvDet invDet = new InvDet();
                        ArrayList<InvDet> ordList = new ArrayList<>();

                        MainActivity activity = (MainActivity) getActivity();
                        InvHedDS invHedDS = new InvHedDS(getActivity());
                        ArrayList<InvHed> invHedList = new ArrayList<>();
                        invHedList.add(activity.selectedInvHed);

                        if (invHedDS.getActiveInvoiceRef() != null) {

                            String TaxedAmt = new TaxDetDS(getActivity()).calculateTax(selectedItem.getFITEM_ITEM_CODE(), new BigDecimal(lblAmt.getText().toString()));

                            invDet.setFINVDET_ID(index_id + "");
                            invDet.setFINVDET_AMT(String.format("%.2f", Double.parseDouble(lblAmt.getText().toString()) - Double.parseDouble(TaxedAmt)));
                            invDet.setFINVDET_BAL_QTY(txtQty.getText().toString());
                            invDet.setFINVDET_B_AMT(String.format("%.2f", Double.parseDouble(lblAmt.getText().toString()) - Double.parseDouble(TaxedAmt)));
                            invDet.setFINVDET_B_SELL_PRICE(lblPrice.getText().toString());
                            invDet.setFINVDET_BT_SELL_PRICE(lblPrice.getText().toString());
                            invDet.setFINVDET_DIS_AMT(lblDisc.getText().toString());
                            invDet.setFINVDET_DIS_PER("0");
                            invDet.setFINVDET_ITEM_CODE(selectedItem.getFITEM_ITEM_CODE());
                            invDet.setFINVDET_PRIL_CODE(activity.selectedDebtor.getFDEBTOR_PRILLCODE());
                            invDet.setFINVDET_QTY(txtQty.getText().toString());
                            invDet.setFINVDET_PICE_QTY(txtQty.getText().toString());
                            invDet.setFINVDET_TYPE("SA");
                            invDet.setFINVDET_BT_TAX_AMT("");
                            invDet.setFINVDET_RECORD_ID("");
                            invDet.setFINVDET_SELL_PRICE(String.format("%.2f", (Double.parseDouble(lblAmt.getText().toString()) - Double.parseDouble(TaxedAmt)) / Double.parseDouble(invDet.getFINVDET_QTY())));
                            invDet.setFINVDET_B_SELL_PRICE(String.format("%.2f", (Double.parseDouble(lblAmt.getText().toString()) - Double.parseDouble(TaxedAmt)) / Double.parseDouble(invDet.getFINVDET_QTY())));
                            invDet.setFINVDET_SEQNO(new InvDetDS(getActivity()).getLastSequnenceNo(activity.selectedInvHed.getFINVHED_REFNO()));
                            invDet.setFINVDET_TAX_AMT(TaxedAmt);
                            invDet.setFINVDET_TAX_COM_CODE(new ItemsDS(getActivity()).getTaxComCodeByItemCode(selectedItem.getFITEM_ITEM_CODE()));
                            invDet.setFINVDET_T_SELL_PRICE(String.format("%.2f", Double.parseDouble(lblAmt.getText().toString()) / Double.parseDouble(invDet.getFINVDET_QTY())));
                            invDet.setFINVDET_BT_SELL_PRICE(String.format("%.2f", Double.parseDouble(lblAmt.getText().toString()) / Double.parseDouble(invDet.getFINVDET_QTY())));
                            invDet.setFINVDET_REFNO(activity.selectedInvHed.getFINVHED_REFNO());
                            invDet.setFINVDET_COM_DISCPER(new ControlDS(getActivity()).getCompanyDisc() + "");
                            invDet.setFINVDET_BRAND_DISCPER(brandDisPer + "");
                            invDet.setFINVDET_BRAND_DISC(String.format("%.2f", brandDis));
                            invDet.setFINVDET_COMDISC(String.format("%.2f", CompDisc));
                            invDet.setFINVDET_TXN_DATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                            invDet.setFINVDET_TXN_TYPE("22");
                            invDet.setFINVDET_IS_ACTIVE("1");
                            invDet.setFINVDET_QOH(iQoh + "");
                            invDet.setFINVDET_DISVALAMT("0");
                            invDet.setFINVDET_PRICE(lblPrice.getText().toString());

                            ordList.add(invDet);

                            if (!(btnAdd.getText().equals("EDIT") && hasChanged == false)) {
                                new InvDetDS(getActivity()).createOrUpdateInvDet(ordList);
                            }

                            if (btnAdd.getText().equals("EDIT"))
                                android.widget.Toast.makeText(getActivity(), "Edited successfully !", android.widget.Toast.LENGTH_SHORT).show();
                            else
                                android.widget.Toast.makeText(getActivity(), "Added successfully !", android.widget.Toast.LENGTH_SHORT).show();

                            txtQty.setEnabled(false);
                            clearTextFields();
                            //FetchData();
                            UtilityContainer.hideKeyboard(getActivity());
                        }
                    } else {
                        android.widget.Toast.makeText(getActivity(), "Enter item quantity..!", android.widget.Toast.LENGTH_SHORT).show();

                    }
                }
            }
            break;


            default:
                break;
        }
    }*/

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void showData() {
        try {
            orderList = new InvDetDS(getActivity()).getAllInvDet(mainActivity.selectedInvHed.getFINVHED_REFNO());
            //ArrayList<InvDet> freeList = new InvDetDS(getActivity()).getAllFreeIssue(mainActivity.selectedInvHed.getFINVHED_REFNO());
            lv_order_det.setAdapter(new InvDetAdapter(getActivity(), orderList));
            //lvFree.setAdapter(new FreeItemsAdapter(getActivity(), freeList));
        } catch (NullPointerException e) {
            Log.v("SA Error", e.toString());
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private boolean DiscountDialogBox(final ArrayList<InvDet> OrderList) {

        // get discount value from the first instance
        final double DiscValue = Double.parseDouble(OrderList.get(0).getFINVDET_B_AMT());
        OrderList.get(0).setFINVDET_B_AMT("0.00");
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.free_issues_items_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Please allocate discounts...");
        alertDialogBuilder.setView(promptView);

        final ListView listView = (ListView) promptView.findViewById(R.id.lv_free_issue);
        final TextView itemName = (TextView) promptView.findViewById(R.id.tv_free_issue_item_name);
        final TextView freeQty = (TextView) promptView.findViewById(R.id.tv_free_qty);

        freeQty.setText("Discount you have : " + DiscValue);

        listView.setAdapter(new InvoiceDiscountAdapter(getActivity(), OrderList));
        listView.setOnItemClickListener(new OnItemClickListener() {

            // double avaliableQty = getAvailableTotal(OrderList);

            @Override
            public void onItemClick(AdapterView<?> parent, View view2, final int position, long id) {

                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

                View promptView = layoutInflater.inflate(R.layout.set_free_issue_dialog, null);

                final TextView leftQty = (TextView) promptView.findViewById(R.id.tv_free_item_left_qty);
                final EditText enteredQty = (EditText) promptView.findViewById(R.id.et_free_qty);

                leftQty.setText("Discount : " + getAvailableTotal(DiscValue, OrderList));
                enteredQty.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        if (enteredQty.getText().toString().equals("")) {

                        } else {
                            if ((getAvailableTotal(DiscValue, OrderList) - (Double.parseDouble(enteredQty.getText().toString()))) < 0) {
                                enteredQty.setText("");
                                leftQty.setText("Discount : " + (getAvailableTotal(DiscValue, OrderList)));
                                android.widget.Toast.makeText(getActivity(), "No more discounts remaining ..!", android.widget.Toast.LENGTH_SHORT).show();
                            } else {
                                leftQty.setText("Discount left : " + (getAvailableTotal(DiscValue, OrderList) - (Double.parseDouble(enteredQty.getText().toString()))));
                            }

                        }
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptView);

                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (!(enteredQty.getText().toString().equals(""))) {
                            OrderList.get(position).setFINVDET_DISVALAMT(enteredQty.getText().toString());
                            freeQty.setText("Discount left : " + getAvailableTotal(DiscValue, OrderList));
                        }

                        listView.clearTextFilter();
                        listView.setAdapter(new InvoiceDiscountAdapter(getActivity(), OrderList));

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
                AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();

            }
        });

        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new InvDetDS(getActivity()).UpdateArrayDiscount(OrderList);
                android.widget.Toast.makeText(getActivity(), "Discount updated successfully..", android.widget.Toast.LENGTH_SHORT).show();

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return true;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public double getAvailableTotal(double discVal, ArrayList<InvDet> OrderList) {
        double avQTY = 0;

        for (InvDet mOrdDet : OrderList) {
            avQTY = avQTY + Double.parseDouble(mOrdDet.getFINVDET_DIS_AMT());
            System.out.println(mOrdDet.getFINVDET_DIS_AMT() + " - " + mOrdDet.getFINVDET_ITEM_CODE());
        }

        return (discVal - avQTY);

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private boolean freeIssueDialogBox(final FreeItemDetails itemDetails) {

        final ArrayList<ItemFreeIssue> itemFreeIssues;
        final String FIRefNo = itemDetails.getRefno();
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.free_issues_items_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("FREE PRODUCTS");
        alertDialogBuilder.setView(promptView);

        final ListView listView = (ListView) promptView.findViewById(R.id.lv_free_issue);
        final TextView itemName = (TextView) promptView.findViewById(R.id.tv_free_issue_item_name);
        final TextView freeQty = (TextView) promptView.findViewById(R.id.tv_free_qty);

        freeQty.setText("Free Quantity : " + itemDetails.getFreeQty());
        itemName.setText("Product : " + new ItemsDS(getActivity()).getItemNameByCode(itemDetails.getFreeIssueSelectedItem()));

        final ItemsDS itemsDS = new ItemsDS(getActivity());
        itemFreeIssues = itemsDS.getAllFreeItemNameByRefno(itemDetails.getRefno());
        listView.setAdapter(new FreeIssueAdapter(getActivity(), itemFreeIssues));

        listView.setOnItemClickListener(new OnItemClickListener() {

            int avaliableQty = 0;

            @Override
            public void onItemClick(AdapterView<?> parent, View view2, final int position, long id) {

                if (itemDetails.getFreeQty() > 0) {

                    ItemFreeIssue freeIssue = itemFreeIssues.get(position);
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View promptView = layoutInflater.inflate(R.layout.set_free_issue_dialog, null);
                    final TextView leftQty = (TextView) promptView.findViewById(R.id.tv_free_item_left_qty);
                    final EditText enteredQty = (EditText) promptView.findViewById(R.id.et_free_qty);

                    leftQty.setText("Free Items Left = " + itemDetails.getFreeQty());

                    enteredQty.addTextChangedListener(new TextWatcher() {
                        public void afterTextChanged(Editable s) {

                            if (enteredQty.getText().toString().equals("")) {

                                leftQty.setText("Free Items Left = " + itemDetails.getFreeQty());

                            } else {
                                avaliableQty = itemDetails.getFreeQty() - Integer.parseInt(enteredQty.getText().toString());

                                if (avaliableQty < 0) {
                                    enteredQty.setText("");
                                    leftQty.setText("Free Items Left = " + itemDetails.getFreeQty());
                                    android.widget.Toast.makeText(getActivity(), "You don't have enough sufficient quantity to order", android.widget.Toast.LENGTH_SHORT).show();
                                } else {
                                    leftQty.setText("Free Items Left = " + avaliableQty);
                                }
                            }
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                    });

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle(freeIssue.getItems().getFITEM_ITEM_NAME());
                    alertDialogBuilder.setView(promptView);

                    alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            itemDetails.setFreeQty(avaliableQty);
                            freeQty.setText("Free Qty you have : " + itemDetails.getFreeQty());
                            itemFreeIssues.get(position).setAlloc(enteredQty.getText().toString());
                            listView.clearTextFilter();
                            listView.setAdapter(new FreeIssueAdapter(getActivity(), itemFreeIssues));
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });

                    AlertDialog alertD = alertDialogBuilder.create();

                    alertD.show();
                } else {
                    android.widget.Toast.makeText(getActivity(), "You don't have enough sufficient quantity to order", android.widget.Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                for (ItemFreeIssue itemFreeIssue : itemFreeIssues) {

                    if (Integer.parseInt(itemFreeIssue.getAlloc()) > 0) {

                        seqno++;
                        InvDet ordDet = new InvDet();
                        ArrayList<InvDet> ordList = new ArrayList<>();

                        ordDet.setFINVDET_ID("0");
                        ordDet.setFINVDET_AMT("0");
                        ordDet.setFINVDET_BAL_QTY(itemFreeIssue.getAlloc());
                        ordDet.setFINVDET_B_AMT("0");
                        //String unitPrice = new ItemPriDS(getActivity()).getProductPriceByCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE(), mainActivity.selectedDebtor.getFDEBTOR_PRILLCODE());
                       //rashmi 2019-03-12
                        String unitPrice = new ItemPriDS(getActivity()).getProductPriceByCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE(), new SalRepDS(getActivity()).getCurrentPriLCode().trim());
                        ordDet.setFINVDET_B_SELL_PRICE("0");
                        ordDet.setFINVDET_BT_SELL_PRICE(unitPrice);
                        ordDet.setFINVDET_DIS_AMT("0");
                        ordDet.setFINVDET_SCHDISPER("0");
                        ordDet.setFINVDET_FREEQTY("0");
                        ordDet.setFINVDET_ITEM_CODE(itemFreeIssue.getItems().getFITEM_ITEM_CODE());
                        ordDet.setFINVDET_PRIL_CODE(new SalRepDS(getActivity()).getCurrentPriLCode().trim());
                        ordDet.setFINVDET_QTY(itemFreeIssue.getAlloc());
                        ordDet.setFINVDET_PICE_QTY(itemFreeIssue.getAlloc());
                        ordDet.setFINVDET_TYPE("FI");
                        ordDet.setFINVDET_RECORD_ID("");
                        ordDet.setFINVDET_REFNO(RefNo);
                        ordDet.setFINVDET_SELL_PRICE("0");
                        ordDet.setFINVDET_SEQNO(seqno + "");
                        ordDet.setFINVDET_TAX_AMT("0");
                        ordDet.setFINVDET_TAX_COM_CODE(new ItemsDS(getActivity()).getTaxComCodeByItemCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE()));
                        ordDet.setFINVDET_T_SELL_PRICE(unitPrice);
                        ordDet.setFINVDET_TXN_DATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        ordDet.setFINVDET_TXN_TYPE("27");
                        ordDet.setFINVDET_IS_ACTIVE("1");
                        ordDet.setFINVDET_LOCCODE(locCode);
                        ordDet.setFINVDET_IS_SYNCED("0");
                        ordDet.setFINVDET_QOH("0");
                        ordDet.setFINVDET_DISVALAMT("0");
                        ordDet.setFINVDET_BRAND_DISC("0");
                        ordDet.setFINVDET_BRAND_DISCPER("0");
                        ordDet.setFINVDET_COMDISC("0");
                        ordDet.setFINVDET_COM_DISCPER("0");
                        ordDet.setFINVDET_PRICE(unitPrice);
                        ordDet.setFINVDET_DIS_PER("0");

						/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*OrdFreeIssue table update*-*-*-*-*-*-*-*-*-*-*-*-*-*/

                        OrdFreeIssue ordFreeIssue = new OrdFreeIssue();
                        ordFreeIssue.setOrdFreeIssue_ItemCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE());
                        ordFreeIssue.setOrdFreeIssue_Qty(itemFreeIssue.getAlloc());
                        ordFreeIssue.setOrdFreeIssue_RefNo(FIRefNo);
                        ordFreeIssue.setOrdFreeIssue_RefNo1(mainActivity.selectedInvHed.getFINVHED_REFNO());
                        ordFreeIssue.setOrdFreeIssue_TxnDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        new OrdFreeIssueDS(getActivity()).UpdateOrderFreeIssue(ordFreeIssue);

						/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*/

                        ordList.add(ordDet);

                        if (new InvDetDS(getActivity()).createOrUpdateInvDet(ordList) > 0) {
                            android.widget.Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_SHORT).show();
                            showData();
                        }
                    }
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
        return true;
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mToggleTextbox() {

        if (mSharedPref.getGlobalVal("keyCustomer").equals("Y")) {
            ibtProduct.setEnabled(true);
            ibtDiscount.setEnabled(true);

            Bundle mBundle = getArguments();
            if (mBundle != null) {
                tmpinvHed = (InvHed) mBundle.getSerializable("order");
            }

        } else {
            ibtProduct.setEnabled(false);
            ibtDiscount.setEnabled(false);
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
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_DETAILS"));
    }

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void ProductDialogBox() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.product_dialog_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        final ListView lvProducts = (ListView) promptView.findViewById(R.id.lv_product_list);
        final SearchView search = (SearchView) promptView.findViewById(R.id.et_search);

        lvProducts.clearTextFilter();
        //himas
       // lvProducts.setAdapter(new NewProductAdapter(getActivity(), productList));


        //productList = new ProductDS(getActivity()).getAllItems("");
        productList = new ProductDS(getActivity()).getAllItems("","SA");//rashmi -2018-10-26
        lvProducts.setAdapter(new NewProduct_Adapter(getActivity(), productList));

        alertDialogBuilder.setCancelable(false).setNegativeButton("DONE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                selectedItemList = new ProductDS(getActivity()).getSelectedItems("SA");
                updateInvoiceDet(selectedItemList);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //productList = new ProductDS(getActivity()).getAllItems(query);
                productList = new ProductDS(getActivity()).getAllItems(query,"SA");//Rashmi 2018-10-26
               // lvProducts.setAdapter(new NewProductAdapter(getActivity(), productList));
                lvProducts.setAdapter(new NewProduct_Adapter(getActivity(), productList));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                    productList.clear();
                    //productList = new ProductDS(getActivity()).getAllItems(newText);
                    productList = new ProductDS(getActivity()).getAllItems(newText,"SA");//rashmi-2018-10-26
                  //  lvProducts.setAdapter(new NewProductAdapter(getActivity(), productList));
                    //added dhanushika
                    lvProducts.setAdapter(new NewProduct_Adapter(getActivity(), productList));
                                return true;
            }
        });
    }

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void newDeleteOrderDialog(final int position) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Confirm Deletion !");
        alertDialogBuilder.setMessage("Do you want to delete this item ?");
        alertDialogBuilder.setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                new ProductDS(getActivity()).updateProductQty(orderList.get(position).getFINVDET_ITEM_CODE(), "0");
                new InvDetDS(getActivity()).mDeleteProduct(mainActivity.selectedInvHed.getFINVHED_REFNO(), orderList.get(position).getFINVDET_ITEM_CODE());
                android.widget.Toast.makeText(getActivity(), "Deleted successfully!", android.widget.Toast.LENGTH_SHORT).show();
                showData();

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-click on list view row by items*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void popEditDialogBox(final InvDet invDet,ArrayList<FreeItemDetails> itemDetailsArrayList) {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.input_dialog_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Enter Quantity");
        alertDialogBuilder.setView(promptView);

        final EditText txtInputBox = (EditText) promptView.findViewById(R.id.txtInputBox);
        final TextView lblQoh = (TextView) promptView.findViewById(R.id.lblQOH);

        final TextView itemName = (TextView) promptView.findViewById(R.id.tv_free_issue_item_name);
        final TextView freeQty = (TextView) promptView.findViewById(R.id.tv_free_qty);

        if(itemDetailsArrayList==null){
            freeQty.setVisibility(View.GONE);
            itemName.setVisibility(View.GONE);
        }else{
            for(FreeItemDetails itemDetails :itemDetailsArrayList){
            freeQty.setText("Free Quantity : " + itemDetails.getFreeQty());
            itemName.setText("Product : " + new ItemsDS(getActivity()).getItemNameByCode(itemDetails.getFreeIssueSelectedItem()));
            }
        }



        lblQoh.setText(invDet.getFINVDET_QOH());
        txtInputBox.setText(invDet.getFINVDET_QTY());
        txtInputBox.selectAll();

        txtInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (txtInputBox.length() > 0) {

                    int enteredQty = Integer.parseInt(txtInputBox.getText().toString());

                    if (enteredQty > Double.parseDouble(invDet.getFINVDET_QOH())) {
                        Toast.makeText(getActivity(), "Quantity exceeds QOH !", Toast.LENGTH_SHORT).show();
                        txtInputBox.setText("0");
                        txtInputBox.selectAll();
                    } else
                        lblQoh.setText((int) Double.parseDouble(invDet.getFINVDET_QOH()) - enteredQty + "");
                } else {
                    txtInputBox.setText("0");
                    txtInputBox.selectAll();
                }
            }
        });

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                if (Integer.parseInt(txtInputBox.getText().toString()) > 0) {
                    new ProductDS(getActivity()).updateProductQty(invDet.getFINVDET_ITEM_CODE(), txtInputBox.getText().toString());
                    mUpdateInvoice(invDet.getFINVDET_ID(), invDet.getFINVDET_ITEM_CODE(), txtInputBox.getText().toString(), invDet.getFINVDET_PRICE(), invDet.getFINVDET_SEQNO(), invDet.getFINVDET_QOH(),invDet.getFINVDET_CHANGED_PRICE());
                } else
                    Toast.makeText(getActivity(), "Enter Qty above Zero !", Toast.LENGTH_SHORT).show();

                showData();
            }

        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();

    }

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            VanSalesOrderDetails.this.mToggleTextbox();
        }
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void updateInvoiceDet(final ArrayList<Product> list) {


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Updating products...");
                pDialog.setCancelable(false);
                pDialog.show();
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                int i = 0;
                new InvDetDS(getActivity()).mDeleteRecords(mainActivity.selectedInvHed.getFINVHED_REFNO());

                for (Product product : list) {
                    i++;
                    mUpdateInvoice("0", product.getFPRODUCT_ITEMCODE(), product.getFPRODUCT_QTY(), product.getFPRODUCT_PRICE(), i + "", product.getFPRODUCT_QOH(),product.getFPRODUCT_CHANGED_PRICE());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(pDialog.isShowing()){
                    pDialog.dismiss();
                }

                showData();
            }

        }.execute();
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mUpdateInvoice(String id, String itemCode, String Qty, String price, String seqno, String qoh, String changedPrice) {

        ArrayList<InvDet> arrList = new ArrayList<>();
        double amt = 0.0;
        if(changedPrice.equals("0.0") || changedPrice.equals("0")) {
            amt = Double.parseDouble(price) * Double.parseDouble(Qty);
        }else{
            amt = Double.parseDouble(changedPrice) * Double.parseDouble(Qty);
        }
        String TaxedAmt = new TaxDetDS(getActivity()).calculateTax(itemCode, new BigDecimal(amt));
        InvDet invDet = new InvDet();

        invDet.setFINVDET_ID(id + "");
        //2018/10/23 no need deduct taxed for amount - mega heaters
       // invDet.setFINVDET_AMT(String.format("%.2f", amt - Double.parseDouble(TaxedAmt)));
        invDet.setFINVDET_AMT(String.format("%.2f", amt));

        invDet.setFINVDET_BAL_QTY(Qty);
        //2018/10/23 no need deduct taxed for amount - mega heaters
        //invDet.setFINVDET_B_AMT(String.format("%.2f", amt - Double.parseDouble(TaxedAmt)));
        invDet.setFINVDET_B_AMT(String.format("%.2f", amt));
        invDet.setFINVDET_B_SELL_PRICE(price);
        invDet.setFINVDET_BT_SELL_PRICE(price);
        invDet.setFINVDET_DIS_AMT("0");
        invDet.setFINVDET_DIS_PER("0");
        invDet.setFINVDET_ITEM_CODE(itemCode);
        invDet.setFINVDET_PRIL_CODE(new SalRepDS(getActivity()).getCurrentPriLCode().trim());
        invDet.setFINVDET_QTY(Qty);
        invDet.setFINVDET_PICE_QTY(Qty);
        invDet.setFINVDET_TYPE("SA");
        invDet.setFINVDET_BT_TAX_AMT("0");
        invDet.setFINVDET_RECORD_ID("");
        invDet.setFINVDET_SELL_PRICE(String.format("%.2f", (amt - Double.parseDouble(TaxedAmt)) / Double.parseDouble(invDet.getFINVDET_QTY())));
        invDet.setFINVDET_B_SELL_PRICE(String.format("%.2f", (amt - Double.parseDouble(TaxedAmt)) / Double.parseDouble(invDet.getFINVDET_QTY())));
        invDet.setFINVDET_SEQNO(seqno + "");
        invDet.setFINVDET_TAX_AMT(TaxedAmt);
        invDet.setFINVDET_TAX_COM_CODE(new ItemsDS(getActivity()).getTaxComCodeByItemCode(itemCode));
        invDet.setFINVDET_T_SELL_PRICE(String.format("%.2f", amt / Double.parseDouble(invDet.getFINVDET_QTY())));
        invDet.setFINVDET_BT_SELL_PRICE(String.format("%.2f", amt / Double.parseDouble(invDet.getFINVDET_QTY())));
        invDet.setFINVDET_REFNO(mainActivity.selectedInvHed.getFINVHED_REFNO());
        invDet.setFINVDET_COM_DISCPER(new ControlDS(getActivity()).getCompanyDisc() + "");
        invDet.setFINVDET_BRAND_DISCPER("0");
        invDet.setFINVDET_BRAND_DISC("0");
        invDet.setFINVDET_COMDISC("0");
        invDet.setFINVDET_TXN_DATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        invDet.setFINVDET_TXN_TYPE("22");
        invDet.setFINVDET_IS_ACTIVE("1");
        invDet.setFINVDET_QOH(qoh);
        invDet.setFINVDET_DISVALAMT("0");
        invDet.setFINVDET_PRICE(price);
        invDet.setFINVDET_CHANGED_PRICE(changedPrice);

        arrList.add(invDet);
        new InvDetDS(getActivity()).createOrUpdateInvDet(arrList);
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

}