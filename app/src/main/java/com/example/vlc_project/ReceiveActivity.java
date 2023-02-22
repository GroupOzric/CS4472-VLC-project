package com.example.vlc_project;

import android.app.Activity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ReceiveActivity extends Activity {

    Button button;
    TextView textView1;
//    Button button2;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("MAINX", "addlist");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        addListenerOnButton();

        textView1 = findViewById(R.id.textView1);
        try{
            String data = getIntent().getStringExtra("key");
            if(data.length() > 0){
                Log.d("MAINX", data);
                textView1.setText(data);
            }
        }
        catch (Exception e){
            Log.d("MAINX", "ERROR");
        }



    }

    public void addListenerOnButton() {
        Log.d("LOADEDE", "addlist");
        final Context context = this;

        button = (Button) findViewById(R.id.button1);
//        button2 = (Button) findViewById(R.id.dbview);
        Log.d("LOADEDE", "addlist");
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.d("FLASH", "Onlclick");
                Intent intent = new Intent(context, CameraScreen.class);
                startActivity(intent);

            }

        });
//        button2.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                Log.d("LOADED", "Onlclick");
//                Intent intent = new Intent(MainActivity.this, addActivity.class);
//                startActivity(intent);
//
//            }
//
//        });

    }
}