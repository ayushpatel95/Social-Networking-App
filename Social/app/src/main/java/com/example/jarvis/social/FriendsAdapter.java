package com.example.jarvis.social;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
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

        public ImageView iv;
        RadioButton radioButton;

        public ViewHolder(View v) {
            super(v);
            view = v;
            tvname = (TextView) v.findViewById(R.id.rowfriendname);
            iv = (ImageView) v.findViewById(R.id.removefriend);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendsAdapter(ArrayList<Users> myDataset, Context context) {
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
                .inflate(R.layout.friendsrow_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.tvname.setText(mDataset.get(position).getFirstname());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,FriendsPosts.class);
                intent.putExtra("uid",mDataset.get(position));
                context.startActivity(intent);

            }
        });
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you really want to remove " +mDataset.get(position).getFirstname() +" as a friend ?" )
                        .setCancelable(true)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                        auth = FirebaseAuth.getInstance();
                                        reference.child("People").child(auth.getCurrentUser().getUid()).child("Friends").child(mDataset.get(position).getUid()).setValue(null);
                                        reference.child("People").child(mDataset.get(position).getUid()).child("Friends").child(auth.getCurrentUser().getUid()).setValue(null);
                                        mDataset.remove(mDataset.get(position));
                                        notifyDataSetChanged();
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


