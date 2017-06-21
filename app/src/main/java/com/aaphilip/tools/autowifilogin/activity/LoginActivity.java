package com.aaphilip.tools.autowifilogin.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aaphilip.tools.autowifilogin.R;
import com.aaphilip.tools.autowifilogin.util.Constants;
import com.aaphilip.tools.autowifilogin.util.Preferences;

public class LoginActivity  extends Activity {
    private EditText mUsernameEt;
    private EditText mPasswordEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Preferences.getPreferences(this).isLocallyAuthenticated()) {
            doSuccessfulLogin();
            super.onBackPressed();
        }
        setContentView(R.layout.activity_login);
        mUsernameEt = (EditText) findViewById(R.id.et_username);
        mPasswordEt = (EditText) findViewById(R.id.et_password);
    }

    @SuppressWarnings("UnusedParameters") // is necessitated by function signature
    public void onLoginClick(View v) {
        String username = mUsernameEt.getText().toString();
        String password = mPasswordEt.getText().toString();

        if (username.equalsIgnoreCase(Constants.INTUIT_USERNAME) &&
                password.equals(Constants.INTUIT_PASSWORD)) {
            doSuccessfulLogin();
        } else {
            doFailedLogin();
        }
    }

    private void doSuccessfulLogin() {
        // TODO: Add to preferences that login has happened
        Preferences.getPreferences(this).setLocallyAuthenticated();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Authenticated! Auto Wifi Login enabled!", Toast.LENGTH_SHORT).show();
    }

    private void doFailedLogin() {
        Toast.makeText(this, "Wrong username/password! Try again", Toast.LENGTH_SHORT).show();
    }
}
