package luckong.deliverydrivertiplog;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by luckong on 7/28/17.
 */


public class PersonalDBHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "statistics_table";
    private static final String TAG = "PersonalDbHelper";
    private static final String col1 = "ID";
    private static final String col2 = "amountOwed";
    private static final String col3 = "creditTips";
    private static final String col4 = "cashTips";
    private static final String col5 = "totalTips";
    private static final String col6 = "mileage";
    private static final String col7 = "numOfDeliveries";
    private static final String col8 = "walkAmount";


    public PersonalDBHelper(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + TABLE_NAME + " ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + col2 + " TEXT" + col3 + " TEXT" + col4 + " TEXT" + col5 + " TEXT"
                + col6 + " TEXT" + col7 + " TEXT" + col8 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1){
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String amountOwed, String creditTips, String cashTips,
                           String totalTips, String mileage, String numOfDeliveries,
                           String walkAmount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col2, amountOwed);
        contentValues.put(col3, creditTips);
        contentValues.put(col4, cashTips);
        contentValues.put(col5, totalTips);
        contentValues.put(col6, mileage);
        contentValues.put(col7, numOfDeliveries);
        contentValues.put(col8, walkAmount);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

}