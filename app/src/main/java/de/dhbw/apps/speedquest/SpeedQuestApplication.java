package de.dhbw.apps.speedquest;

import android.app.Application;
import android.util.Log;

import java.util.function.Consumer;
import java.util.function.Function;

import de.dhbw.apps.speedquest.client.SpeedQuestClient;

public class SpeedQuestApplication extends Application {

    public final SpeedQuestClient client = new SpeedQuestClient(this);

    public SpeedQuestApplication() {
        client.getGameCache().register();
    }

}
