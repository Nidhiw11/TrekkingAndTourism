package com.example.trek_tour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class Payment extends AppCompatActivity implements PaymentResultListener {
    private TextView txtPaymentStatus;
    private Button btnPayNow;
    private EditText editAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        txtPaymentStatus=findViewById(R.id.Payment_Status);
        editAmount=findViewById(R.id.edit_amount);
        btnPayNow=findViewById(R.id.btn_pay);
        //Get the EditText object
        EditText editText = (EditText) findViewById(R.id.edit_amount);

       //Get the Intent object
        Intent intent = getIntent();

        //Get the integer value from the Intent object
        int intValue = intent.getIntExtra("intValue", 0);

        //Set the integer value in the EditText object
        editText.setText(String.valueOf(intValue));
        Checkout.preload(Payment.this);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startPayment(Integer.parseInt(editAmount.getText().toString()));

            }
        });
    }
    public void startPayment(int Amount){
        Checkout checkout=new Checkout();
        checkout.setKeyID("rzp_test_lyVg4EbhAATCjE");
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("name","RazorPay");
            jsonObject.put("description","Thank you for your Payment");
            //jsonObject.put("image,"");
            jsonObject.put("theme.color","#3399cc");
            jsonObject.put("currency","INR");
            jsonObject.put("amount",Amount*100);

            JSONObject retryObj=new JSONObject();
            retryObj.put("enabled",true);
            retryObj.put("max count",4);
            jsonObject.put("retry",retryObj);
            checkout.open(Payment.this,jsonObject);
        } catch(Exception e){
            Toast.makeText(Payment.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPaymentSuccess(String s) {
        txtPaymentStatus.setText(s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        txtPaymentStatus.setText("Error:"+s);
    }
}