package com.ui.waterserviceplatform;

/**
 * Created by vhelmich on 29/11/17.
 */

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;

public class FragmentPhoto extends Fragment {

        private TedBottomPicker bottomSheet;
        private GridView gv;
        private ImageAdapter imageAdapter;
        private FloatingActionButton button;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_photo, container, false);

            setupButton(view);
            setupGridView(view);
            setupNavBar(view);

            return view;
        }

    /**
     * Setup the floating button
     * @param view view containing the button
     */
    private void setupButton(View view){
            button = (FloatingActionButton) view.findViewById(R.id.pickImage);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createBottomSheet();
                    bottomSheet.show(getActivity().getSupportFragmentManager());
                }
            });
        }

    /**
     * Setup the gridView
     * @param view view containing the button
     */
    private void setupGridView(View view){
        gv = (GridView) view.findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this, getActivity().getApplicationContext());
        gv.setAdapter(imageAdapter);
    }

    /**
     * Setup the navbar
     * @param view view containing the navbar
     */
    private void setupNavBar(View view){
        Button nextButton = view.findViewById(R.id.codeButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).switchTab(2);
            }
        });

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).switchTab(0);
            }
        });
    }

    /**
     * Create the image picker
     */
    private void createBottomSheet(){
            bottomSheet = new TedBottomPicker.Builder(getContext())
                    .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {
                        @Override
                        public void onImagesSelected(ArrayList<Uri> uriList) {
                            imageAdapter.addImages(uriList);
                        }
                    })
                    .setPeekHeight(1300)
                    .showTitle(false)
                    .setSelectMinCount(1)
                    .setSelectMaxCount(4-imageAdapter.getCount())
                    .setCompleteButtonText(getString(R.string.done))
                    .setEmptySelectionText(getString(R.string.no_select))
                    .create();
        }

    /**
     * Set the floating button visibility
     * @param visible visibility of the button
     */
    public void setButtonVisibility(int visible){
            button.setVisibility(visible);
        }

}
