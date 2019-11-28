package com.datamation.megaheaters.view.presale;

import android.app.AlertDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.R;

import com.datamation.megaheaters.adapter.FreeIssueAdapter;

import com.datamation.megaheaters.adapter.NewPreProductAdapter;
import com.datamation.megaheaters.adapter.OrderDetailsAdapter;
import com.datamation.megaheaters.adapter.PreSalesFreeItemAdapter;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.data.ControlDS;
import com.datamation.megaheaters.data.ItemPriDS;
import com.datamation.megaheaters.data.ItemsDS;
import com.datamation.megaheaters.data.OrdFreeIssueDS;
import com.datamation.megaheaters.data.PreProductDS;
import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.data.TaxDetDS;
import com.datamation.megaheaters.data.TranSODetDS;
import com.datamation.megaheaters.model.FreeIssue;
import com.datamation.megaheaters.model.FreeItemDetails;
import com.datamation.megaheaters.model.ItemFreeIssue;
import com.datamation.megaheaters.model.OrdFreeIssue;
import com.datamation.megaheaters.model.PreProduct;
import com.datamation.megaheaters.model.TranSODet;
import com.datamation.megaheaters.model.TranSOHed;
import com.datamation.megaheaters.view.MainActivity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Dhanushika on 1/17/2018.
 */

public class PreSalesOrderNewDetails extends Fragment{
    private static final String TAG = "PreSalesOrderNewDetails";
    public View view;
    public SharedPref mSharedPref;
    private ListView lv_pre_order_detlv, lv_pre_Free;
    private ImageButton pre_Product_btn, pre_Discount_btn;
    private  String RefNo;
    public MainActivity mainActivity;
    int seqno = 0, index_id = 0;
    ArrayList<PreProduct> PreproductList = null, selectedItemList = null;
    ArrayList<TranSODet> orderList;
    SweetAlertDialog pDialog;
    private  MyReceiver r;
    private TranSOHed tmpsoHed=null;  //from re oder creation




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.sales_management_pre_sales_details_new, container, false);
        lv_pre_order_detlv = (ListView) view.findViewById(R.id.lvProducts_Pre);
        lv_pre_Free = (ListView) view.findViewById(R.id.lvFreeIssue_Pre);
        pre_Product_btn = (ImageButton) view.findViewById(R.id.btnPreSalesProduct);
        pre_Discount_btn = (ImageButton) view.findViewById(R.id.btnPreSalesDisc);
        seqno = 0;
        mSharedPref = new SharedPref(getActivity());
        RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.NumVal));
        mainActivity = (MainActivity) getActivity();
        tmpsoHed=new TranSOHed();
        //------------------------------------------------------------------------------------------------------------------------

        showData();
        pre_Product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              new LoardingPreProductFromDB().execute();
            }
        });
        pre_Product_btn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        pre_Product_btn.setBackground(getResources().getDrawable(R.drawable.product_down));
                    }
                    break;

                    case MotionEvent.ACTION_UP: {
                        pre_Product_btn.setBackground(getResources().getDrawable(R.drawable.product));
                    }
                    break;
                }
                return false;
            }
        });
//------------------------------------------------------------------------------------------------------------------------------------
        lv_pre_order_detlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new TranSODetDS(getActivity()).restFreeIssueData(RefNo);
                new OrdFreeIssueDS(getActivity()).ClearFreeIssues(RefNo);
                TranSODet tranSODet=orderList.get(position);

                FreeIssue issue = new FreeIssue(getActivity());
                ArrayList<FreeItemDetails> list=issue.getEligibleFreeItemsBySalesItem(orderList.get(position),new SharedPref(getActivity()).getGlobalVal("KeyCostCode"));
                popEditDialogBox(tranSODet,list);

            }
        });
        //---------------------------------------------------------------------------------------------------------------------------------
        lv_pre_order_detlv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new TranSODetDS(getActivity()).restFreeIssueData(RefNo);
                new OrdFreeIssueDS(getActivity()).ClearFreeIssues(RefNo);
                newDeleteOrderDialog(position);
                return true;
            }
        });

        //----------------------------------------------discountFreeissue-------------------------------------------------------------------------

        pre_Discount_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        pre_Discount_btn.setBackground(getResources().getDrawable(R.drawable.discount_down));
                    }
                    break;

                    case MotionEvent.ACTION_UP: {
                        pre_Discount_btn.setBackground(getResources().getDrawable(R.drawable.discount));
                    }
                    break;
                }
                return false;
            }
        });

        //-----------------------------------------pre Discount calculation-----------------------------------------------------

        pre_Discount_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateFreeIssue();
            }
        });
        return view;
    }


    //-------------------------------------- show pre product dialog----------------------------------------------------------------------

    public void PreProductDialogBox() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.product_dialog_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        final ListView lvProducts = (ListView) promptView.findViewById(R.id.lv_product_list);
        final SearchView search = (SearchView) promptView.findViewById(R.id.et_search);

        lvProducts.clearTextFilter();
        PreproductList = new PreProductDS(getActivity()).getAllItems("");
        lvProducts.setAdapter(new NewPreProductAdapter(getActivity(), PreproductList));





        alertDialogBuilder.setCancelable(false).setNegativeButton("DONE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                selectedItemList = new PreProductDS(getActivity()).getSelectedItems();
                updateSOeDet(selectedItemList);
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
                PreproductList = new PreProductDS(getActivity()).getAllItems(query);
                lvProducts.setAdapter(new NewPreProductAdapter(getActivity(), PreproductList));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                      PreproductList.clear();
                    PreproductList = new PreProductDS(getActivity()).getAllItems(newText);
                    lvProducts.setAdapter(new NewPreProductAdapter(getActivity(), PreproductList));

                return false;
            }
        });

    }
    //------------------------------------------------------------------------------------------------------------------------------------------------
    public void updateSOeDet(final ArrayList<PreProduct> list) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Fetch Data Please Wait.");
                pDialog.setCancelable(false);
                pDialog.show();

                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                int i = 0;
                 new TranSODetDS(getActivity()).restData(RefNo);

                for (PreProduct product : list) {
                    i++;
                    mUpdatePrsSales("0", product.getPREPRODUCT_ITEMCODE(), product.getPREPRODUCT_QTY(), product.getPREPRODUCT_PRICE(), i + "", product.getPREPRODUCT_QOH(),product.getPREPRODUCT_CHANGED_PRICE(),product.getPREPRODUCT_DISCOUNT());
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
    //---------------------------------------------Calculate Free isuee-------------------------------------------------------------------------------

    public void calculateFreeIssue() {
        	/* GET CURRENT ORDER DETAILS FROM TABLE */
        ArrayList<TranSODet> dets = new TranSODetDS(getActivity()).getSAForFreeIssueCalc(RefNo);
            /* CLEAR ORDERDET TABLE RECORD IF FREE ITEMS ARE ALREADY ADDED. */
        new TranSODetDS(getActivity()).restFreeIssueData(RefNo);
            /* Clear free issues in OrdFreeIss */
        new OrdFreeIssueDS(getActivity()).ClearFreeIssues(RefNo);


        FreeIssue issue = new FreeIssue(getActivity());

			/* Get discounts for assorted items */
        ArrayList<ArrayList<TranSODet>> metaOrdList = issue.SortDiscount(dets);

			/* Iterate through for discounts for items */
        for (ArrayList<TranSODet> OrderList : metaOrdList) {

            double totAmt = 0;
            double freeVal = Double.parseDouble(OrderList.get(0).getFTRANSODET_BAMT());
            String discPer = OrderList.get(0).getFTRANSODET_SCHDISPER();
            String discType = OrderList.get(0).getFTRANSODET_DISCTYPE();
            String discRef = OrderList.get(0).getFTRANSODET_DISC_REF();
            OrderList.get(0).setFTRANSODET_BAMT("0");

            for (TranSODet det : OrderList)
                totAmt += Double.parseDouble(det.getFTRANSODET_PRICE()) * (Double.parseDouble(det.getFTRANSODET_QTY()));

            for (TranSODet det : OrderList) {
                det.setFTRANSODET_SCHDISPER(discPer);
                det.setFTRANSODET_DISCTYPE(discType);
                det.setFTRANSODET_DISC_REF(discRef);

                double disc;

					/*
					 * For value, calculate amount portion & for percentage ,
					 * calculate percentage portion
					 */
                disc = (freeVal / totAmt) * Double.parseDouble(det.getFTRANSODET_PRICE()) * (Double.parseDouble(det.getFTRANSODET_QTY()));

					/* Calculate discount amount from disc percentage portion */
                if (discType.equals("P"))
                    disc = (Double.parseDouble(det.getFTRANSODET_AMT()) / 100) * disc;

                new TranSODetDS(getActivity()).updateDiscount(det, disc, det.getFTRANSODET_DISCTYPE());
            }
        }

        // GET ARRAY OF FREE ITEMS BY PASSING IN ORDER DETAILS
        ArrayList<FreeItemDetails> list = issue.getFreeItemsBySalesItem(dets, new SharedPref(getActivity()).getGlobalVal("preKeyCostCode"));
        // PASS EACH ITEM IN TO DIALOG BOX FOR USER SELECTION
        for (FreeItemDetails freeItemDetails : list) {
            freeIssueDialogBox(freeItemDetails);
        }
    }
    //------------------------------------------------------------show ---------------Free issue Dailog box--------------------------------------------------------------------------

    private boolean freeIssueDialogBox(final FreeItemDetails itemDetails) {

        final ArrayList<ItemFreeIssue> itemFreeIssues;
        final String FIRefNo = itemDetails.getRefno();
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.free_issues_items_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Free Issue Schemes");
        alertDialogBuilder.setView(promptView);

        final ListView listView = (ListView) promptView.findViewById(R.id.lv_free_issue);
        final TextView itemName = (TextView) promptView.findViewById(R.id.tv_free_issue_item_name);
        final TextView freeQty = (TextView) promptView.findViewById(R.id.tv_free_qty);

        freeQty.setText("Free Quantity : " + itemDetails.getFreeQty());
        itemName.setText("Product : " + new ItemsDS(getActivity()).getItemNameByCode(itemDetails.getFreeIssueSelectedItem()));

        final ItemsDS itemsDS = new ItemsDS(getActivity());
        itemFreeIssues = itemsDS.getAllFreeItemNameByRefno(itemDetails.getRefno());
        listView.setAdapter(new FreeIssueAdapter(getActivity(), itemFreeIssues));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
                                    Toast.makeText(getActivity(), "You don't have enough sufficient quantity to order", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "You don't have enough sufficient quantity to order", Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                for (ItemFreeIssue itemFreeIssue : itemFreeIssues) {

                    if (Integer.parseInt(itemFreeIssue.getAlloc()) > 0) {

                        seqno++;
                        TranSODet ordDet = new TranSODet();
                        TranSODetDS detDS = new TranSODetDS(getActivity());
                        ArrayList<TranSODet> ordList = new ArrayList<TranSODet>();

                        ordDet.setFTRANSODET_ID("0");
                        ordDet.setFTRANSODET_AMT("0");
                        ordDet.setFTRANSODET_BALQTY(itemFreeIssue.getAlloc());
                        ordDet.setFTRANSODET_BAMT("0");
                        ordDet.setFTRANSODET_BDISAMT("0");
                        ordDet.setFTRANSODET_BPDISAMT("0");
                        String unitPrice = new ItemPriDS(getActivity()).getProductPriceByCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE(), mainActivity.selectedDebtor.getFDEBTOR_PRILLCODE());
                        ordDet.setFTRANSODET_BSELLPRICE("0");
                        ordDet.setFTRANSODET_BTSELLPRICE(unitPrice);
                        ordDet.setFTRANSODET_DISAMT("0");
                        ordDet.setFTRANSODET_SCHDISPER("0");
                        ordDet.setFTRANSODET_FREEQTY("0");
                        ordDet.setFTRANSODET_ITEMCODE(itemFreeIssue.getItems().getFITEM_ITEM_CODE());
                        ordDet.setFTRANSODET_PDISAMT("0");
                        ordDet.setFTRANSODET_PRILCODE(mainActivity.selectedDebtor.getFDEBTOR_PRILLCODE());
                        ordDet.setFTRANSODET_QTY(itemFreeIssue.getAlloc());
                        ordDet.setFTRANSODET_PICE_QTY(itemFreeIssue.getAlloc());
                        ordDet.setFTRANSODET_TYPE("FI");
                        ordDet.setFTRANSODET_RECORDID("");
                        ordDet.setFTRANSODET_REFNO(RefNo);
                        ordDet.setFTRANSODET_SELLPRICE("0");
                        ordDet.setFTRANSODET_SEQNO(seqno + "");
                        ordDet.setFTRANSODET_TAXAMT("0");
                        ordDet.setFTRANSODET_TAXCOMCODE(new ItemsDS(getActivity()).getTaxComCodeByItemCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE()));
                        ordDet.setFTRANSODET_TIMESTAMP_COLUMN("");
                        ordDet.setFTRANSODET_TSELLPRICE(unitPrice);
                        ordDet.setFTRANSODET_TXNDATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        ordDet.setFTRANSODET_TXNTYPE("27");
                        ordDet.setFTRANSODET_IS_ACTIVE("1");
                        ordDet.setFTRANSODET_LOCCODE(new SalRepDS(getActivity()).getCurrentLocCode());
                        ordDet.setFTRANSODET_COSTPRICE("0");
                        ordDet.setFTRANSODET_BTAXAMT("0");
                        ordDet.setFTRANSODET_IS_SYNCED("0");
                        ordDet.setFTRANSODET_QOH("0");
                        ordDet.setFTRANSODET_SCHDISC("0");
                        ordDet.setFTRANSODET_BRAND_DISC("0");
                        ordDet.setFTRANSODET_BRAND_DISPER("0");
                        ordDet.setFTRANSODET_COMP_DISC("0");
                        ordDet.setFTRANSODET_COMP_DISPER("0");
                        ordDet.setFTRANSODET_DISCTYPE("");
                        ordDet.setFTRANSODET_PRICE(unitPrice);

						/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*OrdFreeIssue table update*-*-*-*-*-*-*-*-*-*-*-*-*-*/

                        OrdFreeIssue ordFreeIssue = new OrdFreeIssue();
                        ordFreeIssue.setOrdFreeIssue_ItemCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE());
                        ordFreeIssue.setOrdFreeIssue_Qty(itemFreeIssue.getAlloc());
                        ordFreeIssue.setOrdFreeIssue_RefNo(FIRefNo);
                        ordFreeIssue.setOrdFreeIssue_RefNo1(RefNo);
                        ordFreeIssue.setOrdFreeIssue_TxnDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        new OrdFreeIssueDS(getActivity()).UpdateOrderFreeIssue(ordFreeIssue);

						/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*/

                        ordList.add(ordDet);

                        if (detDS.createOrUpdateSODet(ordList) > 0) {
                            Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_SHORT).show();
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

    //-----------------------------------------------------------------------------------------------------------------------------------------

    public void showData() {
        try {
            lv_pre_order_detlv.setAdapter(null);
            orderList = new TranSODetDS(getActivity()).getAllOrderDetails(RefNo);
            ArrayList<TranSODet> freeList=new TranSODetDS(getActivity()).getAllFreeIssue(RefNo);

            lv_pre_order_detlv.setAdapter(new OrderDetailsAdapter(getActivity(), orderList));

            lv_pre_Free.setAdapter(new PreSalesFreeItemAdapter(getActivity(), freeList));
        } catch (NullPointerException e) {
          e.printStackTrace();
        }
    }
//------------------------------------------------------update TranSODet tbl------------------------------------------------------------------------------------

    public void mUpdatePrsSales(String id, String itemCode, String Qty, String price, String seqno, String qoh, String changedPrice, String discount) {
        TranSODet SODet = new TranSODet();
        ArrayList<TranSODet> SOList = new ArrayList<TranSODet>();
        double amt = 0.0,bamt = 0.0,amtWithTax = 0.0,disAmt = 0.0, amtWithoutTax = 0.0, TsellPrice = 0.0,BsellPrice = 0.0, sellPrice = 0.0 ,disPrice = 0.0, pDis = 0.0;

        if(changedPrice.equals("0.0")) {
            //2018-09-19 rashmi
            //TsellPrice = Double.parseDouble(price);

            sellPrice = Double.parseDouble(price);
            amtWithoutTax = Double.parseDouble(price) * Double.parseDouble(Qty);
        }else{
            //2018-09-19 rashmi

//            TsellPrice = Double.parseDouble(changedPrice);
            sellPrice = Double.parseDouble(changedPrice);
            amtWithoutTax = Double.parseDouble(changedPrice) * Double.parseDouble(Qty);
        }
        BsellPrice = Double.parseDouble(price);

        //String TaxedAmt = new TaxDetDS(getActivity()).calculateTax(itemCode, new BigDecimal(amtWithTax));
        // rashmi - 2018-08-22
        // String TaxedPrice = new TaxDetDS(getActivity()).calculateTax(itemCode, new BigDecimal(TsellPrice));
        // rashmi - 2018-09-19
        // String TaxedPrice = new TaxDetDS(getActivity()).calculateTax(itemCode, new BigDecimal(sellPrice));
        // TsellPrice = sellPrice + Double.parseDouble(TaxedPrice);
        // sellPrice = TsellPrice - Double.parseDouble(TaxedPrice); comment 2018-09-19
        // rashmi - 2018-09-19 because tax amt calculate for sellprice*qty - disAmt
        // String TaxedAmt = (String.format("%.2f",Double.parseDouble(TaxedPrice) * Double.parseDouble(Qty)));

        disPrice = (sellPrice* Double.parseDouble(discount))/100;
        disAmt = disPrice * Double.parseDouble(Qty);
        amt = amtWithoutTax - disAmt;
        String sArray[] = new TaxDetDS(getActivity()).calculateTaxForward(itemCode, amt);
//      String sArray[] = new TaxDetDS(getActivity()).calculatePriceTaxForward(itemCode, priceWithoutDis);
//      amtWithoutTax = amtWithTax - Double.parseDouble(TaxedAmt);

//      priceWithoutDis = sellPrice - disPrice;
//      bamt = amtWithoutTax - disAmt;


//      2018-08-22
//      No need sArray[0] initialize as Tsellprice-  because ERP get TsellPrice as changed Price..So it include only for calculate amount
//      TsellPrice = Double.parseDouble(sArray[0]);
//      2018-09-19
//      amt = Double.parseDouble(sArray[0]) * Double.parseDouble(Qty);
//      TaxedAmt = (String.format("%.2f",Double.parseDouble(sArray[1]) * Double.parseDouble(Qty)));
        amt  = Double.parseDouble(sArray[0]);
        bamt = Double.parseDouble(sArray[0]);
        amtWithTax = Double.parseDouble(sArray[0]);
        String TaxedAmt = String.format("%.2f",Double.parseDouble(sArray[1]));
        TsellPrice = (amtWithTax * 100)/(amtWithoutTax - disAmt);
//        if(tmpsoHed.getFTRANSOHED_TAXREG().equals("N")){
            pDis = TsellPrice * (Double.parseDouble(discount)/100) * Double.parseDouble(Qty);
//        }else{
//            pDis = sellPrice * (Double.parseDouble(discount)/100) * Double.parseDouble(Qty);
//        }
        SODet.setFTRANSODET_ID(id + "");
        SODet.setFTRANSODET_AMT(String.format("%.2f", amt));
        SODet.setFTRANSODET_BALQTY(Qty);
        SODet.setFTRANSODET_QTY(Qty);
//      SODet.setFTRANSODET_BAMT(String.format("%.2f", amt - Double.parseDouble(TaxedAmt)));
        SODet.setFTRANSODET_BAMT(String.format("%.2f", bamt));

        SODet.setFTRANSODET_BDISAMT("0");
        SODet.setFTRANSODET_BPDISAMT(String.format("%.2f",disAmt));
        SODet.setFTRANSODET_BTAXAMT("0");
        SODet.setFTRANSODET_TAXAMT(TaxedAmt);
        SODet.setFTRANSODET_DISAMT(String.format("%.2f",disAmt));
        SODet.setFTRANSODET_SCHDISPER("0");
        SODet.setFTRANSODET_COMP_DISPER(new ControlDS(getActivity()).getCompanyDisc() + "");
        SODet.setFTRANSODET_BRAND_DISPER("0");
        SODet.setFTRANSODET_BRAND_DISC("0");
        SODet.setFTRANSODET_COMP_DISC("0");
        SODet.setFTRANSODET_COSTPRICE(new ItemsDS(getActivity()).getCostPriceItemCode(itemCode));
        SODet.setFTRANSODET_ITEMCODE(itemCode);
        if(!mainActivity.selectedDebtor.getFDEBTOR_PRILLCODE().equals(""))
        SODet.setFTRANSODET_PRILCODE(mainActivity.selectedDebtor.getFDEBTOR_PRILLCODE());
        else
        SODet.setFTRANSODET_PRILCODE(new ItemsDS(getActivity()).getPrillCodebyItemCode(itemCode));
        SODet.setFTRANSODET_PICE_QTY(Qty);
        SODet.setFTRANSODET_REFNO(RefNo);
//        SODet.setFTRANSODET_SELLPRICE(String.format("%.2f", (amt - Double.parseDouble(TaxedAmt)) / Double.parseDouble(SODet.getFTRANSODET_QTY())));
//        SODet.setFTRANSODET_BSELLPRICE(String.format("%.2f", (amt - Double.parseDouble(TaxedAmt)) / Double.parseDouble(SODet.getFTRANSODET_QTY())));
//rashmi 2018-08-10 for indra
//        SODet.setFTRANSODET_SELLPRICE(String.format("%.2f", amt / Double.parseDouble(SODet.getFTRANSODET_QTY())));
//        SODet.setFTRANSODET_BSELLPRICE(String.format("%.2f", amt / Double.parseDouble(SODet.getFTRANSODET_QTY())));
//rashmi 2018-08-13 for indra as erp
//        SODet.setFTRANSODET_SELLPRICE(String.format("%.2f", amtWithoutTax));
//        SODet.setFTRANSODET_BSELLPRICE(String.format("%.2f", amtWithoutTax));
        SODet.setFTRANSODET_SELLPRICE(String.format("%.2f", sellPrice));
//      2018-09-19
//      SODet.setFTRANSODET_BSELLPRICE(String.format("%.2f", sellPrice));
        SODet.setFTRANSODET_BSELLPRICE(String.format("%.2f", BsellPrice));

        SODet.setFTRANSODET_SEQNO(new TranSODetDS(getActivity()).getLastSequnenceNo(RefNo));
        SODet.setFTRANSODET_TAXCOMCODE(new ItemsDS(getActivity()).getTaxComCodeByItemCode(itemCode));
//      SODet.setFTRANSODET_BTSELLPRICE(String.format("%.2f", (amt - Double.parseDouble(TaxedAmt)) / Double.parseDouble(SODet.getFTRANSODET_QTY())));
//      SODet.setFTRANSODET_TSELLPRICE(String.format("%.2f", (amt - Double.parseDouble(TaxedAmt)) / Double.parseDouble(SODet.getFTRANSODET_QTY())));
//      SODet.setFTRANSODET_BTSELLPRICE(String.format("%.2f", amt / Double.parseDouble(SODet.getFTRANSODET_QTY())));
        SODet.setFTRANSODET_BTSELLPRICE(String.format("%.2f", TsellPrice));
//      SODet.setFTRANSODET_BTSELLPRICE(String.format("%.2f", amtWithTax / Double.parseDouble(SODet.getFTRANSODET_QTY())));
//      SODet.setFTRANSODET_TSELLPRICE(String.format("%.2f", amt / Double.parseDouble(SODet.getFTRANSODET_QTY())));
        SODet.setFTRANSODET_TSELLPRICE(String.format("%.2f", TsellPrice));
//      SODet.setFTRANSODET_TSELLPRICE(String.format("%.2f", amtWithTax / Double.parseDouble(SODet.getFTRANSODET_QTY())));
        SODet.setFTRANSODET_TXNTYPE("21");
        SODet.setFTRANSODET_LOCCODE(new SalRepDS(getActivity()).getCurrentLocCode());
        SODet.setFTRANSODET_TXNDATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        SODet.setFTRANSODET_IS_ACTIVE("1");
        SODet.setFTRANSODET_RECORDID("");
        SODet.setFTRANSODET_PDISAMT(String.format("%.2f",pDis));
        SODet.setFTRANSODET_IS_SYNCED("0");
        SODet.setFTRANSODET_QOH(qoh);
        SODet.setFTRANSODET_TYPE("SA");
        SODet.setFTRANSODET_SCHDISC("0");
        SODet.setFTRANSODET_PRICE(price);
        SODet.setFTRANSODET_DISCTYPE("");
        SODet.setFTRANSODET_SEQNO(seqno + "");
        SODet.setFTRANSODET_CHANGED_PRICE(changedPrice);
        SODet.setFTRANSODET_DISPER(discount);

        SOList.add(SODet);
        new TranSODetDS(getActivity()).createOrUpdateSODet(SOList);

    }

    //----------------------------------------show popup dialog------------------------------------------------------------------------

    public void popEditDialogBox(final TranSODet tranSODet,ArrayList<FreeItemDetails >itemDetailsArrayList) {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.input_dialog_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Enter Quantity");
        alertDialogBuilder.setView(promptView);

        final EditText txtInputBox = (EditText) promptView.findViewById(R.id.txtInputBox);
        final TextView lblQoh = (TextView) promptView.findViewById(R.id.lblQOH);

        final TextView itemName = (TextView) promptView.findViewById(R.id.tv_free_issue_item_name);
        final TextView freeQty = (TextView) promptView.findViewById(R.id.tv_free_qty);

        lblQoh.setText(tranSODet.getFTRANSODET_QOH());
        txtInputBox.setText(tranSODet.getFTRANSODET_QTY());
        txtInputBox.selectAll();

        if(itemDetailsArrayList==null){
            freeQty.setVisibility(View.GONE);
            itemName.setVisibility(View.GONE);
        }else{
            for(FreeItemDetails itemDetails :itemDetailsArrayList){
                freeQty.setText("Free Quantity : " + itemDetails.getFreeQty());
                itemName.setText("Product : " + new ItemsDS(getActivity()).getItemNameByCode(itemDetails.getFreeIssueSelectedItem()));
            }
        }


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
                    lblQoh.setText((int) Double.parseDouble(tranSODet.getFTRANSODET_QOH()) - enteredQty + "");
                 /*   int enteredQty = Integer.parseInt(txtInputBox.getText().toString());

                    if (enteredQty > Double.parseDouble(tranSODet.getFTRANSODET_QOH())) {
                        Toast.makeText(getActivity(), "Quantity exceeds QOH !", Toast.LENGTH_SHORT).show();
                        txtInputBox.setText("0");
                        txtInputBox.selectAll();
                    } else
                        ;*/
                } else {
                    txtInputBox.setText("0");
                    txtInputBox.selectAll();
                }
            }
        });

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                if (Integer.parseInt(txtInputBox.getText().toString()) > 0) {
                    new PreProductDS(getActivity()).updateProductQty(tranSODet.getFTRANSODET_ITEMCODE(), txtInputBox.getText().toString());
                    mUpdatePrsSales(tranSODet.getFTRANSODET_ID(), tranSODet.getFTRANSODET_ITEMCODE(), txtInputBox.getText().toString(), tranSODet.getFTRANSODET_PRICE(), tranSODet.getFTRANSODET_SEQNO(), tranSODet.getFTRANSODET_QOH(),tranSODet.getFTRANSODET_CHANGED_PRICE(),tranSODet.getFTRANSODET_DISAMT());
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

    //-------------------------------------------------------------------------------------------------------------------------
    public void newDeleteOrderDialog(final int position) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Confirm Deletion !");
        alertDialogBuilder.setMessage("Do you want to delete this item ?");
        alertDialogBuilder.setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                new PreProductDS(getActivity()).updateProductQty(orderList.get(position).getFTRANSODET_ITEMCODE(), "0");

                int count = new TranSODetDS(getActivity()).deleteOrdDetItemCode(orderList.get(position).getFTRANSODET_ITEMCODE(), orderList.get(position).getFTRANSODET_TYPE());
                if (count > 0) {

                  //  new TranSODetDS(getActivity()).restFreeIssueData(RefNo);
                    //#
                    /*new OrdFreeIssueDS(getActivity()).ClearFreeIssues(RefNo);
                    new OrderDiscDS(getActivity()).RemoveOrderDiscRecord(RefNo, itemCode);*/

                    Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_LONG).show();

                    showData();
                }
                android.widget.Toast.makeText(getActivity(), "Deleted successfully!", android.widget.Toast.LENGTH_SHORT).show();


            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();


    }


//---------------------------------LoardingPreProductFromDB----------------------------------------------------------------------------------------
    public class LoardingPreProductFromDB extends AsyncTask<Object, Object, ArrayList<PreProduct>> {
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
        protected ArrayList<PreProduct> doInBackground(Object... objects) {

            if (new PreProductDS(getActivity()).tableHasRecords()) {
                    PreproductList = new PreProductDS(getActivity()).getAllItems("");
            } else {
                    PreproductList =new ItemsDS(getActivity()).getAllItemForPreSales("","txntype ='21'",RefNo,mainActivity.selectedDebtor.getFDEBTOR_PRILLCODE());

           //     PreproductList =new ItemsDS(getActivity()).getAllItemForPreSales("","txntype ='21'",RefNo, new SalRepDS(getActivity()).getCurrentLocCode(),mainActivity.selectedDebtor.getFDEBTOR_PRILLCODE());
                new PreProductDS(getActivity()).insertOrUpdateProducts(PreproductList);
                //---------re Order Temp product  list added for  fProducts_pre table-----------------dhanushika-------------------------------
                if(tmpsoHed!=null) {
                    ArrayList<TranSODet> detArrayList = tmpsoHed.getSoDetArrayList();
                    if (detArrayList != null) {
                        for (int i = 0; i < detArrayList.size(); i++) {
                            String tmpItemName = detArrayList.get(i).getFTRANSODET_ITEMCODE();
                            String tmpQty = detArrayList.get(i).getFTRANSODET_QTY();
                            //Update Qty in  fProducts_pre table
                            int count = new PreProductDS(getActivity()).updateProductQtyFor(tmpItemName, tmpQty);
                            if (count > 0) {
                               // Log.d("InsertOrUpdate", "success");
                            } else {
                               // Log.d("InsertOrUpdate", "Failed");
                            }

                        }
                    }
                }
                //----------------------------------------------------------------------------

            }
            return PreproductList;
        }


        @Override
        protected void onPostExecute(ArrayList<PreProduct> preProducts) {
            super.onPostExecute(preProducts);

            if(pDialog.isShowing()){
                pDialog.dismiss();
            }
            PreProductDialogBox();

        }
    }
//------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_DETAILS"));
    }
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }



    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            PreSalesOrderNewDetails.this.mToggleTextbox();
        }
    }

    public void mToggleTextbox() {

        if (mSharedPref.getGlobalVal("PrekeyCustomer").equals("Y")) {

            pre_Discount_btn.setEnabled(true);
            pre_Product_btn.setEnabled(true);

            // from PreSalesAdapter----- for re oder creation
            Bundle mBundle = getArguments();
            if (mBundle != null) {
                tmpsoHed = (TranSOHed) mBundle.getSerializable("order");
            }
            showData();

            selectedItemList = new PreProductDS(getActivity()).getSelectedItems();
            if(selectedItemList !=null &&  !selectedItemList.isEmpty()){
                updateSOeDet(selectedItemList);
            }



        } else {
            Toast.makeText(getActivity(), "Select a customer to continue...", Toast.LENGTH_SHORT).show();
            pre_Discount_btn.setEnabled(false);
            pre_Product_btn.setEnabled(false);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------------------
}
