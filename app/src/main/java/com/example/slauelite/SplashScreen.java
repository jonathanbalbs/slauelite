package com.example.slauelite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.view.Window;

public class SplashScreen extends AppCompatActivity {

    private static boolean splashLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
        }
        if (!splashLoaded){
            setContentView(R.layout.activity_splash_screen);

            final int secondsDelayed = 5;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyDatabaseHandler dbHandler = new MyDatabaseHandler(getApplicationContext());
                    SQLiteDatabase db = dbHandler.getReadableDatabase();
                    UserContract uc = new UserContract();

                    Cursor cursor = db.query(uc.getTableName(), null, null, null, null, null, null);

                    if (cursor.getCount() > 0) {
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, secondsDelayed * 1000);

            splashLoaded = true;
        } else {
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }

    }
}
