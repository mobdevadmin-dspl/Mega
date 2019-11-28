package com.datamation.megaheaters.view.receipt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.CustomerAdapter;
import com.datamation.megaheaters.control.IResponseListener;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.data.DebtorDS;
import com.datamation.megaheaters.data.FDDbNoteDS;
import com.datamation.megaheaters.data.RecHedDS;
import com.datamation.megaheaters.model.Debtor;
import com.datamation.megaheaters.model.RecHed;
import com.datamation.megaheaters.view.MainActivity;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReceiptCustomer extends Fragment {
    View view;
    ListView lvCustomers;
    ArrayList<Debtor> customerList;
    Debtor debtor;
    CustomerAdapter customerAdapter;
    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    SharedPref mSharedPref;
    TextView txtCusName;
    ImageButton btnCust, btnSearch;
    ProgressDialog progressDialog;
    SweetAlertDialog pDialog;
    IResponseListener listener;

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sales_management_pre_sales_customer, container, false);
        setHasOptionsMenu(true);
        mSharedPref = new SharedPref(getActivity());
        MainActivity activity = (MainActivity) getActivity();
        lvCustomers = (ListView) view.findViewById(R.id.cus_lv);

        localSP = activity.getSharedPreferences(SETTINGS, 0);
        btnCust = (ImageButton) view.findViewById(R.id.btnCust);
        btnSearch = (ImageButton) view.findViewById(R.id.btnsearch_cus);

        txtCusName = (TextView) view.findViewById(R.id.txtSelCust);
        ReferenceNum referenceNum = new ReferenceNum(getActivity());
        ArrayList<RecHed> getRecHed = new RecHedDS(getActivity()).getAllCompletedRecHedS(referenceNum.getCurrentRefNo(getResources().getString(R.string.RecNumVal)));

        /*if (!getRecHed.isEmpty()) {

            for (RecHed recHed : getRecHed) {

                if (activity.selectedDebtor == null) {
                    DebtorDS debtorDS = new DebtorDS(getActivity());
                    activity.selectedDebtor = debtorDS.getSelectedCustomerByCode(recHed.getFPRECHED_DEBCODE());
                    activity.cusPosition = Integer.parseInt(localSP.getString("Van_Sales_Cus_Position", "0").toString());
                }
            }
        }*/

        /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

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


        /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

//        lvCustomers = (ListView) view.findViewById(R.id.cus_lv);
//        lvCustomers.clearTextFilter();
//        dsDebtorDS = new DebtorDS(getActivity());
//        customerList = dsDebtorDS.getOutstandingCustomersForReceipt();
//        customerAdapter = new CustomerAdapter(getActivity(), customerList);
//        lvCustomers.setAdapter(customerAdapter);
//
//        if (activity.selectedDebtor != null) {
//            lvCustomers.setSelection(activity.cusPosition);
//        }

        if (activity.selectedDebtor != null)
            txtCusName.setText(activity.selectedDebtor.getFDEBTOR_NAME());

        /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        btnCust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new AsyncTask<Void, Void, Void>() {

                    protected void onPreExecute() {
                        String s = "Retrieving customers...";
                        SpannableString ss2 = new SpannableString(s);
                        ss2.setSpan(new RelativeSizeSpan(1.5f), 0, ss2.length(), 0);
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(ss2);
                        progressDialog.show();
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        customerList = new DebtorDS(getActivity()).getOutstandingCustomersForReceipt();
                        customerAdapter = new CustomerAdapter(getActivity(), customerList);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        progressDialog.dismiss();
                        lvCustomers.setAdapter(customerAdapter);

                    }
                }.execute();


            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchDialogBox();

            }
        });

        /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        lvCustomers.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view2, final int position, long id) {

				/* Check whether automatic date time option checked or not */
                final int i = android.provider.Settings.Global.getInt(getActivity().getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0);
                MainActivity activity = (MainActivity) getActivity();
                /* If option is selected */
                if(activity.selectedDebtor != null)
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Do you want to change the customer ?");
                    alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialogBuilder.setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity activity = (MainActivity) getActivity();
                            String RefNo = null;
                            RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.RecNumVal));
                            new FDDbNoteDS(getActivity()).ClearFddbNoteData();
                            new RecHedDS(getActivity()).CancelReceiptS(RefNo);

                            activity.cusPosition = 0;
                            activity.selectedDebtor = null;
                            activity.selectedRecHed = null;
                            UtilityContainer.ClearReceiptSharedPref(getActivity());

                            if (i > 0)
                            {
                                debtor = customerList.get(position);
                                activity.selectedDebtor = debtor;
                                activity.isCustomerChanged = 1;

                                mSharedPref.setGlobalVal("ReckeyCustomer", "1");
                                mSharedPref.setGlobalVal("ReckeyCusCode", debtor.getFDEBTOR_CODE());
                                txtCusName.setText(activity.selectedDebtor.getFDEBTOR_NAME());
                                lvCustomers.setAdapter(null);
                                listener.moveNextFragmentRece();

					            /* if not selected */
                            }
                            else
                            {
                                android.widget.Toast.makeText(getActivity(), "Please tick the 'Automatic Date and Time' option to continue..", android.widget.Toast.LENGTH_LONG).show();
                                /* Show Date/time settings dialog */
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                            }

                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertD = alertDialogBuilder.create();
                    alertD.show();
                }
                else
                {
                    if (i > 0)
                    {
                        debtor = customerList.get(position);
                        activity.selectedDebtor = debtor;
                        mSharedPref.setGlobalVal("ReckeyCustomer", "1");
                        mSharedPref.setGlobalVal("ReckeyCusCode", debtor.getFDEBTOR_CODE());
                        txtCusName.setText(activity.selectedDebtor.getFDEBTOR_NAME());
                        lvCustomers.setAdapter(null);
                        listener.moveNextFragmentRece();

                        /* if not selected */
                    }
                    else
                    {
                        android.widget.Toast.makeText(getActivity(), "Please tick the 'Automatic Date and Time' option to continue..", android.widget.Toast.LENGTH_LONG).show();
                        /* Show Date/time settings dialog */
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                    }
                }
            }
        });
        return view;
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (IResponseListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onButtonPressed");
        }
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        for (int i = 0; i < menu.size(); ++i) {
            menu.removeItem(menu.getItem(i).getItemId());
        }

        inflater.inflate(R.menu.frag_customer_phone_menu, menu);
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_customer_search_phone).getActionView();
//        searchView.setOnQueryTextListener(new OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                lvCustomers = (ListView) view.findViewById(R.id.cus_lv);
//                lvCustomers.clearTextFilter();
//                dsDebtorDS = new DebtorDS(getActivity());
//                customerList = dsDebtorDS.getCustomerByCodeAndNameForReceipt(newText);
//                lvCustomers.setAdapter(new CustomerAdapter(getActivity(), customerList));
//                return false;
//            }
//        });
//
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void mSearchDialogBox() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.input_dialogbox, null);
        final EditText enteredText = (EditText) promptView.findViewById(R.id.txtTextbox);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        alertDialogBuilder.setCancelable(false).setPositiveButton("Search", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                lvCustomers.setAdapter(null);
                customerList = new DebtorDS(getActivity()).getOutstandingCustomersForReceipt(enteredText.getText().toString());
                lvCustomers.setAdapter(new CustomerAdapter(getActivity(), customerList));


            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }
}
