package com.ui.waterserviceplatform;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        }

        for(int i=0;i<tvIds.length;i++){
            codeFields.add((CustomEditText) findViewById(tvIds[i]));
            if(i!=0){
                codeFields.get(i).setBefore(codeFields.get(i-1));
            }
        }
        setReplaceEt();
        setOnClickEt();
        System.out.println("code: " + generatedCode);
        codeReceived = (TextView) findViewById(R.id.codeNumber);
        codeReceived.setText("Your code is " + generatedCode);
        codeButton = findViewById(R.id.codeButton);
        codeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkCode(getInputCode())) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "The code is not correct.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

            }
        });


    }

    private boolean checkCode(int code) {
        return code == generatedCode;
    }

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

    private void setReplaceEt(){
        for(int i = 0; i<codeFields.size();i++){
            final CustomEditText et = codeFields.get(i);
            final int j = i;
            et.addTextChangedListener(new TextWatcher() {
                boolean empty = false;
                @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.d("beforeshit","before " + charSequence.toString());
                    if(charSequence.length()==0) {
                        empty = true;
                        Log.d("beforeshit", "before is true");
                    }
                }
                @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(i2 == 0 && j!=0) {
                        codeFields.get(j-1).requestFocus();
                    }
                }
                @Override public void afterTextChanged(Editable editable) {
                    Log.d("beforeshit","after " + editable.toString());
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

    private boolean allFilled(){
        for(final CustomEditText et : codeFields){
            if(et.getText().length()==0){
                return false;
            }
        }
        return true;
    }
}
