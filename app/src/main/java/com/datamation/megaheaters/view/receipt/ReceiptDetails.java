package com.datamation.megaheaters.view.receipt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.ReceiptAdapter;
import com.datamation.megaheaters.control.IResponseListener;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.data.FDDbNoteDS;
import com.datamation.megaheaters.model.FDDbNote;
import com.datamation.megaheaters.view.MainActivity;

import java.util.ArrayList;

public class ReceiptDetails extends Fragment implements OnClickListener {

    View view;
    Button bAdd, bCancel;
    EditText et_RefNo, et_date, et_dueAmt, et_enterAmt, et_BalAmt,  et_remark;
    TextView et_remnant;
    int seqno = 0, index_id = 0;
    public static SharedPreferences localSP;
    public static final String SETTINGS = "SETTINGS";
    ListView lv_order_det;
    ArrayList<FDDbNote> orderList;
    SharedPref mSharedPref;
    String RefNo;
    FDDbNote selectedItem;
    double ReceivedAmt;
    MainActivity mainActivity;
    boolean isAllocated = false;
    MyReceiver r;
    IResponseListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sales_management_single_receipt_details, container, false);
        mSharedPref = new SharedPref(getActivity());

        seqno = 0;
        bAdd = (Button) view.findViewById(R.id.btn_add);
        bCancel = (Button) view.findViewById(R.id.btnCancel);
        et_BalAmt = (EditText) view.findViewById(R.id.et_BalAmt);
        et_date = (EditText) view.findViewById(R.id.et_fdDate);
        et_dueAmt = (EditText) view.findViewById(R.id.et_dueAmt);
        et_enterAmt = (EditText) view.findViewById(R.id.et_enterAmt);
        et_RefNo = (EditText) view.findViewById(R.id.et_RefNo);
        et_remnant = (TextView) view.findViewById(R.id.et_Remnant);
        et_remark = (EditText) view.findViewById(R.id.et_Remark);
        lv_order_det = (ListView) view.findViewById(R.id.lv_order_det);
        RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.RecNumVal));
        mainActivity = (MainActivity) getActivity();

        bCancel.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                clearTextFields();
                lv_order_det.setEnabled(true);
                FetchData();
            }
        });

        et_enterAmt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (et_enterAmt.getText().length() > 0) {
//commented by rashmi -2019/05/06 because over payment allocated allow
//                    if (Double.parseDouble(et_enterAmt.getText().toString().replaceAll(",", "")) > Double.parseDouble(et_dueAmt.getText().toString().replaceAll(",", ""))) {
//                        Toast.makeText(getActivity(), "Entered amount exceeds Due amount !", Toast.LENGTH_SHORT).show();
//                        et_enterAmt.setText("0.00");
//                        et_BalAmt.setText("0.00");
//
//                    } else if (Double.parseDouble(et_enterAmt.getText().toString().replaceAll(",", "")) > Double.parseDouble(et_remnant.getText().toString().toString().replaceAll(",", ""))) {
//                        Toast.makeText(getActivity(), "Entered amount exceeds Remaining amount !", Toast.LENGTH_SHORT).show();
//                        et_enterAmt.setText("0.00");
//                        et_BalAmt.setText("0.00");
//                    } else {
                        et_BalAmt.setText(String.format("%,.2f", (Double.parseDouble(et_dueAmt.getText().toString().replaceAll(",", "")) - Double.parseDouble(et_enterAmt.getText().toString().replaceAll(",", "")))));//                  }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        lv_order_det.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {

                FDDbNote fdDbNote = orderList.get(position);
                et_enterAmt.clearFocus();
                et_enterAmt.requestFocus();

                if (isAllocated) {
                    if (fdDbNote.getFDDBNOTE_ENTER_AMT().length() <= 0) {
                        Toast.makeText(getActivity(), "No remaining allocation amount !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                selectedItem = fdDbNote;
                index_id = Integer.parseInt(fdDbNote.getFDDBNOTE_ID());
                et_RefNo.setText(fdDbNote.getFDDBNOTE_REFNO());
                et_date.setText(fdDbNote.getFDDBNOTE_TXN_DATE());
                et_remark.setText(fdDbNote.getFDDBNOTE_REMARKS());
                et_dueAmt.setText(String.format("%,.2f", (Double.parseDouble(fdDbNote.getFDDBNOTE_TOT_BAL()))));

                if (fdDbNote.getFDDBNOTE_ENTER_AMT() == null || fdDbNote.getFDDBNOTE_ENTER_AMT().equals("")) {
                    et_enterAmt.setText("0.00");
                } else {
                    et_remnant.setText(String.format("%,.2f", (Double.parseDouble(et_remnant.getText().toString().replaceAll(",", "")) + Double.parseDouble(fdDbNote.getFDDBNOTE_ENTER_AMT()))));
                    et_enterAmt.setText(String.format("%,.2f", (Double.parseDouble(fdDbNote.getFDDBNOTE_ENTER_AMT().toString()))));
                }

                et_enterAmt.clearFocus();
                et_enterAmt.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.showSoftInput(et_enterAmt, InputMethodManager.SHOW_IMPLICIT);
                }

                et_enterAmt.selectAll();
                bAdd.setEnabled(true);
                bCancel.setEnabled(true);
                lv_order_det.setEnabled(false);
            }
        });

        /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        bAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (et_enterAmt.getText().toString().equals("") || et_enterAmt.getText().toString() == "")
                {
                    Toast.makeText(getActivity(), "Enter amount can't be empty", Toast.LENGTH_LONG).show();
                    et_enterAmt.requestFocus();
                }
                else
                {
                    ArrayList<FDDbNote> list = new ArrayList<>();
                    double enteramount = 0.00;
                    double dueamount = 0.00;
                    enteramount = Double.parseDouble(et_enterAmt.getText().toString().replaceAll(",", ""));
                    int emptycheck = Double.compare(enteramount, dueamount);

                    if (emptycheck > 0) {

                        dueamount = Double.parseDouble(selectedItem.getFDDBNOTE_TOT_BAL());
                        int check = Double.compare(enteramount, dueamount);

                        if (check == 0 || (!et_remark.getText().toString().equals(""))) {

                            FDDbNote fdDbNote = new FDDbNote();
                            fdDbNote.setFDDBNOTE_ADD_DATE(selectedItem.getFDDBNOTE_ADD_DATE());
                            fdDbNote.setFDDBNOTE_ADD_MACH(selectedItem.getFDDBNOTE_ADD_MACH());
                            fdDbNote.setFDDBNOTE_ADD_USER(selectedItem.getFDDBNOTE_ADD_USER());
                            fdDbNote.setFDDBNOTE_AMT(selectedItem.getFDDBNOTE_AMT());
                            fdDbNote.setFDDBNOTE_B_AMT(selectedItem.getFDDBNOTE_B_AMT());
                            fdDbNote.setFDDBNOTE_B_TAX_AMT(selectedItem.getFDDBNOTE_B_TAX_AMT());
                            fdDbNote.setFDDBNOTE_CR_ACC(selectedItem.getFDDBNOTE_CR_ACC());
                            fdDbNote.setFDDBNOTE_CUR_CODE(selectedItem.getFDDBNOTE_CUR_CODE());
                            fdDbNote.setFDDBNOTE_CUR_RATE(selectedItem.getFDDBNOTE_CUR_RATE());
                            fdDbNote.setFDDBNOTE_DEB_CODE(selectedItem.getFDDBNOTE_DEB_CODE());

                            if (Double.parseDouble(et_enterAmt.getText().toString().replaceAll(",", "")) > 0)
                                fdDbNote.setFDDBNOTE_ENTER_AMT(String.format("%.2f", Double.parseDouble(et_enterAmt.getText().toString().replaceAll(",", ""))));
                            else
                                fdDbNote.setFDDBNOTE_ENTER_AMT("");

                            fdDbNote.setFDDBNOTE_GL_BATCH(selectedItem.getFDDBNOTE_GL_BATCH());
                            fdDbNote.setFDDBNOTE_GL_POST(selectedItem.getFDDBNOTE_GL_POST());
                            fdDbNote.setFDDBNOTE_ID(index_id + "");
                            fdDbNote.setFDDBNOTE_MANU_REF(selectedItem.getFDDBNOTE_MANU_REF());
                            fdDbNote.setFDDBNOTE_OV_PAY_AMT(selectedItem.getFDDBNOTE_OV_PAY_AMT());
                            fdDbNote.setFDDBNOTE_PRT_COPY(selectedItem.getFDDBNOTE_PRT_COPY());
                            fdDbNote.setFDDBNOTE_RECORD_ID(selectedItem.getFDDBNOTE_PRT_COPY());
                            fdDbNote.setFDDBNOTE_REF_INV(selectedItem.getFDDBNOTE_REF_INV());
                            fdDbNote.setFDDBNOTE_REFNO(selectedItem.getFDDBNOTE_REFNO());
                            fdDbNote.setFDDBNOTE_REFNO1(selectedItem.getFDDBNOTE_REFNO1());
                            fdDbNote.setFDDBNOTE_REP_CODE(selectedItem.getFDDBNOTE_REP_CODE());
                            fdDbNote.setFDDBNOTE_SALE_REF_NO(selectedItem.getFDDBNOTE_SALE_REF_NO());
                            fdDbNote.setFDDBNOTE_TAX_AMT(selectedItem.getFDDBNOTE_TAX_AMT());
                            fdDbNote.setFDDBNOTE_TAX_COM_CODE(selectedItem.getFDDBNOTE_TAX_COM_CODE());
                            fdDbNote.setFDDBNOTE_TOT_BAL(selectedItem.getFDDBNOTE_TOT_BAL());
                            fdDbNote.setFDDBNOTE_TOT_BAL1(selectedItem.getFDDBNOTE_TOT_BAL1());
                            fdDbNote.setFDDBNOTE_TXN_DATE(selectedItem.getFDDBNOTE_TXN_DATE());
                            fdDbNote.setFDDBNOTE_TXN_TYPE(selectedItem.getFDDBNOTE_TXN_TYPE());
                            list.add(fdDbNote);

                            new FDDbNoteDS(getActivity()).createOrUpdateFDDbNote(list);
                            Toast.makeText(getActivity(), "Updated successfully !", Toast.LENGTH_SHORT).show();
                            clearTextFields();
                            lv_order_det.setEnabled(true);
                            FetchData();
                        } else {
                            Toast.makeText(getActivity(), "Please Enter Remark !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please Enter Valid Enter Amount !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public double getTotalSale(ArrayList<FDDbNote> list) {

        double d = 0;

        if (list.size() > 0) {
            for (FDDbNote fnote : list) {
                if (fnote.getFDDBNOTE_ENTER_AMT() != null && !fnote.getFDDBNOTE_ENTER_AMT().equals(""))
                    d += Double.parseDouble(fnote.getFDDBNOTE_ENTER_AMT().toString());
            }
        }
        return d;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void FetchData() {

            if (mainActivity.selectedDebtor != null && mainActivity.selectedRecHed != null) {
                if(mSharedPref.getGlobalVal("ReckeyPayMode").equals("-SELECT-")){
                    Toast.makeText(getActivity(), "Please select pay mode", android.widget.Toast.LENGTH_SHORT).show();
                    navigateToHeader(1);
                }else {
                    if(mSharedPref.getGlobalVal("ReckeyPayMode").equals("CHEQUE")){
                        if(mSharedPref.getGlobalVal("ReckeyCHQDate").equals("-SELECT DATE-") || mSharedPref.getGlobalVal("ReckeyCHQDate").equals("***") || mSharedPref.getGlobalVal("ReckeyCHQDate").equals("") || mSharedPref.getGlobalVal("ReckeyCHQDate").equals(null)){
                            Toast.makeText(getActivity(), "Please enter cheque date", android.widget.Toast.LENGTH_SHORT).show();
                            navigateToHeader(1);
                        }else if(mSharedPref.getGlobalVal("ReckeyCHQNo").equals("0")){
                            Toast.makeText(getActivity(), "Please enter cheque no", android.widget.Toast.LENGTH_SHORT).show();
                            navigateToHeader(1);
                        }else{
                            lv_order_det.setAdapter(null);
                            orderList = new FDDbNoteDS(getActivity()).getAllRecords(mainActivity.selectedDebtor.getFDEBTOR_CODE(), false);
                            lv_order_det.setAdapter(new ReceiptAdapter(getActivity(), orderList, false, RefNo));
                            double rem = (ReceivedAmt - getTotalSale(orderList));
                            et_remnant.setText(String.format("%,.2f", (rem)));

                            if (rem <= 0)
                                isAllocated = true;
                            else
                                isAllocated = false;

                            mSharedPref.setGlobalVal("ReckeyRemnant", et_remnant.getText().toString().replaceAll(",", ""));

                        }

                    }else {
                        lv_order_det.setAdapter(null);
                        orderList = new FDDbNoteDS(getActivity()).getAllRecords(mainActivity.selectedDebtor.getFDEBTOR_CODE(), false);
                        lv_order_det.setAdapter(new ReceiptAdapter(getActivity(), orderList, false, RefNo));
                        double rem = (ReceivedAmt - getTotalSale(orderList));
                        et_remnant.setText(String.format("%,.2f", (rem)));

                        if (rem <= 0)
                            isAllocated = true;
                        else
                            isAllocated = false;

                        mSharedPref.setGlobalVal("ReckeyRemnant", et_remnant.getText().toString().replaceAll(",", ""));
                    }
                }

        }
    else
        {
            Toast.makeText(getActivity(), "Enter Header values", android.widget.Toast.LENGTH_SHORT).show();
            navigateToHeader(1);
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void clearTextFields() {
        index_id = 0;
        et_BalAmt.setText("0.00");
        et_date.setText("N/A");
        et_dueAmt.setText("0.00");
        et_enterAmt.setText("0.00");
        et_remark.setText("");
        et_RefNo.setText("N/A");
        selectedItem = null;
        bAdd.setEnabled(false);
        bCancel.setEnabled(false);
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onClick(View arg0) {
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/


    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (IResponseListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onButtonPressed");
        }
    }


    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ReceiptDetails.this.mRefreshHeader();
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

	public void mRefreshHeader(){
        ReceivedAmt = mainActivity.ReceivedAmt;
        FetchData();
    }


    public void navigateToHeader(int position) {
        MainActivity activity = (MainActivity) getActivity();
        listener.moveNextFragmentRece();
    }
}
