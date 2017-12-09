package com.ui.waterserviceplatform;

/**
 * Created by vhelmich on 29/11/17.
 */

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;

public class FragmentGeneral extends Fragment {

    private SeekBar slider;
    private EditText idField, addInfoField;
    private ScrollView scrollview;
    private int intensity = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_general, container, false);

        slider = (SeekBar) view.findViewById(R.id.seekbarDamage);
        idField = (EditText) view.findViewById(R.id.idPipe);
        addInfoField = (EditText) view.findViewById(R.id.addInfo);
        scrollview = (ScrollView) view.findViewById(R.id.scrollview);
        setListeners(view);

        return view;
    }

    /**
     * Set listeners for various elements
     */
    private void setListeners(View view){
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
                ((MainActivity)getActivity()).resetTabColor(0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        Button nextButton = view.findViewById(R.id.codeButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loseFocus();
                ((MainActivity)getActivity()).switchTab(1);
            }
        });
    }

    /**
     * Hides the keyboard
     */
    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if(getActivity().getCurrentFocus()!=null) {
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Removes focus for every field
     */
    public void loseFocus(){
        hideKeyboard();
        addInfoField.clearFocus();
        idField.clearFocus();
        if(getView()!=null) {
            getView().findViewById(R.id.generalFragment).requestFocus();
        }
    }

    /**
     * Return the intensity
     * @return intensity
     */
    public int getIntensity(){
        return this.intensity;
    }

}
