package com.ui.waterserviceplatform;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private FragmentGeneral general;
    private FragmentPhoto photo;
    private FragmentLocation location;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkNumber();
        setupTabLayout();

    }

    /**
     * Setup the tabLayout
     */
    private void setupTabLayout(){
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to adapter one by one
        adapter.addFragment(general = new FragmentGeneral(), "General");
        adapter.addFragment(photo = new FragmentPhoto(), "Photo");
        adapter.addFragment(location = new FragmentLocation(), "Location");
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white)
        );
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    FragmentGeneral fragment = (FragmentGeneral) adapter.getItem(tab.getPosition());
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
                general.loseFocus();
            }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
            }
        });
    }

    /**
     * Check if the phone number has been set. If not, launch the confirmation activity
     */
    private void checkNumber(){
        SharedPreferences sharedPrefs = getSharedPreferences("data", MODE_PRIVATE);
        if(!sharedPrefs.contains("phoneNumber")){
            Intent intent = new Intent(getApplicationContext(), PhoneActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Set current tab
     * @param tab tab position
     */
    public void switchTab(int tab){
        viewPager.setCurrentItem(tab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.done:
                sendData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Send the data if no field is missing
     */
    public void sendData(){
        if(checkData()){
            SharedPreferences pref = getApplication().getSharedPreferences("data", MODE_PRIVATE);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getText(R.string.send_confirm))
                    .setTitle(R.string.send_as + pref.getString("phone", ""));
            builder.setPositiveButton(getText(R.string.confirm), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),
                            getText(R.string.report_sent),
                            Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
            });
            builder.setNegativeButton(getText(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    /**
     * Check if some required data are missing
     * @return true if required data are missing
     */
    public boolean checkData(){
        boolean emptyIntensity = general.getIntensity()==0;
        boolean emptyLocation = location.getCurrentMarker() == null;
        if(emptyIntensity){
            switchTab(0);
            getTabTextView(0).setTextColor(getResources().getColor(R.color.red));
            ((TextView)general.getView().findViewById(R.id.seekbarText))
                    .setTextColor(getResources().getColor(R.color.red));
        }
        if(emptyLocation){
            if(!emptyIntensity) {
                switchTab(2);
            }
            getTabTextView(2).setTextColor(getResources().getColor(R.color.red));
        }
        if(emptyIntensity || emptyLocation){
            Toast.makeText(getApplicationContext(),
                    getText(R.string.missing_data),
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    /**
     * Reset the color of the tab at the given position
     * @param pos given position
     */
    public void resetTabColor(int pos){
        if(pos==0){
            ((TextView)general.getView().findViewById(R.id.seekbarText))
                    .setTextColor(getResources().getColor(R.color.defaultGrey));
        }
        getTabTextView(pos).setTextColor(getResources().getColor(R.color.white));
    }

    /**
     * Get the textView of the tab at the given position
     * @param pos given position
     * @return textView of the tab at the given position
     */
    private TextView getTabTextView(int pos){
        LinearLayout ll = (LinearLayout)((LinearLayout)tabLayout.getChildAt(0)).getChildAt(pos);
        return (TextView) ll.getChildAt(1);
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {
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
    }
}
