package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {
    private static final int PICTURE_RESULTS = 42;
    //lets handle firebase
    //database instance
    private FirebaseDatabase mFirebaseDatabase;
    //define database reference(generic)
    private DatabaseReference mDatabaseReference;
    private EditText textTitle;
    private EditText textPrice;
    private EditText textDescription;
    private TravelDeal deal;
    private ImageView imageView;
    private Button btnImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        //getInstance of firebase
       // FirebaseUtil.openFbReference("traveldeals",this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
         mDatabaseReference=FirebaseUtil.mDatabaseReference;
        textTitle = (EditText)findViewById(R.id.text_title);
        textPrice = (EditText)findViewById(R.id.text_price);
        textDescription = (EditText)findViewById(R.id.text_description);
        imageView = (ImageView) findViewById(R.id.image);
        btnImage = (Button)findViewById(R.id.btn_image);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //an implicit intent to get the image.
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                //start activity for results to get results
                startActivityForResult(intent.createChooser(intent,"Insert Picture"),PICTURE_RESULTS);
            }
        });
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
        textPrice.setText(deal.getPrice());
        textDescription.setText(deal.getDescription());
        showImage(deal.getImageUrl());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICTURE_RESULTS &&resultCode==RESULT_OK){
             //UPLOAD TO Firebase.
            Uri imageUri=data.getData();
          final   StorageReference reference=FirebaseUtil.mStorageReference
                    .child(imageUri.getLastPathSegment());
           reference.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   Task<Uri> url=reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           Log.d("url", "onSuccess: uri= "+ uri.toString());
                           String url= uri.toString();
                           deal.setImageUrl(url);
                           showImage(url);
                       }
                   });
                   String pictureName = taskSnapshot.getStorage().getPath();

                   deal.setImageName(pictureName);
                  // Log.d("Url: ", url);
                  // Log.d("Name", pictureName);

               }
           });

        }
    }

    private void showImage(String url) {
        if (url != null && url.isEmpty() == false) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(imageView);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_insert,menu);
        //disable some settings for normal users
        if(FirebaseUtil.isAdmin==true){
            menu.findItem(R.id.menu_delete).setVisible(true);
            menu.findItem(R.id.menu_save).setVisible(true);
            enableEditTexts(true);
        }
        else{
            menu.findItem(R.id.menu_delete).setVisible(false);
            menu.findItem(R.id.menu_save).setVisible(false);
            enableEditTexts(false);
        }
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
           // Log.d("image name",deal.getImageName());
            //delete picture.
            if(deal.getImageName()!=null&&deal.getImageName().isEmpty()==false){
                StorageReference picRef = FirebaseUtil.mStorage.getReference().child(deal.getImageName());
                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Delete Image", "Image Successfully Deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Delete Image", e.getMessage());
                    }
                });
            }
        }
    }
    private void backtoList(){
        Intent intent=new Intent(this,ListActivity.class);
        startActivity(intent);
    }
    private void enableEditTexts(boolean isEnabled){
        textPrice.setEnabled(isEnabled);
        textTitle.setEnabled(isEnabled);
        textDescription.setEnabled(isEnabled);
        btnImage.setEnabled(isEnabled);


    }
}
