package com.example.travelmantics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity {
//lets handle firebase
    //database instance
    private FirebaseDatabase mFirebaseDatabase;
    //define database reference(generic)
    private DatabaseReference mDatabaseReference;
    private EditText textTitle;
    private EditText textPrice;
    private EditText textDescription;
    private TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        //getInstance of firebase
        FirebaseUtil.openFbReference("traveldeals");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference=FirebaseUtil.mDatabaseReference;
        textTitle = (EditText)findViewById(R.id.text_title);
        textPrice = (EditText)findViewById(R.id.text_price);
        textDescription = (EditText)findViewById(R.id.text_description);
        //getExtra. work Modular Next time.
        Intent intent=getIntent();
        //get deal passed by intent. else create a new deal
        TravelDeal deal=(TravelDeal)intent.getSerializableExtra("Deal");
        if(deal==null){
             deal=new TravelDeal();//create new deal if no deal is passed.
            //that is why we have empty constructor in TravelDeal.java


        }
        this.deal=deal;
        textTitle.setText(deal.getTitle());
        textDescription.setText(deal.getDescription());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_insert,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_save){
            saveDeal();
            Toast.makeText(this,"Deal Saved",Toast.LENGTH_LONG).show();
            clean();//rest the editTexts
            backtoList();
            return  true;
        }
        else if(item.getItemId()==R.id.menu_delete){
            deleteDeal();
            Toast.makeText(this,"Note Successfully Deleted",Toast.LENGTH_SHORT).show();
            backtoList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//reset the values in the forms
    private void clean() {
        textTitle.setText("");
        textDescription.setText("");
        textPrice.setText("");
        textTitle.requestFocus();
    }
//save deal into Firebase database
    private void saveDeal() {
        //set deal values to
        //a deal is created
   deal.setTitle(textTitle.getText().toString());
   deal.setDescription(textDescription.getText().toString());
   deal.setPrice(textPrice.getText().toString());
//   TravelDeal deal =new TravelDeal(mTitle,mDescription,mPrice,"");
        //deal created already. no need to create new deal.
        //use an if to either create or edit in firebase
        //if we already hsve a deal id, update, else, push a new one
        if(deal.getId()==null) {
            //push data to firebase
            mDatabaseReference.push().setValue(deal);
        }
        //we have Id already, edit deal
        else{
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }

        
    }
    //delete deal
    private void deleteDeal(){
        if(deal==null){
            Toast.makeText(this,"Please save the note before deleting",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            mDatabaseReference.child(deal.getId()).removeValue();
        }
    }
    private void backtoList(){
        Intent intent=new Intent(this,ListActivity.class);
        startActivity(intent);
    }
}
