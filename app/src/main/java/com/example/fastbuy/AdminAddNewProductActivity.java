package com.example.fastbuy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        
        CategoryName = getIntent().getExtras().get("Category").toString();


    }
}