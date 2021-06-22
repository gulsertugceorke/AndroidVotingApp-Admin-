package com.example.ovsadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    //in this section I described the components
    private EditText et_userName,et_password;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //Bu metod uygulama açıldığında çalıştırılan metod.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Burada Buttona tıklandığında çalıştırılacak kodlar yer alıyor.
                String username = et_userName.getText().toString();
                String password = et_password.getText().toString();

                if (username.equals("admin") && password.equals("1234")){
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(),"Username or password is incorrect",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void init(){
        et_userName = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_log);
    }
}