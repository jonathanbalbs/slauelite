package com.example.slauelite.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.slauelite.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.home_tab_1, R.string.home_tab_2, R.string.home_tab_3};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.

        Fragment fragment;

        switch (position) {
            case 0:
                fragment = new EventsFragment();
                break;
            case 1:
                fragment = new LibraryFragment();
                break;
            default:
                fragment = new NewsFragment();
                break;
        }


        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        CharSequence text = mContext.getResources().getString(TAB_TITLES[position]);

        return text;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}