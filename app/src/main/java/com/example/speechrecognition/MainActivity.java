package com.example.speechrecognition;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.icu.text.StringSearch;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.os.UserHandle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    //views from activity
    TextView mTextTv;
    TextView mTextResponses;
    ImageButton mVoiceBtn;
    List<String> wordBank = new ArrayList<>();
    List<String> responses = new ArrayList<>();

    boolean word = false;
    private BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextTv = (TextView) findViewById(R.id.textTv);
        mTextResponses = (TextView) findViewById(R.id.textTv);
        mVoiceBtn = (ImageButton) findViewById(R.id.voiceBtn);

        //create array list and connect it to xml file
        wordBank = Arrays.asList(getResources().getStringArray(R.array.Words));
        responses = Arrays.asList(getResources().getStringArray(R.array.responses));

        //button click to show speech to text dialog
        mVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    Fragment fragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.Home:
                            fragment = new HomeFragment();
                            break;
                        case R.id.Mic:
                            fragment = new MicFragment();
                            break;
                        case R.id.About:
                            fragment = new AboutFragment();
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

                    return true;
                }

            };


    public void speak() {
        //intent to show speech to text dialog
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi say something");

        //start intent
        try {
            //in there was no error
            //show dialog
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            //if there was some error
            //get message of error and show
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //this is the counter for the responses loop
        int responseItem =0;
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
            ArrayList<String> result = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mTextTv.setText(result.get(0).toUpperCase());
            //loops every word in a phrase until it keyword from wordBank
            for(String wordItem: wordBank) {
                if (result.get(0).contains(wordItem)) {
                    mTextResponses.setText(responses.get(responseItem).toString());
                    break;
                }
                //item +1
                //this moves to the next word, and it will check if there is key word in word item
                responseItem++;
            }
        }
    }
}