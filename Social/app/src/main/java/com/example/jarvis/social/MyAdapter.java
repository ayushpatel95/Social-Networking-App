package com.example.jarvis.social;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<Post> mDataset;
    Context context;
    String user_id,token;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        public  TextView tvname,tvmessage,tvphon,tvtime;

        public ImageView iv;
        RadioButton radioButton;

        public ViewHolder(View v) {
            super(v);
            view = v;
            tvname = (TextView) v.findViewById(R.id.rowname);
            tvmessage = (TextView) v.findViewById(R.id.rowmsg);
            tvtime = (TextView) v.findViewById(R.id.rowtime);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<Post> myDataset, Context context) {
        mDataset = myDataset; this.context = context;
    }


    public ArrayList<Post> getList() {
        return mDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

         holder.tvname.setText(mDataset.get(position).getUser());
        holder.tvmessage.setText(mDataset.get(position).getMessage());
        holder.tvtime.setText(mDataset.get(position).getTime());

        holder.tvname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot x: dataSnapshot.getChildren()){
                            if(x.getKey().matches(mDataset.get(position).getUid())){
                                Users users = new Users();
                                users.setFirstname(x.child("First name").getValue(String.class));
                                users.setUid(x.getKey());
                                if(!x.getKey().matches(auth.getCurrentUser().getUid())){
                                    Intent intent = new Intent(context,FriendsPosts.class);
                                    intent.putExtra("uid",users);
                                    context.startActivity(intent);
                                }
                                else
                                    {
                                    Intent intent = new Intent(context,UserWall.class);
                                    context.startActivity(intent);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}


