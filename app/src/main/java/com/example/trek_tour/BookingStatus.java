package com.example.trek_tour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class BookingStatus extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private TextView textViewWelcome,textViewFullName,textViewEmail,textViewCheckIn,textViewCheckOut,textViewAId,textViewTId,textViewAmt;
    private ProgressBar progressBar;
    private String name,email,CheckIn,CheckOut,AId,TId,Amt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_status);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

       //getSupportActionBar().setTitle("Booking_Status");
        textViewWelcome= findViewById(R.id.welcometext);
        textViewFullName= findViewById(R.id.show_fullname);
        textViewEmail=findViewById(R.id.show_email);
        textViewCheckIn=findViewById(R.id.show_dob);
        textViewCheckOut=findViewById(R.id.show_dob2);
        textViewAId=findViewById(R.id.show_acc);
        textViewTId=findViewById(R.id.show_trans);
        textViewAmt=findViewById(R.id.show_pay);
        progressBar=findViewById(R.id.progress1);
        final String[] childU = new String[1];
        String userId=firebaseUser.getUid();
        //Extracting user reference from db for registered user
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference().child("Registered Users");
        referenceProfile.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot locationSnapshot: snapshot.getChildren()) {
                    for (DataSnapshot bumpSnapshot : locationSnapshot.getChildren()) {
                        User user = locationSnapshot.getValue(User.class);
                        childU[0] =locationSnapshot.getKey();
                        if (user != null) {
                            name=firebaseUser.getDisplayName();
                            email=firebaseUser.getEmail();
                            CheckIn=user.checkIn;
                            CheckOut=user.checkOut;
                            AId=user.accType;
                            TId=user.tranId;
                            Amt=user.payment;

                            textViewWelcome.setText("Welcome," + name+"!");
                            textViewFullName.setText(name);
                            textViewEmail.setText(email);
                            textViewCheckIn.setText(CheckIn);
                            textViewCheckOut.setText(CheckOut);
                            textViewAId.setText(AId);
                            textViewTId.setText(TId);
                            textViewAmt.setText(Amt);

                        }
                        else {
                            Toast.makeText(BookingStatus.this,"Error",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingStatus.this,"Something wet wrong",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }

        });

        Button PayNow=findViewById(R.id.button_payment);
        PayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Get the TextView object
                TextView textView = (TextView) findViewById(R.id.show_pay);

//Get the integer value from the TextView object
                int intValue = Integer.parseInt(textView.getText().toString());

//Create an Intent object to start the second activity
                Intent intent = new Intent(BookingStatus.this, Payment.class);

//Add the integer value to the Intent object
                intent.putExtra("intValue", intValue);

//Start the second activity
                startActivity(intent);



               /* Intent intent=new Intent(BookingStatus.this,Payment.class);
                startActivity(intent);
                finish();*/
            }
        });


    }

}