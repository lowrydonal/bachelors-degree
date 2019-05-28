package com.lowry.lapspace3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

/*
 * ACTIVITY: MATCH INPUT IMAGE WITH TEMPLATE IMAGE USING SIFT
 */

public class VerifyImageActivity extends AppCompatActivity {
    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("nonfree");
    }
    private FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT);
    private DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
    private DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
//    private boolean isMatch;
    int lap_id;

    // important thresholds
    private int matchThreshold = 7; // amount of matches that warrants a match
    double threshold = 0.55; // determines "quality" of matches that are not discarded

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_image);

//         if match image passed via intent, computation is done (2nd time around)
//        Intent intent = getIntent();
//        if (intent.hasExtra("isMatch")) {
//            ImageView matchIV = (ImageView) findViewById(R.id.match_iv);
//            TextView matchTV = (TextView) findViewById(R.id.match_tv);
//
//            lap_id = intent.getIntExtra("lap_id", -1);
//
//            // get intent data
//            byte[] matchImgByteArray = getIntent().getByteArrayExtra("matchImgByteArray");
//            Bitmap matchImage = BitmapFactory.decodeByteArray(matchImgByteArray, 0, matchImgByteArray.length);
//            String matchResult = intent.getStringExtra("matchResultString");
//
//            // assign to layout
//            matchIV.setImageBitmap(matchImage);
//            matchTV.setText(matchResult);
//            String isMatch = intent.getStringExtra("match");
//            if (isMatch == "match") {
//                Intent intent2 = new Intent(VerifyImageActivity.this, RetrieveLapProfileDetailsActivity.class);
//                intent.putExtra("lap_id", lap_id);
//                Toast.makeText(this, "Successful Verification", Toast.LENGTH_LONG).show();
//                VerifyImageActivity.this.startActivity(intent2);
//            }
//            else {
//                Intent intent3 = new Intent(VerifyImageActivity.this, RetrieveLapProfileDetailsActivity.class);
//                intent.putExtra("lap_id", lap_id);
//                Toast.makeText(this, "Unsuccessful Verification", Toast.LENGTH_LONG).show();
//                VerifyImageActivity.this.startActivity(intent3);
//            }
//        }
//        else { // computation not done yet (1st time around)


            // get image to verify and resize
            String filePath = getIntent().getStringExtra("imageFilePath");
            Bitmap bitmap2 = BitmapFactory.decodeFile(filePath);
            bitmap2 = getResizedBitmap(bitmap2, 600);  // resize bitmap2

            // get template image from database
            String imageString = getIntent().getStringExtra("tempImgString");
            byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            decodedByte = getResizedBitmap(decodedByte, 600);
            Bitmap bitmap1 = decodedByte;

            // do matching and display match image
            new MatchProcessingTask().execute(bitmap1, bitmap2);


//        }
    }


    //---------------------------------------METHODS---------------------------------------------//


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


    // MATCH PROCESSING ASYNC TASK
    private class MatchProcessingTask extends AsyncTask<Bitmap, Void, Void> {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        FragmentProgressBar progressBarFragment;

        @Override
        protected void onPreExecute() {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            progressBarFragment = new FragmentProgressBar();
            fragmentTransaction.replace(android.R.id.content, progressBarFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        @Override
        protected Void doInBackground(Bitmap... params) {
//            ClassMatchData matchDataObject = doMatchProcessing(params[0], params[1]);
            doMatchProcessing(params[0], params[1]);
            return null;
        }
    }


    // MATCH PROCESSING
    public void doMatchProcessing(Bitmap bitmap1, Bitmap bitmap2) {
//        boolean isMatch;
        // convert bitmaps to mats
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();
        Utils.bitmapToMat(bitmap1, mat1);
        Utils.bitmapToMat(bitmap2, mat2);
        // grayscale
        Imgproc.cvtColor(mat1, mat1, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.cvtColor(mat2, mat2, Imgproc.COLOR_RGBA2GRAY);
        // detect keypoints
        MatOfKeyPoint keyPoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keyPoints2 = new MatOfKeyPoint();
        detector.detect(mat1, keyPoints1);
        detector.detect(mat2, keyPoints2);
        // exctract descriptors
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();
        extractor.compute(mat1, keyPoints1, descriptors1);
        extractor.compute(mat2, keyPoints2, descriptors2);
        // match descriptors
        List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
        matcher.knnMatch(descriptors1, descriptors2, matches, 2);
        // remove bad matches
        LinkedList<DMatch> goodMatches = new LinkedList<DMatch>();
        for (int i = 0; i < matches.size(); i++) {
            MatOfDMatch matofDMatch = matches.get(i);
            DMatch[] array = matofDMatch.toArray();
            DMatch m1 = array[0];
            DMatch m2 = array[1];
            if (m1.distance <= m2.distance * threshold) {
                goodMatches.addLast(m1);
            }
        }
        // decide whether its a match
        if (goodMatches.size() >= matchThreshold) {
//            isMatch = true;
            Intent intent2 = new Intent(VerifyImageActivity.this, SuccessVerifyActivity.class);
            intent2.putExtra("lap_id", lap_id);
            VerifyImageActivity.this.startActivity(intent2);
        } else {
//            isMatch = false;
            Intent intent3 = new Intent(VerifyImageActivity.this, UnsuccessfulVerifyActivity.class);
            intent3.putExtra("lap_id", lap_id);
            VerifyImageActivity.this.startActivity(intent3);
        }

    // COMMENTED OUT: JUST NEED MATCH RESULT NOW!!!
//        // convert List<MatOfDMatch> to MatOfDMatch
//        MatOfDMatch good_matches_mat = new MatOfDMatch();
//        good_matches_mat.fromList(goodMatches);
//        // draw match image
//        Mat matchedImage = new Mat();
//        Features2d.drawMatches(mat1, keyPoints1, mat2, keyPoints2, good_matches_mat, matchedImage);
//        // convert mat to bitmap
//        Bitmap bm = Bitmap.createBitmap(matchedImage.cols(), matchedImage.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(matchedImage, bm);
//        // display if match or not
//        String goodMatchesCount = String.valueOf(goodMatches.size());
//        String textViewString = goodMatchesCount + " matches: ";
//        if (isMatch) {
//            textViewString += "Match!";
//        } else { // no match
//            textViewString += "No Match.";
//        }
//        // return match data object
//        ClassMatchData matchData = new ClassMatchData(bm, textViewString);
//        return matchData;
    }


    // ON BACK BUTTON PRESSED
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(VerifyImageActivity.this, RetrieveLapProfileDetailsActivity.class);
                intent.putExtra("lap_id", lap_id);
                VerifyImageActivity.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // phone back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VerifyImageActivity.this, RetrieveLapProfileDetailsActivity.class);
        intent.putExtra("lap_id", lap_id);
        VerifyImageActivity.this.startActivity(intent);
    }
}
