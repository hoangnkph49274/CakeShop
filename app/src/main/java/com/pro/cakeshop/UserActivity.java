package com.pro.cakeshop;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pro.cakeshop.Adapter.ViewPagerAdapter;
import com.pro.cakeshop.Adapter.ViewUserPagerAdapter;
import com.pro.cakeshop.Fragment.OrderFragment;

public class UserActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        viewPager2 = findViewById(R.id.viewpager_2);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Khởi tạo Adapter và gán cho ViewPager2
        ViewUserPagerAdapter adapter = new ViewUserPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        // Lắng nghe sự kiện khi chuyển tab bằng ViewPager
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });

        if (getIntent().getBooleanExtra("show_order_tab", false)) {
            viewPager2.setCurrentItem(1); // Thay 1 bằng vị trí của OrderFragment trong ViewPager2
        }

        // Lắng nghe sự kiện khi nhấn vào BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                viewPager2.setCurrentItem(0);
            } else if (itemId == R.id.nav_cart) {
                viewPager2.setCurrentItem(1);
            } else if (itemId == R.id.nav_history) {
                viewPager2.setCurrentItem(2);
            } else if (itemId == R.id.nav_settings) {
                viewPager2.setCurrentItem(3);
            } else {
                return false;
            }

            return true;
        });
    }
}
