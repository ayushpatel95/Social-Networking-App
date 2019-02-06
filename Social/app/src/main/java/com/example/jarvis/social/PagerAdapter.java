package com.example.jarvis.social;

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.jarvis.social.Friendsfragment;

/**
 * Created by jarvis on 17/11/17.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int tabscount;

    public PagerAdapter(FragmentManager fm, int tabscount) {
        super(fm);
        this.tabscount = tabscount;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position){
            case 0:
                Friendsfragment friendsfragment = new Friendsfragment();
                return friendsfragment;
            case 1:
                Addnewfriend addnewfriend = new Addnewfriend();
                return addnewfriend;
            case 2:
                RequestPending requestPending = new RequestPending();
                return requestPending;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabscount;
    }
}
