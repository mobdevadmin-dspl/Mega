package com.datamation.megaheaters.view.presale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.AvailbelFreeIsuueSchemaAdapter;
import com.datamation.megaheaters.adapter.CustomerDebtAdapter;
import com.datamation.megaheaters.adapter.ExpandableListAdapter;
import com.datamation.megaheaters.adapter.MustSalesAdapter;
import com.datamation.megaheaters.adapter.PresalesPagerAdapter;
import com.datamation.megaheaters.control.ConnectionDetector;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.data.ControlDS;
import com.datamation.megaheaters.data.FDDbNoteDS;
import com.datamation.megaheaters.data.FOrdHedSummaryDS;
import com.datamation.megaheaters.data.FreeHedDS;
import com.datamation.megaheaters.data.ItemsDS;
import com.datamation.megaheaters.data.TranSODetDS;
import com.datamation.megaheaters.data.fOrdDetSummary;
import com.datamation.megaheaters.data.fOrdDetSummaryDS;
import com.datamation.megaheaters.data.fOrdhedSummary;
import com.datamation.megaheaters.model.FDDbNote;
import com.datamation.megaheaters.model.FreeHed;
import com.datamation.megaheaters.model.FreeIssue;
import com.datamation.megaheaters.model.Items;
import com.datamation.megaheaters.model.TranSODet;
import com.datamation.megaheaters.model.TranSOHed;
import com.datamation.megaheaters.view.stock_inquiry.StockInquiryDialog;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.LayoutParams;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

@SuppressLint("NewApi")
public class PreSales extends Fragment {
    View view;
    TabLayout tabLayout;
    Activity activity;
    FloatingActionMenu fam;
    FloatingActionButton fab;
    ViewPager viewPager;
    PresalesPagerAdapter adapter;
    boolean status;
    private ControlDS controlDS;
    private int ImageViewStatus;
    TranSOHed soHedTmp=null;
    private  SweetAlertDialog pDialog;
    private   String sp_url;
    public static SharedPreferences localSP;
    public static final String SETTINGS = "SETTINGS";
    ArrayList<fOrdhedSummary>fOrdhedSummaryArrayList;
    ArrayList<fOrdDetSummary>ordDetSummaryArrayList;
    private ExpandableListAdapter expandableListAdapter;
    private String debCode="";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sales_management_pre_sales, container, false);
        setHasOptionsMenu(true);
        activity = getActivity();
        controlDS = new ControlDS(getActivity());
        ImageViewStatus = controlDS.getCurrentStatus();
        localSP = getActivity().getSharedPreferences(SETTINGS, 0);
        sp_url= localSP.getString("URL", "").toString();



        Bundle mBundle = getArguments();

        if (mBundle != null) {
            status = mBundle.getBoolean("Active");
            soHedTmp= (TranSOHed) mBundle.getSerializable("order");

          if(soHedTmp!=null)
            Log.d("<>********hh********","" +soHedTmp.getFTRANSOHED_REFNO());

        }

        //if status =1 show image Details Tab bar  # dhanushika#
        if (ImageViewStatus == 1) {
            tabLayout = (TabLayout) view.findViewById(R.id.tabs);
            tabLayout.addTab(tabLayout.newTab().setText("Customer"));  //0
            tabLayout.addTab(tabLayout.newTab().setText("Header"));  //1
            tabLayout.addTab(tabLayout.newTab().setText("Image Details"));//2
            tabLayout.addTab(tabLayout.newTab().setText("Details")); //3
            tabLayout.addTab(tabLayout.newTab().setText("Summary"));  //4

        } else {     //if status =0 hide image Details Tab bar
            tabLayout = (TabLayout) view.findViewById(R.id.tabs);
            tabLayout.addTab(tabLayout.newTab().setText("Customer"));  //0
            tabLayout.addTab(tabLayout.newTab().setText("Header"));  //1
            tabLayout.addTab(tabLayout.newTab().setText("Details")); //2
            tabLayout.addTab(tabLayout.newTab().setText("Summary"));  //3
        }


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                               @Override
                                               public void onTabSelected(TabLayout.Tab tab) {
                                                   viewPager.setCurrentItem(tab.getPosition());
                                               }

                                               @Override
                                               public void onTabUnselected(TabLayout.Tab tab) {
                                               }

                                               @Override
                                               public void onTabReselected(TabLayout.Tab tab) {
                                               }
                                           }

        );

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        //if status =1 show image Details Tab bar # dhanushika#
        if (ImageViewStatus == 1) {
            adapter = new PresalesPagerAdapter(getChildFragmentManager(), 5, ImageViewStatus,soHedTmp);
        } else {
            adapter = new PresalesPagerAdapter(getChildFragmentManager(), 4, ImageViewStatus,soHedTmp);
        }
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                tabLayout.getTabAt(position).select();
                //   Log.d("<======>","click position "+ position);

                //if status =1 show image Details Tab bar
                if (ImageViewStatus == 1) {
                    if (position == 4)
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("TAG_SUMMARY"));
                    else if (position == 1)
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("TAG_HEADER"));
                    else if (position == 2)
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("TAG_ImG_DETAILS"));
                    else if (position == 3)
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("TAG_DETAILS"));
                } else {         //if status =0 hide image Details Tab bar
                    if (position == 3)
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("TAG_SUMMARY"));
                    else if (position == 1)
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("TAG_HEADER"));
                    else if (position == 2)
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("TAG_DETAILS"));

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        SubActionButton.Builder builder = new SubActionButton.Builder(activity);

        ImageView icon_1 = new ImageView(getActivity());
        icon_1.setImageResource(R.drawable.float_1);

        ImageView icon_2 = new ImageView(getActivity());
        icon_2.setImageResource(R.drawable.float_2);

        ImageView icon_3 = new ImageView(getActivity());
        icon_3.setImageResource(R.drawable.float_3);

        ImageView icon_4 = new ImageView(getActivity());
        icon_4.setImageResource(R.drawable.float_4);

        ImageView icon_5 = new ImageView(getActivity());
        icon_5.setImageResource(R.drawable.float_5);

        ImageView icon_6 = new ImageView(getActivity());
        icon_6.setImageResource(R.drawable.float_6);

        SubActionButton must_sales_btn = builder.setContentView(icon_1).build();
        SubActionButton cus_outstranding_btn = builder.setContentView(icon_2).build();
        SubActionButton stock_inquiry_btn = builder.setContentView(icon_3).build();
        SubActionButton button_4 = builder.setContentView(icon_4).build();
        SubActionButton lastBillDetail_btn = builder.setContentView(icon_5).build();
        SubActionButton btn_free_issue = builder.setContentView(icon_6).build();

        LayoutParams params = new LayoutParams(70, 70);

        must_sales_btn.setLayoutParams(params);
        cus_outstranding_btn.setLayoutParams(params);
        stock_inquiry_btn.setLayoutParams(params);
        button_4.setLayoutParams(params);
        lastBillDetail_btn.setLayoutParams(params);
        btn_free_issue.setLayoutParams(params);

        fam = new FloatingActionMenu.Builder(activity)
                .setStartAngle(-180)
                .setEndAngle(0)
                .setRadius(200)
                .addSubActionView(stock_inquiry_btn)
                .addSubActionView(cus_outstranding_btn)
                .addSubActionView(must_sales_btn)
                .addSubActionView(button_4)
                .addSubActionView(lastBillDetail_btn)
                .addSubActionView(btn_free_issue)
                .attachTo(fab)
                .build();

        stock_inquiry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new StockInquiryDialog(activity);
                fam.close(true);
            }
        });

        cus_outstranding_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debCode= new SharedPref(getActivity()).getGlobalVal("PrekeyCusCode");
                if (debCode.equals(""))
                    mCustomerOutstandingDialog("");
                else
                    mCustomerOutstandingDialog(debCode);
                fam.close(true);
            }
        });

        must_sales_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //#dhanushika#
                showMustSalesDailog();
                //Toast.makeText(getActivity(), "Delete Clicked", Toast.LENGTH_SHORT).show();
                fam.close(true);
            }
        });

        button_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Delete Clicked", Toast.LENGTH_SHORT).show();
                fam.close(true);
            }
        });

        lastBillDetail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean connectionStatus = new ConnectionDetector(getActivity()).isConnectingToInternet();
                if (connectionStatus == true) {
                    //showLastThreeBillPresales();
                   new FOrdHedLastBil().execute();
                } else {
                    Toast.makeText(getActivity(), "can't get data", Toast.LENGTH_LONG).show();
                }
             ;
                fam.close(true);
            }
        });

        btn_free_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPreSalesFreeIsueSchema();
               // Toast.makeText(getActivity(), "Delete Clicked", Toast.LENGTH_SHORT).show();
                fam.close(true);
            }
        });

        fam.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fam.close(true);
                    }
                }, 2000);
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
            }
        });

        return view;
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mMoveToHeader() {
        viewPager.setCurrentItem(1);
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fam.close(true);
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mCustomerOutstandingDialog(String debCode) {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.customer_debtor, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Customer Outstanding " + (debCode.equals("") ? debCode : "(" + debCode + ")"));
        alertDialogBuilder.setView(promptView);

        final ListView listView = (ListView) promptView.findViewById(R.id.lvCusDebt);
        ArrayList<FDDbNote> list = new FDDbNoteDS(getActivity()).getDebtInfo(debCode);
        listView.setAdapter(new CustomerDebtAdapter(getActivity(), list));

        alertDialogBuilder.setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onStart() {
        super.onStart();
        if (status)
            viewPager.setCurrentItem(2);

    }

    //---------------------------------------Must Sales----------------dhanushika-----------------------------------------------
    public void showMustSalesDailog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.must_sales_product_daliog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Must Sales ");
        alertDialogBuilder.setView(promptView);
        final ListView listView = (ListView) promptView.findViewById(R.id.lv_mustSales_list);
        ArrayList<Items> itemsArrayList = new ItemsDS(getActivity()).getItemsForMustSales();
        listView.setAdapter(new MustSalesAdapter(getActivity(), itemsArrayList));
        alertDialogBuilder.setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

//---------------------------------------------------------------------------------------------------------
    public void ShowPreSalesFreeIsueSchema(){
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.availbale_free_issue_product_daliog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Free Issue Schema: ");
        alertDialogBuilder.setView(promptView);
        final ListView listView = (ListView) promptView.findViewById(R.id.lv_available_list);

        String  RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.NumVal));
        FreeIssue issue = new FreeIssue(getActivity());
        FreeHed freeHed=new FreeHed() ;
        FreeHedDS freeHedDS = new FreeHedDS(getActivity());
        ArrayList<FreeHed>freeHedArrayList=new ArrayList<FreeHed>();
        ArrayList<TranSODet> dets = new TranSODetDS(getActivity()).getSAForFreeIssueCalc(RefNo);

        for(TranSODet soDet:dets){
            freeHed= freeHedDS.getFreeIssueItemSchema(soDet.getFTRANSODET_ITEMCODE());

            if(freeHed.getFFREEHED_REFNO()!=null) {
                freeHedArrayList.add(freeHed);
            }
        }

        listView.setAdapter(new AvailbelFreeIsuueSchemaAdapter(getActivity(), freeHedArrayList));
        alertDialogBuilder.setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
    //---------------------------------------------------------------------------------------------------------------------------------


    public void showLastThreeBillPresales(){
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.dailog_last_bill, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Pre Sales Summary ");
        alertDialogBuilder.setView(promptView);
        final ExpandableListView listView = (ExpandableListView) promptView.findViewById(R.id.vanSales_history_listview);


        debCode= new SharedPref(getActivity()).getGlobalVal("PrekeyCusCode");
       final ArrayList<fOrdhedSummary> ordhedSummaryArrayList=new FOrdHedSummaryDS(getActivity()).getPreSalseSummary(debCode);

           // create the adapter by passing your ArrayList data


            expandableListAdapter = new ExpandableListAdapter(getActivity(), ordhedSummaryArrayList);
            // attach the adapter to the expandable list view
            listView.setAdapter(expandableListAdapter);
           expandableListAdapter.setData(ordhedSummaryArrayList);



        listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getActivity(),
                        ordhedSummaryArrayList.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();
                expandableListAdapter.setData(ordhedSummaryArrayList);

            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getActivity(),
                        ordhedSummaryArrayList.get(groupPosition)
                                + " -> "
                                +  ordhedSummaryArrayList.get(groupPosition).getlistChildData().get(childPosition), Toast.LENGTH_SHORT
                ).show();


                return false;
            }
        });






        alertDialogBuilder.setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }
    ///----------------------------get From the server--------------------------------------------------------------------------------------------

    public class  FOrdHedLastBil extends AsyncTask<Object, Object, ArrayList<fOrdhedSummary>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Fetching order hed data from the server...");
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected ArrayList<fOrdhedSummary> doInBackground(Object... objects) {

            String URL = "http://" + sp_url;
            debCode= new SharedPref(getActivity()).getGlobalVal("PrekeyCusCode");
            try {
               URL json = new URL(URL + getActivity().getResources().getString(R.string.ConnectionURL) + "/FOrdHedLastBill/mobile123/" + localSP.getString("Console_DB", "").toString() + "/"+debCode);
                URLConnection jc  = json.openConnection();


                BufferedReader readerfdblist = new BufferedReader(new InputStreamReader(jc.getInputStream()));
                String line = readerfdblist.readLine();
                JSONObject jsonResponse = new JSONObject(line);
                JSONArray jsonArray = jsonResponse.getJSONArray("FOrdHedLastBillResult");
                fOrdhedSummaryArrayList = new ArrayList<>();
                fOrdhedSummaryArrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    fOrdhedSummary ordhedSummary = new fOrdhedSummary();
                    ordhedSummary.setRefNo(jsonObject.getString("RefNo"));
                    ordhedSummary.setDebCode(jsonObject.getString("DebCode"));
                    ordhedSummary.setTotalAmt(jsonObject.getString("TotalAmt"));
                    ordhedSummary.setTotalDis(jsonObject.getString("TotalDis"));
                    ordhedSummary.setTxnDate(jsonObject.getString("TxnDate"));
                    fOrdhedSummaryArrayList.add(ordhedSummary);
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return fOrdhedSummaryArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<fOrdhedSummary> fOrdhedSummary) {
            super.onPostExecute(fOrdhedSummary);
            if (pDialog.isShowing())
            pDialog.dismiss();

            if (new FOrdHedSummaryDS(getActivity()).CreateOrUpdateHedData(fOrdhedSummary) > 0) ;
                new FOrdDetLastBill().execute();
            Log.v("createOrUpdate", "Result :  Data Inserted successfully");
        }
//------------------------------------------------get Last three bill infromation from sever------------------------------------------------------
    }
        public class FOrdDetLastBill extends AsyncTask<Object, Object, ArrayList<fOrdDetSummary>> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Fetching order details from the server...");
                pDialog.setCancelable(false);
                pDialog.show();
            }


            @Override
            protected ArrayList<fOrdDetSummary> doInBackground(Object... objects) {

                String URL = "http://" + sp_url;
                debCode= new SharedPref(getActivity()).getGlobalVal("PrekeyCusCode");
                try {
                    URL json = new URL(URL + getActivity().getResources().getString(R.string.ConnectionURL) + "/FOrdDetLastBil/mobile123/" + localSP.getString("Console_DB", "").toString() + "/"+debCode);
                    URLConnection jc = json.openConnection();


                    BufferedReader readerfdblist = new BufferedReader(new InputStreamReader(jc.getInputStream()));
                    String line = readerfdblist.readLine();
                    JSONObject jsonResponse = new JSONObject(line);
                    JSONArray jsonArray = jsonResponse.getJSONArray("FOrdDetLastBilResult");
                    ordDetSummaryArrayList = new ArrayList<>();
                    ordDetSummaryArrayList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        fOrdDetSummary ordDetSummary=new fOrdDetSummary();
                        ordDetSummary.setAmt(jsonObject.getString("Amt"));
                        ordDetSummary.setItemCode(jsonObject.getString("ItemCode").trim());
                        ordDetSummary.setQty(jsonObject.getString("Qty"));
                        ordDetSummary.setRefNo(jsonObject.getString("RefNo"));
                        ordDetSummary.setSeqNo(jsonObject.getString("SeqNo"));
                        ordDetSummary.setTaxAmt(jsonObject.getString("TaxAmt"));
                        ordDetSummary.setTaxComCode(jsonObject.getString("TaxComCode").trim());
                        ordDetSummary.setTxnDate(jsonObject.getString("TxnDate"));
                        ordDetSummary.setType(jsonObject.getString("type"));
                        ordDetSummaryArrayList.add(ordDetSummary);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return ordDetSummaryArrayList;
            }

            @Override
            protected void onPostExecute(ArrayList<fOrdDetSummary> ordDetSummaryArrayList) {
                super.onPostExecute(ordDetSummaryArrayList);
                if (pDialog.isShowing())
                    pDialog.dismiss();
             if(ordDetSummaryArrayList!=null){
                 if (new fOrdDetSummaryDS(getActivity()).CreateOrUpdateDetData(ordDetSummaryArrayList) > 0) ;

                 showLastThreeBillPresales();

                 Log.v("createOrUpdate", "Result :  Data Inserted successfully");
             }

            }
        }


}
