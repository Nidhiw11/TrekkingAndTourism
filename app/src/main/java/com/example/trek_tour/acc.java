package com.example.trek_tour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class acc extends AppCompatActivity {
    private Button Next;
    private EditText checkIn,checkOut;
    private TextView payment;
    private RadioGroup AccType,TranId;
    private RadioButton ButtonAcc,ButtonTrans ;
    private DatePickerDialog picker;
    private ProgressBar progressBar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc);


        Toast.makeText(acc.this, "You Can Choose Your Accommodation Type and Transportation Type ", Toast.LENGTH_LONG).show();

        int myInt = getIntent().getIntExtra("myInt", 0); // retrieve the integer value from the Intent
        TextView myTextView = findViewById(R.id.amt); // the TextView to retrieve the value in
        myTextView.setText(Integer.toString(myInt)); // convert the integer to a String and set it as the text of the TextView




        progressBar=findViewById(R.id.progressBar);
        checkIn=findViewById(R.id.dob);
        checkOut=findViewById(R.id.dob2);
        Next=findViewById(R.id.next);
        payment=findViewById(R.id.amt);


        AccType=findViewById(R.id.radiogender);
        AccType.clearCheck();

        TranId=findViewById(R.id.radiotrans);
        TranId.clearCheck();

        //Setting up DatePicker on checkIn
        checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar=Calendar.getInstance();
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);

                //Date Picker Dialog
                picker=new DatePickerDialog(acc.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        checkIn.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        //Setting up DatePicker on checkOut
        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar=Calendar.getInstance();
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);

                //Date Picker Dialog
                picker=new DatePickerDialog(acc.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        checkOut.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectAccType=AccType.getCheckedRadioButtonId();
                ButtonAcc=findViewById(selectAccType);

                int selectTranType=TranId.getCheckedRadioButtonId();
                ButtonTrans=findViewById(selectTranType);

                String CheckIn=checkIn.getText().toString();
                String CheckOut=checkOut.getText().toString();
                String Payment=payment.getText().toString();
                String AType;
                String TId;

                if(TextUtils.isEmpty(CheckIn)){
                    Toast.makeText(acc.this, "Please select Date of checkIn ", Toast.LENGTH_LONG).show();
                    checkIn.setError("Date is required");
                    checkIn.requestFocus();
                }
                else if(TextUtils.isEmpty(CheckOut)){
                    Toast.makeText(acc.this, "Please select Date of checkOut ", Toast.LENGTH_LONG).show();
                    checkOut.setError("Date is required");
                    checkOut.requestFocus();
                }
                else if(TextUtils.isEmpty(Payment)){
                    Toast.makeText(acc.this, "Please Enter Amount of Payment", Toast.LENGTH_LONG).show();
                    payment.setError("Amount is required");
                    payment.requestFocus();}
                else{
                    AType=ButtonAcc.getText().toString();
                    TId=ButtonTrans.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(CheckIn,CheckOut, String.valueOf(payment.getText()),AType,TId);
                }
            }
        });

    }

    private void registerUser(String checkIn, String checkOut, String payment, String aType, String tId) {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser firebaseuser=auth.getCurrentUser();
        //Enter user data into the firebase realtime database
        User writeUserDetails=new User(checkIn,checkOut,payment,aType,tId);

        //Extracting user reference from database for registered users
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(firebaseuser.getUid()).push().setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(acc.this,BookingStatus.class);
                    startActivity(intent);
                    finish();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}