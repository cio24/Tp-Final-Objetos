package com.example.acgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.acgallery.Composited.Folder;

public class ImageAdapter extends PagerAdapter {
    private Context mContext;
    private Folder folder;
    private SubsamplingScaleImageView image;

    ImageAdapter(Context context, Folder folder) {
        this.folder = folder;
        mContext = context;
    }

    @Override
    public int getCount() {
        return folder.getFilesAmount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(folder.getFileAt(position).getAbsolutePath(),bmOptions);
        imageView.setImageBitmap(bitmap);


        ImageSource source = ImageSource.bitmap(bitmap);
        image.setImage(source);

        container.addView(imageView,0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}