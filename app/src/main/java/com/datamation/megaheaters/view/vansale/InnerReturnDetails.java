package com.datamation.megaheaters.view.vansale;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.ProductAdapter;
import com.datamation.megaheaters.adapter.ReturnDetailsAdapter;
import com.datamation.megaheaters.adapter.ReturnReasonAdapter;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.data.DebItemPriDS;
import com.datamation.megaheaters.data.FInvRDetDS;
import com.datamation.megaheaters.data.FInvRHedDS;
import com.datamation.megaheaters.data.InvHedDS;
import com.datamation.megaheaters.data.ItemPriDS;
import com.datamation.megaheaters.data.ItemsDS;
import com.datamation.megaheaters.data.ProductDS;
import com.datamation.megaheaters.data.ReasonDS;
import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.data.SharedPreferencesClass;
import com.datamation.megaheaters.data.TaxDetDS;
import com.datamation.megaheaters.listviewitems.CustomKeypadDialogPrice;
import com.datamation.megaheaters.model.FInvRDet;
import com.datamation.megaheaters.model.FInvRHed;
import com.datamation.megaheaters.model.InvDet;
import com.datamation.megaheaters.model.InvHed;
import com.datamation.megaheaters.model.Items;
import com.datamation.megaheaters.model.Product;
import com.datamation.megaheaters.model.Reason;
import com.datamation.megaheaters.view.MainActivity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class InnerReturnDetails extends Fragment implements OnClickListener {

    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    public String RefNo = "";
    View view;
    Button itemSearch, bAdd, bFreeIssue, reasonSearch;
    EditText lblItemName, txtQty, editTotDisc, lblReason,lblNou;
   TextView lblPrice;
    int totPieces = 0;
    double amount = 0.00,price = 0.00,minPrice = 0.00, maxPrice = 0.00, changedPrice = 0.0;
    double values = 0.00, iQoh;
   //TextView lblNou, lblPrice;
    Items selectedItem = null;
    Reason selectedReason = null;
    int seqno = 0, index_id = 0;
    ListView lv_return_det;
    ArrayList<FInvRDet> returnList;
    SharedPref mSharedPref;
    boolean hasChanged = false;
    String locCode;
    double brandDisPer;
    Spinner returnType;
    ArrayList<Items> list = null;
    ArrayList<Items> itemList = null;
    ArrayList<Reason> reasonList = null;
    MainActivity activity;
    MyReceiver r;
    private  SweetAlertDialog pDialog;
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }

    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_RETURN"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sales_management_return_details, container, false);
        mSharedPref = new SharedPref(getActivity());
        seqno = 0;
        totPieces = 0;
        localSP = getActivity().getSharedPreferences(SETTINGS, 0);
        itemSearch = (Button) view.findViewById(R.id.btn_item_search);
        reasonSearch = (Button) view.findViewById(R.id.btn_reason_search);
        bAdd = (Button) view.findViewById(R.id.btn_add);
        bFreeIssue = (Button) view.findViewById(R.id.btn_free);
        lblItemName = (EditText) view.findViewById(R.id.et_item);
        lblReason = (EditText) view.findViewById(R.id.et_reason);
        activity = (MainActivity) getActivity();
        lv_return_det = (ListView) view.findViewById(R.id.lv_return_det);

//        if(activity.selectedDebtor.getFDEBTOR_TAX_REG().equals("Y")){
//            RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanNumValTax));
//        }else{
//            RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanNumValNonTax));
//        }

        RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanReturnNumVal));


        editTotDisc = (EditText) view.findViewById(R.id.et_TotalDisc);
        lblNou = (EditText) view.findViewById(R.id.et_pieces);
        lblPrice = (TextView) view.findViewById(R.id.tv_price);
        txtQty = (EditText) view.findViewById(R.id.tv_unit);
        returnType = (Spinner) view.findViewById(R.id.spinner_return_Type);
        itemSearch.setOnClickListener(this);
        reasonSearch.setOnClickListener(this);
        bAdd.setOnClickListener(this);
        bFreeIssue.setOnClickListener(this);

//            ArrayList<FInvRHed> getReturnHed = new FInvRHedDS(getActivity()).getAllActiveInvrhed();
//
//            if (!getReturnHed.isEmpty()) {
//
//                for (FInvRHed returnHed : getReturnHed) {
//                    activity.selectedReturnHed = returnHed;
//
//                    if (activity.selectedRetDebtor == null) {
//                        DebtorDS debtorDS = new DebtorDS(getActivity());
//                        activity.selectedRetDebtor = debtorDS.getSelectedCustomerByCode(returnHed.getFINVRHED_DEBCODE());
//                    }
//                }
//            }

        ArrayList<String> strList = new ArrayList<String>();
        strList.add("Select Return type to continue...");
        strList.add("MR-MARKET RETURN");
        strList.add("UR-USABLE RETURN");
        strList.add("RP-REPLACEMENT");

        final ArrayAdapter<String> returnTypeAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.return_spinner_item, strList);
        returnTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        returnType.setAdapter(returnTypeAdapter);
        FetchData();

			/*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
//        lblPrice.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if ((lblPrice.length() > 0)) {
//                   if(!(minPrice<= Double.parseDouble(lblPrice.getText().toString())) || !(maxPrice>= Double.parseDouble(lblPrice.getText().toString()))){
//                       lblPrice.setText(String.format("%.2f",price));
//                       Toast.makeText(getActivity(),"Cannot change price",Toast.LENGTH_LONG).show();
//                   }
//                }
//            }
//        });
//        lblPrice.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CustomKeypadDialogPrice keypadPrice = new CustomKeypadDialogPrice(getActivity(), true, new CustomKeypadDialogPrice.IOnOkClickListener() {
//                    @Override
//                    public void okClicked(double value) {
//                        //price cannot be changed less than gross profit
//                        if(minPrice <=value && value <= maxPrice) {
//                            //  save changed price
//                           // new ProductDS(context).updateProductPrice(product.getFPRODUCT_ITEMCODE(), String.valueOf(value));
//                            //  value should be set for another variable in preProduct
//                            //  preProduct.setPREPRODUCT_PRICE(String.valueOf(value));
//                            changedPrice = value;
//                            lblPrice.setText(""+changedPrice);
//                        }else{
//                            changedPrice = price;
//                            Toast.makeText(getActivity(),"Price cannot be change..",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//                keypadPrice.show();
//
//                keypadPrice.setHeader("CHANGE PRICE");
////                if(preProduct.getPREPRODUCT_CHANGED_PRICE().equals("0")){
//                keypadPrice.loadValue(changedPrice);
//            }
//        });
			/*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        txtQty.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if(! txtQty.getText().equals(null))
                {
                    if ((txtQty.length() > 0))
                    {
                        totPieces = Integer.parseInt(txtQty.getText().toString());
                        hasChanged = true;
                        amount = Double.parseDouble(txtQty.getText().toString())
                                * Double.parseDouble(lblPrice.getText().toString());
                    }
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });

            /*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        txtQty.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                txtQty.setText("");
            }
        });

            /*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        lv_return_det.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                FInvRDet returnDet = returnList.get(position);
                deleteOrderDialog(getActivity(), "Return Details ", returnDet.getFINVRDET_ITEMCODE(),RefNo);
                return true;
            }
        });

            /*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        lv_return_det.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                FInvRDet returnDet = returnList.get(position);
                FetchData();
                bAdd.setText("EDIT");
                selectedItem = new Items();
                selectedItem.setFITEM_ITEM_CODE(returnDet.getFINVRDET_ITEMCODE());
                index_id = Integer.parseInt(returnDet.getFINVRDET_ID());
                lblItemName.setText(new ItemsDS(getActivity()).getItemNameByCode(returnDet.getFINVRDET_ITEMCODE()));
                txtQty.setText(returnDet.getFINVRDET_QTY());
                lblPrice.setText(new ItemPriDS(getActivity()).getProductPriceByCode(selectedItem.getFITEM_ITEM_CODE(), activity.selectedRetDebtor.getFDEBTOR_PRILLCODE()));
                hasChanged = false;
                editTotDisc.setText(returnDet.getFINVRDET_DIS_AMT());
                lblReason.setText(returnDet.getFINVRDET_RETURN_REASON());
            }
        });
        return view;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-grab data from table-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_item_search:
                clearTextFields();
                //prodcutDetailsDialogbox();
                if(activity.selectedDebtor != null)
                {
                    new LoardingItemsFromDB().execute();
                }
                else
                {
                    Toast.makeText(getActivity(), "Select a customer to continue...", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_reason_search:
                reasonsDialogbox();
                break;
            case R.id.btn_add: {
                if (!(lblItemName.getText().toString().equals("")) && !((lblReason.getText().toString().equals(""))) && !(returnType.getSelectedItem().toString().contains("Select Return"))) {
//                    Log.v("ITEM NAME>>>>",lblItemName.getText().toString());
//                    Log.v("ITEM REASON>>>>",lblReason.getText().toString());
                    if(TextUtils.isEmpty(txtQty.getText()))
                    {
                        txtQty.setText("0");
                    }
                    if (Integer.parseInt(txtQty.getText().toString()) > 0) {
//                        Log.v("TOTAL PEIACES>>>>",totPieces+"");
                        FInvRDet ReturnDet = new FInvRDet();
                        ArrayList<FInvRDet> ReturnList = new ArrayList<FInvRDet>();

                        ArrayList<FInvRHed> returnHedList = new ArrayList<FInvRHed>();

                        String TaxedAmt = new TaxDetDS(getActivity()).calculateTax(selectedItem.getFITEM_ITEM_CODE(),
                                new BigDecimal(amount - Double.parseDouble(editTotDisc.getText().toString())));
                        FInvRHed hed = new FInvRHed();

                        hed.setFINVRHED_REFNO(RefNo);
                        hed.setFINVRHED_MANUREF(activity.selectedInvHed.getFINVHED_MANUREF());
                        hed.setFINVRHED_INV_REFNO(activity.selectedInvHed.getFINVHED_REFNO());
                        hed.setFINVRHED_REMARKS(activity.selectedInvHed.getFINVHED_REMARKS());
                        hed.setFINVRHED_ADD_USER(new SalRepDS(getActivity()).getCurrentRepCode());
                        hed.setFINVRHED_ADD_DATE(currentTime());
                        hed.setFINVRHED_ADD_MACH(localSP.getString("MAC_Address", "No MAC Address").toString());
                        hed.setFINVRHED_TXNTYPE("24");
                        hed.setFINVRHED_TXN_DATE(activity.selectedInvHed.getFINVHED_TXNDATE());
                        hed.setFINVRHED_IS_ACTIVE("1");
                        hed.setFINVRHED_IS_SYNCED("0");

                        if (activity.selectedDebtor != null) {
                            hed.setFINVRHED_DEBCODE(activity.selectedDebtor.getFDEBTOR_CODE());
                            hed.setFINVRHED_TAX_REG(activity.selectedDebtor.getFDEBTOR_TAX_REG());
                        }

                        hed.setFINVRHED_LOCCODE(new SalRepDS(getActivity()).getCurrentLocCode());
                        hed.setFINVRHED_ROUTE_CODE(new SharedPref(getActivity()).getGlobalVal("KeyRouteCode"));
                        hed.setFINVRHED_COSTCODE("");

                        activity.selectedReturnHed = hed;
                        SharedPreferencesClass.setLocalSharedPreference(activity, "Return_Start_Time", currentTime());

                        returnHedList.add(activity.selectedReturnHed);
//                        Log.v("RETURN HED>>>>",activity.selectedInvHed.toString());
                        if (new FInvRHedDS(getActivity()).createOrUpdateInvRHed(returnHedList) > 0) {
//                            Log.v("START DET SAVE>>>>",">>>>>>");
                            seqno++;
                            ReturnDet.setFINVRDET_ID(index_id + "");
                            ReturnDet.setFINVRDET_SEQNO(seqno + "");
                            ReturnDet.setFINVRDET_COST_PRICE(lblPrice.getText().toString());
                            ReturnDet.setFINVRDET_SELL_PRICE(""+price);
                            ReturnDet.setFINVRDET_T_SELL_PRICE(""+price);
                            ReturnDet.setFINVRDET_DIS_AMT(editTotDisc.getText().toString());
                            ReturnDet.setFINVRDET_AMT(
                                    String.format("%.2f", Double.parseDouble(txtQty.getText().toString())
                                            * changedPrice));

                            ReturnDet.setFINVRDET_TAX_AMT(TaxedAmt);
                            ReturnDet.setFINVRDET_QTY(totPieces + "");
                            ReturnDet.setFINVRDET_BAL_QTY(totPieces + "");
                            ReturnDet.setFINVRDET_RETURN_REASON(lblReason.getText().toString());
                            ReturnDet.setFINVRDET_RETURN_REASON_CODE(new ReasonDS(getActivity()).getReaCodeByName(lblReason.getText().toString()));
                            ReturnDet.setFINVRDET_REFNO(RefNo);
                            ReturnDet.setFINVRDET_ITEMCODE(selectedItem.getFITEM_ITEM_CODE());
                            ReturnDet.setFINVRDET_PRILCODE("");
                            ReturnDet.setFINVRDET_IS_ACTIVE("1");
                            ReturnDet.setFINVRDET_TAXCOMCODE(
                                    new ItemsDS(getActivity()).getTaxComCodeByItemCode(selectedItem.getFITEM_ITEM_CODE()));
                            ReturnDet.setFINVRDET_TXN_DATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                            ReturnDet.setFINVRDET_TXN_TYPE("SR");
                            ReturnDet.setFINVRDET_RETURN_TYPE(returnType.getSelectedItem().toString().split("-")[0]);
                            ReturnDet.setFINVRDET_CHANGED_PRICE(""+changedPrice);

                            ReturnList.add(ReturnDet);
//                            Log.v("RETURNDET>>>>",ReturnList.toString());
                            //	if (!(bAdd.getText().equals("EDIT") && hasChanged == false)) {
                            new FInvRDetDS(getActivity()).createOrUpdateInvRDet(ReturnList);
                            //	}
//                            Log.v("FINISH DET SAVE>>>>",">>>>>>");
                            if (bAdd.getText().equals("EDIT"))
                                Toast.makeText(getActivity(), "Edited successfully !", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getActivity(), "Added successfully !", Toast.LENGTH_LONG).show();
                            FetchData();
                            clearTextFields();

                        }
                    }else{
                        Toast.makeText(getActivity(), "Please add quantities ", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Please fill details ", Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
                break;
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void deleteOrderDialog(final Context context, String title, final String itemCode,final String refNo) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Are you sure you want to delete this entry?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                int count = new FInvRDetDS(context).mDeleteRetDet(itemCode,refNo);

                if (count > 0)
                {
                    Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_LONG).show();
                    clearTextFields();
                    FetchData();
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

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void FetchData() {
        try {
            lv_return_det.setAdapter(null);
            returnList = new FInvRDetDS(getActivity()).getAllInvRDet(activity.selectedReturnHed.getFINVRHED_REFNO());
            lv_return_det.setAdapter(new ReturnDetailsAdapter(getActivity(), returnList));

        } catch (NullPointerException e) {
            Log.v(" Error", e.toString());
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void clearTextFields() {
        values = 0.0;
        index_id = 0;
        totPieces = 0;
        lblItemName.setText("");
        txtQty.setText("0");
        txtQty.clearFocus();
        lblNou.setText("0");
        lblPrice.setText("0.00");
        editTotDisc.setText("0.00");
        bAdd.setText("ADD");
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void prodcutDetailsDialogbox(ArrayList<Items> listParam) {

        list = listParam;
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.order_details_search_item);

        final SearchView search = (SearchView) dialog.findViewById(R.id.et_search);
        final ListView productList = (ListView) dialog.findViewById(R.id.lv_product_list);

        //clearTextFields();
        dialog.setCancelable(true);
        productList.clearTextFilter();

        //list = new ItemsDS(getActivity()).getAllItem("", "txntype ='21'", RefNo, new SalRepDS(getActivity()).getCurrentLocCode(),activity.selectedDebtor.getFDEBTOR_PRILLCODE());
        productList.setAdapter(new ProductAdapter(getActivity(), list));

        productList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedItem = list.get(position);
//                brandDisPer = new DebItemPriDS(getActivity()).getBrandDiscount(selectedItem.getFITEM_BRANDCODE(),
//                        activity.selectedRetDebtor.getFDEBTOR_CODE());
                lblItemName.setText(selectedItem.getFITEM_ITEM_NAME());
                Log.v("ITEM NAME",selectedItem.getFITEM_ITEM_NAME().toString());
                //lblNou.setText(selectedItem.getFITEM_NOUCASE());
                lblPrice.setText(selectedItem.getFITEM_PRICE());
                price = Double.parseDouble(selectedItem.getFITEM_PRICE());
                changedPrice = price;
                minPrice = Double.parseDouble(selectedItem.getFITEM_MIN_PRICE());
                maxPrice = Double.parseDouble(selectedItem.getFITEM_MAX_PRICE());
                Log.v("ITEM PRICE",selectedItem.getFITEM_PRICE().toString());
//                lblPrice.setText(new ItemPriDS(getActivity()).getProductPriceByCode(selectedItem.getFITEM_ITEM_CODE(),
//                activity.selectedRetDebtor.getFDEBTOR_PRILLCODE()));
                iQoh = Double.parseDouble(selectedItem.getFITEM_QOH());
                txtQty.requestFocus();
                txtQty.selectAll();
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.dismiss();

            }
        });

        search.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                list.clear();
                list = new ItemsDS(getActivity()).getAllItem(newText, "TxnType ='SR'", RefNo, new SalRepDS(getActivity()).getCurrentLocCode(),activity.selectedDebtor.getFDEBTOR_PRILLCODE());
                productList.clearTextFilter();
                productList.setAdapter(new ProductAdapter(getActivity(), list));
                return false;
            }
        });
        dialog.show();
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void reasonsDialogbox() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.return_reason_item);
        final ListView reasonListView = (ListView) dialog.findViewById(R.id.lv_reason_list);
        dialog.setCancelable(true);
        reasonListView.clearTextFilter();

        reasonList = new ReasonDS(getActivity()).getAllReasonsByRtCode("RT02");

        reasonListView.setAdapter(new ReturnReasonAdapter(getActivity(), reasonList));

        reasonListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedReason = reasonList.get(position);
                lblReason.setText(selectedReason.getFREASON_NAME());
                lblItemName.requestFocus();
                dialog.dismiss();

            }
        });

        dialog.show();

    }
    public void mRefreshData() {
        try {
//            amount = Double.parseDouble(txtQty.getText().toString())
//                    * Double.parseDouble(lblPrice.getText().toString());
            FInvRHed hed = new FInvRHed();
            ArrayList<FInvRHed> returnHedList = new ArrayList<FInvRHed>();
            hed.setFINVRHED_REFNO(RefNo);
            hed.setFINVRHED_MANUREF(activity.selectedInvHed.getFINVHED_MANUREF());
            hed.setFINVRHED_REMARKS(activity.selectedInvHed.getFINVHED_REMARKS());
            hed.setFINVRHED_ADD_USER(new SalRepDS(getActivity()).getCurrentRepCode());
            hed.setFINVRHED_ADD_DATE(currentTime());
            hed.setFINVRHED_ADD_MACH(localSP.getString("MAC_Address", "No MAC Address").toString());
            hed.setFINVRHED_TXNTYPE("42");
            hed.setFINVRHED_TXN_DATE(activity.selectedInvHed.getFINVHED_TXNDATE());
            hed.setFINVRHED_IS_ACTIVE("1");
            hed.setFINVRHED_IS_SYNCED("0");

            if (activity.selectedRetDebtor != null) {
                hed.setFINVRHED_DEBCODE(activity.selectedRetDebtor.getFDEBTOR_CODE());
                hed.setFINVRHED_TAX_REG(activity.selectedRetDebtor.getFDEBTOR_TAX_REG());
            }

            hed.setFINVRHED_LOCCODE(new SalRepDS(getActivity()).getCurrentLocCode());
            hed.setFINVRHED_ROUTE_CODE(new SharedPref(getActivity()).getGlobalVal("KeyRouteCode"));
            hed.setFINVRHED_COSTCODE("");

            activity.selectedReturnHed = hed;
            SharedPreferencesClass.setLocalSharedPreference(activity, "Return_Start_Time", currentTime());

            returnHedList.add(activity.selectedReturnHed);
//                        Log.v("RETURN HED>>>>",activity.selectedInvHed.toString());
            new FInvRHedDS(getActivity()).createOrUpdateInvRHed(returnHedList );
            lv_return_det.setAdapter(null);
            returnList = new FInvRDetDS(getActivity()).getAllInvRDet(activity.selectedReturnHed.getFINVRHED_REFNO());
            lv_return_det.setAdapter(new ReturnDetailsAdapter(getActivity(), returnList));
            lblPrice.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomKeypadDialogPrice keypadPrice = new CustomKeypadDialogPrice(getActivity(), true, new CustomKeypadDialogPrice.IOnOkClickListener() {
                        @Override
                        public void okClicked(double value) {
                            //price cannot be changed less than gross profit
                            //changedPrice = price;
                            //validation removed from return 2019/04/01 - said menaka
                           // if(minPrice <=value && value <= maxPrice) {
                                //  save changed price
                                 new FInvRDetDS(getActivity()).updateProductPrice(selectedItem.getFITEM_ITEM_CODE(), String.valueOf(price));
                                //  value should be set for another variable in preProduct
                                //  preProduct.setPREPRODUCT_PRICE(String.valueOf(value));
                                changedPrice = value;
                                lblPrice.setText(""+changedPrice);
//                            }else{
//                                //changedPrice = price;
//                                Toast.makeText(getActivity(),"Price cannot be change..",Toast.LENGTH_LONG).show();
//                            }
                        }
                    });
                    keypadPrice.show();

                    keypadPrice.setHeader("CHANGE PRICE");
//                if(preProduct.getPREPRODUCT_CHANGED_PRICE().equals("0")){
                    keypadPrice.loadValue(changedPrice);
                }
            });
//            amount = Double.parseDouble(txtQty.getText().toString())
//                    * changedPrice;
//            if(changedPrice != price){
//                new FInvRDetDS(getActivity()).updateProductPrice(selectedItem.getFITEM_ITEM_CODE(),""+changedPrice);
//            }
        } catch (NullPointerException e) {
            Log.v(" Error", e.toString());
        }
    }
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            InnerReturnDetails.this.mRefreshData();
        }
    }
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private String currentTime() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/



    //------------------------------------------------------------------------------------------------------------------------------------
    public class LoardingItemsFromDB extends AsyncTask<Object, Object, ArrayList<Items>> {
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
        protected ArrayList<Items> doInBackground(Object... objects) {

            itemList = null;
            itemList = new ItemsDS(getActivity()).getAllItem("", "TxnType ='SR'", RefNo, new SalRepDS(getActivity()).getCurrentLocCode(),activity.selectedDebtor.getFDEBTOR_PRILLCODE());
            return itemList;
        }


        @Override
        protected void onPostExecute(ArrayList<Items> products) {
            super.onPostExecute(products);

            if(pDialog.isShowing()){
                pDialog.dismiss();
            }

            prodcutDetailsDialogbox(itemList);

        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
}
