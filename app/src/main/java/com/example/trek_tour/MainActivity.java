package com.example.trek_tour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private EditText editTextLoginEmail,editTextLoginPwd;
    private TextView fpass;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private EditText mEmailField;
    private EditText mPasswordField;
    private CheckBox mRememberMeCheckbox;
    private SharedPreferences mSharedPreferences;
    private static  final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextLoginEmail=findViewById(R.id.email);
        editTextLoginPwd=findViewById(R.id.password);
        progressBar=findViewById(R.id.progress);
        authProfile=FirebaseAuth.getInstance();

        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mRememberMeCheckbox = findViewById(R.id.checkBox);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (mSharedPreferences.getBoolean("remember_me", false)) {
            mEmailField.setText(mSharedPreferences.getString("email", ""));
            mPasswordField.setText(mSharedPreferences.getString("password", ""));
            mRememberMeCheckbox.setChecked(true);
        }

        // Set a listener on the "login" button
        Button loginButton = findViewById(R.id.button1);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();

                // Save email and password if "remember me" is checked
                if (mRememberMeCheckbox.isChecked()) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.putBoolean("remember_me", true);
                    editor.apply();
                } else {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.remove("email");
                    editor.remove("password");
                    editor.remove("remember_me");
                    editor.apply();
                }

                // Perform login logic here
                // ...
            }
        });


        //reset password (Forget_Password)
        fpass=(TextView) findViewById(R.id.forgetpassword);
        fpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"You can reset your password now !!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this,FP.class));
            }
        });

        //open login Activity
        Button Login=findViewById(R.id.button1);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail=editTextLoginEmail.getText().toString();
                String textPwd=editTextLoginPwd.getText().toString();

                if(TextUtils.isEmpty((textEmail))){
                    Toast.makeText(MainActivity.this,"Please enter  your email",Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(MainActivity.this,"Please re-enter your email",Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("valid email is required");
                    editTextLoginEmail.requestFocus();
                }else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(MainActivity.this,"Please enter your Password",Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("Password is required");
                    editTextLoginPwd.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                }
            }
        });
        //open Register Activity
        Button SignUp=findViewById(R.id.button2);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Registration.class);
                startActivity(intent);
            }
        });
    }
    private void loginUser(String email,String pwd) {
        authProfile.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Get instance of the current User
                    FirebaseUser firebaseUser=authProfile.getCurrentUser();

                    //check if email is verified before user can access their profile
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(MainActivity.this,"You are logged in now",Toast.LENGTH_SHORT).show();
                        //open user Profile
                        //Start the DashBoard Activity
                        startActivity(new Intent(MainActivity.this,DashB.class));
                        finish(); //Close Login Activity..
                    }else{
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();//sign out user
                        showAlertDialog();
                    }
                }
                else{
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setError("User does not exists or is no longer valid.Please register again ");
                        editTextLoginEmail.requestFocus();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        editTextLoginEmail.setError("Invalid credentials.Kindly,check and re-enter.");
                        editTextLoginEmail.requestFocus();
                    }catch(Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void showAlertDialog() {
        //setup the Alert Builder
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now.You cannot login without email Verification.");

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
   /* //Check if User already logged in. In such case,straightaway take the  DashBoard page
    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser()!= null){
            Toast.makeText(MainActivity.this,"Already Logged In !!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,DashB.class));
            finish();
        }
        else{
            Toast.makeText(MainActivity.this,"You can login now !!",Toast.LENGTH_SHORT).show();
        }
    }

    */
}