package com.example.trek_tour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TrekLibrary extends AppCompatActivity {
    private ImageView imageView;
    private TextView FeedBackPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trek_library);

        imageView=findViewById(R.id.upcoming_treks);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TrekLibrary.this,Upcoming_treks.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.Documentation);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TrekLibrary.this,Documented_articles.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.Scl_Clg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TrekLibrary.this,School_College.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.imageView3);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TrekLibrary.this,Trek_details.class);
                startActivity(intent);
            }
        });
        imageView=findViewById(R.id.kalsubai);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TrekLibrary.this,Kalsubai_details.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.karjat);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TrekLibrary.this,Karjat_details.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.kedarnath);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TrekLibrary.this,Kedarnath.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.devkund);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TrekLibrary.this,Devkund.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.bramhtal);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TrekLibrary.this,Bramhtal.class);
                startActivity(intent);
            }
        });

        FeedBackPage=findViewById(R.id.banner3);
        FeedBackPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TrekLibrary.this,feedback.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.user);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TrekLibrary.this,UserProfile.class);
                startActivity(intent);
            }
        });

    }
}