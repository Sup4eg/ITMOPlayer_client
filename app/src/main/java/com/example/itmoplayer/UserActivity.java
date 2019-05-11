package com.example.itmoplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class UserActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        final String login = getIntent().getExtras().getString("login");
        final String[] user_db_properties = {login};
        final ImageView user_image = (ImageView) findViewById(R.id.image_user);
        final Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        final TextView clock_image = (TextView) findViewById(R.id.font_awesome_clock);
        final TextView music_image = (TextView) findViewById(R.id.font_awesome_music);
        final TextView music_user = (TextView) findViewById(R.id.font_awesome_user);
        final TextView user_name = (TextView) findViewById(R.id.user_name);
        clock_image.setTypeface(fontAwesomeFont);
        music_image.setTypeface(fontAwesomeFont);
        music_user.setTypeface(fontAwesomeFont);


        final CountDownLatch latch = new CountDownLatch(1);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Proxy proxy = new Proxy();
                    String msg = proxy.mainMain("get_account_details", user_db_properties);
                    Gson gson = new Gson();
                    Map map = gson.fromJson(msg, Map.class);
                    String result = map.get("result").toString();
                    Map user_data = gson.fromJson(result, Map.class);
                    String image = user_data.get("Image").toString();
                    user_image.setImageURI(Uri.parse(String.valueOf(image)));
                    final String f_name = user_data.get("First_name").toString();
                    final String s_name = user_data.get("Second_name").toString();
                    user_name.setText(f_name + " " + s_name);
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}



