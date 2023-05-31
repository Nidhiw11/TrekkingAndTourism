package com.example.trek_tour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateP extends AppCompatActivity {
    private EditText editTextUpdateName,editTextUpdateDoB,editTextUpdateMobile;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String textFullName,textDoB,textGender,textMobile;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_p);
        getSupportActionBar().setTitle("Update Profile Details");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        progressBar=findViewById(R.id.progressbar5);
        editTextUpdateName=findViewById(R.id.edittext_profile_name);
        editTextUpdateDoB=findViewById(R.id.edittext_profile_dob);
        editTextUpdateMobile=findViewById(R.id.edittext_profile_mobile);

        radioGroupUpdateGender=findViewById(R.id.radio_group_update_gender);

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();

        //Show Profile Data
        showProfile(firebaseUser);

        //Upload Profile Pic
        Button buttonUploadProfilePic=findViewById(R.id.button_upload_profile_pic);
        buttonUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent=new Intent(UpdateP.this,UploadProfilePic.class);
                startActivity(intent);
                finish();
            }
        });


        //Setting up DatePicker on editText
        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Extract saved dd,mm,yyyy into different variable by creating an array delimited by "/"
                String textSADoB[]=textDoB.split("/");

                int day=Integer.parseInt(textSADoB[0]);
                int month=Integer.parseInt(textSADoB[1])-1;//to care of month index starting from 0
                int year=Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;

                //Date Picker Dialog
                picker=new DatePickerDialog(UpdateP.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        //Update Profile
        Button buttonUpdateProfile=findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(firebaseUser);
            }
        });
    }
    //Update Profile
    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID=radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected=findViewById(selectedGenderID);

        //Validate Mobile Number Using Matcher and Pattern (Regular Expression)
        String mobileRegex="[6-9][0-9]{9}";//First no.can be {6,8,9} and rest 9 nos.can be any no
        Matcher mobileMatcher;
        Pattern mobilePattern=Pattern.compile(mobileRegex);
        mobileMatcher=mobilePattern.matcher(textMobile);


        if(TextUtils.isEmpty(textFullName))
        {
            Toast.makeText(UpdateP.this, "Please Enter your full name", Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full Name is required");
            editTextUpdateName.requestFocus();
        }else if(TextUtils.isEmpty(textDoB)){
            Toast.makeText(UpdateP.this, "Please Enter your Date-of-Birth", Toast.LENGTH_LONG).show();
            editTextUpdateDoB.setError("Date Of Birth is required");
            editTextUpdateDoB.requestFocus();
        }else if(TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())){
            Toast.makeText(UpdateP.this, "Please select your gender", Toast.LENGTH_LONG).show();
            radioButtonUpdateGenderSelected.setError("Gender is Required");
            radioButtonUpdateGenderSelected.requestFocus();
        }
        else if (TextUtils.isEmpty(textMobile)) {
            Toast.makeText(UpdateP.this, "Please enter your mobile no", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No is Required");
            editTextUpdateMobile.requestFocus();
        }else if(textMobile.length()!=10){
            Toast.makeText(UpdateP.this, "Please re-enter your mobile no", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No.should be 10 digits");
            editTextUpdateMobile.requestFocus();
        }else if(!mobileMatcher.find()){
            Toast.makeText(UpdateP.this, "Please re-enter your mobile no", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No. is not valid");
            editTextUpdateMobile.requestFocus();
        }else{
            //Obtain the data entered by User
            textGender=radioButtonUpdateGenderSelected.getText().toString();
            textFullName=editTextUpdateName.getText().toString();
            textDoB=editTextUpdateDoB.getText().toString();
            textMobile=editTextUpdateMobile.getText().toString();

            //Enter User Data into the Firebase Realtime Database.set up dependencies
            ReadWriteDetails writeDetails=new ReadWriteDetails(textFullName,textDoB,textGender,textMobile);

            //Extract User Reference from Database for "Registered Users"
            DatabaseReference referenceProfile=FirebaseDatabase.getInstance().getReference("Registered Users");
            String userID=firebaseUser.getUid();

            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //Setting new display Name
                        UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileUpdates);
                        Toast.makeText(UpdateP.this,"Update Sucessfully !!",Toast.LENGTH_LONG).show();

                        //Stop user fro returnning to UpadteProfileActivity on Pressing back button and close activity
                        Intent intent=new Intent(UpdateP.this,UserProfile.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else{
                        try{
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateP.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    //fetch data from Firebase and  display
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered=firebaseUser.getUid();

        //Extracting User Reference from Database for "Registered Users"
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        progressBar.setVisibility(View.VISIBLE);
        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteDetails readUserDetails=snapshot.getValue(ReadWriteDetails.class);
                if(readUserDetails!=null){
                    textFullName=firebaseUser.getDisplayName();
                    textDoB=readUserDetails.dob;
                    textGender=readUserDetails.gender;
                    textMobile=readUserDetails.mobile;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDoB.setText(textDoB);
                    editTextUpdateMobile.setText(textMobile);

                    //Show Gender through Radio Button
                    if(textGender.equals("Male")){
                        radioButtonUpdateGenderSelected=findViewById(R.id.radio_male);
                    }
                    else{
                        radioButtonUpdateGenderSelected=findViewById(R.id.radio_female);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                }else{
                    Toast.makeText(UpdateP.this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateP.this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
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
            NavUtils.navigateUpFromSameTask(UpdateP.this);
        }else if(id==R.id.menu_refresh){
            //Refresh Activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }  else if (id == R.id.menu_user_profile) {
            Intent  intent=new Intent(UpdateP.this,UserProfile.class);
            startActivity(intent);
        } else if (id == R.id.menu_update_profile) {
            Intent  intent=new Intent(UpdateP.this,UpdateP.class);
            startActivity(intent);
        }else if(id==R.id.menu_settings){
            Intent  intent=new Intent(UpdateP.this,feedback.class);
            startActivity(intent);
        }else if(id==R.id.menu_Logout){
            authProfile.signOut();
            Toast.makeText(UpdateP.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(UpdateP.this,MainActivity.class);

            //Clear stack to prevent user coming back to DashBoard on back button after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();//close DashBoard
        }else{
            Toast.makeText(UpdateP.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}