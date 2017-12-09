package com.ui.waterserviceplatform;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by vhelmich on 29/11/17.
 */

public class CodeActivity extends AppCompatActivity {

    private boolean auto;
    private String phoneNumber;
    private int generatedCode;
    private TextView codeReceived;
    private Button codeButton;
    private int[] tvIds = {R.id.codeField1,R.id.codeField2,R.id.codeField3,
            R.id.codeField4,R.id.codeField5,R.id.codeField6};
    private ArrayList<CustomEditText> codeFields = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_code);

        Intent intent = getIntent();

        Bundle b = intent.getExtras();

        if(b!=null)
        {
            generatedCode = (int) b.get("code");
            auto = (boolean) b.get("auto");
            phoneNumber = (String) b.get("number");
        }

        // the number has been automatically provided
        if(auto){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                   storeNumberAndLaunchMain();
                }
            }, 500);
        }
        else {
            setupInterface();
        }


    }

    /**
     * Setup the interface by adding the listener, creating necessary view etc..
     */
    private void setupInterface(){
        for (int i = 0; i < tvIds.length; i++) {
            codeFields.add((CustomEditText) findViewById(tvIds[i]));
            if (i != 0) {
                codeFields.get(i).setBefore(codeFields.get(i - 1));
            }
        }
        setReplaceEt();
        setOnClickEt();
        codeReceived = (TextView) findViewById(R.id.codeNumber);
        codeReceived.setText(getText(R.string.code_is).toString() + generatedCode);
        codeButton = findViewById(R.id.codeButton);
        codeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkCode(getInputCode())) {
                    storeNumberAndLaunchMain();
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = getText(R.string.incorrect_code);
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

            }
        });
    }

    /**
     * Check if the provided code match the generated one
     * @param code provided code
     * @return True if the code is correct
     */
    private boolean checkCode(int code) {
        return code == generatedCode;
    }

    /**
     * Generate the provided code from the different editTexts
     * @return input code
     */
    private int getInputCode(){
        int res = 0;
        for(CustomEditText et : codeFields){
            String text = et.getText().toString();
            if(text.length()==1){
                res = res * 10 + Integer.parseInt(text);
            }
        }
        return res;
    }

    /**
     * Setup the listeners for the editTexts
     */
    private void setReplaceEt(){
        for(int i = 0; i<codeFields.size();i++){
            final CustomEditText et = codeFields.get(i);
            final int j = i;
            et.addTextChangedListener(new TextWatcher() {
                boolean empty = false;
                @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.length()==0) {
                        empty = true;
                    }
                }
                @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(i2 == 0 && j!=0) {
                        codeFields.get(j-1).requestFocus();
                    }
                }
                @Override public void afterTextChanged(Editable editable) {
                    et.removeTextChangedListener(this);
                    if(editable.length()==2) {
                        et.setText(editable.toString().substring(1,2));
                    }
                    else if(editable.length()==1){
                        et.setText(editable);
                    }
                    if(j!=codeFields.size()-1 && et.getText().length()!=0){
                        codeFields.get(j+1).requestFocus();
                    }
                    else if(j==codeFields.size()-1 && allFilled()) {
                        codeButton.performClick();
                    }
                    else if(empty && editable.length()==0 && j!=0)
                        codeFields.get(j-1).requestFocus();
                    et.setSelection(et.getText().length());
                    et.addTextChangedListener(this);
                    empty=false;
                }}
            );
        }

    }

    /**
     * Setup the listeners for the editTexts
     */
    private void setOnClickEt(){
        for(final CustomEditText et : codeFields){
            et.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et.setSelection(et.getText().length());
                }
            });
            et.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            et.setSelection(et.getText().length());
                        }
                    }, 50);
                    return false;
                }
            }
            );
        }
    }

    /**
     * Check if the all the editTexts have been filled
     * @return true if the editTexts are filled
     */
    private boolean allFilled(){
        for(final CustomEditText et : codeFields){
            if(et.getText().length()==0){
                return false;
            }
        }
        return true;
    }

    /**
     * Store the provided phone number into the preferences and start the main activity.
     */
    private void storeNumberAndLaunchMain(){
        SharedPreferences sharedPref = this.getSharedPreferences("data",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.commit();

        Toast.makeText(getApplicationContext(),
                getText(R.string.confirmation_end),
                Toast.LENGTH_SHORT)
                .show();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
