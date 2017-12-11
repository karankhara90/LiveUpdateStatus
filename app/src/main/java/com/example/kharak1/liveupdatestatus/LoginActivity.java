package com.example.kharak1.liveupdatestatus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText pwdEditText;
    private Button loginButton;

    //    private FirebaseAuth mAuth;
    protected FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = (EditText) findViewById(R.id.emailEditTextLogin);
        pwdEditText = (EditText) findViewById(R.id.passwordEditTextLogin);
        loginButton = (Button) findViewById(R.id.loginButton);

        // get firebase-authorization object
        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);

        // listen to the register button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setMessage("Please wait...");
                mDialog.show();
                String email = emailEditText.getText().toString().trim();
                String pwd = pwdEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    mDialog.dismiss();
                    showAlertDialog("Error", "Email can not be empty");
                } else if (pwd.isEmpty()) {
                    mDialog.dismiss();
                    showAlertDialog("Error", "Password can not be empty");
                } else {
                    loginViaFirebase(email, pwd);
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

    private  void loginViaFirebase(String email, String password) {
        // firebase
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                try{
                    mDialog.dismiss();
                    if (!task.isSuccessful()) {
                        //error registering user
                        showAlertDialog("Error", task.getException().getMessage());
                    } else {
                        // success. take the user home
                        finish();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    }
                }catch (Exception e){
                    System.out.println("Exception e: "+e);
                }
            }
        });
    }

    // inbuilt method to show options menu named "home_menu".. it will have "Logout" option in our case.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.createAccountMenu:
                mAuth.signOut();
                finish();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
