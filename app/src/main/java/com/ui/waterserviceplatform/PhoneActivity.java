package com.ui.waterserviceplatform;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by vhelmich on 29/11/17.
 */

public class PhoneActivity extends AppCompatActivity {

    private EditText phoneField;
    private String phoneNumber;
    private String generatedCode;
    private Button confirmButton;
    private final static int MY_PERMISSIONS_REQUEST_READ_SMS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_phone);

        phoneField = (EditText) findViewById(R.id.phoneNumberField);

        confirmButton = findViewById(R.id.confirmNumberButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                phoneNumber = phoneField.getText().toString();
                if(checkPhoneNumber(phoneNumber)) {
                    //TODO: send code
                    generatedCode = "123456"; //set here only for testing
                    Intent intent = new Intent(getApplicationContext(), CodeActivity.class);
                    intent.putExtra("stringname",generatedCode);
                    startActivity(intent);


                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Please enter a valid phone number.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

            }
        });
        requestPhonePermission();
    }

    private void requestPhonePermission(){
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Permission required");
            alertDialog.setMessage("We can automatically verify your number if you give us the permission" +
                    " to read your SMS, else you will have to do it manually.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ActivityCompat.requestPermissions(PhoneActivity.this,
                            new String[]{Manifest.permission.READ_SMS},
                            MY_PERMISSIONS_REQUEST_READ_SMS);
                }
            });
            alertDialog.show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        String number = telephonyManager.getLine1Number();
                        phoneField.setText(number);
                        confirmButton.performClick();
                    }
                    catch(SecurityException e){

                    }
                }
                return;
            }
        }
    }
    
    private boolean checkPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^[+]?[0-9]{10,13}$");
    }

}
