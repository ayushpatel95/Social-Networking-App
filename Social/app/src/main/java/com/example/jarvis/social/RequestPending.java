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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestPending.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestPending#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestPending extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    DatabaseReference reference;
    FirebaseAuth auth;
    private String mParam2;
    ArrayList<Users> users;
    private OnFragmentInteractionListener mListener;

    public RequestPending() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestPending.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestPending newInstance(String param1, String param2) {
        RequestPending fragment = new RequestPending();
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
        final View view = inflater.inflate(R.layout.fragment_request_pending, container, false);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();



        reference.child("People").child(auth.getCurrentUser().getUid()).child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users =new ArrayList<>();
                for(DataSnapshot x: dataSnapshot.getChildren()){
                    if(!x.getKey().matches(auth.getCurrentUser().getUid())){
                        Users user = new Users();
                        user.setFirstname(x.getValue(String.class));
                        user.setUid(x.getKey());
                        user.setStatus("r");
                        users.add(user);
                    }

                }
                reference.child("People").child(auth.getCurrentUser().getUid()).child("Sent").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot x: dataSnapshot.getChildren()){
                            if(!x.getKey().matches(auth.getCurrentUser().getUid())){
                                Users user = new Users();
                                user.setFirstname(x.getValue(String.class));
                                user.setUid(x.getKey());
                                user.setStatus("s");
                                users.add(user);
                            }
                        }
                        RecyclerView rcv = (RecyclerView) view.findViewById(R.id.rcv4);
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        rcv.setLayoutManager(mLayoutManager);
                        RequestPendingAdapter adapter = new RequestPendingAdapter(users, getActivity());
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
