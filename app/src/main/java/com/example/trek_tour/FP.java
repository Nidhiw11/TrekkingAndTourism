package com.example.trek_tour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class FP extends AppCompatActivity {
    private Button buttonPwdReset;
    private EditText editTextPwdResetEmail;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static  final String TAG="ForgetPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fp);
        getSupportActionBar().setTitle("Forget Password");
        editTextPwdResetEmail=findViewById(R.id.edittext_password_reset_email);
        buttonPwdReset=findViewById(R.id.resetbtn);
        progressBar=findViewById(R.id.progressbar1);

        int actionBarTitleId= Resources.getSystem().getIdentifier("action_bar_title","id","android");
        if (actionBarTitleId>0){
            TextView title=(TextView) findViewById(actionBarTitleId);
            if (title!=null){
                title.setTextColor(Color.BLACK);
            }
        }

        buttonPwdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=editTextPwdResetEmail.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(FP.this, "Please Enter your registerd email", Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("Email is Required");
                    editTextPwdResetEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(FP.this, "Please Enter your valid email", Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("Valid email is required");
                    editTextPwdResetEmail.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });


    }

    private void resetPassword(String email) {
        authProfile=FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(FP.this, "Please Check Your Inbox For Password reset link", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(FP.this,MainActivity.class);

                    //Clear stack to prevent user coming back to ForgetPasswordActivity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        editTextPwdResetEmail.setError("User doesn't exist or is no longer valid. Please register again");
                    }catch (Exception e)
                    {
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(FP.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}