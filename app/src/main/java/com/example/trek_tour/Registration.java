package com.example.trek_tour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    private EditText editTextRegisterFullName,editTextRegistrationEmail,editTextRegistrationDOB,editTextRegistrationMob,
            editTextRegistrationPass, editTextRegistrationConfpwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private  DatePickerDialog picker;
    public static String textFullName;
    private static final String TAG="Registration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
       // getSupportActionBar().setTitle("Registration");
        Toast.makeText(Registration.this, "You can register now", Toast.LENGTH_LONG).show();

        progressBar=findViewById(R.id.progressBar);
        editTextRegisterFullName=findViewById(R.id.fullname);
        editTextRegistrationEmail=findViewById(R.id.email1);
        editTextRegistrationDOB=findViewById(R.id.dob);
        editTextRegistrationMob=findViewById(R.id.phonenumber);
        editTextRegistrationPass=findViewById(R.id.password1);
        editTextRegistrationConfpwd=findViewById(R.id.password2);

        //RadioButton for Gender
        radioGroupRegisterGender=findViewById(R.id.radiogender);
        radioGroupRegisterGender.clearCheck();

        //Setting up DatePicker on editText
        editTextRegistrationDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar=Calendar.getInstance();
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);

                //Date Picker Dialog
                picker=new DatePickerDialog(Registration.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextRegistrationDOB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });


        Button SignUp=findViewById(R.id.button3);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedGenderId=radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected=findViewById(selectedGenderId);

              //obtain the entered data
                textFullName=editTextRegisterFullName.getText().toString();
                String textEmail=editTextRegistrationEmail.getText().toString();
                String textDOB=editTextRegistrationDOB.getText().toString();
                String textMobile=editTextRegistrationMob.getText().toString();
                String textPass=editTextRegistrationPass.getText().toString();
                String textConfpwd=editTextRegistrationConfpwd.getText().toString();
                String textGender; //can't obtain the value before verifying if any button was selected or not

                //Validate Mobile Number Using Matcher and Pattern (Regular Expression)
                String mobileRegex="[6-9][0-9]{9}";//First no.can be {6,8,9} and rest 9 nos.can be any no
                Matcher mobileMatcher;
                Pattern mobilePattern=Pattern.compile(mobileRegex);
                mobileMatcher=mobilePattern.matcher(textMobile);

                if(TextUtils.isEmpty(textFullName))
                {
                    Toast.makeText(Registration.this, "Please Enter your full name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full Name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(Registration.this, "Please enter your Email Id", Toast.LENGTH_LONG).show();
                    editTextRegistrationEmail.setError("Email Id is required");
                    editTextRegistrationEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(Registration.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextRegistrationEmail.setError("Valid email is required");
                    editTextRegistrationEmail.requestFocus();
                }else if(TextUtils.isEmpty(textDOB)){
                    Toast.makeText(Registration.this, "Please Enter your Date-of-Birth", Toast.LENGTH_LONG).show();
                    editTextRegistrationDOB.setError("Date Of Birth is required");
                    editTextRegistrationDOB.requestFocus();
                }else if(radioGroupRegisterGender.getCheckedRadioButtonId()==-1){
                    Toast.makeText(Registration.this, "Please select your gender", Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Gender is Required");
                    radioButtonRegisterGenderSelected.requestFocus();
                }else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(Registration.this, "Please enter your mobile no", Toast.LENGTH_LONG).show();
                    editTextRegistrationMob.setError("Mobile No is Required");
                    editTextRegistrationMob.requestFocus();
                }else if(textMobile.length()!=10){
                    Toast.makeText(Registration.this, "Please re-enter your mobile no", Toast.LENGTH_LONG).show();
                    editTextRegistrationMob.setError("Mobile No.should be 10 digits");
                    editTextRegistrationMob.requestFocus();
                }else if(!mobileMatcher.find()){
                    Toast.makeText(Registration.this, "Please re-enter your mobile no", Toast.LENGTH_LONG).show();
                    editTextRegistrationMob.setError("Mobile No. is not valid");
                    editTextRegistrationMob.requestFocus();
                }
                else if(TextUtils.isEmpty(textPass)){
                    Toast.makeText(Registration.this, "Please Enter your password", Toast.LENGTH_LONG).show();
                    editTextRegistrationPass.setError("Password is required");
                    editTextRegistrationPass.requestFocus();
                }else if(textPass.length()<6){
                    Toast.makeText(Registration.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                    editTextRegistrationPass.setError("Password too weak");
                    editTextRegistrationPass.requestFocus();
                }else if(TextUtils.isEmpty(textConfpwd)){
                    Toast.makeText(Registration.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                    editTextRegistrationConfpwd.setError("Password confirmation is required");
                    editTextRegistrationConfpwd.requestFocus();
                }else if(!textPass.equals(textConfpwd)){
                    Toast.makeText(Registration.this, "Please same same password", Toast.LENGTH_LONG).show();
                    editTextRegistrationConfpwd.setError("Password confirmation is required");
                    editTextRegistrationConfpwd.requestFocus();
                    //clear the entered passwords
                    editTextRegistrationPass.clearComposingText();
                    editTextRegistrationConfpwd.clearComposingText();
                }else{
                    textGender=radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName,textEmail,textDOB,textGender,textMobile,textPass);
                }
            }

        });

    }
    private void registerUser(String textFullName, String textEmail, String textDOB,String textGender,String textMobile, String textPass) {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        //create User Profile
        auth.createUserWithEmailAndPassword(textEmail,textPass).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {

                    FirebaseUser firebaseUser=auth.getCurrentUser();

                    //update display name of user
                    UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //Enter User Data into the Firebase Realtime Database.
                    ReadWriteDetails writeUserDetails=new ReadWriteDetails(textFullName,textDOB,textGender,textMobile);
                    //Extracting User reference from Database for "Registered Users"
                    DatabaseReference referenceProfile=FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //send verification Email
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(Registration.this, "User registered successfully.Please verify your email",
                                        Toast.LENGTH_LONG).show();

                            //open user Profile after successful registration
                             Intent intent=new Intent(Registration.this,MainActivity.class);
                             //to prevent user from returning  back to Register Activity on  pressing back button after registration
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                 Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                             finish(); //to close register activity
                            }else{
                                Toast.makeText(Registration.this, "User registered Fail .Please try again", Toast.LENGTH_LONG).show();
                            }
                            //hide progressBar without user creation is successful or failed
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        editTextRegistrationPass.setError("Your password is too weak.kindly use mix of alphabets,numbers,and special characters");
                        editTextRegistrationPass.requestFocus();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        editTextRegistrationPass.setError("Your email is invalid or already in use .kindly re-enter,");
                        editTextRegistrationPass.requestFocus();
                    }catch(FirebaseAuthUserCollisionException e){
                        editTextRegistrationPass.setError("User is already with this email.use another email");
                        editTextRegistrationPass.requestFocus();
                    }catch(Exception e) {
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    //hide progressBar without user creation is successful or failed
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}
