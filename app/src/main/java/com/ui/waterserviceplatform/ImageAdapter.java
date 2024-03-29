package com.ui.waterserviceplatform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jm on 12/7/17.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Uri> imagesUri = new ArrayList<>();
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private FragmentPhoto fp;

    public ImageAdapter(FragmentPhoto f, Context c) {
        mContext = c;
        fp=f;
        options = new DisplayImageOptions.Builder().build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(new ImageLoaderConfiguration.Builder(c).build());

    }

    public int getCount() {
        return imagesUri.size();
    }

    public Object getItem(int position) {
        return imagesUri.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Uri image = imagesUri.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.image_layout, null);
        }
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.image_grid);
        final ImageView close = (ImageView)convertView.findViewById(R.id.image_delete);
        new LoadImage(imageView, image.toString()).execute();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImage(position);
                fp.setButtonVisibility(View.VISIBLE);
            }
        });
        return convertView;
    }

    private void removeImage(int position){
        imagesUri.remove(position);
        this.notifyDataSetChanged();
    }

    public void addImages(ArrayList<Uri> images){
        imagesUri.addAll(images);
        if(this.getCount()==4){
            fp.setButtonVisibility(View.INVISIBLE);
        }
        this.notifyDataSetChanged();
    }

    class LoadImage extends AsyncTask<Object, Void, Bitmap>{

        private ImageView imv;
        private String path;

        public LoadImage(ImageView imv, String path) {
            this.imv = imv;
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            ImageSize targetSize = new ImageSize(400,400);
            Bitmap bmp = imageLoader.loadImageSync(path,  targetSize,options);
            Bitmap resized = Bitmap.createScaledBitmap(bmp, 400, 400, true);

            return resized;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            if(result != null && imv != null){
                imv.setVisibility(View.VISIBLE);
                imv.setImageBitmap(result);
            }
        }

    }
}
