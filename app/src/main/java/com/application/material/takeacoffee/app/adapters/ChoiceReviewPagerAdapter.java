package com.application.material.takeacoffee.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * A simple pager adapter that represents 5 {@link com.application.material.takeacoffee.app.fragments} objects, in
 * sequence.
 */
public class ChoiceReviewPagerAdapter extends FragmentStatePagerAdapter {
    private String coffeeMachineId;
    public ChoiceReviewPagerAdapter(FragmentManager fm, String coffeeMachineId) {
        super(fm);
        this.coffeeMachineId = coffeeMachineId;
    }

    @Override
    public Fragment getItem(int position) {
//        return ReviewStatusChoiceFragment.create(position, coffeeMachineId);
        return null;
    }

    @Override
    public int getCount() {
//        return Common.NUM_PAGES;
        return 0;
    }
}
