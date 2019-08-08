package com.example.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class DealRecyclerAdapter extends RecyclerView.Adapter<DealRecyclerAdapter.viewHolder> {
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<TravelDeal>deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    //Listener to liten to database changes
    private ChildEventListener mChildlistener;
    private ImageView imageView;
   // private int mPosition;

    //constructor
    public DealRecyclerAdapter(){
        //initialize database
        //FirebaseUtil.openFbReference("traveldeals",ListActivity.class);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference=FirebaseUtil.mDatabaseReference;
        mChildlistener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //retrieve deals

                //create an object of deal to hold the deal values
                TravelDeal deal=dataSnapshot.getValue(TravelDeal.class);
                //log to see value
                Log.d("Deal", deal.getTitle());
                //add deal to arrayadapter.
                deal.setId(dataSnapshot.getKey());
                deals.add(deal);
                //notify when item is inserted.
                notifyItemInserted(deals.size()-1);
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
        deals=FirebaseUtil.mDeals;
        mDatabaseReference.addChildEventListener(mChildlistener);

    }


//bind viewHolder
    @NonNull
    @Override
    public DealRecyclerAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view=mLayoutInflater.from(parent.getContext()).inflate(R.layout.item_deals,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealRecyclerAdapter.viewHolder holder, int position) {
    TravelDeal deal=deals.get(position);
    holder.tvTitle.setText(deal.getTitle());
    holder.tvDescription.setText(deal.getDescription());
    holder.tvPrice.setText(deal.getPrice());
//    Log.d("image url",deal.getImageUrl());
        showImage(deal.getImageUrl());
    //Handle Item Click (Send intent for display

    }

    private void showImage(String url) {
        if (url != null && url.isEmpty()==false) {
            Picasso.get()
                    .load(url)
                    .resize(160, 160)
                    .centerCrop()
                    .into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    //Viewholder
    public class viewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        private final TextView tvDescription;
        private final TextView tvPrice;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle=itemView.findViewById(R.id.tv_title);
            tvDescription = (TextView)itemView.findViewById(R.id.tv_description);
            tvPrice = (TextView)itemView.findViewById(R.id.tv_price);
            imageView = (ImageView)itemView.findViewById(R.id.img_deal );

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int mPosition=getAdapterPosition();
                    Log.d("position",String.valueOf(mPosition));
                    //get the particular data we are clicking on.
                    TravelDeal selectedDeal=deals.get(mPosition);
                    Intent intent=new Intent(v.getContext(),DealActivity.class);
                    //put the data as extra to intent.
                    //use serializable (Ideally Parcelable for performance reasons)
                    intent.putExtra("Deal", selectedDeal);
                    v.getContext().startActivity(intent);
                }
            });

        }
    }
}
