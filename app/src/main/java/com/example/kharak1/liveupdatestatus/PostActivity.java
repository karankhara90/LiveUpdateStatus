package com.example.kharak1.liveupdatestatus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kharak1.liveupdatestatus.model.Status;
import com.example.kharak1.liveupdatestatus.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostActivity extends AppCompatActivity {

    private EditText mPostEditText;
    private Button mPostButton;

    private DatabaseReference mStatusDB;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mDialog = new ProgressDialog(this);
        mStatusDB = FirebaseDatabase.getInstance().getReference().child("Status");

        mPostEditText = (EditText)findViewById(R.id.postEditText);
        mPostButton = (Button) findViewById(R.id.postButton);

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setMessage("Posting");
                mDialog.show();
                String status = mPostEditText.getText().toString();

                if(status.isEmpty()){
                    //
                    mDialog.dismiss();
                }else{
                    // proceed
                    postStatusToFB(status, FirebaseAuth.getInstance().getUid());

                    startActivity(new Intent(PostActivity.this, HomeActivity.class));
                }
            }
        });
    }

    private void postStatusToFB(String userStatus, String userId){
        Status status = new Status(userStatus, userId);
        mStatusDB.push().setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDialog.dismiss();
                Toast.makeText(PostActivity.this, "Success", Toast.LENGTH_LONG).show();
            }
        });
    }

}
