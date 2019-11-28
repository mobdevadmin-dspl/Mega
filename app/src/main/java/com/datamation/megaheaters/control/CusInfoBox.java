package com.datamation.megaheaters.control;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.datamation.megaheaters.data.RouteDS;
import com.datamation.megaheaters.model.Debtor;
import com.datamation.megaheaters.R;

public class CusInfoBox {

    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;

    public void debtorDetailsDialogbox(final Context context, String title, Debtor debtor) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View promptView = layoutInflater.inflate(R.layout.selected_debtor_details, null);
        localSP = context.getSharedPreferences(SETTINGS, 0);
        final EditText debCode = (EditText) promptView.findViewById(R.id.EditText1);
        final EditText debName = (EditText) promptView.findViewById(R.id.EditText01);
        final EditText debAddress = (EditText) promptView.findViewById(R.id.EditText02);
        final EditText debTele = (EditText) promptView.findViewById(R.id.EditText03);
        final EditText debMobile = (EditText) promptView.findViewById(R.id.EditText04);
        final EditText DebRoute = (EditText) promptView.findViewById(R.id.EditText05);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title.toUpperCase());

        alertDialogBuilder.setView(promptView);

        RouteDS routeDS = new RouteDS(context);

        debCode.setText(debtor.getFDEBTOR_CODE());
        debName.setText(debtor.getFDEBTOR_NAME());
        debAddress.setText(debtor.getFDEBTOR_ADD1() + " " + debtor.getFDEBTOR_ADD2() + " " + debtor.getFDEBTOR_ADD3());
        debTele.setText(debtor.getFDEBTOR_TELE());
        debMobile.setText(debtor.getFDEBTOR_MOB());
        DebRoute.setText(routeDS.getRouteNameByCode(debtor.getFDEBTOR_CODE()));

        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();


                            }
                        });

        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();
    }


}