package com.aaphilip.tools.autowifilogin.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aaphilip.tools.autowifilogin.R;
import com.aaphilip.tools.autowifilogin.network.Requester;
import com.aaphilip.tools.autowifilogin.util.Constants;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loginClicked(View v) {
        Requester.ResultHandler resultHandler = new Requester.ResultHandler() {
            @Override
            public void onError(IOException e) {
                Toast.makeText(
                        MainActivity.this,
                        "Are you sure you are connected to Guest Wifi? Try Again.",
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onSuccess(String response) {
                Toast.makeText(MainActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String response) {
                Toast.makeText(MainActivity.this, "Already logged in ;) ;) ;)", Toast.LENGTH_SHORT)
                        .show();
            }
        };

        new Requester(resultHandler).makeAsyncRequest(Constants.INTUIT_USERNAME,
                Constants.INTUIT_PASSWORD);
    }
}
