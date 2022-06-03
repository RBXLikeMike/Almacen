package com.example.almacen.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.almacen.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();



    }

    public void inicioMenu(View u){
        Intent i = new Intent(getApplicationContext(), MenuLateralActivity.class);
        startActivity(i);
        finish();
    }
}