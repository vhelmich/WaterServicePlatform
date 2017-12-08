package com.ui.waterserviceplatform;

/**
 * Created by vhelmich on 29/11/17.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentContainer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Button;

public class FragmentGeneral extends Fragment {

    private SeekBar slider;
    private static final int RANGE = 10;
    private static final int STEP_SIZE = 1;
    private EditText idField, addInfoField;
    private ScrollView scrollview;

    public int intensity;
    public String pipeID;
    public String additionalInfo;



    public FragmentGeneral() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_general, container, false);

        intensity = 0;
        pipeID = "";
        additionalInfo = "";

        slider = (SeekBar) view.findViewById(R.id.seekbarDamage);
        idField = (EditText) view.findViewById(R.id.idPipe);
        addInfoField = (EditText) view.findViewById(R.id.addInfo);
        scrollview = (ScrollView) view.findViewById(R.id.scrollview);

        addInfoField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            scrollview.fullScroll(View.FOCUS_DOWN);
                        }

                    }, 500);

                }
            }
        });
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    intensity = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        final Button nextButton = view.findViewById(R.id.codeButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveContent();
                hideKeyboard();
                ((MainActivity)getActivity()).switchTab(1);
            }
        });

        return view;
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void saveContent() {
        pipeID = idField.getText().toString();
        System.out.println("pipe ID " +  pipeID);

        additionalInfo = addInfoField.getText().toString();
        System.out.println("additional info " + additionalInfo);
    }
}
