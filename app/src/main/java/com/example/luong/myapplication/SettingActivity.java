package com.example.luong.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by luong on 4/2/2018.
 */

public class SettingActivity extends AppCompatActivity {

    static String ip_server = "None";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("DKMDKMDKMDKDMKDMDKMDKDM", "CLGTTTTTTTTTTTTTTTTTTTTTTTTT");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        Button button = (Button) findViewById(R.id.setting_button_done);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.input_server);
                ip_server = editText.getText().toString();
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
