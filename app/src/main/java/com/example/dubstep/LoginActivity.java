package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText txtemail, txtpassword;
    Button btn_login;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    private ImageButton GoogleSignInButton;
    private ImageButton PhoneSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 1;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtemail = (EditText) findViewById(R.id.EmailLoginEditText);
        txtpassword = (EditText) findViewById(R.id.PasswordLoginEditText);
        btn_login = (Button) findViewById(R.id.LoginButton);
        GoogleSignInButton = (ImageButton) findViewById(R.id.GoogleSignInButton);
        PhoneSignInButton = (ImageButton) findViewById(R.id.PhoneSignInButton);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GSignIn();
            }
        });

        PhoneSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneSignIn();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFireBaseUser = firebaseAuth.getCurrentUser();
                if (mFireBaseUser != null){
                    //FirebaseAuth.getInstance().signOut();
                    //mGoogleSignInClient.signOut();
                    //Toast.makeText(LoginActivity.this,  "You are logged in",Toast.LENGTH_LONG).show();
                }
                else {
                    //Toast.makeText(LoginActivity.this,  "Please Log in",Toast.LENGTH_LONG).show();
                }
            }
        };

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );

                String email = txtemail.getText().toString().trim();
                String password = txtpassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this,  "Please Enter Email",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this,  "Please Enter Password",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {


                                    FirebaseDatabase.getInstance().getReference("user")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String Role = dataSnapshot.child("Role").getValue().toString();
                                            if (Role.equals("Customer")){
                                                progressDialog.dismiss();
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finish();
                                            }
                                            else if (Role.equals("Rider")) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(LoginActivity.this, RetailerMainActivity.class));
                                                finish();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this,  "Error fetching Rider or Customer from database",Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                } else {

                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this,  "Login Failed or User not Available",Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthStateListener);

        FirebaseUser user = firebaseAuth.getCurrentUser();

    }

    public void btn_register(View view) {

        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
    }

    public void GSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void PhoneSignIn() {
        Intent intent = new Intent(LoginActivity.this, OTPSendActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(LoginActivity.this,  "Login Failed or User not Available",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            boolean newuser = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (newuser) {
                                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                            }
                            else {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class)); //change later based on rider/customer
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,  "Login Failed or User not Available",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


}
