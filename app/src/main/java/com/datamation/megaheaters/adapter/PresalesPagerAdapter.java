package com.datamation.megaheaters.adapter;

/**
 * Created by Himas on 5/25/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.datamation.megaheaters.model.TranSOHed;
import com.datamation.megaheaters.view.presale.PreSaleImageOrder;
import com.datamation.megaheaters.view.presale.PreSalesCustomer;
import com.datamation.megaheaters.view.presale.PreSalesHeader;
import com.datamation.megaheaters.view.presale.PreSalesOrderNewDetails;
import com.datamation.megaheaters.view.presale.PreSalesSummary;

public class PresalesPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs = 0;
    private int ImageViewStatus; // # dhanushika#
    private TranSOHed soHed;


    public PresalesPagerAdapter(FragmentManager fm, int NumOfTabs, int ImageViewStatus,TranSOHed soHed) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.ImageViewStatus = ImageViewStatus;
        this.soHed =soHed;
    }

    @Override
    public Fragment getItem(int position) {
        if (ImageViewStatus == 1) {
            switch (position) {
                case 0:
                    PreSalesCustomer frag1 = new PreSalesCustomer();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order",soHed);
                    frag1.setArguments(bundle);

                    return frag1;
                case 1:
                    PreSalesHeader frag2 = new PreSalesHeader();
                    return frag2;
                case 2:
                    PreSaleImageOrder frag3 = new PreSaleImageOrder();
                    Bundle bundlef = new Bundle();
                    bundlef.putSerializable("order",soHed);
                    frag3.setArguments(bundlef);
                    return frag3;
                case 3:
                    PreSalesOrderNewDetails frag4 = new PreSalesOrderNewDetails();   //new  fragmnet added
                    Bundle bundledet = new Bundle();
                    bundledet.putSerializable("order",soHed);
                    frag4.setArguments(bundledet);
                    return frag4;
                case 4:
                    PreSalesSummary frag5 = new PreSalesSummary();
                    return frag5;

                default:
                    return null;
            }
        } else {   //hide ImageOrder tab
            switch (position) {
                case 0:
                    PreSalesCustomer frag1 = new PreSalesCustomer();
                    return frag1;
                case 1:
                    PreSalesHeader frag2 = new PreSalesHeader();
                    return frag2;
                case 2:
                    PreSalesOrderNewDetails frag4 = new PreSalesOrderNewDetails();  //new  fragmnet added
                    Bundle bundlef = new Bundle();
                    bundlef.putSerializable("order",soHed);
                    frag4.setArguments(bundlef);
                    return frag4;
                case 3:
                    PreSalesSummary frag5 = new PreSalesSummary();
                    return frag5;

                default:
                    return null;
            }
        }

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}