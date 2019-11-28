package com.datamation.megaheaters.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;
import com.datamation.megaheaters.R;
import com.datamation.megaheaters.control.ConnectionDetector;
import com.datamation.megaheaters.control.TaskType;
import com.datamation.megaheaters.control.download.DownloadTaskListener;
import com.datamation.megaheaters.control.download.Downloader;
import com.datamation.megaheaters.data.DatabaseHelper;
import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.data.ServerDatabaseDS;
import com.datamation.megaheaters.data.SharedPreferencesClass;
import com.datamation.megaheaters.model.ServerDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;


public class SplashActivity extends Activity implements DownloadTaskListener {

    private static ProgressDialog progressDialog;
    private static String spURL = "";
    DatabaseHelper db;
    SharedPreferences localSP;
    Thread splashTread;
    private String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);
        TAG = getClass().getSimpleName();
        //forceCrash();
        db = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase YourDatabaseName;
        YourDatabaseName = db.getWritableDatabase();
        db.onUpgrade(YourDatabaseName, 1, 2);

        try {
            titleMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }

        localSP = getSharedPreferences(SharedPreferencesClass.SETTINGS, 0);
        StartAnimations();
    }
//    public void forceCrash() {
//        throw new RuntimeException("This is a crash");
//    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    public void titleMethod() throws Exception {

        TextView tvVersion = (TextView) findViewById(R.id.spl_version);
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        tvVersion.setText("Version " + pInfo.versionName);

    }

    private void dDatabaseListDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.ip_connection, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        alertDialogBuilder.setTitle("Please enter server URL");
        alertDialogBuilder.setView(promptView);

        final EditText input = (EditText) promptView.findViewById(R.id.et_ip);

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                boolean connectionStatus = new ConnectionDetector(SplashActivity.this).isConnectingToInternet();
                spURL = input.getText().toString().trim();
                String URL = "http://" + input.getText().toString().trim();

                if (Patterns.WEB_URL.matcher(URL).matches()) {

                    if (connectionStatus == true) {
                        String downLoadURL = getResources().getString(R.string.ConnectionURL) + "/GetdatabaseNames/mobile123";

                        new Downloader(SplashActivity.this, SplashActivity.this, TaskType.DATABASENAME, URL, downLoadURL).execute();

                    } else {

                        Toast.makeText(SplashActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                        reCallActivity();
                    }
                } else {

                    Toast.makeText(SplashActivity.this, "Invalid URL Entered. Please Enter Valid URL.", Toast.LENGTH_LONG).show();
                    reCallActivity();
                }

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                SplashActivity.this.finish();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    /**
     * after download completed.
     */
    @Override
    public void onTaskCompleted(TaskType taskType, String result) {
        switch (taskType) {
            case DATABASENAME:
                new PrefetchData().execute(result);
                break;

            default:
                break;
        }

    }

    public void reCallActivity() {
        Intent mainActivity = new Intent(SplashActivity.this, SplashActivity.class);
        startActivity(mainActivity);
    }

    private void StartAnimations() {

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout l = (RelativeLayout) findViewById(R.id.lnSplash);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.imageView1);
        iv.clearAnimation();
        iv.startAnimation(anim);
        //----

        new CountDownTimer(3500, 3500) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                if (!localSP.getString("URL", "").equals("")) {
                    Intent intent = null;

                    if (!new SalRepDS(getApplicationContext()).getCurrentRepCode().equals("") && localSP.getString("Sync_Status", "").toString().equals("Success")) {
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                        intent.putExtra("Synced", true);
                    } else {
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("Synced", false);
                    }

                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();

                } else {
                    dDatabaseListDialog();
                }

            }
        }.start();


    }

    private class PrefetchData extends AsyncTask<String, Integer, Boolean> {

        int totalRecords = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SplashActivity.this);
            progressDialog.setTitle("Prefetching data...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            try {
                int recordCount = 0;
                String jsonLine = arg0[0];

                ServerDatabaseDS ds = new ServerDatabaseDS(SplashActivity.this);
                ArrayList<ServerDatabase> list = new ArrayList<ServerDatabase>();
                JSONObject jsonResponse = new JSONObject(jsonLine);
                JSONArray jsonArray = jsonResponse.getJSONArray("GetdatabaseNamesResult");
                totalRecords = jsonArray.length();
                Log.v(TAG, "Array Length Server DB :" + jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {

                    ServerDatabase serverDatabase = new ServerDatabase();
                    JSONObject jObject = (JSONObject) jsonArray.get(i);
                    String sdbname = jObject.getString("Name");
                    Log.v(TAG, "DB Name:  " + sdbname);

                    serverDatabase.setDatabaseName(sdbname);
                    list.add(serverDatabase);

                    ++recordCount;
                    publishProgress(recordCount);
                }

                int db_result = ds.createOrUpdateServerDB(list);
                if (db_result > 0) {
                    return true;
                } else {
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressDialog.setMessage("Downloading meta data..." + progress[0] + "/" + totalRecords);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result) {

                Toast.makeText(getApplicationContext(), "Database list received", Toast.LENGTH_SHORT).show();
                SharedPreferencesClass.setLocalSharedPreference(SplashActivity.this, "URL", spURL);
                Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                mainActivity.putExtra("Synced", false);
                startActivity(mainActivity);

            } else {
                Toast.makeText(getApplicationContext(), "Database list failed", Toast.LENGTH_SHORT).show();
                reCallActivity();
            }

        }

    }
}
