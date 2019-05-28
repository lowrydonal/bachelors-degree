package com.lowry.lapspace3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * FRAGMENT: DISPLAY IMAGE PREVIEWS
 */

public class DisplayImgPreviewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // init view
        View view = inflater.inflate(R.layout.fragment_display_img_preview, container, false);

        ImageView previewImageView = (ImageView) view.findViewById(R.id.preview_image_view);
        ImageView previewTempImageView = (ImageView) view.findViewById(R.id.preview_image_view);

        // get data from activity
        GetImagesActivity activity = (GetImagesActivity) getActivity();
        String imageString = activity.imageString;
        String filePath = activity.mCurrentPhotoPath;

        // decode images to bitmaps
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        decodedByte = getResizedBitmap(decodedByte, 600); // resize
        Bitmap previewTempImageBitmap = decodedByte;
        previewTempImageView.setImageBitmap(previewTempImageBitmap);

        Bitmap previewBitmap = BitmapFactory.decodeFile(filePath);
        previewImageView.setImageBitmap(previewBitmap);

        // button listener

        return view;
    }


    // RESIZE BITMAP WITH SAME RATIO
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
