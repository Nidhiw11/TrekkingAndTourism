package com.example.trek_tour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class DashB extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private ImageView imageView;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_b);
        getSupportActionBar().setTitle("DASHBORD");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        imageView=findViewById(R.id.Tracker);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DashB.this,Location_Tracker.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.Organisation);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DashB.this,Acco_Travals.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.trektype);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DashB.this,BookingStatus.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.Pay);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DashB.this,Payment.class);
                startActivity(intent);
            }
        });
        imageView=findViewById(R.id.treklib);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DashB.this,TrekLibrary.class);
                startActivity(intent);
            }
        });

        imageView=findViewById(R.id.Logout);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authProfile.signOut();
                Toast.makeText(DashB.this, "Logged Out", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(DashB.this,MainActivity.class);
                //Clear stack to prevent user coming back to DashBoard on back button after Logging out
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();//close DashBoard
            }
        });

        authProfile=FirebaseAuth.getInstance();

    }
    //Creating ActionBar Menu
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(DashB.this);
        }else if(id==R.id.menu_refresh){
            //Refresh Activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }  else if (id == R.id.menu_user_profile) {
            Intent  intent=new Intent(DashB.this,UserProfile.class);
            startActivity(intent);
        } else if (id == R.id.menu_update_profile) {
            Intent  intent=new Intent(DashB.this,UpdateP.class);
            startActivity(intent);
           //finish();
        }else if(id==R.id.menu_settings){
            Intent  intent=new Intent(DashB.this,feedback.class);
            startActivity(intent);
        }else if(id==R.id.menu_Logout){
            authProfile.signOut();
            Toast.makeText(DashB.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(DashB.this,MainActivity.class);

            //Clear stack to prevent user coming back to DashBoard on back button after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();//close DashBoard
        }else{
            Toast.makeText(DashB.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}