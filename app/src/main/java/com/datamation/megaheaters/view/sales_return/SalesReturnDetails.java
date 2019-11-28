package com.datamation.megaheaters.view.sales_return;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
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
import com.datamation.megaheaters.data.ItemPriDS;
import com.datamation.megaheaters.data.ItemsDS;
import com.datamation.megaheaters.data.ReasonDS;
import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.data.TaxDetDS;
import com.datamation.megaheaters.listviewitems.CustomKeypadDialogPrice;
import com.datamation.megaheaters.model.FInvRDet;
import com.datamation.megaheaters.model.FInvRHed;
import com.datamation.megaheaters.model.Items;
import com.datamation.megaheaters.model.Reason;
import com.datamation.megaheaters.view.MainActivity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SalesReturnDetails extends Fragment implements OnClickListener {

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
   // TextView lblNou, lblPrice;
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
    ArrayList<Reason> reasonList = null;
    MainActivity activity;

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
        lblPrice.setOnClickListener(this);

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
        strList.add("Select Return type to continue ...");
        strList.add("MR");
        strList.add("UR");

        final ArrayAdapter<String> returnTypeAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.return_spinner_item, strList);
        returnTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        returnType.setAdapter(returnTypeAdapter);
        FetchData();

			/*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        txtQty.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if ((txtQty.length() > 0)) {
                    totPieces = Integer.parseInt(txtQty.getText().toString());
                    hasChanged = true;
                    amount = Double.parseDouble(txtQty.getText().toString())
                            * Double.parseDouble(lblPrice.getText().toString());
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
                deleteOrderDialog(getActivity(), "Return Details ", returnDet.getFINVRDET_ID());
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
                lblPrice.setText(new ItemPriDS(getActivity()).getProductPriceByCode(
                        selectedItem.getFITEM_ITEM_CODE(), new SalRepDS(getActivity()).getCurrentPriLCode().trim()));
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
                prodcutDetailsDialogbox();
                break;
            case R.id.tv_price:

                        CustomKeypadDialogPrice keypadPrice = new CustomKeypadDialogPrice(getActivity(), true, new CustomKeypadDialogPrice.IOnOkClickListener() {
                            @Override
                            public void okClicked(double value) {
                                //price cannot be changed less than gross profit
                                //changedPrice = price;
                                //validation removed from return 2019/04/01 - said menaka
                                // if(minPrice <=value && value <= maxPrice) {
                                //  save changed price
                                if(new ItemPriDS(getActivity()).getProductPriceByCode(selectedItem.getFITEM_ITEM_CODE(), new SalRepDS(getActivity()).getCurrentPriLCode().trim()).equals("") || new ItemPriDS(getActivity()).getProductPriceByCode(selectedItem.getFITEM_ITEM_CODE(), new SalRepDS(getActivity()).getCurrentPriLCode().trim()).equals(null)) {
                                    price = 0.0;
                                }else{
                                    price = Double.parseDouble(new ItemPriDS(getActivity()).getProductPriceByCode(selectedItem.getFITEM_ITEM_CODE(),new SalRepDS(getActivity()).getCurrentPriLCode().trim()));
                                }
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


                break;
            case R.id.btn_reason_search:
                reasonsDialogbox();
                break;
            case R.id.btn_add: {

                if (!(lblItemName.getText().toString().equals("") && (lblReason.getText().toString().equals("")))) {

                    if (values >= 0.0 && totPieces > 0) {

                        FInvRDet ReturnDet = new FInvRDet();
                        ArrayList<FInvRDet> ReturnList = new ArrayList<FInvRDet>();

                        ArrayList<FInvRHed> returnHedList = new ArrayList<FInvRHed>();

                        String TaxedAmt = new TaxDetDS(getActivity()).calculateTax(selectedItem.getFITEM_ITEM_CODE(),
                                new BigDecimal(amount - Double.parseDouble(editTotDisc.getText().toString())));

                        returnHedList.add(activity.selectedReturnHed);

                        if (new FInvRHedDS(getActivity()).createOrUpdateInvRHed(returnHedList) > 0) {
                            seqno++;
                            ReturnDet.setFINVRDET_ID(index_id + "");
                            ReturnDet.setFINVRDET_SEQNO(seqno + "");
                            ReturnDet.setFINVRDET_COST_PRICE(lblPrice.getText().toString());
                            ReturnDet.setFINVRDET_SELL_PRICE(""+price);
                            ReturnDet.setFINVRDET_T_SELL_PRICE(""+price);
                            ReturnDet.setFINVRDET_DIS_AMT(editTotDisc.getText().toString());
                            ReturnDet.setFINVRDET_AMT(
                                    String.format("%.2f", Double.parseDouble(txtQty.getText().toString())
                                            * changedPrice));ReturnDet.setFINVRDET_CHANGED_PRICE(String.format("%.2f", changedPrice));
                            ReturnDet.setFINVRDET_TAX_AMT(TaxedAmt);
                            ReturnDet.setFINVRDET_QTY(totPieces + "");
                            ReturnDet.setFINVRDET_BAL_QTY(totPieces + "");
                            ReturnDet.setFINVRDET_RETURN_REASON(lblReason.getText().toString());
                            ReturnDet.setFINVRDET_RETURN_REASON_CODE(new ReasonDS(getActivity()).getReaCodeByName(lblReason.getText().toString()));
                            ReturnDet.setFINVRDET_REFNO(RefNo);
                            ReturnDet.setFINVRDET_ITEMCODE(selectedItem.getFITEM_ITEM_CODE());
                            ReturnDet.setFINVRDET_PRILCODE(new SalRepDS(getActivity()).getCurrentPriLCode().trim());
                            ReturnDet.setFINVRDET_IS_ACTIVE("1");
                            ReturnDet.setFINVRDET_TAXCOMCODE(
                                    new ItemsDS(getActivity()).getTaxComCodeByItemCode(selectedItem.getFITEM_ITEM_CODE()));
                            ReturnDet.setFINVRDET_TXN_DATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                            ReturnDet.setFINVRDET_TXN_TYPE("21");
                            ReturnDet.setFINVRDET_RETURN_TYPE(returnType.getSelectedItem().toString());

                            ReturnList.add(ReturnDet);

                            //	if (!(bAdd.getText().equals("EDIT") && hasChanged == false)) {
                            new FInvRDetDS(getActivity()).createOrUpdateInvRDet(ReturnList);
                            //	}

                            if (bAdd.getText().equals("EDIT"))
                                android.widget.Toast
                                        .makeText(getActivity(), "Edited successfully !", android.widget.Toast.LENGTH_LONG)
                                        .show();
                            else
                                android.widget.Toast
                                        .makeText(getActivity(), "Added successfully !", android.widget.Toast.LENGTH_LONG)
                                        .show();
                            FetchData();
                            clearTextFields();

                        }
                    }else{
                        Toast.makeText(getActivity(),"Please enter quantity",Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Toast.makeText(getActivity(),"Please select item and return reason",Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                break;
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void deleteOrderDialog(final Context context, String title, final String Id) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Are you sure you want to delete this entry?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                int count = new FInvRDetDS(context).deleteRetDetByID(Id);

                if (count > 0) {
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

    public void prodcutDetailsDialogbox() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.order_details_search_item);

        final SearchView search = (SearchView) dialog.findViewById(R.id.et_search);
        final ListView productList = (ListView) dialog.findViewById(R.id.lv_product_list);

        clearTextFields();
        dialog.setCancelable(true);
        productList.clearTextFilter();

        list = new ItemsDS(getActivity()).getAllItem("", "txntype ='21'", RefNo, new SalRepDS(getActivity()).getCurrentLocCode());
        productList.setAdapter(new ProductAdapter(getActivity(), list));

        productList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedItem = list.get(position);
                brandDisPer = new DebItemPriDS(getActivity()).getBrandDiscount(selectedItem.getFITEM_BRANDCODE(),
                        activity.selectedRetDebtor.getFDEBTOR_CODE());
                lblItemName.setText(selectedItem.getFITEM_ITEM_NAME());
                lblNou.setText(selectedItem.getFITEM_NOUCASE());
                if(new ItemPriDS(getActivity()).getProductPriceByCode(selectedItem.getFITEM_ITEM_CODE(), new SalRepDS(getActivity()).getCurrentPriLCode().trim()).equals("") || new ItemPriDS(getActivity()).getProductPriceByCode(selectedItem.getFITEM_ITEM_CODE(), new SalRepDS(getActivity()).getCurrentPriLCode().trim()).equals(null)) {
                    price = 0.0;
                }else{
                    price = Double.parseDouble(new ItemPriDS(getActivity()).getProductPriceByCode(selectedItem.getFITEM_ITEM_CODE(), activity.selectedRetDebtor.getFDEBTOR_PRILLCODE()));
                    changedPrice = price;
                }
                lblPrice.setText(""+price);
              //  lblPrice.setText(new ItemPriDS(getActivity()).getProductPriceByCode(selectedItem.getFITEM_ITEM_CODE(),
              //          activity.selectedRetDebtor.getFDEBTOR_PRILLCODE()));
                iQoh = Double.parseDouble(selectedItem.getFITEM_QOH());
                txtQty.requestFocus();
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
                list = new ItemsDS(getActivity()).getAllItem(newText, "txntype ='21'", RefNo, locCode);

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


}
