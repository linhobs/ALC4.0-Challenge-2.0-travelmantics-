package com.example.travelmantics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.insert_menu){
            Intent intent =new Intent(this, DealActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.attachListener();
    }
}
