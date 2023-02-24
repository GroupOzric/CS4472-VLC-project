package com.example.vlc_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CameraScreen extends CameraActivity {

    CameraBridgeViewBase cameraBridgeViewBase;
    TextView textView2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if(OpenCVLoader.initDebug()) Log.d("LOADED", "success");
        else Log.d("LOADED", "err");
        Log.d("FLASH", "ONCREATE");


        getPermission();

        cameraBridgeViewBase = findViewById(R.id.cameraView);
        textView2 = findViewById(R.id.textView2);
        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {

            }

            @Override
            public void onCameraViewStopped() {

            }

            int detectedFrames = 0;
            int notDetectedFrames = 0;
            int frames = 0;
            String detectedString = "";
            String detectedStringWindow = "";
            String detectedText = "";
            int textLength = 0;
            //            boolean readingStarted = false;
            List<Integer> detectionList =new ArrayList<Integer>();
            final CameraBridgeViewBase.CvCameraViewListener2 context = this;



            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

//                Log.d("FLASH", "ONFRAME");
                //System.out.println("camera detected....");
                Mat rgba = inputFrame.rgba();
                Mat gray = new Mat();
                Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_BGR2GRAY);
                Mat bw = new Mat();
//                Core.bitwise_not(bw, bw);
                Imgproc.threshold(gray, bw, 254, 255, Imgproc.THRESH_BINARY);


                // getting pixel value
                // Find contours

                List<MatOfPoint> contours = new ArrayList<>();
                Mat hierarchy = new Mat();
                Imgproc.findContours(bw, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);



                double maxCounterArea = 0;
                // Process each contour
                for (MatOfPoint contour : contours) {
                    // Calculate area
                    Mat contourMat = new Mat();
                    contour.convertTo(contourMat, CvType.CV_32SC2);
                    double area = Imgproc.contourArea(contourMat);
                    if (maxCounterArea < area){
                        maxCounterArea = area;
                    }
                }
//                frames++;

//                frames
//                Log.d("Frames", Integer.toString(frames));

//                Log.d("FLASH", "Counter  Area"+ Double.toString(maxCounterArea));

                // Filter by area  & (maxCounterArea < 7000)
//10000
                if ((maxCounterArea > 20000)) {
                    // Further filter by shape and other properties
//                    detectionList.add(1);
                    notDetectedFrames = 0;
                    detectedFrames++;

//                    Log.d("FLASH", Integer.toString(detectedFrames));


//                    if(detectedFrames> 20){
//                        Log.d("FLASH", "FLASH DETECTED");
//                        detectedString = detectedString + "1";
//                        Log.d("FLASH", "DETECTED STRING"+ detectedString);
//                        detectedFrames = 0;
//                    }

                    // This contour is a candidate region where the flashlight is present
                }else{

                    notDetectedFrames++;
                    //4
                    if(notDetectedFrames > 2){
                        //                    Log.d("FLASH", "UNTIL THIS END .................."+ Integer.toString(detectedFrames));
////                    detectionList.add(0);
                        //10
                        if(detectedFrames >= 5){
//                    Log.d("FLASH", );
                            detectedString = detectedString + "0";
                            detectedStringWindow = detectedStringWindow + "0";
                            Log.d("FLASH", "String.................."+ detectedString);
                            String showText = detectedString+"  ..  "+detectedText;
//                            textView2.setText(showText);
//                            Log.d("FLASH", "0 DETECTED .................."+ Integer.toString(detectedFrames));
                        }else if ((detectedFrames> 1) & (detectedString.length() > 0)){
//                        Log.d("FLASH", Integer.toString(detectedFrames));
                            detectedString = detectedString + "1";
                            detectedStringWindow = detectedStringWindow + "1";
                            Log.d("FLASH", "String.................."+ detectedString);
                            String showText = detectedString+"  ..  "+detectedText;
//                            textView2.setText(showText);
//                            Log.d("FLASH", "1 DETECTED .................."+ Integer.toString(detectedFrames));
                        }

                        detectedFrames = 0;



                        // convert byte to string
                        if(detectedStringWindow.length()>= 8){
                            int checkStringlength = detectedStringWindow.length();
                            String checkString = detectedStringWindow.substring(checkStringlength-8, checkStringlength);
                            int number = Integer.parseInt(checkString, 2);

                            //                          receiving the length of text
                            if(textLength == 0){
                                textLength = number;
//                                receiving the rest
                            }else{
                                char ch = (char) number;
                                detectedText = detectedText + ch;
                                String showText = detectedText; //+"  ..  "+detectedText;
                                textView2.setText(showText);
                            }
//                          if length is match
                            if(detectedText.length() == textLength){
                                Intent intent = new Intent(CameraScreen.this, ReceiveActivity.class);
                                intent.putExtra("key", detectedText);
                                startActivity(intent);
                            }


                            detectedStringWindow = "";
                        }

                    }
//                    if(detectedFrames > 0){
//                        Log.d("FLASH", "UNTIL THIS END .................."+ Integer.toString(detectedFrames));
//
//                    }


//                    Log.d("FLASH", "UNTIL THIS END .................."+ Integer.toString(detectedFrames));
////                    detectionList.add(0);
//                    if(detectedFrames > 10){
////                    Log.d("FLASH", );
//                        Log.d("FLASH", "0 DETECTED .................."+ Integer.toString(detectedFrames));
//                    }else if (detectedFrames> 0){
////                        Log.d("FLASH", Integer.toString(detectedFrames));
//                        Log.d("FLASH", "1 DETECTED .................."+ Integer.toString(detectedFrames));
//                    }


//                    Log.d("FLASH", "FLASK OFF DETECTED");
//                    if(notDetectedFrames> 20){
//                        Log.d("FLASH", "NOT DETECTED");
//                        detectedString = detectedString + "0";
////                        Log.d("FLASH", "DETECTED STRING"+ detectedString.substring(0,8));
//                        notDetectedFrames = 0;
//                    }
                }

                // darshana method
//                if(detectedFrames > 2){
////                    Log.d("FLASH", );
//                    Log.d("FLASH", "1 DETECTED .................."+ Integer.toString(detectedFrames));
//                }else if (detectedFrames> 0){
//                    Log.d("FLASH", Integer.toString(detectedFrames));
//                    Log.d("FLASH", "0 DETECTED .................."+ Integer.toString(detectedFrames));
//                }

//                if(detectionList.size() > 2){
//                    int alldetectedFrames = 0;
//                    int allnonDetectedFrames = 0;
//                    for(Integer d : detectionList){
//                        if(d == 1){
//                            alldetectedFrames += 1;
//                        }else if(d == 0){
//                            allnonDetectedFrames+=1;
//                        }
//
//                    }
//                    if(alldetectedFrames > 0){
//                        ///// detected
//                        detectedString = detectedString + "1";
//                        Log.d("FLASH", "1 DETECTED .................");
//                        detectionList.removeAll(detectionList);
//                    }else if(allnonDetectedFrames > 2){
//                        detectedString = detectedString + "0";
//                        Log.d("FLASH", "0 DETECTED ..................");
//                        detectionList.removeAll(detectionList);
//                    }else{
//                        detectionList.remove(0);
//                    }
////                    detectionList.removeAll(detectionList);
////                    Log.d("FLASH", detectedString);
//                }


                // testing ...................
//                if(frames == 100){
//                    Intent intent = new Intent(CameraScreen.this, MainActivity.class);
//                    intent.putExtra("key", detectedString);
//                    startActivity(intent);
//                }

                // Start and End Establishing ....................
//                if(detectedString.length() >= 8){
//                    int strLength = detectedString.length();
//                    String lastByte = detectedString.substring(strLength-8, strLength);
//
//                    // not started
//                    if(!readingStarted){
//                        readingStarted = true;
//                        detectedString = "";
//                    }
//
//                    // ending
//                    else{
//                        Log.d("Frames", detectedString);
//                        Intent intent = new Intent((Context) context, MainActivity.class);
//                        intent.putExtra("key", detectedString);
//                        startActivity(intent);
//                    }
//
//                }





                // end of on camera frame
                return bw;
            }

        });

//        if(OpenCVLoader.initDebug()) {
//            cameraBridgeViewBase.enableView();
//            try {
//                InputStream inputStream = getResources().openRawResource(R.raw.lbpcascade_frontalface);
//                File file = new File(getDir("cascade", MODE_PRIVATE), "lbpcascade_frontalface.xml");
//                FileOutputStream fileOutputStream = new FileOutputStream(file);
//            }
//            catch (FileNotFoundException e){
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraBridgeViewBase.enableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        List<?> a = Collections.singletonList(cameraBridgeViewBase);
        String liststring = "";
        for (int i = 0; i < a.size(); i++){
            liststring = liststring+ a.get(i);
        }
        Log.d("FLASH", liststring);
        return Collections.singletonList(cameraBridgeViewBase);
    }

    void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 3);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==3 && grantResults.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                getPermission();
            }
        }
    }
}