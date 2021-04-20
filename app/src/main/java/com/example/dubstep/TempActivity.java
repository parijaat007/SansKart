package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TempActivity extends AppCompatActivity {

    /////////////////////////////////////////////////////////////////
    // 1. CREATE THESE MEMBER VARIABLES.
    //////////////////////////////////////////////////////////////////
    private DatabaseReference fromReference;
    private DatabaseReference toReference;
    private String PhoneNumber;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_temp);


        //////////////////////////////////////////////////////////////
        // 2. ADD THE FOLLOWING IN ON CREATE
        //////////////////////////////////////////////////////////////

        PhoneNumber = getIntent().getStringExtra("PhoneNumber");
        uid = getIntent().getStringExtra("UID");
        fromReference = FirebaseDatabase.getInstance().getReference("Cart").child(uid);
        toReference = FirebaseDatabase.getInstance().getReference("Orders");


        // 3. CALL THESE METHODS IN THIS ORDER. COPY CART ITEMS AND ADDUSERINFO CAN BE CALLED IN ONCREATE
        // BUT PREFERABLY CALL ALL THREE INSIDE ON CLICK OF A BUTTON.

        copyCartItems(fromReference,toReference);
        addUserInfo(PhoneNumber,toReference);

        //4. CALL THIS METHOD ONLY WHEN YOU GET THE LATITUDE AND LONGITUDE OF THE USER.

        double lat=72.5,longt=23.86;
        addUserLocation(lat,longt,toReference);


    }

    private void addUserLocation(final double lat, final double longt, final DatabaseReference OrderNode) {
        OrderNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,Object> loc = new HashMap<>();
                loc.put("Latitude",lat);
                 loc.put("Longitude",longt);

                OrderNode.child(uid).child("Customer_Info").child("Location").setValue(loc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addUserInfo(final String phoneNumber, final DatabaseReference OrderNode) {
        OrderNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,Object> h = new HashMap<>();
                h.put("PhoneNumber",phoneNumber);
                OrderNode.child(uid).child("Customer_Info").setValue(h);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void copyCartItems(DatabaseReference from, final DatabaseReference to) {

        from.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                to.child(uid).setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Toast.makeText(TempActivity.this,"COPIED",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(TempActivity.this,"ERROR",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
