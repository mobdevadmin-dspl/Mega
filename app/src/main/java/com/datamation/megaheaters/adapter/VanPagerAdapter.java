package com.datamation.megaheaters.adapter;

/**
 * Created by Himas on 5/25/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.datamation.megaheaters.model.FInvRHed;
import com.datamation.megaheaters.model.InvHed;
import com.datamation.megaheaters.view.vansale.InnerReturnDetails;
import com.datamation.megaheaters.view.vansale.VanSaleCustomer;
import com.datamation.megaheaters.view.vansale.VanSalesHeader;
import com.datamation.megaheaters.view.vansale.VanSalesOrderDetails;
import com.datamation.megaheaters.view.vansale.VanSalesSummary;

public class VanPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs = 0;
    private InvHed tmpinvHed;
    private FInvRHed tmpinvRHed;


    public VanPagerAdapter(FragmentManager fm, int NumOfTabs,InvHed tmpinvHed) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.tmpinvHed=tmpinvHed;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                VanSaleCustomer frag1 = new VanSaleCustomer();
                Bundle bundle = new Bundle();
                bundle.putSerializable("order",tmpinvHed);
                frag1.setArguments(bundle);
                return frag1;
            case 1:
                VanSalesHeader frag2 = new VanSalesHeader();
                return frag2;
            case 2:
                VanSalesOrderDetails frag3 = new VanSalesOrderDetails();
                Bundle bundleF = new Bundle();
                bundleF.putSerializable("order",tmpinvHed);
                frag3.setArguments(bundleF);
                return frag3;
            case 3:
                InnerReturnDetails frag4 = new InnerReturnDetails();
                Bundle bundleR = new Bundle();
                bundleR.putSerializable("return",tmpinvRHed);
                frag4.setArguments(bundleR);
                return frag4;
            case 4:
                VanSalesSummary frag5 = new VanSalesSummary();
                return frag5;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}