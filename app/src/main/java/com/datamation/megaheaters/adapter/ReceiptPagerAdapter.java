package com.datamation.megaheaters.adapter;

/**
 * Created by Himas on 5/25/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.datamation.megaheaters.view.receipt.ReceiptCustomer;
import com.datamation.megaheaters.view.receipt.ReceiptHeader;
import com.datamation.megaheaters.view.receipt.ReceiptDetails;
import com.datamation.megaheaters.view.receipt.ReceiptSummary;

public class ReceiptPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs = 0;


    public ReceiptPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ReceiptCustomer frag1 = new ReceiptCustomer();
                return frag1;
            case 1:
                ReceiptHeader frag2 = new ReceiptHeader();
                return frag2;
            case 2:
                ReceiptDetails frag3 = new ReceiptDetails();
                return frag3;
            case 3:
                ReceiptSummary frag4 = new ReceiptSummary();
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