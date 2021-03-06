package com.example.messiah.questjournal;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SettingTabActivity extends AppCompatActivity {

    public static boolean  prefSound = true;
    static boolean prefMusic = true;

    Typeface type;

    public void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        prefMusic = sharedPreferences.getBoolean("setSound", false);
        prefSound = sharedPreferences.getBoolean("setMusic", false);
    }

    public void savePreferences(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_tab);

        type = Typeface.createFromAsset(getAssets(),"fonts/pixel_font.ttf");
        TextView settingName = (TextView) findViewById(R.id.settings_name);
        TextView settingNick = (TextView) findViewById(R.id.settings_nickname);
        TextView soundS = (TextView) findViewById(R.id.switchSound);
        TextView musicS = (TextView) findViewById(R.id.switchMusic);
        TextView button = (TextView) findViewById(R.id.button);

        button.setTypeface(type);
        soundS.setTypeface(type);
        musicS.setTypeface(type);
        settingNick.setTypeface(type);
        settingName.setTypeface(type);

        String nickname_ref = "https://questjournal.firebaseio.com/users/" + MainActivity.UID + "/nickname";

        Firebase ref = new Firebase(nickname_ref);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                EditText textview_nickname = (EditText) findViewById(R.id.settings_nickname);
                textview_nickname.setText("");
                textview_nickname.setHint("Change nickname: " + snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        Switch music = (Switch) findViewById(R.id.switchMusic);
        music.setChecked(prefMusic);
        music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CharacterActivity.myMusic.start();
                    CharacterActivity.myMusic.setLooping(true);
                    prefMusic = true;
                } else {
                    // The toggle is disabled
                    CharacterActivity.myMusic.pause();
                    prefMusic = false;
                }
            }
        });

        Switch sound = (Switch) findViewById(R.id.switchSound);
        sound.setChecked(prefSound);
        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prefSound = true;
                    CharacterActivity.mySound.start();
                } else {
                    prefSound = false;
                }
            }
        });
    }
    public void updateNickname(View view){
        EditText newName = (EditText) findViewById(R.id.settings_nickname);
        String nicknameref = "https://questjournal.firebaseio.com/users/" + MainActivity.UID;
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if(newName.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please enter a new name", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("auth", nicknameref);
        Firebase ref = new Firebase(nicknameref);
//        Firebase nicknameRef = ref;
        Map<String, Object> nickname = new HashMap<String, Object>();
        nickname.put("nickname", newName.getText().toString());
        ref.updateChildren(nickname);

        TabActivity tabs = (TabActivity) getParent();
        tabs.getTabHost().setCurrentTab(0);
    }
    public void logOut(View view){
        MainActivity.ref.unauth();
        Toast.makeText(getApplicationContext(), "logged out", Toast.LENGTH_SHORT).show();
        Log.i("auth", "logged out");
        Intent newIntent = new Intent(SettingTabActivity.this, MainActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newIntent);
        finish();
    }
}