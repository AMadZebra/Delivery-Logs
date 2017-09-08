package luckong.deliverydrivertiplog;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

/*CHANGELOG:
    (SUNDAY AUG 13)
    -Added working getAllTimeStats function in databasehelper
    -Added totalCashPrice, totalCreditPrice, and totalTips columns to database
    -Cleaned up home.java to be much less dependent of sharedPreferences
    -Finished upgrading saving method for allTimeVariables in home.java and statistics.java

    GOAL FOR (MONDAY AUG 14)
    -get alltimestats to work on update
    -finish allTimeStats XML design

    (MON AUG 14 - AUG 15)
    -Confirmed that all time stats works on update
    -allTimeStats works on update
    -all AllTimeStats implemented in XML
    -fixed bug where current owe/tip/numofdeliveries wasn't showing on home page upon resume

*/
//TODO: refresh graphs on adding date
//TODO: Fix Bugs
//TODO: commit on github
//TODO: Publish app
//-------------------------------SHIPPABLE PRODUCT COMPLETED----------------------

public class home extends AppCompatActivity{

    ProgressDialog mProgressDialog;
    TextView textView;

    DatabaseHelper myDB;

    SharedPreferences.Editor editor;
    Button confirm;
    EditText priceEdit;
    EditText tipEdit;
    CheckBox priceCredit;
    CheckBox tipCredit;
    TextView currentTips;
    TextView numberOfDeliveries;
    TextView currentTotal;

    float currentTip;
    float currentPrice;
    float numOfDeliveries;
    SharedPreferences preferences;


    float todaystotalCashPrice;
    float todaystotalCashTip;
    float todaystotalCreditPrice;
    float todaystotalCreditTip;
    float todaysnumOfDeliveries;
    String date;

    float lastCashPrice;
    float lastCreditPrice;
    float lastCashTip;
    float lastCreditTip;
    float storeMileageRate;
    float startingBank;

    int month;
    Boolean overwriteDay;

    Stack<Float> todaysCashPriceHistory;
    Stack<Float> todaysCashTipHistory;
    Stack<Float> todaysCreditPriceHistory;
    Stack<Float> todaysCreditTipHistory;

    DecimalFormat precision = new DecimalFormat("0.00");
    DecimalFormat amountDelivery = new DecimalFormat("0");
    Calendar c = Calendar.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        //Get database
        myDB = new DatabaseHelper(this);

        //Button and edittext declarations
        confirm = (Button)findViewById(R.id.button);
        priceEdit = (EditText)findViewById(R.id.priceInput);
        tipEdit = (EditText)findViewById(R.id.tipInput);
        priceCredit = (CheckBox)findViewById(R.id.creditPrice);
        tipCredit = (CheckBox)findViewById(R.id.creditTip);
        currentTips = (TextView)findViewById(R.id.currentTips);
        numberOfDeliveries = (TextView)findViewById(R.id.numOfDeliveries);
        currentTotal = (TextView)findViewById(R.id.currentTotal);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        todaysCashPriceHistory = new Stack<Float>();
        todaysCashTipHistory = new Stack<Float>();
        todaysCreditPriceHistory = new Stack<Float>();
        todaysCreditTipHistory = new Stack<Float>();


/*
        //VARIABLE CLEARING
        editor.remove("allTimeMileage");
        editor.remove("allTimeCreditTips");
        editor.remove("allTimeCashTips");
        editor.remove("totalTipsAllTime");
        editor.remove("totalTipsALlTime");
        editor.clear();
        */



        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                date = preferences.getString("date", "No date");

                //getSavedDateVariables();

                System.out.println("DATE AT CONFRIM ON CLICK: " + date);

                //If a shift has already been saved today but user attempts to start another shift on the same day
                System.out.println("NKUMBER OF DELIVERIES IS NOW: " + todaysnumOfDeliveries + " AND: " + numOfDeliveries);
                if(todaysnumOfDeliveries < 1f && myDB.checkIfDayIsAlreadySaved(date)) {
                    //Prompt user to override today's saved shift
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked

                                    resetCurrentDayVariables();
                                    editor.putBoolean("overwriteDay", true);
                                    editor.apply();
                                    saveDelivery();

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(home.this);
                    builder.setMessage("Override Today's Saved Run?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                } else{
                    //No shift saved today, continue saving delivieries
                    saveDelivery();

                }
            }
        });
    }


    //Saves data of each delivery
    void saveDelivery(){
        currentPrice = 0;
        currentTip = 0;

        todaystotalCashPrice = preferences.getFloat("CashPrice", 0);
        todaystotalCashTip = preferences.getFloat("CashTip", 0);
        todaystotalCreditPrice = preferences.getFloat("CreditPrice", 0);
        todaystotalCreditTip = preferences.getFloat("CreditTip", 0);
        todaysnumOfDeliveries = preferences.getFloat("numOfDeliveries", 0);

        String currentPriceGet = priceEdit.getText().toString();
        String currentTipGet = tipEdit.getText().toString();

        //Treat empty price entry as 0s
        if (currentPriceGet.equals("")) {
            currentPrice = 0;
            todaysCashPriceHistory.push(0f);
            todaysCreditPriceHistory.push(0f);

        } else {
            currentPrice = Float.parseFloat(currentPriceGet);
        }

        //Treat empty tip entry as 0s
        if (currentTipGet.equals("")) {
            currentTip = 0;
            todaysCashTipHistory.push(0f);
            todaysCreditTipHistory.push(0f);

        } else {
            currentTip = Float.parseFloat(currentTipGet);
        }


        //Store price value as either credit or cash
        if (priceCredit.isChecked()) {
            todaystotalCreditPrice = todaystotalCreditPrice + currentPrice;
            //editor.putFloat(date + "CreditPrice", todaystotalCreditPrice);
            editor.putFloat("CreditPrice", todaystotalCreditPrice);

            todaysCreditPriceHistory.push(currentPrice);
            todaysCashPriceHistory.push(0f);
            editor.apply();

        } else {
            todaystotalCashPrice = todaystotalCashPrice + currentPrice;
            //editor.putFloat(date + "CashPrice", todaystotalCashPrice);
            editor.putFloat("CashPrice", todaystotalCashPrice);

            System.out.println("PUTTING TODAYSCASHPRICE: " + todaystotalCashPrice + " WITH CURRENTPRICE: "  + currentPrice);
            todaysCashPriceHistory.push(currentPrice);
            todaysCreditPriceHistory.push(0f);
            editor.apply();
        }


        if (tipCredit.isChecked()) {
            todaystotalCreditTip = todaystotalCreditTip + currentTip;
           // editor.putFloat(date + "CreditTip", todaystotalCreditTip);
            editor.putFloat("CreditTip", todaystotalCreditTip);

            System.out.println("TODAYS CREDITTIP IS NOW: " + todaystotalCreditTip);


            todaysCreditTipHistory.push(currentTip);
            todaysCashTipHistory.push(0f);
            editor.apply();
        } else {
            todaystotalCashTip = todaystotalCashTip + currentTip;
           // editor.putFloat(date + "CashTip", todaystotalCashTip);
            editor.putFloat("CashTip", todaystotalCashTip);

            todaysCashTipHistory.push(currentTip);
            todaysCreditTipHistory.push(0f);
            editor.apply();
        }

        //Set page as empty
        priceEdit.setText("");
        tipEdit.setText("");
        priceCredit.setChecked(false);
        tipCredit.setChecked(false);

        double currentTipDisplayAmount = todaystotalCreditTip + todaystotalCashTip;
        todaysnumOfDeliveries = todaysnumOfDeliveries + 1;
        float storeMileageRate = preferences.getFloat("storeMileage", (float)1.10);
        double currentOwe = todaystotalCashPrice + startingBank - todaystotalCreditTip - todaysnumOfDeliveries*storeMileageRate;
        double currentTotalDisplayAmount = currentTipDisplayAmount+todaysnumOfDeliveries*storeMileageRate;



        //editor.putFloat(date + "numOfDeliveries", todaysnumOfDeliveries);
        editor.putFloat("numOfDeliveries", todaysnumOfDeliveries);
        editor.apply();

        //update amount currently owed and current Tips
        currentTips.setText("$" + precision.format(currentTipDisplayAmount));
        currentTotal.setText("$" + precision.format(currentTotalDisplayAmount));

        //update # of deliveries
        numberOfDeliveries.setText(" " + amountDelivery.format(todaysnumOfDeliveries));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        date = preferences.getString("date", "No date");


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_results) {

            todaysnumOfDeliveries = preferences.getFloat("numOfDeliveries", 0);
            storeMileageRate = preferences.getFloat("storeMileage", (float)1.10);

            if(todaysnumOfDeliveries > 0) {

                float walkAmount = todaystotalCashTip + todaystotalCreditTip + todaysnumOfDeliveries * storeMileageRate;


                //Store amount owed for the day
                getCurrentDayVariables();
                float storeMileageRate = preferences.getFloat("storeMileage", (float) 1.10);
                float mileage = todaysnumOfDeliveries * storeMileageRate;
                float totalAmountOwed = todaystotalCashPrice - todaystotalCreditTip - mileage + startingBank;
                float totalTips = todaystotalCashTip + todaystotalCreditTip;

                editor.apply();

                System.out.println("TOTALAMOUNT SENDING: CASHPRICE: " + todaystotalCashPrice + " CREDITTIP: " + todaystotalCreditTip + " MILEAGE: " + mileage);

                //PLACE VARIABLES IN DATABASE
                //overwriteDay = preferences.getBoolean("overwriteDay", false);
                overwriteDay = myDB.checkIfDayIsAlreadySaved(date);
                if (overwriteDay) { //Data already found with this date, overwrite the data
                    float highestTip = getHighestTip();
                    myDB.updateDeliveryDay(date, todaystotalCashTip, todaystotalCreditTip,  mileage, todaysnumOfDeliveries, storeMileageRate, totalAmountOwed, walkAmount, totalTips, highestTip, todaystotalCashPrice, todaystotalCreditPrice);
                    editor.putBoolean("overwriteDay", false);
                    editor.apply();
                    //Toast.makeText(getApplicationContext(), "UDPATE CALLED, DATE=" + date, Toast.LENGTH_SHORT).show();
                } else { //Make new data for this date

                    float highestTip = getHighestTip();
                    myDB.insertData(date, totalAmountOwed, todaystotalCashTip, todaystotalCreditTip, mileage, todaysnumOfDeliveries, walkAmount, month, todaystotalCashPrice, todaystotalCreditPrice, totalTips, highestTip);
                    //Toast.makeText(getApplicationContext(), "INSERT CALLED", Toast.LENGTH_SHORT).show();
                }


                //Start results page
                Intent intent = new Intent(home.this, results.class);
                startActivity(intent);

                //reset current day variables as the day has ended
                editor.putFloat("CashPrice", 0);
                editor.putFloat("CashTip", 0);
                editor.putFloat("CreditPrice", 0);
                editor.putFloat("CreditTip", 0);
                editor.putFloat("numOfDeliveries", 0);

                editor.apply();
            }else{
                Toast.makeText(getApplicationContext(), "No deliveries made!", Toast.LENGTH_SHORT).show();
            }

        }

        if(id == R.id.action_deleteLastDelivery){
            currentTotal = (TextView)findViewById(R.id.currentTotal);
            currentTips = (TextView)findViewById(R.id.currentTips);
            numberOfDeliveries = (TextView)findViewById(R.id.numOfDeliveries);

            getCurrentDayVariables();


            if(todaysnumOfDeliveries > 0){

                lastCashPrice = todaysCashPriceHistory.pop();
                lastCreditTip = todaysCreditTipHistory.pop();
                lastCashTip = todaysCashTipHistory.pop();
                lastCreditPrice = todaysCreditPriceHistory.pop();


                float newCashPrice = todaystotalCashPrice-lastCashPrice;
                float newCashTip = todaystotalCashTip-lastCashTip;
                float newCreditTip = todaystotalCreditTip - lastCreditTip;
                float newCreditPrice = todaystotalCreditPrice-lastCreditPrice;

                System.out.println("TOTAL CREDIT PRICE " + lastCreditPrice + " TOTAL CREDIT TIP " + lastCreditTip + " TOTAL CASH PRICE " + lastCashPrice + " TOTAL CASH TIP " + lastCashTip);

                //Update data after deletion
                todaystotalCashPrice = newCashPrice;
                todaystotalCashTip = newCashTip;
                todaystotalCreditPrice = newCreditPrice;
                todaystotalCreditTip = newCreditTip;
                todaysnumOfDeliveries--;
                editor.putFloat("CashPrice", newCashPrice);
                editor.putFloat("CashTip", newCashTip);
                editor.putFloat("CreditPrice", newCreditPrice);
                editor.putFloat("CreditTip", newCreditTip);
                editor.putFloat("numOfDeliveries", todaysnumOfDeliveries);
                editor.apply();

                double currentTipDisplayAmount = newCreditTip + newCashTip;
                float storeMileageRate = preferences.getFloat("storeMileage", (float)1.10);
                double currentTotalDisplayAmount = currentTipDisplayAmount + storeMileageRate*todaysnumOfDeliveries;

                currentTotal.setText("$"+precision.format(currentTotalDisplayAmount));
                currentTips.setText("$"+precision.format(currentTipDisplayAmount));

                //update # of deliveries
                numberOfDeliveries.setText(" " + amountDelivery.format(todaysnumOfDeliveries));
                System.out.println("COMPLETE DELETE");

            }else{
                //Prompt user that there are no deliveries to delete
                Toast.makeText(getApplicationContext(),"No Deliveries to Delete!", Toast.LENGTH_SHORT).show();
            }


        }


        if(id == R.id.action_settings){

            Intent intent = new Intent(home.this, settings.class);
            startActivity(intent);
        }

        if(id == R.id.action_statistics){
            Intent intent = new Intent(home.this, statistics.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    float getHighestTip(){
        float creditTipTemp;
        float cashTipTemp;
        float highestTip;
        creditTipTemp = todaysCreditTipHistory.pop();
        cashTipTemp = todaysCashTipHistory.pop();

        if(creditTipTemp>cashTipTemp){
            highestTip = creditTipTemp;
        }else{
            highestTip = cashTipTemp;
        }

        while(!todaysCreditTipHistory.empty()){
            creditTipTemp = todaysCreditTipHistory.pop();
            cashTipTemp = todaysCashTipHistory.pop();

            if(creditTipTemp>highestTip){
                highestTip = creditTipTemp;
            }
            if(cashTipTemp>highestTip){
                highestTip = cashTipTemp;
            }
        }

        return highestTip;
    }

    void getCurrentDayVariables(){
        todaystotalCashPrice = preferences.getFloat("CashPrice", 0);
        todaystotalCashTip = preferences.getFloat("CashTip", 0);
        todaystotalCreditPrice = preferences.getFloat("CreditPrice", 0);
        todaystotalCreditTip = preferences.getFloat("CreditTip", 0);
        todaysnumOfDeliveries = preferences.getFloat("numOfDeliveries", 0);
    }

    void resetCurrentDayVariables(){

        //reset database for current day
        myDB.updateDeliveryDay(date, 0, 0, 0, 0, storeMileageRate, startingBank, 0, 0, 0, 0, 0);

        //Reset the current day's delivery data
        editor.putFloat("CashPrice", 0);
        editor.putFloat("CashTip", 0);
        editor.putFloat("CreditPrice",0);
        editor.putFloat("CreditTip", 0);
        editor.putFloat("numOfDeliveries",0);

        todaystotalCashPrice = 0;
        todaystotalCashTip = 0;
        todaystotalCreditPrice = 0;
        todaystotalCreditTip = 0;
        todaysnumOfDeliveries = 0;

        editor.apply();
    }

    @Override
    public void onResume(){
        super.onResume();

        //Get current Days delivery information
        todaystotalCashPrice = preferences.getFloat("CashPrice", 0);
        todaystotalCashTip = preferences.getFloat("CashTip", 0);
        todaystotalCreditPrice = preferences.getFloat("CreditPrice", 0);
        todaystotalCreditTip = preferences.getFloat("CreditTip", 0);
        todaysnumOfDeliveries = preferences.getFloat("numOfDeliveries", 0);
        startingBank = preferences.getFloat("startingBank", 15);


        if(todaysnumOfDeliveries < 1){
            month=c.get(Calendar.MONTH)+1;
            System.out.println("MONTH IS: " + month);

            date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
            editor.putString("date", date);

            //Toast.makeText(getApplicationContext(),"Putting date: " + date, Toast.LENGTH_SHORT).show();
            editor.apply();
        }else{
            editor.putBoolean("currentDay", true);
            editor.apply();
        }

        date = preferences.getString("date", "No date");

        //getSavedDateVariables();

        float i=1;
        while(i<=todaysnumOfDeliveries){
            todaysCashPriceHistory.push(preferences.getFloat(i+"CashHistory", 0));
            todaysCashTipHistory.push(preferences.getFloat(i+"CashTipHistory", 0));
            todaysCreditPriceHistory.push(preferences.getFloat(i+"CreditHistory", 0));
            todaysCreditTipHistory.push(preferences.getFloat(i+"CreditTipHistory", 0));
            editor.remove(i+"CashHistory");
            editor.remove(i+"CreditHistory");
            editor.remove(i+"CashTipHistory");
            editor.remove(i+"CreditTipHistory");
            editor.apply();
            i++;
        }
        float storeMileageRate = preferences.getFloat("storeMileage", (float)1.10);
        currentTip = todaystotalCashTip + todaystotalCreditTip;
        float currentOwe = todaystotalCashPrice + startingBank - todaystotalCreditTip - todaysnumOfDeliveries*storeMileageRate;
        float currentTipDisplayAmount = todaystotalCreditTip + todaystotalCashTip;
        float currentTotalDisplayAmount = currentTipDisplayAmount+storeMileageRate*todaysnumOfDeliveries;
        currentTotal.setText("$"+precision.format(currentTotalDisplayAmount));
        currentTips.setText("$" + precision.format(currentTipDisplayAmount));
        numberOfDeliveries.setText(" " + amountDelivery.format(todaysnumOfDeliveries));
    }

    @Override
    public void onStop(){
        super.onStop();

        todaysnumOfDeliveries = preferences.getFloat("numOfDeliveries", 0);

        //Save all stack data when app is closed
        while(!todaysCashTipHistory.empty()){
            editor.putFloat(todaysnumOfDeliveries+"CashTipHistory", todaysCashTipHistory.pop());
        }

        while(!todaysCashPriceHistory.empty()){
            editor.putFloat(todaysnumOfDeliveries+"CashHistory", todaysCashPriceHistory.pop());
        }

        while(!todaysCreditTipHistory.empty()){
            editor.putFloat(todaysnumOfDeliveries+"CreditTipHistory", todaysCreditTipHistory.pop());
        }

        while(!todaysCreditPriceHistory.empty()){
            editor.putFloat(todaysnumOfDeliveries+"CreditHistory", todaysCreditPriceHistory.pop());
        }
        editor.apply();



    }
}