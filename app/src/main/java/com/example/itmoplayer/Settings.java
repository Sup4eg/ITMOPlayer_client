package com.example.itmoplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class Settings extends AppCompatActivity {

    static final int GALLERY_REQUEST = 1;
    static Uri selectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        final String login = getIntent().getExtras().getString("login");
        final String[] user_db_properties = {login};
        final EditText f_name = (EditText) findViewById(R.id.f_name);
        final EditText s_name = (EditText) findViewById(R.id.s_name);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText user_login = (EditText) findViewById(R.id.login);
        final EditText email = (EditText) findViewById(R.id.email);
        final ImageView user_image = (ImageView) findViewById(R.id.image_user);
        final TextView edit_button = (TextView) findViewById(R.id.edit_button);
        final Button save = (Button) findViewById(R.id.save);
        final Button exit = (Button) findViewById(R.id.exit);
        final TextView error_settings = (TextView) findViewById(R.id.error_settings);

        MainActivity mainActivity = new MainActivity();

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
                    final String f_name_parse = user_data.get("First_name").toString();
                    final String s_name_parse = user_data.get("Second_name").toString();
                    final String email_parse = user_data.get("Email").toString();
                    final String user_login_parse = user_data.get("Login").toString();
                    final String password_parse = user_data.get("Password").toString();
                    f_name.setText(f_name_parse);
                    s_name.setText(s_name_parse);
                    email.setText(email_parse);
                    password.setText(password_parse);
                    user_login.setText(user_login_parse);

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


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.session.set_user("");
                mainActivity.session.set_user_password("");
                Intent main_intent = new Intent(Settings.this, MainActivity.class);
                startActivity(main_intent);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String f_name_text = f_name.getText().toString(),
                        s_name_text = s_name.getText().toString(),
                        password_text = password.getText().toString(),
                        user_login_text = user_login.getText().toString(),
                        email_text = email.getText().toString();


                String image_uri = String.valueOf(selectedImage);

                final String[] new_user_db_properties = {login, f_name_text, s_name_text, user_login_text, email_text, password_text, image_uri};


                if (f_name_text.isEmpty() || s_name_text.isEmpty() || password_text.isEmpty() || email_text.isEmpty()
                || user_login_text.isEmpty() || user_image.getDrawable() == null) {
                    error_settings.setText("*You must put all values in all fields");
                } else {
                    error_settings.setText("");


                    final CountDownLatch latch = new CountDownLatch(1);

                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Proxy proxy = new Proxy();
                                String msg = proxy.mainMain("update_account_details", new_user_db_properties);
                                mainActivity.session.set_user(user_login_text);
                                mainActivity.session.set_user_password(password_text);
                                Intent intent = new Intent(Settings.this, UserActivity.class);
                                intent.putExtra("login", user_login_text);
                                startActivity(intent);
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
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT <19) {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, GALLERY_REQUEST);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        if(resultCode != RESULT_CANCELED){
            if (requestCode == GALLERY_REQUEST) {
                if (imageReturnedIntent != null) {
                    Bitmap bitmap = null;
                    final ImageView user_image = (ImageView) findViewById(R.id.image_user);

                    switch(requestCode) {
                        case GALLERY_REQUEST:
                            if(resultCode == RESULT_OK){
                                selectedImage = imageReturnedIntent.getData();
                                System.out.println(selectedImage);
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                user_image.setImageBitmap(bitmap);
                            }
                    }
                }

            }
        }

    }

}
