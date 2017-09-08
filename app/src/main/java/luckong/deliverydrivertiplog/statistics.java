package luckong.deliverydrivertiplog;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static android.R.attr.entries;
import static java.security.AccessController.getContext;
import static luckong.deliverydrivertiplog.R.layout.fragment_graphs;


public class statistics extends AppCompatActivity implements ActionBar.TabListener{


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private PagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private NonSwipeableViewPager mViewPager;

    static int pos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new FixedTabsPagerAdapter(getSupportFragmentManager());


        mViewPager = (NonSwipeableViewPager) findViewById(R.id.container);

        // Set up the ViewPager with the sections adapter.
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayout);



        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);



    }


    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

        if (tab.getPosition() == 0) {

            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
            Intent i = new Intent("TAG_REFRESH");
            lbm.sendBroadcast(i);

        }

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        DecimalFormat precision = new DecimalFormat("00"); //USED FOR DATE SAVING
        DecimalFormat moneyPrecision = new DecimalFormat("0.00");
        DecimalFormat deliveryPrecision = new DecimalFormat("0");
        int selectedMonth;

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }



        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

            switch (getArguments().getInt(ARG_SECTION_NUMBER))
            {
                case 1: { //Calendar of delivery history
                    rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
                    CalendarView calendar = (CalendarView)rootView.findViewById(R.id.deliveryHistory);
                    final DatabaseHelper myDB = new DatabaseHelper(getContext());
                    calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                        @Override
                        public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayofMonth) {
                            preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            editor = preferences.edit();

                            selectedMonth = month+1;

                            final String selectedDate = precision.format(selectedMonth)+"/"+precision.format(dayofMonth)+"/"+year;
                            editor.putString("date", selectedDate);
                            editor.putBoolean("currentDay", false);
                            editor.apply();

                            //Get day's results from database
                            Cursor res = myDB.getDeliveryDay(selectedDate);
                            if(res.getCount() == 0){
                                //show Message
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                myDB.insertData(selectedDate, 0, 0, 0, 0, 0, 0, selectedMonth, 0, 0, 0, 0);
                                                Intent intent = new Intent(getContext(), results.class);
                                                startActivity(intent);
                                                break;


                                            case DialogInterface.BUTTON_NEGATIVE:
                                                //No button clicked
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("No data found for " + selectedDate + ", create new data for this day?").setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();
                                return;
                            }

                            System.out.println("STATISTICS SENDING DATE: " + year+"-"+precision.format(selectedMonth)+"-"+precision.format(dayofMonth));

                            Intent intent = new Intent(getContext(), results.class);
                            startActivity(intent);



                        }
                    });
                    break;
                }
                case 2: {// Graphs of delivery performance
                    rootView = inflater.inflate(fragment_graphs, container, false);
                    DatabaseHelper myDB = new DatabaseHelper(getContext());


                    //Get day's results from database
                    final ArrayList<String> dates = myDB.getAllDates();
                    final ArrayList<Float> totalTipsArray = myDB.getAllTotalTips();
                    final ArrayList<Float> allNumOfDeliveries = myDB.getAllNumOfDeliveries();
                    final ArrayList<Float> walkAmountArray = myDB.getAllWalkAmount();


                    LineChart graph = (LineChart) rootView.findViewById(R.id.graph);
                    LineChart numOfDeliveriesGraph = (LineChart)rootView.findViewById(R.id.numOfDeliveriesGraph);
                    LineChart walkAmountGraph= (LineChart)rootView.findViewById(R.id.walkAmountGraph);



                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

                    if (totalTipsArray.size()>0) {
                        //TOTAL TIPS GRAPH
                        List<Entry> entries = new ArrayList<Entry>();

                        if(totalTipsArray.size()>6){
                            for(int i=totalTipsArray.size()-1;i>totalTipsArray.size()-7; i--){
                                entries.add(new Entry(i, totalTipsArray.get(i)));
                            }
                        }else{
                            int i=0;
                            while(i<totalTipsArray.size()){
                                entries.add(new Entry(i, totalTipsArray.get(i)));
                                i++;
                            }
                        }

                            LineDataSet dataSet = new LineDataSet(entries, null);
                            dataSet.setDrawValues(false);
                            dataSet.setLineWidth(3);
                            dataSet.setCircleRadius(5);
                            dataSet.setColor(Color.GREEN);
                            dataSet.setCircleColor(Color.GREEN);
                            LineData lineData = new LineData(dataSet);
                            lineData.setValueTextSize(10f);
                            XAxis xAxis = graph.getXAxis();
                            xAxis.setLabelRotationAngle(-45);

                            if(totalTipsArray.size()>1){
                                xAxis.setLabelCount(totalTipsArray.size()-1);
                            }else{
                                xAxis.setLabelCount(1);
                            }

                            xAxis.setValueFormatter(new IAxisValueFormatter() {

                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    if(totalTipsArray.size()>2){
                                        return dates.get((int) value);
                                    }else{

                                        //For graphs with only 1 data point
                                        if(totalTipsArray.size() == 1) {
                                            if (value == 0f) {
                                                return dates.get(0);
                                            } else {
                                                return "";
                                            }
                                        }else{
                                            //For graphs with only 2 data points
                                            if (value == 0f || value == 1f) {
                                                if(value == 0f){
                                                    return dates.get(0);
                                                }else{
                                                    return dates.get(1);
                                                }
                                            } else {
                                                return "";
                                            }
                                        }
                                    }

                                }

                                public int getDecimalDigits() {
                                    return 0;
                                }

                            });

                            graph.setVisibleXRange(1f, 7f);
                            graph.setData(lineData);
                            graph.notifyDataSetChanged();
                            Legend legend = graph.getLegend();
                            legend.setEnabled(false);
                            graph.getDescription().setEnabled(false);
                            graph.invalidate();
                            pos=0;
                        //}


                        //NUMOFDELIVERIES GRAPH
                        List<Entry> entries2 = new ArrayList<Entry>();
                            if(allNumOfDeliveries.size()>6){
                                for(int i=allNumOfDeliveries.size()-1;i>allNumOfDeliveries.size()-7; i--){
                                    entries2.add(new Entry(i, allNumOfDeliveries.get(i)));
                                }
                            }else{
                                int i=0;
                                while(i<allNumOfDeliveries.size()){
                                    entries2.add(new Entry(i, allNumOfDeliveries.get(i)));
                                    i++;
                                }
                            }
                            LineDataSet dataSet2 = new LineDataSet(entries2, null);
                            dataSet2.setLineWidth(3);
                            dataSet2.setColor(Color.BLUE);
                            dataSet2.setDrawValues(false);
                            dataSet2.setCircleRadius(5);
                            dataSet2.setCircleColor(Color.BLUE);
                            LineData lineData2 = new LineData(dataSet2);
                            lineData2.setValueTextSize(10f);

                            XAxis xAxis2 = numOfDeliveriesGraph.getXAxis();
                            xAxis2.setLabelRotationAngle(-45);
                            if(allNumOfDeliveries.size()>1){
                                xAxis2.setLabelCount(allNumOfDeliveries.size()-1);
                            }else{
                                xAxis2.setLabelCount(1);
                            }
                            xAxis2.setValueFormatter(new IAxisValueFormatter() {

                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {

                                    if(allNumOfDeliveries.size()>2){
                                        return dates.get((int) value);
                                    }else{

                                        //For graphs with only 1 data point
                                        if(allNumOfDeliveries.size() == 1) {
                                            if (value == 0f) {
                                                return dates.get(0);
                                            } else {
                                                return "";
                                            }
                                        }else{
                                            //For graphs with only 2 data points
                                            if (value == 0f || value == 1f) {
                                                if(value == 0f){
                                                    return dates.get(0);
                                                }else{
                                                    return dates.get(1);
                                                }
                                            } else {
                                                return "";
                                            }
                                        }
                                    }
                                }

                                public int getDecimalDigits() {
                                    return 0;
                                }

                            });
                            numOfDeliveriesGraph.setVisibleXRange(1f, 7f);
                            numOfDeliveriesGraph.setData(lineData2);
                            numOfDeliveriesGraph.notifyDataSetChanged();
                            Legend legend2 = numOfDeliveriesGraph.getLegend();
                            legend2.setEnabled(false);
                            numOfDeliveriesGraph.getDescription().setEnabled(false);
                            numOfDeliveriesGraph.invalidate();



                        //WALK AMOUNT GRAPH
                        List<Entry> entries3 = new ArrayList<Entry>();
                            if(walkAmountArray.size()>6){
                                for(int i=walkAmountArray.size()-1;i>walkAmountArray.size()-7; i--){
                                    entries3.add(new Entry(i, walkAmountArray.get(i)));
                                }
                            }else{
                                int i=0;
                                while(i<walkAmountArray.size()){
                                    entries3.add(new Entry(i, walkAmountArray.get(i)));
                                    i++;
                                }
                            }
                            LineDataSet dataSet3 = new LineDataSet(entries3, null);
                            dataSet3.setLineWidth(3);
                            dataSet3.setColor(Color.MAGENTA);
                            dataSet3.setDrawValues(false);
                            dataSet3.setCircleColor(Color.MAGENTA);
                            dataSet3.setCircleRadius(5);
                            LineData lineData3 = new LineData(dataSet3);
                            lineData3.setValueTextSize(10f);

                            XAxis xAxis3 = walkAmountGraph.getXAxis();
                            xAxis3.setLabelRotationAngle(-45);
                            if(walkAmountArray.size()>1){
                                xAxis3.setLabelCount(walkAmountArray.size()-1);
                            }else{
                                xAxis3.setLabelCount(1);
                            }

                            xAxis3.setValueFormatter(new IAxisValueFormatter() {

                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {

                                    if(walkAmountArray.size()>2){
                                        return dates.get((int) value);
                                    }else{

                                        //For graphs with only 1 data point
                                        if(walkAmountArray.size() == 1) {
                                            if (value == 0f) {
                                                return dates.get(0);
                                            } else {
                                                return "";
                                            }
                                        }else{
                                            //For graphs with only 2 data points
                                            if (value == 0f || value == 1f) {
                                                if(value == 0f){
                                                    return dates.get(0);
                                                }else{
                                                    return dates.get(1);
                                                }
                                            } else {
                                                return "";
                                            }
                                        }
                                    }
                                }

                                public int getDecimalDigits() {
                                    return 0;
                                }

                            });
                            walkAmountGraph.setVisibleXRange(1f, 7f);
                            walkAmountGraph.setData(lineData3);
                            walkAmountGraph.notifyDataSetChanged();
                            Legend legend3 = walkAmountGraph.getLegend();
                            legend3.setEnabled(false);
                            walkAmountGraph.getDescription().setEnabled(false);
                            walkAmountGraph.invalidate();

                    }


                    break;
                }

                case 3: {
                    rootView = inflater.inflate(R.layout.fragment_all_time_stats, container, false);

                    TextView allTimeTips = (TextView)rootView.findViewById(R.id.allTimeTips);
                    TextView percentageCreditText = (TextView)rootView.findViewById(R.id.percentCredit);
                    TextView allTimeMileageText = (TextView)rootView.findViewById(R.id.allTimeMileage);
                    TextView allTimeDeliveriesText = (TextView)rootView.findViewById(R.id.allTimeDeliveries);
                    TextView allTimeWalkText = (TextView)rootView.findViewById(R.id.allTimeWalk);
                    TextView allTimeAverageTipsText = (TextView)rootView.findViewById(R.id.allTimeAverageTips);
                    TextView mostNumberOfDeliveriesText = (TextView)rootView.findViewById(R.id.mostNumberOfDeliveries);
                    TextView mostNumberOfTipsText = (TextView)rootView.findViewById(R.id.mostNumberOfTips);
                    TextView totalValueOfPizzaText = (TextView)rootView.findViewById(R.id.totalValueOfPizza);
                    TextView highestTip = (TextView)rootView.findViewById(R.id.highestTip);

                    preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

                    DatabaseHelper myDB = new DatabaseHelper(getContext());

                    float[] allTimeStats = myDB.getAllTimeStats();

                    allTimeTips.setText(" $" + moneyPrecision.format(allTimeStats[1]));
                    if(Float.isNaN(allTimeStats[2])){
                        percentageCreditText.setText(" 0%");
                    }else{
                        percentageCreditText.setText(" " + deliveryPrecision.format(allTimeStats[2]) + "%");
                    }
                    allTimeMileageText.setText(" $" + moneyPrecision.format(allTimeStats[3]));
                    allTimeDeliveriesText.setText((deliveryPrecision.format(allTimeStats[4])) + " deliveries");
                    allTimeWalkText.setText("$"+moneyPrecision.format(allTimeStats[5]));
                    allTimeAverageTipsText.setText("$" + moneyPrecision.format(allTimeStats[6]) + " per day");
                    mostNumberOfDeliveriesText.setText(deliveryPrecision.format(allTimeStats[7]) + " deliveries");
                    mostNumberOfTipsText.setText("$"+moneyPrecision.format(allTimeStats[8]));
                    totalValueOfPizzaText.setText("$" + moneyPrecision.format(allTimeStats[0]) + " worth of Pizza");
                    highestTip.setText("$" + moneyPrecision.format(allTimeStats[9]));

                    break;
                }

            }
            return rootView;
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class FixedTabsPagerAdapter extends FragmentPagerAdapter {

        public FixedTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }



        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "HISTORY";
                case 1:
                    return "GRAPHS";
                case 2:
                    return "STATS";
            }
            return null;
        }
    }

}
