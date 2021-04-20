package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPSendActivity extends AppCompatActivity {

    private Button Send;
    private EditText PhoneNumber;
    private ProgressBar Progress;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private static final String TAG = "MAIN_TAG";
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_send);

        Send = findViewById(R.id.btSend);
        PhoneNumber = findViewById(R.id.etPhoneNumber);
        Progress = findViewById(R.id.pgbProgressBar);

        fAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(OTPSendActivity.this, "Verification Complete", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OTPSendActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(OTPSendActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);

                mVerificationId = verificationId;
                forceResendingToken = token;
                Intent intent = new Intent(OTPSendActivity.this, OTPVerifyActivity.class);
                intent.putExtra("otp", mVerificationId);
                startActivity(intent);
            }
        };

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = PhoneNumber.getText().toString().trim();
                Send.setVisibility(View.INVISIBLE);
                Progress.setVisibility(View.VISIBLE);

                if(phone.length() < 10)
                {
                    Toast.makeText(OTPSendActivity.this, "Enter a valid Phone Number", Toast.LENGTH_SHORT).show();
                    Send.setVisibility(View.VISIBLE);
                    Progress.setVisibility(View.GONE);
                }
                else
                {
                    startPhoneVerification("+91" + phone);
                }
            }
        });
    }

    private void startPhoneVerification(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(fAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}