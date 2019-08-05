package com.example.travelmantics;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//a class to handle firebase instance and database reference

public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static  FirebaseUtil firebaseUtil;
    //fireebase Auth
    public static FirebaseAuth mFirebaseAuth ;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static ArrayList<TravelDeal>mDeals;
    private static final int RC_SIGN_IN = 123;
    private static ListActivity caller;
    private FirebaseUtil(){}
    public static boolean isAdmin;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageReference;

    public static  void openFbReference(String ref,final ListActivity callerActivity){
        if(firebaseUtil==null){
            firebaseUtil=new FirebaseUtil();
            mFirebaseDatabase=FirebaseDatabase.getInstance();
            //get instance of firebase Auth.
            mFirebaseAuth=mFirebaseAuth.getInstance();

            caller=callerActivity;
            mAuthListener =new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if(firebaseAuth.getCurrentUser()==null) {
                        FirebaseUtil.signIn();
                    }
                    else {
                        String userId=firebaseAuth.getUid();
                        //check if signed in user is admin
                        checkAdmin(userId);

                    }
                    Toast.makeText(callerActivity.getBaseContext(),"welcome back",Toast.LENGTH_LONG).show();
                }
            };
            connectStorage();

        }


        mDeals=new ArrayList<TravelDeal>();
        mDatabaseReference=mFirebaseDatabase.getReference().child(ref);
    }

    public static void signIn(){
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]

    }
    //check if signed in user is admin
    private static void checkAdmin(String uid){
        FirebaseUtil.isAdmin=false;
        DatabaseReference reference=mFirebaseDatabase.getReference().child("administrators")
                .child(uid);
        Log.d("admin","you are an administrator");
        //add childevent listener
        ChildEventListener childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin=true;
                caller.showMenu();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addChildEventListener(childEventListener);
    }
    //two methods to attach and detach listener.
    public static  void attachListener(){
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }
    public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    public static void connectStorage(){
        //connect to storage
        mStorage=FirebaseStorage.getInstance();
        mStorageReference=mStorage.getReference().child("deals_pictures");


    }
}
