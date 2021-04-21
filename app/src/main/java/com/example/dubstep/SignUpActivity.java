package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText txtFullName, txtusername, txtemail, txtpassword, txtpassword2, txtMobileNumber;
    RadioButton customerButton, riderButton;
    Button SignUp;
    //RadioGroup CustomerTypeGroup;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog1;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtemail = findViewById(R.id.EmailEditText);
        txtFullName = findViewById(R.id.NameEditText);
        txtusername = findViewById(R.id.UsernameEditText);
        txtMobileNumber = findViewById(R.id.MobileNumberEditText);
        txtpassword = findViewById(R.id.PasswordEditText);
        txtpassword2 = findViewById(R.id.PasswordRetypeText);
        SignUp = findViewById(R.id.SignUpButton);
        customerButton = findViewById(R.id.CustomerButton);
        riderButton =  findViewById(R.id.RiderButton);
        //regularButton = (RadioButton) findViewById(R.id.RegularButton);
        //irregularButton = (RadioButton) findViewById(R.id.IrregularButton);
        //CustomerTypeGroup = (RadioGroup) findViewById(R.id.CustomerTypeGroup);

        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        firebaseAuth = FirebaseAuth.getInstance();
        //CustomerTypeGroup.setVisibility(View.INVISIBLE);

        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerButton.isChecked()){
                    //CustomerTypeGroup.setVisibility(View.VISIBLE);
                }
            }
        });

        riderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (riderButton.isChecked()){
                    //CustomerTypeGroup.setVisibility(View.INVISIBLE);
                }
            }
        });



        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog1 = new ProgressDialog(SignUpActivity.this);
                progressDialog1.show();
                progressDialog1.setContentView(R.layout.progress_dialog);
                progressDialog1.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );

                final String email = txtemail.getText().toString().trim();
                String password = txtpassword.getText().toString().trim();
                String password2 = txtpassword2.getText().toString().trim();
                final String fullName = txtFullName.getText().toString();
                final String Username = txtusername.getText().toString();
                final String MobileNumber = txtMobileNumber.getText().toString();
                String Role = "";
                //String CustomerType = "";

                if (TextUtils.isEmpty(fullName)) {
                    Toast.makeText(SignUpActivity.this,  "Please Enter Full Name",Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(Username)) {
                    Toast.makeText(SignUpActivity.this,  "Please Enter Username",Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(MobileNumber)) {
                    Toast.makeText(SignUpActivity.this,  "Please Enter Username",Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this,  "Please Enter Email",Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this,  "Please Enter Password",Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(password2)) {
                    Toast.makeText(SignUpActivity.this,  "Please Reenter Password",Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    return;
                }
                if (!txtpassword.getText().toString().equals(txtpassword2.getText().toString())) {
                    Toast.makeText(SignUpActivity.this,  "Please Reenter The Same Password",Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    return;
                }
                if (customerButton.isChecked() == false && riderButton.isChecked() == false) {
                    Toast.makeText(SignUpActivity.this,  "Please select either Customer or Rider",Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    return;
                }
                if (customerButton.isChecked()) {

//                    if (regularButton.isChecked() == false && irregularButton.isChecked() == false) {
//                        Toast.makeText(SignUpActivity.this, "Please select either Regular or Irregular", Toast.LENGTH_SHORT).show();
//                        progressDialog1.dismiss();
//                        return;
//                    }
                    Role = "Customer";
//                    if (regularButton.isChecked()){
//                        CustomerType = "Regular";
//                    } else if (irregularButton.isChecked()){
//                        CustomerType = "Irregular";
//                    }
                } else if (riderButton.isChecked()) {
                    Role = "Retailer";
                }

                final String finalRole = Role;
//                final String finalCustomerType = CustomerType;
                final String finalCustomerType = "Regular";
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                     user details = new user(
                                             fullName,
                                             Username,
                                             MobileNumber,
                                             email,
                                             finalRole,
                                             finalCustomerType
                                    );

                                     FirebaseDatabase.getInstance().getReference("user")
                                             .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                             .setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                             startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                             progressDialog1.dismiss();
                                         }
                                     });


                                } else {
                                    task.getException().printStackTrace();
                                    Toast.makeText(SignUpActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                    progressDialog1.dismiss();
                                }

                            }
                        });
            }
        });
    }
}
