package com.datamation.megaheaters.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.MonthlySalesSummAdapter;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.data.DashboardDS;
import com.datamation.megaheaters.model.MonthSale;
import com.datamation.megaheaters.model.TargetVsAchivement;

import java.util.ArrayList;

/**
 * Created by Sathiyaraja on 2/8/2018.
 */

public class DashboardSummery extends Fragment {

    View rootview;
    private Toolbar toolbar;
    public static String  selectedMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.dashboard_summery, container, false);
        Toolbar toolbar = (Toolbar) rootview.findViewById(R.id.titleBar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.dm_logo_64);

//        toolbar = (Toolbar) rootview.findViewById(R.id.titleBar);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            toolbar.setNavigationIcon(R.drawable.ic_apps);
//            toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_settings));
//            toolbar.inflateMenu(R.menu.dashboard_menu);
//        }
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UtilityContainer.mLoadFragment(new IconPallet_mega(), getActivity());
//            }
//        });

        ArrayList<MonthSale> monthSalesList = new ArrayList<>();
        ArrayList<TargetVsAchivement> vsAchivements = new ArrayList<>();
        monthSalesList.clear();
        vsAchivements.clear();
        DashboardDS dashboardDS = new DashboardDS(getActivity());
        final ArrayList<MonthSale> monthSalesDb = dashboardDS.GetmonthSales();


//        for (int i = 0; i < monthSalesDb.size(); i++) {
//
//            MonthSale monthSale = new MonthSale();
//            monthSale.setMonth((i + 1) + "Month");
//            monthSale.setSalesAmount(1543.0f);
//
//            TargetVsAchivement achivement = new TargetVsAchivement();
//            achivement.setTarget(50000.0f);
//            achivement.setAchivment(32000.0f);
//            vsAchivements.add(achivement);
//
//            monthSale.setTargetVsAchivements(vsAchivements);
//            monthSalesList.add(monthSale);
//        }
        ListView list = (ListView) rootview.findViewById(R.id.monthlySalesSummery);

        MonthlySalesSummAdapter monthlySalesSummAdapter = new MonthlySalesSummAdapter(getActivity(), monthSalesDb);
        list.setAdapter(monthlySalesSummAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MonthSale monthSale =  monthSalesDb.get(position);
                selectedMonth = monthSale.getMonth();

                UtilityContainer.mLoadFragment(new DashboardSummerySub(), getActivity());
            }
        });
        setHasOptionsMenu(true);
        return rootview;
    }
 /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        for (int i = 0; i < menu.size(); ++i) {
            menu.removeItem(menu.getItem(i).getItemId());
        }
        inflater.inflate(R.menu.mnu_close, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.close:
                UtilityContainer.mLoadFragment(new IconPallet_mega(), getActivity());
                //return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

}
