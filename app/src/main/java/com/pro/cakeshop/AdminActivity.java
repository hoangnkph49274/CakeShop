package com.pro.cakeshop;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pro.cakeshop.Adapter.ViewPagerAdapter;
import com.pro.cakeshop.R;

public class AdminActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        viewPager2 = findViewById(R.id.viewpager_2);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Khởi tạo Adapter và gán cho ViewPager2
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        // Lắng nghe sự kiện khi chuyển tab bằng ViewPager
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });

        // Lắng nghe sự kiện khi nhấn vào BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_category) {
                viewPager2.setCurrentItem(0);
            } else if (itemId == R.id.nav_product) {
                viewPager2.setCurrentItem(1);
            } else if (itemId == R.id.nav_order) {
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
