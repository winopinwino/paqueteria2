package com.example.paqueteria2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
     Button btn_conductor,btn_cliente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_conductor = (Button) findViewById(R.id.btn_conductor);
        btn_cliente = (Button) findViewById(R.id.btn_cliente);

        btn_conductor.setOnClickListener(this);
        btn_cliente.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_conductor:
                Intent intent =  new Intent(MainActivity.this,Login2Activity.class);
                startActivity(intent);
                break;
            case R.id.btn_cliente:
                Intent intent2 =  new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
