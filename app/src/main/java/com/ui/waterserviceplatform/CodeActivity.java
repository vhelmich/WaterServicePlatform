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

public class CodeActivity extends AppCompatActivity {


    private TextView errormsgCode;
    private String code;
    private EditText codeField;
    private String generatedCode;
    //private String exampleCode = "123456"; //set here only for testing


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_code);

        Intent intent = getIntent();

        Bundle b = intent.getExtras();

        if(b!=null)
        {
            generatedCode =(String) b.get("stringname");

        }

        System.out.println("code: " + generatedCode);

        errormsgCode = (TextView) findViewById(R.id.errorMsgCode);
        codeField = (EditText) findViewById(R.id.codeField);
        final Button codeButton = findViewById(R.id.codeButton);
        codeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                code = codeField.getText().toString();
                if (checkCode(code)) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                } else {
                    errormsgCode.setText("The code is not correct.");
                    errormsgCode.setVisibility(View.VISIBLE);
                }

            }
        });


    }

    private boolean checkCode(String code) {
        return code.equals(generatedCode);
    }


}
