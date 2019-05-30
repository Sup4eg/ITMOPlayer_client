package com.example.itmoplayer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class MusicResults extends AppCompatActivity {

    public static ImageView image;
    public static String[] user_db_properties = new String[1];
    public static CountDownLatch latch;
    static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_results);

        final Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        final EditText music_request = (EditText) findViewById(R.id.music_request_results);
        final TextView music_search_image = (TextView) findViewById(R.id.font_awesome_search);
        final TextView search_view = (TextView) findViewById(R.id.search_view);
        final String request = getIntent().getExtras().getString("request");
        user_db_properties[0] = request;
        image = (ImageView) findViewById(R.id.image);

        music_request.setText(request);
        music_search_image.setTypeface(fontAwesomeFont);

        latch = new CountDownLatch(1);
        GetMusicData load_request = new GetMusicData();
        load_request.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        music_search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText current_request = (EditText) findViewById(R.id.music_request_results);
                String request_str = current_request.getText().toString();
                user_db_properties[0] = request_str;
                UpdateMusicData load_request2 = new UpdateMusicData();
                load_request2.start();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        ListView androidListView = (ListView) findViewById(R.id.result_list);

        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView titleView = (TextView) view.findViewById(R.id.music_title);
                TextView singerView = (TextView) view.findViewById(R.id.singer_name);
                ImageView coverView = (ImageView) view.findViewById(R.id.cover);

                String MusicTitle = titleView.getText().toString();
                String singer = singerView.getText().toString();

                coverView.buildDrawingCache();
                Bitmap bitmap = coverView.getDrawingCache();

                Intent intent = new Intent(MusicResults.this, PlayMusic.class);
                intent.putExtra("BitmapImage", bitmap);
                intent.putExtra("MusicTitle", MusicTitle);
                intent.putExtra("Singer", singer);

                startActivity(intent);
            }
        });


    }


    public class UpdateMusicData extends Thread {

        @TargetApi(Build.VERSION_CODES.O)
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            try {
                Proxy proxy = new Proxy();
                String msg = proxy.mainMain("get_music_information", user_db_properties);
                Gson gson = new Gson();
                Map map = gson.fromJson(msg, Map.class);
                String result = map.get("result").toString();
                Map music_data = gson.fromJson(result, Map.class);
                String singers_str = music_data.get("singers").toString();
                String albums_str = music_data.get("albums").toString();
                String songs_str = music_data.get("songs").toString();

                Message message = Message.obtain(); // Creates an new Message instance
                Bundle bundle = new Bundle();
                bundle.putString("key1", singers_str);
                bundle.putString("key2", albums_str);
                bundle.putString("key3", songs_str);

                message.setData(bundle);

                handler.sendMessage(message);

                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class GetMusicData extends Thread {


        @RequiresApi(api = Build.VERSION_CODES.O)
        public void update_adapter(String singer, String albums, String songs)		//Инициирует завершение потока
        {
            try {
                ListView musicListViewsetAdapter = setAdapter(singer, albums, songs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @TargetApi(Build.VERSION_CODES.O)
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
                try {
                    Proxy proxy = new Proxy();
                    String msg = proxy.mainMain("get_music_information", user_db_properties);
                    Gson gson = new Gson();
                    Map map = gson.fromJson(msg, Map.class);
                    String result = map.get("result").toString();
                    Map music_data = gson.fromJson(result, Map.class);
                    String singers_str = music_data.get("singers").toString();
                    String albums_str = music_data.get("albums").toString();
                    String songs_str = music_data.get("songs").toString();
                    ListView musicListViewsetAdapter = setAdapter(singers_str, albums_str, songs_str);

                    handler = new Handler(Looper.getMainLooper()) {
                        public void handleMessage(Message update_music) {
                            Bundle bundle = update_music.getData();

                            String singer = bundle.getString("key1");
                            String albums = bundle.getString("key2");
                            String songs = bundle.getString("key3");
                            try {
                                ListView musicListViewsetAdapter = setAdapter(singer, albums, songs);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ListView setAdapter(String singer, String albums, String songs) throws IOException {

        singer = singer.replaceAll("=", ":").replaceAll("\\[", "").replaceAll("\\]", "");
        String[] singer_arr = singer.split(",");

        albums = albums.replaceAll("=", ":").replaceAll("\\[", "").replaceAll("\\]", "");
        String[] albums_arr = albums.split(",");
        System.out.println(albums_arr.length);

        songs = songs.replaceAll("=", ":").replaceAll("\\[", "").replaceAll("\\]", "");
        String[] songs_arr = songs.split(",");

        List<String> MusicTitle = new ArrayList<String>();
        List<String> SingerName = new ArrayList<String>();
        List<Bitmap> listViewImage = new ArrayList<Bitmap>();


        for (int i = 0; i < singer_arr.length / 2; i = i + 2) {
            MusicTitle.add("Artist");
            SingerName.add(singer_arr[i]);
            String string_image = singer_arr[i + 1].replaceAll("b'", "").replaceAll("\\'", "");

            byte[] decodedImage = Base64.decode(string_image, Base64.DEFAULT);

            Bitmap decodedByteImage = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
            listViewImage.add(decodedByteImage);
        }

        for (int i = 0; i < albums_arr.length / 3; i = i + 3) {
            MusicTitle.add(albums_arr[i]);
            SingerName.add(albums_arr[i + 1]);
            String string_image = albums_arr[i + 2].replaceAll("b'", "").replaceAll("\\'", "");

            byte[] decodedImage = Base64.decode(string_image, Base64.DEFAULT);

            Bitmap decodedByteImage = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
            listViewImage.add(decodedByteImage);
        }

        for (int i = 0; i < songs_arr.length / 3; i = i + 3) {
            MusicTitle.add(songs_arr[i]);
            SingerName.add(songs_arr[i + 1]);
            String string_image = songs_arr[i + 2].replaceAll("b'", "").replaceAll("\\'", "");

            byte[] decodedImage = Base64.decode(string_image, Base64.DEFAULT);

            Bitmap decodedByteImage = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
            listViewImage.add(decodedByteImage);
        }


        List<HashMap<String, Object>> aList = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < MusicTitle.size(); i++) {
            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("title", MusicTitle.get(i));
            hm.put("singer_name", SingerName.get(i));
            hm.put("listview_image", listViewImage.get(i));
            aList.add(hm);
        }


        String[] from = {"listview_image", "title", "singer_name",};
        int[] to = {R.id.cover, R.id.music_title, R.id.singer_name};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.list_music, from, to);
        ListView androidListView = (ListView) findViewById(R.id.result_list);

        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if ((view instanceof ImageView) & (data instanceof Bitmap)) {
                    ImageView iv = (ImageView) view;
                    Bitmap bm = (Bitmap) data;
                    iv.setImageBitmap(bm);
                    return true;
                }
                return false;

            }

        });

        androidListView.setAdapter(simpleAdapter);
        return androidListView;
    }

}
