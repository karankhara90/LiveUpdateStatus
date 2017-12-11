package com.example.kharak1.liveupdatestatus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kharak1.liveupdatestatus.model.Status;
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.example.kharak1.liveupdatestatus.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private DatabaseReference mStatusDBRef;
    private DatabaseReference mUsersDBRef;

    private RecyclerView mHomeRecycler;  // not used

    private ListView mListView;
    private Context context;
//    private ArrayAdapter<String> mAdapterUserID;
    private MyCustomAdapter mAdapter;
//    private MyCustomAdapter mAdapterUserStatus;

    private ArrayList<String> mPhotoArrayList = new ArrayList<>();
    private ArrayList<String> mNameArrayList = new ArrayList<String>();
    private ArrayList<String> mUserIdArrayList = new ArrayList<String>();
    private ArrayList<String> mStatusArrayList = new ArrayList<String>();

    private View rowView;

    public static final String TAG = HomeActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mStatusDBRef = FirebaseDatabase.getInstance().getReference().child("Status");
        mUsersDBRef =  FirebaseDatabase.getInstance().getReference().child("Users");

        mListView = (ListView)findViewById(R.id.homeListView);

        mAdapter = new MyCustomAdapter(this, R.layout.status_row, mNameArrayList);
//        mAdapterUserStatus = new MyCustomAdapter(this, R.layout.status_row, mStatusArrayList);

        mListView.setAdapter(mAdapter);

//        mListView.setAdapter(mAdapterUserStatus);

        mUsersDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User userObj = dataSnapshot.getValue(User.class);
                Log.e(TAG,"============= "+userObj.getDisplayName());
                mNameArrayList.add(userObj.getDisplayName());
                mUserIdArrayList.add(userObj.getUserId());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mStatusDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Status statusObj = dataSnapshot.getValue(Status.class);
//                mFullNameArrayList.add(statusObj.getUserId());
                Log.e(TAG,"------------- "+statusObj.getUserStatus());
//                Log.e(TAG,"************* "+userObj.getDisplayName());

                mStatusArrayList.add(statusObj.getUserStatus());
//                mNameArrayList.add(userObj.getDisplayName());

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null){
            goToLogin();
        }
    }


    public class MyCustomAdapter extends ArrayAdapter<String>
    {
        public MyCustomAdapter(Context ctx, int txtViewResourceId, List<String> objects) {
            super(ctx, txtViewResourceId, objects);
        }
        //  The ListView( which is type of AdapterView) instance calls the getView() method on the adapter for each data element.
        // In this method the adapter creates the row layout and maps the data to the views in the layout.
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            Log.e(TAG,"----------=--=----------");
            LayoutInflater inflater = getLayoutInflater(); //To inflate XML layout file,you can use LayoutInflator system service.
            rowView = inflater.inflate(R.layout.status_row, parent, false);

            context = HomeActivity.this;
            String imageUrl = "1";
                    //(ImageButton)
            Log.e(TAG, "imageURL: "+imageUrl);
            ImageButton userImageButton = (ImageButton)rowView.findViewById(R.id.userImageButton);
            Picasso.with(context).load(imageUrl).placeholder(R.mipmap.ic_launcher).into(userImageButton);

            userImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // go to Profile
                    Intent goToProfileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                    Log.e(TAG, "user id: "+mUserIdArrayList.get(position));
                    goToProfileIntent.putExtra("USER_ID", mUserIdArrayList.get(position));
                    startActivity(goToProfileIntent);
                }
            });

            TextView userNameTextView = (TextView) rowView.findViewById(R.id.userNameTextView);
            Log.e(TAG,"position: "+position);

            Log.e(TAG,"mFullNameArrayList.get(position): "+ mNameArrayList.get(position));
            userNameTextView.setText(mNameArrayList.get(position));
            TextView userStatusTextView = (TextView)rowView.findViewById(R.id.userStatusTextView);
            userStatusTextView.setText(mStatusArrayList.get(position));

            try{
                Log.e(TAG,"position: "+position);
            }catch (Exception excp1){
                Log.e(TAG,"excp1 in--- "+excp1);
            }
            return rowView;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mAuth.getCurrentUser()==null){
            goToLogin();
        }
    }

    private void goToLogin(){
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }

    // inbuilt method to show options menu named "home_menu".. it will have "Logout" option in our case.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutMenu:
                mAuth.signOut();
                finish();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                return true;

            case R.id.addNewMenu:
                startActivity(new Intent(HomeActivity.this, PostActivity.class));

            case R.id.myProfileMenu:
                Intent goToProfileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                goToProfileIntent.putExtra("USER_ID", mAuth.getCurrentUser().getUid());
                startActivity(goToProfileIntent);
        }
        return super.onOptionsItemSelected(item);
    }


    /*@Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Status")
                .limitToLast(50);

        FirebaseRecyclerOptions<Status> options =
                new FirebaseRecyclerOptions.Builder<Status>()
                        .setQuery(mStatusDBRef, Status.class)
                        .build();


        FirebaseRecyclerAdapter<Status, StatusViewHolder> firebaseRecyclerAdapter =
            new FirebaseRecyclerAdapter<Status, StatusViewHolder>(options) {
                @Override
                protected void onBindViewHolder(StatusViewHolder holder, int position, Status model) {
                    holder.setUserName(model.getUserId());
                    holder.setUserStatus(model.getUserStatus());

                }

                @Override
                public StatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.status_row, parent, false);

                    return new StatusViewHolder(view);
                }


        };
        mHomeRecycler.setAdapter(firebaseRecyclerAdapter);
    }*/

    public static class StatusViewHolder extends RecyclerView.ViewHolder{

        View view;
        ImageButton userImageButton;
        public StatusViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            userImageButton = (ImageButton)view.findViewById(R.id.userImageButton);
        }

        public void setUserPhotoUrl(Context context, String imageUrl){
            userImageButton = (ImageButton)view.findViewById(R.id.userImageButton);
            Picasso.with(context).load(imageUrl).placeholder(R.mipmap.ic_launcher).into(userImageButton);
        }

        public  void setUserName(String name){
            TextView userNameTextView = (TextView)view.findViewById(R.id.userNameTextView);
            userNameTextView.setText(name);
        }

        public  void setUserStatus(String status){
            TextView userStatusTextView = (TextView)view.findViewById(R.id.userStatusTextView);
            userStatusTextView.setText(status);
        }
    }

}
