package com.example.kharak1.liveupdatestatus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.kharak1.liveupdatestatus.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText pwdEditText;
    private Button registerButton;

    // Firebase var
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDB;


    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // assign the var
        nameEditText = (EditText) findViewById(R.id.nameEditTextResgister);
        emailEditText = (EditText) findViewById(R.id.emailEditTextRegister);
        pwdEditText = (EditText) findViewById(R.id.passwordEditTextRegister);
        registerButton = (Button) findViewById(R.id.registerButton);

        // get firebase-authorization object
        mAuth = FirebaseAuth.getInstance();
        mUsersDB = FirebaseDatabase.getInstance().getReference().child("Users");  // if the "Users" db doesn't exist, it will create it automatically.

        mDialog = new ProgressDialog(this);

        // listen to the register button click
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setMessage("Please wait...");
                mDialog.show();
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String pwd = pwdEditText.getText().toString().trim();

                if (name.isEmpty()) {
                    mDialog.dismiss();
                    // make alert.
                    showAlertDialog("Error", "Name can not be empty");
                } else if (email.isEmpty()) {
                    mDialog.dismiss();
                    showAlertDialog("Error", "Email can not be empty");
                } else if (pwd.isEmpty()) {
                    mDialog.dismiss();
                    showAlertDialog("Error", "Password can not be empty");
                } else {
                    registerUserToFirebase(email, pwd, name);
                }
            }
        });

    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private  void registerUserToFirebase(String email, String password, final String name) {
        // firebase
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                try{
                    if (!task.isSuccessful()) {
                        //error registering user
                        showAlertDialog("Error", task.getException().getMessage());
                    } else {
                        // success.
                        final FirebaseUser currentUser = task.getResult().getUser();

                        // to update user's info.
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        currentUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                User newUser = new User(currentUser.getDisplayName(), currentUser.getEmail(), "", currentUser.getUid());

                                // this will assign uid to new user just created into database.
                                mUsersDB.child(currentUser.getUid()).setValue(newUser);
                                mUsersDB.child(currentUser.getDisplayName()).setValue(newUser);

                                // take user home.
                                finish();
                                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            }
                        });

                    }
                }catch (Exception e){
                    System.out.println("Exception e: "+e);
                }
            }
        });
    }

}

