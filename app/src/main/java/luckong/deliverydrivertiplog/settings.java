package luckong.deliverydrivertiplog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class settings extends AppCompatActivity {
    TextView vehicleMPG;
    TextView storeMileage;
    TextView startingBank;
    Button storeMileageEdit;
    Button startingBankEdit;
    Button editMPG;
    SharedPreferences settingsPreferences;
    SharedPreferences.Editor editor;
    DecimalFormat precision = new DecimalFormat("0");
    DecimalFormat moneyPrecision = new DecimalFormat("0.00");

    float mpg;
    float storeMileageRate;
    float startingBankAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        vehicleMPG = (TextView)findViewById(R.id.vehicleMPG);
        editMPG = (Button)findViewById(R.id.mpgEdit);
        storeMileage = (TextView)findViewById(R.id.storeMileage);
        storeMileageEdit = (Button)findViewById(R.id.storeMileageEdit);
        startingBankEdit = (Button)findViewById(R.id.startingBankEdit);
        startingBank = (TextView)findViewById(R.id.startingBank);

        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settingsPreferences.edit();

        mpg = settingsPreferences.getFloat("mpg", 0);
        storeMileageRate = settingsPreferences.getFloat("storeMileage", (float)1.10);
        startingBankAmount = settingsPreferences.getFloat("startingBank", (float)15.00);

        vehicleMPG.setText(precision.format(mpg) + " mpg");
        storeMileage.setText("$" + moneyPrecision.format(storeMileageRate));
        startingBank.setText("$" + moneyPrecision.format(startingBankAmount));

        storeMileageEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(settings.this);
                builder.setTitle("Enter Store Mileage Rate");

                // Set up the input
                final EditText input = new EditText(settings.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!input.getText().toString().isEmpty()) {
                            float m_Text = Float.valueOf(input.getText().toString());
                            storeMileage.setText("$" + moneyPrecision.format(m_Text));
                            editor.putFloat("storeMileage", m_Text);
                            editor.apply();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


        editMPG.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(settings.this);
                builder.setTitle("Enter Vehicle MPG");

                // Set up the input
                final EditText input = new EditText(settings.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!input.getText().toString().isEmpty()) {
                            float m_Text = Float.valueOf(input.getText().toString());
                            vehicleMPG.setText(precision.format(m_Text) + " mpg");
                            editor.putFloat("mpg", m_Text);
                            editor.apply();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        startingBankEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(settings.this);
                builder.setTitle("Enter Starting Bank");

                // Set up the input
                final EditText input = new EditText(settings.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!input.getText().toString().isEmpty()) {
                            float m_Text = Float.valueOf(input.getText().toString());
                            startingBank.setText("$" + precision.format(m_Text));
                            editor.putFloat("startingBank", m_Text);
                            editor.apply();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }

        });


    }
}
