package com.ui.waterserviceplatform;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by vhelmich on 29/11/17.
 */

public class PhoneActivity extends AppCompatActivity {

    private TextView errormsgPhone;
    private TextView errormsgCode;
    private EditText phoneField;
    private String phoneNumber;
    private String code;
    private EditText codeField;
    private String generatedCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_phone);
;
        phoneField = (EditText) findViewById(R.id.phoneNumberField);
        errormsgPhone = (TextView) findViewById(R.id.errorMsgPhone);




        final Button confirmButton = findViewById(R.id.confirmNumberButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                phoneNumber = phoneField.getText().toString();
                if(checkPhoneNumber(phoneNumber)) {
                    //TODO: send code
                    generatedCode = "123456"; //set here only for testing
                    Intent intent = new Intent(getApplicationContext(), CodeActivity.class);
                    intent.putExtra("stringname",generatedCode);
                    System.out.println(intent.getStringExtra("stringname"));
                    startActivity(intent);


                } else {
                    errormsgPhone.setText("Please enter a valid phone number.");
                    errormsgPhone.setVisibility(View.VISIBLE);
                }

            }
        });






    }

    private boolean checkPhoneNumber(String phoneNumber) {

        //TODO: check, if phone number is valid

        return !phoneNumber.isEmpty();
    }

    private boolean checkCode(String code) {

        if (code.equals(generatedCode)) {
            return true;
        } else {
            return false;
        }
    }


}
