package com.tcs.innovations.mlcp.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tcs.innovations.mlcp.R;
import com.tcs.innovations.mlcp.fragments.ReservedSlotsFragment;
import com.tcs.innovations.mlcp.fragments.VehiclesTaggedFragment;
import com.tcs.innovations.mlcp.fragments.VehiclesUntaggedFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhi on 2/20/2016.
 */

public class LevelDetailsActivity extends AppCompatActivity {
    public static String slotNameTwoLetter;
    private static String levelId;
    private static String levelName;
    ActionBar actionBar;
    private ViewPager viewPager;
    private TextView textView1;


    public static String getLevelId() {
        return levelId;
    }

    public static String getLevelName() {
        return levelName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Level Details");
        setSupportActionBar(toolbar);
        // actionBar = getSupportActionBar();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView textView = (TextView) findViewById(R.id.header1);
        textView1 = (TextView) findViewById(R.id.header2);


        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            levelName = bundle.getString("Level");
            levelId = bundle.getString("LevelId");

            String freeSlots = bundle.getString("freeslots");
            String s = bundle.getString("oneslotname");

            slotNameTwoLetter = s.substring(0, 2);
            textView.setText("Level : " + levelName);
            textView1.setText("Free Slots :" + freeSlots);
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(LevelDetailsActivity.this, R.color.colorAccent));
        String[] titles = new String[]{
                "Vehicles Tagged",
                "Vehicles Untagged",
                "Reserved Slots"
        };

        for (int i = 0; i < titles.length; i++) {
            tabLayout.getTabAt(i).setText(titles[i]);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int currentPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter) viewPager.getAdapter();
                Fragment f = viewPagerAdapter.getItem(position);
                f.onResume();
                Fragment c = viewPagerAdapter.getItem(currentPosition);
                c.onPause();
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

        private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new VehiclesTaggedFragment(), "");
        adapter.addFragment(new VehiclesUntaggedFragment(), "");
        adapter.addFragment(new ReservedSlotsFragment(), "");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }


}
