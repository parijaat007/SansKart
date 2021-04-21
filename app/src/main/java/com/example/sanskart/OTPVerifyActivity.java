package com.example.sanskart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OTPVerifyActivity extends AppCompatActivity {

    Button Verify;
    EditText OTP;
    ProgressBar Progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_verify);

        Verify = findViewById(R.id.btVerify);
        OTP = findViewById(R.id.etOTP);
        Progress = findViewById(R.id.pgbProgressBar);

        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Progress.setVisibility(View.VISIBLE);
                Verify.setVisibility(View.INVISIBLE);

                if(OTP.getText().toString().trim().length() < 6)
                {
                    Toast.makeText(OTPVerifyActivity.this, "Invalid OTP Format", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String verificationId = getIntent().getStringExtra("otp");
                    String code = OTP.getText().toString();

                    PhoneAuthCredential creds = PhoneAuthProvider.getCredential(verificationId, code);

                    FirebaseAuth.getInstance().signInWithCredential(creds)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Progress.setVisibility(View.GONE);
                                    Verify.setVisibility(View.VISIBLE);

                                    if(task.isSuccessful())
                                    {
                                        Intent intent = new Intent(OTPVerifyActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(OTPVerifyActivity.this, "The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}