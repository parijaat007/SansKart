package com.example.sanskart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button addbutton, uploadbutton;
    EditText product_name, product_price;
    RadioButton fruitradio, vegradio, mealradio;
    ImageView productimage;
    String uploadedimageUrl = "";

    TextView username;
    ImageView userprofile;
    FirebaseAuth firebaseAuth;
    Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference userref;
    private DatabaseReference fruitref;
    private DatabaseReference mealref;
    private DatabaseReference vegref;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private GoogleSignInClient mGoogleSignInClient;
    private FloatingActionButton mCartButton;
    public user current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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
                    Intent intent = new Intent(AddProductActivity.this, LoginActivity.class);
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

                username = (TextView)findViewById(R.id.tv_username);
                username.setText("Welcome " + current_user.Username + "!");

                userprofile = (ImageView)findViewById(R.id.iv_userimage);
                if(firebaseAuth.getCurrentUser().getPhotoUrl() != null)
                {
                    Picasso.get().load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(userprofile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        toolbar = findViewById(R.id.main_toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

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

        // Add Product

        fruitref = FirebaseDatabase.getInstance().getReference().child("fruit_menu");
        mealref = FirebaseDatabase.getInstance().getReference().child("food_menu");
        vegref = FirebaseDatabase.getInstance().getReference().child("vegetable_menu");

        addbutton = (Button) findViewById(R.id.AddButton);
        product_name = (EditText) findViewById(R.id.mProductName);
        product_price = (EditText) findViewById(R.id.mProductPrice);
        fruitradio = (RadioButton) findViewById(R.id.FruitButton);
        vegradio = (RadioButton) findViewById(R.id.VegetableButton);
        mealradio = (RadioButton) findViewById(R.id.MealButton);

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = product_name.getText().toString();
                String price = product_price.getText().toString();

                if(name.isEmpty() || price.isEmpty())
                {
                    Toast.makeText(AddProductActivity.this, "Please Enter Valid Details", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(vegradio.isChecked())
                    {
                        final HashMap<String,Object> order = new HashMap<>();

                        order.put("base_price",price);
                        order.put("name",name);
                        order.put("imgurl",uploadedimageUrl);

                        vegref.child(name).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(AddProductActivity.this,"Product Added",Toast.LENGTH_SHORT).show();
                                }else
                                    Toast.makeText(AddProductActivity.this,"Error",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else if(mealradio.isChecked())
                    {
                        final HashMap<String,Object> order = new HashMap<>();

                        order.put("base_price",price);
                        order.put("name",name);
                        order.put("imgurl",uploadedimageUrl);

                        mealref.child(name).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(AddProductActivity.this,"Product Added",Toast.LENGTH_SHORT).show();
                                }else
                                    Toast.makeText(AddProductActivity.this,"Error",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else if(fruitradio.isChecked())
                    {
                        final HashMap<String,Object> order = new HashMap<>();

                        order.put("base_price",price);
                        order.put("name",name);
                        order.put("imgurl",uploadedimageUrl);

                        fruitref.child(name).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(AddProductActivity.this,"Product Added",Toast.LENGTH_SHORT).show();
                                }else
                                    Toast.makeText(AddProductActivity.this,"Error",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(AddProductActivity.this, "No category selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Upload Image

        uploadbutton = (Button) findViewById(R.id.UploadButton);
        productimage = (ImageView) findViewById(R.id.productimage);

        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoosePicture();
            }
        });
    }

    private void ChoosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data!=null)
        {
            imageUri = data.getData();
            productimage.setImageURI(imageUri);
            UploadImage();
        }
    }

    private void UploadImage() {

        final ProgressDialog pd = new ProgressDialog(AddProductActivity.this);
        pd.setTitle("Uploading...");
        pd.show();

        String imagepath = UUID.randomUUID().toString();
        uploadedimageUrl = imagepath;

        StorageReference picref = storageReference.child("images/" + imagepath);

        picref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(AddProductActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddProductActivity.this, "Image Failed to Upload!", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double prog = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Percentage: " + (int)prog + "%");
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_prod:
                break;
            case R.id.nav_home:
                Intent intent2 = new Intent(AddProductActivity.this, MainActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.profile_nav:
                Intent intent = new Intent(AddProductActivity.this,  ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AddProductActivity.this, LoginActivity.class));
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
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
}
