package com.example.slauelite;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    private static boolean splashLoaded = false;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!splashLoaded){
            setContentView(R.layout.activity_splash_screen);

            imageView = findViewById(R.id.useLogo);

            imageView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.expand_image));

            final int secondsDelayed = 4;
            new Handler().postDelayed(() -> {
                startActivity(setRoute());
                finish();
            }, secondsDelayed * 1000);

            splashLoaded = true;
        } else {
            startActivity(setRoute());
            finish();
        }
    }

    public Intent setRoute() {
        Intent intent;

        MyDatabaseHandler dbHandler = new MyDatabaseHandler(getApplicationContext());
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        UserContract uc = new UserContract();

        Cursor cursor = db.query(uc.getTableName(), null, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            intent = new Intent(SplashScreen.this, MainActivity.class);
        }else {
            intent = new Intent(SplashScreen.this, LoginActivity.class);
        }
        cursor.close();
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        return intent;
    }
}
