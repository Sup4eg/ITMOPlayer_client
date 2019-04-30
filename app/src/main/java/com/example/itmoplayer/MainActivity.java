package com.example.itmoplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {


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

        submit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!(f_name.getText().toString().isEmpty() || s_name.getText().toString().isEmpty()
                    || login.getText().toString().isEmpty()
                    || password.getText().toString().isEmpty()
                    || email.getText().toString().isEmpty())){
                error.setText("");
            } else {
                error.setText("You must put all values in all fields");
            }
        }
    });
    }
}
