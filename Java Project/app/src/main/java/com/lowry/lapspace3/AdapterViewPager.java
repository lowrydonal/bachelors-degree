package com.lowry.lapspace3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/*
 * CLASS: ADAPTER FOR TAB VIEW IN ADMIN PROFILE
 */

public class AdapterViewPager extends FragmentStatePagerAdapter {

    int tabCount;

    public AdapterViewPager(FragmentManager fragmentManager, int tabCount) {
        super(fragmentManager);
        this.tabCount = tabCount;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentListOpenCases listOpenCasesFragment = new FragmentListOpenCases();
                return listOpenCasesFragment;
            case 1:
                FragmentListActivatedCases listActivatedCasesFragment = new FragmentListActivatedCases();
                return listActivatedCasesFragment;
            case 2:
                FragmentListLiveCampaigns listLiveCampaignsFragment = new FragmentListLiveCampaigns();
                return listLiveCampaignsFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return tabCount;
    }
}