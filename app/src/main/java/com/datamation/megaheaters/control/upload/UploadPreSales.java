package com.datamation.megaheaters.control.upload;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.control.TaskType;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.data.DebtorDS;
import com.datamation.megaheaters.data.TranSOHedDS;
import com.datamation.megaheaters.model.mapper.PreSalesMapper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class UploadPreSales extends AsyncTask<ArrayList<PreSalesMapper>, Integer, ArrayList<PreSalesMapper>> {

    // Shared Preferences variables
    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    Context context;
    ProgressDialog dialog;
    UploadTaskListener taskListener;
    TaskType taskType;
    int totalRecords;

    public UploadPreSales(Context context, UploadTaskListener taskListener, TaskType taskType) {

        this.context = context;
        this.taskListener = taskListener;
        this.taskType = taskType;

        localSP = context.getSharedPreferences(SETTINGS, 0);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        // dialog.setTitle("Uploading records");
        dialog.show();
    }

    @Override
    protected ArrayList<PreSalesMapper> doInBackground(ArrayList<PreSalesMapper>... params) {

        int recordCount = 0;
        publishProgress(recordCount);

        ArrayList<PreSalesMapper> RCSList = params[0];
        totalRecords = RCSList.size();

        final String sp_url = localSP.getString("URL", "").toString();
        String URL = "http://" + sp_url;

        for (PreSalesMapper c : RCSList) {
//            try {
//                FileWriter writer = new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "test.txt");
//                writer.write(new Gson().toJson(c));
//                writer.close();
//            } catch (Exception e) {
//
//            }
            try {
                List<String> List = new ArrayList<String>();
                String sJsonHed = new Gson().toJson(c);
                List.add(sJsonHed);
                String sURL = URL + context.getResources().getString(R.string.ConnectionURL) + "/insertFOrdHed";
                boolean bStatus = UtilityContainer.mHttpManager(sURL, List.toString());
               // boolean bStatus = UtilityContainer.mHttpManager(sURL, new Gson().toJson(c));

                if (bStatus) {
                    c.setSynced(true);
                } else {
                    c.setSynced(false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            ++recordCount;
            publishProgress(recordCount);
        }
        return RCSList;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dialog.setMessage("Uploading.. PreSale Record " + values[0] + "/" + totalRecords);
    }

    @Override
    protected void onPostExecute(ArrayList<PreSalesMapper> RCSList) {

        super.onPostExecute(RCSList);
        List<String> list = new ArrayList<>();

        if (RCSList.size() > 0) {
            list.add("PRE SALES SUMMARY\n");
            list.add("------------------------------------\n");
        }

        int i = 1;
        for (PreSalesMapper c : RCSList) {
            new TranSOHedDS(context).updateIsSynced(c);

            if (c.isSynced()) {
                list.add(i + ". " + c.getFTRANSOHED_REFNO() + " (" + new DebtorDS(context).getCustNameByCode(c.getFTRANSOHED_DEBCODE()) + ")" + " --> Success\n");
            } else {
                list.add(i + ". " + c.getFTRANSOHED_REFNO() + " (" + new DebtorDS(context).getCustNameByCode(c.getFTRANSOHED_DEBCODE()) + ")" + " --> Failed\n");
            }
            i++;
        }

        dialog.dismiss();
        taskListener.onTaskCompleted(taskType, list);
    }

}
