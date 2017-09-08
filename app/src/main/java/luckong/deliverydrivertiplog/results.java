package luckong.deliverydrivertiplog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.preferenceCategoryStyle;
import static android.R.attr.text;
import static android.R.attr.value;

public class results extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences weeklyPreferences;
    EditText amountOwed;
    TextView walkAmount;
    TextView creditAmount;
    TextView mileageAmount;
    TextView cashAmount;
    TextView totalTip;
    TextView numOfDeliveriesText;
    TextView extraMileage;
    TextView logDate;
    Button editButton;
    Button finishEdit;
    Button deleteDay;

    float totalAmountOwed;
    float totalWalkAmount;
    float totalCashPrice;
    float totalCashTipFloat;
    double totalCashTip;
    float totalCreditPrice;
    float totalCreditTip;
    float mpg;
    float storeMileageRate;
    float totalTips;
    float numOfDeliveries;
    float totalTipsAllTime;
    float allTimeCreditTips;
    float allTimeCashTips;
    float allTimeMileage;
    float mileage;
    float allTimeDeliveries;
    float allTimeWalkAmount;
    float mostNumOfDeliveries;
    float mostNumOfTips;
    float yData[];
    Boolean currentDay;
    String date;
    DecimalFormat precision = new DecimalFormat("0.00");

    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        myDB = new DatabaseHelper(this);

        //XML layout declarations
            amountOwed = (EditText)findViewById(R.id.storeOwe);
            walkAmount = (TextView)findViewById(R.id.walkAmount);
            creditAmount = (TextView)findViewById(R.id.creditAmount);
            mileageAmount = (TextView)findViewById(R.id.mileageAmount);
            cashAmount = (TextView)findViewById(R.id.cashAmount);
            totalTip = (TextView)findViewById(R.id.totalTipAmount);
            numOfDeliveriesText = (TextView)findViewById(R.id.numOfDeliveries);
            editButton = (Button)findViewById(R.id.editButton);
            logDate = (TextView)findViewById(R.id.logDate);
            finishEdit = (Button)findViewById(R.id.finishEdit);
            deleteDay = (Button)findViewById(R.id.deleteButton);


            final DecimalFormat amountDelivery = new DecimalFormat("0");

            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            date = preferences.getString("date", "No data");

            //Get day's results from database
            Cursor res = myDB.getDeliveryDay(date);
        /*
            if(res.getCount() == 0){
                //show Message
                showMessage("Error", "No Data Found");
                logDate.setText(date);
                return;
            }
*/
            if(res.moveToFirst()){
                //Show found results

                date = res.getString(0);
                totalAmountOwed = res.getFloat(1);
                totalCashTipFloat = res.getFloat(2);
                totalCreditTip = res.getFloat(3);
                mileage = res.getFloat(4);
                numOfDeliveries = res.getFloat(5);
                totalWalkAmount = res.getFloat(6);
                res.close();
            }

            logDate.setText(date);
            amountOwed.setText("$" + precision.format(totalAmountOwed));
            cashAmount.setText("$" + precision.format(totalCashTipFloat));
            totalTip.setText("$" + precision.format(totalCashTipFloat+totalCreditTip));
            creditAmount.setText("$" + precision.format(totalCreditTip));
            mileageAmount.setText("$" + precision.format(mileage));
            numOfDeliveriesText.setText("" + amountDelivery.format(numOfDeliveries));
            walkAmount.setText("$" + precision.format(totalWalkAmount));

            totalTips = (float)totalCashTip + totalCreditTip;

        /*
            weeklyEditor.putFloat(date+"amountOwed", (float)totalAmountOwed);
            weeklyEditor.putFloat(date+"walkAmount", (float)(totalCashTip + totalCreditTip + mileage));
            weeklyEditor.putFloat(date+"creditAmount", totalCreditTip);
            weeklyEditor.putFloat(date+"cashAmount", (float)totalCashTip);
            weeklyEditor.putFloat(date+"numOfDeliveries", numOfDeliveries);
            weeklyEditor.putFloat(date+"mileage", mileage);
            weeklyEditor.putFloat(date+"Tip", totalTips);
            weeklyEditor.apply();
*/

            editButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //When Change Values button is pressed

                    finishEdit.setVisibility(View.VISIBLE);
                    editButton.setVisibility(View.INVISIBLE);
/*
                    //Get variables again
                    numOfDeliveries = preferences.getFloat(date+"numOfDeliveries", 0);
                    totalAmountOwed = preferences.getFloat(date+"totalAmountOwed", 0);
                    totalCashPrice = preferences.getFloat(date+"CashPrice", 0);
                    totalCashTipFloat = preferences.getFloat(date+"CashTip", 0);
                    totalCreditPrice = preferences.getFloat(date+"CreditPrice", 0);
                    totalCreditTip = preferences.getFloat(date+"CreditTip", 0);
                    mpg = preferences.getFloat("mpg", 0);
                    storeMileageRate = preferences.getFloat("storeMileage", (float)1.10);
                    mileage = preferences.getFloat(date+"mileage", 0);


                    System.out.println("TOTALAMOUNT RECEIVED: " + totalAmountOwed);
*/
                    //Make all values appear as numbers instead of as strings with a $ in beginning removed
                    storeMileageRate = preferences.getFloat("storeMileage", (float)1.10);
                    amountOwed.setText(precision.format((double)totalAmountOwed));
                    walkAmount.setText(precision.format(totalWalkAmount));
                    creditAmount.setText(precision.format(totalCreditTip));
                    cashAmount.setText(precision.format(totalCashTipFloat));
                    totalTip.setText(precision.format(totalCashTipFloat + totalCreditTip));
                    numOfDeliveriesText.setText(amountDelivery.format(numOfDeliveries));
                    mileageAmount.setText(precision.format(mileage));

                    //The walk amount and total tips should not be editable

                    //amountOwed Edit
                    amountOwed.setFocusable(true);
                    amountOwed.setFocusableInTouchMode(true);
                    amountOwed.setClickable(true);

                    //creditAmount Edit
                    creditAmount.setFocusable(true);
                    creditAmount.setFocusableInTouchMode(true);
                    creditAmount.setClickable(true);

                    //cashAmount Edit
                    cashAmount.setFocusable(true);
                    cashAmount.setFocusableInTouchMode(true);
                    cashAmount.setClickable(true);

                    //numberOfDeliveries Edit
                    numOfDeliveriesText.setFocusable(true);
                    numOfDeliveriesText.setFocusableInTouchMode(true);
                    numOfDeliveriesText.setClickable(true);

                    //mileageAmount Edit
                    mileageAmount.setFocusable(true);
                    mileageAmount.setFocusableInTouchMode(true);
                    mileageAmount.setClickable(true);

                }
            });

            finishEdit.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    //APPLYING CHANGES

                    //Get edited amountOwed
                    totalAmountOwed = Float.parseFloat(amountOwed.getText().toString());
                    //weeklyEditor.putFloat(date+"totalAmountOwed", totalAmountOwed);


                    //Get edited creditAmount
                    totalCreditTip= Float.parseFloat(creditAmount.getText().toString());
                   // weeklyEditor.putFloat(date+"CreditTip", totalCreditTip);

                    //Get edited cashAmount
                    totalCashTipFloat = Float.parseFloat(cashAmount.getText().toString());
                   // weeklyEditor.putFloat(date+"CashTip", totalCashTipFloat);

                    //Get edited number of deliveries
                    numOfDeliveries = Float.parseFloat(numOfDeliveriesText.getText().toString());
                  //  weeklyEditor.putFloat(date+"numOfDeliveries", numOfDeliveries);

                    //Get edited mileage amount
                    mileage = Float.parseFloat(mileageAmount.getText().toString());
                  //  weeklyEditor.putFloat(date+"mileage", mileage);


                    //Update Database
                    totalWalkAmount = totalCreditTip+totalCashTipFloat+numOfDeliveries*storeMileageRate;
                    totalTips = totalCreditTip+totalCashTipFloat;
                    myDB.updateDeliveryDay(date, totalCashTipFloat, totalCreditTip, mileage,numOfDeliveries, storeMileageRate, totalAmountOwed, totalWalkAmount, totalTips, 0, 0, 0);
                    /*
                    //Change all time highest number of deliveries
                    mostNumOfDeliveries = preferences.getFloat("mostNumOfDeliveries", 0);
                    if(numOfDeliveries > mostNumOfDeliveries){
                        weeklyEditor.putFloat("mostNumOfDeliveries", numOfDeliveries);
                        weeklyEditor.putString("deliveryRecordDate", date);
                    }

                    //Change all time highest total tip
                    mostNumOfTips = preferences.getFloat("mostNumOfTips", 0);
                    float totalTips = totalCashTipFloat + totalCreditTip;
                    if(totalTips > mostNumOfTips){
                        weeklyEditor.putFloat("mostNumOfTips", totalTips);
                        weeklyEditor.putString("mostNumOfTipsDate", date);
                    }

                    weeklyEditor.apply();
*/
                    //Show all values with dollar signs again
                    amountOwed.setText("$" + precision.format(totalAmountOwed));
                    walkAmount.setText("$" + precision.format(totalCashTipFloat + totalCreditTip + mileage));
                    creditAmount.setText("$" + precision.format(totalCreditTip));
                    cashAmount.setText("$" + precision.format(totalCashTipFloat));
                    totalTip.setText("$" + precision.format(totalCashTipFloat + totalCreditTip));
                    numOfDeliveriesText.setText("" + amountDelivery.format(numOfDeliveries));
                    mileageAmount.setText("$" + precision.format(mileage));

                    //Make amountOwed uneditable
                    amountOwed.setFocusable(false);
                    amountOwed.setFocusableInTouchMode(false);
                    amountOwed.setClickable(false);

                    //Make creditAmount uneditable
                    creditAmount.setFocusable(false);
                    creditAmount.setFocusableInTouchMode(false);
                    creditAmount.setClickable(false);

                    //Make cashAmount uneditable
                    cashAmount.setFocusable(false);
                    cashAmount.setFocusableInTouchMode(false);
                    cashAmount.setClickable(false);

                    //Make number of deliveries uneditable
                    numOfDeliveriesText.setFocusable(false);
                    numOfDeliveriesText.setFocusableInTouchMode(false);
                    numOfDeliveriesText.setClickable(false);

                    //Make mileage uneditable
                    mileageAmount.setFocusable(false);
                    mileageAmount.setFocusableInTouchMode(false);
                    mileageAmount.setClickable(false);

                    //Change visibility of buttons
                    finishEdit.setVisibility(View.INVISIBLE);
                    editButton.setVisibility(View.VISIBLE);

                }
            });

        deleteDay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                boolean success = myDB.deleteDate(date);
                                Toast.makeText(getApplicationContext(), "RESULT: " + success, Toast.LENGTH_SHORT).show();
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Delete this day's delivery data?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void onStop(){
        super.onStop();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentDay = preferences.getBoolean("currentDay", false);
        SharedPreferences.Editor editor = preferences.edit();
        if(currentDay){
            //Remove current day's delivery values
            System.out.println("REMOVING LOCAL VARIABLES");
            editor.remove("CashPrice");
            editor.remove("CreditPrice");
            editor.remove("CashTip");
            editor.remove("CreditTip");
            editor.remove("numOfDeliveries");

        }
        editor.remove("currentDay");
        editor.apply();

    }
}
