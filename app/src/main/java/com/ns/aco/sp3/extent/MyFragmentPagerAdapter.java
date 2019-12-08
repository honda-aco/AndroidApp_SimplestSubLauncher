package com.ns.aco.sp3.extent;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ns.aco.sp3.view.FragmentPager;

/**
 * Created by ns on 2017/02/08.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    public MyFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FragmentPager.newInstance(position);
            case 1:
                return FragmentPager.newInstance(position);
            case 2:
                return FragmentPager.newInstance(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "ページ" + (position + 1);
    }
}
