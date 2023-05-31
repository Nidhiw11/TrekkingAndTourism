package com.example.trek_tour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class School_College extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_college);


        imageView=findViewById(R.id.imageView3);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(School_College.this,waterkingdom.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.WetNJoy);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(School_College.this,WetnJoy.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.bolly);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(School_College.this,Bolly.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.pawna);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(School_College.this,Pawana.class);
                startActivity(intent);
            }
        });
    }
}