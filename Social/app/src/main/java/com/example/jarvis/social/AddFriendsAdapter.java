package com.example.jarvis.social;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;

public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsAdapter.ViewHolder> {
    private ArrayList<Users> mDataset;
    Context context;
    DatabaseReference reference;
    FirebaseAuth auth;
    String user_id,token;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        public  TextView tvname;
        String user;


        public ImageView iv;
        RadioButton radioButton;

        public ViewHolder(View v) {
            super(v);
            view = v;
            tvname = (TextView) v.findViewById(R.id.rowfriendname);
            iv = (ImageView) v.findViewById(R.id.addfriend);
     /*
      */  }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AddFriendsAdapter(ArrayList<Users> myDataset, Context context) {
        mDataset = myDataset; this.context = context;
    }


    public ArrayList<Users> getList() {
        return mDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.addfriendsrow_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this pos
        // - replace the contents of the view with that element

        holder.tvname.setText(mDataset.get(position).getFirstname());

        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth = FirebaseAuth.getInstance();
                reference = FirebaseDatabase.getInstance().getReference();

                reference.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String user = dataSnapshot.child("First name").getValue(String.class);
                        reference.child("People").child(mDataset.get(position).getUid()).child("Requests").child(auth.getCurrentUser().getUid()).setValue(user);
                        reference.child("People").child(auth.getCurrentUser().getUid()).child("Sent").child(mDataset.get(position).getUid()).setValue(mDataset.get(position).getFirstname());

                        Toast.makeText(context,"Request Sent",Toast.LENGTH_SHORT).show();
                        mDataset.remove(position);
                        notifyDataSetChanged();

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


