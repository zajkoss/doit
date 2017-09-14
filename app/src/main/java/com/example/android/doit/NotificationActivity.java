package com.example.android.doit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;


public class NotificationActivity extends AppCompatActivity {

    SharedPreferences prefs;
    public String whenPref = "com.example.android.doit.when";
    public  String doNotiPref = "com.example.android.doit.doNotification";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        prefs = this.getSharedPreferences("com.example.android.doit", Context.MODE_PRIVATE);

        setSwitch();
        setRadioGroup();

    }

    private void setRadioGroup(){
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio);
        long time = prefs.getLong(whenPref,900000);

        switch ((int)time){
            case 300000: radioGroup.check(R.id.radio_5); break;
            case 900000: radioGroup.check(R.id.radio_15); break;
            case 1800000: radioGroup.check(R.id.radio_30); break;
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                long w = 0;
                if(checkedId == R.id.radio_5){
                    w = 300000;
                }else if(checkedId == R.id.radio_15){
                    w = 900000;
                }else if(checkedId == R.id.radio_30){
                    w = 1800000;
                }
                prefs.edit().putLong(whenPref,w).commit();

            }
        });
    }

    private void setSwitch(){

        final Switch _switch = (Switch) findViewById(R.id.switch_not);
       boolean check = prefs.getBoolean(doNotiPref,true);
        if(check){
            _switch.setChecked(true);
        }else{
            _switch.setChecked(false);
        }
        _switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean(doNotiPref,_switch.isChecked()).apply();

            }
        });

    }
}
