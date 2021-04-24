package com.example.sanskart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.codemybrainsout.ratingdialog.RatingDialog;
import com.example.sanskart.Interface.ItemClickListener;
import com.example.sanskart.Model.FoodItem;
import com.example.sanskart.Model.OrderItem;
import com.example.sanskart.ViewHolder.FoodItemViewHolder;
import com.example.sanskart.ViewHolder.OrderItemsAdapter;
import com.example.sanskart.ViewHolder.OrderItemsAdapterCustomer;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class MyOrdersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView username;
    ImageView userprofile;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference userref;
    private DatabaseReference orderref;
    private OrderItemsAdapterCustomer adapter;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private GoogleSignInClient mGoogleSignInClient;
    public user current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public  void  onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user==null){
                    Intent intent = new Intent(MyOrdersActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };


        userref = FirebaseDatabase.getInstance().getReference("user").child(firebaseAuth.getCurrentUser().getUid());
        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_user = dataSnapshot.getValue(user.class);

                if(current_user.Role.equals("Retailer")){
                    gotorider();
                }
                if(current_user.Role.equals("Customer"))
                {
                    username = (TextView)findViewById(R.id.tv_username);
                    username.setText("Welcome " + current_user.Username + "!");

                    userprofile = (ImageView)findViewById(R.id.iv_userimage);
                    if(firebaseAuth.getCurrentUser().getPhotoUrl() != null)
                    {
                        Picasso.get().load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(userprofile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        toolbar = findViewById(R.id.main_toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        firebaseAuth = FirebaseAuth.getInstance();
        orderref = FirebaseDatabase.getInstance().getReference("Orders");
        setUpRecyclerView();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_orders);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_home:
                Intent intent_home = new Intent(MyOrdersActivity.this,  MainActivity.class);
                startActivity(intent_home);
            case R.id.vegetables_page:
                Intent intent_vegetable = new Intent(MyOrdersActivity.this,  VegetableActivity.class);
                startActivity(intent_vegetable);
                break;
            case R.id.fruits_page:
                Intent intent_fruit = new Intent(MyOrdersActivity.this, FruitActivity.class);
                startActivity(intent_fruit);
                break;
            case R.id.nav_orders:
                break;
            case R.id.others_page:
                Intent intent_others = new Intent(MyOrdersActivity.this,  OtherActivity.class);
                startActivity(intent_others);
                break;
            case R.id.bnd_page:
                Intent intent_bnd = new Intent(MyOrdersActivity.this,  BndActivity.class);
                startActivity(intent_bnd);
                break;
            case R.id.profile_nav:
                Intent intent = new Intent(MyOrdersActivity.this,  ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();
                startActivity(new Intent(MyOrdersActivity.this, LoginActivity.class));
                finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    private void gotorider() {
        Intent intent = new Intent(MyOrdersActivity.this, RetailerMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.rider_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        getLastKnownLocation();

        FirebaseRecyclerOptions<OrderItem> options = new FirebaseRecyclerOptions.Builder<OrderItem>()
                .setQuery(orderref.orderByChild("Customer_UID").equalTo(firebaseAuth.getCurrentUser().getUid()),OrderItem.class)
                .build();

        adapter = new OrderItemsAdapterCustomer(options);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OrderItemsAdapterCustomer.OnItemClickListener() {
            @Override
            public void onItemClick(final String orderid)
            {
                final RatingDialog ratingDialog = new RatingDialog.Builder(MyOrdersActivity.this)
                        .threshold(6)
                        .formHint("Tell Us More!")
                        .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                            @Override
                            public void onFormSubmitted(String feedback) {
                                orderref.child(orderid).child("Feedback").setValue(feedback);
                                Toast.makeText(MyOrdersActivity.this, "Feedback Recieved", Toast.LENGTH_SHORT).show();
                            }
                        }).build();
                ratingDialog.show();
            }
        });

    }

}
