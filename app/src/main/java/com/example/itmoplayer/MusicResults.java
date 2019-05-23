package com.example.itmoplayer;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicResults extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_results);

        final Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        final EditText music_request = (EditText) findViewById(R.id.music_request_results);
        final TextView music_search_image = (TextView) findViewById(R.id.font_awesome_search);

        music_request.setText(getIntent().getExtras().getString("request"));
        music_search_image.setTypeface(fontAwesomeFont);


    }

    public void setAdapter() {

        String[] MusicTitle = new String[]{
//                "25", "Revival", "Kamikaze", "19",
//                "36", "ListView Title 6"
        };

        String[] SingerName = new String[]{
//                "Adele", "Eminem", "Ed Sheran", "Adele", "Eminem", "Ed Sheran"
        };

        int[] listViewImage = new int[]{
//                R.drawable.adele, R.drawable.adele, R.drawable.adele,
//                R.drawable.adele, R.drawable.adele, R.drawable.adele,
        };

        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 6; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("title", MusicTitle[i]);
            hm.put("singer_name", SingerName [i]);
            hm.put("listview_image", Integer.toString(listViewImage[i]));
            aList.add(hm);
        }


        String[] from = {"listview_image", "title", "singer_name", };
        int[] to = {R.id.cover, R.id.music_title, R.id.singer_name};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.list_music, from, to);
        ListView androidListView = (ListView) findViewById(R.id.result_list);
        androidListView.setAdapter(simpleAdapter);

    }

}
