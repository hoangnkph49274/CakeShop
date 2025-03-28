package com.pro.cakeshop.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pro.cakeshop.Fragment.HistoryFragment;
import com.pro.cakeshop.Fragment.HomeFragment;
import com.pro.cakeshop.Fragment.OrderFragment;
import com.pro.cakeshop.Fragment.SettingFragment;


public class ViewUserPagerAdapter extends FragmentStateAdapter {

    public ViewUserPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new OrderFragment();
            case 2:
                return new HistoryFragment();
            case 3:
                return new SettingFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
