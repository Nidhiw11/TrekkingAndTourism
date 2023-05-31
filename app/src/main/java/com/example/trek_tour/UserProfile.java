package com.example.trek_tour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfile extends AppCompatActivity {

    private TextView textViewWelcome,textViewName,textViewEmail,textViewDoB,textViewGender,textViewMobile;
    private ProgressBar progressBar;
    public static String name,email,dob,gender,mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setTitle("HOME");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        textViewWelcome= findViewById(R.id.welcometext);
        textViewName=findViewById(R.id.show_fullname);
        textViewEmail=findViewById(R.id.show_email);
        textViewDoB=findViewById(R.id.show_dob);
        textViewGender=findViewById(R.id.show_gender);
        textViewMobile=findViewById(R.id.show_phone);
        progressBar=findViewById(R.id.progress1);

        //set onClickListener on ImageView to Open UploadProfilePic
        imageView=findViewById(R.id.profile_dp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserProfile.this,UploadProfilePic.class);
                startActivity(intent);
            }
        });

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();

        if (firebaseUser==null){
            Toast.makeText(UserProfile.this,"Something went wrong! No data found",Toast.LENGTH_LONG).show();
        }
        else {
            checkifEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }
     //Users coming to UserProfile after Successful registration
    private void checkifEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }

    }

    private void showAlertDialog() {
        //setup the Alert Builder
        AlertDialog.Builder builder=new AlertDialog.Builder(UserProfile.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now.You cannot login without email Verification next time.");

        // Open Email Apps if User clicks/taps Continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //To email app in new window and not within our app
                startActivity(intent);
            }
        });
        //Create AlertDialog
        AlertDialog alertDialog= builder.create();

        //show the AlertDialog
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID= firebaseUser.getUid();
        //Extracting user Reference Database for "Registered users"
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteDetails readWriteDetails=snapshot.getValue(ReadWriteDetails.class);
                if (readWriteDetails!=null){
                    name=readWriteDetails.fullname;
                    email=firebaseUser.getEmail();
                    dob=readWriteDetails.dob;
                    gender=readWriteDetails.gender;
                    mobile=readWriteDetails.mobile;

                    textViewWelcome.setText("Welcome," + name+"!");
                    textViewName.setText(name);
                    textViewEmail.setText(email);
                    textViewDoB.setText(dob);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);

                    //Set User DP(After user has uploaded)
                    Uri uri=firebaseUser.getPhotoUrl();

                    //ImageViewer setImageURI() should not be used with regular URIs.So we are using Picasso
                    Picasso.with(UserProfile.this).load(uri).into(imageView);

                }else{
                    Toast.makeText(UserProfile.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfile.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }
    //Creating ActionBAr Menu
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
            NavUtils.navigateUpFromSameTask(UserProfile.this);
        }else if(id==R.id.menu_refresh){
            //Refresh Activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }  else if (id == R.id.menu_user_profile) {
            Intent intent=new Intent(UserProfile.this,UserProfile.class);
            startActivity(intent);
        } else if (id == R.id.menu_update_profile) {
            Intent  intent=new Intent(UserProfile.this,UpdateP.class);
            startActivity(intent);
        }else if(id==R.id.menu_settings){
            Intent  intent=new Intent(UserProfile.this,feedback.class);
            startActivity(intent);
        }else if(id==R.id.menu_Logout){
            authProfile.signOut();
            Toast.makeText(UserProfile.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(UserProfile.this,MainActivity.class);

            //Clear stack to prevent user coming back to DashBoard on back button after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();//close DashBoard
        }else{
            Toast.makeText(UserProfile.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}