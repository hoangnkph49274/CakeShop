package com.pro.cakeshop.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.pro.cakeshop.Fragment.Admin.AdminCategoryFragment;
import com.pro.cakeshop.Fragment.Admin.AdminOrderFragment;
import com.pro.cakeshop.Fragment.Admin.AdminProductFragment;
import com.pro.cakeshop.Fragment.Admin.AdminSettingFragment;


public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AdminCategoryFragment();
            case 1:
                return new AdminProductFragment();
            case 2:
                return new AdminOrderFragment();
            case 3:
                return new AdminSettingFragment();
            default:
                return new AdminCategoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Có 4 mục trong BottomNavigationView
    }
}
