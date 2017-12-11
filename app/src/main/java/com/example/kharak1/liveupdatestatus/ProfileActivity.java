package com.example.kharak1.liveupdatestatus;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ImageView mUserImageView;
    private EditText mUserNameEditText;
    private Button mUpdateButton;

    private String passedUserId;
    private DatabaseReference mUsersDBRef;
    private FirebaseUser currentUser;
    private static final int REQUEST_PHOTO_CAPTURE = 1;
    private static final int REQUEST_PHOTO_PICK = 2;

    private Uri mPhotoUri;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // assign variables
        mUserImageView = (ImageView)findViewById(R.id.userImageViewProfile);
        mUserNameEditText = (EditText)findViewById(R.id.userNameEditTextProfile);
        mUpdateButton = (Button)findViewById(R.id.updateProfileButton);

        passedUserId = getIntent().getStringExtra("USER_ID");
        mUsersDBRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();

        mUsersDBRef.child(passedUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("displayName").getValue(String.class);
                String photoUrl = dataSnapshot.child("photoUrl").getValue(String.class);

                mUserNameEditText.setText(name);

                try {
                    Picasso.with(ProfileActivity.this).load(photoUrl).placeholder(R.mipmap.ic_launcher).into(mUserImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(!passedUserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            // not my profile
            mUserImageView.setEnabled(false);
            mUserNameEditText.setFocusable(false);
            mUpdateButton.setVisibility(View.GONE);
        }else{
            // it's my profile
            mUserImageView.setEnabled(true);
            mUserNameEditText.setFocusable(true);
            mUpdateButton.setVisibility(View.VISIBLE);
        }

        // listen to imageview click
        mUserImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setMessage("How would you like to add photo");
                builder.setPositiveButton("Take photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // take photo
                        dispatchTakePhotoIntent();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton("Choose photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // choose photo
                        dispatchChoosePhotoIntent();
                    }
                });
                builder.create().show();
            }
        });

        // listen to update button click
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update username
                String newUserName = mUserNameEditText.getText().toString().trim();
                if(!TextUtils.isEmpty(newUserName)){
                    updateUserName(newUserName);
                }
                // update user image
                if(mPhotoUri != null){
                    updateUserPhoto(mPhotoUri);
                }

            }
        });
        Toast.makeText(getApplicationContext(), currentUser.getDisplayName(), Toast.LENGTH_LONG).show();
    }

    private void updateUserName(final String newUserName){
        // to update user's info.
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUserName)
                .build();
        currentUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // update users DB as well
                Map<String, Object> updateUserNameMap = new HashMap<>();
                updateUserNameMap.put("displayName",newUserName);
                mUsersDBRef.child(passedUserId).updateChildren(updateUserNameMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Success updating name!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
    private void updateUserPhoto(final Uri photoUri){
        // make a table "UserImages" under Storage in Firebase
        StorageReference userImageRef = mStorage.child("UserImages").child(currentUser.getUid()).child(photoUri.getLastPathSegment());
        userImageRef.putFile(photoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Uri uploadedImageUri = task.getResult().getDownloadUrl();

                Map<String, Object> updatePhotoMap = new HashMap<>();
                updatePhotoMap.put("photoUrl", uploadedImageUri.toString());
                mUsersDBRef.child(currentUser.getUid()).updateChildren(updatePhotoMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Success updating photo!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void dispatchTakePhotoIntent(){
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE );
        if(takePhotoIntent.resolveActivity(getPackageManager()) != null){
            // we can take the photo
            startActivityForResult(takePhotoIntent, REQUEST_PHOTO_CAPTURE);
        }
    }

    private void dispatchChoosePhotoIntent(){
        Intent choosePhoto = new Intent(Intent.ACTION_PICK);
        choosePhoto.setType("image/*");
        startActivityForResult(choosePhoto, REQUEST_PHOTO_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_PHOTO_CAPTURE && resultCode == RESULT_OK){
            //success taking photo
            mPhotoUri = data.getData();
            mUserImageView.setImageURI(mPhotoUri);
        }else if(requestCode == REQUEST_PHOTO_PICK && resultCode == RESULT_OK){
            // success choosing/picking photo
            mPhotoUri = data.getData();
            mUserImageView.setImageURI(mPhotoUri);
        }

    }
}
