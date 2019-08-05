package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
//arrayist to hold data retrieved
    ArrayList<TravelDeal>deals;
     //database instance and reference
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    //Listener to liten to database changes
    private ChildEventListener mChildlistener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu,menu);
        MenuItem insertMenu=menu.findItem(R.id.insert_menu);
        if(FirebaseUtil.isAdmin==true){
            insertMenu.setVisible(true);
        }
        else{
            insertMenu.setVisible(false);
        }
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.insert_menu){
            Intent intent =new Intent(this, DealActivity.class);
            startActivity(intent);
            return true;
        }
        else if(item.getItemId()==R.id.logout_menu){
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("logout","user logged out successfully");
                        FirebaseUtil.attachListener();
                        // ...
                    }

                });
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        //
        super.onResume();

        FirebaseUtil.openFbReference("traveldeals",this);
        //initialize database
//        FirebaseUtil.openFbReference("traveldeals");
//        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
//        mDatabaseReference=FirebaseUtil.mDatabaseReference;
//        //childeventListener
//        mChildlistener=new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                //retrieve deals
//
//                //create an object of deal to hold the deal values
//                TravelDeal deal=dataSnapshot.getValue(TravelDeal.class);
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        //add listener to databaselistener
//        mDatabaseReference.addChildEventListener(mChildlistener);
        RecyclerView dealRecyclerview=(RecyclerView)findViewById(R.id.rv_deals);
        LinearLayoutManager mLayoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        DealRecyclerAdapter adapter=new DealRecyclerAdapter();
        dealRecyclerview.setAdapter(adapter);
        dealRecyclerview.setLayoutManager(mLayoutManager);
        FirebaseUtil.attachListener();
    }
    public void showMenu(){
        invalidateOptionsMenu();
    }
}
