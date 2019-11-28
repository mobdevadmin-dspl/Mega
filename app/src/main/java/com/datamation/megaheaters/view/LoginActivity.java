package com.datamation.megaheaters.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.DetailedState;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.data.SharedPreferencesClass;
import com.datamation.megaheaters.model.SalRep;

import java.util.ArrayList;

public class LoginActivity extends Activity implements OnClickListener {

    EditText username, password;
    TextView txtver;
    int tap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login);

        username = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);
        Button login = (Button) findViewById(R.id.btnlogin);
        txtver = (TextView) findViewById(R.id.textVer);
        txtver.setText("Version " + getVersionCode());

        login.setOnClickListener(this);

        txtver.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tap += 1;
                StartTimer(3000);
                if (tap >= 7) {
                    showCredits(3000);
                }
            }
        });

    }

    public String getVersionCode() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return "0";

    }

    public void StartTimer(int timeout) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tap = 0;
            }
        }, timeout);

    }

    public void showCredits(int timeout) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Designed & Developed by Rashmi");
        alertDialogBuilder.setTitle("FINAC SFA");
        alertDialogBuilder.setIcon(R.drawable.developer);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.cancel();
            }
        }, timeout);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnlogin: {
//			try {

//				if (getInternetStatus()) {

//					String ver = new GetVersionCode(this).execute().get();
//
//					if (ver != null) {
//						double newVersion = Double.parseDouble(ver);
//						double currentVersion = Double.parseDouble(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
//
//						if (newVersion > currentVersion) {
//							Toast.makeText(this, "New version available.\nPlease update before continue...", Toast.LENGTH_LONG).show();
//						} else {
//							StartApp();
//						}
//					} else
//						StartApp();

//				} else
                StartApp();

//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} catch (ExecutionException e) {
//				e.printStackTrace();
//			} catch (NameNotFoundException e) {
//				e.printStackTrace();
//			}

            }
            break;

            default:
                break;
        }
    }

    private void StartApp() {
        SharedPreferencesClass.setLocalSharedPreference(getApplicationContext(), "first_login", "Success");
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
//--
    private void LoginValidation() {
        // TODO Auto-generated method stub
        SalRepDS ds = new SalRepDS(getApplicationContext());
        ArrayList<SalRep> list = ds.getSaleRepDetails();
        for (SalRep salRep : list) {

            if (salRep.getREPCODE().equals(username.getText().toString().toUpperCase()) && salRep.getNAME().equals(password.getText().toString().toUpperCase())) {

                StartApp();

            } else {
                Toast.makeText(getApplicationContext(), "Invalid username or password.", Toast.LENGTH_LONG).show();

            }
        }
    }

    public boolean getInternetStatus() {

        boolean conStatus = false;

        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() && wifi.getDetailedState() == DetailedState.CONNECTED) {
            conStatus = true;
        } else if (mobile.isAvailable() && mobile.getDetailedState() == DetailedState.CONNECTED) {
            conStatus = true;
        } else {
            conStatus = false;
        }

        return conStatus;

    }

}
