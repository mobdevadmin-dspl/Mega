package com.datamation.megaheaters.control.upload;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.control.TaskType;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.data.TourDS;
import com.datamation.megaheaters.model.mapper.TourMapper;
import com.google.gson.Gson;

public class UploadTour extends AsyncTask<ArrayList<TourMapper>, Integer, ArrayList<TourMapper>> {

    // Shared Preferences variables
    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    Context context;
    ProgressDialog dialog;
    UploadTaskListener taskListener;
    TaskType taskType;
    int totalRecords;

    public UploadTour(Context context, UploadTaskListener taskListener, TaskType taskType) {

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
    protected ArrayList<TourMapper> doInBackground(ArrayList<TourMapper>... params) {

        int recordCount = 0;
        publishProgress(recordCount);

        ArrayList<TourMapper> RCSList = params[0];
        totalRecords = RCSList.size();
        final String sp_url = localSP.getString("URL", "").toString();
        String URL = "http://" + sp_url;

        for (TourMapper c : RCSList) {
            try {
                List<String> List = new ArrayList<String>();
                String sJsonHed = new Gson().toJson(c);
                List.add(sJsonHed);
                String sURL = URL + context.getResources().getString(R.string.ConnectionURL) + "/insertTourInfo";
                //boolean bStatus = UtilityContainer.mHttpManager(sURL, new Gson().toJson(c));
                boolean bStatus = UtilityContainer.mHttpManager(sURL, List.toString());

                if (bStatus)
                    c.setSynced(true);
                else
                    c.setSynced(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
//            try {
//                String sURL = URL + context.getResources().getString(R.string.ConnectionURL) + "/insertTourInfo";
//                boolean bStatus = UtilityContainer.mHttpManager(sURL, new Gson().toJson(c));
//
//                if (bStatus) {
//                    c.setSynced(true);
//                } else {
//                    c.setSynced(false);
//                }
//
//            } catch (Exception e) {
//
//                e.getStackTrace();
//            }

            ++recordCount;
            publishProgress(recordCount);
        }

        return RCSList;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dialog.setMessage("Uploading.. Tour record " + values[0] + "/" + totalRecords);
    }

    @Override
    protected void onPostExecute(ArrayList<TourMapper> RCSList) {

        super.onPostExecute(RCSList);
        List<String> list = new ArrayList<>();

        if (RCSList.size() > 0) {
            list.add("\nATTENDANCE SUMMARY");
            list.add("------------------------------------\n");
        }
        int i = 1;
        for (TourMapper c : RCSList) {
            new TourDS(context).updateIsSynced(c);

            if (c.isSynced()) {
                list.add(i + ". " + c.getFTOUR_ID() + " --> Success\n");
            } else {
                list.add(i + ". " + c.getFTOUR_ID() + " --> Failed\n");
            }
            i++;
        }

        dialog.dismiss();
        taskListener.onTaskCompleted(taskType, list);
    }
}