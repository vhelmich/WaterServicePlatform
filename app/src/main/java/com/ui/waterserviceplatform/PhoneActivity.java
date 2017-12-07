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
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vhelmich on 29/11/17.
 */

public class PhoneActivity extends AppCompatActivity {

    private EditText phoneField;
    private String phoneNumber;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_phone);

        phoneField = findViewById(R.id.phoneNumberField);

        confirmButton = findViewById(R.id.confirmNumberButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                phoneNumber = phoneField.getText().toString();
                if(checkPhoneNumber(phoneNumber)) {
                    launchCodeActivity(false);
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
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                try {
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String number = telephonyManager.getLine1Number();
                    phoneField.setText(number);
                    launchCodeActivity(true);
                }
                catch(SecurityException e){

                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }


        };
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionlistener)
                .setRationaleTitle("Requesting permission")
                .setRationaleMessage("We can automatically verify your number if you give us the permission" +
                        " to read your SMS, else you will have to do it manually.")
                .setPermissions(Manifest.permission.READ_SMS)
                .check();
    }

    private boolean checkPhoneNumber(String phoneNumber) {
        return true;//phoneNumber.matches("^[+]?[0-9]{10,13}$");
    }

    private void launchCodeActivity(boolean auto){
        Random r = new Random();
        int code = r.nextInt(999999 - 100000) + 100000;
        Intent intent = new Intent(getApplicationContext(), CodeActivity.class);
        intent.putExtra("code", code);
        intent.putExtra("auto", auto);
        intent.putExtra("number", phoneField.getText().toString());
        startActivity(intent);
    }

}
