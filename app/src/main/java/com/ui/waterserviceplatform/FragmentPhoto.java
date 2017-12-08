package com.ui.waterserviceplatform;

/**
 * Created by vhelmich on 29/11/17.
 */
import android.*;
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
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
            checkStoragePermission();

            button = (FloatingActionButton) view.findViewById(R.id.pickImage);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createBottomSheet();
                    bottomSheet.show(getActivity().getSupportFragmentManager());
                }
            });

            gv = (GridView) view.findViewById(R.id.gridview);
            imageAdapter = new ImageAdapter(this, getActivity().getApplicationContext());
            gv.setAdapter(imageAdapter);

            final Button nextButton = view.findViewById(R.id.codeButton);
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

            return view;
        }

        private void checkStoragePermission(){
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                }
            };
            TedPermission.with(getContext())
                    .setPermissionListener(permissionlistener)
                    .setRationaleTitle("Requesting permission")
                    .setRationaleMessage("We need to get access to your storage to send pictures.")
                    .setDeniedMessage("Permission denied")
                    .setDeniedMessage("If you reject permission, you cannot send pictures.\n" +
                            "Please turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        }

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
                    .setSelectMaxCount(3-imageAdapter.getCount())
                    .setCompleteButtonText("Done")
                    .setEmptySelectionText("No Select")
                    .create();
        }

        public void setButtonVisiblity(int visible){
            button.setVisibility(visible);
        }

}
