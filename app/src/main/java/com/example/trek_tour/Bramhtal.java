package com.example.trek_tour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Bramhtal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bramhtal);

        int myInt = 6000; // the integer value you want to store
        TextView myTextView = findViewById(R.id.Brahm_price); // the TextView to store the value in
        myTextView.setText(Integer.toString(myInt)); // convert the integer to a String and set it as the text of the TextView

        Button BookTrek=findViewById(R.id.Book);
        BookTrek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Bramhtal.this,acc.class);
                intent.putExtra("myInt", myInt); // add the integer value as an extra to the Intent
                startActivity(intent);
                finish();
            }
        });
    }
}