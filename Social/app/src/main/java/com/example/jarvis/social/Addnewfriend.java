package com.example.jarvis.social;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Addnewfriend.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Addnewfriend#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Addnewfriend extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth auth;
    DatabaseReference reference;
    ArrayList<Users> users,sentrequestusers,friends,recievedrequestusers;

    private OnFragmentInteractionListener mListener;

    public Addnewfriend() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Addnewfriend.
     */
    // TODO: Rename and change types and number of parameters
    public static Addnewfriend newInstance(String param1, String param2) {
        Addnewfriend fragment = new Addnewfriend();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_addnewfriend, container, false);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        users =new ArrayList<>();
        sentrequestusers = new ArrayList<>();

        recievedrequestusers = new ArrayList<>();
        friends = new ArrayList<>();

        reference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot x: dataSnapshot.getChildren()){
                    if(!x.getKey().matches(auth.getCurrentUser().getUid())){
                        Users user = new Users();
                        user.setFirstname(x.child("First name").getValue(String.class));
                        user.setUid(x.getKey());
                        users.add(user);



                    }

                    reference.child("People").child(auth.getCurrentUser().getUid()).child("Sent").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot x: dataSnapshot.getChildren()){
                                Users users = new Users();
                                users.setUid(x.getKey());
                                users.setFirstname(x.getValue(String.class));
                                sentrequestusers.add(users);
                            }
                            for(int i =0; i<users.size();i++){
                                for(int j=0; j<sentrequestusers.size();j++){
                                    if(users.get(i).getUid().matches(sentrequestusers.get(j).getUid())){
                                        users.remove(users.get(i));
                                    }
                                }
                            }
                            reference.child("People").child(auth.getCurrentUser().getUid()).child("Friends").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot x: dataSnapshot.getChildren()){
                                        Users users = new Users();
                                        users.setUid(x.getKey());
                                        users.setFirstname(x.getValue(String.class));
                                        friends.add(users);
                                    }

                                    for(int i=0;i<users.size();i++){
                                        for(int j =0; j< friends.size();j++){
                                            if(users.get(i).getUid().matches(friends.get(j).getUid())){
                                                users.remove(users.get(i));

                                            }
                                        }
                                    }
                                    reference.child("People").child(auth.getCurrentUser().getUid()).child("Requests").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot x:dataSnapshot.getChildren()){

                                                Users users = new Users();
                                                users.setFirstname(x.getValue(String.class));
                                                users.setUid(x.getKey());
                                                recievedrequestusers.add(users);
                                            }
                                            for(int i=0;i<users.size();i++){
                                                for(int j=0;j<recievedrequestusers.size();j++){
                                                    if(users.get(i).getUid().matches(recievedrequestusers.get(j).getUid())){
                                                        users.remove(users.get(i));
                                                    }
                                                }
                                            }
                                            RecyclerView rcv = (RecyclerView) view.findViewById(R.id.rcv3);
                                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                            rcv.setLayoutManager(mLayoutManager);
                                            AddFriendsAdapter adapter = new AddFriendsAdapter(users, getActivity());
                                            rcv.setAdapter(adapter);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}
