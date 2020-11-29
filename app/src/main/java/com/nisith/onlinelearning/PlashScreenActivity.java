package com.nisith.onlinelearning;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class PlashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plash_screen);
        final CircleImageView appImageView = findViewById(R.id.app_icon_image_view);
        final TextView textview1 = findViewById(R.id.text_view1);
        final TextView textView2 = findViewById(R.id.text_view2);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        YoYo.with(Techniques.FadeIn)
                                .duration(3000)
                                .repeat(0)
                                .playOn(appImageView);
                        YoYo.with(Techniques.RubberBand)
                                .duration(1000)
                                .repeat(12)
                                .playOn(textview1);
                        YoYo.with(Techniques.RubberBand)
                                .duration(1000)
                                .repeat(12)
                                .playOn(textView2);

                    }
                });
                SystemClock.sleep(3000);
                startActivity(new Intent(PlashScreenActivity.this, HomeActivity.class));
                finish();

            }
        });
        thread.start();
    }


}