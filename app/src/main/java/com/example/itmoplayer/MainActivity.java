package com.example.itmoplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.Runtime;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;


public class MainActivity extends AppCompatActivity {

    static final int GALLERY_REQUEST = 1;
    static Uri selectedImage = null;
    static int image_id;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
            final Button submit = (Button) findViewById(R.id.submit);
            final EditText f_name = (EditText) findViewById(R.id.f_name);
            final EditText s_name = (EditText) findViewById(R.id.s_name);
            final EditText login = (EditText) findViewById(R.id.login);
            final EditText password = (EditText) findViewById(R.id.password);
            final EditText email = (EditText) findViewById(R.id.email);
            final EditText error = (EditText) findViewById(R.id.error);
            final Button image = (Button) findViewById(R.id.chose);
            final ImageView imageView = (ImageView) findViewById(R.id.imageView);
            final Button logIn_button = (Button) findViewById(R.id.login_button);

            image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String text_f_name = f_name.getText().toString(),
                    text_s_name = s_name.getText().toString(),
                    text_login = login.getText().toString(),
                    text_password = password.getText().toString(),
                    text_email = email.getText().toString(),
                    image = selectedImage.toString();
            final String[] user_db_properties = {text_f_name, text_s_name, text_login, text_password,  text_email, image};
            if (!(text_f_name.isEmpty() || text_s_name.isEmpty()
                    || text_login.isEmpty()
                    || text_password.isEmpty()
                    || text_email.isEmpty() || imageView.getDrawable() == null)){
                error.setText("");

                final CountDownLatch latch = new CountDownLatch(1);

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            Proxy proxy = new Proxy();
                            String msg = proxy.mainMain("insert_account_details", user_db_properties);
                            Gson gson = new Gson();
                            Map map = gson.fromJson(msg, Map.class);
                            if (map.get("result") == "You should put unique email and login") {
                                error.setText("You need to registrate to go on");
                            } else {
                                error.setText("");
                            }
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

            } else {
                error.setText("You must put all values in all fields");
            }
        }
    });
        logIn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogIn.class);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//        System.out.println(resultCode);
//        System.out.println(requestCode);
        if(resultCode != RESULT_CANCELED){
            if (requestCode == GALLERY_REQUEST) {
                if (imageReturnedIntent != null) {
                    Bitmap bitmap = null;
                    ImageView imageView = (ImageView) findViewById(R.id.imageView);

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
                                imageView.setImageBitmap(bitmap);
                            }
                    }
                }

            }
        }

    }

}
