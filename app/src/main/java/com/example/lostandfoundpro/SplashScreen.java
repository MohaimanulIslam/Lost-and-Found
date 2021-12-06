package com.example.lostandfoundpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class SplashScreen extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        Hide the tool bar from this activity
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();

        setContentView(R.layout.splashscreen);

        progressBar = findViewById(R.id.progressBarId);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                go();
                mynewscreen();

            }

            private void mynewscreen() {
                Intent i = new Intent(getApplicationContext(),LogIn.class);
                startActivity(i);}
        });
        thread.start();
    }

    private  void  go(){
        try {
            Thread.sleep(1000);
            progressBar.getClass();

        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

}