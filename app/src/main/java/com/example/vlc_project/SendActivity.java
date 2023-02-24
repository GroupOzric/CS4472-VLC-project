package com.example.vlc_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Collections;

public class SendActivity extends AppCompatActivity {

    TextInputEditText textInputEditText;
    Button sendButton;
    TextView textView;
    ProgressBar progressBar;
    int counter = 0;
    private CameraManager cameraManager;
    private String cameraId;
//    private static final long BIT_DURATION = 300;
    private static final long BIT_DURATION_1 = 50;
    private static final long BIT_DURATION_0 = 250;
    int data_length =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        textInputEditText = (TextInputEditText) findViewById(R.id.textInputEditText);
        sendButton = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.sendPreview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        textView.setText("Enter Message to Send !");
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("Sending data....");
                progressBar.setVisibility(View.VISIBLE);
                String data = String.valueOf(textInputEditText.getText());
//                sendAsciiData(data);
                new SendDataAsyncTask().execute(data);
            }
        });
    }

    private class SendDataAsyncTask extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String data = params[0];

            // Send data here and update progress
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int data_length = data.length();
            String ascii_data_length = Character.toString((char) data_length);
            data = ascii_data_length + data;
            System.out.println(data);
            for (int i = 0; i < data.length(); i++) {
                char c = data.charAt(i);
                sendAsciiData(Character.toString(c));
                publishProgress((i+1)*100/data.length());
            }
            turnFlashlightOff();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Update progress bar here
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            // Update UI after sending data
            textView.setText("Data Sent Successfully!");
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setProgress(0);

            textView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Enter Message to Send");
                }
            }, 5000);
        }

    }

    private void sendAsciiData(String data) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            }
            cameraId = cameraManager.getCameraIdList()[0]; // use the first camera
            for (int i = 0; i < data.length(); i++) {
                char c = data.charAt(i);
                String binaryString = Integer.toBinaryString(c); // convert the character to binary string
                if(binaryString.length()<8){
                    binaryString =String.join("", Collections.nCopies((8-binaryString.length()), "0"))+binaryString;
                }
                System.out.println(String.format("%c : %s",c,binaryString));
                for (int j = 0; j < binaryString.length(); j++) {
                    if (binaryString.charAt(j) == '1') {
                        turnFlashlightOn();
                        Thread.sleep(BIT_DURATION_1);
                        turnFlashlightOff();
                        Thread.sleep(150);
                    } else if (binaryString.charAt(j) == '0') {
                        turnFlashlightOn();
                        Thread.sleep(BIT_DURATION_0);
                        turnFlashlightOff();
                        Thread.sleep(150);
                    }
                }
                counter++;
                progressBar.setProgress(counter);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void turnFlashlightOn() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true);
            } else {
                Camera camera = Camera.open();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void turnFlashlightOff() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false);
            } else {
                Camera camera = Camera.open();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                camera.stopPreview();
                camera.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}