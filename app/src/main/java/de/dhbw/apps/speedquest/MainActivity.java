package de.dhbw.apps.speedquest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import de.dhbw.apps.speedquest.client.SpeedQuestClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tryConnect();
    }

    private void tryConnect() {
        SpeedQuestApplication app = (SpeedQuestApplication)getApplication();
        app.client.connect("194.62.29.124", 4430, "Hans", "WNGJ");
    }
}