package com.tcs.innovations.mlcp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tcs.innovations.mlcp.R;
import com.tcs.innovations.mlcp.fragments.DefaulterVehicleListFragment;
import com.tcs.innovations.mlcp.fragments.LevelListFragment;
import com.tcs.innovations.mlcp.fragments.LiveDataFragment;
import com.tcs.innovations.mlcp.utilities.HttpManager;
import com.tcs.innovations.mlcp.utilities.UrlBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static String TotalFreeSlots;
    private ViewPager viewPager;
    private Timer timer;
    private TimerTaskHelper timerTaskHelper;
    private TextView messageText1;
    private TextView messageText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messageText1 = (TextView) findViewById(R.id.header1);
        messageText2 = (TextView) findViewById(R.id.header2);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);


        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
        String[] titles = new String[]{
                "Live Data",
                "Levels",
                "Defaulter Vehicles"
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

        getData();
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LiveDataFragment(), "");
        adapter.addFragment(new LevelListFragment(), "");
        adapter.addFragment(new DefaulterVehicleListFragment(), "");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void getData() {
        String url1 = UrlBean.getUrl() + "remaining_slots.php";
        HttpManager httpManager1 = new HttpManager(MainActivity.this, new HttpManager.ServiceResponse() {
            @Override
            public void onServiceResponse(String serviceResponse) {
                if (HttpManager.getStatusCode() == 200 && serviceResponse != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(serviceResponse);
                        String totalslots = jsonObject.getString("availableslots");
                        messageText2.setText("Total Free Slots :" + totalslots);
                        TotalFreeSlots = totalslots;
                        Log.d("Free slots", totalslots);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //Toast.makeText(MainActivity.this, "No internet connection, please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        });
        httpManager1.execute(url1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            overridePendingTransition(R.anim.anim_translate_down, R.anim.anim_translate_up);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_erroneous_parking) {
            Intent intent = new Intent(MainActivity.this, AddErroneousVehicleActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timerTaskHelper = new TimerTaskHelper();
        timer.schedule(timerTaskHelper, 600, 6000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
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

    private class TimerTaskHelper extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                String url = UrlBean.getUrl() + "get_next_slot.php";


                @Override
                public void run() {
                    HttpManager httpManager = new HttpManager(MainActivity.this, new HttpManager.ServiceResponse() {
                        @Override
                        public void onServiceResponse(String serviceResponse) {
                            if (HttpManager.getStatusCode() == 200 && serviceResponse != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(serviceResponse);
                                    String nextLevel = jsonObject.getString("floorname");
                                    if (nextLevel.contains("Parking full")) {
                                        messageText1.setText("Parking full.");
                                    } else {
                                        messageText1.setText("Next allocation level : " + nextLevel);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                //Toast.makeText(MainActivity.this, "No internet connection, please try again later.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    httpManager.execute(url);

                }
            });
        }

    }
}
