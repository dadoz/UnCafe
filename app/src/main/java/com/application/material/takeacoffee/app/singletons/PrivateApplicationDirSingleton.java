package com.application.material.takeacoffee.app.singletons;

import android.app.Activity;
import android.content.Context;

import java.io.File;

/**
 * Created by davide on 06/01/15.
 */
public class PrivateApplicationDirSingleton {
    private static PrivateApplicationDirSingleton instance;
    private final String COFFEE_MACHINE_DIR = "takeacoffeePrivateDirectory";
    private static File privateAppDirectory;
    private static Context context;

    private PrivateApplicationDirSingleton() {
        privateAppDirectory = ((Activity) context).getApplication().getApplicationContext()
                .getDir(COFFEE_MACHINE_DIR, Context.MODE_PRIVATE); //Creating an internal dir;
    }

    public static File getDir(Context ctx) {
        context = ctx;
        if(instance == null) {
            instance = new PrivateApplicationDirSingleton();
        }
        return privateAppDirectory;
    }

}
