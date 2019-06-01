package com.example.itmoplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RecentlyPlayed extends AppCompatActivity {
    static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_played);

        final Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        final TextView go_to_user = (TextView) findViewById(R.id.go_to_user);
        final TextView go_to_search = (TextView) findViewById(R.id.go_to_search);
        final TextView go_to_clock = (TextView) findViewById(R.id.go_to_clock);

        ListView androidListView = (ListView) findViewById(R.id.recently_list);

        go_to_clock.setTypeface(fontAwesomeFont);
        go_to_search.setTypeface(fontAwesomeFont);
        go_to_user.setTypeface(fontAwesomeFont);

        ShowRecentlyPlayed thread_1 = new ShowRecentlyPlayed();
        thread_1.start();

        handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message update_music) {
                Bundle bundle = update_music.getData();

                String result = bundle.getString("key1");

                String[] parse_string = result.split(":")[1].replaceAll("\\[", "").replaceAll("\\]", "")
                        .replaceAll("\"", "")
                        .split(",");
                setAdapter(parse_string);
            }
        };

        go_to_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecentlyPlayed.this, UserActivity.class);
                intent.putExtra("login", MainActivity.session.get_user());
                startActivity(intent);
            }
        });

        go_to_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecentlyPlayed.this, MusicResults.class);
                intent.putExtra("request", "Search your music here");
                startActivity(intent);
            }
        });


        androidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView titleView = (TextView) view.findViewById(R.id.recently_music_title);
                TextView singerView = (TextView) view.findViewById(R.id.recently_singer_name);
                ImageView coverView = (ImageView) view.findViewById(R.id.recently_cover);

                String MusicTitle = titleView.getText().toString();
                String singer = singerView.getText().toString();

                coverView.buildDrawingCache();
                Bitmap bitmap = coverView.getDrawingCache();

                Intent intent = new Intent(RecentlyPlayed.this, PlayMusic.class);
                System.out.println(MusicTitle);
                intent.putExtra("BitmapImage", bitmap);
                intent.putExtra("MusicTitle", MusicTitle);
                intent.putExtra("Singer", singer);

                startActivity(intent);
            }
        });


    }

    public class ShowRecentlyPlayed extends Thread{
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            Proxy proxy = new Proxy();
            String[] user_arr = {MainActivity.session.get_user()};
            String msg = null;
            try {
                msg = proxy.mainMain("get_music", user_arr);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            Map map = gson.fromJson(msg, Map.class);
            String result = map.get("result").toString();
            if (!result.equals("None")) {
                String[] parse_string = result.split(":")[1].replaceAll("\\[", "").replaceAll("\\]", "")
                        .split(",");

                Message message = Message.obtain(); // Creates an new Message instance
                Bundle bundle = new Bundle();
                bundle.putString("key1", result);

                message.setData(bundle);
                handler.sendMessage(message);
            }

        }
    }
    public void setAdapter(String[] parse_string){
        List<String> MusicTitle = new ArrayList<String>();
        List<String> SingerName = new ArrayList<String>();
        List<Bitmap> listViewImage = new ArrayList<Bitmap>();
        for (int i = 0; i < parse_string.length; i = i + 3) {
            MusicTitle.add(parse_string[i]);
            SingerName.add(parse_string[i+1]);
            String song_image = parse_string[i+2].replaceAll("b'", "").replaceAll("\\'", "");
            byte[] decodedImage = Base64.decode(song_image, Base64.DEFAULT);

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
        int[] to = {R.id.recently_cover, R.id.recently_music_title, R.id.recently_singer_name};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.recently_list, from, to);
        ListView androidListView = (ListView) findViewById(R.id.recently_list);

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

    }
}
