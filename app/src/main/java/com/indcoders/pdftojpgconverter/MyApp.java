package com.indcoders.pdftojpgconverter;

import android.app.Application;

/**
 * Created by Akki on 01/08/15.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "SERIF", "noto_reg.ttf");
    }
}
