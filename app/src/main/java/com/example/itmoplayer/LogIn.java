package com.example.itmoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


public class LogIn extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final Button submit = (Button) findViewById(R.id.submit);
        final EditText user_login_email = (EditText) findViewById(R.id.user_login);
        final EditText user_password = (EditText) findViewById(R.id.user_password);
        final TextView regestration_error = (TextView) findViewById(R.id.regestration_error);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_login_text = user_login_email.getText().toString(),
                        user_password_text = user_password.getText().toString();

                final String[] user_db_properties = {user_login_text, user_password_text};

                if (user_login_text.isEmpty() || user_password_text.isEmpty()) {
                    regestration_error.setText("You should put all values in all fields");
                } else {

                    regestration_error.setText("");

                    final CountDownLatch latch = new CountDownLatch(1);

                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Proxy proxy = new Proxy();
                                String msg = proxy.mainMain("find_account_details", user_db_properties);
                                Gson gson = new Gson();
                                Map map = gson.fromJson(msg, Map.class);
                                if (map.get("result").toString().equals("None")) {
                                    regestration_error.setText("You need to registrate to go on");
                                } else if (map.get("result").equals("Done")){
                                    regestration_error.setText("");
                                    Intent intent = new Intent(LogIn.this, UserActivity.class);
                                    intent.putExtra("login", user_login_text);
                                    startActivity(intent);
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


                }

            }
        });
    }

}
