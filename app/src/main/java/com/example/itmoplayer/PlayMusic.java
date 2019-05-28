package com.example.itmoplayer;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PlayMusic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        final Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        final String MusicTitle = getIntent().getExtras().getString("MusicTitle");
        final String Singer = getIntent().getExtras().getString("Singer");
        final Bitmap artist_image = (Bitmap) getIntent().getParcelableExtra("BitmapImage");
        final ImageView music_image = (ImageView) findViewById(R.id.image_music);
        final TextView music_name = (TextView) findViewById(R.id.music_name);
        final TextView artist_name = (TextView) findViewById(R.id.artist_name);
        final TextView play_music = (TextView) findViewById(R.id.play_music);
        final TextView pause_music = (TextView) findViewById(R.id.pause_music);
        final TextView next_music = (TextView) findViewById(R.id.next_music);
        final TextView back_music = (TextView) findViewById(R.id.back_music);

        play_music.setTypeface(fontAwesomeFont);
        pause_music.setTypeface(fontAwesomeFont);
        next_music.setTypeface(fontAwesomeFont);
        back_music.setTypeface(fontAwesomeFont);

        music_image.setImageBitmap(artist_image);
        music_name.setText(MusicTitle);
        artist_name.setText(Singer);
    }
}
