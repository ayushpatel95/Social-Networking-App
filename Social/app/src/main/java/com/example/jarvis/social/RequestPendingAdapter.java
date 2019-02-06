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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestPendingAdapter extends RecyclerView.Adapter<RequestPendingAdapter.ViewHolder> {
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

        public ImageView iv1,iv2;
        RadioButton radioButton;

        public ViewHolder(View v) {
            super(v);
            view = v;
            tvname = (TextView) v.findViewById(R.id.rowfriendname);
            iv1 = (ImageView) v.findViewById(R.id.accept);
            iv2 = (ImageView) v.findViewById(R.id.reject);

                }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RequestPendingAdapter(ArrayList<Users> myDataset, Context context) {
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
                .inflate(R.layout.requestrow_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        if(mDataset.get(position).getStatus().matches("r")){
            holder.iv1.setImageResource(R.drawable.accept);
            holder.iv2.setImageResource(R.drawable.decline);
        }
        else{
            holder.iv2.setImageResource(R.drawable.delete);
        }
        holder.tvname.setText(mDataset.get(position).getFirstname());
        holder.iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDataset.get(position).getStatus().matches("r")) {


                    reference.child("People").child(auth.getCurrentUser().getUid()).child("Friends").child(mDataset.get(position).getUid()).setValue(mDataset.get(position).getFirstname());
                    reference.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String user = dataSnapshot.child("First name").getValue(String.class);
                            reference.child("People").child(mDataset.get(position).getUid()).child("Friends").child(auth.getCurrentUser().getUid()).setValue(user);
                            reference.child("People").child(auth.getCurrentUser().getUid()).child("Requests").child(mDataset.get(position).getUid()).setValue(null);
                            reference.child("People").child(mDataset.get(position).getUid()).child("Sent").child(auth.getCurrentUser().getUid()).setValue(null);
                            Toast.makeText(context, "Added as a friend", Toast.LENGTH_SHORT).show();

                            mDataset.remove(position);
                            notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                  }
        });
        holder.iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDataset.get(position).getStatus().matches("r")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do you really want to deline " +mDataset.get(position).getFirstname() +"'s request?" )
                            .setCancelable(true)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Toast.makeText(context,"Request declined",Toast.LENGTH_SHORT).show();
                                            reference.child("People").child(auth.getCurrentUser().getUid()).child("Requests").child(mDataset.get(position).getUid()).setValue(null);
                                            reference.child("People").child(mDataset.get(position).getUid()).child("Sent").child(auth.getCurrentUser().getUid()).setValue(null);

                                            mDataset.remove(position);
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
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do you really want to cancel the request sent to  " +mDataset.get(position).getFirstname() +" ?" )
                            .setCancelable(true)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            reference.child("People").child(auth.getCurrentUser().getUid()).child("Sent").child(mDataset.get(position).getUid()).setValue(null);
                                            reference.child("People").child(mDataset.get(position).getUid()).child("Requests").child(auth.getCurrentUser().getUid()).setValue(null);
                                            mDataset.remove(position);
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
            }
        });

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}


