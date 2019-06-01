package com.example.itmoplayer;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class PlayMusic extends AppCompatActivity {
    static Handler handler;
    static Handler progress_handler;
    static String[] music_request = new String[2];
    static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        final String MusicTitle = getIntent().getExtras().getString("MusicTitle");
        final String Singer = getIntent().getExtras().getString("Singer");
        final Bitmap artist_image = getIntent().getParcelableExtra("BitmapImage");
        music_request[0] = Singer;
        music_request[1] = MusicTitle;
        final ImageView music_image = findViewById(R.id.image_music);
        final TextView music_name = findViewById(R.id.music_name);
        final TextView artist_name = findViewById(R.id.artist_name);

        music_image.setImageBitmap(artist_image);
        music_name.setText(MusicTitle);
        artist_name.setText(Singer);


        StartMusic main_thread = new StartMusic();
        main_thread.start();

    }


    public class ProgressBar extends Thread {
        @Override
        public void run() {
            int current_time = mediaPlayer.getCurrentPosition() / 1000;
            while (true) {
                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("key1", String.valueOf(current_time));
                message.setData(bundle);
                progress_handler.sendMessage(message);
                current_time = mediaPlayer.getCurrentPosition() / 1000;
//                music_lenght = mediaPlayer.getDuration() / 1000;
            }
        }
    }

    public class StartMusic extends Thread{

        @Override
        public void run() {

            final Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

            final TextView play_music = findViewById(R.id.play_music);
            final TextView pause_music = findViewById(R.id.pause_music);
            final TextView next_music = findViewById(R.id.next_music);
            final TextView back_music = findViewById(R.id.back_music);
            final SeekBar progress_control = findViewById(R.id.progressControl);

            play_music.setTypeface(fontAwesomeFont);
            pause_music.setTypeface(fontAwesomeFont);
            next_music.setTypeface(fontAwesomeFont);
            back_music.setTypeface(fontAwesomeFont);

            Proxy proxy = new Proxy();
            try {
                String msg = proxy.mainMain("get_music_composition", music_request);
                String[] store_music_information = {music_request[0], music_request[1], MainActivity.session.get_user()};
                proxy.mainMain("store_music", store_music_information);
                Gson gson = new Gson();
                Map map = gson.fromJson(msg, Map.class);
                String result = map.get("result").toString();
                Map music_map = gson.fromJson(result, Map.class);
                String song = music_map .get("song").toString().replaceAll("b'", "").replaceAll("\\'", "");
                byte[] decodedSong = Base64.decode(song, Base64.DEFAULT);
                mediaPlayer = new MediaPlayer();
                String music_name = music_request[1];
                String music_extension = music_name.substring(music_name.lastIndexOf("."));
                String music_title = music_name.substring(0, music_name.lastIndexOf("."));

                handler = new Handler(Looper.getMainLooper()) {
                    public void handleMessage(Message update_music) {
                        Bundle bundle = update_music.getData();
                        String update = bundle.getString("key1");
                        assert update != null;
                        if (update.equals("toPause")) {
                            play_music.setText("");
                            pause_music.setText(R.string.font_awesome_pause);
                        } else if (update.equals("toPlay")){
                            play_music.setText(R.string.font_awesome_play);
                            pause_music.setText("");
                        }

                    }
                };

                progress_handler = new Handler(Looper.getMainLooper()) {
                    public void handleMessage(Message update_music) {
                        Bundle bundle = update_music.getData();
                        int bur_position = Integer.parseInt(bundle.getString("key1"));
                        progress_control.setMax(mediaPlayer.getDuration() / 1000);
                        progress_control.setProgress(bur_position);
                    }
                };

                playMusicFile(decodedSong, mediaPlayer, music_title, music_extension);

                ProgressBar bar_thread = new ProgressBar();
                bar_thread.start();

                System.out.println(mediaPlayer.getDuration());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopPlay(mediaPlayer);
                        progress_control.setProgress(0);
                    }
                });

                progress_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                       progress_control.setMax(mediaPlayer.getDuration() / 1000);
                        if(fromUser)
                        {
                            mediaPlayer.seekTo(progress*1000);
                            System.out.println(mediaPlayer.getCurrentPosition());
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });


                pause_music.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.pause();
                        Message message = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("key1", "toPlay");
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                });

                play_music.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.start();
                        Message message = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putString("key1", "toPause");
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                });


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void playMusicFile(byte[] mp3SoundByteArray, MediaPlayer mediaPlayer, String song, String extension) {
            try {

                File tempMp3 = File.createTempFile(song, extension, getCacheDir());
                tempMp3.deleteOnExit();
                FileOutputStream fos = new FileOutputStream(tempMp3);
                fos.write(mp3SoundByteArray);
                fos.close();

                mediaPlayer.reset();

                FileInputStream fis = new FileInputStream(tempMp3);
                mediaPlayer.setDataSource(fis.getFD());

                mediaPlayer.prepare();

                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("key1", "toPause");
                message.setData(bundle);
                handler.sendMessage(message);
                mediaPlayer.start();

            } catch (IOException ex) {
                String s = ex.toString();
                ex.printStackTrace();
            }
        }

        private void stopPlay(MediaPlayer mPlayer){
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("key1", "toPlay");
            message.setData(bundle);
            handler.sendMessage(message);
            try {
                mPlayer.prepare();
                mPlayer.seekTo(0);
            }
            catch (Throwable t) {
                Toast.makeText(PlayMusic.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
