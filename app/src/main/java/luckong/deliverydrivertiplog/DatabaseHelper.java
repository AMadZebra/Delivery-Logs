package luckong.deliverydrivertiplog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by luckong on 8/10/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DeliveryLogs.db";
    public static final String TABLE_NAME= "DeliveryLogs_Table";
    public static final String COL_1 = "ID"; //ID is date of delivery
    public static final String COL_2 = "TotalOwe";
    public static final String COL_3 = "CashTip";
    public static final String COL_4 = "CreditTip";
    public static final String COL_5 = "Mileage";
    public static final String COL_6 = "numOfDeliveries";
    public static final String COL_7 = "walkAmount";
    public static final String COL_8 = "month";
    public static final String COL_9 = "totalCashPrice";
    public static final String COL_10 = "totalCreditPrice";
    public static final String COL_11 = "totalTips";
    public static final String COL_12 = "highestTip";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID TEXT PRIMARY KEY,TotalOwe FLOAT,CashTip FLOAT,CreditTip FLOAT,Mileage FLOAT,numOfDeliveries FLOAT,walkAmount FLOAT,month INTEGER,totalCashPrice FLOAT,totalCreditPrice FLOAT,totalTips FLOAT,highestTip FLOAT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }


    public boolean deleteDate(String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_1 + " like '" + date + "' ", null) > 0;
    }



    public boolean insertData(String date, float TotalOwe, float CashTip, float CreditTip, float Mileage, float numOfDeliveries, float walkAmount, int month, float totalCashPrice, float totalCreditPrice, float totalTips, float highestTip){
        SQLiteDatabase db = this.getWritableDatabase();

        //TEMP
        //onUpgrade(db, 0, 1);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, date);
        contentValues.put(COL_2, TotalOwe);
        contentValues.put(COL_3, CashTip);
        contentValues.put(COL_4, CreditTip);
        contentValues.put(COL_5, Mileage);
        contentValues.put(COL_6, numOfDeliveries);
        contentValues.put(COL_7, walkAmount);
        contentValues.put(COL_8, month);
        contentValues.put(COL_9, totalCashPrice);
        contentValues.put(COL_10, totalCreditPrice);
        contentValues.put(COL_11, totalTips);
        contentValues.put(COL_12, highestTip);
        long result = db.insert(TABLE_NAME, null, contentValues);


        if(result == -1){
            System.out.println("RESULT WAS -1 REEEEEEEEEeEEEEEEE");
            return false;
        }else{
            return true;
        }
    }

    public boolean checkIfDayIsAlreadySaved(String date){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME + " where " + COL_1 + " like '" + date + "'", null);
        if(cursor.getCount() >= 1){
            cursor.close();
            System.out.println("DAAAAAAAATE ALREADY SAVED");
            return true;
        }else{
            cursor.close();
            System.out.println("REEEETUUURNING FALSE");
            return false;
        }


    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public ArrayList getAllTotalTips(){
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = { COL_11 };
        Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, "ID ASC");

        ArrayList<Float> values = new ArrayList<Float>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                values.add(cursor.getFloat(0));
                cursor.moveToNext();
            }
            cursor.close();

        }

        return values;
    }

    public ArrayList getAllNumOfDeliveries(){
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = { COL_6 };
        Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, "ID ASC");

        ArrayList<Float> values = new ArrayList<Float>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                values.add(cursor.getFloat(cursor.getColumnIndex(COL_6)));

                cursor.moveToNext();
            }
            cursor.close();
        }
        return values;
    }

    public ArrayList getAllWalkAmount(){
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = { COL_7 };
        Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, "ID ASC");

        ArrayList<Float> values = new ArrayList<Float>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                values.add(cursor.getFloat(cursor.getColumnIndex(COL_7)));

                cursor.moveToNext();
            }
            cursor.close();
        }
        return values;
    }


    public ArrayList getAllDates(){
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = { COL_1 };
        Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, null);

        ArrayList<String> values = new ArrayList<String>();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                values.add(cursor.getString(cursor.getColumnIndex(COL_1)));

                cursor.moveToNext();
            }
            cursor.close();
        }
        return values;
    }

    public Cursor getDeliveryDay(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID like '" + date + "' LIMIT 1", null);

        return cursor;
    }

    public float[] getAllTimeStats(){
        SQLiteDatabase db = this.getWritableDatabase();
        float sumOfCashPrice=0;
        float sumOfCreditPrice=0;
        float sumOfCashTip=0;
        float sumOfCreditTip=0;
        float sumOfMileage=0;
        float sumOfNumOfDeliveries=0;
        float numOfDeliveryDays=0;
        float recordNumOfDeliveries=0;
        float recordNumOfTips=0;
        float highestTip=0;


        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_9 + ") FROM " + TABLE_NAME, null);
        if(cursor.moveToFirst()){
            sumOfCashPrice = cursor.getFloat(0);
        }

        Cursor cursor2 = db.rawQuery("SELECT SUM(" + COL_10 + ") FROM " + TABLE_NAME, null);
        if(cursor2.moveToFirst()){
            sumOfCreditPrice = cursor2.getFloat(0);
        }

        Cursor cursor3 = db.rawQuery("SELECT SUM(" + COL_3 + ") FROM " + TABLE_NAME, null);
        if(cursor3.moveToFirst()){
            sumOfCashTip = cursor3.getFloat(0);
        }

        Cursor cursor4 = db.rawQuery("SELECT SUM(" + COL_4 + ") FROM " + TABLE_NAME, null);
        if(cursor4.moveToFirst()){
            sumOfCreditTip = cursor4.getFloat(0);
        }

        Cursor cursor5 = db.rawQuery("SELECT SUM(" + COL_5 + ") FROM " + TABLE_NAME, null);
        if(cursor5.moveToFirst()){
            sumOfMileage = cursor5.getFloat(0);
        }

        Cursor cursor6 = db.rawQuery("SELECT SUM(" + COL_6 + ") FROM " + TABLE_NAME, null);
        if(cursor6.moveToFirst()){
            sumOfNumOfDeliveries = cursor6.getFloat(0);
        }

        Cursor cursor7 = db.rawQuery("SELECT Count(*) FROM " + TABLE_NAME, null);
        if(cursor7.moveToFirst()){
            numOfDeliveryDays = cursor7.getFloat(0);
        }

        Cursor cursor8 = db.rawQuery("SELECT MAX(" + COL_6 + ") FROM " + TABLE_NAME, null);
        if(cursor8.moveToFirst()){
            recordNumOfDeliveries = cursor8.getFloat(0);
        }

        Cursor cursor9 = db.rawQuery("SELECT MAX(" + COL_11 + ") FROM " + TABLE_NAME, null);
        if(cursor9.moveToFirst()){
            recordNumOfTips = cursor9.getFloat(0);
        }

        Cursor cursor10 = db.rawQuery("SELECT MAX(" + COL_12 + ") FROM " + TABLE_NAME, null);
        if(cursor10.moveToFirst()){
            highestTip = cursor10.getFloat(0);
        }


        cursor.close();
        cursor2.close();
        cursor3.close();
        cursor4.close();
        cursor5.close();
        cursor6.close();
        cursor7.close();
        cursor8.close();
        cursor9.close();
        cursor10.close();

        float allTimeValueOfPizzas = sumOfCashPrice + sumOfCreditPrice;
        float allTimeTips = sumOfCashTip + sumOfCreditTip;
        float percentOfCreditTip = (sumOfCreditTip/(allTimeTips))*100;
        float totalMade = sumOfMileage+sumOfCashTip+sumOfCreditTip;
        float averageTipsPerDay = (sumOfCashTip+sumOfCreditTip)/numOfDeliveryDays;


        float[] allTimeStats = new float[]{allTimeValueOfPizzas, allTimeTips, percentOfCreditTip, sumOfMileage, sumOfNumOfDeliveries, totalMade, averageTipsPerDay, recordNumOfDeliveries, recordNumOfTips, highestTip};

        return allTimeStats;
    }


    public void updateDeliveryDay(String date, float totalCashTip, float totalCreditTip, float mileage, float numOfDeliveries, float storeMileageRate, float totalOwe, float walkAmount, float totalTips, float highestTip, float cashPrice, float creditPrice){
        //Delete database values from same day
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        //float totalOwe = totalCashPrice - totalCreditTip - numOfDeliveries*storeMileageRate+ 15;
        cv.put("ID", date);
        cv.put("TotalOwe",totalOwe);
        cv.put("CashTip",totalCashTip);
        cv.put("CreditTip",totalCreditTip);
        cv.put("Mileage", mileage);
        cv.put("numOfDeliveries", numOfDeliveries);
        cv.put("walkAmount", walkAmount);
        cv.put("totalTips", totalTips);
        if(highestTip > 0f){
            cv.put("highestTip", highestTip);
        }
        if(cashPrice > 0f){
            cv.put("totalCashPrice", cashPrice);
        }
        if(creditPrice > 0f){
            cv.put("totalCreditPrice", creditPrice);
        }

        db.update(TABLE_NAME, cv, "ID like '"+date + "'", null);

    }



}
