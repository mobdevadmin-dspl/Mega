package com.datamation.megaheaters.adapter;

/**
 * Created by Himas on 5/25/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.datamation.megaheaters.view.sales_return.SalesReturnCustomer;
import com.datamation.megaheaters.view.sales_return.SalesReturnDetails;
import com.datamation.megaheaters.view.sales_return.SalesReturnHeader;
import com.datamation.megaheaters.view.sales_return.SalesReturnSummary;

public class ReturnPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs = 0;


    public ReturnPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                SalesReturnCustomer frag1 = new SalesReturnCustomer();
                return frag1;
            case 1:
                SalesReturnHeader frag2 = new SalesReturnHeader();
                return frag2;
            case 2:
                SalesReturnDetails frag3 = new SalesReturnDetails();
                return frag3;
            case 3:
                SalesReturnSummary frag4 = new SalesReturnSummary();
                return frag4;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}