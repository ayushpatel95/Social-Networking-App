package com.example.jarvis.social;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserWallAdapter extends RecyclerView.Adapter<UserWallAdapter.ViewHolder> {
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
            iv = (ImageView) v.findViewById(R.id.delete);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public UserWallAdapter(ArrayList<Post> myDataset, Context context) {
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
                .inflate(R.layout.userwall_row_layout, parent, false);
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

        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final FirebaseAuth auth = FirebaseAuth.getInstance();
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you really want to delete this post?")
                        .setCancelable(true)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        reference.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot x : dataSnapshot.getChildren()) {
                                                    if (x.child("Message").getValue(String.class).matches(mDataset.get(position).getMessage()) && x.child("UID").getValue(String.class).matches(auth.getCurrentUser().getUid())) {
                                                        reference.child("Posts").child(x.getKey()).setValue(null);
                                                        mDataset.remove(position);
                                                        notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}


