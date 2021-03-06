package com.example.messiah.questjournal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Firebase ref used to reference our Firebase backend
    public static Firebase ref;
    public static String UID;
    int createUserClick = 0;

    Typeface type;

    // on create establish firebase connection
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        type = Typeface.createFromAsset(getAssets(),"fonts/pixel_font.ttf");
        TextView user = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);
        TextView character = (TextView) findViewById(R.id.character);
        TextView pass2 = (TextView) findViewById(R.id.password2);

        pass2.setTypeface(type);
        user.setTypeface(type);
        password.setTypeface(type);
        character.setTypeface(type);

        // Sets the Android Context for Firebase

        //enables offline capabilities
        //Firebase.getDefaultConfig().setPersistenceEnabled(true);

        Firebase.setAndroidContext(this);

        // Creates a new Firebase ref using specified path
        ref = new Firebase("https://questjournal.firebaseio.com/");

        if(ref.getAuth() != null){
            AuthData authData = ref.getAuth();
            UID = authData.getUid().toString();

            Intent newIntent = new Intent(MainActivity.this, CharacterActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
            finish();
        }
    }

    // sign in
    public void signIn(View v) {
        TextView user = (TextView) findViewById(R.id.username);
        TextView status = (TextView) findViewById(R.id.password);
//      final TextView nickname = (TextView) findViewById(R.id.character);

        ref.authWithPassword(user.getText().toString(), status.getText().toString(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                UID = authData.getUid().toString();

                Toast.makeText(getApplicationContext(), "Logged in successfully.", Toast.LENGTH_SHORT).show();
                Intent newIntent = new Intent(MainActivity.this, CharacterActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
                finish();
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
                Toast.makeText(getApplicationContext(), "Failed to log in, please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //create account
    public void create(final View v){

        TextView user = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);

        final TextView character = (TextView) findViewById(R.id.character);

        TextView pass2 = (TextView) findViewById(R.id.password2);
        ImageButton logIn = (ImageButton) findViewById(R.id.login_button);
        ImageButton create = (ImageButton) findViewById(R.id.create_button);

        if(createUserClick == 0){
            Log.i("create", "entered if");
            logIn.setVisibility(View.INVISIBLE);
            create.animate().translationYBy(100f).setDuration(400);
            character.setVisibility(View.VISIBLE);
            pass2.setVisibility(View.VISIBLE);
            createUserClick = 1;
            return;
        }
        if (createUserClick == 1) {
            if (user.getText().toString().equals("") | password.getText().toString().equals("") | character.getText().toString().equals("") | pass2.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            } else if(!password.getText().toString().equals(pass2.getText().toString())){
                Toast.makeText(getApplicationContext(), "Passwords did not match.", Toast.LENGTH_SHORT).show();
            } else {
                ref.createUser(user.getText().toString(), password.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        Log.i("auth", "Successfully created user account with uid: " + result.get("uid"));
                        Map<String, String> map = new HashMap<String, String>();
//                        Map<String, Integer> expMap = new HashMap<String, Integer>();
                        map.put("nickname", character.getText().toString());
                        map.put("exp", "0");
                        ref.child("users").child(result.get("uid").toString()).setValue(map);

                        Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
                        signIn(v);
                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                        Toast.makeText(getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
