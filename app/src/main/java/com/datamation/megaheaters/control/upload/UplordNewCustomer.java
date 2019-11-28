package com.datamation.megaheaters.control.upload;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.control.TaskType;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.data.NewCustomerDS;
import com.datamation.megaheaters.model.NewCustomer;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dhanushika on 4/9/2018.
 */

public class UplordNewCustomer extends AsyncTask<ArrayList<NewCustomer>, Integer, ArrayList<NewCustomer>>  {
    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    Context context;
    ProgressDialog dialog;
    UploadTaskListener taskListener;
    TaskType taskType;
    int totalRecords;


    public UplordNewCustomer(Context context, UploadTaskListener taskListener, TaskType taskType) {

        this.context = context;
        this.taskListener = taskListener;
        this.taskType = taskType;

        localSP = context.getSharedPreferences(SETTINGS, 0);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.show();
    }



    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dialog.setMessage("Uploading.. New Customer Record " + values[0] + "/" + totalRecords);
    }

    @Override
    protected ArrayList<NewCustomer> doInBackground(ArrayList<NewCustomer>[] params) {
        int recordCount = 0;
        publishProgress(recordCount);

        ArrayList<NewCustomer> RCSList = params[0];

        totalRecords = RCSList.size();

        final String sp_url = localSP.getString("URL", "").toString();
        String URL = "http://" + sp_url;
        for(NewCustomer newCustomer:RCSList){
            try {
                String sURL = URL + context.getResources().getString(R.string.ConnectionURL) + "/insertDebtor";
                Log.d("inserNewCustomer","<><><" + sURL);
                List<String> List = new ArrayList<String>();
                String sJsonHed = new Gson().toJson(newCustomer);
                List.add(sJsonHed);
                    /* Log.v("## Json1 ##",  List.toString());*/
                boolean bStatus = UtilityContainer.mHttpManager(sURL,List.toString());

                if (bStatus)
                    newCustomer.setIS_SYNCED(true);
                else
                    newCustomer.setIS_SYNCED(false);

            } catch (Exception e) {
                e.printStackTrace();
            }

            ++recordCount;
            publishProgress(recordCount);
        }


        return RCSList;
    }

    @Override
    protected void onPostExecute(ArrayList<NewCustomer> newCustomers) {
        super.onPostExecute(newCustomers);

        List<String> list = new ArrayList<>();

        if (newCustomers.size() > 0) {
            list.add("NEW CUSTOMER SUMMARY\n");
            list.add("------------------------------------\n");
        }

        int i = 1;
        for (NewCustomer c : newCustomers) {
            new NewCustomerDS(context).updateIsSynced(c);

            if (c.isIS_SYNCED()) {
                list.add(i + ". " + c.getCUSTOMER_ID() + " --> Success\n");
            } else {
                list.add(i + ". " + c.getCUSTOMER_ID() + " --> Failed\n");
            }
            i++;
        }


        dialog.dismiss();
        taskListener.onTaskCompleted(taskType, new ArrayList<String>());
    }
}
