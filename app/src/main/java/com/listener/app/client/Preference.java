package com.listener.app.client;

import android.os.Bundle;

import android.preference.PreferenceActivity;

import com.cai.chat_05.R;


public class Preference extends PreferenceActivity {

 

    /** Called when the activity is first created. */
 
    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.preferences);

    }

}
