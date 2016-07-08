package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;


public class AuthActivity extends BaseActivity{

    private EditText mLoginField;
    private EditText mPassField;
    private Button mButton;
    private TextView mForgetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mLoginField = (EditText)findViewById(R.id.email_field);
        mPassField = (EditText)findViewById(R.id.pass_field);
        mButton = (Button)findViewById(R.id.enter_button);
        mForgetPass = (TextView)findViewById(R.id.forget_pass);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });

    }

    private void startMainActivity(){
        Intent intentActivity = new Intent(this,MainActivity.class);
        startActivity(intentActivity);
        finish();
    }
}