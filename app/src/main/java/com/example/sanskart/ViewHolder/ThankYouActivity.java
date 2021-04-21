package com.example.sanskart.ViewHolder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sanskart.MainActivity;
import com.example.sanskart.R;

public class ThankYouActivity extends AppCompatActivity {

    Button ContinueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        ContinueButton = findViewById(R.id.ContinueButton);

        ContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ThankYouActivity.this, MainActivity.class));
            }
        });
    }
}
