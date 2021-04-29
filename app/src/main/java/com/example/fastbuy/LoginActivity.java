package com.example.fastbuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastbuy.Model.Users;
import com.example.fastbuy.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

public class LoginActivity extends AppCompatActivity {

    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private  ProgressDialog loadingBar;
    private TextView AdminLink, NotAdminLink;

    private String parentDbName = "Users";
    private CheckBox chkBoxRememberMe;
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        loadingBar = new ProgressDialog(this);

        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });
    }

    private void LoginUser() {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please write your Phone Number", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone, password);
        }
    }

    private void AllowAccessToAccount(String phone, String password) {

        if(chkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(LoginActivity.this, parentDbName + " " + phone, Toast.LENGTH_LONG).show();
                //Log.d(TAG, "allow " + parentDbName + " " + phone);
                if(dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    //Log.d(TAG, "exists");
                    Users userData = new Users();
                    userData.setPhone(dataSnapshot.child(parentDbName).child(phone).child("phone").getValue().toString());
                    userData.setPassword(dataSnapshot.child(parentDbName).child(phone).child("password").getValue().toString());

                    //Log.d(TAG, dataSnapshot.child(parentDbName).child(phone).child("phone").getValue() + " " + phone);
                    if(userData.getPhone().equals(phone))
                    {

                        //Log.d(TAG, "phone equal");
                        if(userData.getPassword().equals(password))
                        {
                            Log.d(TAG, "password equal");
                            if(parentDbName.equals("Admins"))
                            {
                                Log.d(TAG, "1");
                                Toast.makeText(LoginActivity.this, "Welcome, Admin,....logged in successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if(parentDbName.equals("Users"))
                            {
                                Log.d(TAG, "2");
                                Toast.makeText(LoginActivity.this, "logged in successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                //Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                //startActivity(intent);
                            }
                        }
                        else
                        {
                            Log.d(TAG, "3");
                            Toast.makeText(LoginActivity.this, "Password incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                }
                else {
                    Log.d(TAG, "4");
                    Toast.makeText(LoginActivity.this, "Account with this number does not exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                Log.d(TAG, "end");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}