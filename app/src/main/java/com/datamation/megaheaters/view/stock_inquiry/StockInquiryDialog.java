package com.datamation.megaheaters.view.stock_inquiry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.StockInquiryAdaptor;
import com.datamation.megaheaters.control.ListExpandHelper;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.data.ControlDS;
import com.datamation.megaheaters.data.ItemsDS;
import com.datamation.megaheaters.data.TourHedDS;
import com.datamation.megaheaters.model.Control;
import com.datamation.megaheaters.model.StockInfo;
import com.datamation.megaheaters.model.TourHed;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Himas on 6/20/2017.
 */

public class StockInquiryDialog {

    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    ListView lvStockData;
    String LocCode = "";
    ArrayList<StockInfo> arrayList;
    Spinner spnTrans, spnTourcode;
    TextView txtTotQty;
    String PRINTER_MAC_ID;
    BluetoothAdapter mBTAdapter;
    BluetoothSocket mBTSocket;
    Dialog dialogProgress;
    String printString = "";
    ArrayList<TourHed> tourList;
    SearchView searchView;
    Context context;
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            try {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getAddress().equalsIgnoreCase(PRINTER_MAC_ID)) {
                        mBTAdapter.cancelDiscovery();
                        dialogProgress.dismiss();
                        printBillToDevice(PRINTER_MAC_ID, printString);
                    }
                }
            } catch (Exception e) {
                Log.e("Class  ", "fire 1 ", e);

            }
        }
    };

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public StockInquiryDialog(final Context context) {
        this.context = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.sales_mangement_stock_inquiry, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Stock Inquiry");
        alertDialogBuilder.setView(view);

        lvStockData = (ListView) view.findViewById(R.id.listviewStockData);
        localSP = context.getSharedPreferences(SETTINGS, 0);
        PRINTER_MAC_ID = new SharedPref(context).getGlobalVal("printer_mac_address").toString();

        spnTrans = (Spinner) view.findViewById(R.id.spnTrans);
        spnTourcode = (Spinner) view.findViewById(R.id.spnTourCode);
        txtTotQty = (TextView) view.findViewById(R.id.txtTotQty);
        searchView = (SearchView) view.findViewById(R.id.stock_search);
        new TaskRunner().execute("");
        ArrayList<String> strList = new ArrayList<>();
        strList.add("- Select a Transaction -");
        strList.add("Pre Sales");
       // strList.add("Van Sales");

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, strList);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTrans.setAdapter(dataAdapter1);

		/*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        spnTrans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new TaskRunner().execute("");
//                if (position > 0) {
//                    tourList = new TourHedDS(context).getTourDetails(position == 1 ? "S" : "V");
//                    loadTourCodes(tourList);
//                } else {
//                    lvStockData.setAdapter(null);
//                    spnTourcode.setSelection(0);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

		/*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        spnTourcode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    searchView.setVisibility(View.VISIBLE);

                    if (!LocCode.equals(tourList.get(position - 1).getTOURHED_LOCCODE())) {
                        txtTotQty.setText(new ItemsDS(context).getTotalStockQOH(tourList.get(position - 1).getTOURHED_LOCCODE()));
                        new TaskRunner().execute();
                    }
                    LocCode = tourList.get(position - 1).getTOURHED_LOCCODE();
                } else {
                    lvStockData.setAdapter(null);
                    LocCode = "";
                    txtTotQty.setText("");
                    searchView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchView.getWidth() >= 0)
                    new TaskRunner().execute(newText);
                return false;
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TaskRunner().execute("");
            }
        });


        alertDialogBuilder.setCancelable(false).setPositiveButton("Print", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mPrintStock();
            }
        });

        alertDialogBuilder.setCancelable(false).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
        ListExpandHelper.getListViewSize(lvStockData);

    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

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

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void doProgress(String s, int timeout) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(s);
        progressDialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, timeout);

    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mPrintStock() {

        setBluetooth(true);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Please confirm printing ?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                PrintStock();
                doProgress("Stock details being printed..!", 3000);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setBluetooth(false);
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void PrintStock() {

        ArrayList<Control> controlList;
        printString = "";
        int i = 0;
        String space;
        final int LINECHAR = 44;

        controlList = new ControlDS(context).getAllControl();

        int lengthDealABCom = (LINECHAR - controlList.get(0).getFCONTROL_COM_NAME().length()) / 2;
        space = String.format("%" + lengthDealABCom + "s", " ");
        String aa = space + controlList.get(0).getFCONTROL_COM_NAME() + "\r\n";

        int lengthDealBBCom = (LINECHAR - controlList.get(0).getFCONTROL_COM_ADD1().length()) / 2;
        space = String.format("%" + lengthDealBBCom + "s", " ");
        String bb = space + controlList.get(0).getFCONTROL_COM_ADD1() + "\r\n";

        String addressCCom = controlList.get(0).getFCONTROL_COM_ADD2().trim() + ", " + controlList.get(0).getFCONTROL_COM_ADD3().trim() + ".";
        int lengthDealCBCom = (LINECHAR - addressCCom.length()) / 2;
        space = String.format("%" + lengthDealCBCom + "s", " ");
        String cc = space + addressCCom + "\r\n";

        String TelCom = "Tel:" + controlList.get(0).getFCONTROL_COM_TEL1().trim();
        int lengthDealDBCom = (LINECHAR - TelCom.length()) / 2;
        space = String.format("%" + lengthDealDBCom + "s", " ");
        String dd = space + TelCom + "\r\n";

        String line = "--------------------------------------------" + "\r\n";
        // String line = String.format("%48s", "-") + "\r\n";

        String compAdd = aa + bb + cc + dd;

        // |<------44------------------------>|
        // ITEMCODE <-14-> ITEMNAME <-11-> QOH

        //String sGroup = "GROUP CODE: " + "" + " - " + new ItemsDS(context).getGroupByCode("");
        String sSaleType = "SALE MODE: " + spnTrans.getSelectedItem().toString();
        //String sTourCode = spnTourcode.getSelectedItem().toString();

        printString += compAdd;
        printString += "\r\n" + sSaleType + "\r\n";
        printString += line + "ITEMCODE" + String.format("%28s", " ") +  "QOH" + "\r\n";
        printString += "ITEMNAME" + String.format("%11s", " ")+"\r\n";
        printString += line;

        for (StockInfo item : arrayList) {

            i++;
            int len = (i + ". ").length() + item.getStock_Itemcode().length();
            int itemNameLength = 0;
            String itemNameFull = item.getStock_Itemname();
            String itemName1 = null,itemName2 = null;
            itemNameLength = item.getStock_Itemname().length();
            if(itemNameLength > 43)
            {
                itemName1 = itemNameFull.substring(0,43);
                itemName2 = itemNameFull.substring(44,item.getStock_Itemname().length());
            }

            String space1 = String.format("%" + (36 - len) + "s", " ");
            String qoh = String.valueOf((int) Double.parseDouble(item.getStock_Qoh()));
            String Space2 = String.format("%" + (14 - qoh.length()) + "s", " ");

            if(itemNameLength > 43)
            {
                printString += i + ". " + item.getStock_Itemcode() + space1 + qoh + "\r\n";
                printString += itemName1 + Space2+"\r\n";
                printString += itemName2 + Space2+"\r\n\n";
            }
            else
            {
                printString += i + ". " + item.getStock_Itemcode() + space1 + qoh + "\r\n";
                printString += itemNameFull + Space2+"\r\n\n";
            }



        }

        space = String.format("%" + ((LINECHAR - "Developed by Datamation Systems.".length()) / 2) + "s", " ");
        printString += line + space + "Developed by Datamation Systems.";
        Log.v("**", printString);

        DetectPrinter();

    }

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void DetectPrinter() {

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        try {
            if (mBTAdapter.isDiscovering())
                mBTAdapter.cancelDiscovery();
            else
                mBTAdapter.startDiscovery();
        } catch (Exception e) {
            Log.e("Class ", "fire 4", e);
        }
        System.out.println("BT Searching status :" + mBTAdapter.isDiscovering());

        if (mBTAdapter == null) {
            android.widget.Toast.makeText(context, "Device has no bluetooth capability...", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBTAdapter.isEnabled()) {
            }
            printBillToDevice(PRINTER_MAC_ID, printString);
        }

    }

	/*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void printBillToDevice(final String address, String BILL) {

        mBTAdapter.cancelDiscovery();
        try {
            BluetoothDevice mdevice = mBTAdapter.getRemoteDevice(address);
            Method m = mdevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            mBTSocket = (BluetoothSocket) m.invoke(mdevice, 1);

            mBTSocket.connect();
            OutputStream os = mBTSocket.getOutputStream();
            os.flush();
            os.write(BILL.getBytes());
            System.out.println(BILL);

            if (mBTAdapter != null)
                mBTAdapter.cancelDiscovery();

        } catch (Exception e) {
            Log.e("Class ", "fire 2 ", e);
            android.widget.Toast.makeText(context, "Printer Device Disable Or Invalid MAC.Please Enable the Printer or MAC Address.", android.widget.Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }

    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void loadTourCodes(ArrayList<TourHed> list) {

        ArrayList<String> strList = new ArrayList<String>();
        strList.add("- Select a Tour -");

        for (TourHed hed : list) {
            strList.add(hed.getTOURHED_REFNO() + " - " + hed.getTOURHED_ID());
        }

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, strList);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTourcode.setAdapter(dataAdapter2);

    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    class TaskRunner extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        StockInquiryAdaptor adaptor;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait while loading ...");
            progressDialog.show();
            lvStockData.clearTextFilter();
            lvStockData.setAdapter(null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            if (params.length > 0)
                arrayList = new ItemsDS(context).getStocks(params[0], LocCode);
            else
                arrayList = new ItemsDS(context).getStocks("", LocCode);

            adaptor = new StockInquiryAdaptor(context, arrayList);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            lvStockData.setAdapter(adaptor);
            progressDialog.dismiss();
            super.onPostExecute(result);
        }
    }
}
