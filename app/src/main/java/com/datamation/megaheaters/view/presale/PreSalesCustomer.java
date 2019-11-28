package com.datamation.megaheaters.view.presale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.CustomerAdapter;
import com.datamation.megaheaters.control.CusInfoBox;
import com.datamation.megaheaters.control.IResponseListener;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.data.DebtorDS;
import com.datamation.megaheaters.data.TranSOHedDS;
import com.datamation.megaheaters.model.Debtor;
import com.datamation.megaheaters.model.TranSOHed;
import com.datamation.megaheaters.view.MainActivity;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PreSalesCustomer extends Fragment {
    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    View view;
    ListView lvCustomers;
    ArrayList<Debtor> customerList;
    Debtor debtor;
    CustomerAdapter customerAdapter;
    SharedPref mSharedPref;
    IResponseListener listener;
    TextView txtCusName;
    ImageButton btnCust,btnSearch;
    SweetAlertDialog pDialog;
    private  TranSOHed tmpsoHed=null;


    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sales_management_pre_sales_customer, container, false);
        mSharedPref = new SharedPref(getActivity());
        MainActivity activity = (MainActivity) getActivity();
        lvCustomers = (ListView) view.findViewById(R.id.cus_lv);
        localSP = activity.getSharedPreferences(SETTINGS, 0);

        btnCust = (ImageButton) view.findViewById(R.id.btnCust);
        txtCusName = (TextView) view.findViewById(R.id.txtSelCust);
        btnSearch = (ImageButton) view.findViewById(R.id.btnsearch_cus);
        //-----------------------------------from Reorder Only-----------------------------------------------------------------------------------
        Bundle mBundle = getArguments();

        if (mBundle != null) {
            tmpsoHed = (TranSOHed) mBundle.getSerializable("order");
            if (tmpsoHed != null) {
                activity.selectedDebtor = new DebtorDS(getActivity()).getSelectedCustomerByCode(tmpsoHed.getFTRANSOHED_DEBCODE());
                Log.d("<>*Pre sales cus****", "" + tmpsoHed.getFTRANSOHED_REFNO());
                new SharedPref(getActivity()).setGlobalVal("PrekeyCusCode", activity.selectedDebtor.getFDEBTOR_CODE());
                mSharedPref.setGlobalVal("PrekeyCustomer", "Y");
                new SharedPref(getActivity()).setGlobalVal("PrekeyCreditDis", activity.selectedDebtor.getFDEBTOR_CREDITDISCOUNT());

            }

        }
//-----------------------------------------------------------------------------------------------------------------------------------
        if (activity.selectedSOHed == null) {
            TranSOHed SOHed = new TranSOHedDS(getActivity()).getActiveSOHed();
            if (SOHed != null) {
                if (activity.selectedDebtor == null)
                    activity.selectedDebtor = new DebtorDS(getActivity()).getSelectedCustomerByCode(SOHed.getFTRANSOHED_DEBCODE());
            }
        }

        if(activity.selectedDebtor!=null)
            txtCusName.setText(activity.selectedDebtor.getFDEBTOR_NAME());

 /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        btnCust.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        btnCust.setBackground(getResources().getDrawable(R.drawable.cus_down));
                    }
                    break;

                    case MotionEvent.ACTION_UP: {
                        btnCust.setBackground(getResources().getDrawable(R.drawable.cus));
                    }
                    break;
                }
                return false;
            }
        });

        /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
        btnCust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new AsyncTask<Void, Void, Void>() {

                    protected void onPreExecute() {
                        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        pDialog.setTitleText("Retrieving customers..");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        customerList = new DebtorDS(getActivity()).getRouteCustomers("", "");
                        customerAdapter = new CustomerAdapter(getActivity(), customerList);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        pDialog.dismiss();
                        lvCustomers.setAdapter(customerAdapter);
                    }
                }.execute();

            }
        });

   /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        btnSearch.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        btnSearch.setBackground(getResources().getDrawable(R.drawable.search_cus_down));
                    }
                    break;

                    case MotionEvent.ACTION_UP: {
                        btnSearch.setBackground(getResources().getDrawable(R.drawable.search_cus));
                    }
                    break;
                }
                return false;
            }
        });
 /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchDialogBox();

            }
        });
	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        lvCustomers.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {

				/* Check whether automatic date time option checked or not */
                int i = android.provider.Settings.Global.getInt(getActivity().getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0);
                /* If option is selected */
                if (i > 0) {
                    MainActivity activity = (MainActivity) getActivity();
                    debtor = customerList.get(position);
                    activity.selectedDebtor = debtor;
                    activity.cusPosition = position;
                    txtCusName.setText(activity.selectedDebtor.getFDEBTOR_NAME());
                    new SharedPref(getActivity()).setGlobalVal("PrekeyCusCode", debtor.getFDEBTOR_CODE());
                    new SharedPref(getActivity()).setGlobalVal("PrekeyCreditDis", debtor.getFDEBTOR_CREDITDISCOUNT());

                    navigateToHeader(position);
                    /* if not selected */
                } else {
                    android.widget.Toast.makeText(getActivity(), "Please tick the 'Automatic Date and Time' option to continue..", android.widget.Toast.LENGTH_LONG).show();
                    /* Show Date/time settings dialog */
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                }
            }
        });

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        lvCustomers.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CusInfoBox alertBox = new CusInfoBox();
                if(debtor!=null)
               alertBox.debtorDetailsDialogbox(getActivity(),debtor.getFDEBTOR_CODE(),debtor);

                return true;
            }
        });
        return view;
    }

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void navigateToHeader(int position) {

        MainActivity activity = (MainActivity) getActivity();
        debtor = customerList.get(position);
        activity.selectedDebtor = debtor;
        android.widget.Toast.makeText(getActivity(), debtor.getFDEBTOR_NAME() + " selected", android.widget.Toast.LENGTH_LONG).show();
        mSharedPref.setGlobalVal("PrekeyCustomer", "Y");
        listener.moveNextFragment_Pre();
    }

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (IResponseListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onButtonPressed");
        }
    }


      /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mSearchDialogBox() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.input_dialogbox, null);
        final EditText enteredText = (EditText) promptView.findViewById(R.id.txtTextbox);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        alertDialogBuilder.setCancelable(false).setPositiveButton("Search", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                lvCustomers.setAdapter(null);
                customerList = new DebtorDS(getActivity()).getRouteCustomers("", enteredText.getText().toString());
                lvCustomers.setAdapter(new CustomerAdapter(getActivity(), customerList));
            }

        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.create().show();
    }

}
