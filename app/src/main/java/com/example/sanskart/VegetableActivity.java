package com.example.sanskart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


import com.example.sanskart.Interface.ItemClickListener;
import com.example.sanskart.Model.FoodItem;
import com.example.sanskart.ViewHolder.FoodItemViewHolder;
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

public class VegetableActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button btn_logout;
    TextView username;
    ImageView userprofile;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference userref;
    private DatabaseReference foodref;
    private DatabaseReference cartref;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private GoogleSignInClient mGoogleSignInClient;
    private FloatingActionButton mCartButton;
    public user current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    Intent intent = new Intent(VegetableActivity.this, LoginActivity.class);
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

                if(current_user.Role.equals("Rider")){
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

        mCartButton = findViewById(R.id.cart_btn);

        mCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VegetableActivity.this,CartMainActivity.class);
                startActivity(intent);
            }
        });

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

        navigationView.setCheckedItem(R.id.nav_home);

        foodref = FirebaseDatabase.getInstance().getReference().child("vegetable_menu");
        cartref = FirebaseDatabase.getInstance().getReference("Cart");

        recyclerView = findViewById(R.id.main_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(VegetableActivity.this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_home:
                Intent intent_home = new Intent(VegetableActivity.this,  MainActivity.class);
                startActivity(intent_home);
            case R.id.vegetables_page:
                break;
            case R.id.fruits_page:
                Intent intent_fruit = new Intent(VegetableActivity.this,  FruitActivity.class);
                startActivity(intent_fruit);
                break;
            case R.id.others_page:
                Intent intent_others = new Intent(VegetableActivity.this,  OtherActivity.class);
                startActivity(intent_others);
                break;
            case R.id.bnd_page:
                Intent intent_bnd = new Intent(VegetableActivity.this,  BndActivity.class);
                startActivity(intent_bnd);
                break;
            case R.id.profile_nav:
                Intent intent = new Intent(VegetableActivity.this,  ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();
                startActivity(new Intent(VegetableActivity.this, LoginActivity.class));
                finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthStateListener);

        FirebaseRecyclerOptions<FoodItem> options = new FirebaseRecyclerOptions.Builder<FoodItem>().setQuery(foodref,FoodItem.class).build();
        final FirebaseRecyclerAdapter<FoodItem, FoodItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<FoodItem, FoodItemViewHolder>(options) {

                    private ItemClickListener listener;
                    @Override
                    protected void onBindViewHolder(@NonNull final FoodItemViewHolder holder, final int position, @NonNull FoodItem model) {

                        holder.mFoodItemName.setText(model.getName());
                        holder.mFoodItemPrice.setText("Price: "+ model.getBase_price() + "₹");
                        holder.mShopProvider.setText("By: " + model.getShopName());
                        holder.mAddToCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.mAddToCart.setEnabled(false);
                                addToCart(getRef(position).getKey());
                                //Toast.makeText(MainActivity.this,getRef(position).getKey(),Toast.LENGTH_SHORT).show();
                            }
                        });

                        if(model.getImageUrl() != null)
                        {
                            Log.d("URL: ", model.getImageUrl().toString());
                            StorageReference storageRef;
                            storageRef = FirebaseStorage.getInstance().getReference();

                            storageRef.child("images/" + model.getImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).into(holder.mFoodImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(VegetableActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                        {
                            Log.d("URL: ", "No URL Found");
                        }
                    }

                    @NonNull
                    @Override
                    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item_layout,parent,false);
                        FoodItemViewHolder holder = new FoodItemViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
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
        Intent intent = new Intent(VegetableActivity.this, RetailerMainActivity.class);
        startActivity(intent);
        finish();
    }

    public void addToCart(final String ref){
        DatabaseReference foodItemRef = foodref.child(ref);
        foodItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FoodItem addedItem = dataSnapshot.getValue(FoodItem.class);
                final HashMap<String,Object> cartMap = new HashMap<>();
                cartMap.put("Name",addedItem.getName().toString());
                cartMap.put("Price",addedItem.getBase_price().toString());
                cartMap.put("Quantity","1");
                cartMap.put("Product_ID",ref);

                cartref.child(firebaseAuth.getCurrentUser().getUid()).child("Products")
                        .child(ref)
                        .updateChildren(cartMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(VegetableActivity.this,"Added to Cart",Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
