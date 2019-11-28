package com.datamation.megaheaters.view.tour;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.data.TourDS;
import com.datamation.megaheaters.model.Tour;
import com.datamation.megaheaters.R;
import com.datamation.megaheaters.view.IconPallet_mega;
import com.datamation.megaheaters.view.MainActivity;
import com.datamation.megaheaters.control.UtilityContainer;

public class TourInfo extends Fragment implements OnClickListener {

    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    View view;
    Button itemSearch, btnStart, btnEnd;
    EditText txtTourDate, txtStartTime, txtVehicle, txtStartKm, txtEndKm,
            txtEndTime, txtRoute, txtDistance, txtDriver, txtAssist;
    LinearLayout linearEnd, linearStart;
    TextView lblMsg;
    Tour oTour;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sales_management_tour_info, container, false);

        btnStart = (Button) view.findViewById(R.id.btn_start);
        btnEnd = (Button) view.findViewById(R.id.btn_End);
        txtTourDate = (EditText) view.findViewById(R.id.et_tour_date);
        txtStartTime = (EditText) view.findViewById(R.id.et_start_time);
        txtVehicle = (EditText) view.findViewById(R.id.et_vehicle);
        txtStartKm = (EditText) view.findViewById(R.id.tv_startKm);
        txtDistance = (EditText) view.findViewById(R.id.txtDistance);
        txtDriver = (EditText) view.findViewById(R.id.et_driver);
        txtAssist = (EditText) view.findViewById(R.id.et_Assist);
        txtRoute = (EditText) view.findViewById(R.id.txtRoute);
        txtEndKm = (EditText) view.findViewById(R.id.txtEndKm);
        linearEnd = (LinearLayout) view.findViewById(R.id.lnFinish);
        linearStart = (LinearLayout) view.findViewById(R.id.lnStart);
        lblMsg = (TextView) view.findViewById(R.id.lblMessage);
        txtEndTime = (EditText) view.findViewById(R.id.txtEndTime);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("Tour Info");
        toolbar.setLogo(R.drawable.dm_logo_64);
        setHasOptionsMenu(true);

        localSP = getActivity().getSharedPreferences(SETTINGS, 0);
        oTour = new TourDS(getActivity()).getIncompleteRecord();

		/* No incomplete records */
        if (oTour == null) {

            if (new TourDS(getActivity()).hasTodayRecord()) {
                linearEnd.setVisibility(View.GONE);
                linearStart.setVisibility(View.GONE);
                lblMsg.setVisibility(View.VISIBLE);

            } else {
                linearEnd.setVisibility(View.INVISIBLE);
                btnStart.setEnabled(true);
                txtTourDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                txtStartTime.setText(new SimpleDateFormat("hh:mm a").format(new Date()));
            }

        } else {
            linearEnd.setVisibility(View.VISIBLE);
            btnStart.setEnabled(false);
            txtTourDate.setText(oTour.getFTOUR_DATE());
            txtStartTime.setText(oTour.getFTOUR_S_TIME().substring(11, oTour.getFTOUR_S_TIME().length()));
            txtVehicle.setText(oTour.getFTOUR_VEHICLE());
            txtVehicle.setEnabled(false);
            txtStartKm.setText(oTour.getFTOUR_S_KM());
            txtStartKm.setEnabled(false);
            txtRoute.setText(oTour.getFTOUR_ROUTE());
            txtRoute.setEnabled(false);
            txtDriver.setText(oTour.getFTOUR_DRIVER());
            txtDriver.setEnabled(false);
            txtAssist.setText(oTour.getFTOUR_ASSIST());
            txtAssist.setEnabled(false);
            txtEndTime.setText(new SimpleDateFormat("hh:mm a").format(new Date()));
        }

        btnEnd.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        txtEndKm.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtEndKm.getText().length() > 0)
                    txtDistance.setText(String.format("%.2f", Double.parseDouble(txtEndKm.getText().toString()) - Double.parseDouble(txtStartKm.getText().toString())));
            }
        });

        return view;

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_start:

                if (txtRoute.length() > 0 && txtStartKm.length() > 0 && txtVehicle.length() > 0 && txtDriver.length() > 0 && txtAssist.length() > 0) {

                    Tour tour = new Tour();
                    tour.setFTOUR_DATE(txtTourDate.getText().toString());
                    tour.setFTOUR_ROUTE(txtRoute.getText().toString());
                    tour.setFTOUR_S_KM(txtStartKm.getText().toString());
                    tour.setFTOUR_S_TIME(new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(new Date()));
                    tour.setFTOUR_VEHICLE(txtVehicle.getText().toString());
                    tour.setFTOUR_DRIVER(txtDriver.getText().toString());
                    tour.setFTOUR_ASSIST(txtAssist.getText().toString());

                    new TourDS(getActivity()).InsertUpdateTourData(tour);
                    clearTextFields();
                    Toast.makeText(getActivity(), "Tour start info saved! ", Toast.LENGTH_SHORT).show();
                    NavigateOff();

                } else
                    Toast.makeText(getActivity(), "Fill in the fields!", Toast.LENGTH_SHORT).show();

                break;

            case R.id.btn_End:

                if (txtEndKm.getText().toString().length() > 0) {
                    Tour tour = new Tour();
                    tour.setFTOUR_DATE(oTour.getFTOUR_DATE());
                    tour.setFTOUR_ROUTE(oTour.getFTOUR_ROUTE());
                    tour.setFTOUR_S_KM(oTour.getFTOUR_S_KM());
                    tour.setFTOUR_S_TIME(oTour.getFTOUR_S_TIME());
                    tour.setFTOUR_VEHICLE(oTour.getFTOUR_VEHICLE());
                    tour.setFTOUR_F_TIME(new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(new Date()));
                    tour.setFTOUR_F_KM(txtEndKm.getText().toString());
                    tour.setFTOUR_DISTANCE(txtDistance.getText().toString());
                    tour.setFTOUR_DRIVER(oTour.getFTOUR_DRIVER());
                    tour.setFTOUR_ASSIST(oTour.getFTOUR_ASSIST());

                    tour.setFTOUR_IS_SYNCED("0");
                    tour.setFTOUR_MAC(localSP.getString("MAC_Address", "No MAC Address").toString());
                    tour.setFTOUR_REPCODE(new SalRepDS(getActivity()).getCurrentRepCode());

                    new TourDS(getActivity()).InsertUpdateTourData(tour);
                    clearTextFields();
                    Toast.makeText(getActivity(), "Tour End info saved! ", Toast.LENGTH_SHORT).show();
                    NavigateOff();

                } else
                    Toast.makeText(getActivity(), "Fill in the fields!", Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void NavigateOff() {
        UtilityContainer.mLoadFragment(new IconPallet_mega(), getActivity());
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void clearTextFields() {

        txtTourDate.setText("");
        txtStartTime.setText("");
        txtVehicle.setText("");
        txtStartKm.setText("");
        txtRoute.setText("");
        txtEndTime.setText("");
        txtEndKm.setText("");
        txtDriver.setText("");
        txtAssist.setText("");
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        for (int i = 0; i < menu.size(); ++i) {
            menu.removeItem(menu.getItem(i).getItemId());
        }
        inflater.inflate(R.menu.mnu_close, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.close:
                UtilityContainer.mLoadFragment(new IconPallet_mega(), getActivity());
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
