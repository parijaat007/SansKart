package com.example.sanskart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sanskart.Model.OrderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Order_placed extends AppCompatActivity {

    DatabaseReference orderref;
    FirebaseAuth firebaseAuth;
    final double deliveryChargePerKM = 5;
    String amountPayable;
    String UID;
    TextView amt_pay;
    TextView status;
    TextView mDistance;
    TextView mTotalPayable;
    Button continuebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        amountPayable = getIntent().getStringExtra("TotalAmount");
        UID = getIntent().getStringExtra("UID");

        amt_pay = findViewById(R.id.amount_payable);
        status = findViewById(R.id.Status_TextView);
        mDistance = findViewById(R.id.distance_textView);
        mTotalPayable = findViewById(R.id.Total_Amount_Payable_TextView);

        amt_pay.setText("AMOUNT PAYABLE : " + amountPayable + "Rs.");
        status.setText("Your Order Has Been Placed!");

//        orderref = FirebaseDatabase.getInstance().getReference().child("Orders").child(UID);
//
//        orderref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                OrderItem order = dataSnapshot.getValue(OrderItem.class);
//                String s = order.getStatus();
//
//                if(s.equals("0"))
//
////                }else if(s.equals("1")){
////                    status.setText("RIDER HAS ACCEPTED YOUR ORDER.");
////                    mDistance.setVisibility(View.VISIBLE);
////                    double distance = Double.parseDouble(order.getDistance());
////                    mDistance.setText("Delivery Distance : "+order.getDistance()+" Km");
////
////                    double Delivery_Charge = distance * deliveryChargePerKM;
////                    double total_pay = Delivery_Charge + Double.parseDouble(amountPayable);
////
////                    mTotalPayable.setVisibility(View.VISIBLE);
////
////                    mTotalPayable.setText("Total Payable Amount: "+total_pay);
////
////
////
////                }else if(s.equals("2")){
////                    status.setText("ORDER HAS BEEN DELIVERED.");
////                }
////                //Toast.makeText(Order_placed.this,"Status "+order.getStatus(),Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        continuebtn = (Button) findViewById(R.id.ContinueButton);
        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Order_placed.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
