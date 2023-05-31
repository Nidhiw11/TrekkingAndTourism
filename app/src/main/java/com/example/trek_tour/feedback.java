package com.example.trek_tour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class feedback extends AppCompatActivity {
    private Button SendFeedback;
    private TextView feedback;
    private EditText WriteFeedBack;
    TextView tvFeedback;
    RatingBar rbStars;
    private FirebaseAuth authProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        SendFeedback=findViewById(R.id.btnSend);
        feedback=findViewById(R.id.MitFeedback);
        WriteFeedBack=findViewById(R.id.EditFeed);

        tvFeedback = findViewById(R.id.MitFeedback);
        rbStars = findViewById(R.id.NidStars);
        rbStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating==0)
                {
                    tvFeedback.setText("Very Dissatisfied");
                }
                else if(rating==1)
                {
                    tvFeedback.setText("Dissatisfied");
                }
                else if(rating==2 || rating==3)
                {
                    tvFeedback.setText("OK");
                }
                else if(rating==4)
                {
                    tvFeedback.setText("Satisfied");
                }
                else if(rating==5)
                {
                    tvFeedback.setText("Very Satisfied");
                }
                else
                {

                }
            }
        });

        SendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String FeedBack=feedback.getText().toString();
                String WrFeedBack=WriteFeedBack.getText().toString();


                if(TextUtils.isEmpty(FeedBack)){
                    Toast.makeText(feedback.this, "Please give your feedback  ", Toast.LENGTH_LONG).show();
                    feedback.setError("feedback is required");
                    feedback.requestFocus();
                }
                else if(TextUtils.isEmpty(WrFeedBack)){
                    Toast.makeText(feedback.this, "Please Enter Amount of Payment", Toast.LENGTH_LONG).show();
                    WriteFeedBack.setError("Amount is required");
                    WriteFeedBack.requestFocus();
                }
                else{
                    Toast.makeText(feedback.this, "save feedback successfully", Toast.LENGTH_LONG).show();
                    registerUser(FeedBack,WrFeedBack);
                }
            }

            private void registerUser(String feedBack, String wrFeedBack) {
                FirebaseAuth auth=FirebaseAuth.getInstance();
                FirebaseUser firebaseuser=auth.getCurrentUser();
                FeedUser writeFeed=new FeedUser(feedBack,wrFeedBack);

                //Extracting user reference from database for registered users
                DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
                referenceProfile.child(firebaseuser.getUid()).push().setValue(writeFeed).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent intent=new Intent(feedback.this,TrekLibrary.class);
                            startActivity(intent);
                            finish();

                        }
                    }
                });
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
            NavUtils.navigateUpFromSameTask(feedback.this);
        }else if(id==R.id.menu_refresh){
            //Refresh Activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }  else if (id == R.id.menu_user_profile) {
            Intent intent=new Intent(feedback.this,UserProfile.class);
            startActivity(intent);
        } else if (id == R.id.menu_update_profile) {
            Intent  intent=new Intent(feedback.this,UpdateP.class);
            startActivity(intent);
        }else if(id==R.id.menu_settings){
            Intent  intent=new Intent(feedback.this,feedback.class);
            startActivity(intent);
        }else if(id==R.id.menu_Logout){
            authProfile.signOut();
            Toast.makeText(feedback.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(feedback.this,MainActivity.class);

            //Clear stack to prevent user coming back to DashBoard on back button after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();//close DashBoard
        }else{
            Toast.makeText(feedback.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}